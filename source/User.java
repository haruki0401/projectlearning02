public class User {
	private String name;
	private String password;
	private String address;
	private double value=0;
	private int question=0, answer=0;
	private String job;
	private String belong;
	private String group;
	private int coin=0;

	public User(String name, String password){
		this.name = name;
		this.password = password;
	}

	public String getName(){
		return name;
	}

	public String getPassword(){
		return password;
	}

	public void setAddress(String s){
		address = s;
	}

	public String getAddress(){
		return address;
	}

	public void setValue(double v){
		value = v;
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

	public void setGroup(String s){
		group = s;
	}

	public String getGroup(){
		return group;
	}

	public void setCoin(int n){
		coin = n;
	}

	public void renewCoin(int n) {
		coin += n;
	}

	public int getCoin(){
		return coin;
	}
}