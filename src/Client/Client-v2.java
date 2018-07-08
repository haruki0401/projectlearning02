
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Client {
	private Socket soc;
	private PrintWriter out;
	private BufferedReader in;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private User myuser;

	public boolean connectServer(String ipAddress,int port) {		//�T�[�o�Ƃ̐ڑ�
        try {
            soc = new Socket(ipAddress,port);
            return true;
        }catch(UnknownHostException e) {
            System.out.println("�z�X�g�ɐڑ��ł��܂���B");
            System.out.println(e);
            return false;
        }catch(IOException e) {
            System.out.println("�T�[�o�[�ڑ����ɃG���[���������܂����B");
            System.out.println(e);
            return false;
        }
	}

	public boolean loginRequest(String userName,String password) { //���O�C���v��
        try {
            in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
            out.println(userName);
            out.println(password);
            out.flush();
            String isPer = in.readLine();
            if(isPer.equals("permit") == true) {  //���O�C���F�؂��ꂽ
                return true;
            } else if(isPer.equals("notPermit") == true) {  //���O�C���F�؂���Ȃ�����
                return false;
            } else {
                System.out.println("�F�؂Ƃ͕ʂ̕�����ł��B");  //�ʂ̕����񂪑����Ă���
            }
            //�X�g���[�����N���[�Y����
            //out.close();
            //in.close();
        } catch(IOException e) {
            System.out.println(e);
        }
        return false;
    }

	public boolean accountRequest(String userName,String password) { //�A�J�E���g�쐬�v��
        try {
            out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
            out.println(userName);
            out.println(password);
            out.flush();
            String isPer = in.readLine();
            if(isPer.equals("permit") == true) {  //�V�K�쐬�ł���
                return true;
            } else if(isPer.equals("notPermit") == true) {  //�V�K�쐬�ł��Ȃ�
                return false;
            } else {
                System.out.println("�m�F�Ƃ͕ʂ̕�����ł��B");  //�ʂ̕����񂪑����Ă���
            }
            //out.close();
            //in.close();
        }catch(IOException e) {
            System.out.println(e);
        }
        return false;
    }

	public boolean requestGroupSearch(String group) {		//�O���[�v�����v��
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println(group);
			out.flush();
			in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			if(in.readLine().equals("true")) return true;		//�Y������O���[�v�������
			else if(in.readLine().equals("false")) return false;		//�Y������O���[�v���Ȃ����
			else {												//�\�����ʕ����񂪑���ꂽ��
				System.out.println("�Ⴄ�����񂪑����܂����B");
				return false;
			}
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
		return false;
	}

	public void sendUserInformation(String job, String belong, ArrayList<String> group){		//�A�J�E���g���]��
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println(myuser.getName());		//������User�I�u�W�F�N�g���T�[�o�ŒT���Ă��炤���߂̃L�[(���O)�̓]��
			out.println(job);
			out.println(belong);
			out.flush();
			for(String s: group) {
				out.println(s);
				out.flush();
			}
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
	}

	public void receiveUserInformation() {		//�����̃A�J�E���g�����擾
		User u;
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println(myuser.getName());		//������User�I�u�W�F�N�g���T�[�o�ŒT���Ă��炤���߂̃L�[(���O)�̓]��
			ois = new ObjectInputStream(soc.getInputStream());
			u = (User) ois.readObject();		//�T�[�o����󂯎����������User�I�u�W�F�N�g
			myuser = u;
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
	}

	public void creatGroup(String group/*�O���[�v��*/, String explanation/*�O���[�v�̐���*/) {		//�O���[�v�V�K�쐬
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

	public ArrayList<User> receiveOtherUserInformation(String group) throws ClassNotFoundException {		//���O���[�v�̑����[�U�̏����擾
		ArrayList<User> groupmember = new ArrayList<User>();
		int number;		//�����o�[��
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println(group);		//�ǂ̃O���[�v�̃����o�[�����T�[�o���T�����߂̃L�[(�O���[�v��)�̓]��
			in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			number = Integer.parseInt(in.readLine());		//�����o�[��
			ois = new ObjectInputStream(soc.getInputStream());
			for(int i=0; i<number; i++) {
				groupmember.add((User) ois.readObject());
			}
			return groupmember;
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
		return null;
	}

	public ArrayList<Question> receiveQuestions(String group) {		//�O���[�v���̎�����擾
		ArrayList<Question> groupquestion = new ArrayList<Question>();
		int number;		//���␔
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println(group);
			out.flush();
			in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			number = Integer.parseInt(in.readLine());
			ois = new ObjectInputStream(soc.getInputStream());
			for(int i=0; i<number; i++) {
				groupquestion.add((Question)ois.readObject());
			}
			return groupquestion;
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
		return null;
	}

	public void sendQuestion(String question/*������e*/, String answerer/*�I�t�@�[���o������*/, int coin) {		//����̑��M
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println(myuser.getName());		//����҂̖��O�̑��M
			out.println(myuser.getGroup());		//����̑�����O���[�v���̑��M
			out.println(question);				//������e�̑��M
			if(answerer.equals(null)==false)sendOffer(question, answerer);		//�I�t�@�[���肪�w�肳��Ă���ꍇ�A�I�t�@�[�𑗂�
			out.println(coin);					//����ɂ�����ꂽ�R�C�����̑��M
			out.flush();
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
	}

	public void sendOffer(String question/*������e*/, String answerer/*�I�t�@�[���o������*/) {		//�I�t�@�[�̑��M
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println(question);		//�ǂ̎���ɑ΂���I�t�@�[�������ʂ��邽�߂̃L�[�i������e�j�̑��M
			out.println(answerer);
			out.flush();
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
	}

	public ArrayList<Question> receiveAnswer() {		//����������������̎擾�B�񓚁A�����ғ��̏��͂���������o���B
		ArrayList<Question> myquestion = new ArrayList<Question>();
		int number;
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println(myuser.getName());		//�N�̎��₩�𔻕ʂ��邽�߂̃L�[�i�����̖��O�j�̑��M
			out.flush();
			in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			number = Integer.parseInt(in.readLine());
			ois = new ObjectInputStream(soc.getInputStream());
			for(int i=0; i<number; i++) {
				myquestion.add((Question) ois.readObject());
			}
			return myquestion;
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
		return null;
	}

	public ArrayList<Question> receiveOffer() {		//�����ɗ��Ă���I�t�@�[�̎�M�i��̓I�ɂ́AQuestion�N���X�̑���offered�Ɏ����̖��O���Z�b�g����Ă���Question�I�u�W�F�N�g�̎�M�j
		ArrayList<Question> offer = new ArrayList<Question>();
		int number;
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println(myuser.getName());		//�����ɍ��v���鎿������ʂ��邽�߂̃L�[�i�����̖��O�j�̑��M
			out.flush();
			in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			number = Integer.parseInt(in.readLine());
			ois = new ObjectInputStream(soc.getInputStream());
			for(int i=0; i<number; i++) {
				offer.add((Question) ois.readObject());
			}
			return offer;
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
		return null;
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
			out.println(myuser.getName());		//�����ҁi�����̖��O�j�̑��M�B
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
		String mes;
		try {
			in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			mes = in.readLine();
			return mes;
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
		return null;
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
		int c;
		try {
			in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			c = Integer.parseInt(in.readLine());
			return c;
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
		return 0;
	}

	public User getMyUser() {		//������User�I�u�W�F�N�g��Ԃ�
		return myuser;
	}

	public static void main(String[] args) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u

	}

}