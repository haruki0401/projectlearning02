package pro2;

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
	private ArrayList<Question> myquestion;		//��������������
	private ArrayList<Question> myoffer;		//�����ɗ��Ă���I�t�@�[
	private ArrayList<Question> mycandidacy;		//����������₵������
	private Group mygroup;						//�O���[�v���
	private static String ipadress = "localhost";
	private static int port = 10027;

	public boolean connectServer(String ipaddress,int port) {		//�T�[�o�Ƃ̐ڑ�
        try {
            soc = new Socket(ipaddress,port);
            System.out.println("�T�[�o�Ɛڑ��ł��܂���");
            return true;
        }catch(UnknownHostException e) {
            System.out.println("�z�X�g�ɐڑ��ł��܂���B");
            System.out.println(e);
        }catch(IOException e) {
            System.out.println("�T�[�o�[�ڑ����ɃG���[���������܂����B");
            System.out.println(e);
        }
        return false;
	}

	public boolean loginRequest(String userName,String password) { //���O�C���v��
        try {
            in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
            out.println("�F��");
            out.println(userName);
            out.println(password);
            out.flush();
            String isPer = in.readLine();
            if(isPer.equals("ltrue") == true) {  //���O�C���F�؂��ꂽ
                return true;
            } else if(isPer.equals("lfalse") == true) {  //���O�C���F�؂���Ȃ�����
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

	public boolean accountRequest(String userName,String password, String ai/*�����t*/) { //�A�J�E���g�쐬�v��
        try {
            out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
            out.println("�V�K�o�^");
            out.println(userName);
            out.println(password);
            out.println(ai);
            out.flush();
            String isPer = in.readLine();
            System.out.println(isPer);
            if(isPer.equals("rtrue")) {  //�V�K�쐬�ł���
                return true;
            } else if(isPer.equals("rfalse")) {  //�V�K�쐬�ł��Ȃ�
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

	public String remindPassword(String ai) {		//�p�X���[�h��Y�ꂽ�ꍇ�̗v��
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			out.println("�Y�ꂽ�l");
			out.println(ai);
			out.flush();
			String isPer = in.readLine();
			if(isPer.equals("ftrue")==true) {
				String password = in.readLine();
				return password;
			}else if(isPer.equals("ffalse")){
				String s = "�����t���Ⴂ�܂�";
				return s;
			}
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
        return null;

	}

	public void logoutRequest() {		//���O�A�E�g
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println("���O�A�E�g");
			out.flush();
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
	}

	public void sendUserInformation(String job, String belong, ArrayList<String> group){		//�A�J�E���g���]��
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println("�l���");
			//out.println(myuser.getName());		//������User�I�u�W�F�N�g���T�[�o�ŒT���Ă��炤���߂̃L�[(���O)�̓]��
			out.println(job);
			out.println(belong);
			out.println(group.size());
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

	public boolean requestGroupSearch(String group) {		//�O���[�v�����v��
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println("�O���[�v����");
			out.println(group);
			out.flush();
			in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			if(in.readLine().equals("gtrue")) return true;		//�Y������O���[�v�������
			else if(in.readLine().equals("gfalse")) return false;		//�Y������O���[�v���Ȃ����
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



	public void receiveUserInformation(String name) {		//�����̃A�J�E���g�����擾
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println("���");
			out.println(name);
			out.flush();
			ois = new ObjectInputStream(soc.getInputStream());
			myuser = (User) ois.readObject();		//�T�[�o����󂯎����������User�I�u�W�F�N�g
			System.out.println("�I�u�W�F�N�g�擾");
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
			System.out.println("�I�u�W�F�N�g�擾���s");
		} catch (ClassNotFoundException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
			System.out.println("�I�u�W�F�N�g�擾���s");
		}
	}

	public boolean creatGroup(String group/*�O���[�v��*/, String explanation/*�O���[�v�̐���*/) {		//�O���[�v�V�K�쐬
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println("�O���[�v�쐬");
			out.println(group);
			out.println(explanation);
			out.flush();
			in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			if(in.readLine().equals("gtrue")==true) return true;
			else if(in.readLine().equals("gfalse")==false) return false;
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
		return false;
	}

	public void receiveQuestions(String group) {		//�O���[�v�������擾
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println("�������̃O���[�v���");
			out.println(group);
			out.flush();
			ois = new ObjectInputStream(soc.getInputStream());
			mygroup = (Group) ois.readObject();
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
	}

	public void sendQuestion(String question/*������e*/, String group/*����̑�����O���[�v*/,  String answerer/*�I�t�@�[���o������*/, int coin) {		//����̑��M
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println("������e");
			out.println(question);				//������e�̑��M
			out.println(group);					//����̑�����O���[�v���̑��M
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

	public void receiveAnswer() {		//��������������̏��̎擾�B�񓚁A�����ғ��̏��͂���������o���B
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println("��������������");
			out.println(myuser.getName());		//�N�̎��₩�𔻕ʂ��邽�߂̃L�[�i�����̖��O�j�̑��M
			out.flush();
			ois = new ObjectInputStream(soc.getInputStream());
			while(true) {
				myquestion.add((Question) ois.readObject());
			}
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO �����������ꂽ catch �u���b�N
			//e.printStackTrace();
		}
	}

	public void receiveOffer() {		//�����ɗ��Ă���I�t�@�[�̎�M�i��̓I�ɂ́AQuestion�N���X�̑���offered�Ɏ����̖��O���Z�b�g����Ă���Question�I�u�W�F�N�g�̎�M�j
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println("�I�t�@�[���Ă邩��");
			out.flush();
			ois = new ObjectInputStream(soc.getInputStream());
			while(true) {
				myoffer.add((Question)ois.readObject());
			}
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO �����������ꂽ catch �u���b�N
		}
	}

	public void receiveCandidate(){		//����������₵������̎�M
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println("����₵�Ă鎿��");
			out.flush();
			ois = new ObjectInputStream(soc.getInputStream());
			while(true) {
				mycandidacy.add((Question)ois.readObject());
			}
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO �����������ꂽ catch �u���b�N
			//e.printStackTrace();
		}
	}

	public void sendAnswer(String question/*������e*/, String answer/*�񓚓��e*/) {		//�񓚂̑��M
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println("��");
			out.println(answer);
			out.println(question);
			out.flush();
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
	}

	public void Candidacy(String question/*������e*/) {		//�����
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println("�񓚗����");
			out.println(question);		//����₷�鎿��̓��e�̑��M
			out.flush();
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
	}

	public void cancelCandidacy(String question) {		//���������������̎�����
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println("����������");
			out.println(question);		//����������������̓��e�̑��M
			out.flush();
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}

	}

	public void sendValue(String question/*������e*/, double value/*�]���l*/) {		//�]���l�̑��M
		try {
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println("�]���l�ύX");
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
			out.println("�I�t�@�[������");
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
			out.println("�I�t�@�[����");
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
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println("���b�Z�[�W��M");
			out.flush();
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
			out.println("����");
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
			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			out.println("����");
			out.flush();
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

	public ArrayList<Question> getMyQuestion() {		//������User�I�u�W�F�N�g��Ԃ�
		return myquestion;
	}

	public ArrayList<Question> getMyCandidacy() {		//������User�I�u�W�F�N�g��Ԃ�
		return myquestion;
	}

	public Group getMyGroup() {		//������User�I�u�W�F�N�g��Ԃ�
		return mygroup;
	}


	public static void main(String[] args) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u

		//���������p�̏���
		Client client = new Client();
		if(client.connectServer("192.168.56.1", 10084)==true) {
			if(client.accountRequest("ua", "ua", "�ЂƂ��")==true) {
				if(client.creatGroup("���l������w", "�������̃O���[�v�ł�")==true) {
					ArrayList<String> as = new ArrayList<String>();
					as.add("���l������w");
					client.sendUserInformation("�w��", "��w��", as);
				}else {
					System.out.println("�O���[�v�쐬���s");
				}
			}else {
				System.out.println("�A�J�E���g�쐬���s");
			}

			client.receiveUserInformation("ua");
			User myuser = client.getMyUser();
			System.out.println(myuser.getName());
			ArrayList<String> g = new ArrayList<String>();
			g = myuser.getGroup();
			for(String s: g) {
				System.out.println(s);
			}

		}



	}

}
