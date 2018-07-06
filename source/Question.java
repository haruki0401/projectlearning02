package pro2;

import java.io.Serializable;
import java.util.ArrayList;


public class Question implements Serializable {

	
	private User questioner;		//質問者

	private User answerer;		//回答者

	private User offer;			//オファー人

	private String question;		//質問内容

	private String answer;			//回答内容

	private String group;			//質問の属するグループ

	private ArrayList<User> candidates= new ArrayList<User>();;		//立候補者のリスト

	private boolean answered = false;		//回答されたかどうか

	private double value=0;				//回答の評価値

	private int coin;				//質問にかけられたコイン
	
	




	public Question(User questioner, String question, String group,int coin) {//コインも追加

		this.questioner = questioner;

		this.question = question;

		this.group = group;
		
		this.coin=coin;

	}



	public void setCandidates(User candidate) {

		candidates.add(candidate);

	}
	



	public void setOffer(User s) {

		offer = s;
		

	}
	public void canselOffer() {
		offer=null;
	}
	public User getOffer() {
		return offer;
	}
	



	public void setAnswerer(User user) {//回答し終わった人のこと

		answerer = user;

		

	}
	public void setAnswer(String s) {
		answer=s;
		answered=true;
		
	}



	public void setValue(double v) {

		value = v;

	}




	public void setCoin(int c) {

		coin = c;

	}



	public User getQuestioner() {

		return questioner;

	}



	public String getQuestion() {

		return question;

	}



	public String getGroup() {

		return group;

	}



	public User getAnswerer() {

		return answerer;

	}



	public String getAnswer() {

		return answer;

	}



	public ArrayList<User> getCandidates(){

		return candidates;

	}
	
	public void clearCandidates() {
		candidates.clear();
	}



	public User getOffered() {

		return offer;

	}



	public double getValue() {

		return value;

	}



	public int getCoin() {

		return coin;

	}



	public boolean checkAnswered() {

		return answered;

	}





}