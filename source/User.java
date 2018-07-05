package pro2;

import java.io.Serializable;
import java.util.ArrayList;



public class User implements Serializable {
	private String name;
	private String pass;
	private String ai;
	private String job;
	private String belong;
	private double value;
	private int question;
	private int answer;
	ArrayList group;



	User(String name,String pass,String ai,String job,String syozoku,double value,int question,int answer,ArrayList group){
		this.name=name;
		this.pass=pass;
		this.ai=ai;
		this.job=job;
		this.belong=belong;
		this.value=value;
		this.question=question;
		this.answer=answer;
		this.group=group;
		
		
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



	public void setQuestion(int n){

		question = n;

	}



	public int getQuestion(){

		return question;

	}



	public void setAnswer(int n){

		answer = n;

	}



	public int getAnswer(){

		return answer;

	}






	public ArrayList getGroup(){

		return group;

	}



}