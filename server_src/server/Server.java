package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
// ServerSocketに必要
import java.net.ServerSocket;
import java.net.Socket;
// ArrayListに必要
import java.util.ArrayList;

public class Server {
	protected static int PORT; // サーバの待ち受けポート

	// プレイヤーデータのあるファイル
	private final static String FILE_NAME = "./player.txt";
	// 全体の戦績データのあるファイル
	private final static String RESULT_FILE_NAME = "./result.txt";

	private static FileReader fileRead;
	private static BufferedReader bufFileRead;
	private static FileWriter fileWrite;
	private static BufferedWriter bufFileWrite;

	// Receiverの配列
	public ArrayList<Receiver> receiver = new ArrayList<Receiver>();


	// サーバ起動からの接続者の総数
	private int numPlayer = 0;

	// semaphore
	private static boolean serverSemaphore = false;
	private static boolean playerSemaphore = false;
	private static boolean resultSemaphore = false;

	public static void main(String[] args) {
		PORT = Integer.parseInt(args[0]);
		Server s = new Server();
		s.mainLoop();
	}
	private void mainLoop(){
		try{
			System.out.println("サーバを起動しました");
			// ポートを取得
	        int port = Server.PORT;

			// サーバーソケットを作成
	        ServerSocket ss = new ServerSocket(port);
	        while (true) {
	        	// 新規接続を受け付ける
	        	Socket socket = ss.accept();

	        	System.out.println("クライアントが新たに接続されました");
			while(serverSemaphore);
			setSemaphore(true);
			receiver.add(new Receiver(socket, numPlayer, this));
         	receiver.get(numPlayer).start();
			numPlayer++;
	        setSemaphore(false);
		   }

		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 同期用関数
	private synchronized static void setSemaphore(boolean s){
		if(s)
			while(serverSemaphore); // 2つ連続でここまで飛んできた時は終了まで待つ
		serverSemaphore = s;
	}

	private synchronized static void setplayerSemaphore(boolean s){
		if(s)
			while(playerSemaphore); // 2つ連続でここまで飛んできた時は終了まで待つ
		playerSemaphore = s;
	}

	private synchronized static void setResultSemaphore(boolean s){
		if(s)
			while(resultSemaphore); // 2つ連続でここまで飛んできた時は終了まで待つ
		resultSemaphore = s;
	}


	// 新規登録の際に使えるidかどうか(登録も）
	public synchronized boolean isNotUsed(String id, String pass, PlayerData pla,int myNumber){

		while(playerSemaphore);
		setplayerSemaphore(true);
		boolean a=true;

		try{

			fileRead = new FileReader(FILE_NAME);
			bufFileRead = new BufferedReader(fileRead);
			while(bufFileRead.ready()){
				String readedLine = bufFileRead.readLine();

				String[] str=readedLine.split("/");

				if(str[0].equals(id)) {
					 a=false;
					 break;
				}
			}

					bufFileRead.close();
					fileRead.close();
				setplayerSemaphore(false);

				if(a) {
					receiver.get(myNumber).player=new PlayerData(id,pass);
					outputText(receiver.get(myNumber).player.fileOutStr(), FILE_NAME);
				}

		}
		catch(FileNotFoundException e){
			System.out.println("no file");
		}
		// ファイルがない以外の問題の時
		catch(Exception e){
			e.printStackTrace();
		}
		setplayerSemaphore(false);
		return a;
	}


	// ログインできるかどうか
	public String judgeLogin(String id, String pass, PlayerData pla,int myNumber){
		while(playerSemaphore);
		setplayerSemaphore(true);

		String answer="0";

		int alreadyLogin=0;

		try{

			for(int i=0;i<receiver.size();i++) {//同じIDで既にログインしていないかチェック
				if(receiver.get(i).player.getID().equals(id)) {
					if(receiver.get(i).getWhereIs()!=0&&receiver.get(i).getWhereIs()!=-1) {
						alreadyLogin=1;
						break;
					}
				}
			}

			if(alreadyLogin==0) {

				fileRead = new FileReader(FILE_NAME);
				bufFileRead = new BufferedReader(fileRead);
				while(bufFileRead.ready()){
					String readedLine = bufFileRead.readLine();

					String[] str=readedLine.split("/");

					if(str[0].equals(id)&&str[1].equals(pass)) {

						receiver.get(myNumber).player=new PlayerData(readedLine);
						 answer=str[0]+'/'+str[2]+'/'+str[3]+'/'+str[4]+'/'+str[5];
						 break;

					}

				}
				bufFileRead.close();
				fileRead.close();
			}
		}
		catch(FileNotFoundException e){
			System.out.println("fileError");
		}
		// ファイルがない以外の問題の時
		catch(Exception e){
			e.printStackTrace();
		}
		setplayerSemaphore(false);
		return answer;
	}


	//ファイルに書き込む  引数:追加で書き込む文字列, ファイル名
	private static synchronized void outputText(String str, String name){
		if(name.equals(FILE_NAME)){
			while(playerSemaphore);
			setplayerSemaphore(true);
		}else if(name.equals(RESULT_FILE_NAME)){
			while(resultSemaphore);
			setResultSemaphore(true);
		}
		try{
			fileWrite = new FileWriter(name, true);
			bufFileWrite = new BufferedWriter(fileWrite);
			bufFileWrite.write(str+"\n");
			bufFileWrite.close();
			fileWrite.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		if(name.equals(FILE_NAME))
			setplayerSemaphore(false);
		else if(name.equals(RESULT_FILE_NAME))
			setResultSemaphore(false);
	}

	//個人の戦績をファイルに更新する関数  引数:自分,相手
	private synchronized void renewText(int num1, int num2){
		while(playerSemaphore);
		setplayerSemaphore(true);
		try{
			// 読み込み部
			ArrayList<String> inoutStr = new ArrayList<String>();//一時的にこの配列にplayer.txtのデータ読み込む

			fileRead = new FileReader(FILE_NAME);
			bufFileRead = new BufferedReader(fileRead);

			while(bufFileRead.ready())
				inoutStr.add(bufFileRead.readLine());

			bufFileRead.close();
			fileRead.close();

			// 書き込み部
			fileWrite = new FileWriter(FILE_NAME, false);
			bufFileWrite = new BufferedWriter(fileWrite);
			String id, line;
			String name1,name2;

			name1=receiver.get(num1).player.getID();
			name2=receiver.get(num2).player.getID();


			for(int i = 0; i < inoutStr.size(); i++){
				line=inoutStr.get(i);

				id = inoutStr.get(i).split("/")[0];
				if(id.equals(name1))
					line = receiver.get(num1).player.fileOutStr();
				else if(id.equals(name2))
					line = receiver.get(num2).player.fileOutStr();
				bufFileWrite.write(line+"\n");
			}

			bufFileWrite.close();
			fileWrite.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		setplayerSemaphore(false);
	}

	// 戦績データファイルから検索  引数:検索文字列(完全一致検索)
	public String inputResultText(String str){
		while(resultSemaphore);
		setResultSemaphore(true);

		String returnStr = "";
		String firstPlayer, secondPlayer,judge;
		try{
			int hit = 0;// ヒット数
			fileRead = new FileReader(RESULT_FILE_NAME);
			bufFileRead = new BufferedReader(fileRead);
			while(bufFileRead.ready()){
				String readedLine = bufFileRead.readLine();

				firstPlayer=readedLine.split("/")[0];
				secondPlayer=readedLine.split("/")[1];
				judge=readedLine.split("/")[2];

				if(firstPlayer.equals(str)) {
					if(judge.equals("lose")) {
						returnStr+="\n"+secondPlayer+"/"+"01";
					}
					else if(judge.equals("earlyLose")) {
						returnStr+="\n"+secondPlayer+"/"+"11";
					}
					else if(judge.equals("draw")) {
						returnStr+="\n"+secondPlayer+"/"+"10";
					}

					hit++;
				}
				else if(secondPlayer.equals(str)) {
					if(judge.equals("lose")) {
						returnStr+="\n"+firstPlayer+"/"+"00";
					}
					else if(judge.equals("earlyLose")) {
						returnStr+="\n"+firstPlayer+"/"+"00";
					}
					else if(judge.equals("draw")) {
						returnStr+="\n"+firstPlayer+"/"+"10";
					}

					hit++;
				}

				if(hit>=30) {//ヒット数30以下
					break;
				}

			}
			bufFileRead.close();
			fileRead.close();
		}
		catch(FileNotFoundException e){
		}
		// ファイルがない以外の問題の時
		catch(Exception e){
			e.printStackTrace();
		}

		setResultSemaphore(false);
		return returnStr;
	}

	// IDからmyNumに変換
	public int changeFromID(String id){
		for(int i = 0;;i++){
			if(receiver.get(i).player.getID().equals(id)&&receiver.get(i).getWhereIs()!=-1&&receiver.get(i).getWhereIs()!=0)
				return i;
		}
	}

	//現在ログインしている自分以外の人取得
	public String getLoginPlayerData(int myNumber){
		// できれば入力途中は避けたい
		int max = receiver.size();
		String retStr = "3";

		int playerFound=0;//onlineplayerが見つかったかどうかの変数

		try{//receiver配列バグった時のために一応例外処理
			for(int i = 0;i < max;i++){

				if(i==myNumber) {//自分を見つけた
					//do nothing
				}else if(receiver.get(i).getWhereIs()==2||receiver.get(i).getWhereIs()==3){
					retStr += receiver.get(i).player.getPlayerData()+"\n";
					playerFound=1;
				}
			}

			if(playerFound==1) {//最後の改行消す
				retStr=retStr.substring(0,retStr.length()-1);
			}

		}
		catch(Exception e){
			e.printStackTrace();
		}
		return retStr;
	}

	//現在接続している人の状態を取得
	public void displayPlayer(){
		String state="";
		int where;

		System.out.println("現在ログインしているプレイヤー");
		for(int i = 0; i < receiver.size();i++) {
			where=receiver.get(i).getWhereIs();

			if(where!=-1&&where!=0) {
				if(where==1) {
					state="ログイン中";
				}
				else if(where==2||where==3) {
					state="オンライン中";
				}
				else if(where==4) {
					state="対局中";
				}

				System.out.println("number "+i+".ID: "+receiver.get(i).player.getID()+", "+state);
			}
		}
	}

	// １人のplayerdata表示
	public String getPlayerData(String str){
		while(playerSemaphore);
		setplayerSemaphore(true);

		String retStr = "";

		try{
			fileRead = new FileReader(FILE_NAME);
			bufFileRead = new BufferedReader(fileRead);

			while(bufFileRead.ready()){
				String readedLine = bufFileRead.readLine();

				String s[]=readedLine.split("/");

				String id=s[0];


				if(id.equals(str)) {
					retStr+=s[0]+"/"+s[2]+"/"+s[3]+"/"+s[4]+"/"+s[5];

					break;
				}

			}
			bufFileRead.close();
			fileRead.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		setplayerSemaphore(false);
		return retStr;
	}


	public void resultWin(int winnerNum,int loserNum){

		renewText(winnerNum, loserNum);
		outputText(receiver.get(loserNum).player.getID() + "/" + receiver.get(winnerNum).player.getID()+"/lose/win", RESULT_FILE_NAME);
	}

	public void resultDraw(int num1,int num2){

		renewText(num1, num2);
		outputText(receiver.get(num1).player.getID() +"/" + receiver.get(num2).player + "/draw", RESULT_FILE_NAME);
	}

	public void resultEarlyLose(int winnerNum,int loserNum){

		renewText(winnerNum, loserNum);
		outputText(receiver.get(loserNum).player.getID() +"/" + receiver.get(winnerNum).player.getID() + "/earlyLose/win", RESULT_FILE_NAME);
	}


}
