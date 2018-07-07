package pro2;

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
	private String ipadress = "localhost";
	private int poat = 10027;

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
            out.println("認証");
            out.println(userName);
            out.println(password);
            out.flush();
            String isPer = in.readLine();
            if(isPer.equals("ltrue") == true) {  //ログイン認証された
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
            if(isPer.equals("rture") == true) {  //新規作成できる
                return true;
            } else if(isPer.equals("rfalse") == true) {  //新規作成できない
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
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
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
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	public boolean requestGroupSearch(String group) {		//グループ検索要求
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println("グループ検索");
			out.println(group);
			out.flush();
			in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
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



	public void receiveUserInformation() {		//自分のアカウント情報を取得
		User u;
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println("情報");
			out.println(myuser.getName());		//ほしいUserオブジェクトの名前（この場合は自分の名前）
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

	public boolean creatGroup(String group/*グループ名*/, String explanation/*グループの説明*/) {		//グループ新規作成
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println("グループ作成");
			out.println(group);
			out.println(explanation);
			out.flush();
			in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			if(in.readLine().equals("")==true) return true;
			else if(in.readLine().equals("")==false) return false;
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return false;
	}

	public void interGroup(String group) {

	}

	public Group receiveQuestions(String group) {		//グループ内情報を取得
		Group g;
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println("入室中のグループ情報");
			out.println(group);
			out.flush();
			ois = new ObjectInputStream(soc.getInputStream());
			g = (Group) ois.readObject();
			return g;
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return null;
	}

	public void sendQuestion(String question/*質問内容*/, String group/*質問の属するグループ*/,  String answerer/*オファーを出す相手*/, int coin) {		//質問の送信
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println("質問内容");
			out.println(question);				//質問内容の送信
			out.println(group);					//質問の属するグループ名の送信
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

	public ArrayList<Question> receiveAnswer() {		//自分がした質問の情報の取得。回答、立候補者等の情報はここから取り出す。
		ArrayList<Question> myquestion = new ArrayList<Question>();
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println("自分がした質問");
			out.println(myuser.getName());		//誰の質問かを判別するためのキー（自分の名前）の送信
			out.flush();
			ois = new ObjectInputStream(soc.getInputStream());
			while(true) {
				myquestion.add((Question) ois.readObject());
			}
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO 自動生成された catch ブロック
			return myquestion;
			//e.printStackTrace();
		}
		return null;
	}

	public ArrayList<Question> receiveOffer() {		//自分に来ているオファーの受信（具体的には、Questionクラスの属性offeredに自分の名前がセットされているQuestionオブジェクトの受信）
		ArrayList<Question> offer = new ArrayList<Question>();
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println("オファー来てるかな");
			out.flush();
			ois = new ObjectInputStream(soc.getInputStream());
			while(true) {
				offer.add((Question)ois.readObject());
			}
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO 自動生成された catch ブロック
			return offer;
		}
		return null;
	}

	public ArrayList<Question> receiveCandidate(){		//自分が立候補した質問の受信
		ArrayList<Question> cq = new ArrayList<Question>();

		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println("立候補してる質問");
			out.flush();
			ois = new ObjectInputStream(soc.getInputStream());
			while(true) {
				cq.add((Question)ois.readObject());
			}
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO 自動生成された catch ブロック
			return cq;
			//e.printStackTrace();
		}
		return null;
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
