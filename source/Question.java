import java.util.ArrayList;

public class Question {
	private User questioner;		//�����
	private User answerer;		//�񓚎�
	private User offered;			//�I�t�@�[���ꂽ�l
	private String question;		//������e
	private String answer;			//�񓚓��e
	private String group;			//����҂̑�����O���[�v
	private ArrayList<User> candidates;		//�����҂̃��X�g
	private boolean answered = false;		//�񓚂��ꂽ���ǂ���
	private double value=0;				//�񓚂̕]���l
	private int coin;				//����ɂ�����ꂽ�R�C��

	public Question(User questioner, String question, String group) {
		this.questioner = questioner;
		this.question = question;
		this.group = group;
	}

	public void setCandidates(User candidate) {
		candidates.add(candidate);
	}

	public void setOffered(User s) {
		offered = s;
	}

	public void setAnswer(User user, String ans) {
		answerer = user;
		answer = ans;
		answered = true;
	}

	public void setValue(double v) {
		value = v;
	}

	public void renewValue(double newv) {
		value = (value + newv) / 2;
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

	public User getOffered() {
		return offered;
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
