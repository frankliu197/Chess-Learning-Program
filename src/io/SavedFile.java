package io;

import java.util.ArrayList;

import chessgame.ChessBoard;
import chessgame.Move;
import io.AbstractGameFile.AbstractGameFileIterator;
import pieces.Piece;
import pieces.Turn;

/**
 * This saves a position in a GAME
 * @author frankliu197
 */
public class SavedFile extends AbstractGameFile {
	/**The ending position of the Saved File*/
	private Piece[][] endingPosition;
	
	/**
	 * Creates a new saved file associated with the chessFile given.<br>
	 * NOTE: used fileOrganizer to write this object to file
	 * @param chessFile (ChessFile) - The chessFile that contains this saved File
	 */
	protected SavedFile(ChessFile chessFile) {
		super(chessFile);
	}
	
	
	@Override
	public SpecificFileIterator getSpecificFileIterator() {
		return new SavedFileIterator();
	}


	@Override
	public Piece[][] getEndingPosition() {
		return ChessBoard.clonedBoard(endingPosition);
	}

	@Override
	public Piece[][] getStartingPosition(){
		return ChessBoard.getDefaultBoard();
	}

	@Override
	public void resetInformation(Piece[][] startingPosition, ArrayList<Move> moveHistory, Piece[][] endingPosition) {
		setMoveHistory(moveHistory);
		this.endingPosition = endingPosition;
	}
	
	/**
	 * This is an iterator for SavedFile (it is the same thing as GameFileIterator)
	 *
	 */
	public class SavedFileIterator extends AbstractGameFileIterator {
		
		/**
		 * Default constructor method that creates this Iterator with no special properties
		 */
		private SavedFileIterator(){
			
		}
		
		@Override
		public void addStartingPosition(Piece piece, int[] position) {
			throw new IllegalStateException();
		}
		
		@Override
		public void removeStartingPosition(int[] position) {
			throw new IllegalStateException();
		}
	}//end of SavedFileIterator
	
}//end of SavedFile
