import java.awt.Button;
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

import android.app.Activity;
import android.app.Application;
import android.os.AsyncTask;
import android.widget.TextView;

public class Client extends Application{
	private Socket soc;
	private PrintWriter out;
	private BufferedReader in;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private User myuser;
	String ipadress;
	int poat;

	UserSupport us;
	Activity activity;
	boolean f;

	public void onCreate(){
		super.onCreate();
		myuser = new User();
	}

	public void connectServer(String ipAddress,String port, final Activity activity) {		//�T�[�o�Ƃ̐ڑ�
        this.acivity = activity;
        final String ipaddress = ipaddress;
        //final int portnum = Integer.parseInt(port);
        new AsyncTask<String, Void, String>(){

        	@Override
        	protected String doInBackground(String... strings){
        		try {
        			soc = new Socket(strings[0], Integer.parseInt(strings[1]));
       			}catch(UnknownHostException e) {
       				System.out.println("�z�X�g�ɐڑ��ł��܂���B");
           			System.out.println(e);
           			return "�z�X�g�ɐڑ��ł��܂���";
        		}catch(IOException e) {
        			System.out.println("�T�[�o�[�ڑ����ɃG���[���������܂����B");
        			System.out.println(e);
        			return "�T�[�o�ڑ����ɃG���[���������܂���";
        		}

        		return "�ڑ�����";
        	}

        	@Override
        	protected void onPostExecute(String s){
        		super.onPostExecute(s);
        		TextView tv = (TextView)activity.findViewById(R.id.result_text);
        		Button loginbutton = (Button)activity.findViewById(R.id.go_login_button);
        		tv.setText(s);
        		if(s.equals("�ڑ�����")){
        			loginbutton.setVisibility(loginbutton.VISIBLE);
        		}
        	}
        }.execute(ipAddress, port);
	}

	public boolean loginRequest(String userName, String password, final Activity activity) { //���O�C���v��
        this.activity = activity;
        f = true;
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {
                try{
                    in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                    out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
                    out.println("�F��");//�����t
                    out.println(strings[0]);
                    out.println(strings[1]);
                    out.flush();
                    String isPer = in.readLine();
                    System.out.println(isPer);
                    if (isPer.equals("lture") == true) {  //���O�C���F�؂��ꂽ
                        f = true;
                        return "���O�C������";
                    } else if (isPer.equals("lfalse") == true) {  //���O�C���F�؂���Ȃ�����
                        f = false;
                        return "���O�C�����s";
                    } else {
                        System.out.println("�F�؂Ƃ͕ʂ̕�����ł��B");  //�ʂ̕����񂪑����Ă���
                        f = false;
                        return "���O�C�����s";
                    }
                    //�X�g���[�����N���[�Y����
                    //out.close();
                    //in.close();
                } catch(IOException e){
                    System.out.println(e);
                }
                f = false;
                return "���O�C�����s";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                if(s.equals("���O�C������")) {

                    Intent intent = new Intent(activity.getApplication(), HomeActivity.class);
                    startActivity(intent);
                }
            }
        }.execute(userName,password);
        return f;
    }

	public boolean accountRequest(String userName,String password, String ai, Activity activity) { //�A�J�E���g�쐬�v��
        this.activity = activity;
        final Activity act = activity;
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {
                try {
                    out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
                    in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                    out.println("�V�K�o�^");
                    out.println(strings[0]);
                    out.println(strings[1]);
                    out.println(strings[2]);
                    out.flush();
                    String isPer = in.readLine();
                    if (isPer.equals("rtrue") == true) {  //�V�K�쐬�ł���
                        return "�o�^����";
                    } else if (isPer.equals("rfalse") == true) {  //�V�K�쐬�ł��Ȃ�
                        return "�o�^���s";
                    } else {
                        System.out.println("�m�F�Ƃ͕ʂ̕�����ł��B");  //�ʂ̕����񂪑����Ă���
                    }
                    //out.close();
                    //in.close();
                }catch(IOException e){
                    System.out.println(e);
                }
                return "�o�^���s";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(s.equals("�o�^����")) {
                    Intent intent = new Intent(act.getApplication(), HomeActivity.class);
                    startActivity(intent);
                }
            }

        }.execute(userName, password, ai);
        return true;
    }

	public String remindPassword(String ai, Activity activity) {		//�p�X���[�h��Y�ꂽ�ꍇ�̗v��
		this.activity = activity;
        final Activity act = activity;
        String password;
        new AsyncTask<String, Void, String>() {
        	@Override
            protected String doInBackground(String... strings) {
        		try {
        			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
        			in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
        			out.println("�Y�ꂽ�l");
        			out.println(strings[0]);
        			out.flush();
        			String isPer = in.readLine();
        			if(isPer.equals("ftrue")==true) {
        				password = in.readLine();
        				return "�p�X���[�h�擾����";
        			}else if(isPer.equals("ffalse")){
        				return "�����t���Ⴂ�܂�";
        			}
        		} catch (IOException e) {
        			// TODO �����������ꂽ catch �u���b�N
        			e.printStackTrace();
        		}
        		return "�p�X���[�h�擾���s";
        	}

        	@Override
    		protected void onPostExecute(String s) {
    		}
        }.execute(ai);

        return password;
	}

	public void logoutRequest(final Activity activity) {		//���O�A�E�g
		this.activity = activity;
		final Activity act = activity;
		new AsyncTask<String, Void, String>(){
			@Override
			protected String doInBackground(String...strings) {
				try {
					out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
					out.println("���O�A�E�g");
					out.flush();
					return "���O�A�E�g����";
				} catch (IOException e) {
					// TODO �����������ꂽ catch �u���b�N
					e.printStackTrace();
				}
				return
			}
		}.execute();

		@Override
		protected void onPostExecute(String s) {
		}

	}

	public void sendUserInformation(String job, String belong, ArrayList<String> group, Activity activity){		//�A�J�E���g���]��
		this.activity = activity;
        final Activity act = activity;
        new AsyncTask<String, Void, String>() {
        	@Override
            protected String doInBackground(String... strings) {
        		try {
        			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
        			out.println("�l���");
        			//out.println(myuser.getName());		//������User�I�u�W�F�N�g���T�[�o�ŒT���Ă��炤���߂̃L�[(���O)�̓]��
        			out.println(strings[0]);
        			out.println(strings[1]);
        			out.println(group.size());
        			out.flush();
        			for(String s: group) {
        				out.println(s);
        				out.flush();
        			}
        			return "�ݒ芮��";
        		} catch (IOException e) {
        			// TODO �����������ꂽ catch �u���b�N
        			e.printStackTrace();
        		}
        		return "�ݒ莸�s";
        	}

        	@Override
    		protected void onPostExecute(String s) {
    		}

        }.execute(job, belong, group);

	}

	public boolean requestGroupSearch(String group, Activity activity) {		//�O���[�v�����v��
		this.activity = activity;
        final Activity act = activity;
        f = false;
        new AsyncTask<String, Void, String>() {
        	@Override
            protected String doInBackground(String... strings) {
        		try {
        			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
        			out.println("�O���[�v����");
        			out.println(strings[0]);
        			out.flush();
        			in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
        			if(in.readLine().equals("gtrue")) {
        				f = true;
        				return "�O���[�v�L��";		//�Y������O���[�v�������
        			}
        			else if(in.readLine().equals("gfalse")) {
        				f = false;
        				return "�O���[�v����";		//�Y������O���[�v���Ȃ����
        			}
        			else {												//�\�����ʕ����񂪑���ꂽ��
        				f = false;
        				System.out.println("�Ⴄ�����񂪑����܂����B");
        				return "�������s";
        			}
        		} catch (IOException e) {
        			// TODO �����������ꂽ catch �u���b�N
        			e.printStackTrace();
        		}
        	}

        	@Override
    		protected void onPostExecute(String s) {
    		}

        }.execute(group);

		return f;
	}

	public void receiveUserInformation(String name, Activity activity) {		//�����̃A�J�E���g�����擾
		this.activity = activity;
        final Activity act = activity;
		User u;
		new AsyncTask<String, Void, String>() {
			@Override
            protected String doInBackground(String... strings) {
				try {
					out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
					out.println("���");
					out.println(strings[0]);		//�ق���User�I�u�W�F�N�g�̖��O�i���̏ꍇ�͎����̖��O�j
					ois = new ObjectInputStream(soc.getInputStream());
					u = (User) ois.readObject();		//�T�[�o����󂯎����������User�I�u�W�F�N�g
					myuser = u;
					return "�擾����";
				} catch (IOException e) {
					// TODO �����������ꂽ catch �u���b�N
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO �����������ꂽ catch �u���b�N
					e.printStackTrace();
				}
				return "�擾���s";
			}

			@Override
    		protected void onPostExecute(String s) {
    		}

		}.execute(name);

	}

	public boolean creatGroup(String group/*�O���[�v��*/, String explanation/*�O���[�v�̐���*/, Activity activity) {		//�O���[�v�V�K�쐬
		this.activity = activity;
        final Activity act = activity;
        f = false;
        new AsyncTask<String, Void, String>() {
        	@Override
            protected String doInBackground(String... strings) {
        		try {
        			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
        			out.println("�O���[�v�쐬");
        			out.println(strings[0]);
        			out.println(strings[1]);
        			out.flush();
        			in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
        			if(in.readLine().equals("gtrue")==true) {
        				f = true;
        				return "�쐬����";
        			}
        			else if(in.readLine().equals("gfalse")==true) {
        				f = false;
        				return "�O���[�v���ɑ���";
        			}
        		} catch (IOException e) {
        			// TODO �����������ꂽ catch �u���b�N
        			e.printStackTrace();
        		}
        		return "�쐬���s";
        	}

        	@Override
    		protected void onPostExecute(String s) {
    		}


        }.execute(group, explanation);

		return f;
	}

	public Group receiveQuestions(String group, Activity activity) {		//�O���[�v�������擾
		this.activity = activity;
        final Activity act = activity;
        Group g = null;
        new AsyncTask<String, Void, String>() {
        	@Override
            protected String doInBackground(String... strings) {
        		try {
        			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
        			out.println("�������̃O���[�v���");
        			out.println(group);
        			out.flush();
        			ois = new ObjectInputStream(soc.getInputStream());
        			g = (Group) ois.readObject();
        			return "�擾����";
        		} catch (IOException e) {
        			// TODO �����������ꂽ catch �u���b�N
        			e.printStackTrace();
        		} catch (ClassNotFoundException e) {
        			// TODO �����������ꂽ catch �u���b�N
        			e.printStackTrace();
        		}
        		return "�擾���s";
        	}

        	@Override
    		protected void onPostExecute(String s) {
    		}

        }.execute(group);
		return g;
	}

	public void sendQuestion(String question/*������e*/, String group/*����̑�����O���[�v*/,  String answerer/*�I�t�@�[���o������*/, int coin/*����ɂ�����R�C��*/, Activity activity) {		//����̑��M
		this.activity = activity;
        final Activity act = activity;
        new AsyncTask<String, Void, String>() {
        	@Override
            protected String doInBackground(String... strings) {
        		try {
        			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
        			out.println("������e");
        			out.println(strings[0]);				//������e�̑��M
        			out.println(strings[1]);					//����̑�����O���[�v���̑��M
        			if(answerer.equals(null)==false)sendOffer(strings[0], strings[2]);		//�I�t�@�[���肪�w�肳��Ă���ꍇ�A�I�t�@�[�𑗂�
        			out.println(strings[3]);					//����ɂ�����ꂽ�R�C�����̑��M
        			out.flush();
        			return "���␬��";
        		} catch (IOException e) {
        			// TODO �����������ꂽ catch �u���b�N
        			e.printStackTrace();
        		}
        		return "���⎸�s";
        	}

        	@Override
    		protected void onPostExecute(String s) {
    		}

        }.execute(question, group, answerer, coin);

	}

	public void sendOffer(String question/*������e*/, String answerer/*�I�t�@�[���o������*/, Activity activity) {		//�I�t�@�[�̑��M
		this.activity = activity;
        final Activity act = activity;
        new AsyncTask<String, Void, String>() {
        	@Override
            protected String doInBackground(String... strings) {
        		try {
        			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
        			out.println(strings[0]);		//�ǂ̎���ɑ΂���I�t�@�[�������ʂ��邽�߂̃L�[�i������e�j�̑��M
        			out.println(strings[1]);
        			out.flush();
        			return "�I�t�@�[����";
        		} catch (IOException e) {
        			// TODO �����������ꂽ catch �u���b�N
        			e.printStackTrace();
        		}
        		return "�I�t�@�[���s";
        	}

        	@Override
    		protected void onPostExecute(String s) {
    		}

        }.execute(question, answerer);

	}

	public ArrayList<Question> receiveAnswer(Activity activity) {		//��������������̏��̎擾�B�񓚁A�����ғ��̏��͂���������o���B
		this.activity = activity;
        final Activity act = activity;
		ArrayList<Question> myquestion = new ArrayList<Question>();
		new AsyncTask<String, Void, String>() {
			@Override
            protected String doInBackground(String... strings) {
				try {
					out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
					out.println("��������������");
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
					return "�擾����";
					//e.printStackTrace();
				}
				return "�擾���s";
			}

			@Override
    		protected void onPostExecute(String s) {
    		}

		}.execute();

		return myquestion;
	}

	public ArrayList<Question> receiveOffer(Activity activity) {		//�����ɗ��Ă���I�t�@�[�̎�M�i��̓I�ɂ́AQuestion�N���X�̑���offered�Ɏ����̖��O���Z�b�g����Ă���Question�I�u�W�F�N�g�̎�M�j
		this.activity = activity;
        final Activity act = activity;
		ArrayList<Question> offer = new ArrayList<Question>();
		new AsyncTask<String, Void, String>() {
			@Override
            protected String doInBackground(String... strings) {
				try {
					out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
					out.println("�I�t�@�[���Ă邩��");
					out.flush();
					ois = new ObjectInputStream(soc.getInputStream());
					while(true) {
						offer.add((Question)ois.readObject());
					}
				} catch (IOException e) {
					// TODO �����������ꂽ catch �u���b�N
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO �����������ꂽ catch �u���b�N
					return "�擾����";
				}
				return "�擾���s";
			}

			@Override
    		protected void onPostExecute(String s) {
    		}

		}.execute();

		return offer;
	}

	public ArrayList<Question> receiveCandidate(Activity activity){		//����������₵������̎�M
		this.activity = activity;
        final Activity act = activity;
		ArrayList<Question> cq = new ArrayList<Question>();
		new AsyncTask<String, Void, String>() {
			@Override
            protected String doInBackground(String... strings) {
				try {
					out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
					out.println("����₵�Ă鎿��");
					out.flush();
					ois = new ObjectInputStream(soc.getInputStream());
					while(true) {
						cq.add((Question)ois.readObject());
					}
				} catch (IOException e) {
					// TODO �����������ꂽ catch �u���b�N
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO �����������ꂽ catch �u���b�N
					return "�擾����";
					//e.printStackTrace();
				}
				return "�擾���s";
			}

			@Override
    		protected void onPostExecute(String s) {
    		}

		}.execute();

		return cq;
	}

	public void sendAnswer(String question/*������e*/, String answer/*�񓚓��e*/, Activity activity) {		//�񓚂̑��M
		this.activity = activity;
        final Activity act = activity;
        new AsyncTask<String, Void, String>() {
        	@Override
            protected String doInBackground(String... strings) {
        		try {
        			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
        			out.println("��");
        			out.println(strings[0]);
        			out.println(strings[1]);
        			out.flush();
        			return "���M����";
        		} catch (IOException e) {
        			// TODO �����������ꂽ catch �u���b�N
        			e.printStackTrace();
        		}
        		return "���M���s";
        	}

        	@Override
    		protected void onPostExecute(String s) {
    		}

        }.execute(question, answer);

	}

	public void Candidacy(String question/*������e*/, Activity activity) {		//�����
		this.activity = activity;
        final Activity act = activity;
        new AsyncTask<String, Void, String>() {
        	@Override
            protected String doInBackground(String... strings) {
        		try {
        			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
        			out.println("�񓚗����");
        			out.println(strings[0]);
        			out.flush();
        			return "����␬��";
        		} catch (IOException e) {
        			// TODO �����������ꂽ catch �u���b�N
        			e.printStackTrace();
        		}
        		return "����⎸�s";
        	}

        	@Override
    		protected void onPostExecute(String s) {
    		}

        }.execute(question);

	}

	public void cancelCandidacy(String question, Activity activity) {		//���������������̎�����
		this.activity = activity;
        final Activity act = activity;
        new AsyncTask<String, Void, String>() {
        	@Override
            protected String doInBackground(String... strings) {
        		try {
        			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
        			out.println("����������");
        			out.println(strings[0]);		//����������������̓��e�̑��M
        			out.flush();
        			return "����������";
        		} catch (IOException e) {
        			// TODO �����������ꂽ catch �u���b�N
        			e.printStackTrace();
        		}
        		return "���������s";
        	}

        	@Override
    		protected void onPostExecute(String s) {
    		}

        }.execute(question);

	}

	public void sendValue(String question/*������e*/, double value/*�]���l*/, Activity activity) {		//�]���l�̑��M
		this.activity = activity;
        final Activity act = activity;
        new AsyncTask<String, Void, String>() {
        	@Override
            protected String doInBackground(String... strings) {
        		try {
        			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
        			out.println("�]���l�ύX");
        			out.println(strings[0]);
        			out.println(strings[1]);
        			out.flush();
        			return "���M����";
        		} catch (IOException e) {
        			// TODO �����������ꂽ catch �u���b�N
        			e.printStackTrace();
        		}
        		return "���M���s";
        	}

        	@Override
    		protected void onPostExecute(String s) {
    		}

        }.execute(question, value);

	}

	public void cancelOffer(String question/*������e*/, Activity activity) {		//�I�t�@�[������
		this.activity = activity;
        final Activity act = activity;
        new AsyncTask<String, Void, String>() {
        	@Override
            protected String doInBackground(String... strings) {
        		try {
        			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
        			out.println("�I�t�@�[������");
        			out.println(strings[0]);
        			out.flush();
        			return "����������";
        		} catch (IOException e) {
        			// TODO �����������ꂽ catch �u���b�N
        			e.printStackTrace();
        		}
        		return "���������s";
        	}

        	@Override
    		protected void onPostExecute(String s) {
    		}

        }.execute(question);

	}

	public void refuseOffer(String question/*������e*/, Activity activity) {		//�I�t�@�[����
		this.activity = activity;
        final Activity act = activity;
        new AsyncTask<String, Void, String>() {
        	@Override
            protected String doInBackground(String... strings) {
        		try {
        			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
        			out.println("�I�t�@�[����");
        			out.println(strings[0]);
        			out.flush();
        			return "���ې���";
        		} catch (IOException e) {
        			// TODO �����������ꂽ catch �u���b�N
        			e.printStackTrace();
        		}
        		return "���ێ��s";
        	}

        	@Override
    		protected void onPostExecute(String s) {
    		}

        }.execute(question);

	}

	public String receiveMessage(Activity activity) {		//���b�Z�[�W��M
		this.activity = activity;
        final Activity act = activity;
		String mes = new String();
		new AsyncTask<String, Void, String>() {
			@Override
            protected String doInBackground(String... strings) {
				try {
					out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
					out.println("���b�Z�[�W��M");
					out.flush();
					in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
					mes = in.readLine();
					return "��M����";
				} catch (IOException e) {
					// TODO �����������ꂽ catch �u���b�N
					e.printStackTrace();
				}
				return "��M���s";
			}

			@Override
    		protected void onPostExecute(String s) {
    		}

		}.execute();

		return mes;
	}

	public void sendCoin(int coin, Activity activity) {		//����
		this.activity = activity;
        final Activity act = activity;
        new AsyncTask<String, Void, String>() {
        	@Override
            protected String doInBackground(String... strings) {
        		try {
        			out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
        			out.println("����");
        			out.println(strings[0]);
        			out.flush();
        			return "��������";
        		} catch (IOException e) {
        			// TODO �����������ꂽ catch �u���b�N
        			e.printStackTrace();
        		}
        		return "�������s";
        	}

        	@Override
    		protected void onPostExecute(String s) {
    		}

        }.execute(coin);

	}

	public int receiveCoin(Activity activity) {		//����
		this.activity = activity;
        final Activity act = activity;
		int c=0;
		new AsyncTask<String, Void, String>() {
			@Override
            protected String doInBackground(String... strings) {
				try {
					out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
					out.println("����");
					out.flush();
					in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
					c = Integer.parseInt(in.readLine());
					return "��������";
				} catch (IOException e) {
					// TODO �����������ꂽ catch �u���b�N
					e.printStackTrace();
				}
				return "�������s";
			}

			@Override
    		protected void onPostExecute(String s) {
    		}

		}.execute();

		return c;
	}

	public User getMyUser() {		//������User�I�u�W�F�N�g��Ԃ�
		return myuser;
	}

	public static void main(String[] args) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u

	}

}
