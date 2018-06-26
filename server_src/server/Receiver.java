package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
// ServerSocketに必要
import java.net.Socket;
import java.util.ArrayList;

//データ送受信スレッド
public class Receiver extends Thread{

	private final static int NEW_PLAYER = 0;//プレイヤーの新規登録
	private final static int LOGIN = 1;//ログイン
	private final static int RESULT = 2;//戦績表示
	private final static int SEND_NOW_PLAYERS = 3;//現在onlineのプレイヤーを表示
	private final static int OFFER_TO_OTHER = 4;//相手へオファー送信
	private final static int REPLY_TO_OTHER = 5;//相手へオファーの返事送信
	private final static int WHERE_PUT = 6;//相手の駒を伝える
	private final static int PLAY_RESULT = 7;//試合結果を勝者から受け取る（引き分けの場合は黒から）
	private final static int GAME_FINISH = 8;//mainmenuへ


	PlayerData player = new PlayerData();

	private int myNum;

	private int decidedOpponentNum=-1;//対局が確定した相手のmyNum

	private int turn=-1;//0: 自分の番 1:相手の番


	private int whereIs=0;//playerが今どの段階にいるか
	//-1:切断 0:接続しただけand新規登録しただけand切断のときにも１を代入 1:ログイン済み 2:オンライン済み 3:オファー送信中 4:対局中

	private int sendOfferNum=-1;//オファーしている人のmyNum,-1ならオファーしていない

	private ArrayList<PlayerData>receiveOfferPlayer=new ArrayList<PlayerData>();//オファーを受けている人のmyNumの配列 長さ0ならオファー受信してない

	private InputStreamReader sisr; //受信データ用文字ストリーム
	private BufferedReader br; //文字ストリーム用のバッファ
	private PrintWriter printWriter; //データ送信用オブジェクト
	private Server server;

	Receiver (Socket socket, int num, Server server){
		this.server = server;
		try{
			sisr = new InputStreamReader(socket.getInputStream());
			br = new BufferedReader(sisr);
			// このソケットに送るときのための物で、myNumの違いに注意
			printWriter = new PrintWriter(socket.getOutputStream(), true);
			myNum = num;
		} catch (IOException e) {
			System.err.println("データ受信時にエラーが発生しました: " + e);
		}
	}

	public void run(){
		int type;
		try{
			while(true) {// データを受信し続ける
				String inputLine = br.readLine();//データを一行分読み込む
				if (inputLine != null){ //データを受信したら

					type = Integer.parseInt("" +inputLine.charAt(0));
					switch(type){
						case NEW_PLAYER:{
							String str1 = inputLine.substring(1);
							while((inputLine = br.readLine()) == null);

							if(server.isNotUsed(str1, inputLine, player,myNum))
								printWriter.println("01");
							else
								printWriter.println("00");

							printWriter.flush();
							break;
						}
						case LOGIN:{

							if(inputLine.charAt(1)==('1')) {//log in 要求
								String str1 = inputLine.substring(2);
								while((inputLine = br.readLine()) == null);

								String msg=server.judgeLogin(str1, inputLine, player,myNum);

								if(msg.equals("0"))
									printWriter.println("10");
								else {
									whereIs=1;//ログインした状態
									printWriter.println("11"+msg);

									server.displayPlayer();//player一覧の更新
								}

								printWriter.flush();
							}
							else if(inputLine.charAt(1)==('0')) {//log out 要求
								whereIs=0;

								server.displayPlayer();//player一覧の更新

							}

							break;
						}
						case RESULT:{
							String str1 = inputLine.substring(1);

							String str2,str3;

							String answer;

							str2=server.getPlayerData(str1);

							if(!str2.equals("")) {
								str3=server.inputResultText(str1);

								answer="2"+str2+str3;
							}else {
								answer="2"+str2;
							}

							printWriter.println(answer);
							printWriter.flush();
							break;
						}
						case SEND_NOW_PLAYERS:{//31: online画面に 32:offline

							if(inputLine.charAt(1)=='1') {
								whereIs=2;//このタイミングで自分もオンラインにする

								server.displayPlayer();//player一覧の更新

								printWriter.println(server.getLoginPlayerData(myNum));
								printWriter.flush();
							}
							else if(inputLine.charAt(1)=='0') {
								whereIs=1;//ログイン画面に戻ったこと

								cancelOffer(null);

								receiveOfferPlayer.clear();//オファーされている配列をクリア
								printWriter.println("42");//client側のoffer配列も変更する
								printWriter.flush();

								server.displayPlayer();//player一覧の更新

							}

							break;
						}

						case OFFER_TO_OTHER:{//41+id:オファー 40+id:オファーキャンセル
							if(inputLine.charAt(1)=='1'&&sendOfferNum==-1) {
								String opponentName = inputLine.substring(2);
								int opponentNum = server.changeFromID(opponentName);


								if(server.receiver.get(opponentNum).getSendOfferNum()==myNum) {//両方がオファー送りあってマッチング成立

									if(server.receiver.get(opponentNum).getWhereIs()==3) {

										//自分がほかのオファーの人に拒否送信

										cancelOffer(opponentName);

										//相手がほかのオファーの人に拒否送信

										server.receiver.get(opponentNum).cancelOffer(player.getID());

										decidedOpponentNum=opponentNum;
										server.receiver.get(decidedOpponentNum).setdecidedOpponentNum(myNum);

										sendOfferNum=-1;
										server.receiver.get(decidedOpponentNum).setSendOfferNum(-1);

										receiveOfferPlayer.clear();//オファーされている配列をクリア
										printWriter.println("42");//client側のoffer配列も変更する
										printWriter.flush();

										server.receiver.get(decidedOpponentNum).receiveOfferPlayer.clear();//相手のオファーされている配列もクリア
										server.receiver.get(decidedOpponentNum).printWriter.println("42");//相手のclient側のoffer配列も変更する
										server.receiver.get(decidedOpponentNum).printWriter.flush();

										whereIs=4;
										server.receiver.get(decidedOpponentNum).setWhereIs(4);

										server.displayPlayer();//player一覧の更新

										matching(myNum,decidedOpponentNum);

									}
									else {//相手がオファー送信やめてるからエラーを送信
										printWriter.println("520"+server.receiver.get(opponentNum).player.getPlayerData());
										printWriter.flush();
									}


								}
								else if((server.receiver.get(opponentNum).getWhereIs()==2)||(server.receiver.get(opponentNum).getWhereIs()==3)) {
									sendOfferNum=opponentNum;//オファーしてる人代入
									whereIs=3;

									server.receiver.get(opponentNum).addOfferPlayer(player);

									server.receiver.get(opponentNum).printWriter.println("41" + player.getPlayerData());
									server.receiver.get(opponentNum).printWriter.flush();
								}else {
									printWriter.println("500"+server.receiver.get(opponentNum).player.getPlayerData());
									printWriter.flush();
								}

							}

							else if(inputLine.charAt(1)=='0'&&sendOfferNum!=-1) {
								String opponentName = inputLine.substring(2);
								int opponentNum = server.changeFromID(opponentName);

								sendOfferNum=-1;//オファーしている人がいなくなったので
								whereIs=2;
								server.receiver.get(opponentNum).removeOfferPlayer(player);

								server.receiver.get(opponentNum).printWriter.println("40" + player.getPlayerData());
								server.receiver.get(opponentNum).printWriter.flush();
							}

							break;
						}

						case REPLY_TO_OTHER:{//51+id:オファー受理,50+id:オファー拒否

							String opponentName = inputLine.substring(2);
							int opponentNum = server.changeFromID(opponentName);

							if(inputLine.charAt(1)=='1') {

								if(server.receiver.get(opponentNum).getWhereIs()==3) {

									//自分がほかのオファーの人に拒否送信

									cancelOffer(opponentName);

									//相手がほかのオファーの人に拒否送信

									server.receiver.get(opponentNum).cancelOffer(player.getID());

									decidedOpponentNum=opponentNum;
									server.receiver.get(decidedOpponentNum).setdecidedOpponentNum(myNum);

									server.receiver.get(decidedOpponentNum).setSendOfferNum(-1);

									receiveOfferPlayer.clear();//オファーされている配列をクリア
									printWriter.println("42");//client側のoffer配列も変更する
									printWriter.flush();

									server.receiver.get(decidedOpponentNum).receiveOfferPlayer.clear();//相手のオファーされている配列もクリア
									server.receiver.get(decidedOpponentNum).printWriter.println("42");//相手のclient側のoffer配列も変更する
									server.receiver.get(decidedOpponentNum).printWriter.flush();

									whereIs=4;
									server.receiver.get(decidedOpponentNum).setWhereIs(4);

									server.displayPlayer();//player一覧の更新

									matching(myNum,decidedOpponentNum);

								}
								else {//相手がオファー送信やめてるからエラーを送信
									printWriter.println("520"+server.receiver.get(opponentNum).player.getPlayerData());
									printWriter.flush();
								}

							}

							else if(inputLine.charAt(1)=='0') {
								if(server.receiver.get(opponentNum).getWhereIs()==3) {
									server.receiver.get(opponentNum).printWriter.println("500"+player.getPlayerData());//相手に拒否した旨を送信
									server.receiver.get(opponentNum).printWriter.flush();

									server.receiver.get(opponentNum).setWhereIs(2);
									server.receiver.get(opponentNum).setSendOfferNum(-1);

								}
							}

							break;
						}

						case WHERE_PUT:{//passの時は"688を送信",それ以外は"6ij"(i,j:任意)

							if(turn==0) {//自分の番のときのみ転送

								turn=1;

								server.receiver.get(decidedOpponentNum).setTurn(0);

								server.receiver.get(decidedOpponentNum).printWriter.println(inputLine);

								server.receiver.get(decidedOpponentNum).printWriter.flush();

							}

							break;
						}
						case PLAY_RESULT:{//"7win"普通に勝った,"7earlyWin":相手の投了or切断,"7draw":引き分け
							// 勝ったとき

							if(whereIs==4) {//何回も送らないように

								if(inputLine.equals("7win")) {

									player.incrementWin();
									server.receiver.get(decidedOpponentNum).player.incrementLose();
									server.resultWin(myNum, decidedOpponentNum);
								}
								else if(inputLine.equals("7earlyWin")) {

									player.incrementWin();
									server.receiver.get(decidedOpponentNum).player.incrementEarlyLose();
									server.resultEarlyLose(myNum, decidedOpponentNum);
								}
								else if(inputLine.equals("7draw")) {

									player.incrementDraw();
									server.receiver.get(decidedOpponentNum).player.incrementDraw();
									server.resultDraw(myNum, decidedOpponentNum);
								}

							}

							break;
						}

						case GAME_FINISH:{//ゲーム終了し、menuscreenにもどったことを受信
							turn=-1;
							decidedOpponentNum=-1;
							whereIs=1;

							server.displayPlayer();//player一覧の更新

							printWriter.println("8"+player.getPlayerData());
							printWriter.flush();


							break;
						}

						default:
							System.out.println("クライアント側からの送信のタイプが不適です");
							break;
					}
				}
			}
		} catch (IOException e){ // 接続が切れたとき

			if(decidedOpponentNum!=-1) {

				//相手に切断した情報をおくる
				server.receiver.get(decidedOpponentNum).printWriter.println("7");
			}

			if(whereIs==2||whereIs==3) {
				cancelOffer(null);

				sendOfferNum=-1;

				receiveOfferPlayer.clear();//オファーされている配列をクリア
				printWriter.println("42");//client側のoffer配列も変更する
				printWriter.flush();

			}


			whereIs=-1;//切断状態に移行

			System.out.println("number "+myNum+":切断");

			server.displayPlayer();//player一覧の更新

		}
	}

	private void matching(int myNum2, int decidedOpponentNum2) {
		if(myNum<decidedOpponentNum) {

			//マッチング成功

				//自分に送信(黒)
				setTurn(0);

				printWriter.println("511"+server.receiver.get(decidedOpponentNum).player.getPlayerData());
				printWriter.flush();

				//相手に送信(白)
				server.receiver.get(decidedOpponentNum).setTurn(1);

				server.receiver.get(decidedOpponentNum).printWriter.println("512"+player.getPlayerData());//相手に受理した旨を送信
				server.receiver.get(decidedOpponentNum).printWriter.flush();

			}
			else if(myNum>decidedOpponentNum){
			//マッチング成功

				//自分に送信(白)
				setTurn(1);


				printWriter.println("512"+server.receiver.get(decidedOpponentNum).player.getPlayerData());
				printWriter.flush();

				//相手に送信(黒)
				server.receiver.get(decidedOpponentNum).setTurn(0);

				server.receiver.get(decidedOpponentNum).printWriter.println("511"+player.getPlayerData());//相手に受理した旨を送信
				server.receiver.get(decidedOpponentNum).printWriter.flush();

			}
	}
	private void cancelOffer(String str) {//str以外にoffercancelされたことを送信
		for(int i=0;i<receiveOfferPlayer.size();i++) {
			if(!(receiveOfferPlayer.get(i).getID().equals(str))) {
				int cancelNum = server.changeFromID(receiveOfferPlayer.get(i).getID());

				server.receiver.get(cancelNum).printWriter.println("500"+player.getPlayerData());//相手に拒否した旨を送信
				server.receiver.get(cancelNum).printWriter.flush();

				server.receiver.get(cancelNum).setWhereIs(2);
				server.receiver.get(cancelNum).setSendOfferNum(-1);
			}
		}
	}
	private void setdecidedOpponentNum(int num) {
		decidedOpponentNum=num;
	}
	private void setTurn(int i) {
		turn=i;
	}

	private int getSendOfferNum() {
		return sendOfferNum;
	}

	private void setSendOfferNum(int i) {
		sendOfferNum=i;
	}
	public int getWhereIs() {
		return whereIs;
	}

	public void setWhereIs(int i) {
		whereIs=i;
	}

	public void addOfferPlayer(PlayerData player) {
		receiveOfferPlayer.add(player);
	}

	public void removeOfferPlayer(PlayerData player) {

		for(int i=0;i<receiveOfferPlayer.size();i++) {
			if(player==receiveOfferPlayer.get(i)) {
				receiveOfferPlayer.remove(i);
				break;
			}
		}

	}
}