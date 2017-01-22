package pieces;

import javax.swing.ImageIcon;

import chessgame.ChessBoard;
import chessgame.Move;

import static chessgame.ChessBoard.*;
import static pieces.Turn.*;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This enum stores multiple pieceType and helps with thier possible Moves
 * @author Frank Liu
 * @since November 9
 */
public enum PieceType implements Serializable{
	PAWN, BISHOP, KNIGHT, ROOK, QUEEN, KING, EMPTY;

	private static final int PAWN_WHITE_ENPASSANT_YPOSITION = 4;
	private static final int PAWN_BLACK_ENPASSANT_YPOSITION = 3;
	private static final int PAWN_WHITE_STARTING_YPOSITION = 1;
	private static final int PAWN_BLACK_STARTING_YPOSITION = 6;
	private static final int PAWN_WHITE_TRAVEL_DIRECTION = 1;
	private static final int PAWN_BLACK_TRAVEL_DIRECTION = -1;
	private static final int PAWN_TAKING_CASES = 2;
	private static final int KING_NUMBER_OF_POSSIBLE_CASTLES = 2;
	
	/* Number of cases and the range of each case for a certain pieceType
	 * You get the range for a certain pieceType using
	 * CASES[pieceType.ordinal()]
	 */
	private static final int[] CASES = {4, 4, 8, 4, 8, 8, 0};
	private static final int[] RANGE = {1, 7, 1, 7, 7, 1, 0};
	
	@Override
	public String toString(){
		if (this == KNIGHT){
			return "n";
		} else {
			//returns the first letter of the pieceType in lower case
			return this.name().substring(0, 1).toLowerCase();
		}
	}//end of toString method

	/**
	 * Returns an ArrayList of Moves of the possible moves of the given pieceType (with turn). <br>
	 * This does not take check in account
	 * 
	 * @param position - (int[2]) position of the piece
	 * @param turn - (Turn) the turn of the piece
	 * @param chessBoard - (chessBoard) chessBoard the piece appears on
	 * @return (ArrayList -- move) - all the possible moves for 
	 */
	protected ArrayList<Move> getPossibleMoves(int[] position, Turn turn, ChessBoard chessBoard){
		if (this == PAWN){
			return getPawnPossibleMoves(position, turn, chessBoard);
		} else {
			int num = ordinal();
			ArrayList<Move> places;
			
			if (this == KING){
				places = new ArrayList<>(RANGE[num] * CASES[num] + KING_NUMBER_OF_POSSIBLE_CASTLES);
				places.addAll(addCastles(position, turn, chessBoard));
			} else {
				places = new ArrayList<>(RANGE[num] * CASES[num]);
			}
	
			// for all cases and ranges
			for (int n = 0; n < CASES[num]; n++) {
				for (int i = 1; i <= RANGE[num]; i++) {
	
					int[] coords = combine(n, position, i);
	
					if (inBounds(coords)) {
						if (chessBoard.isEmptyAt(coords)) {
							// empty square
							addMove(chessBoard, position, coords, places);
						} else if (chessBoard.containsEnemyPieceAt(coords, turn)) {
							// attacking opponent piece
							addMove(chessBoard, position, coords, places);
							break;
						} else {
							// attacking own piece
							break;
						}
					}//end of inBounds if statment
				}//end of range for statements
			}//end of cases for statement
			return places;
		}//end of checking pieceType if statement
	}//end of getPossibleMoves method

	/**
	 * Adds a move to the list as given in places. If the move is attacking a king, it will go to
	 * the front of the list.<br>
	 * 
	 * NOTE: this assumes that the move is valid, and does not check if the move is attacking
	 * 		enemy piece or friendly piece
	 * 
	 * @param chessBoard - (chessBoard) the chessBoard this is valid in
	 * @param startPos - (int[2]) old position of the piece
	 * @param endPos - (int[2]) new position of the piece
	 * @param places - (ArrayList -- move) arrayList to add move to
	 */
	private static void addMove(ChessBoard chessBoard, int[] startPos, int[] endPos, ArrayList<Move> places) {
		// if is attacking king, put it in the front of the list
		if (chessBoard.containsPieceAt(endPos, KING)) {
			places.add(0, new Move(chessBoard.getPieceAt(startPos), startPos, endPos));
		} else {
			places.add(new Move(chessBoard.getPieceAt(startPos), startPos, endPos));
		}
	}//end of addMove method

	/**
	 * Returns all possible ways to castle in an arrayList
	 * @param position - (int[2]) the position of the king
	 * @param turn - (Turn) the turn of the king
	 * @param chessBoard - (chessBoard) the chessBoard this king is on
	 * @param moveHistory - (ArrayList -- move) the moveHistory for this chessBOard
	 * @return (ArrayList -- Move) - An arrayList of castle moves if possible
	 */
	private ArrayList<Move> addCastles(int[] position, Turn turn, ChessBoard chessBoard) {
		ArrayList<Move> moves = new ArrayList<Move>(KING_NUMBER_OF_POSSIBLE_CASTLES);
		
		if (kingInCastlePosition(position, turn)) {
			Piece king = chessBoard.getPieceAt(position);
			
			// castle to king side
			if (canCastle(chessBoard, position, KINGSIDE_ROOK_XPOSITION, turn)) {
				moves.add(new Move(king, position, new int[] { position[0] + ChessBoard.CASTLE_DISTANCE, position[1]}, new Piece(), new Piece(), true, false));
			}
	
			// castle to queen side
			if (canCastle(chessBoard, position, QUEENSIDE_ROOK_XPOSITION, turn)) {
				moves.add(new Move(king, position, new int[] { position[0] - ChessBoard.CASTLE_DISTANCE, position[1] }, new Piece(), new Piece(), true, false));	
			}
		}
		return moves;
	}//end of addCastles

	/**
	 * Returns if the king can castle, assuming that the King is in the
	 * right position to castle. It checks if the rook exists, if they moved
	 * and if there are any pieces between it
	 * 
	 * @param chessBoard - (chessBoard) the chessBoard the information is valid in
	 * @param position - (int[2]) the position of the king
	 * @param rookXPos - (int) the x position of the rook the king is castling to
	 * @param turn - (Turn)s the turn of the king
	 * @return boolean - if the king can castle with that rook
	 */
	private boolean canCastle(ChessBoard chessBoard, int[] position, int rookXPos, Turn turn) {
	
		// check if the rook is in the right position
		boolean rookAvailable = chessBoard.containsFriendlyPieceAt(rookXPos, position[1], ROOK, turn);
	
		// direction - direction to check; places - places to check
		int direction;
		int places;
	
		// check queens side or king side castle
		if (rookXPos == KINGSIDE_ROOK_XPOSITION) {
			direction = 1;
			places = 2;
		} else {
			direction = -1;
			places = 3;
		}
	
		// check if all the places between is not empty, return false
		for (int i = 1; i <= places; i++) {
			if (!chessBoard.isEmptyAt(position[0] + direction * i, position[1])) {
				return false;
			}
		}
	
		// check if both king and rook has not moved
		boolean kHasNotMoved = chessBoard.hasNotMoved(position);
		boolean rHasNotMoved = chessBoard.hasNotMoved(rookXPos, position[1]);
	
		return rookAvailable && kHasNotMoved && rHasNotMoved;
	}//end of CanCastle method

	/** 
	 * A method that uses all combine methods of pieces in piecesCombine static class: <br>
	 * Combines different positions this pieceType can move <br>
	 * <b> see </b> piecesCombine
	 * @param n - (int) iteration number (case #)
	 * @param position- (int[2]) coordinates of the piece
	 * @param i - (int) value (the distance to use for a certain case)<br>
	 * 			e.g. for a rook on a case where it will go towards the right,
	 * 			it will go i values to the right
	 * @return int[] - int[0] for new x coordinates and int[1] for new y
	 *         coordinates
	 */
	private int[] combine(int n, int[] position, int i){
		switch(this){
		case PAWN: return PiecesCombine.pawnCombine(n, position, i);
		case BISHOP: return PiecesCombine.bishopCombine(n, position, i);
		case KNIGHT: return PiecesCombine.knightCombine(n, position, i);
		case ROOK: return PiecesCombine.rookCombine(n, position, i);
		case QUEEN: return PiecesCombine.queenCombine(n, position, i);
		case KING: return PiecesCombine.kingCombine(n, position, i);
		default: return new int[]{0, 0};
		}
	}//end of Combine method

	/**
	 * Returns if the the king is in the right position to castle.
	 * 
	 * @param position - (int[]) position of the king
	 * @param turn - (turn) the turn of the king
	 * @return boolean - if the king is in the right position to castle
	 */
	private boolean kingInCastlePosition(int[] position, Turn turn) {
		if (turn == WHITE && position[0] == KING_XPOSITION && position[1] == WHITE_SIDE_YPOSITION ) {
			return true;
		} else if (turn == BLACK && position[0] == KING_XPOSITION && position[1] == BLACK_SIDE_YPOSITION) {
			return true;
		}
		return false;
	}//end of kingInCastlePosition

	private ArrayList<Move> getPawnPossibleMoves(int[] position, Turn turn, ChessBoard chessBoard) {
		ArrayList<Move> places = new ArrayList<>(4);
		ArrayList<Move> moveHistory = chessBoard.getMoveHistory();
		
		/*
		 * cases: cases for combine Direction of travel: the direction of
		 * Travel for the pawn
		 */
		int cases, directionOfTravel;

		// fill in information
		if (turn == WHITE) {
			cases = position[1] == PAWN_WHITE_STARTING_YPOSITION ? 4 : 3;
			directionOfTravel = PAWN_WHITE_TRAVEL_DIRECTION;
		} else {
			cases = position[1] == PAWN_BLACK_STARTING_YPOSITION ? 4 : 3;
			directionOfTravel = PAWN_BLACK_TRAVEL_DIRECTION;
		}

		for (int n = 0; n < cases; n++) {
			int coords[] = PiecesCombine.pawnCombine(n, position, directionOfTravel);

			if (inBounds(coords)) {
				
				// for the taking cases
				if (n < PAWN_TAKING_CASES) {
					
					//can take a piece
					if (chessBoard.containsEnemyPieceAt(coords, turn)) {
						addMove(chessBoard, position, coords, places);
						continue;
					}//end of taking if Statement

					// check if piece is in the right position for enPassant
					int[] attackingSquare = combine(n + 4, position, directionOfTravel);
					if (moveHistory.size() > 0 && pieceAvailableForEnpassant(chessBoard, position, attackingSquare,
							moveHistory.get(moveHistory.size() - 1), turn)) {
						
						// add move with special move being true
						places.add(new Move(chessBoard.getPieceAt(position), position, coords, new Piece(), new Piece(), true, false));

					}//end of enPassuant if statement
					
				} else {
					// for the moving forward case
					if (chessBoard.isEmptyAt(coords)) {
						places.add(new Move(chessBoard.getPieceAt(position), position, coords));
					} else {
						break;
					}
				}
			} // end of checking boundaries
		} // end of for loop
		return places;
	}
	
	/**
	 * Checks if a piece is available for enPassant
	 * @param chessBoard - (chessBoard) the chessBoard this is occurring on
	 * @param position - (int[]) the position of the pawn
	 * @param combine - (int[]) the position of the square the pawn is attacking
	 *            for enPassuant
	 * @param lastMove - (chessBoard) the last move done on the chessBoard
	 * @param turn - (Turn) the turn of the player pawn (not enemy)
	 * @return boolean - if the piece can enPassant
	 */
	private boolean pieceAvailableForEnpassant(ChessBoard chessBoard, int[] position, int[] combined, Move lastMove, Turn turn) {

		// rightPlace: if this pawn is in the right place
		int enPassantLine = turn == WHITE ? PAWN_WHITE_ENPASSANT_YPOSITION : PAWN_BLACK_ENPASSANT_YPOSITION;
		boolean rightPlace = position[1] == enPassantLine;

		// pieceAvailable: if the position contains a enemy pawn
		boolean pieceAvailable = chessBoard.containsEnemyPieceAt(combined, PAWN, turn);

		// checks if the enemy pawn started from a start position (since
		// pawn is in the right place, it has to have moved twice)
		boolean fromStartPosition = lastMove.startY() == PAWN_WHITE_STARTING_YPOSITION || lastMove.startY() == PAWN_BLACK_STARTING_YPOSITION;

		// check if the last moved moved is the enemy pawn, by checking the
		// end position
		boolean lastMovePawn = lastMove.hasEndPosition(combined);
		
		return rightPlace && pieceAvailable && fromStartPosition && lastMovePawn;
	}//end of pieceAvailableForEnpassant
		
	/**
	 * A container for combine methods of pieces. Here are what all the combine methods do: <br>
	 * All the combine methods are written (pieceType)Combine. <br> <br>
	 * Here is the javadoc of the combine method <br> 
	 * <br>
	 *
	 * Combines the numbers given for different possible placements of the piece <br>
	 * 
	 * @param n - (int) iteration number (case #)
	 * @param position- (int[2]) coordinates of the piece
	 * @param i - (int) value (the distance to use for a certain case)<br>
	 * 			e.g. for a rook on a case where it will go towards the right,
	 * 			it will go i values to the right
	 * @return int[] - int[0] for new x coordinates and int[1] for new y
	 *         coordinates
	 *
	 * @author frankliu197
	 */
	private static class PiecesCombine {
		
		/**
		 * A combine method for a PAWN: <br>
		 * Combines the numbers given for different possible placements of the piece <br>
		 * 
		 * @param n - (int) iteration number (case #)
		 * @param position- (int[2]) coordinates of the piece
		 * @param i - (int) value (the distance to use for a certain case)<br>
		 * @return int[] - int[0] for new x coordinates and int[1] for new y
		 *         coordinates
		 */
		private static int[] pawnCombine(int n, int[] position, int i){
			switch (n) {
			case 0:
				return new int[] { position[0] - 1, position[1] + i };
			// take piece on left case
			case 1:
				return new int[] { position[0] + 1, position[1] + i };
			// take piece on right case
			case 2:
				return new int[] { position[0], position[1] + i };
			// move 1 up case
			case 3:
				return new int[] { position[0], position[1] + i * 2 };
			// move 2 up case
			case 4:
				return new int[] { position[0] - 1, position[1] };
			// enpassuant case, to left
			case 5:
				return new int[] { position[0] + 1, position[1] };
			// enpassuant case, to right
			default:
				throw new IllegalArgumentException("WRONG NUMBER");
			}//end of switch statement
		}//end of pawnCombine method
			
		/**
		 * A combine method for a BISHOP: <br>
		 * Combines the numbers given for different possible placements of the piece <br>
		 * 
		 * @param n - (int) iteration number (case #)
		 * @param position- (int[2]) coordinates of the piece
		 * @param i - (int) value (the distance to use for a certain case)<br>
		 * @return int[] - int[0] for new x coordinates and int[1] for new y
		 *         coordinates
		 */
		private static int[] bishopCombine(int n, int[] position, int i) {
			switch (n) {
			case 0:
				return new int[] { position[0] + i, position[1] + i };
			// move diagonal up and right
			case 1:
				return new int[] { position[0] - i, position[1] - i };
			// down and left
			case 2:
				return new int[] { position[0] + i, position[1] - i };
			// right and down
			case 3:
				return new int[] { position[0] - i, position[1] + i };
			// left and up
			default:
				throw new IllegalArgumentException("WRONG NUMBER");
			}//end of switch statement
		}//end of bishopCombine statement
		
		/**
		 * A combine method for a KNIGHT: <br>
		 * Combines the numbers given for different possible placements of the piece <br>
		 * 
		 * @param n - (int) iteration number (case #)
		 * @param position- (int[2]) coordinates of the piece
		 * @param i - (int) value (the distance to use for a certain case)<br>
		 * @return int[] - int[0] for new x coordinates and int[1] for new y
		 *         coordinates
		 */
		private static int[] knightCombine(int n, int[] position, int i) {
			switch (n) {
			case 0:
				return new int[] { position[0] + 2, position[1] + 1 };
			case 1:
				return new int[] { position[0] + 2, position[1] - 1 };
			case 2:
				return new int[] { position[0] - 2, position[1] + 1 };
			case 3:
				return new int[] { position[0] - 2, position[1] - 1 };
			case 4:
				return new int[] { position[0] - 1, position[1] + 2 };
			case 5:
				return new int[] { position[0] - 1, position[1] - 2 };
			case 6:
				return new int[] { position[0] + 1, position[1] + 2 };
			case 7:
				return new int[] { position[0] + 1, position[1] - 2 };
			default:
				throw new IllegalArgumentException("WRONG NUMBER");
			}
		}//end of knightCombine method
		
		/**
		 * A combine method for a ROOK: <br>
		 * Combines the numbers given for different possible placements of the piece <br>
		 * 
		 * @param n - (int) iteration number (case #)
		 * @param position - (int[2]) coordinates of the piece
		 * @param i - (int) value (the distance to use for a certain case)<br>
		 * @return int[] - int[0] for new x coordinates and int[1] for new y
		 *         coordinates
		 */
		private static int[] rookCombine(int n, int[] position, int i) {
			switch (n) {
			case 0:
				return new int[] { position[0] + i, position[1] };
			// right case
			case 1:
				return new int[] { position[0] - i, position[1] };
			// left case
			case 2:
				return new int[] { position[0], position[1] - i };
			// down case
			case 3:
				return new int[] { position[0], position[1] + i };
			// up case
			default:
				throw new IllegalArgumentException("WRONG NUMBER");
			}
		}//end of rookCombine method
		
		/**
		 * A combine method for a QUEEN: <br>
		 * Combines the numbers given for different possible placements of the piece <br>
		 * 
		 * @param n - (int) iteration number (case #)
		 * @param position- (int[2]) coordinates of the piece
		 * @param i - (int) value (the distance to use for a certain case)<br>
		 * @return int[] - int[0] for new x coordinates and int[1] for new y
		 *         coordinates
		 */
		private static int[] queenCombine(int n, int[] position, int i) {
			return kingCombine(n, position, i);
		}//end of queenCombine method
		
		/**
		 * A combine method for a KING: <br>
		 * Combines the numbers given for different possible placements of the piece <br>
		 * 
		 * @param n - (int) iteration number (case #)
		 * @param position- (int[2]) coordinates of the piece
		 * @param i - (int) value (the distance to use for a certain case)<br>
		 * @return int[] - int[0] for new x coordinates and int[1] for new y
		 *         coordinates
		 */
		private static int[] kingCombine(int n, int[] position, int i) {
			if (n > 3) {
				return rookCombine(n - 4, position, i);
			} else {
				return bishopCombine(n, position, i);
			}
		}//end of kingCombine method	
	}//end of PiecesCombine static class
	
}//end of pieceType class