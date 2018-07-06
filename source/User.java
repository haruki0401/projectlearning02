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




	User(String name,String pass,String ai){
		this.name=name;
		this.pass=pass;
		this.ai=ai;
		group.add("all");


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






	public ArrayList<String> getGroup(){

		return group;

	}
	public void clearGroup() {
		group.clear();
	}
	public void setGroup(String a) {
		group.add(a);
	}



}