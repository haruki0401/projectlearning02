package pro2_Driver;

import pro2.Question;
import pro2.User;

public class QuestionDriver {

	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ
		User u1 = new User("u1", "1", "しつもんしゃ");
		System.out.println("(u1, 質問１, グループ１, 100)でQuestionクラスのオブジェクト生成");
		Question q1 = new Question(u1, "質問１", "グループ１", 100);
		System.out.println("getQuestioner出力: "+q1.getQuestioner().getName());
		System.out.println("getQuestion出力: "+q1.getQuestion());
		System.out.println("getGroup出力: "+q1.getGroup());
		System.out.println("getCoin出力: "+q1.getCoin());

		User u2 = new User("u2", "2", "りっこうほしゃ");
		User u3 = new User("u3", "3", "りっこうほしゃ");
		System.out.println("u2,u3をsetCandidatesで立候補者に追加");
		q1.setCandidates(u2);
		q1.setCandidates(u3);
		for(User u: q1.getCandidates()) {
			System.out.println("getCandidates出力: "+u.getName());
		}

		System.out.println("u2をsetOfferでオファー");
		q1.setOffer(u2);
		System.out.println("getOffer出力: "+q1.getOffer().getName());

		System.out.println("u2がcanselOfferでオファー拒否");
		q1.canselOffer();
		System.out.println("checkOffered出力: "+q1.checkOffered());

		System.out.println("u3がdelCandidateで立候補キャンセル");
		q1.delCandidate(u3);
		for(User u: q1.getCandidates()) {
			System.out.println("getCandidates出力: "+u.getName());
		}

		System.out.println("再びu2にオファー");
		System.out.println("u2が回答");
		q1.setAnswer("回答１");
		q1.setAnswerer(u2);
		System.out.println("getAnswerer出力: "+q1.getAnswerer().getName());
		System.out.println("getAnswer出力: "+q1.getAnswer());

		System.out.println("評価値4.5でsetValueで評価");
		q1.setValue(4.5);
		System.out.println("getValue出力: "+q1.getValue());
	}

}
