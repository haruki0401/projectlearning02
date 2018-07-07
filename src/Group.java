package pro2;

import java.io.Serializable;
import java.util.ArrayList;

public class Group implements Serializable{
		String gname;//グループ名
		static ArrayList<Question> chat = new ArrayList<Question>();
		static ArrayList<User> member = new ArrayList<User>();
		String intro=""; 
		
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
		
		public ArrayList getchat() {
			
			return chat; 
		}
		
		public void setmember(User user) {
			member.add(user);
		}
		public void delmember(User user) {
			member.remove(user);
		}
		public ArrayList getmember() {
			return member;
		}
		public String getintro() {
			return intro;
		}
		
		




}
