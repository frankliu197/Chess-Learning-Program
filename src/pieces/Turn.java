/**
 * This class contains the two turns and their names
 * @author frankliu197
 * @since October 3, 2016
 */
package pieces;

import java.util.Locale;

import chessgame.ChessBoard;
import io.Difficulty;

public enum Turn {
	WHITE, BLACK, NONE;
	
	/**
	 * Returns a string representation of this object
	 * @return String - white for WHITE and black for BLACK
	 */
	public String toString(){
			return this.name().substring(0, 1) + 
					this.name().substring(1, this.name().length()).toLowerCase();
	}
	
	/**
	 * Returns a turn of the current index given depending on this.ordinal
	 * @param i (int) - the index to use
	 * @return turn - the corresponding turn variable
	 */
	public static Turn valueOf(int i){
		return values()[i];
	}
	
	/**
	 * Returns the win type of a certain turn with reference to the win types in the ChessBoard class
	 * @return String[] - some of the public static final String winTypse in the ChessBoard class
	 */
	public String winType(){
		if (this == WHITE){
			return ChessBoard.WHITE_WINS;
		} if (this == BLACK){
			return ChessBoard.BLACK_WINS;
		} else {
			return null;
		}
	}
}// end of enum
