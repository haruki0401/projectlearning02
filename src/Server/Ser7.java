package pro2;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Ser5 {

	private static int maxconnection = 1000000;//100�l���ő�ڑ��l��

	private int port; // �T�[�o�̑҂��󂯃|�[�g

	private boolean[] online; //�I�����C����ԊǗ��p�z��

	private PrintWriter[] out; //�f�[�^���M�p�I�u�W�F�N�g


	//private ObjectInputStream[] ois;              /*  �I�u�W�F�N�g���̓X�g���[��  */

	private ObjectOutputStream[] oos;//�o�͗p

	private Receiver[] receiver; //�f�[�^��M�p�I�u�W�F�N�g

	private static int member;//�ڑ����Ă���l�̐l��

	private static Socket[] socket;//��t�p�̃\�P�b�g

	private static boolean[] login;


	//private static int hut=0;//�쐬����Ă����l�̃`���b�g��

	static HashMap<String, String> hashD1 = new HashMap<>();//hash1�̓v���C���̔F�؂Ȃ�
	static HashMap<String, String> hashD2 = new HashMap<>();//hash2//key=aikotoba,value=password
    static HashMap<String, String> hashD3 = new HashMap<>();//���O�A�E��
	static HashMap<String, String> hashD4 = new HashMap<>();//���O�A����
	static HashMap<String, String> hashD5 = new HashMap<>();//���O�A�����t

	static HashMap<String, Double> hashC1 = new HashMap<>();//���O�A�]���l
	static HashMap<String, Integer> hashC2 = new HashMap<>();//���O�A���␔
	static HashMap<String, Integer> hashC3 = new HashMap<>();//���O�A�񓚐�
	static HashMap<String, ArrayList<String>> hashC4 = new HashMap<>();//���O�A�O���[�v

	static HashMap<Integer,String> hashA1 = new HashMap<>();//playerNo,���O
	static HashMap<String,Integer> hashA2 =new HashMap<>();//���O�AplayerNo//���O�C�����Ă���l�Ɠ������O�Ń��O�C���ł��Ȃ��悤�ɂ���



	static HashMap<String, String> hashA8 = new HashMap<>();//��l�ŏ�������肷�邽�߂̕R�Â�

	//int flag = 0;		//ObjectOutputStream���P�񂾂��J���邽�߂̐���Ɏg��


	//�R���X�g���N�^

	public Ser5(int port) { //�҂��󂯃|�[�g�������Ƃ���

		this.port = port; //�҂��󂯃|�[�g��n��

		out = new PrintWriter[maxconnection]; //�f�[�^���M�p�I�u�W�F�N�g���ő�l�����p��
		oos = new ObjectOutputStream[maxconnection];
		receiver = new Receiver[maxconnection]; //�f�[�^��M�p�I�u�W�F�N�g���ő�l�����p��

		online = new boolean[maxconnection];//�I�����C����ԊǗ��p�z���p��

		login = new boolean[maxconnection];//�F�؂�˔j�����l
		socket=new Socket[maxconnection];


	}

	//�f�[�^��M�p�X���b�h(�����N���X)

	class Receiver extends Thread {

		private InputStreamReader[] sisr; //��M�f�[�^�p�����X�g���[��

		private BufferedReader[] br; //�����X�g���[���p�̃o�b�t�@

		private int playerNo;//�v���C�������ʂ��邽�߂̔ԍ�

		// �����N���XReceiver�̃R���X�g���N�^

		Receiver(Socket socket, int playerNo) {

			sisr = new InputStreamReader[maxconnection];

			br = new BufferedReader[maxconnection];

			try {

				this.playerNo = playerNo; //�v���C���ԍ���n��

				sisr[playerNo] = new InputStreamReader(socket.getInputStream());

				br[playerNo] = new BufferedReader(sisr[playerNo]);

			} catch (IOException e) {

				System.err.println("�f�[�^��M���ɃG���[���������܂���: " + e);

			}

		}

		// �����N���X Receiver�̃��\�b�h

		@Override

		public void run() {

			try {
				if(member >= 100) {
					forwardMessage("�ő�ڑ��l���𒴂��Ă��邽�߁A�ڑ���؂�܂�", playerNo);
					socket[playerNo].close();

				}

				//int aite = 0;

				while(true) {// �f�[�^����M��������



					String inputLine = br[playerNo].readLine();//�f�[�^����s���ǂݍ���

					String a = "";

					String b = "";

					String c = "";

					String d="";

					if(inputLine.equals("�F��")) {

						a = br[playerNo].readLine();

						b = br[playerNo].readLine();

						boolean l=true;

						if(hashA2.containsKey(a)) {
							l=false;
						}

						if(ninsyou(a, b)&&l) {

							forwardMessage("ltrue", playerNo);
							forwardMessage(String.valueOf(playerNo), playerNo);

							forwarddammy(playerNo);		//�V����ObjectoutputStream���J���邽�߂Ɏg�p

							hashA1.put(playerNo, a);
							hashA2.put(a,playerNo );

							login[playerNo] = true;

							User user=obj(a);

						} else {
							forwardMessage("lfalse", playerNo);
						}

					}

					if(inputLine.equals("�V�K�o�^")) {

						a = br[playerNo].readLine();

						b = br[playerNo].readLine();

						c = br[playerNo].readLine();

						String k = newpeople(a, b, c);

						hashA1.put(playerNo,a);

						if(k.equals("rtrue")) {
							forwardMessage("rtrue", playerNo);
							forwardMessage(String.valueOf(playerNo), playerNo);

							forwarddammy(playerNo);		//�V����ObjectoutputStream���J���邽�߂Ɏg�p

							System.out.println("�_�~�[���M����");

							User user=obj(a);
						} else {

							forwardMessage("rfalse" , playerNo);


						}

					}

					if(inputLine.equals("�Y�ꂽ�l")) {

						a = br[playerNo].readLine();

						b = br[playerNo].readLine();

						String str;

						if((str = forgetman(a, b)) != null) {

							forwardMessage("ftrue" , playerNo);
							forwardMessage(str, playerNo);

						}else {
							forwardMessage("ffalse",playerNo);
						}

					}
					if(inputLine.equals("���O�A�E�g")) {

						if(logout(playerNo)) {

							login[playerNo] = false;

						}
						hashA2.remove(hashA1.get(playerNo));
						hashA1.remove(playerNo);


					}


					if(inputLine.equals("�l���")) {

						a = br[playerNo].readLine();//�E��

						b = br[playerNo].readLine();//����

						c=br[playerNo].readLine();//�O���[�v��

						int kazu=Integer.parseInt(c);

						User u=obj(hashA1.get(playerNo));
						ArrayList<Group> l=new ArrayList<Group>();
						for(String s:u.getGroup()) {

							System.out.println(s+"lppppppppppppppppppppppppppppp");
							Group g=null;

							FileInputStream inFile1 = new FileInputStream("Group.obj");

							ObjectInputStream inObject1 = new ObjectInputStream(inFile1);

							

							while(true) {

								try {

								 g = (Group)inObject1.readObject();
								 System.out.println(g.getgname()+"pmmmmmmmmmmmmmmmmmmmmmmmmmm");

							if(g.getgname().equals(s)) {
								System.out.println("ulalllllllllllllllllllllllllllllllll");
								g.delmember(u);
								l.add(g);
							}

							}catch(java.io.EOFException e) {



							inObject1.close();
							inFile1.close();
							break;

							} catch (ClassNotFoundException e) {
								// TODO �����������ꂽ catch �u���b�N
								e.printStackTrace();

							}

							}


						}

						for(Group k:l) {
							gnew(k);
						}
						l.clear();

						u.clearGroup();
						for(int i=0;i<kazu;i++) {
							d=br[playerNo].readLine();

							Group g=null;
							FileInputStream inFile1 = new FileInputStream("Group.obj");
							ObjectInputStream inObject1 = new ObjectInputStream(inFile1);


							while(true) {

								try {
								 g = (Group)inObject1.readObject();


							if(g.getgname().equals(d)) {
								g.setmember(obj(hashA1.get(playerNo)));
								u.setGroup(g.getgname());
								l.add(g);
							}

							}catch(java.io.EOFException e) {




							inObject1.close();
							inFile1.close();
							break;
							} catch (ClassNotFoundException e) {
								// TODO �����������ꂽ catch �u���b�N
								e.printStackTrace();
							}

							}

						}
						hashD3.put(hashA1.get(playerNo),a);
						hashD4.put(hashA1.get(playerNo),b);//�v�f�̒u������
						hashC4.put(hashA1.get(playerNo),u.getGroup());


						for(Group k:l) {
							gnew(k);
						}
						unew(u);//�t�@�C���̒��g�̏�������
						Uhyouzi();



					}


					///�O���[�v���`���b�g�@�\
					if(inputLine.equals("�O���[�v�쐬")) {
						a = br[playerNo].readLine();//�O���[�v��//�������O�͂��߂ɂ��悤�Ǝv���Ă���
						b = br[playerNo].readLine();//�O���[�v�̐�����
						
						Group g=new Group(a,b,obj(hashA1.get(playerNo)));
						
						User u=obj(hashA1.get(playerNo));

						if(serch(a)) {
							forwardMessage("gfalse",playerNo);
							System.out.println("gfalse���M����");

						}else {
							forwardMessage("gtrue",playerNo);
							System.out.println("gtrue���M����");
							u.setGroup(a);
//							FileOutputStream outFile = new FileOutputStream("Group.obj");
//							ObjectOutputStream outObject = new ObjectOutputStream(outFile);
//							outObject.writeObject(a);
//							outObject.close();
//							outFile.close();
							unew(u);
							gwrite(g);
						}






					}






					if(inputLine.equals("�������̃O���[�v���")) {
System.out.println("�O���[�v���M�J�n");
						a = br[playerNo].readLine();//��񂪗~�����O���[�v��
						//���̃O���[�v�̃I�u�W�F�N�g���擾��������B
						if(chat(a)!=null) {

							forwardgru(playerNo,chat(a));
						}else {
							forwardMessage("gfalse",playerNo);
						}


					}


					if(inputLine.equals("�O���[�v����")) {
						a = br[playerNo].readLine();//�����������O���[�v���̎擾

						if(serch(a)) {
							forwardMessage("gtrue",playerNo);
						}else{
							forwardMessage("gfalse",playerNo);
						}


						}




					if(inputLine.equals("������e")) {

						
						b=br[playerNo].readLine();//�O���[�v��
						c=br[playerNo].readLine();/////����̃R�C�����ǉ�
						a=br[playerNo].readLine();//������e�̎�M
						int coin=Integer.parseInt(c);
						String name=hashA1.get(playerNo);
						//String group=hashA2.get(playerNo);
						String group=b;
						User ques=obj(name);//����҂̃I�u�W�F�N�g�m��

						ques.setQuestion();
						hashC2.put(name,ques.getQuestion());
						ques.minusCoin(coin);//�q�����R�C���̕��������炷�B
						unew(ques);//�t�@�C���̍X�V

						Question p=new Question(ques,a,group,coin);
						Group g=chat(group);
						g.setchat(p);

//						FileOutputStream outFile = new FileOutputStream("Question.obj");
//						ObjectOutputStream outObject = new ObjectOutputStream(outFile);
//						outObject.writeObject(p);
//						outObject.close();
//						outFile.close();
						qwrite(p);




					}

					if(inputLine.equals("�񓚗����")) {
						a = br[playerNo].readLine();//�񓚂���������
						String name=hashA1.get(playerNo);
						User er=obj(name);
						Question k=ques(a);
					    if(k!=null) {
					    	k.setCandidates(er);


					    qnew(k);//�t�@�C���̓��e���X�V
					    }






						}
					if(inputLine.equals("�I�t�@�[���Ă邩��")) {
						String name=hashA1.get(playerNo);

						FileInputStream inFile1 = new FileInputStream("Question.obj");
						ObjectInputStream inObject1 = new ObjectInputStream(inFile1);
						Question q;
						ArrayList<Question> myo = new ArrayList<Question>();


						try {

						while(true) {

						q = (Question)inObject1.readObject();
						if(q.checkOffered()) {
							if(q.getOffer().getName().equals(name)) {
								myo.add(q);
								//forwardq(playerNo,q,0);
							}


						}
						}



						}catch(java.io.EOFException e) {

							forwardMessage(String.valueOf(myo.size()), playerNo);

							//oos[playerNo] = new ObjectOutputStream(socket[playerNo].getOutputStream());

							for(Question qu: myo) {
								forwardq(playerNo,qu);
								//flag++;
							}


						inObject1.close();
						inFile1.close();
						//break;
						} catch (ClassNotFoundException e) {
							// TODO �����������ꂽ catch �u���b�N
							e.printStackTrace();
						}





					}

					if(inputLine.equals("�I�t�@�[�l�I�o")) {
						a = br[playerNo].readLine();//�I�񂾉񓚎҂̖��O���擾
						b = br[playerNo].readLine();//������e����M

						Question k=ques(b);
						if(k!=null) {
						 k.setOffer(obj(a));

						qnew(k);
						}
					}
					if(inputLine.equals("�I�t�@�[������")) {
						a= br[playerNo].readLine();//������e����M

						Question k=ques(a);
						if(k!=null) {
						 k.canselOffer();

						qnew(k);






						}

					}
					if(inputLine.equals("�I�t�@�[����")) {
						//String name = hashA1.get(playerNo);//�I�񂾉񓚎҂̖��O���擾
						a= br[playerNo].readLine();//������e����M

						Question k=ques(a);
						if(k!=null) {
						 k.canselOffer();

						qnew(k);






						}
					}
					if(inputLine.equals("��������������")) {
						String name=hashA1.get(playerNo);

						FileInputStream inFile1 = new FileInputStream("Question.obj");
						ObjectInputStream inObject1 = new ObjectInputStream(inFile1);
						Question q;
						ArrayList<Question> myq = new ArrayList<Question>();
//System.out.println("���\�b�h���s");

						try {

						while(true) {

						q = (Question)inObject1.readObject();
						//System.out.println(q.getQuestion());
						if(q.getQuestioner().getName().equals(name)) {
System.out.println(q.getQuestion());
							myq.add(q);
							//forwardq(playerNo,q);
							//System.out.println("���M����");
						}


						}



						}catch(java.io.EOFException e) {

							//System.out.println(myq.size()+"        "+String.valueOf(myq.size()));
							String l=String.valueOf(myq.size());
							//forwardMessage("",playerNo);
							forwardMessage(l, playerNo);

//forwardMessage(String.valueOf(myq.size()), playerNo);

							//oos[playerNo] = new ObjectOutputStream(socket[playerNo].getOutputStream());

							for(Question qu: myq) {
								forwardq(playerNo,qu);
							}




						inObject1.close();
						inFile1.close();
						//break;
						} catch (ClassNotFoundException e) {
							// TODO �����������ꂽ catch �u���b�N
							e.printStackTrace();
						}






					}
					if(inputLine.equals("����₵�Ă鎿��")) {
						String name=hashA1.get(playerNo);

						FileInputStream inFile1 = new FileInputStream("Question.obj");
						ObjectInputStream inObject1 = new ObjectInputStream(inFile1);
						Question q;
						ArrayList<Question> myc = new ArrayList<Question>();


						try {

						while(true) {

						q = (Question)inObject1.readObject();
						ArrayList<User> ki=q.getCandidates();
						for(User u:ki) {
							if(u.getName().equals(name)) {
								myc.add(q);
								//forwardq(playerNo,q,0);
							}
						}


						}



						}catch(java.io.EOFException e) {

							forwardMessage(String.valueOf(myc.size()), playerNo);

							//oos[playerNo] = new ObjectOutputStream(socket[playerNo].getOutputStream());

							for(Question qu: myc) {
								forwardq(playerNo,qu);
								//flag++;
							}


						inObject1.close();
						inFile1.close();
						//break;
						} catch (ClassNotFoundException e) {
							// TODO �����������ꂽ catch �u���b�N
							e.printStackTrace();
						}







					}








					if(inputLine.equals("��")) {
						a = br[playerNo].readLine();//�񓚂̎擾
						b=br[playerNo].readLine();//������e
						User u=obj(hashA1.get(playerNo));

						Question k=ques(b);
						String group=k.getGroup();
						if(k!=null) {
						 k.setAnswer(a);
						 hashC3.put(hashA1.get(playerNo), u.getAnswer());
						 k.setAnswerer(obj(hashA1.get(playerNo)));

						 u.setAnswer();//�񓚐�����
						 u.plusCoin(k.getCoin());//����Ɍ������Ă���R�C���̕����������B





						 unew(u);//�t�@�C���̍X�V


						 Group g=chat(group);
							g.setchat(k);

						qnew(k);
						}
					}


					if(inputLine.equals("���")) {//�v���C���[�̑S��񂪋l�܂����I�u�W�F�N�g�𑗂�B
						a = br[playerNo].readLine();//��񂪗~�����l�̖��O
						System.out.println(a);
						User u=obj(a);
						forwardu(playerNo, u);

						/*oos[playerNo] = new ObjectOutputStream(socket[playerNo].getOutputStream());
						oos[playerNo].writeObject(u);

						oos[playerNo].flush();

						oos[playerNo].reset();*/

					}


					if(inputLine.equals("�]���l�ύX")) {
						a = br[playerNo].readLine();//�]���l�̎擾
						b=br[playerNo].readLine();//������e�̎擾
						int val=Integer.parseInt(a);
						Question q=ques(b);
						q.setValue(val);
						qnew(q);

						String name=hashA1.get(playerNo);

						double value;
						double kai;
						double hen;
						hen=Double.parseDouble(a);
						kai=(double)hashC3.get(name);
						value=hashC1.get(name);
						value=(value/(kai-1)+hen)/kai;//���ϒl���Z�o���Ȃ����Ă���B
						hashC1.put(name,value);

						User user=obj(name);

						unew(user);

					}




				//////////////////�ǉ�/////////////////////////////
				if(inputLine.equals("�R�C���w��")) {
					a=br[playerNo].readLine();//�R�C���w����
					int coin=Integer.parseInt(a);
					User u=obj(hashA1.get(playerNo));
					u.plusCoin(coin);
					unew(u);

				}



				if(inputLine.equals("���������")) {//�V�����ǉ����܂����B
					a=br[playerNo].readLine();//����������������e�̎�M
					String name=hashA1.get(playerNo);
					FileInputStream inFile1 = new FileInputStream("Question.obj");
					ObjectInputStream inObject1 = new ObjectInputStream(inFile1);
					Question q;


					try {

					while(true) {

					q = (Question)inObject1.readObject();
					if(q.getQuestion().equals(a)) {
						User u=obj(hashA1.get(playerNo));
						u.plusCoin(q.getCoin());//����ɓq���Ă����R�C����Ԃ��Ă��炤
						unew(u);//player�t�@�C���̍X�V
						qdelete(q);//�v�f��Question.obj����폜����



					}


					}



					}catch(java.io.EOFException e) {




					inObject1.close();
					inFile1.close();
					//break;
					} catch (ClassNotFoundException e) {
						// TODO �����������ꂽ catch �u���b�N
						e.printStackTrace();
					}



				}


				}

			} catch (IOException e) { // �ڑ����؂ꂽ�Ƃ�


				member--;//�ڑ����؂ꂽ�l�������炷

				login[playerNo]=false;
				online[playerNo] = false; //�v���C���̐ڑ���Ԃ��X�V����
				if(hashA1.containsKey(playerNo)) {
					hashA2.remove(hashA1.get(playerNo));
					hashA1.remove(playerNo);

				}


			}

		}

}



	///// ���\�b�h///////////////////
	public void acceptClient() { //�N���C�A���g�̐ڑ�(�T�[�o�̋N��)

		int n = 1;

		member = 0;//�N���ڑ����Ă��Ȃ��Ƃ���0

		try {

			System.out.println("�T�[�o���N�����܂����D");

			ServerSocket ss = new ServerSocket(port); //�T�[�o�\�P�b�g��p��

			while(true) {

				if(online[n] == false) {

					socket[n] = ss.accept(); //�V�K�ڑ����󂯕t����

					online[n] = true;

					//oos[n] = new ObjectOutputStream(socket[n].getOutputStream());

					out[n] = new PrintWriter(socket[n].getOutputStream(),true);

					receiver[n] = new Receiver(socket[n], n);

					online[n] = true;

					receiver[n].start();//�X���b�h���N�����R�C�c���ǂ��Ŕ������邩

					member++;

				} else if(online[n] == true) {

					n++;

					if(n > 104) {//��

						n = 1;

					}

				}

			}

		} catch (Exception e) {

			System.err.println("�\�P�b�g�쐬���ɃG���[���������܂���: " + e);

		}

	}
	public void forwardMessage(String msg, int playerNo) { //������̓]��
System.out.println(msg+" forwardMessage��");
try {


	out[playerNo] = new PrintWriter(socket[playerNo].getOutputStream());
} catch (IOException e) {
	 //TODO �����������ꂽ catch �u���b�N
	e.printStackTrace();
}

		out[playerNo].println(msg);

		out[playerNo].flush();


		try {
			out[playerNo] = new PrintWriter(socket[playerNo].getOutputStream());
		} catch (IOException e) {
			 //TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}



	}

	public void forwarddammy(int playerNo) {		//�V����ObjectoutputStream���J���邽�߂Ɏg�p
		User dammy = new User("dammy", "dammy", "�Ȃ�");
		try {
			oos[playerNo] = new ObjectOutputStream(socket[playerNo].getOutputStream());
			oos[playerNo].writeObject(dammy);
			oos[playerNo].flush();
			//oos[playerNo].reset();



		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
	}

	public void forwardu(int playerNo, User u/*, int flag*/) {
		try {
			//if(flag==0) {
				//oos[playerNo] = new ObjectOutputStream(socket[playerNo].getOutputStream());
			/*}else {
				oos[playerNo] = new NonHeaderObjectOutputStream(socket[playerNo].getOutputStream());
			}*/
System.out.println(u.getName()+" forwardu��");

			oos[playerNo].writeObject(u);
			oos[playerNo].flush();
			//oos[playerNo].reset();

		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
	}


	public void forwardgru(int playerNo,Group a/*, int flag*/) {//�O���[�v�I�u�W�F�N�g���M
	try {
		//if(flag==0) {
			//oos[playerNo] = new ObjectOutputStream(socket[playerNo].getOutputStream());
		//}else {
			//oos[playerNo] = new NonHeaderObjectOutputStream(socket[playerNo].getOutputStream());
		//}
System.out.println(a.getgname()+" forwardgru��");

		oos[playerNo].writeObject(a);

		oos[playerNo].flush();

		//oos[playerNo].reset();
	} catch (IOException e) {
		// TODO �����������ꂽ catch �u���b�N
		e.printStackTrace();
	}

}

	public void forwardq(int playerNo,Question a/*, int flag*/) {//�N�G�X�`�����I�u�W�F�N�g���M
	try {
		//if(flag==0) {
			//oos[playerNo] = new ObjectOutputStream(socket[playerNo].getOutputStream());
		//}else {*/
			//oos[playerNo] = new NonHeaderObjectOutputStream(socket[playerNo].getOutputStream());
		//}
System.out.println(a.getQuestion()+" forwardq��");
		oos[playerNo].writeObject(a);

		oos[playerNo].flush();

		//oos[playerNo].reset();


		//oos[playerNo].close();

	} catch (IOException e) {
		// TODO �����������ꂽ catch �u���b�N
		e.printStackTrace();
	}

}

	public boolean ninsyou(String name, String pass) {//1,�v���C���̔F��

		boolean nin = true;

		if(hashD1.get(name) != null && hashD1.get(name).equals(pass)) {

			for(int i = 1; i < 100000; i++) {

				if(login[i] == true) {

					if(hashA1.get(i).equals(name)) {
						nin = false;
					}

				}

			}

			return nin;

		} else {

			return false;
		}

	}


	synchronized public void qwrite(Question k) {//Questionfile�̏㏑��

		Question Q;
		try {
		ArrayList<Question> a = new ArrayList<Question>();
		FileInputStream inFile1 = new FileInputStream("Question.obj");
		ObjectInputStream inObject1 = new ObjectInputStream(inFile1);
try {
	a.add(k);
		while(true) {
			Q = (Question)inObject1.readObject();

		    a.add(Q);

			}

			}catch(java.io.EOFException e) {




			inObject1.close();
			inFile1.close();

			} catch (ClassNotFoundException e) {
				// TODO �����������ꂽ catch �u���b�N
				e.printStackTrace();
			}




		FileOutputStream outFile = new FileOutputStream("Question.obj");
		ObjectOutputStream outObject = new ObjectOutputStream(outFile);
		for(Question r:a) {

			outObject.writeObject(r);

		}

			outObject.close();

		outFile.close();
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}

	}
	synchronized public void qnew(Question k) {//Questionfile�̍X�V

		Question Q;
		try {
		ArrayList<Question> a = new ArrayList<Question>();
		FileInputStream inFile1 = new FileInputStream("Question.obj");
		ObjectInputStream inObject1 = new ObjectInputStream(inFile1);
try {
		while(true) {
			Q = (Question)inObject1.readObject();
			if(Q.getQuestion().equals(k.getQuestion())) {
				a.add(k);
			}else {
		    a.add(Q);
			}
			}

			}catch(java.io.EOFException e) {




			inObject1.close();
			inFile1.close();

			} catch (ClassNotFoundException e) {
				// TODO �����������ꂽ catch �u���b�N
				e.printStackTrace();
			}




		FileOutputStream outFile = new FileOutputStream("Question.obj");
		ObjectOutputStream outObject = new ObjectOutputStream(outFile);
		for(Question r:a) {

			outObject.writeObject(r);

		}

			outObject.close();

		outFile.close();
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}

	}

	synchronized public void qdelete(Question k) {//Questionfile���̗v�f�̍폜

		Question Q;
		try {
		ArrayList<Question> a = new ArrayList<Question>();
		FileInputStream inFile1 = new FileInputStream("Question.obj");
		ObjectInputStream inObject1 = new ObjectInputStream(inFile1);
try {
		while(true) {
			Q = (Question)inObject1.readObject();
			if(Q.getQuestion().equals(k.getQuestion())) {
			}else {
		    a.add(Q);
			}
			}

			}catch(java.io.EOFException e) {




			inObject1.close();
			inFile1.close();

			} catch (ClassNotFoundException e) {
				// TODO �����������ꂽ catch �u���b�N
				e.printStackTrace();
			}




		FileOutputStream outFile = new FileOutputStream("Question.obj");
		ObjectOutputStream outObject = new ObjectOutputStream(outFile);
		for(Question r:a) {

			outObject.writeObject(r);

		}

			outObject.close();

		outFile.close();
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}

	}

synchronized public void unew(User u) {//User.faile�̍X�V

		User U;;
		try {
		ArrayList<User> a = new ArrayList<User>();
		FileInputStream inFile1 = new FileInputStream("User.obj");
		ObjectInputStream inObject1 = new ObjectInputStream(inFile1);
try {
		while(true) {
			U = (User)inObject1.readObject();
			if(U.getName().equals(u.getName())) {
				a.add(u);
			}else {
		    a.add(U);
			}

		}
			}catch(java.io.EOFException e) {




			inObject1.close();
			inFile1.close();


			} catch (ClassNotFoundException e) {
				// TODO �����������ꂽ catch �u���b�N
				e.printStackTrace();
			}




		FileOutputStream outFile = new FileOutputStream("User.obj");//�����͏㏑���ł͂Ȃ����ׂď����o���̂�true�@�Ȃ�
		ObjectOutputStream outObject = new ObjectOutputStream(outFile);
		for(User r:a) {

			outObject.writeObject(r);

		}

			outObject.close();

		outFile.close();
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}

	}
synchronized public void uwrite(User u) {//User.faile�̍X�V

	User U;;
	try {
	ArrayList<User> a = new ArrayList<User>();
	FileInputStream inFile1 = new FileInputStream("User.obj");
	ObjectInputStream inObject1 = new ObjectInputStream(inFile1);
try {
	a.add(u);
	while(true) {
		U = (User)inObject1.readObject();

	    a.add(U);

		}

		}catch(java.io.EOFException e) {




		inObject1.close();
		inFile1.close();

		} catch (ClassNotFoundException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}




	FileOutputStream outFile = new FileOutputStream("User.obj");//�����͏㏑���ł͂Ȃ����ׂď����o���̂�true�@�Ȃ�
	ObjectOutputStream outObject = new ObjectOutputStream(outFile);
	for(User r:a) {

		outObject.writeObject(r);

	}

		outObject.close();

	outFile.close();
	} catch (IOException e) {
		// TODO �����������ꂽ catch �u���b�N
		e.printStackTrace();
	}

}

synchronized public void gnew(Group g) {//Group.faile�̍X�V

	Group G;
	try {
	ArrayList<Group> a = new ArrayList<Group>();
	FileInputStream inFile1 = new FileInputStream("Group.obj");
	ObjectInputStream inObject1 = new ObjectInputStream(inFile1);
try {
	while(true) {
		G = (Group)inObject1.readObject();
		if(G.getgname().equals(g.getgname())) {
			a.add(g);
		}else {
	    a.add(G);
		}
		}

		}catch(java.io.EOFException e) {




		inObject1.close();
		inFile1.close();

		} catch (ClassNotFoundException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}




	FileOutputStream outFile = new FileOutputStream("Group.obj");//�����͏㏑���ł͂Ȃ����ׂď����o���̂�true�@�Ȃ�
	ObjectOutputStream outObject = new ObjectOutputStream(outFile);
	for(Group r:a) {

		outObject.writeObject(r);

	}

		outObject.close();

	outFile.close();
	} catch (IOException e) {
		// TODO �����������ꂽ catch �u���b�N
		e.printStackTrace();
	}

}

synchronized public void udelete(User u) {//User.faile�̍X�V

	User U;;
	try {
	ArrayList<User> a = new ArrayList<User>();
	FileInputStream inFile1 = new FileInputStream("User.obj");
	ObjectInputStream inObject1 = new ObjectInputStream(inFile1);
try {
	while(true) {
		U = (User)inObject1.readObject();
		if(U.getName().equals(u.getName())) {
			//a.add(u);
		}else {
	    a.add(U);
		}
		}

		}catch(java.io.EOFException e) {




		inObject1.close();
		inFile1.close();

		} catch (ClassNotFoundException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}




	FileOutputStream outFile = new FileOutputStream("User.obj");//�����͏㏑���ł͂Ȃ����ׂď����o���̂�true�@�Ȃ�
	ObjectOutputStream outObject = new ObjectOutputStream(outFile);
	for(User r:a) {

		outObject.writeObject(r);

	}

		outObject.close();

	outFile.close();
	} catch (IOException e) {
		// TODO �����������ꂽ catch �u���b�N
		e.printStackTrace();
	}

}

synchronized public void gwrite(Group g) {//Group.faile�̏㏑��

	Group G;
	try {
	ArrayList<Group> a = new ArrayList<Group>();
	FileInputStream inFile1 = new FileInputStream("Group.obj");
	ObjectInputStream inObject1 = new ObjectInputStream(inFile1);
try {
	a.add(g);
	while(true) {
		G = (Group)inObject1.readObject();
	    a.add(G);

		}

		}catch(java.io.EOFException e) {




		inObject1.close();
		inFile1.close();

		} catch (ClassNotFoundException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}




	FileOutputStream outFile = new FileOutputStream("Group.obj");//�����͏㏑���ł͂Ȃ����ׂď����o���̂�true�@�Ȃ�
	ObjectOutputStream outObject = new ObjectOutputStream(outFile);
	for(Group r:a) {

		outObject.writeObject(r);

	}

		outObject.close();

	outFile.close();
	} catch (IOException e) {
		// TODO �����������ꂽ catch �u���b�N
		e.printStackTrace();
	}

}



	public String newpeople(String a, String b, String c) {//�V�K�o�^�̔F��

		String k = "";


		//FileOutputStream outFile = new FileOutputStream("User.obj");
		//ObjectOutputStream outObject = new ObjectOutputStream(outFile);



			if(!c.matches("^[��-��[]*$")) {
				k = "�����t�͂Ђ炪�Ȃ݂̂ł�";

			} else if(!a.matches("^[0-9a-zA-Z]*$") || !b.matches("^[0-9a-zA-Z]*$")) {//�����t�̂Ђ炪�Ȕ���A���K�\��
				k = "���O�ƃp�X���[�h�͔��p�p�����ł�";

			} else if(a.length() > 8) {
				k = "���O��8�����ȏ�ł�";
			} else if(b.length() > 16) {
				k = "�p�X���[�h��16�����ȏ�ł�";
			}else if(hashD1.containsKey(a)) {
				k="�������O�̐l�����܂�";
			} else {
				User u=new User(a,b,c);

					ArrayList<String> g=new ArrayList<String>();
					g.add("�O���[�v�Ȃ�");
					uwrite(u);


					hashD1.put(a, b);

					hashD2.put(c, b);

					hashD3.put(a,"�o�^�Ȃ�");

					hashD4.put(a,"�o�^�Ȃ�");

					hashD5.put(a, c);

					hashC1.put(a, u.getValue());

					hashC2.put(a, 0);

					hashC3.put(a,0);

					hashC4.put(a,g);


				   // outObject.close();

				   // outFile.close();

				    k="rtrue";

			}






			return k;

	}
	public boolean logout(int a) {//���O�A�E�g����

		return true;

	}
	synchronized public static Group chat(String name) { //�O���[�v�̃`���b�g���擾

	Group gu;
	try {
	FileInputStream inFile1 = new FileInputStream("Group.obj");

	ObjectInputStream inObject1 = new ObjectInputStream(inFile1);



	try {

	while(true) {
	gu = (Group)inObject1.readObject();
    if(gu.getgname().equals(name)) {
    	inObject1.close();
		inFile1.close();
    	return gu;
    }
	}

	}catch(java.io.EOFException e) {



		inObject1.close();
		inFile1.close();
		return null;
	}
	} catch (IOException e1) {
		// TODO �����������ꂽ catch �u���b�N
		e1.printStackTrace();


	} catch (ClassNotFoundException e) {
		// TODO �����������ꂽ catch �u���b�N
		e.printStackTrace();
	}
	return null;
//�����ɂ͓��B�ł��Ȃ��B

}public Question ques(String Q) { //�N�G�X�`�����擾

	Question q;
	try {
	FileInputStream inFile1 = new FileInputStream("Question.obj");
	ObjectInputStream inObject1 = new ObjectInputStream(inFile1);



	try {

	while(true) {
	q = (Question)inObject1.readObject();
    if(q.getQuestion().equals(Q)) {
    	inObject1.close();
		inFile1.close();
    	return q;
    }
	}

	}catch(java.io.EOFException e) {



		inObject1.close();
		inFile1.close();
		return null;
	}
	} catch (IOException e1) {
		// TODO �����������ꂽ catch �u���b�N
		e1.printStackTrace();


	} catch (ClassNotFoundException e) {
		// TODO �����������ꂽ catch �u���b�N
		e.printStackTrace();
	}
	return null;
//�����ɂ͓��B�ł��Ȃ��B

}
synchronized	public static boolean serch(String name) {//���O����O���[�v������
		Group gu;
		try {
		FileInputStream inFile1 = new FileInputStream("Group.obj");
		ObjectInputStream inObject1 = new ObjectInputStream(inFile1);



		try {

		while(true) {
		gu = (Group)inObject1.readObject();
	    if(gu.getgname().equals(name)) {
	    	inObject1.close();
			inFile1.close();
	    	return true;
	    }
		}

		}catch(java.io.EOFException e) {



			inObject1.close();
			inFile1.close();
			return false;
		}
		} catch (IOException e1) {
			// TODO �����������ꂽ catch �u���b�N
			e1.printStackTrace();


		} catch (ClassNotFoundException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
		return false;
	//�����ɂ͓��B�ł��Ȃ��B

	}




	public String forgetman(String a, String b) {//�Y�ꂽ�l�Ƀp�X���[�h��Ԃ��B//���O�ƍ����t����


			String key1 = hashD1.get(a);

			String key2 = hashD2.get(b);

			if(key1 != null && key2 != null) {

				if(key1.equals(key2)) {

					return key1;

				} else {

					return null;

				}

			} else {

				return null;

			}

		}


	public User obj(String name) {//user�̃I�u�W�F�N�g�����A�ʂɃt�@�C�����J���Ă��������A�t�@�C���͂Ȃ�ׂ��J�������Ȃ�����n�b�V���ŏ���



		User u;
		try {
		FileInputStream inFile1 = new FileInputStream("User.obj");
		ObjectInputStream inObject1 = new ObjectInputStream(inFile1);



		try {

		while(true) {
			u= (User)inObject1.readObject();
			
			if(u.getName().equals(name)) {
			return u;
			}
		}
		}catch(java.io.EOFException e) {



			inObject1.close();
			inFile1.close();

		}
		} catch (IOException e1) {
			// TODO �����������ꂽ catch �u���b�N
			e1.printStackTrace();


		} catch (ClassNotFoundException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
		return null;

		

	}
	/////////////////////////////�T�[�o�[�p�̃��\�b�h//////////////////////////////////////////////
	public void Uhyouzi() {//���݂̓o�^���Ă���A�J�E���g���m�F
		User u;
		try {
		FileInputStream inFile1 = new FileInputStream("User.obj");
		ObjectInputStream inObject1 = new ObjectInputStream(inFile1);



		try {

		while(true) {
		u= (User)inObject1.readObject();
		System.out.println(u.getName()+","+u.getPassword()+","+u.getAikotoba()+","+u.getBelong()+","+u.getJob()+","+u.getValue()+","+u.getQuestion()+","+u.getAnswer()+","+u.getGroup());
		}

		}catch(java.io.EOFException e) {



			inObject1.close();
			inFile1.close();

		}
		} catch (IOException e1) {
			// TODO �����������ꂽ catch �u���b�N
			e1.printStackTrace();


		} catch (ClassNotFoundException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}

	//�����ɂ͓��B�ł��Ȃ��B



	}
	public void Ghyouzi() {//���݂̓o�^���Ă���O���[�v���m�F
		Group g;
		try {
		FileInputStream inFile1 = new FileInputStream("Group.obj");
		ObjectInputStream inObject1 = new ObjectInputStream(inFile1);



		try {

		while(true) {
		g= (Group)inObject1.readObject();
		System.out.println(g.getgname());
		}

		}catch(java.io.EOFException e) {



			inObject1.close();
			inFile1.close();

		}
		} catch (IOException e1) {
			// TODO �����������ꂽ catch �u���b�N
			e1.printStackTrace();


		} catch (ClassNotFoundException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}

	//�����ɂ͓��B�ł��Ȃ��B



	}


	public void ban(User u) {//�A�J�E���gBan�p
		udelete(u);
	}



	///////////////////////////////////



	/////////////main���\�b�h/////////////////////////////////////////////////
public static void main(String[] args) { //main

		Ser5 server = new Ser5(10030); //�҂��󂯃|�[�g10000�ԂŃT�[�o�I�u�W�F�N�g������


		try {


		 FileOutputStream outFile = new FileOutputStream("User.obj");
		 ObjectOutputStream outObject = new ObjectOutputStream(outFile);
         User u=new User("dammy","dammy","���");
         outObject.writeObject(u);
		 outObject.close();
         outFile.close();

         FileOutputStream outFile1 = new FileOutputStream("Group.obj");
		 ObjectOutputStream outObject1 = new ObjectOutputStream(outFile1);
         Group g=new Group("dammy","dammy",u);
         outObject1.writeObject(g);
		 outObject1.close();
         outFile1.close();



         FileOutputStream outFile2 = new FileOutputStream("Question.obj");
		 ObjectOutputStream outObject2 = new ObjectOutputStream(outFile2);
         Question q=new Question(u, "dammy","dammy",0);
         outObject2.writeObject(q);
		 outObject2.close();
         outFile2.close();




		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}

		server.acceptClient(); //�N���C�A���g�󂯓�����J�n
}

}

