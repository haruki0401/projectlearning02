package pro2;

import java.util.ArrayList;

public class Group_Driver {

	public static void main(String[] args) {
		String a1="an";
		String a2 ="ap";
		String a3="あっぷる";
		
		String b1="bn";
		String b2="bp";
		String b3="ぶどう";
		ArrayList<Question> qlist=new ArrayList<Question>();
		ArrayList<User> ulist=new ArrayList<User>();
		
		
		
		User ua=new User(a1,a2,a3);//ユーザーaさんの作成
		User ub=new User(b1,b2,b3);//ユーザーbさんの作成
		Group g=new Group("グループ名A","Aです",ua);//aさんによるグループの作成
		Question q=new Question(ua,"質問内容です","グループ名A",100);
		
		
		System.out.println("グループクラスの試験を開始する");
		
		System.out.println();
		
		System.out.println("getgname()でグループ名を取得");//getgnameによるグループ名の取得
		System.out.println(g.getgname());
		System.out.println();
		
		System.out.println("setchat()でグループの質問リストに追加します");
		g.setchat(q);
		System.out.println("getchat()でグループの質問リストを取得");
		qlist=g.getchat();
		System.out.println("取得した質問リストの質問内容の表示");
		for(Question l:qlist) {
		System.out.println(l.getQuestion());
		}
	
		System.out.println();
		
		System.out.println("setmember()によるグループメンバーの追加");//bさんをsetmember()でメンバーに追加
		g.setmember(ub);
		System.out.println("getmember()によるグループメンバーーの取得");//aさんとbさんが現時点でのメンバー
		ulist=g.getmember();
		System.out.println("取得したメンバーの名前の表示");
		for(User u:ulist) {
			System.out.println(u.getName());
		}
		
		
		System.out.println();
		
		System.out.println("delmember()によるグループメンバーの削除");
		g.delmember(ub);//ｂさんをグループから削除
		System.out.println("削除後のメンバーの表示");//aさんのみが現在のメンバー
		ulist=g.getmember();
		for(User u:ulist) {
			System.out.println(u.getName());
		}
		ulist.clear();
		
		System.out.println("getintro()によるグループの紹介文の取得・表示");//グループ作成時に設定した紹介文が出れば成功
		System.out.println(g.getintro());
		
		System.out.println();
	
	
	     System.out.println("グループクラスの試験を終了する");
	
	
	
	}

	
	
	
	
	
	
	
	
}
