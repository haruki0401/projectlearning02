
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
	private Socket soc;
	private PrintWriter out;
	private BufferedReader in;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private User myuser;

	public void sendUserInformation(String job, String belong, String group){		//アカウント情報転送
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println(job);
			out.println(belong);
			out.println(group);
			out.flush();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	public void receiveUserInformation() {		//自分のアカウント情報を取得
		User u = null;
		try {
			ois = new ObjectInputStream(soc.getInputStream());
			u = (User) ois.readObject();
			myuser = u;
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	public void creatGroup(String group, String explanation) {		//グループ新規作成
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

	public User[] receiveOtherUserInformation() {		//他ユーザの情報を取得
		User[] u = null;
		int member = 0;
		try {
			in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			member = Integer.parseInt(in.readLine());
			ois = new ObjectInputStream(soc.getInputStream());
			for(int i=0; i<member; i++) {
				u[i] = (User) ois.readObject();
			}
		} catch (IOException | ClassNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return u;
	}

	public Question[] receiveQuestions(String group) {		//グループ内の質問を取得
		Question q[] = null;
		int number = 0;
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println(group);
			out.flush();
			in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			number = Integer.parseInt(in.readLine());
			ois = new ObjectInputStream(soc.getInputStream());
			for(int i=0; i<number; i++) {
				q[i] = (Question) ois.readObject();
			}
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return q;
	}

	public void sendQuestion(String question/*質問内容*/, String answerer/*オファーを出す相手*/, int coin) {		//質問の送信
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println(myuser.getName());
			out.println(myuser.getGroup());
			out.println(question);
			if(answerer.equals(null)==false)sendOffer(answerer);
			out.println(coin);
			out.flush();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	public void sendOffer(String answerer/*オファーを出す相手*/) {		//オファーの送信
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println(answerer);
			out.flush();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	public Question receiveAnswer() {		//自分がした質問情報の取得。回答、立候補者等の情報はここから取り出す。
		Question q = null;
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println(myuser.getName());
			out.flush();
			ois = new ObjectInputStream(soc.getInputStream());
			q = (Question) ois.readObject();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return q;
	}

	public Question[] receiveOffer() {		//自分に来ているオファーの受信
		Question[] q = null;
		int number = 0;
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println(myuser.getName());
			out.flush();
			in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			number = Integer.parseInt(in.readLine());
			ois = new ObjectInputStream(soc.getInputStream());
			for(int i=0; i<number; i++) {
				q[i] = (Question) ois.readObject();
			}
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return q;
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
			out.println(myuser.getName());
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
		String mes = null;
		try {
			in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			mes = in.readLine();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return mes;
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
		int c = 0;
		try {
			in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			c = Integer.parseInt(in.readLine());
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return c;
	}

	public User getMyUser() {
		return myuser;
	}

	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ

	}

}
