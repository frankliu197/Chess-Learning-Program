package io;

import java.util.Locale;

/**
 * Stores the 4 possible difficulties
 */
public enum Difficulty {
	BEGINNER, INTERMEDIATE,	ADVANCED, PROFESSIONAL, NONE;
	
	@Override
	public String toString(){
		//returns the enum with all letters lowercased but the first one
		return this.name().substring(0, 1) + 
					this.name().substring(1, this.name().length()).toLowerCase();
	}
	
	/**
	 * Returns a difficulty of the current index given depending on this.ordinal
	 * @param i (int) - the index to use
	 * @return difficulty - the corresponding difficulty variable
	 */
	public static Difficulty valueOf(int i){
		return values()[i];
	}
}// end of enum
