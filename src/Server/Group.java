package pro2;

import java.io.Serializable;
import java.util.ArrayList;

public class Group implements Serializable{
	private static final long serialVersionUID = 12L;
		private String gname;//グループ名
		private ArrayList<Question> chat = new ArrayList<Question>();
		private ArrayList<User> member = new ArrayList<User>();
		private String intro="";

		Group(String gname,String intro,User user){
			this.gname=gname;
			this.intro=intro;
			member.add(user);
		}

		public String getgname() {
			return gname;
		}


		public void setchat(Question bun) {
			chat.add(bun);//チャット情報を保持するgroupに格納

			if(chat.size()>100) {
				chat.remove(0);

			}
		}

		public ArrayList<Question> getchat() {

			return chat;
		}

		public void setmember(User user) {
			member.add(user);
		}
		public void delmember(User user) {
			User changeu=null;
			for(User u: member) {
				if(u.getName().equals(user.getName())) {
					changeu = u;
				}
			}
			member.remove(changeu);
		}
		public ArrayList<User> getmember() {
			return member;
		}
		public String getintro() {
			return intro;
		}






}
