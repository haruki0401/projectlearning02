package othello;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
//import java.util.ArrayList;
import java.util.HashMap;

public class Server {
	private int MAX=100; 										    /*  �ő�ڑ���  */
	private int port; 											    /*  �|�[�g�ԍ�  */
	private boolean [] online; 									/*  �N���C�A���g�ڑ����  */
	private int sum_of_threadNum=0;									/*  �g���Ă���X���b�h�̐�  */
	private int p=1;
	PlayerArrayList<Player> game_online_list = new PlayerArrayList<Player>(); 	/*  �ΐ�҂���Ԃ�ArrayList  */
	Receiver receiveThread[] = new Receiver[MAX];                   /*  ��M�N���X�z��A�X���b�h�̔z��  */

	//�R���X�g���N�^�Ń|�[�g�ԍ���ݒ�
	public Server(int port) {
		this.port = port;
		online = new boolean[MAX];                                 /*  �I�����C����ԊǗ��p�z���p��  */

	}

	/**********************�f�[�^��M�p�X���b�h(�����N���X)**********************/
	class Receiver extends Thread{
		private InputStreamReader sisr;                            /*  ��M�f�[�^�p�����X�g���[��  */
		private BufferedReader br;                                 /*  �����X�g���[���p�̃o�b�t�@  */
		private PrintWriter out;                                   /*  �f�[�^���M�p�I�u�W�F�N�g    */
		private ObjectInputStream ois;                             /*  �I�u�W�F�N�g���̓X�g���[��  */
		private ObjectOutputStream oos;                            /*  �I�u�W�F�N�g�o�̓X�g���[��  */
		private int ThreadNo;                                      /*  �X���b�h�ԍ�(�v���C�������ʂ��邽��)  */
		private Player player = new Player("dammy", "dammy");
		private String user_name;                                  /*  ���̃X���b�h�𗘗p���Ă��郆�[�U�� */
		private boolean running=true;
		Socket socket;
		HashMap<Player,Player> map = new HashMap<Player,Player>(); /*  �ΐ풆�̑���ƕR�Â��邽�߂�HashMap  */

		//�����N���XReceiver�̃R���X�g���N�^
		Receiver (Socket socket, Server server, int ThreadNo){
			try{
				this.socket=socket;
				this.ThreadNo = ThreadNo;
				sisr = new InputStreamReader(socket.getInputStream());
				br = new BufferedReader(sisr);
				out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

				System.out.println(ThreadNo+"�����������܂���");
			} catch (IOException e) {
				System.err.println("�f�[�^��M���ɃG���[���������܂���: " + e);
			}
		}

		public void run(){
			try{
				/*  �f�[�^����M��������  */
				while(running) {
					String inputLine = br.readLine();
					if (inputLine != null){                       /*  �擪���b�Z�[�W�����ă��\�b�h���Ă�  */

						/* ���O�C���F�؂̃��N�G�X�g�Ȃ� */
						if(inputLine.equals("loginRequest")) {
							user_name = br.readLine();
							String password = br.readLine();                  	    /* user���ƃp�X���[�h���󂯎�� */
							String msg = loginCheck(user_name,password,ThreadNo);  /* ���O�C���ł��邩�m�F��     */
							out.println(msg);
							out.flush();										    /*���O�C�����ʂ��N���C�A���g�ɑ��M*/

							/* ���O�C���ł����Ȃ�,�I�����C����Ԃɂ��� */
							if(msg.equals("permit")) {
								online[ThreadNo]=true;
							}
							else if(msg.equals("notPermit")) {
								running = false;
								//p--;
							}

						}

						else if(inputLine.equals("logout")) {
							running=false;
							;//p--;
						}

						/*  �A�J�E���g�쐬�̃��N�G�X�g�Ȃ�  */
						else if(inputLine.equals("accountRequest")) {
							user_name = br.readLine();
							String password = br.readLine();                         /*���[�U���ƃp�X���[�h���󂯎��*/
							String msg = accountCreate(user_name, password,ThreadNo);/*�A�J�E���g�쐬���ł��邩�m�F��*/
							out.println(msg);
							out.flush();                                              /*���ʂ��N���C�A���g�ɑ��M*/

						    /* �쐬�ł���Ȃ�A�I�����C����Ԃɒǉ�����  */
							if(msg.equals("permit")) {
								online[ThreadNo]=true;
							}
							else if(msg.equals("notPermit")) {
								running = false;
								//p--;
							}

						}

						/*  �ΐ퐬�т̃��N�G�X�g�Ȃ�  */
						else if(inputLine.equals("myPlayerRequest")) {
								Player player = playerInfo(user_name);    /*  ���[�U����p���ăt�@�C������T��  */
								player.ThreadNo=ThreadNo;
								oos = new ObjectOutputStream(socket.getOutputStream());
								oos.writeObject(player);
								oos.flush();                               /*  �Y���I�u�W�F�N�g���N���C�A���g�ɑ��M  */
						}

						/*  �΋Ǒ҂���Ԃ���home��ʂɖ߂�����  */
						else if(inputLine.equals("returnHome")) {
							game_online_list.remove(player);   /*  �҂���ԃ��X�g����O��  */
							//game_online_flag=false;           /*  flag�̏�Ԃ�ς���     */
						}

						/*  �΋Ǒ҂���ԃ��X�g�̃��N�G�X�g�Ȃ�  */
						else if(inputLine.equals("otherPlayerRequest")){

							/*  1��ڂ̗v���Ȃ�  */

								game_online_list.add(player);      /*  �������g��҂���ԃ��X�g�ɓ��� */
								oos.reset();
								out.flush();
								out.println(String.valueOf(game_online_list.size()));
								out.flush();

								for(Player p:game_online_list) {
									oos.writeObject(p); /*  �N���C�A���g�Ƀ��X�g�𑗐M     */
									oos.flush();
								}

								out.println(String.valueOf(game_online_list.size()));  /*  ���X�g�̃T�C�Y�����M  */
								out.flush();
								//game_online_flag=true;


							/*  update�ɂ��đ��M�̃��N�G�X�g�Ȃ�  */

						}
						else if(inputLine.equals("otherPlayerRequestupDate")){

							/*  1��ڂ̗v���Ȃ�  */


							/*  update�ɂ��đ��M�̃��N�G�X�g�Ȃ�  */

								out.println("upDate");
								out.flush();
								System.out.println("updata�I�u�W�F�N�g���M�O"+game_online_list.size());

								out.flush();
								out.println(String.valueOf(game_online_list.size()));
								out.flush();

								System.out.println(String.valueOf(game_online_list.size()));

								oos.reset();
								for(Player p:game_online_list) {
									oos.writeObject(p); /*  �N���C�A���g�Ƀ��X�g�𑗐M     */
									oos.flush();
									System.out.println(p.getName());
								}

								out.println(String.valueOf(game_online_list.size()));
								out.flush();

						}

						/*  ���҂ւ̑΋ǐ\�����݂Ȃ�  */
						else if(inputLine.equals("requestGame")){

							String opponentstring = String.valueOf(br.read());
							int opponent = Integer.parseInt(opponentstring)-48;  /*  �΋ǎ҂�ThreadNo��  */
							String applier = String.valueOf(br.read());/*  ���g��list�ԍ����󂯎�� */
							String syoriyou = String.valueOf(br.read());
							String syoriyou2 = String.valueOf(br.read());
							String syoriyou3 = String.valueOf(br.readLine());
							String name = String.valueOf(br.readLine());
							System.out.println("name : " + name);
							String rate = String.valueOf(br.readLine());
							System.out.println("rate : " + rate);
							map.put(player, receiveThread[opponent].player);        /* HashMap�ł��݂�������  */
							receiveThread[opponent].map.put(receiveThread[opponent].player, player);
							requestGame(opponent,applier,name,rate);    /* �ΐ��\������ */
							System.out.println("���葤�ɐ\�����ݑ��M����");
						}

						/*  �\�����ɑ΂��铚���Ȃ�  */
						else if(inputLine.equals("Answer")) {
							String ans = br.readLine(); /* �������󂯎��A�\�����݌��ɑ��M  */	
							String name = br.readLine();
							
							if(ans.equals("Yes")) {
								receiveThread[map.get(player).ThreadNo].sendMessage("Answer");
								receiveThread[map.get(player).ThreadNo].sendMessage("Yes");
								receiveThread[map.get(player).ThreadNo].sendMessage(name);
							//	game_online_list.remove(player);
							//	game_online_list.remove(map.get(player));
							}
							else if(ans.equals("No")) {
								receiveThread[map.get(player).ThreadNo].sendMessage("Answer");
								receiveThread[map.get(player).ThreadNo].sendMessage("No");
								receiveThread[map.get(player).ThreadNo].map.remove(receiveThread[map.get(player).ThreadNo].player);
								map.remove(player);
							}
						}

						/*  �΋ǒ��̃f�[�^�]���̃��N�G�X�g�Ȃ�  */
						else if(inputLine.equals("forwardMessage")) {
							String color = br.readLine();
							String operation = br.readLine();
							forwardMessage(color, operation, map.get(player).ThreadNo); //��������ɓ]��
						}

						/*  �f�[�^�X�V�̃��N�G�X�g�Ȃ�  */
						else if(inputLine.equals("dataUpdate")){

							String user_name = br.readLine();
							String result    = br.readLine();
							String rate = br.readLine();
							dataUpdate(user_name,result,rate);
						}
					}
				}
			} catch (IOException e){ // �ڑ����؂ꂽ�Ƃ�
				System.err.println("�v���C�� " + ThreadNo + "�Ƃ̐ڑ����؂�܂����D");
     		}finally {
				try{
					game_online_list.remove(game_online_list.indexOf(player)); /*  �΋Ǒ҂���ԃ��X�g����폜����  */
				}catch(Exception ex) {
				}
				online[ThreadNo] = false;                                 /*  �v���C���̐ڑ���Ԃ��X�V����    */
				printStatus();                                            /*  �ڑ���Ԃ��o�͂���              */
				receiveThread[ThreadNo]=null;                            /*  ����Thread���󂯂�              */
				if(sum_of_threadNum==ThreadNo) {
					sum_of_threadNum--;                                      /*  �g���Ă�X���b�h�������炷      */
					System.out.println("decrement");
				}
			}
		}

		/*  �Ή�����N���C�A���g�Ƀ��b�Z�[�W�𑗐M  */
		public synchronized void sendMessage(String message) {
		    out.println(message);
		    out.flush();
		  }

	}
	/**********************************��M�X���b�h(�����N���X)�̏I���*******************************/


/****************************************���\�b�h**************************************************/

	/* �N���C�A���g�̐ڑ�(�T�[�o�̋N��) */
	public void acceptClient(){
		try {
			System.out.println("�T�[�o���N�����܂����D");
			ServerSocket ss = new ServerSocket(port); /* �T�[�o�\�P�b�g��p�� */
			while (true) {

				Socket socket = ss.accept();        /* �V�K�ڑ����󂯕t����     */

				for (p = 1; p < MAX+1; p++){        /* �ڑ��`�F�b�N */
					if (receiveThread[p] == null)   /* �󂢂Ă���ꍇ */
			            break;
			    }

				if (p == MAX+1)                     /* �󂢂Ă��Ȃ��ꍇ */
			    	continue;                    /* �ȉ��̏��������Ȃ� */

				System.out.println("start Thread"+p);
				receiveThread[p] = new Receiver(socket, this, p);
		        receiveThread[p].start( );
		        sum_of_threadNum++;                  /* �g���Ă���X���b�h�̐� */
		     }
		}catch (Exception e) {
			System.err.println("�\�P�b�g�쐬���ɃG���[���������܂���: " + e);
		}
	}

	/* ���O�C���F�� */
	public String loginCheck(String user_name, String password, int ThreadNo) {
		try{
			Player player;
			//FileInputStream�I�u�W�F�N�g�̐���
			FileInputStream inFile = new FileInputStream("players.obj");

            try{
            	while(true){
            		//ObjectInputStream�I�u�W�F�N�g�̐���
            		ObjectInputStream inObject = new ObjectInputStream(inFile);
            		player = (Player)inObject.readObject(); /*�I�u�W�F�N�g�̓ǂݍ���*/

            		/* �����A���[�U���ƃp�X���[�h����v����Ȃ� */
            		if(player.getName().equals(user_name) && player.getPassword().equals(password)){
            			player.setThreadNo(ThreadNo);
            			receiveThread[ThreadNo].player=player; /*����player�I�u�W�F�N�g��Thread�ɕۑ�*/
            			inObject.close();
            			System.out.println("login permit");
            			return "permit"; /*���O�C����������*/
            		}

            	}
            }catch(EOFException e){
    		}
		}catch(Exception e){
		}

   		/*��v���Ȃ�������A�����Ȃ�*/
		System.out.println("No permit");
		return "notPermit";

	}

	//�A�J�E���g�쐬
	public String accountCreate(String user_name, String password, int ThreadNo) {
		try {
			Player player;
			//FileInputStream�I�u�W�F�N�g�̐���
            FileInputStream inFile = new FileInputStream("players.obj");

            try{
            while(true){
            	//ObjectInputStream�I�u�W�F�N�g�̐���
            	ObjectInputStream inObject = new ObjectInputStream(inFile);
            	player = (Player)inObject.readObject(); //�I�u�W�F�N�g�̓ǂݍ���
            	System.out.println(player.getName());

            	//�����A�������O�̃��[�U�����ɑ��݂��Ă���Ȃ�
            	if(player.getName().equals(user_name)){
            		inObject.close();
            		System.out.println("false");
            		return "notPermit"; //�A�J�E���g�쐬��s���Ƃ���
            	}
             }
            }catch(EOFException e){
    		}

            /*   ���������O�̏d�����Ȃ��A�V�K�A�J�E���g���쐬�ł���Ȃ火����   */

            //FileOutputStream�I�u�W�F�N�g�̐���
            FileOutputStream outFile = new FileOutputStream("players.obj",true);
            //ObjectOutputStream�I�u�W�F�N�g�̐���
            ObjectOutputStream outObject = new ObjectOutputStream(outFile);

            //�N���XPlayer�̃I�u�W�F�N�g�̏�������
            player = new Player(user_name,password);
            outObject.writeObject(player);
            receiveThread[ThreadNo].player=player;
            outObject.close();
       }
       catch(Exception e) {
    	   e.printStackTrace();
       }

		/*�A�J�E���g�쐬�̋���*/
		System.out.println("true");
        return "permit";

	}

	/* Player�̑ΐ퐬�т𑗐M */
	public Player playerInfo(String user_name) {
		//Player�I�u�W�F�N�g���i�[����ϐ�
		Player player = new Player("dammy", "dammy");

        try{
        	//FileInputStream�I�u�W�F�N�g�̐���
            FileInputStream inFile = new FileInputStream("players.obj");

            //�Y������I�u�W�F�N�g��T��
            while(true){
        	//ObjectInputStream�I�u�W�F�N�g�̐���
        	ObjectInputStream inObject = new ObjectInputStream(inFile);
        	player = (Player)inObject.readObject(); //�ǂݍ���

        		if(player.getName().equals(user_name)){
        			inObject.close();
        			break;
        		}

            }
        }
        catch(Exception e){
		}

        return player; //�I�u�W�F�N�g�����^�[���A�N���C�A���g�֑���B

	}

	/*�N���C�A���g�ڑ���Ԃ̏o��*/
	public void printStatus(){
		int i=0;
		boolean flag = false;
		while(i<=sum_of_threadNum) {
			if(online[i]==true) {
				System.out.println
				("���[�U��"+receiveThread[i].player.getName()+"�̓I�����C����Ԃł�");
				flag=true;
			}
			System.out.println(i);
			i++;
		}

		if(!(flag))
				System.out.println("�I�����C����Ԃ�Player�͂��܂���");

	}

	public void dataUpdate(String user_name, String result, String rate) {
		Player player;
		ObjectInputStream inObject;
		PlayerArrayList<Player> arr = new PlayerArrayList<Player>();
        try{
        	/*  FileInputStream�I�u�W�F�N�g�̐���  */
            FileInputStream inFile = new FileInputStream("players.obj");

            /*�f�[�^�X�V������I�u�W�F�N�g��T��*/
            while(true){
            	inObject = new ObjectInputStream(inFile);
            	player = (Player)inObject.readObject(); /* EIEuEWEFENEgC?i��C?cuC?  */

            	if(player.getName().equals(user_name)){
            		if(result.equals("WIN")) {
            			player.setWin(player.getWin()+1);
            		}else if(result.equals("LOSE")) {
            			player.setDefeat(player.getDefeat()+1);
            		}else if(result.equals("DRAW")) {
            			player.setDraw(player.getDraw()+1);
            		}
            		player.setRate(Double.valueOf(rate));
            	}
            	arr.add(player);  /* 1�x���ׂẴI�u�W�F�N�g�t�@�C������ǂݍ��݁A���X�g�Ƃ��� */
            }
         }catch(Exception e){
         }

        /*�I�u�W�F�N�g��ۑ�*/
        try{
           	boolean flag=false;
            for(Player p : arr){

            	/*��ڂ̃I�u�W�F�N�g�̕ۑ��́A�t�@�C����V�������Ă���ۑ�*/
            	if(flag==false){
                    FileOutputStream outFile1 = new FileOutputStream("players.obj");  /*�������Ȃ�*/
                   	ObjectOutputStream outObject1 = new ObjectOutputStream(outFile1);
            		outObject1.writeObject(p);
            		flag=true;
            	}

            	else{

                    FileOutputStream outFile2 = new FileOutputStream("players.obj",true);
                   	ObjectOutputStream outObject2 = new ObjectOutputStream(outFile2);
                    outObject2.writeObject(p);
            	}

            }
        }catch(Exception e){
        }
	}

	/*  �΋ǐ\�����ݓ]��  */
	public void requestGame(int opponent,String applier_list_num,String name,String rate){
		receiveThread[opponent].sendMessage("requestGame");
		receiveThread[opponent].sendMessage(applier_list_num);
		receiveThread[opponent].sendMessage(name);
		System.out.println("name : " + name);
		receiveThread[opponent].sendMessage(rate);
		System.out.println("rate : " + rate);
	}

	/*  �������]��  */
	public void forwardMessage(String msg1, String msg2, int ThreadNo) {
		receiveThread[ThreadNo].sendMessage("forwardMessage");
		receiveThread[ThreadNo].sendMessage(msg1);
		receiveThread[ThreadNo].sendMessage(msg2);
		System.out.println("�X���b�h�i���o�[" + ThreadNo + "�ɑ��M���܂���");
	}

	public static void main(String[] args) {
		Server server = new Server(10027);
		server.acceptClient(); /*  �N���C�A���g�󂯓�����J�n  */
	}

}
