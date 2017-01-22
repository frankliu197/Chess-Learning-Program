package chessgame;

import static pieces.PieceType.*;
import static pieces.Turn.*;
import gui.AlertPane;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;

import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.FontUIResource;

import ai.AI;
import chessgame.ChessBoard;
import chessgame.Display;
import chessgame.MovePanel;
import gui.GUI;
import gui.GUI.GuiMenus;
import io.ChessFile;
import io.Difficulty;
import io.FileOrganizer;
import io.RuleFile;
import io.SavedFile;
import io.SpecificFile;
import io.SpecificFile.SpecificFileIterator;
import pieces.Piece;
import pieces.PieceType;
import pieces.Turn;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 * This class is the display class of the chessBoard. It also controls the undo
 * buttons functions but not position
 * 
 * @since November 25
 * @author Frank and Robert
 *
 */
public class Display extends JPanel implements ActionListener {	
	/** Color variable showing where a piece can move */
	private static final Color TO_MOVE = Color.YELLOW;
	
	/** Color variable showing which pieces can be taken by the selected piece*/
	private static final Color TO_TAKE = Color.RED;
	
	/** Color variable showing that a piece has moved last turn*/
	private static final Color HAS_MOVED = new Color(255, 240, 153);
	
	/** Color variable showing that a piece has moved into a spot where it can be taken and has moved last turn*/
	private static final Color HAS_MOVED_AND_CAN_TAKE = new Color(255, 180, 102);
	
	/**  Color variable showing that a piece has moved into a spot where it can be taken */
	private static final Color CAN_TAKE = new Color(255, 102, 102);
	
	/**Reference variable for undo for index in arrays optionsTxt, optionsBtns and optionsActions */
	public static final int UNDO = 0;
	
	/**Reference variable for resign for index in arrays optionsTxt, optionsBtns and optionsActions */
	public static final int RESIGN = 1;
	
	/**Reference variable for request draw for index in arrays optionsTxt, optionsBtns and optionsActions */
	public static final int REQUEST_DRAW = 2; 
	
	/**Reference variable for declare draw for index in arrays optionsTxt, optionsBtns and optionsActions */
	public static final int DECLARE_DRAW = 3;
	
	/**Reference variable for save to game for index in arrays optionsTxt, optionsBtns and optionsActions */
	public static final int SAVE_TO_GAME = 4;
	
	/**Reference variable for restart for index in arrays optionsTxt, optionsBtns and optionsActions */
	public static final int RESTART = 5;
	
	/**Reference variable for options for index in arrays optionsTxt, optionsBtns and optionsActions */
	public static final int OPTIONS = 6;
	
	/**Reference variable for undo for index in arrays optionsTxt, optionsBtns and optionsActions */
	public static final int SWITCH_PLAYERS = 7;
	
	/**Reference variable for Delete for index in arrays optionsTxt, optionsBtns and optionsActions */
	public static final int DELETE_MOVE = 8;
	
	/**Reference variable for next for index in arrays optionsTxt, optionsBtns and optionsActions, this is used to show next move for game and tactic files */
	public static final int NEXT_MOVE = 9;
	
	/**The total Number of option Variables*/
	public static final int TOTAL_OPTIONS = 10;
	
	/** Text for option buttons */
	private static final String[] optionsTxt = { "Undo", "Resign", "Request Draw", "Declare Draw", "Save Game",
			"Restart", "Options", "", "Delete Moves", "Next Move"};
	
	/** Option buttons */
	private JButton[] optionsBtns;
	
	/** Action listener for buttons */
	private ActionListener[] optionsActions;
	
	/** Action listener that all option buttons have */
	private ActionListener additionDisplayAction;
	
	/** The action the display should do before leaving */
	private ActionListener leaveAction;

	/*
	 * Important instance Variables
	 */
	private int[] startPos = { -1, -1 };
	private ChessBoard chessBoard;
	private ArrayList<Move> canMove = new ArrayList<>();
	private boolean help;
	private boolean ruleFile = false;

	/*
	 * Important GUI variables:
	 */
	private JButton[][] display;
	private JLabel[] xLabel;
	private JLabel[] yLabel;
	private MovePanel movePanel;
	private GUI gui;
	private SpecificFileIterator iterator;
	private boolean practise;
	private int dimension;
	private int border;
	private int correctingFactor;
	
	/**
	 * Instance Initializer sets up the buttons and put them with the actionlisteners
	 */
	{
		optionsBtns = new JButton[TOTAL_OPTIONS];
		optionsActions = new ActionListener[TOTAL_OPTIONS];
	
		leaveAction = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if (iterator != null){
					iterator.save();
				}
			}
		};
		
		// create all action listeners
		optionsActions[UNDO] = new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				undo();
				setEnabledForButtons();
			}
		};
		
		optionsActions[NEXT_MOVE] = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				move(iterator.getNextMove());
				setEnabledForButtons();
			}
		};
		
		optionsActions[DELETE_MOVE] = new ActionListener(){
			@Override public void actionPerformed(ActionEvent e){
				try {
					iterator.deleteMove();
					customUndo();
				} catch(IllegalStateException d){
					int i = AlertPane.showConfirmDialog("Delete this move and future Moves?");
					if (i == AlertPane.YES_OPTION){
						iterator.deleteMovesRegardless();
						customUndo();
					}
				}//end of try catch block
				
			}
			
			/**
			 * Custom undo for deleteMove only since iterator has already undoed
			 */
			private void customUndo(){
				chessBoard.undo();
				movePanel.undo();
				resetGraphics();
				canMove.clear();
				setEnabledForButtons();
			}
		};
		
		optionsActions[SWITCH_PLAYERS] = new ActionListener(){
			@Override public void actionPerformed(ActionEvent arg0) {
				if (optionsBtns[SWITCH_PLAYERS].getText().equals("Switch To Multiplayer")){
					optionsBtns[SWITCH_PLAYERS].setText("Switch To Single Player");
					changePlayers(0);
				} else {
					optionsBtns[SWITCH_PLAYERS].setText("Switch To Multiplayer");
				
					String[] options = {"1", "2", "3", "4", "5"};
					int n = AlertPane.showOptionDialog("Choose an AI level", 
						"Choose an AI level", AlertPane.QUESTION_MESSAGE, options, options[2], 0);
					changePlayers(n + 1);
				}
			}	
		};
		
		optionsActions[OPTIONS] = new ActionListener(){
			@Override public void actionPerformed(ActionEvent e) {
				showOptionsPane();
			}
		};
	
		optionsActions[RESIGN] = new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				if (getTurn() == WHITE){
					showGameOverPane(ChessBoard.BLACK_WINS + ": " + ChessBoard.WIN_BY_RESIGNATION);
				} else {
					showGameOverPane(ChessBoard.WHITE_WINS + ": " + ChessBoard.WIN_BY_RESIGNATION);
				}
			}
		};
	
		optionsActions[REQUEST_DRAW] = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				requestDraw();
			}
		};
	
		optionsActions[DECLARE_DRAW] = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				declareDraw();
			}
		};
	
		
		optionsActions[RESTART] = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (iterator != null){
					iterator.save();
					iterator.resetIterator();
					chessBoard = new ChessBoard(iterator.getStartingPosition(), iterator.getSpecificFileTurn());
				} else {
					chessBoard = new ChessBoard(chessBoard.aiExists(), chessBoard.getAiLevel(), chessBoard.getAiTurn());
				}
				movePanel.reset();
				resetGraphics();
				Display.this.setEnabledForButtons();
			}
		};
		
		optionsActions[SAVE_TO_GAME] = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String s = AlertPane.showInputDialog("Please name this game:");
				while (true) {
					if (s == null) {
						return;
					} else if (s.equals("")) {
						s = AlertPane.showInputDialog("Error. Please choose a valid name for this Game");
					} else if (FileOrganizer.chessFileExists(s, SpecificFile.GAME_FILE)) {
						if (AlertPane.showConfirmDialog("Game exists, overwrite?") == AlertPane.YES_OPTION){
							saveFile(s);
							break;
						} else {
							s = AlertPane.showInputDialog("Please choose a name for this game");
						}
						
					} else {
						saveFile(s);
						break;
					}
				}
				AlertPane.showMessageDialog("Saved!");
			}// end of action Performed Method
			
			/**
			 * Saves the file with file name s as a Saved Game File
			 * @param s (String) - file name
			 */
			private void saveFile(String s) {
				ChessFile c = FileOrganizer.createChessFile(s, ChessFile.DEFAULT_DIFFICULTY, Turn.WHITE,
						SpecificFile.GAME_FILE);
				SpecificFile specificFile = FileOrganizer.openChessFile(c);
				specificFile.resetInformation(ChessBoard.getDefaultBoard(), chessBoard.getMoveHistory(),
							chessBoard.getBoard());
				specificFile.save();
			}
		};
		
		additionDisplayAction = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				resetStartingVariable();
			}
		};
		
		/* Adds actionListener to JButtons
		 */
		for (int i = 0; i < TOTAL_OPTIONS; i++) {
			optionsBtns[i] = new JButton(optionsTxt[i]);
			optionsBtns[i].addActionListener(optionsActions[i]);
			optionsBtns[i].addActionListener(additionDisplayAction);
		}		
	}//end of block
	/**
	 * Get the leave Action that should be done before leaving the display
	 * @return ActionListener leaveAction
	 */
	public ActionListener getLeaveAction(){
		return leaveAction;
	}
	
	
	/**
	 * Constructor of Display - Makes a new chessBoard with graphics with the
	 * given parameters. <br>
	 * The Button params will be controlled by the display (ie ActionListener
	 * and setEnabled), but their bounds need to be controlled by the GUI
	 * @param gui - (GUI) The GUI that controls this display
	 * @param help  - (boolean) true to display pieces that are being attacked
	 * @param aiLevel - (int) Level of the ai
	 * @param turn - (Turn) Turn of the ai
	 */
	public Display(GUI gui, boolean aiExists, int aiLevel, Turn turn, boolean help) {
		this.gui = gui;
		this.chessBoard = new ChessBoard(aiExists, aiLevel, turn);
		this.help = help;
		this.movePanel = new MovePanel(this);
		initializeBoard();
	}// end of constructor
	
	/**
	 * Constructor of Display - Makes a new chessBoard with graphics with the
	 * given parameters. <br>
	 * The Button params will be controlled by the display (ie ActionListener
	 * and setEnabled), but their bounds need to be controlled by the GUI
	 * @param gui (GUI) - The GUI that controls this display
	 * @param file (SpecificFile) - The specific file to use for this chessboard
	 * @param practise (boolean) - if you are in practice mode
	 */
	public Display(GUI gui, SpecificFile file, boolean practise) {
		this.practise = practise;
		this.help = true;
		this.gui = gui;
		this.iterator = file.getSpecificFileIterator();
		this.chessBoard = new ChessBoard(file.getStartingPosition(), file.getTurn());
		
		if (!practise){
			movePanel = new MovePanel(MovePanel.SPECIFIC_FILE, this);
			if (file.isSpecificFileType(SpecificFile.OPENING_FILE)){ movePanel.setPossibleMoves(iterator.getPossibleMoves());}
		} else if (file.isSpecificFileType(SpecificFile.OPENING_FILE)){
			movePanel = new MovePanel(MovePanel.OPENING_PRACTISE, this);
		} else {
			movePanel = new MovePanel(MovePanel.TACTIC_PRACTISE, this);
		}

		initializeBoard();
	}// end of constructor
	

	public Display(GUI gui, RuleFile 
			
			file)
	{

		SpecificFile animation = file.getAnimation();
		this.help = false;
		this.gui = gui;
		this.chessBoard = new ChessBoard(animation.getStartingPosition(), file.getAnimation().getTurn());
		movePanel = new MovePanel(this);

		this.iterator = animation.getSpecificFileIterator();
		initializeBoard();
		ruleFile = true;
		
		Thread animate = new Thread(new Thread(){
			public void run(){
				
				while(true)
				{
					if (iterator.hasNextMove() == false)
					{
						iterator.resetIterator();
						chessBoard = new ChessBoard(file.getAnimation().getStartingPosition(), file.getAnimation().getTurn());
						resetGraphics();
						
					}
					else
					{
						move(iterator.getNextMove());
					}
					try {
						resetGraphics();
						sleep(3000);
						
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
		});
		animate.start();	
	}

	/**
	 * This method resets the starting positions array to its default value -1, -1
	 */
	private void resetStartingVariable(){
		startPos[0] = -1;
		startPos[1] = -1;
	}
	
	/**
	 * This is to be called after the constructor to initialize all the components
	 */
	private void initializeBoard() {
		setLayout(null);
		display = new JButton[8][8];
		xLabel = new JLabel[8];
		yLabel = new JLabel[8];

		for (int x = 0; x < ChessBoard.LENGTH; x++) {

			// horizontal labels
			xLabel[x] = new JLabel(getLetter(x));
			add(xLabel[x]);

			for (int y = 0; y < ChessBoard.LENGTH; y++) {

				// pieces
				display[x][y] = new JButton();
				display[x][y].addActionListener(this);
				add(display[x][y]);

				// vertical labels
				yLabel[y] = new JLabel(8 - y + "");
				add(yLabel[y]);
			}
		}
		
		optionsBtns[SWITCH_PLAYERS].setText(getSwitchPlayersText());
		setEnabledForButtons();
	}//end of initializeBoard method

	/**
	 * This method returns text should be displayed the the SwitchPlayers button
	 * @return string - text that should be displayed the the SwitchPlayers button
	 */
	private String getSwitchPlayersText() {
		return chessBoard.aiExists()? "Switch To Multiplayer": "Switch To Single Player";
	}


	/**
	 * Get a button to set bounds and format from Display. <br>
	 * NOTE: you should not change its actionListener or text showing on it as
	 * they are already set
	 * @param buttonType (int) - a button to show. Use the public static final int variables
	 * @return JButton - the JButton that was requested
	 */
	public JButton getButton(int buttonType){
		return optionsBtns[buttonType];
	}

	/**
	 * Gets the movePanel that this display will control.
	 * @return MovePanel - the movePanel associated with this display
	 */
	public MovePanel getMovePanel() {
		return movePanel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ruleFile)
		{
			return;
		}
		
		JButton button = (JButton) e.getSource();
		
		//prevents pressing when ai is moving
		synchronized(this){
			// find where button is
			for (int x = 0; x < ChessBoard.LENGTH; x++) {
				for (int y = 0; y < ChessBoard.LENGTH; y++) {
					if (button == display[x][y]) {
						Color color = button.getBackground();
						
						/*
						 * Checks how the user clicks the button and takes action
						 * accordingly
						 */
						if (x == startPos[0] && y == startPos[1]) {
							// clicked on original piece
							resetStartingVariable();
							canMove.clear();
						} else if (color == TO_MOVE || color == TO_TAKE) {
							// they clicked on a movable square
							// find move from canMove
							
							for (Move move : canMove) {
								if (x == move.endX() && y == move.endY()) {
			
									//check if the iterator allows it
									if (iterator != null){
										//check if another move exists if not opening file
										if (!iterator.getSpecificFileType().equals(SpecificFile.OPENING_FILE) && iterator.getNextMove() != null && !iterator.setNextMove(move)){
											AlertPane.showMessageDialog("Another Move Already Exists");
											return;
										}
										
										//opening file. can't add more than one move for the person doing the opening
										if (iterator.getSpecificFileTurn() == getTurn() && iterator.getNextMove() != null && !iterator.setNextMove(move)){
											AlertPane.showMessageDialog("Another Move Already Exists");
											return;
										}
									}
									move(move);
								}
							}
							canMove.clear();

						} else if (chessBoard.getPieceAt(x, y).isEnemyPiece(chessBoard.getTurn())) {
							// they clicked on enemy piece they can't take
							resetStartingVariable();
							canMove.clear();
						} else if (chessBoard.getPieceAt(x, y).getTurn() == chessBoard.getTurn()) {
							// they clicked on thier own piece, a new one
							canMove.clear();
							startPos[0] = x;
							startPos[1] = y;
							canMove.addAll(chessBoard.getPieceAt(x, y).filterAvailableMoves(chessBoard.getAvailableMoves(),
									startPos));
							canMove.add(0, new Move(new Piece(), -1, -1, x, y));
						}
						break;
					} // end of if button == display[x][y] if statement
				}
			} // end of for loops

			// reset board colors
			resetGraphics();
			addMoveColors();
		}
		
		
	}//end of ActionListener

	/**
	 * Does proper procedures for requesting a draw as well as showing the gui
	 */
	private void requestDraw() {
		if (chessBoard.aiExists() && chessBoard.isPlayerTurn()) {
			
			//ai part
			if (chessBoard.getAI().askedForDraw()){
				AlertPane.showMessageDialog("Accepted Draw");
				showGameOverPane(ChessBoard.DRAW_ACCEPTED);
			} else {
				AlertPane.showMessageDialog("AI Declined Draw");
			}
			
		} else {
			if (askForDraw()) {
				showGameOverPane(ChessBoard.DRAW_ACCEPTED);
			}
		}

		optionsBtns[REQUEST_DRAW].setEnabled(false);
	}//end of requestDraw method

	/**
	 * Draws a line between the two positions for a desired move.
	 * @param arrayList (ArrayList Move) - the positions to draw move
	 *Does not work unfortuanately
	protected void drawLine(ArrayList<Move> arrayList) {
		
	
		//this.revalidate();
		//this.repaint();
		
		for (Move move: arrayList){
		
			Graphics2D g = (Graphics2D) getGraphics();
			g.setStroke(new BasicStroke(dimension / 10));
			g.setColor(Color.RED);
			
			//draw move
			g.drawLine(getCoord(move.startX()),
					getCoord(ChessBoard.LENGTH - 1 - move.startY()), getCoord(move.endX()),
					getCoord(ChessBoard.LENGTH - 1 - move.endY()));
			
			//draw arrows
			int coordX1, coordY1, coordX2, coordY2;
			//move down or up only
			if (move.startX() == move.endX()){
				int direction = 1;
				
				//move down
				if (move.startY() > move.endY()){
					direction = -1;
				}
				
				coordX1 = getCoord(move.endX() - 1); 
				coordY1 = getCoord(move.endY() - 1 * direction);

				coordX2 = getCoord(move.endX() + 1); 
				coordY2 = getCoord(move.endY() - 1 * direction);
			} 
			//move left or right only
			else if (move.startY() == move.endY()){
				int direction = 1;
				
				//move down
				if (move.startY() > move.endY()){
					direction = -1;
				}
				
				coordX1 = getCoord(move.endX() - 1 * direction); 
				coordY1 = getCoord(move.endY() - 1);

				coordX2 = getCoord(move.endX() + 1 * direction); 
				coordY2 = getCoord(move.endY() + 1);
			}
			//diagonal
			else {
				int directionX = 1; //arrow direction left
				int directionY = 1; //up
				
				//piece moving up
				if (move.startY() > move.endY()){
					directionY = -1;
				}
				
				//piece moving right
				if (move.startX() < move.endX()){
					directionX = -1;
				}
				
				coordX1 = getCoord(move.endX() - 1 * directionX); 
				coordY1 = getCoord(move.endY());

				coordX2 = getCoord(move.endX()); 
				coordY2 = getCoord(move.endY() + 1 * directionY);
			}
			
			int decreaseFactor = 3;
			g.drawLine(getCoord(move.endX()),
					getCoord(ChessBoard.LENGTH - 1 - move.endY()), coordX1 / decreaseFactor,
					coordY1 / decreaseFactor);
			g.drawLine(getCoord(move.endX()),
					getCoord(ChessBoard.LENGTH - 1 - move.endY()), coordX2 / decreaseFactor,
					coordY2 / decreaseFactor);
		}
	}
	
	/**
	 * Returns the proper x or y coordinates of a certain square in column or row c. For entering column c, you get an x coordinate and similarily for y. This returns the CENTER coord of that square.
	 * @param c - the column or row to get the coordinates for
	 * @return int - the coord for the piece
	 *
	private int getCoord(int c){
		return c * dimension + correctingFactor;
	}/
	
	/**
	 * Returns if the chessBoard can undo or not. The only reason it could not
	 * be able to undo would be if there are no moves in the moveHistory, or if
	 * the undo feature is disabled.
	 * 
	 * @return boolean - if the chessBoard can undo or not
	 */
	public boolean canUndo() {
		return chessBoard.canUndo();
	}

	/**
	 * get letter version of this coordinate
	 * 
	 * @param startPosition - an int from 0 - 7
	 * @return letterVersion (String) - e.g. int 0 is A, the technical chess
	 *         terminology for the numbers
	 */
	public static String getLetter(int startPosition) {
		return new Character((char) (startPosition + 97)) + "";
	}

	/**
	 * Moves the piece and gets a the promotion piece if necessary. It will move
	 * the AI to if it exists and add it to iterator.
	 * 
	 * @param move (Move) - Move to complete
	 */
	public void move(Move move) {
		
		//get promotion if required
		if (ruleFile){
			//skip
		} else if (iterator != null && !move.getPromotion().isEmpty() && ChessBoard.endOfBoard(move.endY())){
			//skip
		} else if(chessBoard.isPlayerTurn() && ChessBoard.endOfBoard(move.endY()) && move.contains(PAWN)) {
			
			ImageIcon[] image = new ImageIcon[4];
			PieceType[] pieceTypes = PieceType.values();
			
			for (int i = 0; i < image.length; i++) {
				image[i] = Piece.getPieceIcon(pieceTypes[i + 1], move.getTurn());
			}
			
			//get pieceType
			int n = AlertPane.showOptionDialog("Choose a Piece to promote to", "Promotion", AlertPane.QUESTION_MESSAGE, image, image[3], AlertPane.NO_FULL_SCREEN_BUTTONS);
			PieceType pieceType;
			
			switch(n){
			case 0: pieceType = BISHOP; 
				break;
			case 1: pieceType = KNIGHT; 
				break;
			case 2: pieceType = ROOK; 
				break;
			case 3: pieceType = QUEEN; 
				break;
			default: pieceType = EMPTY;
			}
			
			move = new Move(move, move.getPieceTaken(), new Piece(move.getTurn(), pieceType), move.isSpecialMove());
		} else if (ChessBoard.endOfBoard(move.endY()) && move.contains(PAWN)){
			move = new Move(move, move.getPieceTaken(), chessBoard.getAI().getPromotion(), move.isSpecialMove());
		}
		
		//do move procedures
		chessBoard.move(move);
		addMoveToPanel(move);
		
		//move iterator
		if (iterator != null){
			iterator.setNotes(movePanel.getDescriptionText());
			if (!iterator.setNextMove(move)){
				iterator.addMove(move);
			}
			
			movePanel.setDescriptionText(iterator.getNotes());
			if (iterator.isSpecificFileType(SpecificFile.OPENING_FILE)){
				movePanel.setPossibleMoves(iterator.getPossibleMoves());
			}
		}
		
		resetGraphics();
		setEnabledForButtons();
		
		//check win
		String winType = chessBoard.checkWin();
		if (winType.equals(ChessBoard.BLACK_WINS) || winType.equals(ChessBoard.WHITE_WINS)
				|| winType.equals(ChessBoard.STALEMATE) || winType.equals(ChessBoard.NOT_ENOUGH_MATERIAL)) {
			showGameOverPane(winType);
		}
		
		//do ai
		if (chessBoard.aiExists() && !chessBoard.isPlayerTurn()) {			
			
			Thread aiThread = new Thread(new Runnable(){
				public void run(){
					synchronized(Display.this){
						AI ai = chessBoard.getAI();
						move(chessBoard.getAIMove());
						if (ai.declaresDraw()) {
							declareDraw();
						} else if (ai.requestsDraw()) {
							requestDraw();
						}
					}
				}
			});	
			aiThread.start();
		}
	}//end of Move method

	/**
	 * Does proper Procedures for declaring a Draw
	 */
	private void declareDraw() {
		showGameOverPane(chessBoard.getDrawType());
	}

	/**
	 * Sets all the buttons to enabled or disabled as required in this situation. <Br>
	 */
	private void setEnabledForButtons() {
		boolean enabled = chessBoard.canUndo();
		optionsBtns[UNDO].setEnabled(enabled);
		optionsBtns[RESIGN].setEnabled(enabled);
		optionsBtns[REQUEST_DRAW].setEnabled(enabled);
		optionsBtns[DECLARE_DRAW].setEnabled(enabled && chessBoard.checkWin().equals(ChessBoard.POSSIBLE_DRAW));
		optionsBtns[DELETE_MOVE].setEnabled(enabled);
		optionsBtns[NEXT_MOVE].setEnabled(false);
	
		if (iterator != null && iterator.getPossibleMoves().size() == 1){
			optionsBtns[NEXT_MOVE].setEnabled(true);
		}
	}

	@Override
	public void setBounds(Rectangle r) {
		setBounds(r.x, r.y, r.width, r.height);
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		int size = width;
		if (width != height) {
			size = width > height ? height : width;
		}
		super.setBounds(x, y, size, size);
		dimension = size / 10;
		border = size / 50;
		correctingFactor = dimension + border + dimension / 2;
		
		int font = size / 20;
		changeSizes(dimension, border, font);
		resetGraphics();
	}

	/**
	 * Undos the last move and updates the graphics and iterator
	 */
	public void undo() {
		chessBoard.undo();
		movePanel.undo();
		
		if (iterator != null){
			iterator.setNotes(movePanel.getDescriptionText());
			iterator.undo();
			movePanel.setDescriptionText(iterator.getNotes());
			if (iterator.getSpecificFileType().equals(SpecificFile.OPENING_FILE)){
				movePanel.setPossibleMoves(iterator.getPossibleMoves());
			}
		}//end of iterator
		
		resetGraphics();
		canMove.clear();

		// undo ai move too if it exists (iterator should not be used if playing a game)
		if (chessBoard.aiExists() && !chessBoard.isPlayerTurn() && canUndo()) {
			undo();
			resetGraphics();
		}

		setEnabledForButtons();
	}//end of undo method

	/**
	 * Returns true if the current turn is a player turn
	 * @return boolean - Returns if the current turn is a player turn
	 */
	public boolean isPlayerTurn() {
		return chessBoard.isPlayerTurn();
	}

	/**
	 * adds colors to the move as given through canMove. This is what sets the
	 * background colours underneath a piece everytime it is clicked that show
	 * where it can move
	 */
	private void addMoveColors() {
		for (Move move : canMove) {
			if (chessBoard.isEmptyAt(move.getEndPosition())
					|| chessBoard.containsFriendlyPieceAt(move.getEndPosition(), chessBoard.getTurn())) {
				// its an empty square or it is the piece itself
				display[move.endX()][move.endY()].setBackground(TO_MOVE);

				// display for enPassuant (because it moves to an empty
				// square
				if (move.hasEnpassuant()) {
					display[move.endX()][move.endY()].setBackground(TO_TAKE);
				}

			} else {
				// this assumes that there is a piece at the end position
				display[move.endX()][move.endY()].setBackground(TO_TAKE);
			}
		}
	}//end of addMoveColors method

	/**
	 * add a move to the movepanel object for this display
	 * @param move (Move) - the move object to be added
	 */
	private void addMoveToPanel(Move move) {
		if (movePanel != null) {
			movePanel.addMove(move);
		}
	}

	/**
	 * Resets the sizes of the display components. This is called when the
	 * setBounds method is called.
	 * @param dimension -(int) size of buttons and edge white space
	 * @param border - (int) top and left edge space
	 * @param font - (int) font of the words
	 */
	private void changeSizes(int dimension, int border, int font) {
		// display labels and pieces with proper sides
		for (int x = 0; x < ChessBoard.LENGTH; x++) {
			xLabel[x].setBounds((int) (border + dimension * (x + 1.4)), border + dimension / 4, (int) (dimension / 1.5),
					(int) (dimension / 1.5));
			xLabel[x].setFont(new Font("Arial", Font.BOLD, font));

			for (int y = 0; y < ChessBoard.LENGTH; y++) {

				// pieces
				display[x][y].setBounds(border + dimension + dimension * x,
						border + dimension + dimension * (ChessBoard.LENGTH - 1 - y), dimension, dimension);
				display[x][y].setBorder(BorderFactory.createLineBorder(Color.BLACK, (int) (dimension / 30)));

				// vertical labels
				yLabel[y].setFont(new Font("Arial", Font.BOLD, font));
				yLabel[y].setBounds(border + dimension / 4, border + dimension + dimension / 4 + dimension * y,
						dimension / 2, dimension / 2);
			}
		}

	}//end of changeSizes method

	/**
	 * Returns color of board in DEFAULT conditions (but including moves)
	 * @param x - the x coord of the square
	 * @param y - the y coord of the square
	 * @return Color - the color of that square
	 */
	private Color getSquareColor(int x, int y) {
		// check if they have moved recently
		Move prev = chessBoard.previousMove();
		if (chessBoard.undoMove() != null && chessBoard.undoMove().hasPosition(x, y)) {
			return HAS_MOVED;
		} else if (prev != null && prev.hasPosition(x, y)) {
			return HAS_MOVED;
		}

		if (((y * 9) + x) % 2 == 0) {
			return Color.GRAY;
		} else {
			return Color.WHITE;
		}
	}//end of getSquareColor method

	/**
	 * Sets the background color to a square to LIGHT_RED, if a piece can take
	 * another piece according to possibleMoves. This is only called for the
	 * beginner version or when hints are on.
	 * @param possibleMoves - the possible moves to check through
	 */
	private void setHints(ArrayList<Move> possibleMoves) {
		for (Move move : possibleMoves) {
			if (!chessBoard.isEmptyAt(move.getEndPosition())) {
				// taking a piece
				setBackgroundForCanTake(move.endX(), move.endY());
			}

			if (move.isSpecialMove() && move.getPiece().getPieceType() == PAWN) {
				// enPassant, set the Background to that Pawn that you will take
				display[move.endX()][move.endY()].setBackground(CAN_TAKE);
			}
		}
	}//setHints method

	/**
	 * Sets the display of the current square to Light red, or LIGHT_YELLOW_RED
	 * if required
	 * @param x - x position
	 * @param y - y position
	 */
	private void setBackgroundForCanTake(int x, int y) {
		if (display[x][y].getBackground() == HAS_MOVED) {
			display[x][y].setBackground(HAS_MOVED_AND_CAN_TAKE);
		} else {
			display[x][y].setBackground(CAN_TAKE);
		}
	}


	/**
	 * Resets the board to its original backgrounds and refreshes the pieceIcons
	 * on it Also shows the pieces being attacked if help is on. This is called
	 * when a piece is deselected without a move being made.
	 */
	private void resetGraphics() {
		// find where button is
		for (int x = 0; x < ChessBoard.LENGTH; x++) {
			for (int y = 0; y < ChessBoard.LENGTH; y++) {
				display[x][y].setBackground(getSquareColor(x, y));
				
				if (chessBoard.containsPieceAt(x, y, PieceType.EMPTY)){
					display[x][y].setIcon(chessBoard.getPieceAt(x, y).getPieceIcon());
				} else {
					display[x][y].setIcon(new ImageIcon(chessBoard.getPieceAt(x, y).getPieceIcon().getImage().getScaledInstance((int) getWidth()/10, (int)getWidth()/10, Image.SCALE_DEFAULT)));	
				}
			}
		} // end of for loop

		if (help) {
			setHints(chessBoard.getOpponentAvailableMoves());
			setHints(chessBoard.getAvailableMoves());
		}
		
	}//end of resetGraphics method
	
	/**
	 * Gets the current turn of the chessboard
	 * @return Turn - Current turn of the chessboard
	 */
	public Turn getTurn() {
		return chessBoard.getTurn();
	}

	/**
	 * Changes the number of players in the chessgame, from 1 to 2 and from 2 to
	 * 1. The AI turn will be the one whos not playing this round NOTE: AI level
	 * will be ignored if there are no AIs <Br>
	 * @param aiLevel - (int) level of AI
	 */
	public void changePlayers(int aiLevel) {
		chessBoard = new ChessBoard(chessBoard, !chessBoard.aiExists(), aiLevel);
	}
	
	/**
	 * This method is used to ask the players if they want a draw (not AI)
	 * @return boolean - true if the other player accepts the draw and false otherwise
	 */
	private boolean askForDraw() {
		String[] options = { "Accept", "Decline" };
		int n = AlertPane.showOptionDialog("Draw Has Been Requested", "Draw Request", AlertPane.QUESTION_MESSAGE, options, options[1], AlertPane.NO_FULL_SCREEN_BUTTONS);
		return n == 0;
	}

	/**
	 * Shows the Game Over Pane
	 * @param String - message to show
	 */
	private void showGameOverPane(String string) {
		
		if (iterator == null){
			String[] options = {optionsTxt[UNDO], optionsTxt[RESTART], 
					optionsTxt[SAVE_TO_GAME], "Main Menu", 
					"Change Game Settings"};
			
			int n = AlertPane.showOptionDialog(string, "Winner Exists", AlertPane.PLAIN_MESSAGE, options, options[3], 2);
			
			switch (n){
			case 0: optionsActions[UNDO].actionPerformed(null); 
				break;
			case 1: optionsActions[RESTART].actionPerformed(null); 
				break;
			case 2: optionsActions[SAVE_TO_GAME].actionPerformed(null);
				break;
			case 3: gui.showMainMenu(); 
				break;
			case 4: gui.showPlayGame();
			}
		} else {
			String[] options = {"Close", "Back", "Quit"};
			
			int n = AlertPane.showOptionDialog(string, "Winner Exists", AlertPane.PLAIN_MESSAGE, options, options[3], 0);
			
			switch (n){
			case 0: break;
			case 1: gui.changePanelsTo(GUI.GuiMenus.toSpecificTypeMenu(iterator.getSpecificFileType())); 
			case 2: gui.showMainMenu(); 
			break;
			}
		}
		
	}//end of showGameOverPane method
	


	/**
	 * This method shows the options pane
	 */
	private void showOptionsPane(){
		String[] options = {optionsTxt[RESTART], "Quit", "Back"};
		int chosen = AlertPane.showOptionDialog("Options", "Options", AlertPane.QUESTION_MESSAGE, options, options[2], 0);
		switch(chosen){
		case 0: optionsActions[RESTART].actionPerformed(null); break;
		case 1: gui.showQuit();
		}
	}
	
	/**
	 * Sets the iterator notes
	 * @param notes (String) - the new iterator notes
	 */
	protected void setIteratorNotes(String notes){
		if (iterator != null){
			iterator.setNotes(notes);
		}	
	}
	
	/**
	 * Getter method for iterator notes or an empty string if iterator is null
	 * @return String - iterator notes
	 */
	protected String getIteratorNotes(){
		if (iterator != null){
			return iterator.getNotes();
		}
		return "";
	}
	
	/**
	 * Gets possibleMoves of iterator
	 * Calls iterator.getPossibleMoves()
	 * @return ArrayList<Move> - the Possible Moves
	 */
	protected ArrayList<Move> getIteratorPossibleMoves(){
		if (iterator != null){
			return iterator.getPossibleMoves();
		}
		return null;
	}

	/**
	 * Gets the starting turn of the iterator, or just returns white if iterator is null
	 * @return turn - the starting turn of the iterator
	 */
	protected Turn getIteratorTurn() {
		if (iterator != null){
			return iterator.getSpecificFileTurn();
		}
		return Turn.WHITE;
	}

}//end of Display Class
