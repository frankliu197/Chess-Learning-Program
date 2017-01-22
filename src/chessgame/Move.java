/** 
 * This method has a class Move, which represents a move on the chess board
 * @author frankliu197
 * @since October 23, 2016
 */

package chessgame;

import static pieces.PieceType.*;

import java.io.Serializable;
import java.util.Arrays;

import pieces.Piece;
import pieces.PieceType;
import pieces.Turn;

public class Move implements Serializable, Cloneable {
	private final Piece piece;
	private final int startX;
	private final int startY;
	private final int endX;
	private final int endY;
	private String notes = "";
	private final Piece pieceTaken;
	private final boolean specialMove;
	private final Piece promotion;
	private final boolean checked;
	
	/**
	 * Move Constructor
	 * 
	 * @param piece
	 *            - piece being moved
	 * @param startPosition
	 *            - the starting coordinates of the piece
	 * @param endPosition
	 *            - the ended coordinates of the piece
	 * 
	 *            The other variables are set to their default values:
	 *            pieceTaken: Piece: turn = NONE, pieceType = EMPTY promotion:
	 *            Piece: turn = NONE, pieceType = EMPTY specialMove: boolean:
	 *            false, checked = false
	 */
	public Move(Piece piece, int[] startPosition, int[] endPosition) {
		this(piece, startPosition, endPosition, new Piece(), new Piece(), false, false);
	}

	/**
	 * Move constructor with the given variables:
	 * 
	 * @param piece
	 *            - piece being moved
	 * @param startPosition
	 *            - the starting coordinates of the piece
	 * @param endPosition
	 *            - the ended coordinates of the piece
	 * @param pieceTaken
	 *            - Piece, the piece that was taken in this move
	 * @param promotion
	 *            - Piece, the piece that this is promoted into
	 * @param specialMove
	 *            - boolean, if it is a castle or enPassuant
	 * @param checked - boolean, if after this move, the king is in check
	 */
	public Move(Piece piece, int[] startPos, int[] endPos, Piece pieceTaken, Piece promotion,
			boolean specialMove, boolean checked) {
		this(piece, startPos[0], startPos[1], endPos[0], endPos[1] , pieceTaken, promotion, specialMove, checked);
	}
	
	/**
	 * Move Constructor
	 * 
	 * @param piece
	 *            - piece being moved
	 * @param startX
	 *            - the starting X coordinates of the piece
	 * @param startY
	 *            - the starting Y coordinates of the piece
	 * @param endX
	 *            - the ending X coordinates of the piece
	 * @param endY
	 *            - the ending Y coordinates of the piece
	 *            The other variables are set to their default values:
	 *            pieceTaken: Piece: turn = NONE, pieceType = EMPTY promotion:
	 *            Piece: turn = NONE, pieceType = EMPTY specialMove: boolean:
	 *            false, check = false
	 */
	public Move(Piece piece, int startX, int startY, int endX, int endY) {
		this(piece, startX, startY, endX, endY , new Piece(), new Piece(), false, false);
	}
	
	/**
	 * Makes a move method with the information from the move, and the checked value changed into the given on
	 * @param move - (Move) the move to get information from
	 * @param checked - (boolean) if after this move the king is in check
	 */
	public Move(Move move, boolean checked){
		this(move.piece, move.startX, move.startY, move.endX, move.endY, move.pieceTaken, move.promotion, move.specialMove, checked);
	}
	
	/**
	 * Creates a move with the following parameters
	 * 
	 * @param move
	 *            - Move. Its startingPosition, endingPosition, checked and piece
	 *            information, and check is used to create this move
	 * @param pieceTaken
	 *            - Piece, the piece that was taken in this move
	 * @param promotion
	 *            - Piece, the piece that this is promoted into
	 * @param specialMove
	 *            - boolean, if it is a castle or enPassuant
	 */
	public Move(Move move, Piece pieceTaken, Piece promotion, boolean specialMove) {
		this(move.getPiece(), move.getStartPosition(), move.getEndPosition(), pieceTaken, promotion, specialMove, move.isChecked());
	}



	/**
	 * Move Constructor
	 * 
	 * @param piece2
	 *            - piece being moved
	 * @param startX
	 *            - the starting X coordinates of the piece
	 * @param startY
	 *            - the starting Y coordinates of the piece
	 * @param endX
	 *            - the ending X coordinates of the piece
	 * @param endY
	 *            - the ending Y coordinates of the piece
	 * @param pieceTaken
	 *            - Piece, the piece that was taken in this move
	 * @param promotion
	 *            - Piece, the piece that this is promoted into
	 * @param specialMove
	 *            - boolean, if it is a castle or enPassuant
	 * @param checked - boolean, if after this move, the opponent king is in check
	 */
	public Move(Piece piece, int startX, int startY, int endX, int endY, Piece pieceTaken, Piece promotion, boolean specialMove, boolean checked) {
		this.piece = piece; 
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
		this.pieceTaken = pieceTaken;
		this.promotion = promotion;
		this.specialMove = specialMove;
		this.checked = checked;
	}

	/**
	 * Returns the piece that is used in this move
	 * 
	 * @return Pieces - the piece used
	 */
	public Piece getPiece() {
		return piece;
	}

	/**
	 * Returns the starting coordinates that the piece
	 * 
	 * @return int[2] - the coordinates
	 */
	public int[] getStartPosition() {
		return new int[]{startX, startY};
	}

	/**
	 * Returns if the opponent king is in check after this move
	 * @return check - boolean if the opponent king is in check
	 */
	public boolean isChecked() {
		return checked;
	}
	
	/**
	 * Returns the ending coordates of the piece
	 * 
	 * @return int[2] - the coordinates
	 */
	public int[] getEndPosition() {
		return new int[]{endX, endY};
	}

	/**
	 * Returns whether or not the move is equal to this move. The move is equal
	 * if the piece being moved, and the starting and ending coordinates are the
	 * same
	 * 
	 * @return boolean - true if equal and false if not
	 */
	public boolean equals(Object object) {
		if (object instanceof Move) {
			Move move = (Move) object;
			if (move.piece.equals(piece) && startX == move.startX && startY == move.startY && endX == move.endX && endY == move.endY) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Sets the notes of the move to as given
	 * 
	 * @param notes
	 *            - String
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}

	/**
	 * Returns the notes of this move
	 * 
	 * @return notes - String
	 */
	public String getNotes() {
		return notes;
	}

	/**
	 * Returns a string representation of this class
	 * 
	 * @return String - a to string version of this move class
	 */
	public String toString() {
		String additional = "";
		if (checked){
			additional += "+";
		}
		return piece + Display.getLetter(startX) + (startY + 1) + "-"
				+ Display.getLetter(endX) + (endY + 1) + additional;
	}

	/**
	 * Returns the piece that was taken
	 * @return Piece - the piece that was taken
	 */
	public Piece getPieceTaken() {
		return pieceTaken;
	}

	/**
	 * Returns the starting x position
	 * @return int - the starting x position
	 */
	public int startX() {
		return startX;
	}

	/**
	 * Returns the starting y position
	 * @return int - the starting y position
	 */
	public int startY() {
		return startY;
	}

	/**
	 * Returns the ending x position
	 * @return int - the ending x position
	 */
	public int endX() {
		return endX;
	}

	/**
	 * Returns the ending y position
	 * @return int - the ending y position
	 */
	public int endY() {
		return endY;
	}

	/**
	 * returns the piece that this piece was promoted into
	 * @return Piece - the piece this piece was promoted into
	 */
	public Piece getPromotion() {
		return promotion;
	}

	/**
	 * returns a boolean on whether this move was a special move (aka. enPassuant OR castle)
	 * @return boolean - if this move was a special move (aka. enPassuant OR castle)
	 */
	public boolean isSpecialMove() {
		return specialMove;
	}
	
	/**
	 * Returns if the end position of this move is the same as the end position given
	 * @param x - x coordinate
	 * @param y - y coordinate
	 * @return boolean - true if the coordinates are the same
	 */
	public boolean hasEndPosition(int x, int y){
		return x == endX && y == endY;
	}
	
	/**
	 * Returns if the end position of this move is the same as the end position given
	 * @param position - int[], the psotion of the piece
	 * @return boolean - true if the coordinates are the same
	 */
	public boolean hasEndPosition(int[] position){
		return hasEndPosition(position[0], position[1]);
	}
	
	/**
	 * Returns if the start position of this move is the same as the start position given
	 * @param x - x coordinate
	 * @param y - y coordinate
	 * @return boolean - if the coordinates are the same
	 */
	public boolean hasStartPosition(int x, int y){
		return x == startX && y == startY;
	}
	
	/**
	 * Returns if the start position of this move is the same as the start position given
	 * @param position - int[], the psotion of the piece
	 * @return boolean - if the coordinates are the same
	 */
	public boolean hasStartPosition(int[] position){
		return hasStartPosition(position[0], position[1]);
	}

	/**
	 * Returns if the element has the same end or Start Position
	 * @param x - x position
	 * @param y - y position
	 * @return boolean - true if the move has the same end or start position
	 */
	public boolean hasPosition(int x, int y) {
		return hasStartPosition(x, y) || hasEndPosition(x, y);
	}
	
	/**
	 * Returns if the element has the same end or Start Position
	 * @param int[] - the position of the piece
	 * @return boolean - true if the move has the same end or start position
	 */
	public boolean hasPosition(int[] position) {
		return hasPosition(position[0], position[1]);
	}
	
	/**
	 * Returns if the move contains a certain pieceType
	 * @param pieceType - pieceType to check
	 * @return boolean - if the piece that moved contains this pieceType
	 */
	public boolean contains(PieceType pieceType){
		return piece.contains(pieceType);
	}

	/**
	 * Returns if the move contains a certain turn
	 * @param turn - turn to check
	 * @return boolean - true if the turn given in is the turn of the move
	 */
	public boolean contains(Turn turn){
		return piece.contains(turn);
	}
	
	/**
	 * Returns the turn of this move
	 * @return turn - turn of this move
	 */
	public Turn getTurn(){
		return piece.getTurn();
	}
	
	/**
	 * returns if this move contains a certain piece
	 * @param piece - pieceToCheck
	 * @return boolean - ture if the move contains the piece given in
	 */
	public boolean contains(Piece piece){
		return this.piece.equals(piece);
	}
	
	/**
	 * Returns if this Move has taken a certain Piecetype as given
	 * @param pieceType - the pieceType of the pieceTake
	 * @return boolean - ture if the pieceType is of the move taken is the same as the one given
	 */
	public boolean takenPieceType(PieceType pieceType){
		return pieceTaken.getPieceType() == pieceType;
	}
	
	/**
	 * Returns if the move contains a pawn being promoted
	 * @return boolean - true if the move contains a pawn being Promoted
	 */
	public boolean isPromoted(){
		return !promotion.equals(new Piece());
	}
	
	/**
	 * Returns if the move contains an Enpassuant
	 * @return boolean - true if the move contains a enpassuant
	 */
	public boolean hasEnpassuant(){
		return piece.getPieceType()== PAWN && specialMove;
	}
	
	/**
	 * Returns if the move contains a castle
	 * @return boolean - true if the move contains a castle
	 */
	public boolean hasCastled(){
		return piece.getPieceType()== KING && specialMove;
	}

	/**
	 * Returns the pieceType of the piece moved
	 * @return PieceType - the pieceType of the piece moved
	 */
	public PieceType getPieceType(){
		return piece.getPieceType();
	}
}
