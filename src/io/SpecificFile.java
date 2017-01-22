package io;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;

import chessgame.Move;
import pieces.Piece;
import pieces.Turn;

/**
 * An abstract class for common methods of all specific chessFiles<Br>
 * Note: SpecificFiles do not keep track of whose turn is whose. It is just a huge file of moves.<br>
 * NOTE: USE SAVE TO SAVE WHATEVER CHANGES YOU HAVE DONE TO THIS SPECIFIC FILE
 * @author Frank and Robert
 * @since October 11, 2016
 */
public abstract class SpecificFile extends GeneralFile implements Cloneable, Serializable {
	/**The Opening File Type Reference*/
	public static final String OPENING_FILE = "Opening";
	
	/**The Tactic File Type Reference*/
	public static final String TACTIC_FILE = "Tactic";

	/**The Game File Type Reference*/
	public static final String GAME_FILE = "Game";
	
	/**The Saved File Type Reference*/
	public static final String SAVED_FILE = "Saved";
	
	/**The folder name for SpecificFile*/
	protected static final String FOLDER_NAME = "SpecificFile";
	
	/**The chessFile this specificFile is opened from*/
	private ChessFile chessFile;

	
	/**
	 * Creates a SpecificFile with the corresponding ChessFile
	 * @param chessFile (ChessFile) - the corresponding chessFile of this specificFile
	 */
	protected SpecificFile(ChessFile chessFile){
		super(chessFile.getSpecificFileLocation());
		this.chessFile = chessFile;
	}

	/**
	 * This method returns the Difficulty of this specificFile
	 * @return Difficulty - the Difficulty of this specificFile
	 */
	public Difficulty getDifficulty() {
		return chessFile.getDifficulty();
	}

	/**
	 * Returns the corresponding chessFile
	 * @return chessFile (ChessFile) - the corresponding chessFile
	 */
	public ChessFile getChessFile(){
		return chessFile;
	}

	/**
	 * This method returns the name of the specificFile
	 * @return String - the name of this specificFile
	 */
	public String getName() {
		return chessFile.getName();
	}
	
	
	/**
	 * Get a certain property of the file
	 * @param property - property to get
	 * @return String - a toString version of the property value
	 */
	public String getProperty(String property){
		return chessFile.getProperty(property);
	}

	/**
	 * This returns the SpecificFileIterator for the specified specificFile
	 * NOTE: Saved File does not have a specific File
	 * @return SpecificFileIterator
	 */
	public abstract SpecificFileIterator getSpecificFileIterator();

	/**
	 * Returns this specificFile type
	 * @return type (String) - one of the specific File constants
	 */
	public String getSpecificFileType(){
		return chessFile.getSpecificFileType();
	}//end of getSpecificFileType method

	/**
	 * Returns the ending positions for the SpecificFile
	 * @return position (Piece[][]) - a double array of piece and positions
	 * @throws IllegalStateException - if this is an opening file since there is not just
	 * 			one ending position for an opening file
	 */
	public abstract Piece[][] getEndingPosition();

	/**
	 * Returns the starting positions for the specificFile
	 * You get the basic starting position unless you have a tactic/saved File
	 * @return position (Piece[][]) - a double array of piece and positions
	 */
	public abstract Piece[][] getStartingPosition();

	/**
	 * Returns the move history this file contains.<br>
	 * GAME_FILES and TACTIC_FILES return the future move history<br>
	 * SAVED_FILES return the moveHistory of the game so far <br>
	 * OPENING_FILES throws an error because there are too many possible move histories
	 * 
	 * @return (ArrayList -- move) - the moveHistory for this specificFile
	 */
	public abstract ArrayList<Move> getMoveHistory();
	
	/**
	 * Returns if the specificFile is a certain specificFileType
	 * @return boolean - is it is a certain specificFileType
	 */
	public boolean isSpecificFileType(String specificFileType){
		return specificFileType.equals(getSpecificFileType());
	}
	
	/**
	 * Get the turn for this specificFile
	 * @return turn (Turn) - turn of this specificFile 
	 */
	public Turn getTurn(){
		return chessFile.getTurn();
	}//end of getTurn method

	/**
	 * Returns whether the person has mastered this specificFile
	 * @return mastered (boolean) - true if they have mastered this specificFile and false otherwise
	 */
	public boolean isMastered(){
		return chessFile.isMastered();
	}//end of isMastered
	
	
	/**
	 * Deletes all the currentInformation and resets it with the given properties.<br>
	 * NOTE: this does assumes your inputs are not faulty<br>
	 * NOTE: make sure you use SAVE to save whatever you have done
	 * 
	 * @param startingPosition (Piece[][]) - the starting  position for the specificFile
	 * @param moveHistory (ArrayList -- Move) - the moveHistory for the specificFile
	 * @param endingPosition (Piece[][]) - the ending position for the specificFile
	 */
	public abstract void resetInformation(Piece[][] startingPosition, ArrayList<Move> moveHistory, Piece[][] endingPosition);
	
	/**
	 * Saves the new setup of the file
	 */
	public void save(){
		FileOrganizer.saveFile(this);
	}
	
	/**
	 * Sets its corresponding chessFile
	 * @param chessFile (ChessFile) - the corresponding chessFile to set this one to
	 */
	protected void setChessFile(ChessFile chessFile){
		this.chessFile = chessFile;
		setFileLocation(chessFile.getSpecificFileLocation());
	}

	/**
	 * This is the general interface of all specific file iterators. Also contains all the general methods the specificFile has. <br>
	 * NOTE: Does not keep track of the current Turn
	 */
	public abstract class SpecificFileIterator {
		
		/**
		 * Add a move to the this tree that should take place after the current
		 * setup. The position of the Iterator will be adjusted.
		 * 
		 * @param move (Move) - the move that was taken
		 * @throws IllegalStateException - If the move being added is your move, but a value of
		 *             your move already exists. In this case, either delete the old move
		 */
		public abstract void addMove(Move move);
		
		/**
		 * Returns if this iterator has a next move
		 * @return true is there is a next move for the specificFile
		 */
		public boolean hasNextMove(){
			return getNextMove() != null;
		}
		
		/**
		 * Adds a starting position of the piece
		 * @param piece (Piece) - piece to place
		 * @param position (int[2]) - position of piece
		 * 
		 * @throws IllegalArgumentException - if the position exists
		 * @throws IllegalStateException - if the specificFileType prevents addition of starting positions
		 */
		public abstract void addStartingPosition(Piece piece, int[] position);

		/**
		 * Deletes the current move. The iterator will now go to the previous move.<br>
		 * Note: This method only deletes one move (as in
		 * one with no subsequent moves). If you are deleting moves with
		 * subsequent moves, use the deleteMovesRegardless method.
		 * 
		 * @throws IllegalStateException - if there are more moves after this one
		 * @return Move - the previous move (move to go to) 
		 */
		public abstract Move deleteMove();

		/**
		 * Deletes all moves after (and including) this move. <br>
		 * The iterator now goes to the previous move.
		 * @return Move - the previous move (move to go to) 
		 */
		public abstract Move deleteMovesRegardless();

		/**
		 * Receive the last move that was done
		 * @return Move - returns the move that was done. If there are none before this one, null is returned
		 */
		public abstract Move getMoveDone();

		/**
		 * Returns the move history this file contains.<br>
		 * GAME_FILES and TACTIC_FILES return the future move history<br>
		 * SAVED_FILES return the moveHistory of the game so far <br>
		 * OPENING_FILES throws an error because there are too many possible move histories
		 * 
		 * @return (ArrayList -- move) - the moveHistory for this specificFile
		 */
		public ArrayList<Move> getMoveHistory(){
			return SpecificFile.this.getMoveHistory();
		}
		
		/**
		 * Returns the next move in the specific File and changes the position of the iterator<br><br>
		 * 
		 * For OPENING_FILES where there is more than 1 possible move, it will return the first move listed only
		 * 
		 * @return Move - the next move (null if none is set)
		 */
		public abstract Move toNextMove();

		/**
		 * Returns the next move in the specific File and does not change the position of the iterator<br><br>
		 * 
		 * For OPENING_FILES where there is more than 1 possible move, it will return the first move listed only
		 * 
		 * @return Move - the next move (null if none is set)
		 */
		public abstract Move getNextMove();
		
		/**
		 * Get the notes written about this move. If there is no current move, it will return the notes of the specificFile in general
		 * @return notes (String) - the notes of this move
		 */
		public abstract String getNotes();

		/**
		 * Returns the starting position for the game <br>/
		 * You get the basic starting position unless you have a tactic or Saved File
		 * @return position (Piece[][]) - a double array of piece and positions
		 */
		public Piece[][] getStartingPosition(){
			return SpecificFile.this.getStartingPosition();
		}
		
		/**
		 * Returns the specificFile that this iterator is using
		 * @return specificFile - The specificFile this iterator is using
		 */
		public SpecificFile getSpecificFile(){
			return SpecificFile.this;
		}
		
		/**
		 * Returns this specificFile type
		 * @return type (String) - one of the specific File constants
		 */
		public String getSpecificFileType(){
			return SpecificFile.this.getSpecificFileType();
		}//end of getSpecificFileType method

		/**
		 * Get the turn of the specificFile
		 * @return Turn - the turn of specificFile
		 */
		public Turn getSpecificFileTurn() {
			return SpecificFile.this.getTurn();
		}
		
		/**
		 * This method returns all possible moves at the current position.<br>
		 * Note: Game and Tactic files only have 1 possible moves<br>
		 * THIS DOES NOT CHANGE THE ITERATOR POSITION
		 * 
		 * @return (ArrayList -- Move) - possible opponent moves arrayList
		 */
		public abstract ArrayList<Move> getPossibleMoves();

		/**
		 * Removes a starting position of the piece
		 * @param position (int[2]) - position of piece
		 * @throws IllegalArgumentException - if the position does not exist
		 * @throws IllegalStateException - for opening and game files
		 */
		public abstract void removeStartingPosition(int[] position);

		/**
		 * Resets the iterator to its starting positions <Br>
		 * NOTE: any changes are NOT saved
		 */
		public void resetIterator(){
			
			//undo until the beginning
			while (undo() != null){}
		}
		
		/**
		 * Sets the next move to the iterator. The position of the
		 * Iterator will be adjusted if boolean = true. <br>
		 * This is will adjust the path the iterator will go on for openingFiles
		 * 
		 * @return boolean - if this move exists or not in the SpecificFile
		 */
		public abstract boolean setNextMove(Move move);

		/**
		 * Set the notes about that will describe this move. If there is no current move, it will set the notes of the specificFile in general
		 * 
		 * @param notes (String) - the new notes about this move/specific File
		 */
		public abstract void setNotes(String notes);

		/**
		 * Returns the ending positions for the SpecificFile. Also changes the iterator position to the
		 * end of the file
		 * @return position (Piece[][]) - a double array of piece and positions
		 */
		public abstract Piece[][] toEndingPosition();

		/**
		 * Undo's a move. The iterator position will be adjusted, unless null is returned
		 * 
		 * @return Move - the move undo'ed. Return null if no more moves can be undo'ed
		 */
		public abstract Move undo();

		/**
		 * Saves the new setup of the file
		 */
		public void save(){
			SpecificFile.this.save();
		}

		/**
		 * Returns if the specificFile is a certain specificFileType
		 * @return boolean - is it is a certain specificFileType
		 */
		public boolean isSpecificFileType(String specificFileType) {
			return SpecificFile.this.isSpecificFileType(specificFileType);
		}

		
	}//end of specificFileIterator
}//end of SpecificFile
