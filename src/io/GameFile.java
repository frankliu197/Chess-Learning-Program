package io;

import java.util.ArrayList;

import chessgame.ChessBoard;
import chessgame.Move;
import pieces.Piece;

/**
 * This is the class for GameFile
 * @author frankliu197
 * @since October 23
 */
public class GameFile extends AbstractGameFile {
	
	/**
	 * Constructor method for gameFile<br>
	 * NOTE: used fileOrganizer to write this object to file
	 * 
	 * @param chessFile (ChessFile) - the corresponding chessFile of this game file
	 */
	protected GameFile(ChessFile chessFile) {
		super(chessFile);
	}
	
	@Override
	public Piece[][] getStartingPosition() {
		return ChessBoard.getDefaultBoard();
	}

	@Override
	public SpecificFileIterator getSpecificFileIterator() {
		return new GameFileIterator();
	}

	@Override
	public void resetInformation(Piece[][] startingPosition, ArrayList<Move> moveHistory, Piece[][] endingPosition) {
		setMoveHistory(moveHistory);
	}

	/**
	 * This is an iterator for gameFile
	 *
	 */
	public class GameFileIterator extends AbstractGameFileIterator {
		
		/**
		 * Default constructor method that creates this Iterator with no special properties
		 */
		private GameFileIterator(){
			
		}
		
		@Override
		public void addStartingPosition(Piece piece, int[] position) {
			throw new IllegalStateException();
		}
		
		@Override
		public void removeStartingPosition(int[] position) {
			throw new IllegalStateException();

		}
	}//end of GameFileIterator
}//end of GameFile
