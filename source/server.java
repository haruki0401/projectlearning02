package pro2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StreamTokenizer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class server {

	private static int maxconnection = 105;//100人が最大接続人数

	private int port; // サーバの待ち受けポート

	private boolean[] online; //オンライン状態管理用配列

	private PrintWriter[] out; //データ送信用オブジェクト
	
	private ObjectInputStream[] ois;              /*  オブジェクト入力ストリーム  */

	private ObjectOutputStream[] oos;//出力用

	private Receiver[] receiver; //データ受信用オブジェクト

	private static int member;//接続している人の人数

	private static Socket[] socket;//受付用のソケット
	
	private static boolean[] login;
	
	private static int gru=0;//作成されているグループ数
	private static int hut=0;//作成されている二人のチャット数

	static HashMap<String, String> hashD1 = new HashMap<>();//hash1はプレイヤの認証など
	static HashMap<String, String> hashD2 = new HashMap<>();//hash2//key=aikotoba,value=password
    static HashMap<String, String> hashD3 = new HashMap<>();//名前、職業
	static HashMap<String, String> hashD4 = new HashMap<>();//名前、所属
	static HashMap<String, String> hashD5 = new HashMap<>();//名前、合言葉
	
	static HashMap<String, Double> hashC1 = new HashMap<>();//名前、評価値
	static HashMap<String, Integer> hashC2 = new HashMap<>();//名前、質問数
	static HashMap<String, Integer> hashC3 = new HashMap<>();//名前、回答数
	static HashMap<String, ArrayList> hashC4 = new HashMap<>();//名前、グループ
	
	static HashMap<Integer,String> hashA1 = new HashMap<>();//playerNo,名前
	static HashMap<Integer,String> hashA2 = new HashMap<>();//playerNo,今入室しているグループ（普段は全体ってことでall）
	static HashMap<Integer,String> hashA3 = new HashMap<>();//管理用No,グループ名
	static HashMap<String, Integer> hashA4 = new HashMap<>();//グループ名,管理用No
	static HashMap<String, String> hashA5 = new HashMap<>();//質問内容、名前
	static HashMap<String, String> hashA6 = new HashMap<>();//名前、質問内容
	static HashMap<String, HashMap<String,String>> hashA7 = new HashMap<String,HashMap<String,String>>(); //回答オファー保持用の二階ハッシュ
	static HashMap<String, String> hashA8 = new HashMap<>();//二人で情報をやり取りするための紐づけ
	static HashMap<String, String> hashA9 = new HashMap<>();//名前,相手
	static HashMap<String, String> hashA10 = new HashMap<>();//相手,名前
	static HashMap<String,Integer> hashA11= new HashMap<>();//チャット主、管理用No,
	static HashMap<String,Integer> hashA12= new HashMap<>();//相手、管理用No,
	
	
	static ArrayList<String> syoki = new ArrayList<String>();
	ArrayList[] group=new ArrayList[100000];//グループ内のチャット情報を格納するメソッドとりあえず1万のグループまでは対応←もっといい方法があるのかもしれないが…
	ArrayList[] hutari=new ArrayList[100000];//二人でするチャット情報を格納しておくためのメソッド、とりあえず一万としているが、必要なくなったらすぐ削除していく予定である。
	
	
	
	
	
	
	
	//コンストラクタ

	public server(int port) { //待ち受けポートを引数とする

		this.port = port; //待ち受けポートを渡す

		out = new PrintWriter[maxconnection]; //データ送信用オブジェクトを最大人数分用意
		oos = new ObjectOutputStream[maxconnection];
		receiver = new Receiver[maxconnection]; //データ受信用オブジェクトを最大人数分用意

		online = new boolean[maxconnection];//オンライン状態管理用配列を用意

		login = new boolean[maxconnection];//認証を突破した人
		
		hashA3.put(gru, "all");//管理用No0は全体用グル"all"へ進呈
		hashA4.put("all", gru);
		gru++;
	}

	//データ受信用スレッド(内部クラス)

	class Receiver extends Thread {

		private InputStreamReader[] sisr; //受信データ用文字ストリーム

		private BufferedReader[] br; //文字ストリーム用のバッファ

		private int playerNo;//プレイヤを識別するための番号

		// 内部クラスReceiverのコンストラクタ

		Receiver(Socket socket, int playerNo) {

			sisr = new InputStreamReader[maxconnection];

			br = new BufferedReader[maxconnection];

			try {

				this.playerNo = playerNo; //プレイヤ番号を渡す

				sisr[playerNo] = new InputStreamReader(socket.getInputStream());

				br[playerNo] = new BufferedReader(sisr[playerNo]);

			} catch (IOException e) {

				System.err.println("データ受信時にエラーが発生しました: " + e);

			}

		}

		// 内部クラス Receiverのメソッド

		@Override

		public void run() {

			try {
				if(member >= 100) {
					forwardMessage("最大接続人数を超えているため、接続を切ります", playerNo);
					socket[playerNo].close();

				}

				int aite = 0;

				while(true) {// データを受信し続ける
					if(hashA1.get(playerNo)!=null&&hashA7.get(hashA1.get(playerNo))!=null) {//回答オファー確認場所	
						
						hashA7.get(hashA1.get(playerNo));
						out[playerNo].println((hashA7.get(hashA1.get(playerNo))));

						out[playerNo].flush();//クライアントに回答オファーが来ている名前と質問内容のハッシュを送信
					
					
					}
					String inputLine = br[playerNo].readLine();//データを一行分読み込む

					String a = "";

					String b = "";

					String c = "";
					
					if(inputLine.equals("認証")) {

						a = br[playerNo].readLine();

						b = br[playerNo].readLine();

						if(ninsyou(a, b)) {

							forwardMessage("ltrue", playerNo);

							hashA1.put(playerNo, a);

							login[playerNo] = true;

						} else {
							forwardMessage("lfalse", playerNo);
						}

					}
					
					if(inputLine.equals("新規登録")) {

						a = br[playerNo].readLine();

						b = br[playerNo].readLine();

						c = br[playerNo].readLine();

						String k = newpeople(a, b, c);

						if(k.equals("rtrue")) {

							forwardMessage("rtrue", playerNo);
						} else {
							forwardMessage("rfalse" + k, playerNo);
						}

					}
					
					if(inputLine.equals("忘れた人")) {

						a = br[playerNo].readLine();

						b = br[playerNo].readLine();

						String str;

						if((str = forgetman(a, b)) != null) {

							forwardMessage("ftrue" + str, playerNo);

						}

					}
					if(inputLine.equals("ログアウト")) {

						if(logout(playerNo)) {

							login[playerNo] = false;

						}

					}
					
					if(inputLine.equals("個人情報")) {
						
						a = br[playerNo].readLine();//職業

						b = br[playerNo].readLine();//所属
						
						hashD3.put(hashA1.get(playerNo),a);
						hashD4.put(hashA1.get(playerNo),b);//要素の置き換え
						
						re();//ファイルの中身の書き換え
						
						
						
						
					}
					///グループ化チャット機能
					if(inputLine.equals("グループ作成")) {
						a = br[playerNo].readLine();//グループ名//同じ名前はだめにしようと思っている
						
						
						hashA3.put(gru,a );
						hashA4.put(a,gru);
						
						gru++;
					}
					
					if(inputLine.equals("グループin")) {
						a = br[playerNo].readLine();//入室したいグループ名
						
						hashA2.put(playerNo,a);
						
						forwardArray(group[hashA4.get(a)],playerNo);//そのグループの現在のチャット情報を取得させる。
						
					}
					if(inputLine.equals("グループout")) {
						hashA2.put(playerNo, "not");

						
						
						}
					if(inputLine.equals("質問内容")) {
						
						a=br[playerNo].readLine();//質問内容の受信
						int k=hashA4.get(hashA2.get(playerNo));
						group[k].add(a);//チャット情報を保持するgroupに格納
						
						if(group[k].size()>100) {
							group[k].remove(0);
							
						}
						
						for(int i=0;i<maxconnection;i++) {
						
							if(hashA2.get(playerNo)!=hashA2.get(i)) {//
								forwardArray(group[k],i);//現在同じグルにinしている人達に送信
						
						}
					}
					if(inputLine.equals("回答オファー")) {
						a = br[playerNo].readLine();//回答したい文面
						String qname;//質問した人の名前
						
						qname=hashA5.get(a);
						
						hashA6.put(hashA1.get(playerNo),a);
						hashA7.put(qname,hashA6);
						
						
						}
					if(inputLine.equals("回答オファー取り消し")) {
						hashA6.remove(hashA1.get(playerNo));
						
						}
					if(inputLine.equals("回答者選出")) {
						a = br[playerNo].readLine();//選んだ回答者の名前を取得
						b = br[playerNo].readLine();//質問内容を受信
						
						hashA9.put(hashA1.get(playerNo),a);
						hashA10.put(a,hashA1.get(playerNo));
						hashA11.put(hashA1.get(playerNo),hut);
						hashA11.put(a,hut);
						hut++;
						hutari[hashA11.get(hashA1.get(playerNo))].add(b);//質問内容をあらかじめ持っておく
						
						
						
						}
					
					if(inputLine.equals("チャット受信")) {
						a = br[playerNo].readLine();//チャット内容受信
						hutari[hashA11.get(hashA1.get(playerNo))].add(a);
						
						}
					
					if(inputLine.equals("チャット情報取得")) {
						out[playerNo].println(hutari[hashA11.get(hashA1.get(playerNo))]);

						out[playerNo].flush();
						
					}
					if(inputLine.equals("情報")) {//プレイヤーの全情報が詰まったオブジェクトを送る。
						a = br[playerNo].readLine();//情報が欲しい人の名前
						
						
						oos[playerNo].writeObject(obj(a));

						oos[playerNo].flush();
						
						oos[playerNo].reset();
						
					}
					if(inputLine.equals("質問数変更")) {
						String name=hashA1.get(playerNo);
						hashC2.put(name, hashC2.get(name)+1);
						rewrite();
					}
					if(inputLine.equals("回答数変更")) {
						String name=hashA1.get(playerNo);
						hashC3.put(name, hashC3.get(name)+1);
						rewrite();
					}
					if(inputLine.equals("評価値変更")) {
						a = br[playerNo].readLine();//評価値の取得
						String name=hashA1.get(playerNo);
						
						double value;
						double kai;
						double hen;
						hen=Double.parseDouble(a);
						kai=(double)hashC3.get(name);
						value=hashC1.get(name);
						value=(value/(kai-1)+hen)/kai;//平均値を算出しなおしている。
						hashC1.put(name,value);
						
						rewrite();
						
					}
					
					
					
					
				}	
					
					
					
				}
			} catch (IOException e) { // 接続が切れたとき

				hashA2.remove(playerNo);//グループから出ることになるので、インしているグルから抜ける
				member--;//接続が切れた人数分減らす
				
				login[playerNo]=false;
				online[playerNo] = false; //プレイヤの接続状態を更新する

			


			}

		}

}

	

	///// メソッド///////////////////
	public void acceptClient() { //クライアントの接続(サーバの起動)

		int n = 1;

		member = 0;//誰も接続していないときは0

		try {

			System.out.println("サーバが起動しました．");

			ServerSocket ss = new ServerSocket(port); //サーバソケットを用意

			while(true) {

				if(online[n] == false) {

					socket[n] = ss.accept(); //新規接続を受け付ける

					online[n] = true;

					out[n] = new PrintWriter(socket[n].getOutputStream(), true);

					receiver[n] = new Receiver(socket[n], n);

					online[n] = true;

					receiver[n].start();//スレッドを起動←コイツをどこで発動するか

					member++;

				} else if(online[n] == true) {

					n++;

					if(n > 104) {//注

						n = 1;

					}

				}

			}

		} catch (Exception e) {

			System.err.println("ソケット作成時にエラーが発生しました: " + e);

		}

	}
	public void forwardMessage(String msg, int playerNo) { //操作情報の転送

		out[playerNo].println(msg);

		out[playerNo].flush();

	}
	
	public void forwardArray(ArrayList msg, int playerNo) { //操作情報の転送

		out[playerNo].println(msg);

		out[playerNo].flush();

	}
	public boolean ninsyou(String name, String pass) {//プレイヤの認証

		boolean nin = true;

		if(hashD1.get(name) != null && hashD1.get(name).equals(pass)) {

			for(int i = 1; i < 100; i++) {

				if(login[i] == true) {

					if(hashA1.get(i).equals(name)) {
						nin = false;
					}

				}

			}

			return nin;

		} else {

			return false;
		}

	}
	public void rewrite() {//ファイルの中身をハッシュを元に書き換えるメソッド,chat.kanri.csv

		try {

			FileWriter fw = new FileWriter("chat.kanri.csv");

			for(String nkey : hashC1.keySet()) {

				fw.write(nkey + "," + hashC1.get(nkey) + "," + hashC2.get(nkey) + "," + hashC3.get(nkey) + "," + hashC4.get(nkey) + "\n");

			}

			fw.close();

		} catch (IOException e) {

			// TODO 自動生成された catch ブロック

			e.printStackTrace();

		}

	}
	
	public void re() {//ファイルの中身をハッシュを元に書き換えるメソッド,data.csv

		try {

			FileWriter fw = new FileWriter("data.csv");

			for(String nkey : hashD1.keySet()) {

				fw.write(nkey + "," + hashD1.get(nkey) + "," + hashD5.get(nkey) + "," + hashD3.get(nkey) + "," + hashD4.get(nkey) + "\n");

			}

			fw.close();

		} catch (IOException e) {

			// TODO 自動生成された catch ブロック

			e.printStackTrace();

		}

	}
	
	public String newpeople(String a, String b, String c) {//新規登録の認証

		String k = "";

		int count = 0;//フラグ

		try {

			FileWriter fw1 = new FileWriter("data_100000.csv", true);

			FileWriter fw2 = new FileWriter("chat.kanri.csv", true);

			if(!c.matches("^[ぁ-んー]*$")) {
				k = "合言葉はひらがなのみです";

			} else if(!a.matches("^[0-9a-zA-Z]*$") || !b.matches("^[0-9a-zA-Z]*$")) {//合言葉のひらがな判定、正規表現
				k = "名前とパスワードは半角英数字です";

			} else if(a.length() > 8) {
				k = "名前が8文字以上です";
			} else if(b.length() > 16) {
				k = "パスワードが16文字以上です";
			} else {

				if(!hashD1.containsKey(a)) {

					fw1.write(a + "," + b + "," + c + ","+"job"+","+"syozoku"+"\n");

					fw2.write(a + "," + "0" + "," + 0 + "," + 0 + "," + "all"+ "\n");
					
					syoki.add("all");

					hashD1.put(a, b);

					hashD2.put(c, b);

					hashD3.put(a, "job");

					hashD4.put(a, "shozoku");
					
					hashD5.put(a, c);

					hashC1.put(a, (double) 0);

					hashC2.put(a, 0);

					hashC3.put(a, 0);

					hashC4.put(a, syoki);

					k = "rtrue";
					
					syoki.clear();

				} else {
					//System.out.println(k);
					k = "同じ名前の人がいます";

				}
			}

			fw1.close();

			fw2.close();

		} catch (IOException e) {

			// TODO 自動生成された catch ブロック

			e.printStackTrace();

		}

		return k;

	}
	public boolean logout(int a) {//ログアウト処理

		return true;

	}
	public static void kanri() {//ユーザー情報の管理

		try {

			File newfile = new File("chat.kanri.csv");

			newfile.createNewFile();

			// FileReaderオブジェクトを作成する

			FileReader fr = new FileReader(newfile);

			// BufferedReaderオブジェクトを作成する

			BufferedReader br = new BufferedReader(fr);

			String str;
            int i=4;
			while((str = br.readLine()) != null) {

				String[] line = str.split(",");

				hashC1.put(line[0], Double.parseDouble(line[1]));

				hashC2.put(line[0], Integer.parseInt(line[2]));

				hashC3.put(line[0], Integer.parseInt(line[3]));
				
				
				if(line[i]!="") {
					syoki.add(line[i]);
				}
				
				
if(syoki!=null) {
	

				hashC4.put(line[0], syoki);
				syoki.clear();
}	

			}

			fr.close();

		}

		catch (IOException ioe) {

			ioe.printStackTrace();

		}

	}
	
	
	public String forgetman(String a, String b) {//忘れた人にパスワードを返す。

		String key1 = hashD1.get(a);

		String key2 = hashD2.get(b);

		if(key1 != null && key2 != null) {

			if(key1.equals(key2)) {

				return key1;

			} else {

				return null;

			}

		} else {

			return null;

		}

	}
	
	
	public Player obj(String name) {
		
		String pass=hashD1.get(name);
		String ai=hashD5.get(name);
		String job=hashD3.get(name);
		String syozoku=hashD4.get(name);
		double eval=hashC1.get(name);
		int qcount=hashC2.get(name);
		int acount=hashC3.get(name);
		ArrayList gru=hashC4.get(name);
		Player pl=new Player(name,pass,ai,job,syozoku,eval,qcount,acount,gru);
		
		return pl;
		
		
	}
	///////////////////////////////////

	

	/////////////mainメソッド/////////////////////////////////////////////////

	public static void main(String[] args) { //main

		try {

			File newfile = new File("data.csv");

			newfile.createNewFile();

			// FileReaderオブジェクトを作成する

			FileReader fr = new FileReader("data.csv");

			// BufferedReaderオブジェクトを作成する

			BufferedReader br = new BufferedReader(fr);

			// StreamTokenizerオブジェクトを作成する

			StreamTokenizer st = new StreamTokenizer(br);

			// ',' を区切り文字とする時

			st.whitespaceChars(',', ',');

			String name = "";

			String password = "";

			String aikotoba = "";
			
            String job ="";
            
            String syozoku="";

			while(st.nextToken() != StreamTokenizer.TT_EOF) {

				switch (st.ttype) {

				case StreamTokenizer.TT_WORD:

					if(name.equals("")) {

						name = st.sval;

					} else if(password.equals("")) {

						password = st.sval;

					} else if(aikotoba.equals("")) {

						aikotoba = st.sval;

					}else if(job.equals("")) {

						job = st.sval;

					}else if(syozoku.equals("")) {

						syozoku = st.sval;

					}

				}
//hash1,hash2,hash3,hash4に格納している
				if(!name.equals("") && !password.equals("") && !aikotoba.equals("")&& !job.equals("")&& !syozoku.equals("")) {
					hashD1.put(name, password);

					hashD2.put(aikotoba, password);
					
					hashD3.put(name,job);
					
					hashD4.put(name,syozoku);
					
					hashD5.put(name,aikotoba );

				

					name = "";

					password = "";

					
					aikotoba = "";
					
					job="";
					
					syozoku="";

				}

			}

			fr.close();

		}

		catch (IOException ioe) {

			ioe.printStackTrace();

		}

		kanri();

		server server = new server(10000); //待ち受けポート10000番でサーバオブジェクトを準備

		server.acceptClient(); //クライアント受け入れを開始

	}
}



class Player{
	String name;
	String pass;
	String ai;
	String job;
	String syozoku;
	double eval;
	int qcount;
	int acount;
	ArrayList gru;
	Player(String name,String pass,String ai,String job,String syozoku,double eval,int qcount,int acount,ArrayList gru){
		this.name=name;
		this.pass=pass;
		this.ai=ai;
		this.job=job;
		this.syozoku=syozoku;
		this.eval=eval;
		this.qcount=qcount;
		this.acount=acount;
		this.gru=gru;
		
		
	}
	
	
}
//class chat{
	//String name1;
	//String name2;
	//ArrayList bun;
//}
