package pro2;

public class UserDriver {

	public static void main(String[] args) {

		System.out.println("name:yamada,pass:1234,aikotoba:hello　でユーザを作成します。");

		User user=new User("yamada","1234","hello");


		System.out.println("name: "+user.getName());
		System.out.println("pass "+user.getPassword());
		System.out.println("aikotoba: "+user.getAikotoba());
		System.out.println("name: "+user.getName());

		System.out.println("jobをstudentにセットします。");
		user.setJob("student");
		System.out.println("job: "+user.getJob());

		System.out.println("belongをynuにセットします。");
		user.setBelong("ynu");
		System.out.println("belong: "+user.getBelong());

		System.out.println("valueを3.5にセットします。");
		user.setValue(3.5);
		System.out.println("value: "+user.getValue());

		System.out.println("questionをセットします。");
		user.setQuestion();
		System.out.println("questionNum: "+user.getQuestion());

		System.out.println("answerをセットします。");
		user.setAnswer();
		System.out.println("answerNum: "+user.getAnswer());

		System.out.println("question数を0にセットします。");
		user.decQuestion(0);
		System.out.println("questionNum: "+user.getQuestion());

		System.out.println("answer数を0にセットします。");
		user.decAnswer(0);
		System.out.println("answerNum: "+user.getAnswer());


		System.out.println("gruopにsoccerをセットします。");
		user.setGroup("soccer");
		System.out.println("gruop: "+user.getGroup());


		System.out.println("coinを100にセットします。");
		user.setCoin(100);
		System.out.println("coin: "+user.getCoin());

		System.out.println("coinを100+します。");
		user.plusCoin(100);
		System.out.println("coin: "+user.getCoin());

		System.out.println("coinを100-します。");
		user.minusCoin(100);
		System.out.println("coin: "+user.getCoin());
	}

}
