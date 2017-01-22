package chessgame;

import pieces.Piece;
import pieces.PieceType;
import pieces.Turn;

import static pieces.PieceType.*;
import static pieces.Turn.*;

import java.util.ArrayList;
import java.util.Arrays;
import ai.AI;

/**
 * This is a chessBoard class. It is just a plain position holder that doesn't do anything on its own.
 * You can use the display class to manipulate it like a game.
 * 
 * NOTE: NO METHODS CALLED HERE WILL CHANGE THE ACTUAL GRAPHICS OF THE
 * CHESSBOARD. USE THE DISPLAY CLASS IF YOU NEED TO CHANGE THE GRAPHICS. <Br>
 *
 * @author Robert Desai, Frank Liu
 *
 */
public class ChessBoard {
	/* Basic Static Variables
	 * Order is the order of pieces in the 1st and 8th columns
	 */
	private static final PieceType[] ORDER = new PieceType[] { ROOK, KNIGHT, BISHOP, QUEEN, KING, BISHOP, KNIGHT, ROOK };
	
	/** Default starting position of the gameboard */
	private static final Piece[][] DEFAULT_BOARD;
	
	/** The size of the chessBoard (1 dimensional size) */
	public static final int LENGTH = 8;
	
	/** Win variable for Black and contains a proper game ending message */
	public static final String BLACK_WINS = "Black Wins";
	/** Win variable for White and contains a proper game ending message */
	public static final String WHITE_WINS = "White Wins";
	/** Game ending variable, no player wins and contains a proper game ending message */
	public static final String STALEMATE = "Stalemate";
	/** Game ending variable by forced draw and contains a proper game ending message */
	public static final String POSSIBLE_DRAW = "Draw Declared: ";
	/** Game ending variable where draw is accepted and contains a proper game ending message */
	public static final String DRAW_ACCEPTED = "Draw";
	/** Variable where both oppositions have yet to win and contains a proper message */
	public static final String CURRENTLY_PLAYING = "Currently Playing";
	/** Game ending variable if both teams don't have enough material */
	public static final String NOT_ENOUGH_MATERIAL = "Draw: not enough material";
	/** Game ending variable by forfeit and contains a proper game ending message */
	public static final String WIN_BY_RESIGNATION = "Draw by Resignation";
	
	/** Game ending variable where draw is declared through unending checks and contains a proper game ending message*/
	public static final String PERPETUAL_CHECK = "Draw through perpetual check";
	/** Game ending variable where draw is declared through repeating moves 3 times*/
	public static final String THRICE_REPEATED = "Draw through repeating moves";
	
	/** Castle Distance - the distance the king has to move for the king to castle */
	public static final int CASTLE_DISTANCE = 2;
	/** King's position before castle on either side*/
	public static final int KING_XPOSITION = 4;
	/** Position of rook on the Queen's side before castle on the Queen's side */
	public static final int QUEENSIDE_ROOK_XPOSITION = 0;
	/** Position of rook on the Queen's side after castle on the Queen's side */
	public static final int QUEENSIDE_CASTLED_ROOK_XPOSITION = 3;
	/** Position of the king after castle on the Queen's side */ 
	public static final int QUEENSIDE_CASTLED_KING_XPOSITION = 2;
	/** Position of the rook on the King's side before castle on the King's side */
	public static final int KINGSIDE_ROOK_XPOSITION = 7;
	/** Position of the rook on the Kind's side after castle on the King's side */
	public static final int KINGSIDE_CASTLED_ROOK_XPOSITION = 5;
	/** Position of the king after castle on the King's side */
	public static final int KINGSIDE_CASTLED_KING_XPOSITION = 6;
	/** Row where black castles and where all black non-pawn pieces start at the beginning of the game */
	public static final int BLACK_SIDE_YPOSITION = 7;
	/** Row where white castles and where all white non-pawn pieces start at the beginning of the game */
	public static final int WHITE_SIDE_YPOSITION = 0;
	
	
	/** Checks the position of the board */
	private boolean checked;
	
	/** Checks if the chessboard is in an illegal position */
	private boolean illegalPosition;
	
	
	/** Position of piece on the board */
	private Piece[][] board;
	
	/** The chess ai this ai contains*/
	private AI ai;
	
	/** If there is an ai with the chessboard */
	private boolean aiExists;
	
	/** The available moves of the mover in this current board position */
	private ArrayList<Move> availableMoves = new ArrayList<>();
	
	/** Player's turn */
	private Turn turn;
	
	/** A history of moves */
	private ArrayList<Move> moveHistory = new ArrayList<>(20);
	
	/** The general available moves of the mover in this current board position (aka. excludes check) */
	private ArrayList<Move> moverPossibleMoves = new ArrayList<>();
	
	/** The possible moves for the opponent in the current board position */
	private ArrayList<Move> opponentPossibleMoves = new ArrayList<>();
	
	/** Last move that was done */
	private Move undoMove;
	
	/** The draw type of the chessBoard if there is one. Use chessboard.checkWin() to update */
	private String drawType;

	/**
	 * Sets the default board
	 */
	static {
		DEFAULT_BOARD = new Piece[LENGTH][LENGTH];
		for (int x = 0; x < LENGTH; x++) {
			for (int y = 0; y < LENGTH; y++) {

				Turn turn = y < 4 ? WHITE : BLACK;
				if (endOfBoard(y)) {
					// end of board
					DEFAULT_BOARD[x][y] = new Piece(turn, ORDER[x]);
				} else if (y == 1 || y == 6) {
					DEFAULT_BOARD[x][y] = new Piece(turn, PAWN);
				} else {
					DEFAULT_BOARD[x][y] = new Piece();
				}
			}
		}
	}//end of static block

	/**
	 * Creates a default chessBoard with a possible AI. If chessBoard is WHITE, the ai will move right away
	 * @param aiExists - if aiExists is false, there will be no ai, and the param ai (int) is ignored
	 * @param ai (int) - the ai level
	 * @param turn (Turn) - the turn of the AI
	 */
	public ChessBoard(boolean aiExists, int ai, Turn turn) {
		board = ChessBoard.clonedBoard(DEFAULT_BOARD);
		this.aiExists = aiExists;
		this.turn = BLACK; // will be changed to white in reset
		reset();
		setAvailableMoves();

		if (aiExists) {
			this.ai = new AI(ai, this, turn);
			if (turn == WHITE) {
				move(this.ai.getMove());
			}
		}
	}//end of constructor
	
	/**
	 * Start a chessBoard in a certain position. AI is false;
	 * @param board (Piece[][]) - the starting board to use
	 * @param turn (Turn) - the turn to start on
	 * Note: this is passed by reference
	 */
	public ChessBoard(Piece[][] board, Turn turn){
		this.board = board;
		this.aiExists = false;
		this.turn = getOppositeTurn(turn); // will be changed to white in reset
		reset();
		setAvailableMoves();
	}//end of constructor

	/**
	 * This chessBoard creates a chessBoard with the given chessBoard after a
	 * certain move. It is used to check different states of the board.
	 * @param chessBoard (ChessBoard) - the base chessBoard
	 * @param move (Move) - the move to add
	 */
	private ChessBoard(ChessBoard chessBoard, Move move) {
		turn = move.getTurn();
		moveHistory = chessBoard.getMoveHistory();
		aiExists = chessBoard.aiExists;
		ai = chessBoard.ai;
		
		// make new copy of board not destroy the other chessBoard's copy
		board = new Piece[LENGTH][];
		for (int i = 0; i < LENGTH; i++) {
			board[i] = Arrays.copyOf(chessBoard.board[i], LENGTH);
		}

		//move method without the reset
		Piece pieceTaken = new Piece();
		Piece promotion = move.getPromotion();
		boolean specialMove = move.isSpecialMove();

		if (move.hasEnpassuant()) {
			pieceTaken = board[move.endX()][move.startY()];
			board[move.endX()][move.startY()] = new Piece();

		} else if (move.hasCastled()) {
			// this is a castle
			if (move.endX() - move.startX() == CASTLE_DISTANCE) {
				castleRooks(KINGSIDE_ROOK_XPOSITION, KINGSIDE_CASTLED_ROOK_XPOSITION, move.startY());
			} else {
				castleRooks(QUEENSIDE_ROOK_XPOSITION, QUEENSIDE_CASTLED_ROOK_XPOSITION, move.startY());
			}

		} else if (move.contains(PAWN) && (move.endY() == 7 || move.endY() == 0)) {
			// promotion
			if (aiExists && turn == ai.getTurn()) {
				promotion = ai.getPromotion();
			}

			board[move.startX()][move.startY()] = promotion;
			pieceTaken = board[move.endX()][move.endY()];
		} else {
			pieceTaken = board[move.endX()][move.endY()];
		}

		generalMove(move.getStartPosition(), move.getEndPosition());
		moveHistory.add(new Move(move, pieceTaken, promotion, specialMove));
		undoMove = null;
		reset();
	}//end of constructor

	/**
	 * Ai turn is the turn which is not moving right now
	 * @param chessBoard (ChessBoard) - The base chessboard
	 * @param aiExists (boolean) - If aiExists is false, there is no ai
	 * @param aiLevel (int) - The difficulty of the ai
	 */
	public ChessBoard(ChessBoard chessBoard, boolean aiExists, int aiLevel) {
		this.moveHistory = chessBoard.moveHistory;
		this.turn =  getOppositeTurn(chessBoard.turn);
		this.aiExists = aiExists;
		this.board = chessBoard.board;
		reset();
		setAvailableMoves();
		
		if (aiExists){
			ai = new AI(aiLevel, this, getOppositeTurn(turn));
		}
	}//end of constructor

	/**
	 * Returns whether or not an ai exists
	 * @return boolean - whether or not an ai exists
	 */
	public boolean aiExists() {
		return aiExists;
	}//end of aiExists method

	/**
	 * Returns if it is possible to undo in the current chessBoard.
	 * @return boolean - if it is possible to undo
	 */
	public boolean canUndo() {
		if (aiExists && ai.getTurn() == WHITE){
			return moveHistory.size() > 1;
		}
		return !moveHistory.isEmpty();
	}//end of moveHistory method

	/**
	 * Returns if the y-position sent here is at either end of the board
	 * 
	 * @param pos (int) - the position you want to check
	 * @return boolean - if it is at either end of the board
	 */
	public static boolean endOfBoard(int pos) {
		return pos == 0 || pos == 7;
	}//end of endOfBoard method

	/**
	 * Returns the available moves of the player in the current state of the
	 * board
	 * @return ArrayList<Move> - the available moves of the player in this state
	 *         of the board
	 */
	public ArrayList<Move> getAvailableMoves() {
		ArrayList<Move> copyOfMoverMoves = new ArrayList<>();
		copyOfMoverMoves.addAll(availableMoves);
		return availableMoves;
	}//end of getAvailableMoves method

	/**
	 * Returns the chessBoard pieces and location
	 * @return Piece[][] - an array of pieces that represent the current board positions
	 */
	public Piece[][] getBoard() {
		return clonedBoard(board);
	}//end of getBoard method

	/**
	 * Returns the move History of the current ChessBoard
	 * @return ArrayList<Move> - An array of every move that has occured during
	 *         the game
	 */
	public ArrayList<Move> getMoveHistory() {
		ArrayList<Move> copy = new ArrayList<>();
		copy.addAll(moveHistory);
		return copy;
	}//end of getMoveHistory method

	/**
	 * Returns the a copy of the available moves of the opponent in the current
	 * state of the board
	 * @return ArrayList<Move> - an array of moves available to the opponent if
	 *         their turn was to be right now
	 */
	public ArrayList<Move> getOpponentAvailableMoves() {
		ArrayList<Move> moves = new ArrayList<>(opponentPossibleMoves.size());
		moves.addAll(opponentPossibleMoves);
		return moves;
	}//end of getOpponentAvailableMoves method

	/**
	 * Returns the piece in the given coordinates
	 * @param (int[2])- The position you are looking at, requires an x and y
	 *        coordinate
	 * @return The Piece found at that position
	 */
	public Piece getPieceAt(int[] position) {
		return board[position[0]][position[1]];
	}//end of getPieceAt method

	/**
	 * Returns the piece on the coordinate x, y on the chessboard
	 * @param x (int) - the x coordinate you are looking at
	 * @param y (int) -  the y coordinate you are looking at
	 * @return Piece - the piece on those coordinates
	 */
	public Piece getPieceAt(int x, int y) {
		return board[x][y];
	}//end of getPieceAt method

	/**
	 * Returns if the lastMove was a Promotion.
	 * @return boolean - true if the last move was a Promotion
	 */
	public boolean promoted() {
		return previousMove().contains(PAWN) && previousMove().isSpecialMove();
	}//end of promoted method

	/**
	 * Returns if the moved person is still in check after his/her move. (aka
	 * illegal move/ position)
	 * @return boolean - if they are in check after the move is over
	 */
	public boolean illegalPosition() {
		return illegalPosition;
	}//end of illegalPosition method

	/**
	 * Returns a boolean if the position at position is empty
	 * @param x (int) - x coordinate of Piece
	 * @param y (int) - y coordinate of Piece
	 * @return boolean if the position is empty
	 */
	public boolean isEmptyAt(int x, int y) {
		return getPieceAt(x, y).isEmpty();
	}//end of isEmptyAt method

	/**
	 * Returns boolean - if the position is empty
	 * @param position (int[]) - position to check
	 * @return boolean if the position is empty
	 */
	public boolean isEmptyAt(int[] position) {
		return getPieceAt(position).isEmpty();
	}//end of isEmptyAt method

	/**
	 * Returns is the piece at the given position is an enemy piece according to
	 * the turn given by turn
	 * @param coords (int[]) - the positions to use
	 * @param turn (Turn) - turn you want to use
	 * @return boolean - true if it is an enemy Piece
	 */
	public boolean containsEnemyPieceAt(int[] coords, Turn turn) {
		return getPieceAt(coords).isEnemyPiece(turn);
	}//end of containsEnemyPieceAt method

	/**
	 * Returns is the piece at the given position is an enemy piece according to
	 * the turn given by turn
	 * @param x (int) - the x coordinate of the piece
	 * @param y (int) - the y coordinate of the piece
	 * @param turn (Turn)- turn you want to use
	 * @return boolean - true if it is an enemy Piece
	 */
	public boolean containsEnemyPieceAt(int x, int y, Turn turn) {
		return getPieceAt(x, y).isEnemyPiece(turn);
	}//end of containsEnemyPieceAt method

	/**
	 * Returns is the piece at the given position is an enemy piece according to
	 * the turn given by turn, and is a piece with the given PieceType
	 * @param coords (int[]) - the positions to use
	 * @param pieceType (PieceType) - pieceType to check
	 * @param turn (Turn) - turn you want to use
	 * @return boolean - true if it is an enemy Piece
	 */
	public boolean containsEnemyPieceAt(int[] coords, PieceType pieceType, Turn turn) {
		return getPieceAt(coords).isEnemyPiece(pieceType, turn);
	}//end of containsEnemyPieceAt method

	/**
	 * Returns is the piece at the given position is an enemy piece according to
	 * the turn given by turn, and is a piece with the given PieceType
	 * @param x (int) - the x coordinate of the piece
	 * @param y (int) - the y coordinate of the piece
	 * @param pieceType (PieceType) - pieceType to check
	 * @param turn (Turn) - turn you want to use
	 * @return boolean - true if it is an enemy Piece
	 */
	public boolean containsEnemyPieceAt(int x, int y, PieceType pieceType, Turn turn) {
		return getPieceAt(x, y).isEnemyPiece(pieceType, turn);
	}//end of containsEnemyPieceAt method

	/**
	 * Returns is the piece at the given position is an friendly piece according
	 * to the turn given by turn
	 * @param coords (int[]) - the positions to use
	 * @param turn (Turn) - turn you want to use
	 * @return boolean - true if it is an friendly Piece
	 */
	public boolean containsFriendlyPieceAt(int[] coords, Turn turn) {
		return !isEmptyAt(coords) && !getPieceAt(coords).isEnemyPiece(turn);
	}//end of containsFriendlyPieceAt method

	/**
	 * Returns is the piece at the given position is an friendly piece according
	 * to the turn given by turn
	 * @param x (int) - the x coordinate of the piece
	 * @param y (int) - the y coordinate of the piece
	 * @param turn (Turn) - turn you want to use
	 * @return boolean - true if it is a friendly Piece
	 */
	public boolean containsFriendlyPieceAt(int x, int y, Turn turn) {
		return getPieceAt(x, y).isFriendlyPiece(turn);
	}//end of containsFriendlyPieceAt method

	/**
	 * Returns is the piece at the given position is an friendly piece according
	 * to the turn given by turn, and has the PieceType given in
	 * 
	 * @param x (int) - the x coordinate of the piece
	 * @param y (int) - the y coordinate of the piece
	 * @param pieceType (PieceType) - pieceType to check
	 * @param turn (Turn) - turn you want to use
	 * @return boolean - true if it is a friendly Piece with given PieceType
	 */
	public boolean containsFriendlyPieceAt(int x, int y, PieceType pieceType, Turn turn) {
		return containsFriendlyPieceAt(x, y, turn) && getPieceAt(x, y).contains(pieceType);
	}//end of containsFriendlyPieceAt method

	/**
	 * Returns is the piece at the given position is an friendly piece according
	 * to the turn given by turn, and has the PieceType given in
	 * @param position (int[]) - the position of the piece
	 * @param pieceType (PieceType) - pieceType to check
	 * @param turn (Turn) - turn you want to use
	 * @return boolean - true if it is a friendly Piece with given PieceType
	 */
	public boolean containsFriendlyPieceAt(int[] position, PieceType pieceType, Turn turn) {
		return containsFriendlyPieceAt(position[0], position[1], pieceType, turn);
	}//end of containsFriendlyPieceAt method

	/**
	 * Returns is the piece at the given position has the PieceType given in
	 * @param coords (int[]) - the position of the piece
	 * @param pieceType (PieceType) - pieceType to check
	 * @return boolean - there is a piece with given PieceType
	 */
	public boolean containsPieceAt(int[] coords, PieceType pieceType) {
		return getPieceAt(coords).getPieceType() == pieceType;
	}//end of containsPieceAt method

	/**
	 * Returns is the piece at the given position has the PieceType given in
	 * @param x (int) - x position of the piece
	 * @param y (int) - y position of the piece
	 * @param pieceType (PieceType) - pieceType to check
	 * @return boolean - there is a piece with given PieceType
	 */
	public boolean containsPieceAt(int x, int y, PieceType pieceType) {
		return getPieceAt(x, y).getPieceType() == pieceType;
	}//end of containsPieceAt method

	/**
	 * Changes the board with the following move. Does not change the
	 * chessboard squares' colors but does change the pieceIcons on the squares
	 * Does not change the turn. Adds to move History. Also move to ai if
	 * nessasary
	 * @param move (Move) - A move object that a piece took
	 */
	public void move(Move move) {

		// additional information variables
		Piece pieceTaken = new Piece();
		Piece promotion = move.getPromotion();
		boolean specialMove = move.isSpecialMove();

		if (move.hasEnpassuant()) {
			pieceTaken = board[move.endX()][move.startY()];
			board[move.endX()][move.startY()] = new Piece();

		} else if (move.hasCastled()) {
			// this is a castle
			if (move.endX() - move.startX() == CASTLE_DISTANCE) {
				castleRooks(KINGSIDE_ROOK_XPOSITION, KINGSIDE_CASTLED_ROOK_XPOSITION, move.startY());
			} else {
				castleRooks(QUEENSIDE_ROOK_XPOSITION, QUEENSIDE_CASTLED_ROOK_XPOSITION, move.startY());
			}

		} else if (move.contains(PAWN) && (move.endY() == 7 || move.endY() == 0)) {

			// promotion
			if (aiExists && turn == ai.getTurn()) {
				promotion = ai.getPromotion();
			}

			board[move.startX()][move.startY()] = promotion;
			pieceTaken = board[move.endX()][move.endY()];
		} else {
			pieceTaken = board[move.endX()][move.endY()];
		}

		generalMove(move.getStartPosition(), move.getEndPosition());
		moveHistory.add(new Move(move, pieceTaken, promotion, specialMove));
		undoMove = null;
		reset();
		setAvailableMoves();
	}

	/**
	 * Returns if the current turn is a players turn, instead of no ones turn or
	 * an AI's turn
	 * 
	 * @return boolean - if the current turn is a players turn
	 */
	public boolean isPlayerTurn() {
		if (aiExists && ai.getTurn() == turn) {
			return false;
		}
		return true;
	}

	/**
	 * Returns the last move that was played
	 * 
	 * @return Move - the last move that was played
	 */
	public Move previousMove() {
		if (moveHistory.size() > 0) {
			return moveHistory.get(moveHistory.size() - 1);
		} else {
			return null;
		}

	}

	@Override
	/**
	 * Returns Every piece in the chessboard in the order they appear Starting
	 * from the top right hand corner and moving downwards and then to the right
	 * 
	 * @return String - a 2d array like string that represents every piece as
	 *         described above
	 */
	public String toString() {
		String toReturn = "";
		for (int i = 0; i < LENGTH; i++) {
			toReturn += Arrays.toString(board[i]) + "\n";
		}
		return toReturn;
	}

	/**
	 * Returns the colour that the human player is playing as should be used
	 * only in a player vs AI situation
	 * 
	 * @return Turn - the player's turn
	 */
	public Turn getTurn() {
		return turn;
	}

	/**
	 * Reverses everything that happened in the last move and all traces of it
	 * 
	 */
	public void undo() {
		
		Move move = moveHistory.remove(moveHistory.size() - 1);

		// check enpassant
		if (move.hasEnpassuant()) {
			// fill in taken piece
			board[move.endX()][move.startY()] = move.getPieceTaken();
		} else if (move.hasCastled()) {
			// switch rook back to old position
		
			//shows Queen Castle
			if (move.endX() == KINGSIDE_CASTLED_KING_XPOSITION) {
				castleRooks(KINGSIDE_CASTLED_ROOK_XPOSITION, KINGSIDE_ROOK_XPOSITION, move.startY());
			} else {
				castleRooks(QUEENSIDE_CASTLED_ROOK_XPOSITION, KINGSIDE_CASTLED_ROOK_XPOSITION, move.startY());
			}
		}
		// if a pawn is being promoted
		if (move.isPromoted()) {
			board[move.endX()][move.endY()] = new Piece(move.getPiece().getTurn(), PAWN);
		}

		generalMove(move.getEndPosition(), move.getStartPosition());

		if (!move.hasEnpassuant()) {
			// fill in old piece back (assuming not enpassuant)
			board[move.endX()][move.endY()] = move.getPieceTaken();
		}

		undoMove = move;
		reset();
		setAvailableMoves();
	}

	/**
	 * Returns the the move undoed if there is one
	 * 
	 * @return Move - the undoed move or null if there isn't one
	 */
	public Move undoMove() {
		return undoMove;
	}

	/**
	 * Returns a new chessboard that extends the current chessboard and is
	 * implemented with a certain move. This is used to check the state of a new
	 * chessboard after a given move in case something illegal or abnormal were
	 * to happen.
	 * 
	 * @param move (move) - A move object that a piece took
	 * @return ChessBoard - the new chessboard implemented with the move
	 */
	public ChessBoard viewMove(Move move) {
		return new ChessBoard(this, move);
	}

	/**
	 * Helper method of reAddInformation This sets variables illegalMove, check
	 * and the variables reAddInformation is to do, the possibleMoves array
	 * @param x (int) - the x coordinate that you are looking at
	 * @param y (int) - the y coordinate that you are looking at
	 * @param possibleMoves (ArrayList<Move>) - adds the possible moves of a certain piece to this moveSet
	 */
	private void addPossibleMoves(int x, int y, ArrayList<Move> possibleMoves) {
		ArrayList<Move> moves = board[x][y].getPossibleMoves(new int[] { x, y }, this);

		// checked is always placed first, check if check == true
		if (moves.size() > 0) {
			Piece attacked = getPieceAt(moves.get(0).getEndPosition());
			// currently checked (note the turn has changed before due to
			if (attacked.contains(KING) && !attacked.contains(turn)) {
				illegalPosition = true;
			} else if (attacked.contains(KING)) {
				checked = true;
			}
			possibleMoves.addAll(moves);
		}
	}//end of addPossibleMoves method

	/**
	 * Adjustes the rook position in a castle with the following parameters
	 * @param start (int) - start position
	 * @param end (int) - end position
	 * @param y (int) - y position
	 */
	private void castleRooks(int start, int end, int y) {
		// set rooks
		board[end][y] = board[start][y];
		board[start][y] = new Piece();
	}//end of castleRooks method

	/**
	 * Adjusts the pieces as any normal move would. Psuedo-code as follows:
	 * Piece at new position is the piece at the old position. Piece at old
	 * position is EMPTY. Returns the list that display needs to be edited too
	 * @param startPos (int[]) - startingPosition
	 * @param endpos (int[]) - endingPosition
	 */
	private void generalMove(int[] startPos, int[] endPos) {
		// update board
		board[endPos[0]][endPos[1]] = board[startPos[0]][startPos[1]];
		board[startPos[0]][startPos[1]] = new Piece();
	}//end of generalMove method

	/**
	 * private helper method: To be called after a move. It resets all the
	 * instance variables for preparation of the next move including turn itself
	 */
	private void reset() {
		availableMoves.clear();
		moverPossibleMoves.clear();
		opponentPossibleMoves.clear();
		illegalPosition = false;
		checked = false;
		turn = turn == WHITE ? BLACK : WHITE;
		
		// re add all the required information
		for (int x = 0; x < LENGTH; x++) {
			for (int y = 0; y < LENGTH; y++) {
				if (board[x][y].contains(turn)) {
					addPossibleMoves(x, y, moverPossibleMoves);
				} else {
					addPossibleMoves(x, y, opponentPossibleMoves);
				}
			}
		}
	}//end of reset method

	/**
	 * Sets the available moves in this position.
	 */
	private void setAvailableMoves() {
		ArrayList<Move> copyOfMoverMoves = new ArrayList<>();
		copyOfMoverMoves.addAll(moverPossibleMoves);
		availableMoves = Piece.filterAvailableMoves(this, copyOfMoverMoves, opponentPossibleMoves, checked, turn);
	}//end of setAvailableMoves method

	/**
	 * Gives the opposite turn based on the turn given in
	 * @param turn(Turn) - turn (to have to opposite turn for)
	 * @return Turn - the opposite turn
	 */
	public static Turn getOppositeTurn(Turn turn) {
		return turn == WHITE ? BLACK : WHITE;
	}//end of getOppositeTurn method

	/**
	 * Checks if anyone has won and returns BLACK_WINS, WHITE_WINS, STALEMATE, POSSIBLE_DRAW or CURRENTLY_PLAYING.
	 * If possible Draw is true, use getDrawType to getDraw.
	 * @return String - one of the final static Variables
	 */
	public String checkWin() {
		drawType = null;
		//makes sure its set
		setAvailableMoves();
		if (checked && availableMoves.size() == 0) {
			return turn == WHITE ? BLACK_WINS : WHITE_WINS;
		} else if (availableMoves.size() == 0) {
			return STALEMATE;
		}
		
		//check material
		if (!enoughMaterial(Turn.BLACK) && !enoughMaterial(Turn.WHITE)){
			return NOT_ENOUGH_MATERIAL;
		}
		
		//check perpetual check
		noTie: if (moveHistory.size() - 50 >= 0){
			for (int i = moveHistory.size() - 1; i >= moveHistory.size() - 50; i--){
				if (!moveHistory.get(i).isChecked()){
					break noTie;
				}
			}
			drawType = ChessBoard.PERPETUAL_CHECK;
			return POSSIBLE_DRAW;
		}
		
		//check 3 move
		if (sameLastThreeMoves(moveHistory.size() - 1) && sameLastThreeMoves(moveHistory.size() - 2)){

			drawType = ChessBoard.THRICE_REPEATED;
			return POSSIBLE_DRAW;
		}
		
		return CURRENTLY_PLAYING;
	}//end of checkWin method

	/**
	 * Returns if the last three moves from every fourth index is the same, checking from index and index - 2
	 * @param index - int index to start at
	 * @return if the condition written above is true
	 */
	private boolean sameLastThreeMoves(int index){
		if (moveHistory.size() > 12){
			Move move1 = moveHistory.get(index);
			for (int i = index - 4; i >= index - 8; i -= 4){
				if (!move1.equals(moveHistory.get(i))){
					return false;
				}
			}
			
			Move move2 = moveHistory.get(index - 2);
			for (int i = index - 6; i >= index - 10; i -= 4){
				if (!move2.equals(moveHistory.get(i))){
					return false;
				}
			}
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Returns if the person of that turn have enough material for a checkMate
	 * @param turn Turn of player
	 * @return boolean if the player has enough material for a checkMate
	 */
	private boolean enoughMaterial(Turn turn){
		int knightOrBishop = 0;
		
		for (Piece[] row: board){
			for (Piece p: row){
				if (p.contains(turn)){
					if (p.contains(BISHOP) || p.contains(KNIGHT)){
						if (++knightOrBishop > 1){
							return true;
						}
					} if (p.contains(KING)){
						continue;
					} else  {
						return true;
					}
				}//checking the correct turn
			}//for each loop of pieces
		}
		return false;
	}
	
	
	/**
	 * Gives you an ai moves
	 * @return Move - ai's move with the given ai
	 */
	public Move getAIMove() {
		return ai.getMove();
	}//end of getAIMove method

	/**
	 * Returns whether the mover is in check
	 * @return check- if the mover is in check or not
	 */
	public boolean inCheck() {
		return checked;
	}//end of inCheck method

	/**
	 * Checks if the piece at x, y has the right properties as defined in pieceType and turn
	 * @param x (int) - The x coordinate of the piece
	 * @param y (int) - The y coordinate of the piece
	 * @param pieceType - pieceType to check
	 * @param turn (Turn) - turn to check
	 * @return boolean - if the pieceType and turn have the right properties
	 */
	public boolean containsPieceAt(int x, int y, PieceType pieceType, Turn turn) {
		return containsPieceAt(x, y, pieceType) && getPieceAt(x, y).contains(turn);
	}//end of containsPieceAt method
	
	/**
	 * Checks if the piece at a certain position has the right properties
	 * @param position (int[]) - The position of the piece
	 * @param pieceType (PieceType) - pieceType to check
	 * @param turn (Turn) - turn to check
	 * @return boolean - if the pieceType and turn have the right properties
	 */
	public boolean containsPieceAt(int[] position, PieceType pieceType, Turn turn) {
		return containsPieceAt(position, pieceType) && getPieceAt(position).contains(turn);
	}//end of containsPieceAt method

	/**
	 * Returns if a certain piece has Moved
	 * @param position (Int[]) - position of the piece 
	 * @return boolean - Returns if a certain piece has moved
	 */
	public boolean hasNotMoved(int[] position) {
		for (Move move : moveHistory) {
			// check if anyone took the piece or if the piece has moved
			if (move.hasPosition(position)) {
				return false;
			}
		}
		return true;
	}//end of hasNotMoved method

	/**
	 * Returns if a certain piece has Moved
	 * @param x (int) - The x coordinate of the piece
	 * @param y (int) - The y coordinate of the piece
	 * @return boolean - Returns if a certain piece has moved
	 */
	public boolean hasNotMoved(int x, int y) {
		for (Move move : moveHistory) {
			// check if anyone took the piece or if the piece has moved
			if (move.hasPosition(x, y)) {
				return false;
			}
		}
		return true;
	}//end of hasNotMoved method
	
	/**
	 * Return the default board position
	 * @return Piece[][] the default board position
	 */
	public static Piece[][] getDefaultBoard(){
		return clonedBoard(DEFAULT_BOARD);
	}
	
	/**
	 * Returns the drawType if there is a drawType
	 * @return String - Returns the drawType if there is a drawType as a string
	 */
	public String getDrawType(){
		return drawType;
	}//end of getDrawType method
	
	/**
	 * Checks if both the coords are in bounds
	 * @param coords (int[2]) - the coords to check
	 * @return boolean - true if both int[0] and int[1] are in bounds
	 */
	public static boolean inBounds(int[] coords) {
		return coords[0] < ChessBoard.LENGTH && coords[0] >= 0 && coords[1] < ChessBoard.LENGTH && coords[1] >= 0;
	}//end of inBounds method
	
	/**
	 * Checks if both the coords are in bounds
	 * @param x (int) - Checks the x coordinate of the piece
	 * @param y (int) - Checks the y coordinate of the piece
	 * @return boolean - true if both int[0] and int[1] are in bounds
	 */
	public static boolean inBounds(int x, int y) {
		return x < ChessBoard.LENGTH && x >= 0 && y < ChessBoard.LENGTH && y >= 0;
	}//end of inBounds method

	/**
	 * Returns if piece is at a certain position in the turn
	 * @param x (int) - the x coordinates of the piece
	 * @param y (int) - the y coordinates of the piece
	 * @param turn (Turn) - Turn to check
	 * @return boolean - true if piece is at certain position in the turn
	 */
	public boolean containsPieceAt(int x, int y, Turn turn) {
		return getPieceAt(x, y).contains(turn);
	}//end of containsPieceAt method
	
	/**
	 * Returns if both boards are the same
	 * param board1 (Piece[][]) - Boards to compare
	 * param board2 (Piece[][]) - Boards to compare
	 * @return boolean - Returns if both boards are the same
	 */
	public static boolean sameBoards(Piece[][] board1, Piece[][] board2){
		for(int i = 0; i < LENGTH; i++){
			if (!Arrays.equals(board1[i], board2[i])){
				return false;
			}
		}
		return true;
	}//end of sameBoards method
	
	/**
	 * Returns a copy of the board given in
	 * @param board (Piece[][]) - board to clone
	 * @return copyOfBoard (Piece[][]) - an exact copy of board
	 */
	public static Piece[][] clonedBoard(Piece[][] board){
		Piece[][] newBoard = new Piece[8][8];
		for (int i = 0; i < LENGTH; i++) {
			newBoard[i] = Arrays.copyOf(board[i], LENGTH);
		}
		return newBoard;
	}//end of clonedBoard method

	/**
	 * It returns AiLevel
	 * @return int - AiLevel
	 */
	public int getAiLevel() {
		if (aiExists){
			return ai.getLevel();
		} else {
			return 0;
		}
		
	}//end of getAiLevel method

	/**
	 * Returns AI's turn
+	 * @return turn - Returns AI's turn
	 */
	public Turn getAiTurn() {
		if (aiExists){
			return ai.getTurn();
		} else {
			return Turn.NONE;
		}
	}//end of getAiTurn method

	/**
	 * Returns the AI
	 * @return AI - Return AI
	 */
	public AI getAI() {
		return ai;
	}
}//end of getAI method