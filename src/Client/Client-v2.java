
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Client {
	private Socket soc;
	private PrintWriter out;
	private BufferedReader in;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private User myuser;

	public boolean connectServer(String ipAddress,int port) {		//サーバとの接続
        try {
            soc = new Socket(ipAddress,port);
            return true;
        }catch(UnknownHostException e) {
            System.out.println("ホストに接続できません。");
            System.out.println(e);
            return false;
        }catch(IOException e) {
            System.out.println("サーバー接続時にエラーが発生しました。");
            System.out.println(e);
            return false;
        }
	}

	public boolean loginRequest(String userName,String password) { //ログイン要求
        try {
            in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
            out.println(userName);
            out.println(password);
            out.flush();
            String isPer = in.readLine();
            if(isPer.equals("permit") == true) {  //ログイン認証された
                return true;
            } else if(isPer.equals("notPermit") == true) {  //ログイン認証されなかった
                return false;
            } else {
                System.out.println("認証とは別の文字列です。");  //別の文字列が送られてきた
            }
            //ストリームをクローズする
            //out.close();
            //in.close();
        } catch(IOException e) {
            System.out.println(e);
        }
        return false;
    }

	public boolean accountRequest(String userName,String password) { //アカウント作成要求
        try {
            out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
            out.println(userName);
            out.println(password);
            out.flush();
            String isPer = in.readLine();
            if(isPer.equals("permit") == true) {  //新規作成できる
                return true;
            } else if(isPer.equals("notPermit") == true) {  //新規作成できない
                return false;
            } else {
                System.out.println("確認とは別の文字列です。");  //別の文字列が送られてきた
            }
            //out.close();
            //in.close();
        }catch(IOException e) {
            System.out.println(e);
        }
        return false;
    }

	public boolean requestGroupSearch(String group) {		//グループ検索要求
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println(group);
			out.flush();
			in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			if(in.readLine().equals("true")) return true;		//該当するグループがあれば
			else if(in.readLine().equals("false")) return false;		//該当するグループがなければ
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

	public void sendUserInformation(String job, String belong, ArrayList<String> group){		//アカウント情報転送
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println(myuser.getName());		//自分のUserオブジェクトをサーバで探してもらうためのキー(名前)の転送
			out.println(job);
			out.println(belong);
			out.flush();
			for(String s: group) {
				out.println(s);
				out.flush();
			}
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	public void receiveUserInformation() {		//自分のアカウント情報を取得
		User u;
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println(myuser.getName());		//自分のUserオブジェクトをサーバで探してもらうためのキー(名前)の転送
			ois = new ObjectInputStream(soc.getInputStream());
			u = (User) ois.readObject();		//サーバから受け取った自分のUserオブジェクト
			myuser = u;
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	public void creatGroup(String group/*グループ名*/, String explanation/*グループの説明*/) {		//グループ新規作成
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println(group);
			out.println(explanation);
			out.flush();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	public ArrayList<User> receiveOtherUserInformation(String group) throws ClassNotFoundException {		//同グループの他ユーザの情報を取得
		ArrayList<User> groupmember = new ArrayList<User>();
		int number;		//メンバー数
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println(group);		//どのグループのメンバーかをサーバが探すためのキー(グループ名)の転送
			in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			number = Integer.parseInt(in.readLine());		//メンバー数
			ois = new ObjectInputStream(soc.getInputStream());
			for(int i=0; i<number; i++) {
				groupmember.add((User) ois.readObject());
			}
			return groupmember;
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return null;
	}

	public ArrayList<Question> receiveQuestions(String group) {		//グループ内の質問を取得
		ArrayList<Question> groupquestion = new ArrayList<Question>();
		int number;		//質問数
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println(group);
			out.flush();
			in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			number = Integer.parseInt(in.readLine());
			ois = new ObjectInputStream(soc.getInputStream());
			for(int i=0; i<number; i++) {
				groupquestion.add((Question)ois.readObject());
			}
			return groupquestion;
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return null;
	}

	public void sendQuestion(String question/*質問内容*/, String answerer/*オファーを出す相手*/, int coin) {		//質問の送信
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println(myuser.getName());		//質問者の名前の送信
			out.println(myuser.getGroup());		//質問の属するグループ名の送信
			out.println(question);				//質問内容の送信
			if(answerer.equals(null)==false)sendOffer(question, answerer);		//オファー相手が指定されている場合、オファーを送る
			out.println(coin);					//質問にかけられたコイン数の送信
			out.flush();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	public void sendOffer(String question/*質問内容*/, String answerer/*オファーを出す相手*/) {		//オファーの送信
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println(question);		//どの質問に対するオファーかを識別するためのキー（質問内容）の送信
			out.println(answerer);
			out.flush();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	public ArrayList<Question> receiveAnswer() {		//自分がした質問情報の取得。回答、立候補者等の情報はここから取り出す。
		ArrayList<Question> myquestion = new ArrayList<Question>();
		int number;
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println(myuser.getName());		//誰の質問かを判別するためのキー（自分の名前）の送信
			out.flush();
			in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			number = Integer.parseInt(in.readLine());
			ois = new ObjectInputStream(soc.getInputStream());
			for(int i=0; i<number; i++) {
				myquestion.add((Question) ois.readObject());
			}
			return myquestion;
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return null;
	}

	public ArrayList<Question> receiveOffer() {		//自分に来ているオファーの受信（具体的には、Questionクラスの属性offeredに自分の名前がセットされているQuestionオブジェクトの受信）
		ArrayList<Question> offer = new ArrayList<Question>();
		int number;
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println(myuser.getName());		//条件に合致する質問を識別するためのキー（自分の名前）の送信
			out.flush();
			in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			number = Integer.parseInt(in.readLine());
			ois = new ObjectInputStream(soc.getInputStream());
			for(int i=0; i<number; i++) {
				offer.add((Question) ois.readObject());
			}
			return offer;
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return null;
	}

	public void sendAnswer(String question/*質問内容*/, String answer/*回答内容*/) {		//回答の送信
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println(question);
			out.println(answer);
			out.flush();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	public void Candidacy(String question/*質問内容*/) {		//立候補
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println(question);
			out.println(myuser.getName());		//立候補者（自分の名前）の送信。
			out.flush();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	public void sendValue(String question/*質問内容*/, double value/*評価値*/) {		//評価値の送信
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
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

	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ

	}

}
