package pro2_Driver;

import pro2.Question;
import pro2.User;

public class QuestionDriver {

	public static void main(String[] args) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		User u1 = new User("u1", "1", "�����񂵂�");
		System.out.println("(u1, ����P, �O���[�v�P, 100)��Question�N���X�̃I�u�W�F�N�g����");
		Question q1 = new Question(u1, "����P", "�O���[�v�P", 100);
		System.out.println("getQuestioner�o��: "+q1.getQuestioner().getName());
		System.out.println("getQuestion�o��: "+q1.getQuestion());
		System.out.println("getGroup�o��: "+q1.getGroup());
		System.out.println("getCoin�o��: "+q1.getCoin());

		User u2 = new User("u2", "2", "��������ق���");
		User u3 = new User("u3", "3", "��������ق���");
		System.out.println("u2,u3��setCandidates�ŗ����҂ɒǉ�");
		q1.setCandidates(u2);
		q1.setCandidates(u3);
		for(User u: q1.getCandidates()) {
			System.out.println("getCandidates�o��: "+u.getName());
		}

		System.out.println("u2��setOffer�ŃI�t�@�[");
		q1.setOffer(u2);
		System.out.println("getOffer�o��: "+q1.getOffer().getName());

		System.out.println("u2��canselOffer�ŃI�t�@�[����");
		q1.canselOffer();
		System.out.println("checkOffered�o��: "+q1.checkOffered());

		System.out.println("u3��delCandidate�ŗ����L�����Z��");
		q1.delCandidate(u3);
		for(User u: q1.getCandidates()) {
			System.out.println("getCandidates�o��: "+u.getName());
		}

		System.out.println("�Ă�u2�ɃI�t�@�[");
		System.out.println("u2����");
		q1.setAnswer("�񓚂P");
		q1.setAnswerer(u2);
		System.out.println("getAnswerer�o��: "+q1.getAnswerer().getName());
		System.out.println("getAnswer�o��: "+q1.getAnswer());

		System.out.println("�]���l4.5��setValue�ŕ]��");
		q1.setValue(4.5);
		System.out.println("getValue�o��: "+q1.getValue());
	}

}
