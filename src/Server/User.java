package pro2;

import java.io.Serializable;
import java.util.ArrayList;



public class User implements Serializable {
	private String name;
	private String pass;
	private String ai;
	private String job="";
	private String belong="";
	private double value=0;
	private int question=0;
	private int answer=0;
	ArrayList<String> group=new ArrayList<String>();
	private int coin=0;




	User(String name,String pass,String ai){
		this.name=name;
		this.pass=pass;
		this.ai=ai;


	}



	public String getName(){

		return name;

	}



	public String getPassword(){

		return pass;

	}

	public String getAikotoba() {
		return ai;
	}


	public void setJob(String s){

		job = s;

	}



	public String getJob(){

		return job;

	}

	public void setBelong(String s){

		belong = s;

	}



	public String getBelong(){

		return belong;

	}



	public double getValue(){

		return value;

	}
	public void setValue(double u) {
		value=u;
	}



	public void setQuestion(){

		question++;

	}



	public int getQuestion(){

		return question;

	}



	public void setAnswer(){

		answer++;

	}



	public int getAnswer(){

		return answer;

	}
	public void decAnswer(int a) {
		answer=a;
	}
	public void decQuestion(int q) {
		question=q;
	
}





	public ArrayList<String> getGroup(){

		return group;

	}
	public void clearGroup() {
		group.clear();
	}
	public void setGroup(String a) {
		group.add(a);
	}
	public void setCoin(int a) {
		coin=a;
	}
	public int getCoin() {
		return coin;
	}
	public void minusCoin(int a) {
		coin=coin-a;
	}
	public void plusCoin(int a) {
		coin=coin+a;
	}
	
	
	
	
	




}