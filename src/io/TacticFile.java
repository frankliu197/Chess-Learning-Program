package io;

import java.util.ArrayList;

import chessgame.ChessBoard;
import chessgame.Move;
import pieces.Piece;

/**
 * This is a tactic File (Stores tactics)
 * 
 * @author Frank and Robert
 * @since October 13, 2016
 */
public class TacticFile extends AbstractGameFile{
	/** The starting position of the TacticFile */
	private Piece[][] startingPosition = ChessBoard.getDefaultBoard();
	
	/**
	 * Constructor for TacticFile<br>
	 * NOTE: used fileOrganizer to write this object to file
	 * @param chessFile (ChessFile) - the location of the corresponding chessFile
	 * 
	 */
	protected TacticFile(ChessFile chessFile) {
		super(chessFile);
	}

	@Override
	public SpecificFileIterator getSpecificFileIterator() {
		return new TacticFileIterator();
	}

	@Override
	public Piece[][] getStartingPosition() {
		return ChessBoard.clonedBoard(startingPosition);
	}
	
	@Override
	public void resetInformation(Piece[][] startingPosition, ArrayList<Move> moveHistory, Piece[][] endingPosition) {
		this.startingPosition = startingPosition;
		setMoveHistory(moveHistory);
	}
	
	/**
	 * TacticFileIterator iterates through a tactic file
	 */
	public class TacticFileIterator extends AbstractGameFileIterator{

		/**
		 * Default constructor method that creates this Iterator with no special properties
		 */
		private TacticFileIterator(){
			
		}
		
		@Override
		public void addStartingPosition(Piece piece, int[] position) {
			if (startingPosition[position[0]][position[1]] == null){
				startingPosition[position[0]][position[1]] = piece;
			} else {
				throw new IllegalArgumentException();
			}
		}//end of addStartingPostion method

		@Override
		public void removeStartingPosition(int[] position) {
			if (startingPosition[position[0]][position[1]] != null){
				startingPosition[position[0]][position[1]] = null;
			} else {
				throw new IllegalArgumentException();
			}
		}//end of removeStartingPosition method		
	}//end of TacticFileIterator class
}//end of TacticFile class
