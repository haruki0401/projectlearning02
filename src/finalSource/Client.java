package com.example.tomoko.pro2;
//import java.awt.Button;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
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

import android.app.Application;
import android.os.AsyncTask;

public class Client extends Application{
    private Socket soc;
    private PrintWriter out;
    private BufferedReader in;
    //private ObjectOutputStream oos;
    private ObjectInputStream ois;
    //写真用

    private User myuser;

    //変更点
    private BufferedOutputStream bos;
    private byte[] imagebyte;
    private byte[] imagebyte2;

    private ArrayList<Question> myquestion = new ArrayList<Question>();		//自分がした質問
    private ArrayList<Question> myoffer = new ArrayList<Question>();		//自分に来ているオファー
    private ArrayList<Question> mycandidacy = new ArrayList<Question>();		//自分が立候補した質問
    private Group mygroup;//グループ情報
    private static String ipaddress = "172.20.10.2";
    private static int port = 10031;

    private Question watchingQ;//自分が今見ている質問

    private Listener listener;
    private User dammy;
    //Activity activity;
    boolean f;

    private CallBackTask callbacktask;
    //private CallBackTask2 callbacktask2;

    public void onCreate(){
        super.onCreate();
        //myuser = new User();
        //g = new Group("よここく", "よここくについて",myuser);//あとで消す
        //mq = new ArrayList<>();//変更点
        //Question q = new Question(myuser, "わからない", "よここく", 200);
        // mq.add(q);
    }

    //リスナーをセットする
    void setListener(Listener listener) {
        this.listener = listener;
    }
    //インタフェース
    interface Listener {
        void onSuccess(int i);
    }

    /*void setListenerU(ListenerU listenerU) {
        this.listenerU = listenerU;
    }

    interface ListenerU {
        void onSuccess(User user);
    }*/
    //コールバック用のクラス
    public static class CallBackTask{
        public boolean CallBack(String result) {

            return false;
        }
    }

    public void setOnCallBack(CallBackTask cbt) {
        callbacktask = cbt;
    }

    /*public static class CallBackTask2{
        public void CallBack2(String result) {

        }
    }

    public void setOnCallBack2(CallBackTask2 cbt) {callbacktask2 = cbt;}*/

    public void connectServer() {		//サーバとの接続
        //this.activity = activity;
        //final String ip = ipaddress;
        //final CallBackTask cbt = callbacktask;
        //final Listener ln = listener;
        //final int portnum = port;

        new AsyncTask<Void, Void, String>(){

            private int flag;
            String s;
            @Override
            protected String doInBackground(Void...voids){
                try {
                    soc = new Socket(ipaddress, port);
                    System.out.println(s = "サーバと接続できました");
                    return s;
                }catch(UnknownHostException e) {
                    System.out.println(s = "ホストに接続できません。");
                    System.out.println(e);
                    //flag = 1;

                }catch(IOException e) {
                    System.out.println(s = "サーバー接続時にエラーが発生しました。");
                    System.out.println(e);
                    flag = 2;
                }
                return s;//f = 1
            }

            @Override
            protected void onPostExecute(String s){
                super.onPostExecute(s);

                //コールバックする
                callbacktask.CallBack(s);

            }
        }.execute();
    }

    public boolean loginRequest(final String userName, final String password) { //ログイン要求

        new AsyncTask<Void, Void, String>() {
            //int flag;
            @Override
            protected String doInBackground(Void... voids) {
                //User u;
                try{
                    in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                    out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
                    //写真
                    bos = new BufferedOutputStream(new BufferedOutputStream(soc.getOutputStream()));
                    out.println("認証");
                    out.println(userName);
                    out.println(password);
                    out.flush();
                    String isPer = in.readLine();
                    System.out.println(isPer);
                    if (isPer.contains("ltrue") == true) {  //ログイン認証された
                        int playerNo = Integer.parseInt(in.readLine());
                        System.out.println(playerNo+"番です");
                        out.write("ダミー\n");
                        out.flush();
                        ois = new ObjectInputStream(soc.getInputStream());		//新しいObjectInputStreamを開ける
                        //User dammy = (User) ois.readObject();		//新しいOISを開けるためのdammyの取得
                        //System.out.println("ダミー受信成功");
                        return "ログイン成功";//flag = 0;
                    } else if (isPer.equals("lfalse") == true) {  //ログイン認証されなかった
                        //f = false;
                        //flag = 1;
                        //out.close();
                        //in.close();
                        return "ログイン失敗";//flag = 1;
                    } else {
                        System.out.println("認証とは別の文字列です。");  //別の文字列が送られてきた
                        //f = false;
                        //flag = 2;
                        //out.close();
                        //in.close();
                        return "ログイン失敗";//flag = 2
                    }
                    //ストリームをクローズする

                } catch(IOException e){
                    System.out.println(e);
                } /*catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }*/
                //f = false;//あとで直す
                //return "ログイン失敗";//あとで直す
                return "ログイン失敗";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                callbacktask.CallBack(s);
            }
        }.execute();
        return f;
    }

    public void accountRequest(final String userName,final String password, final String ai) { //アカウント作成要求
        //this.activity = activity;
        //final Activity act = activity;
        //final  CallBackTask cbt = callbacktask;

        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {
                try {
                    out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
                    in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                    //写真
                    bos = new BufferedOutputStream(new BufferedOutputStream(soc.getOutputStream()));
                    out.println("新規登録");
                    out.println(userName);
                    out.println(password);
                    out.println(ai);
                    out.flush();
                    String isPer = in.readLine();
                    System.out.println(isPer);
                    if (isPer.equals("rtrue")) {  //新規作成できる
                        int playerNo = Integer.parseInt(in.readLine());
                        System.out.println(playerNo+"番です");
                        out.write("ダミー\n");
                        out.flush();
                        ois = new ObjectInputStream(soc.getInputStream());		//新しいObjectInputStreamを開ける
                        System.out.println("a");
                        //dammy = (User) ois.readObject();		//新しいOISを開けるためのdammyの取得
                        System.out.println("ダミー受信成功");
                        return "登録成功";
                    } else if (isPer.equals("rfalse") == true) {  //新規作成できない
                        return "登録失敗";
                    } else {
                        System.out.println("確認とは別の文字列です。");  //別の文字列が送られてきた
                    }
                }catch(IOException e){
                    System.out.println(e);
                    e.printStackTrace();
                } /*catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }*/
                return "登録失敗";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                callbacktask.CallBack(s);
                //Intent intent = new Intent(act.getApplication(), RegistUserActivity.class);
                //startActivity(intent);

            }

        }.execute(userName, password, ai);

    }

    public void remindPassword(final String name,final String ai) {		//パスワードを忘れた場合の要求
        //this.activity = activity;
        //final Activity act = activity;
        //String password;
        //final CallBackTask cbt = callbacktask;
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void...voids) {
                try {
                    out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
                    in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                    out.println("忘れた人");
                    out.println(name);
                    out.println(ai);
                    out.flush();
                    String isPer = in.readLine();
                    if(isPer.equals("ftrue")==true) {
                        String password = in.readLine();
                        return password;
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
                super.onPostExecute(s);
                callbacktask.CallBack(s);
            }
        }.execute();

    }

    public void logoutRequest() {		//ログアウト
        //this.activity = activity;
        //final Activity act = activity;
        //final CallBackTask cbt = callbacktask;
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
                return "ログアウト失敗";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                callbacktask.CallBack(s);
            }

        }.execute();


    }

    public void sendUserInformation(final String job, final String belong, final ArrayList<String> group){		//アカウント情報転送

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void...voids) {

                out.println("個人情報");
                out.println(job);
                out.println(belong);
                out.println(group.size());
                out.flush();
                for(String s: group) {
                    out.println(s);
                    out.flush();
                }
                System.out.println("アカウント情報送信成功");
                return "設定完了";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                callbacktask.CallBack(s);
            }

        }.execute();

    }

    public void requestGroupSearch(final String group) {		//グループ検索要求

        //final Listener ln = listener;//必要
        final CallBackTask cbt = callbacktask;

        new AsyncTask<Void, Void, String>() {
            int flag;
            @Override
            protected String doInBackground(Void...voids) {
                try {
                    //out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
                    out.println("グループ検索");
                    out.println(group);
                    out.flush();
                    //in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                    String readL = in.readLine();
                    System.out.println(readL);
                    if(readL.contains("gtrue")) return "グループが見つかりました";		//該当するグループがあれば
                    else if(readL.contains("gfalse")) return "グループは見つかりませんでした";		//該当するグループがなければ
                    else {												//予期せぬ文字列が送られたら
                        System.out.println("違う文字列が送られました。");
                        return "グループ検索失敗";
                    }
                } catch (IOException e) {
                    // TODO 自動生成された catch ブロック
                    e.printStackTrace();
                }
                return "グループ検索失敗";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                callbacktask.CallBack(s);
                /*if(ln != null) {
                    ln.onSuccess(flag);
                }*/
            }

        }.execute();

    }

    public void receiveUserInformation(final String name) {		//自分のアカウント情報を取得

        //final String userN = name;
        //final Listener ln = listener;


        new AsyncTask<Void, Void, String>() {
            int flag;
            //User u;
            @Override
            protected String doInBackground(Void...voids) {
                try {
                    //out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
                    out.println("情報");
                    out.println(name);
                    out.flush();
                    //String s;
                    //System.out.println(s = in.readLine());
                    //if(s.equals(name)) {}
                    //ois = new ObjectInputStream(soc.getInputStream());
                    out.println("準備完了");
                    out.flush();
                    myuser = (User) ois.readObject();		//サーバから受け取った自分のUserオブジェクト
                    System.out.println("オブジェクト取得成功");
                    flag = 0;
                    return "取得成功";
                } catch (IOException e) {
                    // TODO 自動生成された catch ブロック
                    e.printStackTrace();
                    System.out.println("オブジェクト取得失敗");
                } catch (ClassNotFoundException e) {
                    // TODO 自動生成された catch ブロック
                    e.printStackTrace();
                    System.out.println("オブジェクト取得失敗");
                }
                flag = 1;
                return "取得失敗";

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //リスナーで結果をメインスレッドに反映させる
                if(listener != null) {
                    listener.onSuccess(flag);
                }
            }

        }.execute();

    }

    public void creatGroup(final String group/*グループ名*/, final String explanation/*グループの説明*/) {		//グループ新規作成

        f = false;
        final CallBackTask cbt = callbacktask;

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void...voids) {
                try {
                    //out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
                    out.println("グループ作成");
                    out.println(group);
                    out.println(explanation);
                    out.flush();
                    in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                    String s = in.readLine();
                    System.out.println(s);
                    if(s.indexOf("y")==0) {						//yが混入した場合のy除去処理
                        StringBuffer sb = new StringBuffer(s);
                        sb.deleteCharAt(0);
                        s = sb.toString();
                    }
                    System.out.println(s);
                    if(s.equals("gtrue")==true) {
                        System.out.println("グループ作成成功");
                        return "作成成功";
                    }

                    else if(in.readLine().equals("gfalse")==true) {
                        System.out.println("グループ作成失敗");
                        return "作成失敗";
                    } else {												//予期せぬ文字列が送られたら
                        System.out.println("違う文字列が送られました。");
                        return "作成失敗";
                    }
                } catch (IOException e) {
                    // TODO 自動生成された catch ブロック
                    e.printStackTrace();
                }
                System.out.println("接続失敗");
                return "作成失敗";

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                cbt.CallBack(s);
            }


        }.execute();

    }

    public void receiveGroupInformation(final String group) {		//グループ内情報を取得		//メソッドの名前変更

        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void...voids) {
                try {
                    //ois.reset();
                    //out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
                    out.println("入室中のグループ情報");
                    out.println(group);
                    out.flush();
                    //soc.getInputStream().skip(100);
                    //ois = new ObjectInputStream(soc.getInputStream());
                    out.println("準備完了");
                    out.flush();
                    mygroup = (Group) ois.readObject();
                    System.out.println("取得グループ名:" + mygroup.getgname());
                    System.out.println("グループメンバー:" + mygroup.getmember().get(0));
                    System.out.println(mygroup.getchat().size());
                    for(int i = 0 ; i < mygroup.getchat().size(); i++) {
                        System.out.println("質問:" + mygroup.getchat().get(i));
                    }
                    System.out.println("グループ取得成功");
                    return "取得成功";
                } catch (IOException e) {
                    // TODO 自動生成された catch ブロック
                    e.printStackTrace();
                    System.out.println("グループ取得失敗");
                    return "取得失敗";
                } catch (ClassNotFoundException e) {
                    // TODO 自動生成された catch ブロック
                    e.printStackTrace();
                    System.out.println("グループ取得失敗");
                    return "取得失敗";
                }

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                callbacktask.CallBack(s);
            }

        }.execute();
    }

    /*public Group receiveQuestions(String group, Activity activity) {		//グループ内情報を取得
        this.activity = activity;
        final Activity act = activity;
        //Group g = null;
        final CallBackTask cbt = callbacktask;
        final String gr = group;
        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... strings) {
                /*try {
                    out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
                    out.println("入室中のグループ情報");
                    out.println(gr);
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
                return "取得失敗";//あとで直す
                return "取得成功";
            }

            @Override
            protected void onPostExecute(String s) {
                cbt.CallBack(s);
            }

        }.execute(group);

        return g;
    }*/

    public void sendQuestion(final String question/*質問内容*/, final String group/*質問の属するグループ*/,  final String answerer/*オファーを出す相手*/, final int coin/*質問にかけるコイン*/, final boolean isImage, final byte[] ibyte) {		//質問の送信

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void...voids) {
                try {
                    if (coin < 100) {
                        String s = "かけるコインの下限は１００コインです";
                        System.out.println(s);
                        return s;
                    } else {
                        //out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
                        out.println("質問内容");
                        out.println(group);                    //質問の属するグループ名の送信
                        out.println(coin);                    //質問にかけられたコイン数の送信
                        out.println(question);                //質問内容の送信
                        out.println("finish");              //文字列終了のお知らせ
                        out.flush();

                        in.readLine();//読み込み終了

                        if(!isImage) {//写真がないならば
                            out.println("なし");
                            out.flush();
                        } else {//写真があるならば
                            out.println("あり");
                            out.println(ibyte.length);
                            out.flush();
                            System.out.println(in.readLine());
                            bos.write(ibyte, 0, ibyte.length);
                            bos.flush();
                            System.out.println(in.readLine());


                        }

                        System.out.println("送信");
                    }

                    if (answerer != null) {
                        String questr = question + "\n";
                        sendOffer(questr, answerer);
                    }

                    return "送信成功";
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return "送信失敗";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                callbacktask.CallBack(s);//連絡の必要はなし?
            }

        }.execute();

    }

    public void sendOffer(final String question/*質問内容*/, final String answerer/*オファーを出す相手*/) {		//オファーの送信

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void...voids) {
                out.println("オファー人選出");
                //out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
                out.println(answerer);		//どの質問に対するオファーかを識別するためのキー（質問内容）の送信
                out.println(question);
                out.println("finish");              //文字列終了のお知らせ
                out.flush();
                try {
                    in.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return "オファー成功";

            }

            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                callbacktask.CallBack(s);
            }

        }.execute();

    }

    public void receiveMyQuestion() {		//自分がした質問の情報の取得。回答、立候補者等の情報はここから取り出す。		//メソッドの名前変更
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void...voids) {
                try {
                    //out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
                    out.println("自分がした質問");
                    //out.println(name);		//誰の質問かを判別するためのキー（自分の名前）の送信
                    out.flush();
                    in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                    String s1 = in.readLine();
                    System.out.println(s1);
                    if(s1.indexOf("y")==0) {						//yが混入した場合のy除去処理
                        StringBuffer sb = new StringBuffer(s1);
                        sb.deleteCharAt(0);
                        s1 = sb.toString();
                    }
                    System.out.println(s1);
                    int num = Integer.parseInt(s1);
                    //in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                    //String s2 = in.readLine();
                    //System.out.println(s2+"　2回目");
                    out.println("準備完了");
                    out.flush();
                    //ois = new ObjectInputStream(soc.getInputStream());
                    myquestion = new ArrayList<>();
                    for(int i=0; i<num; i++) {
                        //ois = new ObjectInputStream(soc.getInputStream());
                        myquestion.add((Question) ois.readObject());
                    }
                    System.out.println("自分がした質問の受け取り成功");
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
                super.onPostExecute(s);
                callbacktask.CallBack(s);
            }

        }.execute();
    }



    /*public ArrayList<Question> receiveAnswer(Activity activity) {		//自分がした質問の情報の取得。回答、立候補者等の情報はここから取り出す。
        this.activity = activity;
        final Activity act = activity;
        final ArrayList<Question> myquestion = new ArrayList<Question>();
        final CallBackTask cbt = callbacktask;
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {
                /*try {
                    out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
                    out.println("自分がした質問");
                    out.flush();
                    ois = new ObjectInputStream(soc.getInputStream());
                    while(true) {
                        //myquestion.add((Question) ois.readObject());
                        mq.add((Question) ois.readObject());//変更点
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
                return "取得成功";
            }

            @Override
            protected void onPostExecute(String s) {
                cbt.CallBack(s);
            }

        }.execute();

        return myquestion;
    }*/

    public void receiveOffer() {		//自分に来ているオファーの受信（具体的には、Questionクラスの属性offeredに自分の名前がセットされているQuestionオブジェクトの受信）

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void...voids) {
                try {
                    //out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
                    out.println("オファー来てるかな");
                    out.flush();
                    //in = new BufferedReader(new InputStreamReader(soc.getInputStream()));


                    String s = in.readLine();
                    System.out.println(s);
                    int num = Integer.parseInt(s);

                    //ois = new ObjectInputStream(soc.getInputStream());
                    //out.write(s);

                    //ois = new ObjectInputStream(soc.getInputStream());
                    out.println("準備完了");
                    out.flush();
                    myoffer = new ArrayList<>();
                    for(int i=0; i<num; i++) {
                        //ObjectInputStream ois = new ObjectInputStream(soc.getInputStream());
                        myoffer.add((Question)ois.readObject());
                    }
                    System.out.println("自分に来ているオファーの受け取り成功");
                    return "取得成功";
                } catch (IOException e) {
                    // TODO 自動生成された catch ブロック
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    // TODO 自動生成された catch ブロック

                }
                return "取得失敗";
            }

            @Override
            protected void onPostExecute(String s) {
                callbacktask.CallBack(s);//コールバック
            }

        }.execute();

    }

    public void receiveCandidate(){		//自分が立候補した質問の受信

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void...voids) {
                try {
                    //out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
                    out.println("立候補してる質問");
                    out.flush();
                    //in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                    String s = in.readLine();
                    System.out.println(s);

                    int num = Integer.parseInt(s);
                    //ois = new ObjectInputStream(soc.getInputStream());
                    out.println("準備完了");
                    out.flush();
                        mycandidacy = new ArrayList<>();
                    for(int i=0; i<num; i++) {
                        //ObjectInputStream ois = new ObjectInputStream(soc.getInputStream());
                        mycandidacy.add((Question)ois.readObject());
                    }
                    System.out.println("自分が立候補した質問の受け取り成功");
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
                callbacktask.CallBack(s);
            }

        }.execute();

    }

    public void sendAnswer(final String question/*質問内容*/,final String answer/*回答内容*/) {		//回答の送信

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void...voids) {
                try {
                    out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
                    out.println("回答");
                    out.println(answer);
                    out.println("finish");
                    out.flush();
                    in.readLine();
                    out.println(question);
                    out.println("finish");
                    out.flush();
                    in.readLine();

                    //in.readLine();
                    return "送信成功";
                } catch (IOException e) {
                    // TODO 自動生成された catch ブロック
                    e.printStackTrace();
                }
                return "送信失敗";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                callbacktask.CallBack(s);
            }

        }.execute();

    }

    public void Candidacy(final String question/*質問内容*/) {		//立候補

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void...voids) {
                try {
                    out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
                    out.println("回答立候補");
                    out.println(question);
                    out.println("finish");//立候補する質問の内容の送信
                    out.flush();
                    in.readLine();
                    return "立候補成功";
                } catch (IOException e) {
                    // TODO 自動生成された catch ブロック
                    e.printStackTrace();
                }
                return "立候補失敗";

            }

            @Override
            protected void onPostExecute(String s) {
                callbacktask.CallBack(s);
            }

        }.execute();

    }

    public void cancelCandidacy(final String question) {		//自分がした立候補の取り消し

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void...voids) {
                try {
                    out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
                    out.println("立候補取り消し");
                    out.println(question);		//立候補を取り消す質問の内容の送信
                    out.println("finish");
                    out.flush();
                    in.readLine();
                    return "取り消しました";
                } catch (IOException e) {
                    // TODO 自動生成された catch ブロック
                    e.printStackTrace();
                }
                return "取り消しに失敗しました";
            }

            @Override
            protected void onPostExecute(String s) {
                callbacktask.CallBack(s);
            }

        }.execute();

    }

    public void sendValue(final String question/*質問内容*/, final double value/*評価値*/) {		//評価値の送信

        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {
                try {
                    out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
                    out.println("評価値変更");
                    out.println(value);
                    out.println(question);
                    out.println("finish");


                    out.flush();
                    in.readLine();
                    return "評価が送信できました";
                } catch (IOException e) {
                    // TODO 自動生成された catch ブロック
                    e.printStackTrace();
                }
                return "評価の送信に失敗しました";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }

        }.execute();

    }

    public void cancelOffer(final String question/*質問内容*/) {		//オファー取り消し


        final CallBackTask cbt = callbacktask;
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void...voids) {
                try {
                    out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
                    out.println("オファー取り消し");
                    out.println(question);
                    out.println("finish");
                    out.flush();
                    in.readLine();

                    return "オファーを取り消しました";
                } catch (IOException e) {
                    // TODO 自動生成された catch ブロック
                    e.printStackTrace();
                }
                return "オファーの取り消しに失敗しました";
            }

            @Override
            protected void onPostExecute(String s) {
                callbacktask.CallBack(s);
            }

        }.execute();

    }

    public void refuseOffer(final String question/*質問内容*/) {		//オファー拒否

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void...voids) {
                try {
                    out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
                    out.println("オファー拒否");
                    out.println(question);
                    out.println("finish");
                    out.flush();
                    in.readLine();
                    return "オファーを拒否できました";
                } catch (IOException e) {
                    // TODO 自動生成された catch ブロック
                    e.printStackTrace();
                }
                return "オファーを拒否できませんでした";
            }

            @Override
            protected void onPostExecute(String s) {
                callbacktask.CallBack(s);
            }

        }.execute();

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

    public void sendCoin(final int coin) {		//送金

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void...voids) {
                try {
                    out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
                    out.println("コイン購入");
                    out.println(coin);
                    out.flush();
                    return coin + "コインを送金しました";
                } catch (IOException e) {
                    // TODO 自動生成された catch ブロック
                    e.printStackTrace();
                }
                return "送金に失敗しました";
            }

            @Override
            protected void onPostExecute(String s) {
                callbacktask.CallBack(s);;
            }

        }.execute();

    }

    public void receiveCoin() {		//着金


        new AsyncTask<Void, Void, String>() {
            int c=0;
            @Override
            protected String doInBackground(Void...voids) {
                try {
                    out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
                    out.println("着金");
                    out.flush();
                    in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                    c = Integer.parseInt(in.readLine());
                    return String.valueOf(c) + "コインを受け取りました";
                } catch (IOException e) {
                    // TODO 自動生成された catch ブロック
                    e.printStackTrace();
                }
                return "コインの受け取りに失敗しました";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                callbacktask.CallBack(s);
            }

        }.execute();


    }

        //変更点
    public void sendImage() {

        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void...voids) {
                try {
                    out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
                    in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                    out.println("写真送信");
                    out.flush();
                    in.readLine();




                    String imageS = new String(imagebyte);
                    System.out.println(imageS.length());
                    System.out.println(imagebyte.length);
                    System.out.println(imageS);

                    //bos.write(imagebyte, 0, imagebyte.length);
                    //bos.flush();
                    out.print(imageS);
                    out.flush();
                    in.readLine();
                    BufferedInputStream bis = new BufferedInputStream(soc.getInputStream());
                    imagebyte2 = new byte[bis.available()];
                    bis.read(imagebyte2, 0, bis.available());


                    return "受信成功";
                } catch (IOException e) {
                    // TODO 自動生成された catch ブロック
                    e.printStackTrace();
                }
                return "コインの受け取りに失敗しました";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                callbacktask.CallBack(s);
            }

        }.execute();

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

    /*public Group getGroup() {
        return g;
    }*/

    /*public String getI() {
        return strai;
    }*/

    /*public ArrayList<Question> getMyQuestion() {
        return mq;
    }*/

    public static void main(String[] args) {
        // TODO 自動生成されたメソッド・スタブ

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        logoutRequest();
    }
    //自分が今見ている質問
    public Question getWatchingQ() {
        return watchingQ;
    }

    public void setWatchingQ(Question q) {
        watchingQ = q;
    }

    //変更点
    public void setbyte(byte[] image) {
        imagebyte = image;
    }

    public byte[] getbyte() {
        return imagebyte2;
    }




}