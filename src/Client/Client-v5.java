package pro2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Client {
	private Socket soc;
	private PrintWriter out;
	//private InputStreamReader check;
	private BufferedReader in;
	//private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private User myuser;
	private ArrayList<Question> myquestion = new ArrayList<Question>();		//自分がした質問
	private ArrayList<Question> myoffer = new ArrayList<Question>();		//自分に来ているオファー
	private ArrayList<Question> mycandidacy = new ArrayList<Question>();		//自分が立候補した質問
	private Group mygroup;													//グループ情報
	private static String ipaddress = "localhost";
	private static int port = 10084;

	public boolean connectServer() {		//サーバとの接続
        try {
            soc = new Socket(ipaddress,port);
            //ois = new ObjectInputStream(soc.getInputStream());
            //System.out.println("サーバと接続できました");
            return true;
        }catch(UnknownHostException e) {
            System.out.println("ホストに接続できません。");
            System.out.println(e);
        }catch(IOException e) {
            System.out.println("サーバー接続時にエラーが発生しました。");
            System.out.println(e);
        }
        return false;
	}

	public boolean loginRequest(String userName,String password) { //ログイン要求
        try {
        	//check = new InputStreamReader(soc.getInputStream());
            in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
            out.println("認証");
            out.println(userName);
            out.println(password);
            out.flush();
            String isPer = in.readLine();
            if(isPer.equals("ltrue") == true) {  //ログイン認証された
            	ois = new ObjectInputStream(soc.getInputStream());		//新しいObjectInputStreamを開けるために使用
            	User dammy = (User) ois.readObject();		//新しいOISを使用するためのdammyの取得
                return true;
            } else if(isPer.equals("lfalse") == true) {  //ログイン認証されなかった
                return false;
            } else {
                System.out.println("認証とは別の文字列です。");  //別の文字列が送られてきた
            }
            //ストリームをクローズする
            //out.close();
            //in.close();
        } catch(IOException e) {
            System.out.println(e);
        } catch (ClassNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
        return false;
    }

	public boolean accountRequest(String userName,String password, String ai/*合言葉*/) { //アカウント作成要求
        try {
            out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
            out.println("新規登録");
            out.println(userName);
            out.println(password);
            out.println(ai);
            out.flush();
            String isPer = in.readLine();
            System.out.println(isPer);
            if(isPer.equals("rtrue")) {  //新規作成できる
            	ois = new ObjectInputStream(soc.getInputStream());
            	User dammy = (User) ois.readObject();
            	System.out.println("ダミー受信成功");
                return true;
            } else if(isPer.equals("rfalse")) {  //新規作成できない
                return false;
            } else {
                System.out.println("確認とは別の文字列です。");  //別の文字列が送られてきた
            }
            //out.close();
            //in.close();
        }catch(IOException e) {
            System.out.println(e);
        } catch (ClassNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
        return false;
    }

	public String remindPassword(String ai) {		//パスワードを忘れた場合の要求
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			out.println("忘れた人");
			out.println(ai);
			out.flush();
			String isPer = in.readLine();
			if(isPer.equals("ftrue")==true) {
				String password = in.readLine();
				return password;
			}else if(isPer.equals("ffalse")){
				String s = "合言葉が違います";
				return s;
			}
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
        return null;

	}

	public void logoutRequest() {		//ログアウト
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println("ログアウト");
			out.flush();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	public void sendUserInformation(String job, String belong, ArrayList<String> group){		//アカウント情報転送
		//out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
		out.println("個人情報");
		//out.println(myuser.getName());		//自分のUserオブジェクトをサーバで探してもらうためのキー(名前)の転送
		out.println(job);
		out.println(belong);
		out.println(group.size());
		out.flush();
		for(String s: group) {
			out.println(s);
			out.flush();
		}
		System.out.println("アカウント情報送信成功");
	}

	public boolean requestGroupSearch(String group) {		//グループ検索要求
		try {
			//out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println("グループ検索");
			out.println(group);
			out.flush();
			//in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			if(in.readLine().equals("gtrue")) return true;		//該当するグループがあれば
			else if(in.readLine().equals("gfalse")) return false;		//該当するグループがなければ
			else {												//予期せぬ文字列が送られたら
				System.out.println("違う文字列が送られました。");
				return false;
			}
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return false;
	}



	public void receiveUserInformation(String name) {		//自分のアカウント情報を取得
		try {
			//out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println("情報");
			out.println(name);
			out.flush();
			//ObjectInputStream ois = new ObjectInputStream(soc.getInputStream());
			myuser = (User) ois.readObject();		//サーバから受け取った自分のUserオブジェクト
			System.out.println("オブジェクト取得成功");
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			System.out.println("オブジェクト取得失敗");
		} catch (ClassNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			System.out.println("オブジェクト取得失敗");
		}
	}

	public boolean creatGroup(String group/*グループ名*/, String explanation/*グループの説明*/) {		//グループ新規作成
		try {
			//out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println("グループ作成");
			out.println(group);
			out.println(explanation);
			out.flush();
			in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			String s = in.readLine();
			System.out.println(s);
			if(s.indexOf("y")==0) {
				StringBuffer sb = new StringBuffer(s);
				sb.deleteCharAt(0);
				s = sb.toString();
			}
			System.out.println(s);
			if(s.equals("gtrue")==true) {
				System.out.println("グループ作成成功");
				return true;
			}
			else if(s.equals("gfalse")==true) {
				System.out.println("グループ作成失敗");
				return false;
			}else {												//予期せぬ文字列が送られたら
				System.out.println("違う文字列が送られました。");
				return false;
			}
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		System.out.println("接続失敗");
		return false;
	}

	public void receiveGroupInformation(String group) {		//グループ内情報を取得		//メソッドの名前変更
		try {
			//out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println("入室中のグループ情報");
			out.println(group);
			out.flush();
			//soc.getInputStream().skip(100);
			//NonHeaderObjectInputStream ois = new NonHeaderObjectInputStream(soc.getInputStream());
			mygroup = (Group) ois.readObject();
			System.out.println("グループ取得成功");
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			System.out.println("グループ取得失敗");
		} catch (ClassNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			System.out.println("グループ取得失敗");
		}
	}

	public void sendQuestion(String question/*質問内容*/, String group/*質問の属するグループ*/,  int coin) {		//質問の送信
		//out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
		out.println("質問内容");
		out.println(question);				//質問内容の送信
		out.println(group);					//質問の属するグループ名の送信
		out.println(coin);					//質問にかけられたコイン数の送信
		out.flush();
	}

	public void sendOffer(String question/*質問内容*/, String answerer/*オファーを出す相手*/) {		//オファーの送信
		//out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
		out.println(question);		//どの質問に対するオファーかを識別するためのキー（質問内容）の送信
		out.println(answerer);
		out.flush();
	}

	public void receiveMyQuestion() {		//自分がした質問の情報の取得。回答、立候補者等の情報はここから取り出す。		//メソッドの名前変更
		try {
			//out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println("自分がした質問");
			//out.println(name);		//誰の質問かを判別するためのキー（自分の名前）の送信
			out.flush();
			//in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			String s1 = in.readLine();
			System.out.println(s1);
			if(s1.indexOf("y")==0) {
				StringBuffer sb = new StringBuffer(s1);
				sb.deleteCharAt(0);
				s1 = sb.toString();
			}
			System.out.println(s1);
			int num = Integer.parseInt(s1);
			String s2 = in.readLine();
			System.out.println(s2+"　2回目");
			//System.out.println(String.valueOf(num));
			//String checks = String.valueOf(check.read());
			//System.out.println(checks);
			ois = new ObjectInputStream(soc.getInputStream());
			for(int i=0; i<num; i++) {
				//NonHeaderObjectInputStream ois = new NonHeaderObjectInputStream(soc.getInputStream());
				myquestion.add((Question) ois.readObject());
			}
			System.out.println("自分がした質問の受け取り成功");
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	public void receiveOffer() {		//自分に来ているオファーの受信（具体的には、Questionクラスの属性offeredに自分の名前がセットされているQuestionオブジェクトの受信）
		try {
			//out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println("オファー来てるかな");
			out.flush();
			//in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			int num = Integer.parseInt(in.readLine());
			//ois = new ObjectInputStream(soc.getInputStream());
			for(int i=0; i<num; i++) {
				//ObjectInputStream ois = new ObjectInputStream(soc.getInputStream());
				myoffer.add((Question)ois.readObject());
			}
			System.out.println("自分に来ているオファーの受け取り成功");
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO 自動生成された catch ブロック
		}
	}

	public void receiveCandidate(){		//自分が立候補した質問の受信
		try {
			//out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println("立候補してる質問");
			out.flush();
			//in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			int num = Integer.parseInt(in.readLine());
			//ois = new ObjectInputStream(soc.getInputStream());
			for(int i=0; i<num; i++) {
				//ObjectInputStream ois = new ObjectInputStream(soc.getInputStream());
				mycandidacy.add((Question)ois.readObject());
			}
			System.out.println("自分が立候補した質問の受け取り成功");
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO 自動生成された catch ブロック
			//e.printStackTrace();
		}
	}

	public void sendAnswer(String question/*質問内容*/, String answer/*回答内容*/) {		//回答の送信
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println("回答");
			out.println(answer);
			out.println(question);
			out.flush();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	public void Candidacy(String question/*質問内容*/) {		//立候補
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println("回答立候補");
			out.println(question);		//立候補する質問の内容の送信
			out.flush();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	public void cancelCandidacy(String question) {		//自分がした立候補の取り消し
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println("立候補取り消し");
			out.println(question);		//立候補を取り消す質問の内容の送信
			out.flush();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

	}

	public void sendValue(String question/*質問内容*/, double value/*評価値*/) {		//評価値の送信
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println("評価値変更");
			out.println(question);
			out.println(value);
			out.flush();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	public void cancelOffer(String question/*質問内容*/) {		//オファー取り消し
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println("オファー取り消し");
			out.println(question);
			out.flush();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	public void refuseOffer(String question/*質問内容*/) {		//オファー拒否
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println("オファー拒否");
			out.println(question);
			out.flush();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	public String receiveMessage() {		//メッセージ受信
		String mes;
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println("メッセージ受信");
			out.flush();
			in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			mes = in.readLine();
			return mes;
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return null;
	}

	public void sendCoin(int coin) {		//送金
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println("送金");
			out.println(coin);
			out.flush();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	public int receiveCoin() {		//着金
		int c;
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println("着金");
			out.flush();
			in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			c = Integer.parseInt(in.readLine());
			return c;
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return 0;
	}

	public User getMyUser() {		//自分のUserオブジェクトを返す
		return myuser;
	}

	public ArrayList<Question> getMyQuestion() {		//自分のした質問オブジェクトのArrayListを返す
		return myquestion;
	}

	public ArrayList<Question> getMyOffer() {		//自分に来たオファー（質問オブジェクト）のArrayListを返す
		return myoffer;
	}

	public ArrayList<Question> getMyCandidacy() {		//自分が立候補した質問オブジェクトのArrayListを返す
		return mycandidacy;
	}

	public Group getMyGroup() {		//自分の属するグループオブジェクトを返す
		return mygroup;
	}


	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ

		//↓↓実験用の処理

		/*Client client = new Client();
		if(client.connectServer()==true) {
			System.out.println("サーバと接続できました");
			if(client.accountRequest("ua", "ua", "ひとりめ")==true) {
				if(client.creatGroup("横浜国立大学", "横国生のグループです")==true) {
					ArrayList<String> as = new ArrayList<String>();
					as.add("横浜国立大学");
					client.sendUserInformation("学生", "大学生", as);
				}else {
					System.out.println("グループ作成失敗");
				}
			}else {
				System.out.println("アカウント作成失敗");
			}

			client.receiveUserInformation("ua");
			User myuser = client.getMyUser();
			System.out.println(myuser.getName());
			ArrayList<String> g = new ArrayList<String>();
			g = myuser.getGroup();
			for(String s: g) {
				System.out.println(s);
			}

			client.sendQuestion("質問０", "横浜国立大学", 0);

		}*/

		/*Client client2 = new Client();
		if(client2.connectServer()==true) {
			System.out.println("サーバと接続できました");
			if(client2.accountRequest("ub", "ub", "ふたりめ")==true) {
				if(client2.creatGroup("東京大学", "東大生のグループです")==true && client2.creatGroup("横浜国立大学", "横国生のグループです")==true) {
					//System.out.println("");
					ArrayList<String> as = new ArrayList<String>();
					as.add("東京大学");
					as.add("横浜国立大学");
					client2.sendUserInformation("学生", "大学生", as);
				}else {
					System.out.println("グループ作成失敗");
				}
			}else {
				System.out.println("アカウント作成失敗");
			}

			client2.receiveUserInformation("ub");
			User myuser2 = client2.getMyUser();
			System.out.println(myuser2.getName());
			ArrayList<String> g2 = new ArrayList<String>();
			g2 = myuser2.getGroup();
			for(String s: g2) {
				System.out.println(s);
			}

			client2.sendQuestion("質問０", "横浜国立大学", 0);

		}*/

		Client client3 = new Client();
		if(client3.connectServer()) {
			System.out.println("サーバと接続できました");
			if(client3.loginRequest("ub", "ub")) {
				client3.sendQuestion("質問１", "横浜国立大学", 0);
				client3.sendQuestion("質問２", "東京大学", 0);

				client3.receiveUserInformation("ub");

				User myu = client3.getMyUser();
				System.out.println(myu.getName());

				client3.receiveMyQuestion();
				ArrayList<Question> myq = new ArrayList<Question>();
				myq = client3.getMyQuestion();

				for(Question q: myq) {
					System.out.println(q.getQuestion());
					System.out.println(q.getGroup());
				}

				client3.receiveGroupInformation("横浜国立大学");
				Group myg = client3.getMyGroup();

				System.out.println(myg.getgname());

				ArrayList<User> member = new ArrayList<User>();
				member = myg.getmember();
				for(User u: member) {
					System.out.println(u.getName());
				}

				ArrayList<Question> gq = new ArrayList<Question>();
				gq = myg.getchat();
				for(Question q: gq) {
					System.out.println(q.getQuestion());
				}

			}else {
				System.out.println("ログイン失敗");
			}
		}


	}

}
