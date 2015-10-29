package gomoku;

import java.util.Arrays;

//the state of the board


public class State{
	private int board[][]=new int[15][15];

	public State(int b[][]){
		//board= new int[15][];
		for (int i=0;i<15;i++){
			for (int j=0;j<15;j++){
				board[i][j]=b[i][j];
			}
		}
	}
	
	//Eclipse generated hashCode does not use deepHashCode for multi dimension array..
	@Override
	public int hashCode(){
		//System.out.println("hash "+Arrays.deepHashCode(board));
		return Arrays.deepHashCode(board);
	}
	@Override
	public boolean equals(Object obj){
		
		if (this==obj)
			return true;
		
		if (obj==null)
			return false;
		if (getClass()!=obj.getClass())
			return false;
		
		
		State other=(State)obj;
		if (!Arrays.deepEquals(board,other.board))
			return false;
		return true;
	}
}
