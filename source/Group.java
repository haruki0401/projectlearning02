package pro2;

import java.util.ArrayList;

public class Group {
		String gname;//グループ名
		static ArrayList<String> chat = new ArrayList<String>();
		static ArrayList<String> member = new ArrayList<String>();
		
		Group(String gname){
			this.gname=gname;
			
		}

		public String getgname() {
			return gname;
		}
		
		
		public void setchat(String bun) { 
			chat.add(bun);//チャット情報を保持するgroupに格納
			
			if(chat.size()>100) {
				chat.remove(0);
				
			}
		}
		
		public ArrayList getchat() {
			
			return chat; 
		}
		
		public void setmember(String name) {
			member.add(name);
		}
		public void delmember(String name) {
			member.remove(name);
		}
		public ArrayList getmember() {
			return member;
		}
		
		




}
