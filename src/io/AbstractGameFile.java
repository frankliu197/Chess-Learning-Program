package io;

import java.util.ArrayList;

import chessgame.ChessBoard;
import chessgame.Move;
import pieces.Piece;

/**
 * A class that stores all the moves in a certain game. Partially implements for its 
 * two subclasses: tacticsFile and gameFile
 * 
 * @author Frank 
 * @since October 10, 2016
 */

abstract class AbstractGameFile extends SpecificFile {
	/** The move History of the Game File */
	private ArrayList<Move> moveHistory = new ArrayList<>();
	
	/** The Notes of the Game File itself */
	private String gameFileNotes;

	/**
	 * Creates a new abstractGameFile file 
	 * NOTE: used fileOrganizer to write this object to file<br>
	 * @param chessFile (ChessFile) - the corresponding chessFile
	 */
	protected AbstractGameFile(ChessFile chessFile) {
		super(chessFile);
		this.moveHistory = new ArrayList<>();
	}

	@Override
	public ArrayList<Move> getMoveHistory() {
		return moveHistory;
	}
	
	@Override
	public Piece[][] getEndingPosition(){
		ChessBoard chessBoard = new ChessBoard(getStartingPosition(), getTurn());
		SpecificFileIterator iterator = getSpecificFileIterator();
		Move nextMove = iterator.toNextMove();
		while(nextMove != null){
			chessBoard.move(nextMove);
			nextMove = iterator.toNextMove();
		}
		return chessBoard.getBoard();
	}//end of getEndingPosition method
	
	/**
	 * Sets the moveHistory to the given parameter.<br>
	 * NOTE: this is passed by reference
	 * 
	 * @param moveHistory (ArrayList -- move) - moveHistory to change this values moveHistory to
	 */
	protected void setMoveHistory(ArrayList<Move> moveHistory){
		this.moveHistory = moveHistory;
	}
	
	/**
	 * This class iterates information in AbstractGameFile
	 */
	public abstract class AbstractGameFileIterator extends SpecificFileIterator {
		/** The current position that the iterator is on */
		private int moved = -1;

		@Override
		public void addMove(Move move) {
			if (nextMoveExists()){
				throw new IllegalStateException();
			}
			moved++;
			moveHistory.add(move);
		}

		@Override
		public Move deleteMove() {
			if (nextMoveExists()) {
				throw new IllegalStateException();
			}
			
			return deleteMovesRegardless();
		}

		@Override
		public Move deleteMovesRegardless() {
			Move toReturn = undo();
			
			for (int i = moveHistory.size() - 1; i > moved; i--) {			
				moveHistory.remove(i);
			}
			
			if (moved >= 0){
				moved--;
			}
			
			return toReturn;
		}

		@Override
		public Move getMoveDone() {
			if (moved != -1) {
				return moveHistory.get(moved);
			}
			return null;
		}

		@Override
		public Move toNextMove() {
			if (nextMoveExists()) {
				return moveHistory.get(++moved);
			}
			return null;
		}
		
		@Override
		public Move getNextMove() {
			if (nextMoveExists()) {
				Move toReturn = moveHistory.get(++moved);
				moved--;
				return toReturn;
				
			}
			return null;
		}

		@Override
		public String getNotes() {
			if (moved == -1){
				return gameFileNotes;
			} else {
				return moveHistory.get(moved).getNotes();
			}
			
		}

		@Override
		public ArrayList<Move> getPossibleMoves() {
			ArrayList<Move> moves = new ArrayList<Move>(1);
			if (nextMoveExists()){
				moves.add(moveHistory.get(moved + 1));
			}
			
			return moves;
		}

		@Override
		public boolean setNextMove(Move move) {
			if (moved + 1 < moveHistory.size() && moveHistory.get(moved + 1).equals(move)) {
				moved++;
				return true;
			}
			return false;
		}

		@Override
		public void setNotes(String notes) {
			if (moved == -1){
				gameFileNotes = notes;
			} else {
				moveHistory.get(moved).setNotes(notes);
			}
			
		}

		@Override
		public Piece[][] toEndingPosition() {
			moved = moveHistory.size() - 1;
			return AbstractGameFile.this.getEndingPosition();
		}

		@Override
		public Move undo() {
			if (moved == moveHistory.size()){
				moved--;
			}
			
			if (moved > -1) {
				return moveHistory.get(moved--);
			}
			return null;
		}

		/**
		 * This method returns if the next Move exists
		 * @return boolean - the move if it exists, else false
		 */
		private boolean nextMoveExists(){
			return moved + 1 < moveHistory.size();
		}
	}//end of AbstractGameFileIterator class
}//end of AbstractGameFile class