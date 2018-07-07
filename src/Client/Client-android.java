import java.awt.Button;
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

import android.app.Activity;
import android.app.Application;
import android.os.AsyncTask;
import android.widget.TextView;

public class Client extends Application{
	private Socket soc;
	private PrintWriter out;
	private BufferedReader in;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private User myuser;
	String ipadress;
	int poat;

	UserSupport us;
	Activity activity;
	boolean f;

	public void onCreate(){
		super.onCreate();
		myuser = new User();
	}

	public void connectServer(String ipAddress,String port, final Activity activity) {		//サーバとの接続
        this.acivity = activity;
        final String ipaddress = ipaddress;
        //final int portnum = Integer.parseInt(port);
        new AsyncTask<String, Void, String>(){

        	@Override
        	protected String doInBackground(String... strings){
        		try {
        			soc = new Socket(strings[0], Integer.parseInt(strings[1]));
       			}catch(UnknownHostException e) {
       				System.out.println("ホストに接続できません。");
           			System.out.println(e);
           			return "ホストに接続できません";
        		}catch(IOException e) {
        			System.out.println("サーバー接続時にエラーが発生しました。");
        			System.out.println(e);
        			return "サーバ接続時にエラーが発生しました";
        		}

        		return "接続成功";
        	}

        	@Override
        	protected void onPostExecute(String s){
        		super.onPostExecute(s);
        		TextView tv = (TextView)activity.findViewById(R.id.result_text);
        		Button loginbutton = (Button)activity.findViewById(R.id.go_login_button);
        		tv.setText(s);
        		if(s.equals("接続成功")){
        			loginbutton.setVisibility(loginbutton.VISIBLE);
        		}
        	}
        }.execute(ipAddress, port);
	}

	public boolean loginRequest(String userName, String password, final Activity activity) { //ログイン要求
        this.activity = activity;
        f = true;
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {
                try{
                    in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                    out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
                    out.println("認証");//合言葉
                    out.println(strings[0]);
                    out.println(strings[1]);
                    out.flush();
                    String isPer = in.readLine();
                    System.out.println(isPer);
                    if (isPer.equals("lture") == true) {  //ログイン認証された
                        f = true;
                        return "ログイン成功";
                    } else if (isPer.equals("lfalse") == true) {  //ログイン認証されなかった
                        f = false;
                        return "ログイン失敗";
                    } else {
                        System.out.println("認証とは別の文字列です。");  //別の文字列が送られてきた
                        f = false;
                        return "ログイン失敗";
                    }
                    //ストリームをクローズする
                    //out.close();
                    //in.close();
                } catch(IOException e){
                    System.out.println(e);
                }
                f = false;
                return "ログイン失敗";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                if(s.equals("ログイン成功")) {

                    Intent intent = new Intent(activity.getApplication(), HomeActivity.class);
                    startActivity(intent);
                }
            }
        }.execute(userName,password);
        return f;
    }

	public boolean accountRequest(String userName,String password, String ai, Activity activity) { //アカウント作成要求
        this.activity = activity;
        final Activity act = activity;
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {
                try {
                    out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
                    in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                    out.println("新規登録");
                    out.println(strings[0]);
                    out.println(strings[1]);
                    out.println(strings[2]);
                    out.flush();
                    String isPer = in.readLine();
                    if (isPer.equals("rtrue") == true) {  //新規作成できる
                        return "登録成功";
                    } else if (isPer.equals("rfalse") == true) {  //新規作成できない
                        return "登録失敗";
                    } else {
                        System.out.println("確認とは別の文字列です。");  //別の文字列が送られてきた
                    }
                    //out.close();
                    //in.close();
                }catch(IOException e){
                    System.out.println(e);
                }
                return "登録失敗";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(s.equals("登録成功")) {
                    Intent intent = new Intent(act.getApplication(), HomeActivity.class);
                    startActivity(intent);
                }
            }

        }.execute(userName, password, ai);
        return true;
    }

	public String remindPassword(String ai, Activity activity) {		//パスワードを忘れた場合の要求
		this.activity = activity;
        final Activity act = activity;
        String password;
        new AsyncTask<String, Void, String>() {
        	@Override
            protected String doInBackground(String... strings) {
        		try {
        			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
        			in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
        			out.println("忘れた人");
        			out.println(strings[0]);
        			out.flush();
        			String isPer = in.readLine();
        			if(isPer.equals("ftrue")==true) {
        				password = in.readLine();
        				return "パスワード取得成功";
        			}else if(isPer.equals("ffalse")){
        				return "合言葉が違います";
        			}
        		} catch (IOException e) {
        			// TODO 自動生成された catch ブロック
        			e.printStackTrace();
        		}
        		return "パスワード取得失敗";
        	}

        	@Override
    		protected void onPostExecute(String s) {
    		}
        }.execute(ai);

        return password;
	}

	public void logoutRequest(final Activity activity) {		//ログアウト
		this.activity = activity;
		final Activity act = activity;
		new AsyncTask<String, Void, String>(){
			@Override
			protected String doInBackground(String...strings) {
				try {
					out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
					out.println("ログアウト");
					out.flush();
					return "ログアウト成功";
				} catch (IOException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
				return
			}
		}.execute();

		@Override
		protected void onPostExecute(String s) {
		}

	}

	public void sendUserInformation(String job, String belong, ArrayList<String> group, Activity activity){		//アカウント情報転送
		this.activity = activity;
        final Activity act = activity;
        new AsyncTask<String, Void, String>() {
        	@Override
            protected String doInBackground(String... strings) {
        		try {
        			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
        			out.println("個人情報");
        			//out.println(myuser.getName());		//自分のUserオブジェクトをサーバで探してもらうためのキー(名前)の転送
        			out.println(strings[0]);
        			out.println(strings[1]);
        			out.println(group.size());
        			out.flush();
        			for(String s: group) {
        				out.println(s);
        				out.flush();
        			}
        			return "設定完了";
        		} catch (IOException e) {
        			// TODO 自動生成された catch ブロック
        			e.printStackTrace();
        		}
        		return "設定失敗";
        	}

        	@Override
    		protected void onPostExecute(String s) {
    		}

        }.execute(job, belong, group);

	}

	public boolean requestGroupSearch(String group, Activity activity) {		//グループ検索要求
		this.activity = activity;
        final Activity act = activity;
        f = false;
        new AsyncTask<String, Void, String>() {
        	@Override
            protected String doInBackground(String... strings) {
        		try {
        			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
        			out.println("グループ検索");
        			out.println(strings[0]);
        			out.flush();
        			in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
        			if(in.readLine().equals("gtrue")) {
        				f = true;
        				return "グループ有り";		//該当するグループがあれば
        			}
        			else if(in.readLine().equals("gfalse")) {
        				f = false;
        				return "グループ無し";		//該当するグループがなければ
        			}
        			else {												//予期せぬ文字列が送られたら
        				f = false;
        				System.out.println("違う文字列が送られました。");
        				return "検索失敗";
        			}
        		} catch (IOException e) {
        			// TODO 自動生成された catch ブロック
        			e.printStackTrace();
        		}
        	}

        	@Override
    		protected void onPostExecute(String s) {
    		}

        }.execute(group);

		return f;
	}

	public void receiveUserInformation(String name, Activity activity) {		//自分のアカウント情報を取得
		this.activity = activity;
        final Activity act = activity;
		User u;
		new AsyncTask<String, Void, String>() {
			@Override
            protected String doInBackground(String... strings) {
				try {
					out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
					out.println("情報");
					out.println(strings[0]);		//ほしいUserオブジェクトの名前（この場合は自分の名前）
					ois = new ObjectInputStream(soc.getInputStream());
					u = (User) ois.readObject();		//サーバから受け取った自分のUserオブジェクト
					myuser = u;
					return "取得成功";
				} catch (IOException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
				return "取得失敗";
			}

			@Override
    		protected void onPostExecute(String s) {
    		}

		}.execute(name);

	}

	public boolean creatGroup(String group/*グループ名*/, String explanation/*グループの説明*/, Activity activity) {		//グループ新規作成
		this.activity = activity;
        final Activity act = activity;
        f = false;
        new AsyncTask<String, Void, String>() {
        	@Override
            protected String doInBackground(String... strings) {
        		try {
        			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
        			out.println("グループ作成");
        			out.println(strings[0]);
        			out.println(strings[1]);
        			out.flush();
        			in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
        			if(in.readLine().equals("gtrue")==true) {
        				f = true;
        				return "作成成功";
        			}
        			else if(in.readLine().equals("gfalse")==true) {
        				f = false;
        				return "グループ既に存在";
        			}
        		} catch (IOException e) {
        			// TODO 自動生成された catch ブロック
        			e.printStackTrace();
        		}
        		return "作成失敗";
        	}

        	@Override
    		protected void onPostExecute(String s) {
    		}


        }.execute(group, explanation);

		return f;
	}

	public Group receiveQuestions(String group, Activity activity) {		//グループ内情報を取得
		this.activity = activity;
        final Activity act = activity;
        Group g = null;
        new AsyncTask<String, Void, String>() {
        	@Override
            protected String doInBackground(String... strings) {
        		try {
        			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
        			out.println("入室中のグループ情報");
        			out.println(group);
        			out.flush();
        			ois = new ObjectInputStream(soc.getInputStream());
        			g = (Group) ois.readObject();
        			return "取得成功";
        		} catch (IOException e) {
        			// TODO 自動生成された catch ブロック
        			e.printStackTrace();
        		} catch (ClassNotFoundException e) {
        			// TODO 自動生成された catch ブロック
        			e.printStackTrace();
        		}
        		return "取得失敗";
        	}

        	@Override
    		protected void onPostExecute(String s) {
    		}

        }.execute(group);
		return g;
	}

	public void sendQuestion(String question/*質問内容*/, String group/*質問の属するグループ*/,  String answerer/*オファーを出す相手*/, int coin/*質問にかけるコイン*/, Activity activity) {		//質問の送信
		this.activity = activity;
        final Activity act = activity;
        new AsyncTask<String, Void, String>() {
        	@Override
            protected String doInBackground(String... strings) {
        		try {
        			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
        			out.println("質問内容");
        			out.println(strings[0]);				//質問内容の送信
        			out.println(strings[1]);					//質問の属するグループ名の送信
        			if(answerer.equals(null)==false)sendOffer(strings[0], strings[2]);		//オファー相手が指定されている場合、オファーを送る
        			out.println(strings[3]);					//質問にかけられたコイン数の送信
        			out.flush();
        			return "質問成功";
        		} catch (IOException e) {
        			// TODO 自動生成された catch ブロック
        			e.printStackTrace();
        		}
        		return "質問失敗";
        	}

        	@Override
    		protected void onPostExecute(String s) {
    		}

        }.execute(question, group, answerer, coin);

	}

	public void sendOffer(String question/*質問内容*/, String answerer/*オファーを出す相手*/, Activity activity) {		//オファーの送信
		this.activity = activity;
        final Activity act = activity;
        new AsyncTask<String, Void, String>() {
        	@Override
            protected String doInBackground(String... strings) {
        		try {
        			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
        			out.println(strings[0]);		//どの質問に対するオファーかを識別するためのキー（質問内容）の送信
        			out.println(strings[1]);
        			out.flush();
        			return "オファー成功";
        		} catch (IOException e) {
        			// TODO 自動生成された catch ブロック
        			e.printStackTrace();
        		}
        		return "オファー失敗";
        	}

        	@Override
    		protected void onPostExecute(String s) {
    		}

        }.execute(question, answerer);

	}

	public ArrayList<Question> receiveAnswer(Activity activity) {		//自分がした質問の情報の取得。回答、立候補者等の情報はここから取り出す。
		this.activity = activity;
        final Activity act = activity;
		ArrayList<Question> myquestion = new ArrayList<Question>();
		new AsyncTask<String, Void, String>() {
			@Override
            protected String doInBackground(String... strings) {
				try {
					out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
					out.println("自分がした質問");
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
					return "取得成功";
					//e.printStackTrace();
				}
				return "取得失敗";
			}

			@Override
    		protected void onPostExecute(String s) {
    		}

		}.execute();

		return myquestion;
	}

	public ArrayList<Question> receiveOffer(Activity activity) {		//自分に来ているオファーの受信（具体的には、Questionクラスの属性offeredに自分の名前がセットされているQuestionオブジェクトの受信）
		this.activity = activity;
        final Activity act = activity;
		ArrayList<Question> offer = new ArrayList<Question>();
		new AsyncTask<String, Void, String>() {
			@Override
            protected String doInBackground(String... strings) {
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
					return "取得成功";
				}
				return "取得失敗";
			}

			@Override
    		protected void onPostExecute(String s) {
    		}

		}.execute();

		return offer;
	}

	public ArrayList<Question> receiveCandidate(Activity activity){		//自分が立候補した質問の受信
		this.activity = activity;
        final Activity act = activity;
		ArrayList<Question> cq = new ArrayList<Question>();
		new AsyncTask<String, Void, String>() {
			@Override
            protected String doInBackground(String... strings) {
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
					return "取得成功";
					//e.printStackTrace();
				}
				return "取得失敗";
			}

			@Override
    		protected void onPostExecute(String s) {
    		}

		}.execute();

		return cq;
	}

	public void sendAnswer(String question/*質問内容*/, String answer/*回答内容*/, Activity activity) {		//回答の送信
		this.activity = activity;
        final Activity act = activity;
        new AsyncTask<String, Void, String>() {
        	@Override
            protected String doInBackground(String... strings) {
        		try {
        			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
        			out.println("回答");
        			out.println(strings[0]);
        			out.println(strings[1]);
        			out.flush();
        			return "送信成功";
        		} catch (IOException e) {
        			// TODO 自動生成された catch ブロック
        			e.printStackTrace();
        		}
        		return "送信失敗";
        	}

        	@Override
    		protected void onPostExecute(String s) {
    		}

        }.execute(question, answer);

	}

	public void Candidacy(String question/*質問内容*/, Activity activity) {		//立候補
		this.activity = activity;
        final Activity act = activity;
        new AsyncTask<String, Void, String>() {
        	@Override
            protected String doInBackground(String... strings) {
        		try {
        			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
        			out.println("回答立候補");
        			out.println(strings[0]);
        			out.flush();
        			return "立候補成功";
        		} catch (IOException e) {
        			// TODO 自動生成された catch ブロック
        			e.printStackTrace();
        		}
        		return "立候補失敗";
        	}

        	@Override
    		protected void onPostExecute(String s) {
    		}

        }.execute(question);

	}

	public void cancelCandidacy(String question, Activity activity) {		//自分がした立候補の取り消し
		this.activity = activity;
        final Activity act = activity;
        new AsyncTask<String, Void, String>() {
        	@Override
            protected String doInBackground(String... strings) {
        		try {
        			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
        			out.println("立候補取り消し");
        			out.println(strings[0]);		//立候補を取り消す質問の内容の送信
        			out.flush();
        			return "取り消し成功";
        		} catch (IOException e) {
        			// TODO 自動生成された catch ブロック
        			e.printStackTrace();
        		}
        		return "取り消し失敗";
        	}

        	@Override
    		protected void onPostExecute(String s) {
    		}

        }.execute(question);

	}

	public void sendValue(String question/*質問内容*/, double value/*評価値*/, Activity activity) {		//評価値の送信
		this.activity = activity;
        final Activity act = activity;
        new AsyncTask<String, Void, String>() {
        	@Override
            protected String doInBackground(String... strings) {
        		try {
        			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
        			out.println("評価値変更");
        			out.println(strings[0]);
        			out.println(strings[1]);
        			out.flush();
        			return "送信成功";
        		} catch (IOException e) {
        			// TODO 自動生成された catch ブロック
        			e.printStackTrace();
        		}
        		return "送信失敗";
        	}

        	@Override
    		protected void onPostExecute(String s) {
    		}

        }.execute(question, value);

	}

	public void cancelOffer(String question/*質問内容*/, Activity activity) {		//オファー取り消し
		this.activity = activity;
        final Activity act = activity;
        new AsyncTask<String, Void, String>() {
        	@Override
            protected String doInBackground(String... strings) {
        		try {
        			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
        			out.println("オファー取り消し");
        			out.println(strings[0]);
        			out.flush();
        			return "取り消し成功";
        		} catch (IOException e) {
        			// TODO 自動生成された catch ブロック
        			e.printStackTrace();
        		}
        		return "取り消し失敗";
        	}

        	@Override
    		protected void onPostExecute(String s) {
    		}

        }.execute(question);

	}

	public void refuseOffer(String question/*質問内容*/, Activity activity) {		//オファー拒否
		this.activity = activity;
        final Activity act = activity;
        new AsyncTask<String, Void, String>() {
        	@Override
            protected String doInBackground(String... strings) {
        		try {
        			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
        			out.println("オファー拒否");
        			out.println(strings[0]);
        			out.flush();
        			return "拒否成功";
        		} catch (IOException e) {
        			// TODO 自動生成された catch ブロック
        			e.printStackTrace();
        		}
        		return "拒否失敗";
        	}

        	@Override
    		protected void onPostExecute(String s) {
    		}

        }.execute(question);

	}

	public String receiveMessage(Activity activity) {		//メッセージ受信
		this.activity = activity;
        final Activity act = activity;
		String mes = new String();
		new AsyncTask<String, Void, String>() {
			@Override
            protected String doInBackground(String... strings) {
				try {
					out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
					out.println("メッセージ受信");
					out.flush();
					in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
					mes = in.readLine();
					return "受信成功";
				} catch (IOException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
				return "受信失敗";
			}

			@Override
    		protected void onPostExecute(String s) {
    		}

		}.execute();

		return mes;
	}

	public void sendCoin(int coin, Activity activity) {		//送金
		this.activity = activity;
        final Activity act = activity;
        new AsyncTask<String, Void, String>() {
        	@Override
            protected String doInBackground(String... strings) {
        		try {
        			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
        			out.println("送金");
        			out.println(strings[0]);
        			out.flush();
        			return "送金成功";
        		} catch (IOException e) {
        			// TODO 自動生成された catch ブロック
        			e.printStackTrace();
        		}
        		return "送金失敗";
        	}

        	@Override
    		protected void onPostExecute(String s) {
    		}

        }.execute(coin);

	}

	public int receiveCoin(Activity activity) {		//着金
		this.activity = activity;
        final Activity act = activity;
		int c=0;
		new AsyncTask<String, Void, String>() {
			@Override
            protected String doInBackground(String... strings) {
				try {
					out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
					out.println("着金");
					out.flush();
					in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
					c = Integer.parseInt(in.readLine());
					return "着金成功";
				} catch (IOException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
				return "着金失敗";
			}

			@Override
    		protected void onPostExecute(String s) {
    		}

		}.execute();

		return c;
	}

	public User getMyUser() {		//自分のUserオブジェクトを返す
		return myuser;
	}

	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ

	}

}
