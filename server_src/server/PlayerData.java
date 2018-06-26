package server;


public class PlayerData {
	// データ類
	private String id="";
	private String pass="";
	private int numWin = 0;
	private int numDraw = 0;
	private int numLose = 0;
	private int numEarlyLose = 0;


	//コンストラクタ

	// ログイン前に通信切れ用
	PlayerData(){}
	// 新規作成用
	PlayerData(String id, String pass){
		this.id = id; this.pass = pass;
	}
	// ログイン用
	PlayerData(String str){

		String[] data=str.split("/");

		this.id=data[0];
		this.pass=data[1];
		this.numWin=Integer.parseInt(data[2]);
		this.numDraw=Integer.parseInt(data[3]);
		this.numLose=Integer.parseInt(data[4]);
		this.numEarlyLose=Integer.parseInt(data[5]);

		System.out.println("id"+id);

	}

	public String getPlayerData(){
		return new String(id + '/' + numWin + '/' + numDraw + '/' + numLose +  '/' + numEarlyLose);
	}

	public String getID() {
		return id;
	}

	public void incrementWin() {
		numWin++;
	}
	public void incrementDraw() {
		numDraw++;
	}

	public void incrementLose() {
		numLose++;
	}

	public void incrementEarlyLose() {
		numEarlyLose++;
	}


	// ファイルに書き出す際の文字列を作る関数
	public String fileOutStr(){
		//'/' = '/';
		return new String(id + '/' + pass + '/' + numWin + '/' + numDraw + '/' + numLose + '/' + numEarlyLose);
	}

}
