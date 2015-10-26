package gomoku;

public class TTEntry{
	public Piece eval;
	public int type;
	public static int EXACT=1;
	public static int LOWERBOUND=2;
	public static int UPPERBOUND=3;	
	//depth shouldn't matter for gomoku? as long as the board is the same they should eval to the same value
	public int depth;

	public TTEntry(Piece eval,int depth,int flag){
		this.eval=eval;
		this.type=flag;
		this.depth=depth;		
	}
}
