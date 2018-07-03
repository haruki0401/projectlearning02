
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
	private Socket soc;
	private PrintWriter out;
	private BufferedReader in;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private User myuser;

	public void sendUserInformation(String job, String belong, String group){		//�A�J�E���g���]��
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println(job);
			out.println(belong);
			out.println(group);
			out.flush();
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
	}

	public void receiveUserInformation() {		//�����̃A�J�E���g�����擾
		User u = null;
		try {
			ois = new ObjectInputStream(soc.getInputStream());
			u = (User) ois.readObject();
			myuser = u;
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
	}

	public void creatGroup(String group, String explanation) {		//�O���[�v�V�K�쐬
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println(group);
			out.println(explanation);
			out.flush();
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
	}

	public User[] receiveOtherUserInformation() {		//�����[�U�̏����擾
		User[] u = null;
		int member = 0;
		try {
			in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			member = Integer.parseInt(in.readLine());
			ois = new ObjectInputStream(soc.getInputStream());
			for(int i=0; i<member; i++) {
				u[i] = (User) ois.readObject();
			}
		} catch (IOException | ClassNotFoundException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
		return u;
	}

	public Question[] receiveQuestions(String group) {		//�O���[�v���̎�����擾
		Question q[] = null;
		int number = 0;
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println(group);
			out.flush();
			in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			number = Integer.parseInt(in.readLine());
			ois = new ObjectInputStream(soc.getInputStream());
			for(int i=0; i<number; i++) {
				q[i] = (Question) ois.readObject();
			}
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
		return q;
	}

	public void sendQuestion(String question/*������e*/, String answerer/*�I�t�@�[���o������*/, int coin) {		//����̑��M
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println(myuser.getName());
			out.println(myuser.getGroup());
			out.println(question);
			if(answerer.equals(null)==false)sendOffer(answerer);
			out.println(coin);
			out.flush();
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
	}

	public void sendOffer(String answerer/*�I�t�@�[���o������*/) {		//�I�t�@�[�̑��M
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println(answerer);
			out.flush();
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
	}

	public Question receiveAnswer() {		//����������������̎擾�B�񓚁A�����ғ��̏��͂���������o���B
		Question q = null;
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println(myuser.getName());
			out.flush();
			ois = new ObjectInputStream(soc.getInputStream());
			q = (Question) ois.readObject();
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
		return q;
	}

	public Question[] receiveOffer() {		//�����ɗ��Ă���I�t�@�[�̎�M
		Question[] q = null;
		int number = 0;
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println(myuser.getName());
			out.flush();
			in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			number = Integer.parseInt(in.readLine());
			ois = new ObjectInputStream(soc.getInputStream());
			for(int i=0; i<number; i++) {
				q[i] = (Question) ois.readObject();
			}
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
		return q;
	}

	public void sendAnswer(String question/*������e*/, String answer/*�񓚓��e*/) {		//�񓚂̑��M
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println(question);
			out.println(answer);
			out.flush();
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
	}

	public void Candidacy(String question/*������e*/) {		//�����
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println(question);
			out.println(myuser.getName());
			out.flush();
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
	}

	public void sendValue(String question/*������e*/, double value/*�]���l*/) {		//�]���l�̑��M
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println(question);
			out.println(value);
			out.flush();
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
	}

	public void cancelOffer(String question/*������e*/) {		//�I�t�@�[������
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println(question);
			out.flush();
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
	}

	public void refuseOffer(String question/*������e*/) {		//�I�t�@�[����
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println(question);
			out.flush();
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
	}

	public String receiveMessage() {		//���b�Z�[�W��M
		String mes = null;
		try {
			in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			mes = in.readLine();
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
		return mes;
	}

	public void sendCoin(int coin) {		//����
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println(coin);
			out.flush();
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
	}

	public int receiveCoin() {		//����
		int c = 0;
		try {
			in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			c = Integer.parseInt(in.readLine());
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
		return c;
	}

	public User getMyUser() {
		return myuser;
	}

	public static void main(String[] args) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u

	}

}
