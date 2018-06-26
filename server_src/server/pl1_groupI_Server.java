package othello;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
//import java.util.ArrayList;
import java.util.HashMap;

public class Server {
	private int MAX=100; 										    /*  最大接続数  */
	private int port; 											    /*  ポート番号  */
	private boolean [] online; 									/*  クライアント接続状態  */
	private int sum_of_threadNum=0;									/*  使われているスレッドの数  */
	private int p=1;
	PlayerArrayList<Player> game_online_list = new PlayerArrayList<Player>(); 	/*  対戦待ち状態のArrayList  */
	Receiver receiveThread[] = new Receiver[MAX];                   /*  受信クラス配列、スレッドの配列  */

	//コンストラクタでポート番号を設定
	public Server(int port) {
		this.port = port;
		online = new boolean[MAX];                                 /*  オンライン状態管理用配列を用意  */

	}

	/**********************データ受信用スレッド(内部クラス)**********************/
	class Receiver extends Thread{
		private InputStreamReader sisr;                            /*  受信データ用文字ストリーム  */
		private BufferedReader br;                                 /*  文字ストリーム用のバッファ  */
		private PrintWriter out;                                   /*  データ送信用オブジェクト    */
		private ObjectInputStream ois;                             /*  オブジェクト入力ストリーム  */
		private ObjectOutputStream oos;                            /*  オブジェクト出力ストリーム  */
		private int ThreadNo;                                      /*  スレッド番号(プレイヤを識別するため)  */
		private Player player = new Player("dammy", "dammy");
		private String user_name;                                  /*  このスレッドを利用しているユーザ名 */
		private boolean running=true;
		Socket socket;
		HashMap<Player,Player> map = new HashMap<Player,Player>(); /*  対戦中の相手と紐づけるためのHashMap  */

		//内部クラスReceiverのコンストラクタ
		Receiver (Socket socket, Server server, int ThreadNo){
			try{
				this.socket=socket;
				this.ThreadNo = ThreadNo;
				sisr = new InputStreamReader(socket.getInputStream());
				br = new BufferedReader(sisr);
				out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

				System.out.println(ThreadNo+"がせつぞくしました");
			} catch (IOException e) {
				System.err.println("データ受信時にエラーが発生しました: " + e);
			}
		}

		public void run(){
			try{
				/*  データを受信し続ける  */
				while(running) {
					String inputLine = br.readLine();
					if (inputLine != null){                       /*  先頭メッセージ応じてメソッドを呼ぶ  */

						/* ログイン認証のリクエストなら */
						if(inputLine.equals("loginRequest")) {
							user_name = br.readLine();
							String password = br.readLine();                  	    /* user名とパスワードを受け取り */
							String msg = loginCheck(user_name,password,ThreadNo);  /* ログインできるか確認し     */
							out.println(msg);
							out.flush();										    /*ログイン結果をクライアントに送信*/

							/* ログインできたなら,オンライン状態にする */
							if(msg.equals("permit")) {
								online[ThreadNo]=true;
							}
							else if(msg.equals("notPermit")) {
								running = false;
								//p--;
							}

						}

						else if(inputLine.equals("logout")) {
							running=false;
							;//p--;
						}

						/*  アカウント作成のリクエストなら  */
						else if(inputLine.equals("accountRequest")) {
							user_name = br.readLine();
							String password = br.readLine();                         /*ユーザ名とパスワードを受け取る*/
							String msg = accountCreate(user_name, password,ThreadNo);/*アカウント作成ができるか確認し*/
							out.println(msg);
							out.flush();                                              /*結果をクライアントに送信*/

						    /* 作成できるなら、オンライン状態に追加する  */
							if(msg.equals("permit")) {
								online[ThreadNo]=true;
							}
							else if(msg.equals("notPermit")) {
								running = false;
								//p--;
							}

						}

						/*  対戦成績のリクエストなら  */
						else if(inputLine.equals("myPlayerRequest")) {
								Player player = playerInfo(user_name);    /*  ユーザ名を用いてファイルから探索  */
								player.ThreadNo=ThreadNo;
								oos = new ObjectOutputStream(socket.getOutputStream());
								oos.writeObject(player);
								oos.flush();                               /*  該当オブジェクトをクライアントに送信  */
						}

						/*  対局待ち状態からhome画面に戻ったら  */
						else if(inputLine.equals("returnHome")) {
							game_online_list.remove(player);   /*  待ち状態リストから外し  */
							//game_online_flag=false;           /*  flagの状態を変える     */
						}

						/*  対局待ち状態リストのリクエストなら  */
						else if(inputLine.equals("otherPlayerRequest")){

							/*  1回目の要求なら  */

								game_online_list.add(player);      /*  自分自身を待ち状態リストに入れ */
								oos.reset();
								out.flush();
								out.println(String.valueOf(game_online_list.size()));
								out.flush();

								for(Player p:game_online_list) {
									oos.writeObject(p); /*  クライアントにリストを送信     */
									oos.flush();
								}

								out.println(String.valueOf(game_online_list.size()));  /*  リストのサイズも送信  */
								out.flush();
								//game_online_flag=true;


							/*  updateによる再送信のリクエストなら  */

						}
						else if(inputLine.equals("otherPlayerRequestupDate")){

							/*  1回目の要求なら  */


							/*  updateによる再送信のリクエストなら  */

								out.println("upDate");
								out.flush();
								System.out.println("updataオブジェクト送信前"+game_online_list.size());

								out.flush();
								out.println(String.valueOf(game_online_list.size()));
								out.flush();

								System.out.println(String.valueOf(game_online_list.size()));

								oos.reset();
								for(Player p:game_online_list) {
									oos.writeObject(p); /*  クライアントにリストを送信     */
									oos.flush();
									System.out.println(p.getName());
								}

								out.println(String.valueOf(game_online_list.size()));
								out.flush();

						}

						/*  他者への対局申し込みなら  */
						else if(inputLine.equals("requestGame")){

							String opponentstring = String.valueOf(br.read());
							int opponent = Integer.parseInt(opponentstring)-48;  /*  対局者のThreadNoと  */
							String applier = String.valueOf(br.read());/*  自身のlist番号を受け取り */
							String syoriyou = String.valueOf(br.read());
							String syoriyou2 = String.valueOf(br.read());
							String syoriyou3 = String.valueOf(br.readLine());
							String name = String.valueOf(br.readLine());
							System.out.println("name : " + name);
							String rate = String.valueOf(br.readLine());
							System.out.println("rate : " + rate);
							map.put(player, receiveThread[opponent].player);        /* HashMapでお互いを結ぶ  */
							receiveThread[opponent].map.put(receiveThread[opponent].player, player);
							requestGame(opponent,applier,name,rate);    /* 対戦を申し込む */
							System.out.println("相手側に申し込み送信完了");
						}

						/*  申し込に対する答えなら  */
						else if(inputLine.equals("Answer")) {
							String ans = br.readLine(); /* 答えを受け取り、申し込み元に送信  */	
							String name = br.readLine();
							
							if(ans.equals("Yes")) {
								receiveThread[map.get(player).ThreadNo].sendMessage("Answer");
								receiveThread[map.get(player).ThreadNo].sendMessage("Yes");
								receiveThread[map.get(player).ThreadNo].sendMessage(name);
							//	game_online_list.remove(player);
							//	game_online_list.remove(map.get(player));
							}
							else if(ans.equals("No")) {
								receiveThread[map.get(player).ThreadNo].sendMessage("Answer");
								receiveThread[map.get(player).ThreadNo].sendMessage("No");
								receiveThread[map.get(player).ThreadNo].map.remove(receiveThread[map.get(player).ThreadNo].player);
								map.remove(player);
							}
						}

						/*  対局中のデータ転送のリクエストなら  */
						else if(inputLine.equals("forwardMessage")) {
							String color = br.readLine();
							String operation = br.readLine();
							forwardMessage(color, operation, map.get(player).ThreadNo); //もう一方に転送
						}

						/*  データ更新のリクエストなら  */
						else if(inputLine.equals("dataUpdate")){

							String user_name = br.readLine();
							String result    = br.readLine();
							String rate = br.readLine();
							dataUpdate(user_name,result,rate);
						}
					}
				}
			} catch (IOException e){ // 接続が切れたとき
				System.err.println("プレイヤ " + ThreadNo + "との接続が切れました．");
     		}finally {
				try{
					game_online_list.remove(game_online_list.indexOf(player)); /*  対局待ち状態リストから削除する  */
				}catch(Exception ex) {
				}
				online[ThreadNo] = false;                                 /*  プレイヤの接続状態を更新する    */
				printStatus();                                            /*  接続状態を出力する              */
				receiveThread[ThreadNo]=null;                            /*  このThreadを空ける              */
				if(sum_of_threadNum==ThreadNo) {
					sum_of_threadNum--;                                      /*  使ってるスレッド数を減らす      */
					System.out.println("decrement");
				}
			}
		}

		/*  対応するクライアントにメッセージを送信  */
		public synchronized void sendMessage(String message) {
		    out.println(message);
		    out.flush();
		  }

	}
	/**********************************受信スレッド(内部クラス)の終わり*******************************/


/****************************************メソッド**************************************************/

	/* クライアントの接続(サーバの起動) */
	public void acceptClient(){
		try {
			System.out.println("サーバが起動しました．");
			ServerSocket ss = new ServerSocket(port); /* サーバソケットを用意 */
			while (true) {

				Socket socket = ss.accept();        /* 新規接続を受け付ける     */

				for (p = 1; p < MAX+1; p++){        /* 接続チェック */
					if (receiveThread[p] == null)   /* 空いている場合 */
			            break;
			    }

				if (p == MAX+1)                     /* 空いていない場合 */
			    	continue;                    /* 以下の処理をしない */

				System.out.println("start Thread"+p);
				receiveThread[p] = new Receiver(socket, this, p);
		        receiveThread[p].start( );
		        sum_of_threadNum++;                  /* 使われているスレッドの数 */
		     }
		}catch (Exception e) {
			System.err.println("ソケット作成時にエラーが発生しました: " + e);
		}
	}

	/* ログイン認証 */
	public String loginCheck(String user_name, String password, int ThreadNo) {
		try{
			Player player;
			//FileInputStreamオブジェクトの生成
			FileInputStream inFile = new FileInputStream("players.obj");

            try{
            	while(true){
            		//ObjectInputStreamオブジェクトの生成
            		ObjectInputStream inObject = new ObjectInputStream(inFile);
            		player = (Player)inObject.readObject(); /*オブジェクトの読み込み*/

            		/* もし、ユーザ名とパスワードが一致するなら */
            		if(player.getName().equals(user_name) && player.getPassword().equals(password)){
            			player.setThreadNo(ThreadNo);
            			receiveThread[ThreadNo].player=player; /*そのplayerオブジェクトをThreadに保存*/
            			inObject.close();
            			System.out.println("login permit");
            			return "permit"; /*ログインを許可する*/
            		}

            	}
            }catch(EOFException e){
    		}
		}catch(Exception e){
		}

   		/*一致しなかったら、許可しない*/
		System.out.println("No permit");
		return "notPermit";

	}

	//アカウント作成
	public String accountCreate(String user_name, String password, int ThreadNo) {
		try {
			Player player;
			//FileInputStreamオブジェクトの生成
            FileInputStream inFile = new FileInputStream("players.obj");

            try{
            while(true){
            	//ObjectInputStreamオブジェクトの生成
            	ObjectInputStream inObject = new ObjectInputStream(inFile);
            	player = (Player)inObject.readObject(); //オブジェクトの読み込み
            	System.out.println(player.getName());

            	//もし、同じ名前のユーザが既に存在しているなら
            	if(player.getName().equals(user_name)){
            		inObject.close();
            		System.out.println("false");
            		return "notPermit"; //アカウント作成を不許可とする
            	}
             }
            }catch(EOFException e){
    		}

            /*   ↓↓↓名前の重複がなく、新規アカウントが作成できるなら↓↓↓   */

            //FileOutputStreamオブジェクトの生成
            FileOutputStream outFile = new FileOutputStream("players.obj",true);
            //ObjectOutputStreamオブジェクトの生成
            ObjectOutputStream outObject = new ObjectOutputStream(outFile);

            //クラスPlayerのオブジェクトの書き込み
            player = new Player(user_name,password);
            outObject.writeObject(player);
            receiveThread[ThreadNo].player=player;
            outObject.close();
       }
       catch(Exception e) {
    	   e.printStackTrace();
       }

		/*アカウント作成の許可*/
		System.out.println("true");
        return "permit";

	}

	/* Playerの対戦成績を送信 */
	public Player playerInfo(String user_name) {
		//Playerオブジェクトを格納する変数
		Player player = new Player("dammy", "dammy");

        try{
        	//FileInputStreamオブジェクトの生成
            FileInputStream inFile = new FileInputStream("players.obj");

            //該当するオブジェクトを探索
            while(true){
        	//ObjectInputStreamオブジェクトの生成
        	ObjectInputStream inObject = new ObjectInputStream(inFile);
        	player = (Player)inObject.readObject(); //読み込み

        		if(player.getName().equals(user_name)){
        			inObject.close();
        			break;
        		}

            }
        }
        catch(Exception e){
		}

        return player; //オブジェクトをリターン、クライアントへ送る。

	}

	/*クライアント接続状態の出力*/
	public void printStatus(){
		int i=0;
		boolean flag = false;
		while(i<=sum_of_threadNum) {
			if(online[i]==true) {
				System.out.println
				("ユーザ名"+receiveThread[i].player.getName()+"はオンライン状態です");
				flag=true;
			}
			System.out.println(i);
			i++;
		}

		if(!(flag))
				System.out.println("オンライン状態のPlayerはいません");

	}

	public void dataUpdate(String user_name, String result, String rate) {
		Player player;
		ObjectInputStream inObject;
		PlayerArrayList<Player> arr = new PlayerArrayList<Player>();
        try{
        	/*  FileInputStreamオブジェクトの生成  */
            FileInputStream inFile = new FileInputStream("players.obj");

            /*データ更新をするオブジェクトを探索*/
            while(true){
            	inObject = new ObjectInputStream(inFile);
            	player = (Player)inObject.readObject(); /* EIEuEWEFENEgC?i≪C?cuC?  */

            	if(player.getName().equals(user_name)){
            		if(result.equals("WIN")) {
            			player.setWin(player.getWin()+1);
            		}else if(result.equals("LOSE")) {
            			player.setDefeat(player.getDefeat()+1);
            		}else if(result.equals("DRAW")) {
            			player.setDraw(player.getDraw()+1);
            		}
            		player.setRate(Double.valueOf(rate));
            	}
            	arr.add(player);  /* 1度すべてのオブジェクトファイルから読み込み、リストとする */
            }
         }catch(Exception e){
         }

        /*オブジェクトを保存*/
        try{
           	boolean flag=false;
            for(Player p : arr){

            	/*一つ目のオブジェクトの保存は、ファイルを新しくしてから保存*/
            	if(flag==false){
                    FileOutputStream outFile1 = new FileOutputStream("players.obj");  /*第二引数なし*/
                   	ObjectOutputStream outObject1 = new ObjectOutputStream(outFile1);
            		outObject1.writeObject(p);
            		flag=true;
            	}

            	else{

                    FileOutputStream outFile2 = new FileOutputStream("players.obj",true);
                   	ObjectOutputStream outObject2 = new ObjectOutputStream(outFile2);
                    outObject2.writeObject(p);
            	}

            }
        }catch(Exception e){
        }
	}

	/*  対局申し込み転送  */
	public void requestGame(int opponent,String applier_list_num,String name,String rate){
		receiveThread[opponent].sendMessage("requestGame");
		receiveThread[opponent].sendMessage(applier_list_num);
		receiveThread[opponent].sendMessage(name);
		System.out.println("name : " + name);
		receiveThread[opponent].sendMessage(rate);
		System.out.println("rate : " + rate);
	}

	/*  操作情報を転送  */
	public void forwardMessage(String msg1, String msg2, int ThreadNo) {
		receiveThread[ThreadNo].sendMessage("forwardMessage");
		receiveThread[ThreadNo].sendMessage(msg1);
		receiveThread[ThreadNo].sendMessage(msg2);
		System.out.println("スレッドナンバー" + ThreadNo + "に送信しました");
	}

	public static void main(String[] args) {
		Server server = new Server(10027);
		server.acceptClient(); /*  クライアント受け入れを開始  */
	}

}
