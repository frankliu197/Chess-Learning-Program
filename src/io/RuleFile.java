package io;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import chessgame.ChessBoard;
import chessgame.Move;
import pieces.Piece;
import pieces.PieceType;
import pieces.Turn;

/**
 * This class stores a rule file with other instance variables
 * 
 * @author frankliu197
 * @since October 23
 */
public final class RuleFile extends GeneralFile implements Serializable{
	/**The folder name for Rules*/
	protected static final String FOLDER_NAME = "Rules";
	
	/** The name of the Rule File */
	private String name;
	
	/** The description of the Rule File */
	private String description;
	
	/** The element to animate for the Rule File */
	private SpecificFile animation;
	
	/**If the rule File is mastered or Not*/
	private boolean mastered;
	
	/**The lesson number of the rule file */
	private int lessonNumber;
	
	/**
	 * Rules file should never be created. All rules files should be already existing in the
	 * files.
	 * However, it may used for an employee to create a new rule
	 * @param file (File) - the file location of this rule
	 */
	private RuleFile(File file) {
		super(file);
	}
	
	
	/**
	 * Returns the description of this rule
	 * @return description (String) - the description of this rule
	 */
	public String getDescription(){
		return description;
	}
	
	/**
	 * This method returns the name of this ruleFile
	 * @return String - the name of this ruleFiles
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the chess board moves that should be animated
	 * @return animation (SpecificFile File)
	 */
	public SpecificFile getAnimation() {
		return animation;
	}


	/**
	 * Assessor method for if they have mastered this
	 * @return mastered (boolean) - true if they have mastered this RuleFile and false otherwise
	 */
	public boolean isMastered() {
		return mastered;
	}
	
	/**
	 * This is to access the lesson number of the rule file in order to determine where it should
	 * be relative to other rule files.
	 * @return int- lessonNumber
	 */
	public int getLessonNumber(){
		return lessonNumber;
	}
	
	/**
	 * Set if they have mastered this rule. This information will be saved to file.
	 * @param mastered (boolean) - true if they have mastered this RuleFile and false otherwise
	 */
	public void setMastered(boolean mastered) {
		this.mastered = mastered;
		FileOrganizer.saveFile(this);
	}


	/**
	 * This is a private method for creating new RuleFiles. It will write the rule file to file. Create a main method in this class
	 * to activate this method.
	 * @param name (String) - name of this ruleFile
	 * @param description (String) - description of this ruleFile
	 * @param animation (SpecificFIle) - the animation associated with this rule file
	 * @param mastered (boolean) - if the user has mastered this rule file or not
	 * @param lessonNumber (int) - the lesson number of the current rule.
	 */
	private static final void createNewRuleFile(String name, String description, SpecificFile animation, int lessonNumber){
		RuleFile rule = new RuleFile(new File(FOLDER_NAME + "/" + name + ".txt"));
		rule.name = name;
		rule.description = description;
		rule.animation = animation;
		rule.lessonNumber = lessonNumber;
		rule.mastered = false;
		FileOrganizer.saveFile(rule);
	}
	
	public static void main(String ... args){
		ArrayList<Move> move = new ArrayList<>();
		TacticFile file = new TacticFile(new ChessFile(null, null, SpecificFile.TACTIC_FILE, null ));
		move.add(new Move(new Piece(Turn.WHITE,PieceType.QUEEN), 3,0,6,3));
		move.add(new Move(new Piece(Turn.BLACK,PieceType.QUEEN), 3,7,3,3));
		move.add(new Move(new Piece(Turn.WHITE,PieceType.QUEEN), 6,3,3,3));
		Piece[][] board = defaultBoard();
		board[3][0] = new Piece(Turn.WHITE, PieceType.QUEEN);
		board[3][7] = new Piece(Turn.BLACK, PieceType.QUEEN);
		
		file.resetInformation(board, move, null);
		createNewRuleFile("Queen", "The queen is the most powerfull piece. It is a combination of the rook and the bishop and can move"
				+ " as if it were both of those pieces combined, moving either as many spaces as it likes vertically, horizontally,"
				+ " or diagonally", file, 5);
	}






	
	private static Piece[][] defaultBoard(){
		Piece[][] piece = new Piece[8][8];
		
		for (int i = 0; i < piece.length;i++){
			for (int j = 0; j < piece.length; j++){
				piece[i][j] = new Piece();
			}
		}
		
		return piece;
	}
}//end of rule class