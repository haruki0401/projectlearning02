package pro2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

public class Ser {

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
	
	static HashMap<String, String> hashA8 = new HashMap<>();//二人で情報をやり取りするための紐づけ

	
	
	static ArrayList<String> syoki = new ArrayList<String>();
	
	
	
	
	
	
	
	
	
	//コンストラクタ

	public Ser(int port) { //待ち受けポートを引数とする

		this.port = port; //待ち受けポートを渡す

		out = new PrintWriter[maxconnection]; //データ送信用オブジェクトを最大人数分用意
		oos = new ObjectOutputStream[maxconnection];
		receiver = new Receiver[maxconnection]; //データ受信用オブジェクトを最大人数分用意

		online = new boolean[maxconnection];//オンライン状態管理用配列を用意

		login = new boolean[maxconnection];//認証を突破した人
		
		
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
					
					User user = null;
					
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
							
							user=obj(a);

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
							
							user=obj(a);
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
						b = br[playerNo].readLine();//グループの説明文
						Group g=new Group(a,b);
						 
						FileOutputStream outFile = new FileOutputStream("group.obj");
						ObjectOutputStream outObject = new ObjectOutputStream(outFile);
						outObject.writeObject(a);
						outObject.close();
						outFile.close();
						
						
					}
					
					if(inputLine.equals("グループ入室")) {
						a = br[playerNo].readLine();//入室したいグループ名
						
						hashA2.put(playerNo,a);

						
					}
					if(inputLine.equals("入室中のグループ情報")) {
						a = br[playerNo].readLine();//情報が欲しいグループ名
						//そのグループのオブジェクトを取得させ送る。
						if(chat(a)!=null) {
						forwardgru(playerNo,chat(a));
						}else {
							forwardMessage("gfalse",playerNo);
						}
						
						
					}
					if(inputLine.equals("グループ退室")) {
						hashA2.put(playerNo, "not");

						
						}
					
					if(inputLine.equals("グループ検索")) {
						a = br[playerNo].readLine();//検索したいグループ名の取得
						
						if(serch(a)) {
							forwardMessage("gtrue",playerNo);
						}else{
							forwardMessage("gfalse",playerNo);
						}
						

						}

					
					
					
					if(inputLine.equals("質問内容")) {
						
						a=br[playerNo].readLine();//質問内容の受信
						String name=hashA1.get(playerNo);
						String group=hashA2.get(playerNo);
						
						User ques=obj(name);//質問者のオブジェクト確保
						
						Question p=new Question(user,a,group);
						
						Group g=chat(group);
						g.setchat(p);
						
						FileOutputStream outFile = new FileOutputStream("Question.obj");
						ObjectOutputStream outObject = new ObjectOutputStream(outFile);
						outObject.writeObject(p);
						outObject.close();
						outFile.close();
						

				         
				        
					}
					if(inputLine.equals("回答オファー")) {
						a = br[playerNo].readLine();//回答したい文面
						String name=hashA1.get(playerNo);
						User er=obj(name);
						Question k=ques(a);
					    if(k!=null) {
					    	k.setCandidates(user);
					    
					    
					    qnew(k);//ファイルの内容を更新
					    }
						
						
						

						
						
						}
					if(inputLine.equals("オファー来てるかな")) {
						a = br[playerNo].readLine();//質問の内容
						
						ques(a);
						if(ques(a)!=null) {
							forwardq(playerNo,ques(a));
						}else {
							forwardMessage("false",playerNo);
						}
						
						
					}
					
					if(inputLine.equals("回答者選出")) {
						a = br[playerNo].readLine();//選んだ回答者の名前を取得
						b = br[playerNo].readLine();//質問内容を受信
						
						Question k=ques(b);
						if(k!=null) {
						 k.setAnswerer(obj(a));
						
						qnew(k);
						}

						
						
						
						}
					
					if(inputLine.equals("回答")) {
						a = br[playerNo].readLine();//回答の取得
						Question k=ques(b);
						String group=hashA2.get(playerNo);
						if(k!=null) {
						 k.setAnswer(a);
						 Group g=chat(group);
							g.setchat(k);
						
						qnew(k);
						}
					}
					
//					if(inputLine.equals("チャット受信")) {
//						a = br[playerNo].readLine();//チャット内容受信
//						hutari[hashA11.get(hashA1.get(playerNo))].add(a);
//						
//						}
//					
//					if(inputLine.equals("チャット情報取得")) {
//						out[playerNo].println(hutari[hashA11.get(hashA1.get(playerNo))]);
//
//						out[playerNo].flush();
//						
//					}
					if(inputLine.equals("情報")) {//プレイヤーの全情報が詰まったオブジェクトを送る。
						a = br[playerNo].readLine();//情報が欲しい人の名前
						
						
						oos[playerNo].writeObject(obj(a));

						oos[playerNo].flush();
						
						oos[playerNo].reset();
						
					}
					if(inputLine.equals("質問数変更")) {
						String name=hashA1.get(playerNo);
						hashC2.put(name, hashC2.get(name)+1);
						user=obj(name);
						rewrite();
					}
					if(inputLine.equals("回答数変更")) {
						String name=hashA1.get(playerNo);
						hashC3.put(name, hashC3.get(name)+1);
						user=obj(name);
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
						
						user=obj(name);
						
						rewrite();
						
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
	public void forwardgru(int playerNo,Group a) {//グループオブジェクト送信
	try {
		oos[playerNo].writeObject(a);
	
		oos[playerNo].flush();
	
		oos[playerNo].reset();
	} catch (IOException e) {
		// TODO 自動生成された catch ブロック
		e.printStackTrace();
	}

}
	
	public void forwardq(int playerNo,Question a) {//グループオブジェクト送信
	try {
		oos[playerNo].writeObject(a);
	
		oos[playerNo].flush();
	
		oos[playerNo].reset();
	} catch (IOException e) {
		// TODO 自動生成された catch ブロック
		e.printStackTrace();
	}

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
	public void qnew(Question k) {//Questionfileの更新
		
		Question Q;
		try {
		ArrayList<Question> a = new ArrayList<Question>();
		FileInputStream inFile1 = new FileInputStream("Question.obj");
		ObjectInputStream inObject1 = new ObjectInputStream(inFile1);
try {
		while(true) {
			Q = (Question)inObject1.readObject();
			if(Q.getQuestion().equals(k.getQuestion())) {
				a.add(k);
			}else {
		    a.add(Q);
			}
			}
			
			}catch(java.io.EOFException e) {
		
				
				
				
			inObject1.close();
			inFile1.close();
			
			} catch (ClassNotFoundException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}

		
		
		
		FileOutputStream outFile = new FileOutputStream("Question.obj");
		ObjectOutputStream outObject = new ObjectOutputStream(outFile);
		for(Question r:a) {
		
			outObject.writeObject(r);
		
		}
		
			outObject.close();
		
		outFile.close();
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
public Group chat(String name) { //グループのチャット情報取得
	
	Group gu;
	try {	
	FileInputStream inFile1 = new FileInputStream("group.obj");
	ObjectInputStream inObject1 = new ObjectInputStream(inFile1);
	
	
	
	try {
	
	while(true) {
	gu = (Group)inObject1.readObject();
    if(gu.getgname().equals(name)) {
    	inObject1.close();
		inFile1.close();
    	return gu;
    }
	}
	
	}catch(java.io.EOFException e) {

		
		
		inObject1.close();
		inFile1.close();
		return null;
	}
	} catch (IOException e1) {
		// TODO 自動生成された catch ブロック
		e1.printStackTrace();
	
	
	} catch (ClassNotFoundException e) {
		// TODO 自動生成された catch ブロック
		e.printStackTrace();
	} 
	return null;
//ここには到達できない。
	
}public Question ques(String Q) { //クエスチョン取得
	
	Question q;
	try {	
	FileInputStream inFile1 = new FileInputStream("Question.obj");
	ObjectInputStream inObject1 = new ObjectInputStream(inFile1);
	
	
	
	try {
	
	while(true) {
	q = (Question)inObject1.readObject();
    if(q.getQuestion().equals(Q)) {
    	inObject1.close();
		inFile1.close();
    	return q;
    }
	}
	
	}catch(java.io.EOFException e) {

		
		
		inObject1.close();
		inFile1.close();
		return null;
	}
	} catch (IOException e1) {
		// TODO 自動生成された catch ブロック
		e1.printStackTrace();
	
	
	} catch (ClassNotFoundException e) {
		// TODO 自動生成された catch ブロック
		e.printStackTrace();
	} 
	return null;
//ここには到達できない。
	
}
	public static boolean serch(String name) {
		Group gu;
		try {	
		FileInputStream inFile1 = new FileInputStream("group.obj");
		ObjectInputStream inObject1 = new ObjectInputStream(inFile1);
		
		
		
		try {
		
		while(true) {
		gu = (Group)inObject1.readObject();
	    if(gu.getgname().equals(name)) {
	    	inObject1.close();
			inFile1.close();
	    	return true;
	    }
		}
		
		}catch(java.io.EOFException e) {

			
			
			inObject1.close();
			inFile1.close();
			return false;
		}
		} catch (IOException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		
		
		} catch (ClassNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} 
		return false;
	//ここには到達できない。
		
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
	
	
	public User obj(String name) {
		
		String pass=hashD1.get(name);
		String ai=hashD5.get(name);
		String job=hashD3.get(name);
		String belong=hashD4.get(name);
		double eval=hashC1.get(name);
		int qcount=hashC2.get(name);
		int acount=hashC3.get(name);
		ArrayList<String> gru=hashC4.get(name);
		User pl=new User(name,pass,ai);
		pl.setJob(job);
		pl.setBelong(belong);
		pl.setValue(eval);
		pl.setQuestion(qcount);
		pl.setAnswer(acount);
		pl.setGroup(gru);
		
		
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



