package io;

import java.io.File;
import java.io.Serializable;

import io.FileOrganizer.SortingMethod;
import pieces.Turn;

/**
 * This class is a wrapper class of a chess file, which contains the file address of the useful
 * gamefile/tacticfile/openingfile, and its properties
 * @author Frank Liu
 * @since October 3, 2016
 */
public class ChessFile extends GeneralFile implements Serializable {
	/*default values */
	/**The Default Value for Turn for a chessFile. <Br> NOTE: if a field cannot be edited, use the default value*/
	public static final Turn DEFAULT_TURN = Turn.WHITE;
	
	/**The Default Value for Difficulty for a chessFile. <Br> NOTE: if a field cannot be edited, use the default value*/
	public static final Difficulty DEFAULT_DIFFICULTY = Difficulty.BEGINNER;
	
	/**The Default Value for Mastered for a chessFile. <Br> NOTE: if a field cannot be edited, use the default value*/
	public static final boolean DEFAULT_MASTERED = false;
	
	/* FIELD NAMES */
	/**The Property reference for Name*/
	public static final String NAME = "Name";
	
	/**The Property reference for Difficulty*/
	public static final String DIFFICULTY = "Difficulty";
	
	/**The Property reference for Turn*/
	public static final String TURN = "Turn";
	
	/**The Property reference for SpecificFileType*/
	public static final String SPECIFIC_FILE_TYPE = "Type";
	
	/**The Property reference for Mastered*/
	public static final String MASTERED = "Mastered";
	
	/**Returns a List of Properties, and in the right order to display them. Does not Include specificFileType*/
	public static final String[] PROPERTY_LIST = {NAME, DIFFICULTY, TURN, MASTERED};
	
	/**Index for name in Property_LIST*/
	public static final int NAME_INDEX = 0;
	
	/**Index for difficulty in Property_LIST*/
	public static final int DIFFICULTY_INDEX = 1;
	
	/**Index for turn in Property_LIST*/
	public static final int TURN_INDEX = 2;
	
	/**Index for mastered in Property_LIST*/
	public static final int MASTERED_INDEX = 3;
	
	/**The folder name that holds all chessFiles*/
	protected static final String FOLDER_NAME = "ChessFile";
	
	/* instance variables */
	/**The file location of the specificFile*/
	private File specificFileLocation;
	
	/** The name of the ChessFile */
	private String name;
	
	/** The difficulty of the ChessFile */
	private Difficulty difficulty;
	
	/** The Turn of the ChessFile */
	private Turn turn;
	
	/** If the chessFile is mastered or not */
	private boolean mastered;
	
	/** The SpecificFile type this holds */
	private String specificFileType;
	
	/**
	 * Creates a chess file with the following properties. <br>
	 * The file this chessFile links to will not be created. You need 
	 * to use the FileOrganizer class to do this. 
	 * 
	 * @param name (String) - the name of the file that this contains (e.g. Ponziani Opening)
	 * @param difficulty (Difficulty) - difficulty level of the file this contains
	 * @param turn (Turn)- who starts in the game
	 * @param string (SpecificFile Constant) - the type of file that this will contain ie. tactic, with the first letter capped
	 * 
	 */
	protected ChessFile(String name, Difficulty difficulty, String type, Turn turn) {
		super(new File(FOLDER_NAME + "/" + type + "/" + name + ".txt"));
		this.specificFileLocation = new File(SpecificFile.FOLDER_NAME + "/" + type + "/" + name + ".txt");
		this.name = name;
		this.difficulty = difficulty;
		this.turn = turn;
		this.specificFileType = type;
	}

	/**
	 * Open this chessFile. Same as FileOrganizer.openChessFile(this).
	 * @return the specificFile this file contains
	 */
	public SpecificFile open(){
		return FileOrganizer.openChessFile(this);
	}
	
	/**
	 * Returns whether a field for the specificFile can be edited. <br>
	 * If canEdit is false, the value should be set to its default value, except for 
	 * the specificFileType (where there is no specificFile). <br>
	 * 
	 * @param fieldName (String) - the fieldName of the field, one of the final values in this ChessFile
	 * @param specificFileType (String) - a SpecificFileType
	 * @return boolean - if this field can be edited
	 */
	public static boolean canEdit(String fieldName, String specificFileType){
		if (fieldName.equals(TURN)){
			return false;
		}
		if (fieldName.equals(DIFFICULTY)){
			return !specificFileType.equals(SpecificFile.SAVED_FILE);
		}
		if (fieldName.equals(NAME)){
			return true;
		}
		if (fieldName.equals(SPECIFIC_FILE_TYPE)){
			return false;
		}
		if (fieldName.equals(MASTERED)){
			if (specificFileType.equals(SpecificFile.SAVED_FILE) || specificFileType.equals(SpecificFile.GAME_FILE)){
				return false;
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Returns whether a field for the specificFile should be shown. <br>
	 * 
	 * @param fieldName (String) - the fieldName of the field, one of the final values in this ChessFile
	 * @param specificFileType (String) - a SpecificFileType
	 * @return boolean - if this should be showns
	 */
	public static boolean canShow(String fieldName, String specificFileType){
		if (fieldName.equals(TURN)){
			//only game file can't show
			return !specificFileType.equals(SpecificFile.GAME_FILE);
		}
		if (fieldName.equals(DIFFICULTY)){
			return specificFileType != SpecificFile.SAVED_FILE;
		}
		if (fieldName.equals(NAME)){
			return true;
		}
		if (fieldName.equals(SPECIFIC_FILE_TYPE)){
			return false;
		}
		if (fieldName.equals(MASTERED)){
			if (specificFileType.equals(SpecificFile.SAVED_FILE) || specificFileType.equals(SpecificFile.GAME_FILE)){
				return false;
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Returns whether this specificFile can be Practised <br>
	 * 
	 * @param fieldName (String) - the fieldName of the field, one of the final values in this ChessFile
	 * @param specificFileType (String) - a SpecificFileType
	 * @return boolean - if this field can be practised
	 */
	public static boolean canBePractised(String specificFileType){
		return specificFileType.equals(SpecificFile.OPENING_FILE) || specificFileType.equals(SpecificFile.TACTIC_FILE);
	}
	
	
	/**
	 * Returns whether this specificFile can be Edited
	 * @param fieldName (String) - the fieldName of the field, one of the final values in this ChessFile
	 * @param specificFileType (String) - a SpecificFileType
	 * @return boolean - if this field can be practised
	 */
	public static boolean canBeEdited(String specificFile) {
		return specificFile != SpecificFile.SAVED_FILE;
	}

	
	/**
	 * Gets the sorting method for the given property
	 * @param property (String) - one of the final variables
	 * @return Sorting Method[2] - the two sorting methods to use
	 */
	public static SortingMethod[] getSortingMethod(String property) {
		switch (property){
		case NAME: return new SortingMethod[]{SortingMethod.A_TO_Z, SortingMethod.Z_TO_A};
		case DIFFICULTY: return new SortingMethod[]{SortingMethod.EASY_TO_HARD, SortingMethod.HARD_TO_EASY};
		case TURN: return new SortingMethod[]{SortingMethod.WHITE_TO_BLACK, SortingMethod.WHITE_TO_BLACK};
		case MASTERED: return new SortingMethod[]{SortingMethod.MASTERED, SortingMethod.NOT_MASTERED};
		default: return null;
		}
	}

	/**
	 * This method returns the Difficulty of the opening/tactic/Game the file
	 * contains
	 * @return Difficulty - returns the difficulty of the opening/tactic/game file
	 */
	public Difficulty getDifficulty() {
		return difficulty;
	}

	/**
	 * This method returns the name of the opening/tactic/Game/saved file
	 * @return String - the name of this opening/tactic/Game/saved
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get a certain property of the file
	 * @param property - property to get
	 * @return String - a toString version of the property value
	 */
	public String getProperty(String property){
		switch (property){
		case NAME: return name;
		case DIFFICULTY: return difficulty.toString();
		case TURN: return turn.toString();
		case MASTERED: return new Boolean(mastered).toString();
		case SPECIFIC_FILE_TYPE: return specificFileType;
		default: return null;
		}
	}

	/**
	 * Returns this specificFile type.
	 * @return type (String) - one of the specific File constants
	 */
	public String getSpecificFileType(){
		return specificFileType;
	}//end of getSpecificFileType method

	/**
	 * Get the turn for the specificFile this contains
	 * @return turn (Turn) - turn of this specificFile 
	 */
	public Turn getTurn(){
		return turn;
	}//end of getTurn method

	/**aa
	 * Returns whether the person has mastered this specificFile
	 * @return mastered (boolean) - true if they have mastered this ChessFile and false otherwise
	 */
	public boolean isMastered(){
		return mastered;
	}//end of isMastered

	/**
	 * Edit the mastered variable. This change will be saved to File
	 * @param mastered (boolean) - true if they have mastered this ChessFile and false otherwise
	 */
	public void setMastered(boolean mastered){
		this.mastered = mastered;
		FileOrganizer.saveFile(this);
	}

	/**
	 * Updates the information in the chessFile and stores it in file <Br>
	 * NOTE: danger of overwriting.
	 * @param name (String) - the new name for the chessFile
	 * @param difficulty (Difficulty) - the new difficulty of the chessFile
	 * @param turn (turn) - the new turn of the chessFile
	 * @param mastered (boolean) - if this specificFile is maastered
	 */
	public void updateInformation(String name, Difficulty difficulty, Turn turn, boolean mastered){
		SpecificFile specificFile = FileOrganizer.openChessFile(this);
		
		this.name = name;
		this.difficulty = difficulty;
		this.turn = turn;
		this.mastered = mastered;
		this.specificFileLocation = new File(SpecificFile.FOLDER_NAME + "/" + specificFileType + "/" + this.name + ".txt");
		this.setFileLocation(new File(FOLDER_NAME + "/" + specificFileType + "/" + this.name + ".txt"));
		specificFile.setFileLocation(getSpecificFileLocation());
	}//end of updateInformation method

	/**
	 * Returns the name, difficulty, turn and location of the specificFile
	 * @return String - "ChessFile: " + name + ", Difficulty." + difficulty + ", Turn." + turn + ", SpecificFileLocation:" + specificFileLocation
	 */
	public String toString(){
		return "ChessFile: " + name + ", Difficulty." + difficulty + ", Turn." + turn + ", SpecificFileLocation:" + specificFileLocation;
	}

	/**
	 * This method returns the file location of the specificFile
	 * @return File - the file wrapper class of specificFile
	 */
	protected File getSpecificFileLocation() {
		return specificFileLocation;
	}
	
	@Override 
	protected void setFileLocation(File file){
		specificFileLocation = new File(SpecificFile.FOLDER_NAME + "/" + specificFileType + "/" + name + ".txt");
		super.setFileLocation(file);
	}
}// end of class
