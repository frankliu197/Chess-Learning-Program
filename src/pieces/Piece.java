package pieces;

import static pieces.PieceType.*;
import static pieces.Turn.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import chessgame.ChessBoard;
import chessgame.Move;

/**
 * This class represents a piece on the chess board. It is immutable
 * 
 * @author Robert Desai and Frank Liu
 *
 */
public class Piece implements Serializable{
	/**Array of piece Icons. To get the Icon of a certain piece, use getPieceIcon()*/
	private final static ImageIcon[] PIECE_ICON;
	
	/** The Turn of the piece */
	private final Turn turn;
	
	/** The pieceType of the piece */
	private final PieceType pieceType;
	
	/**
	 * Sets up the PieceIcon Array
	 */
	static {
		// set up pieceIcon array
		PIECE_ICON = new ImageIcon[13];
		PieceType[] arr = PieceType.values();
		for (int i = 0; i < PIECE_ICON.length - 1; i++) {
			Turn turn = i % 2 == 0 ? WHITE : BLACK;
			PIECE_ICON[i] = new ImageIcon("ChessPieces/" + turn + " " + arr[i / 2] + ".png");
		}
		PIECE_ICON[12] = new ImageIcon();
	}

	/**
	 * Constructor Method: <br>
	 * Creates a Black Piece with turn = NONE and pieceType = EMPTY
	 */
	public Piece() {
		this.turn = Turn.NONE;
		this.pieceType = PieceType.EMPTY;
	}

	/**
	 * Constructor Method: <br>
	 * Creates a Piece with the given turn and pieceType
	 * 
	 * @param turn - (Turn) turn of Piece
	 * @param pieceType - (PieceType) the pieceType of the Piece
	 */
	public Piece(Turn turn, PieceType pieceType) {
		this.turn = turn;
		this.pieceType = pieceType;
	}

	/**
	 * This method filters the impossible moves from the possibleMove list. This
	 * is for checking special conditions
	 * 
	 * @param postion - (int[2]) the current position of the piece
	 * @param chessBoard - (ChessBoard) the current chessboard being used
	 * @param mpm - (ArrayList -- Move) the possible moves for players pieces (note
	 *            this will be changed)
	 * @param opm - (ArrayList -- Move) the possible moves for the opponents pieces
	 * @param check - boolean if the mover is in check
	 * @param turn - currentTurn of the mover
	 */
	public static ArrayList<Move> filterAvailableMoves(ChessBoard chessBoard, ArrayList<Move> mpm, ArrayList<Move> opm,
			boolean check, Turn turn) {
	
		ArrayList<Move> castles = new ArrayList<>(2);
	
		// check if moving the piece still leaves it in check
		for (int i = 0; i < mpm.size(); i++) {
			ChessBoard newChessBoard = chessBoard.viewMove(mpm.get(i));
			if (newChessBoard.illegalPosition()) {
				mpm.remove(i--);
				continue;
			} else {
				//add checked
				if (newChessBoard.inCheck()) {
					Move move = mpm.get(i);
					move = new Move(move, true);
					mpm.set(i, move);
				}
			}
			
			//check for another condition later if castled
			if (mpm.get(i).hasCastled()) {
				castles.add(mpm.get(i));
			}
		}
	
		// checks for valid castles
		for (Move move : castles) {
	
			// if mam does not contain the in between moves, the one move
			// between the castles or is in check
			if (check || !inBetweenMove(mpm, turn, move)) {
				mpm.remove(move);
				continue;
			}
		}
		return mpm;
	}//end of filterAvailableMoves

	/**
	 * Returns a Image icon according to the 
	 * @param pieceType - (PieceType) image of PieceType
	 * @param turn - (Turn) color for pieceType
	 * @return ImageIcon - an image icon for certain pieceType and turn
	 */
	public static ImageIcon getPieceIcon(PieceType pieceType, Turn turn) {
		return PIECE_ICON[pieceType.ordinal() * 2 + turn.ordinal()];
	}

	/**
	 * Returns if this piece's pieceType contains the Piecetype that is sent in
	 * 
	 * @param PieceType - (PieceType) pieceType to compare this piece's pieceType to
	 * @return boolean - true if this piece's pieceType is the same as the one
	 *         sent in
	 */
	public boolean contains(PieceType pieceType) {
		return this.pieceType == pieceType;
	}

	/**
	 * Returns if this piece's turn contains the turn that is sent in
	 * 
	 * @param turn - (Turn) turn to compare this piece's turn to
	 * @return boolean - true if this piece's turn is the same as the one sent
	 *         in
	 */
	public boolean contains(Turn turn) {
		return this.turn == turn;
	}

	/**
	 * Checks to see if two pieces are the same turn and pieceType
	 * 
	 * @param object - (Object) the piece you want to compare
	 * @return boolean - true, only if the two pieces are the same turn and
	 *         pieceType
	 */
	@Override
	public boolean equals(Object object) {
		if (object instanceof Piece) {
			Piece otherPiece = (Piece) object;
			return otherPiece.getTurn() == turn && otherPiece.getPieceType() == pieceType;
		}
		return false;
	}

	/**
	 * Filters availableMoves for this pieceType with the position given
	 * 
	 * @param mpm - (ArrayList -- Move) mover available move list 
	 * @param position - (int[2]) the position of the piece
	 * @return (ArrayList -- Move) - the available moves for that piece in that
	 *         position after the filter
	 */
	public ArrayList<Move> filterAvailableMoves(ArrayList<Move> mpm, int[] position) {
		ArrayList<Move> moves = new ArrayList<>();
		for (Move move : mpm) {
			if (move.hasStartPosition(position)) {
				moves.add(move);
			}
		}
		return moves;
	}//end of filterAvailableMoves

	/**
	 * Return the PieceType of this piece 
	 * @return PieceType -  the Piece type of a piece
	 */
	public PieceType getPieceType() {
		return pieceType;
	}

	/**
	 * Returns the Image Icon of this piece
	 * @return imageIcon of this piece
	 */
	public ImageIcon getPieceIcon() {
		if (turn == NONE) {
			return PIECE_ICON[12];
		} else {
			return PIECE_ICON[pieceType.ordinal() * 2 + turn.ordinal()];
		}
	}

	/**
	 * Shows the moves of the pieces with no special conditions like check.
	 * 
	 * @param position - (int[2]) the position of the piece.
	 * @param chessBoard- (ChessBoard) the current chessboard being used to check the possible moves
	 * @return (ArrayList -- Move) - the Available moves not including special conditions
	 */
	public ArrayList<Move> getPossibleMoves(int[] position, ChessBoard chessBoard) {
		return pieceType.getPossibleMoves(position, turn, chessBoard);
	}

	/**
	 * Returns the currentTurn of the piece
	 * 
	 * @return turn- (Turn) The turn of the piece
	 */
	public Turn getTurn() {
		return turn;
	}

	/**
	 * Returns true if the piece is an Empty Piece
	 * 
	 * @return boolean - true if it is an empty piece (Basically a blank square
	 *         on the chessboard) and false otherwise
	 */
	public boolean isEmpty() {
		return contains(PieceType.EMPTY);
	}

	/**
	 * Returns if this piece is an enemy piece based on the turn that is sent in
	 * <br>
	 * NOTE: do not send in Turn.NONE
	 * 
	 * @param turn - (Turn) the turn to check
	 * @return boolean - true if this is an enemy piece (if its turn is not the
	 *         same turn as was sent into this method)
	 */
	public boolean isEnemyPiece(Turn turn) {
		// not empty piece and turn is different
		return !isEmpty() && this.turn != turn;
	}

	/**
	 * Returns if this piece is a enemy piece with the given params:
	 * 
	 * @param pieceType - (PieceType) the pieceType you want
	 * @param turn - (Turn) the turn of the piece
	 * @return boolean - true if the piece is an enemy piece with the given
	 *         pieceType
	 */
	public boolean isEnemyPiece(PieceType pieceType, Turn turn) {
		return isEnemyPiece(turn) && contains(pieceType);
	}

	/**
	 * Returns if this piece is a friendly piece with the given turn
	 * @param turn - (Turn) turn to test
	 * @return boolean - true if the piece is an friendlyPiece with the given
	 *         turn
	 */
	public boolean isFriendlyPiece(Turn turn) {
		return !isEmpty() && this.turn == turn;
	}

	@Override 
	/**
	 * Returns the toString of this pieceType
	 * @return String - the toString of this pieceType
	 */
	public String toString() {
		return pieceType.toString();
	}

	/**
	 * Checks if mpm (The mover possible moves) contains a move one to the right
	 * or left of startX, in the direction of endX. Only works for KING
	 * 
	 * @param mpm - (ArrayList -- Move) mover available moves
	 * @param turn - (Turn) the turn of the mover
	 * @param move - (Move) the move to test
	 * @return boolean - if the move is contained in mpm (mover possible moves)
	 */
	private static boolean inBetweenMove(ArrayList<Move> mpm, Turn turn, Move move) {
		int j = move.endX() - move.startX() > 0 ? 1 : -1; // determines
															// direction
		return mpm.contains(
				new Move(new Piece(turn, KING), move.startX(), move.startY(), move.startX() + j, move.endY()));
	}//end of inBetweenMove
}//end of Piece class
