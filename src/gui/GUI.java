package gui;

import static gui.GUI.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.List;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.TextComponent;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.Window;

import chessgame.ChessBoard;
import chessgame.Display;
import chessgame.MovePanel;
import gui.GUI.GuiMenus;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.FontUIResource;

import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionEvent;
import io.ChessFile;
import io.Difficulty;
import io.FileOrganizer;
import io.RuleFile;
import io.SpecificFile;
import io.FileOrganizer.SortingMethod;
import pieces.Turn;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * This is the Main GUI class which will create the overall visuals for the
 * whole program. This will use enum's to store the various different gui's, and
 * then use those enum's to decide which gui to switch to every time a button is
 * pressed and then create a JPanel to send back to the main JFrame.
 * 
 * @author Robert Desai + Frank Liu
 * @since November 10 2016
 */
public class GUI {	
	/**the base background color for the entire GUI*/
	public static final Color BACKGROUND_COLOR = new Color(255,255,153);
	/**the  base button background color used if needed*/
	public static final Color BUTTON_BACKGROUND_COLOR = Color.ORANGE;
	/**the bounds of the GUI as a rectangle.*/
	public static final Rectangle GUI_SIZE;
	/**a small rectangle in the center of the GUI, used for Alert Pane*/
	public static final Rectangle CENTER_OF_GUI;
	/**a smaller version of the above*/
	public static final Rectangle SMALL_CENTER_OF_GUI;
	/**a default spacing factor used to space objects apart*/
	public static final double SPACING_FACTOR = 1.5;
	/**the amount of options used for the single player options pane*/
	protected static final int SINGLE_PLAYER_OPTIONS = 4;
	/**the amountn of options used for the multi player options pane*/
	protected static final int MULTI_PLAYER_OPTIONS = 2;
	/**the X margin available, varies by screen.*/
	protected static final int Y_MARGIN;
	/**the Y margin size. This is the space used by the titles. varies by screen.*/
	protected static final int X_MARGIN;
	/**the border to be used, varies by screen.*/
	protected static final Border CHOSEN;
	/**the border that will mostly not be used, varies by screen.*/
	protected static final Border NOT_CHOSEN;
	/** The X position where the options buttons will start*/
	protected static final int X_START_OF_OPTIONS_BUTTONS;
	/** The Height of the default options buttons*/
	protected static final int OPTIONS_HEIGHT;
	/**The factor to shift by for the components in the PlayGamePanel when changing from singlePlayer to multiplayer*/
	protected static final int SHIFT_FACTOR;
	/**The bounds of the default title */
	protected static final Rectangle TITLE_BOUNDS;
	/** The font of the default title*/
	protected static final Font TITLE_FONT;
	/** Boundaries of the first mini menu options buttons */
	protected static final Rectangle MINI_MENU_OPTIONS_BOUNDS;
	/** The font used for the mini menu used*/
	protected static final Font MINI_MENU_OPTIONS_FONT;
	/** An int assigned to objects that are not selected*/
	protected static final int NOT_SELECTED = -1;
	/** The instructions to display */
	private static final String INSTRUCTIONS;
	
	/** stores the current JFrame */
	protected JFrame frame;
	/** stores the current Container*/
	protected Container c;
	
	/**
	 * This creates the base information for the fullscreen frame
	 */
	static {
		UIManager.put("OptionPane.background", BACKGROUND_COLOR);
		UIManager.put("Panel.background", BACKGROUND_COLOR);
		UIManager.put("Button.background", BUTTON_BACKGROUND_COLOR);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		GUI_SIZE = new Rectangle(0, 0, screenSize.width, screenSize.height);
			
		// set final variables
		X_MARGIN = GUI_SIZE.width / 6;
		Y_MARGIN = GUI_SIZE.height / 5;
		CHOSEN = BorderFactory.createLineBorder(Color.RED, GUI_SIZE.width / 300);
		NOT_CHOSEN = BorderFactory.createLineBorder(Color.BLACK, GUI_SIZE.width / 500);
		OPTIONS_HEIGHT = (int) ((GUI_SIZE.height - Y_MARGIN * 2) / SINGLE_PLAYER_OPTIONS / SPACING_FACTOR / 1.2);
		SHIFT_FACTOR = (int) (OPTIONS_HEIGHT * (SINGLE_PLAYER_OPTIONS - MULTI_PLAYER_OPTIONS) * SPACING_FACTOR);
		TITLE_BOUNDS = new Rectangle(0, 0, GUI_SIZE.width, Y_MARGIN);
		TITLE_FONT = new Font("Arial", Font.BOLD, GUI_SIZE.width / 20);
		MINI_MENU_OPTIONS_BOUNDS = new Rectangle((int) (X_MARGIN * 0.1), (int) (Y_MARGIN * 0.2), (int) (X_MARGIN / 1.1), (int) (Y_MARGIN / 3));
		X_START_OF_OPTIONS_BUTTONS = (int) (X_MARGIN * 2 * SPACING_FACTOR + MINI_MENU_OPTIONS_BOUNDS.width / 4);
		MINI_MENU_OPTIONS_FONT = new Font("Arial", Font.BOLD, GUI_SIZE.width / 70);
		CENTER_OF_GUI = new Rectangle(GUI_SIZE.width / 4, GUI_SIZE.height / 4, GUI_SIZE.width / 2, GUI_SIZE.height / 2);
		SMALL_CENTER_OF_GUI = new Rectangle(GUI_SIZE.width / 4, GUI_SIZE.height / 4, GUI_SIZE.width / 2, GUI_SIZE.height / 4);
		
		//delete temp files if exist
		FileOrganizer.deleteTempFiles();
	}
	
	/**
	 * Sets up the instructions variable
	 */
	static {
		//note this is not put in the file but instead put into the code in case the user just deletes the instruction file.
		INSTRUCTIONS = "This is an interactive and adaptable program built around the idea of learning chess and "
				+ "testing your skills. The program will always be run on full screen of any computer. The main menu is "
				+ "fairly self-explanatory and allows you to choose which area of the program you want to explore. "
				+ "In the play game area, you are able to select single player or multi player. When playing in single "
				+ "player you can chose the level of AI (1-5), if you want help during your game, or if you are playing as "
				+ "white, black, or random. During the game on the right of your screen, you will find a move panel that"
				+ " will record every move that is played, after you finish you are also given the option of saving the "
				+ "game for a later date; On the left-hand side of your screen there are a series of options available"
				+ " during the game that are all self-explanatory. Note that the declare draw button only will enable "
				+ "itself when it is possible to declare a draw. Through the main menu, you can head to the rules section."
				+ " The rules section provides the user with a list of the rules of chess on the left-hand side of your "
				+ "screen, complete with a scroll bar. You can choose which rule to look at and it will give you a brief "
				+ "description as well as a little animation which visually shows you how the rules works. Another option"
				+ " from the main menu is the strategies area. Here you can look at previous games that you have played "
				+ "and write notes on what you decided to do, or view the notes you have written earlier. "
				+ "Instead of previous games from the strategies area, you can decide to look at the openings area; "
				+ "Here, you are able to look at openings that were made earlier, or make an opening yourself. The "
				+ "openings area is here to help beginners with the hardest part of the game, how to start.";
	}

	/**
	 * Create a new GUI instance, starting at mainMenu
	 */
	public GUI(){
		frame = new JFrame("Chess Learning Program");
		frame.setBounds(GUI_SIZE);
		frame.setResizable(false);
		frame.setUndecorated(true);
		frame.setVisible(true);
		c = frame.getContentPane();
		createNewPanel();
		showMainMenu();
	}
	
	/**
	 * Will dispose old frame and create a new one with layout null and fullscreen and close operation EXIT ON CLOSE
	 * makes display null
	 */
	protected void createNewFrame() {
		frame.dispose();
		frame = new JFrame("Chess Learning Program");
		frame.setBounds(GUI_SIZE);
		frame.setResizable(false);
		frame.setUndecorated(true);
		c = frame.getContentPane();
		c.setLayout(null);
		c.setSize(frame.getSize());
		frame.setVisible(true);
	}
	
	/**
	 * Will dispose old frame and create a new one with layout null and fullscreen and close operation EXIT ON CLOSE
	 * makes display null
	 */
	protected void createNewPanel(){
		c.removeAll();
		c.revalidate();
		c.repaint();
		c.setLayout(null);
		c.setSize(frame.getSize());
	}//end of createNewPanel

	/**
	 * Adds main menu onto container
	 * 
	 * @return JPanel - the main menu
	 */
	public void showMainMenu() {
		createNewPanel();
		addTitleLabel(new JLabel(GuiMenus.MAIN_MENU.toString()));
		
		// boundary variables
		int ySize = GUI_SIZE.height / 13;

		// create buttons
		createMenuButtons(0, GuiMenus.PLAY_GAME, ySize);
		createMenuButtons(1, GuiMenus.RULES, ySize);
		createMenuButtons(2, GuiMenus.STRATEGIES, ySize);
		createMenuButtons(3, GuiMenus.USER_MANUAL, ySize);
		createMenuButtons(4, GuiMenus.QUIT, ySize);
	}// end of MainMenu panel
	
	/**
	 * Is called when the GUI is completely exited 
	 */
	public void showQuit(){		
		System.exit(0);
	}
	
	/**
	 * Makes the play game GUI
	 */
	public void showPlayGame() {
		createNewPanel();
		addTitleLabel(new JLabel(GuiMenus.PLAY_GAME.toString()));
		createBackButtons(GuiMenus.MAIN_MENU);
		
		new PlayGamePanel(this, c);
	}
	
	/**
	 * This shows the default playing chessboard for player vs player or player vs AI.
	 * 
	 * @param singlePlayer boolean -if it is single player
	 * @param turn Turn - if it is not single player, the Turn of the player
	 * @param aiLevel int - if it is not single player, the level of the ai.
	 * @param help boolean - if help is wanted to be shown
	 */
	public void showPlay(boolean singlePlayer, int turn, int aiLevel, int help) {
		createNewPanel();

		JLabel titleLbl;
		if (singlePlayer) {
			titleLbl = addTitleLabel(new JLabel("Play Single Player"));
		} else {
			titleLbl = addTitleLabel(new JLabel("Play Multiplayer"));
		}

		// decode Turn for AI.
		Turn newTurn;
		if (turn == 1) {
			newTurn = Turn.BLACK;
		} else if (turn == 3) {
			newTurn = Turn.WHITE;
		} else {
			newTurn = Math.random() >= 0.5 ? Turn.WHITE : Turn.BLACK;
		}

		// create ChessBoard display and movePanels
		Display display = new Display(this, singlePlayer, aiLevel, newTurn, help == 1);
		addMiniMenuButtons(3, display.getButton(Display.UNDO));
		addMiniMenuButtons(4, display.getButton(Display.RESIGN));
		addMiniMenuButtons(5, display.getButton(Display.REQUEST_DRAW));
		addMiniMenuButtons(6, display.getButton(Display.DECLARE_DRAW));
		addMiniMenuButtons(8, display.getButton(Display.OPTIONS));

		MovePanel movePanel = display.getMovePanel();
		movePanel.setBounds((int) (GUI_SIZE.width - X_MARGIN * 1.5), (int) (Y_MARGIN * 0.8), (int) (X_MARGIN),
				(int) (GUI_SIZE.height - Y_MARGIN * 1.3));
		c.add(movePanel);

		JButton switchPlayers = addMiniMenuButtons(0, display.getButton(Display.SWITCH_PLAYERS));
		Rectangle bounds = switchPlayers.getBounds();
		switchPlayers.setBounds(movePanel.getX(), bounds.y, movePanel.getWidth(), (int) (bounds.height * 1.5));

		int smallerOf = (int) (movePanel.getHeight() + Y_MARGIN / 3 > GUI_SIZE.width / 1.2 ? GUI_SIZE.width / 1.2
				: movePanel.getHeight() + Y_MARGIN / 3);
		display.setBounds((GUI_SIZE.width - smallerOf) / 2, (int) (Y_MARGIN * 0.8), smallerOf, smallerOf);
		c.add(display);

		JButton[] backBtns = createBackButtons(GuiMenus.PLAY_GAME);

		for (int i = 0; i < backBtns.length; i++) {
			backBtns[i].addActionListener(display.getLeaveAction());
		}

		switchPlayers.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (switchPlayers.getText().equals("Switch To Multiplayer")) {
					titleLbl.setText("Play Multiplayer");
				} else {
					titleLbl.setText("Play Single Player");
				}
			}
		});
	}//end of showPlay
	
	/**
	 * Puts the strategies menu onto the container, this can then be used to access openings or tactics. 
	 */
	public void showStrategies(){
		createNewPanel();
		addTitleLabel(new JLabel(GuiMenus.STRATEGIES.toString()));
		createBackButtons(GuiMenus.MAIN_MENU);
		
		// boundary variables
		int ySize = GUI_SIZE.height / 8;

		// create buttons
		createMenuButtons(0, GuiMenus.OPENINGS, ySize);
		createMenuButtons(1, GuiMenus.GAMES, ySize);
	}//end of showStrategies
	
	/**
	 * Shows the Tactic File GUI
	 */
	public void showTactics(){
		showSpecificFile(SpecificFile.TACTIC_FILE);
	}
	
	/**
	 * Shows the saved games from the collection of saved games.
	 */
	public void showSavedGames(){
		showSpecificFile(SpecificFile.SAVED_FILE);
	}
	
	/**
	 * Shows the game file menu from the collection of games.
	 */
	public void showGames(){
		showSpecificFile(SpecificFile.GAME_FILE);
	}
	
	/**
	 * Shows the opening menu from the collection of openings
	 */
	public void showOpenings(){
		showSpecificFile(SpecificFile.OPENING_FILE);
	}
	
	/**
	 * Shows a specific File, can be used for any specific File.
	 * 
	 * @param specificFile SpecificFile - the file that will be shown
	 */
	protected void showSpecificFile(String specificFile) {
		ChessFile[] chessFile = FileOrganizer.getChessFiles(specificFile);
		FileOrganizer.sortChessFiles(chessFile, SortingMethod.A_TO_Z);
		
		ArrayList<Integer> sort = new ArrayList<>();
		sort.add(0);
		
		for (int i = 1; i < ChessFile.PROPERTY_LIST.length; i++){
			sort.add(-1);
		}
		showSpecificFile(specificFile, chessFile, sort);
	}//end of showSpecificFile

	/**
	 * Puts the rule panel onto the container
	 */
	public void showRules(){
		createNewPanel();
		createBackButtons(GuiMenus.MAIN_MENU);
		addTitleLabel(new JLabel("Rules"));
			
		new RulePanel(this, c);
	}
	
	/**
	 * This method returns a boundary (x or y) by spacing them the buttons accordingly: <br>
	 * It starts by adding on the margin, adding on a coordinate depending on the option size and element number (i)<br>
	 * The spacing between the elements is dependent on the spacing factor, which is 1.5 . Thus the space between every component
	 * when using this method is half the space of the optionSize.
	 *  
	 * @param i int - the component number.
	 * @param optionSize int - the size of the options.
	 * @param margin int - the size of the margins wanting to be used.
	 * @return int - the coordinate that should be used
	 */
	protected int getCoord(int i, int optionSize, int margin) {
		return (int) (i * optionSize * SPACING_FACTOR) + margin;
	}

	
	/**
	 * changes the JFrame by disposing old one and creating new one and renders it to correct method for correct panel
	 * @param menu (String) - name of the panel (ie. guiType.toString())
	 */
	public void changePanelsTo(GuiMenus menu) {
		String camelCase = menu.toMethodName();
		Class<?> c;
		try {
			c = Class.forName("gui.GUI");
			Method method = c.getDeclaredMethod(camelCase);
			method.invoke(this);
			
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}//end of changePanelsTo

	/**
	 * Main method, creates a new base GUI object
	 */
	public static void main(String... args) {
		new GUI();
	}

	/**
	 * Places the back button and mainMenu button onto the container with the proper formatting
	 * and function. Formatting will change according to the method, createMiniButtons <Br>
	 * Note: this will use up the slots 0 and 1 in the createMiniButtons method
	 * @return JButton[2] - JButton[0] for the back button and JButton[1] for the main menu button
	 */
	protected JButton[] createBackButtons(GuiMenus gui){
		JButton back = addMiniMenuButtons(0, new JButton("Back"));
		back.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent e){
				changePanelsTo(gui);
			}
		});
		c.add(back);
		
		JButton main = addMiniMenuButtons(1, new JButton("Main Menu"));
		main.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent e){
				changePanelsTo(GuiMenus.MAIN_MENU);
			}
		});
		c.add(main);
		return new JButton[]{back, main};
	}//end of createBackButtons
	
	/**
	 * Adds and formats a title label to display on the top of the GUI. The position depends on titlebounds and 
	 * its font is dependent on titleFont. After setting some of its properties, it returns the titleLbl to be added onto the GUI<br>
	 * Factors Adjusted List: <br>
	 * -text (according to string) <Br>
	 * -bounds <Br>
	 * -alignments to center <Br>
	 * -foreground and background <Br>
	 * NOTE: it is not added onto the panel
	 * 
	 * @param titleLbl (JLabel) - titleLbl to format
	 * @return JLabel - titleLbl with its properties adjusted (same reference as titleLbl)
	 */
	protected JLabel addTitleLabel(JLabel titleLbl){
		titleLbl.setBounds(TITLE_BOUNDS);
		titleLbl.setHorizontalAlignment(SwingConstants.CENTER);
		titleLbl.setFont(TITLE_FONT);
		titleLbl.setVerticalAlignment(SwingConstants.CENTER);
		c.add(titleLbl);
		return titleLbl;
	}//end of addTitleLabel

	/**
	 * Adds and formats several mini Menu buttons on the top left corner (according to miniBtnBounds), 
	 * and creates ones underneath it. The spacing underneath it depends on i. i should start from 0.
	 * The distance between the two mini buttons is dependent on SPACING_FACTOR.<Br><Br>
	 * To Summarize and for further information, it automatically does the following: <br>
	 * -Set Bounds <br>
	 * -Set foreground <br>
	 * -Set font <Br>
	 * -Set background <Br>
	 * -Set Alignment
	 * -Adds to container
	 * 
	 * @param i (int) - rows down from the first one (index 0)
	 * @param btn (JButton) - button to add and format
	 * @return JButton - the button that was formatted (same as the one sent in)
	 */
	protected JButton addMiniMenuButtons(int i, JButton btn){
		btn.setBounds(MINI_MENU_OPTIONS_BOUNDS.x, (int) (MINI_MENU_OPTIONS_BOUNDS.y + MINI_MENU_OPTIONS_BOUNDS.height * SPACING_FACTOR * i), MINI_MENU_OPTIONS_BOUNDS.width, MINI_MENU_OPTIONS_BOUNDS.height);
		btn.setFont(MINI_MENU_OPTIONS_FONT);
		if (btn.getText().length() > 15){
			btn.setFont(new Font(MINI_MENU_OPTIONS_FONT.getFontName(), MINI_MENU_OPTIONS_FONT.getStyle(), MINI_MENU_OPTIONS_FONT.getSize() - (btn.getText().length() / 4)));
		}
		btn.setForeground(Color.RED);
		c.add(btn);
		return btn;
	}//end of addMiniMenuButtons
	
	/**
	 * Adds and formats a menu button at a certain area (like in the main menu type of buttons) and allows the creation of multiple menu buttons 
	 * underneath it. The spacing underneath it depends on i and size. i should start from 0.
	 * The distance between the two menuButtons buttons is dependent on SPACING_FACTOR and the font depends on the ySize.
	 * It automatically adds an actionListener to This.
	 * <Br><Br>
	 * To Summarize and for further information, it automatically does the following: <br>
	 * -Set Bounds <br>
	 * -Set Text <Br>
	 * -Set foreground <br>
	 * -Set font <Br>
	 * -Set background <Br>
	 * -Add ActionListener (this) <Br>
	 * -Adds to container
	 * 
	 * @param i (int) - rows down from the first one (index 0)
	 * @param btn (JButton) - the button to format
	 * @param ySize (int) - the size for each button
	 * @param guiType (guiMenu) - guiMenu to linkto
	 * @return JButton - the button that was added (same as the one given in
	 */
	protected JButton addMenuButtons(int i, JButton btn, int ySize, GuiMenus guiType){
		btn.setBounds(X_MARGIN, getCoord(i + 1, ySize, Y_MARGIN), GUI_SIZE.width - 2 * X_MARGIN, ySize);
		btn.setFont(new Font("Arial", Font.BOLD, ySize / 2));
		btn.setForeground(Color.BLUE);
		btn.addActionListener(createActionListenerTo(guiType));
		c.add(btn);
		return btn;
	}//end of addMenuButtons
	
	/**
	 * Creates a new action listener that can be placed onto a button that will change the current GUI
	 * to another GUI of your choice.
	 * 
	 * @param menu - the menu that you want this action listener to change the GUI to.
	 * @return ActionListener - the desired action listener
	 */
	private ActionListener createActionListenerTo(GuiMenus menu) {
		return new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				changePanelsTo(menu);
			}
			
		};
	}

	/**
	 * Creates a menu Button and reformats it accoding to addMenubuttons
	 * 
	 * @param i (int) - rows down from the first one (index 0)
	 * @param guiType (GuiType) - menu button for that GUI type
	 * @param ySize (int) - the size for each button
	 * @return JButton - the button that was added (same as the one given in
	 */
	protected JButton createMenuButtons(int i, GuiMenus guiType, int ySize){
		return addMenuButtons(i, new JButton(guiType.toString()), ySize, guiType);
	}
	
	/**
	 * Each of these GUI types contain a title for thier respective GUI, which you get with toString
	 * 
	 * @author Robert Desai + Frank Liu
	 */
	public enum GuiMenus {
		/*
		 * The variables used to store the different types of GUI's. Each one is
		 * named after the button which would be pressed in order to move to
		 * that desired GUI.
		 * 
		 */
		MAIN_MENU, PLAY_GAME, RULES, STRATEGIES, TACTICS, OPENINGS, GAMES, SAVED_GAMES, PLAY, QUIT, USER_MANUAL;

		@Override
		public String toString() {
			String newString = this.name().substring(0, 1)
					+ this.name().substring(1, this.name().length()).toLowerCase();
			int nextUnderscore = newString.indexOf("_");
			while (nextUnderscore != -1) {
				// remove underscore and make the next letter uppercase
				newString = newString.substring(0, nextUnderscore) + " "
						+ newString.substring(nextUnderscore + 1, nextUnderscore + 2).toUpperCase()
						+ newString.substring(nextUnderscore + 2);
				nextUnderscore = newString.indexOf("_");
			}
			return newString;
		}//end of toString


		/**
		 * Get the method name for the GuiType that will display the method
		 * @return String - the camel case version of the String sent to it
		 */
		public String toMethodName() {
			String toEdit = toString();
			toEdit = "show" + toEdit.replaceAll(" ", "");
			return toEdit;
		}

		/**
		 * This method takes in a String and then returns the GUI type that is related to this. This method 
		 * could be used to translate the name of a button into the GUI that it changes to.
		 * 
		 * @param s String - the String that you want to change to the desired GUI Type
		 * @return The GUI type that is related to the string
		 */
		public static GuiMenus toGuiType(String s) {
			return valueOf(s.replace(" ", "_").toUpperCase());
		}
		
		/**
		 * returns the Gui menu depending on specificFileType
		 * @param s
		 * @return
		 */
		public static GuiMenus toSpecificTypeMenu(String s){
			switch(s){
			case SpecificFile.GAME_FILE: return GAMES;
			case SpecificFile.OPENING_FILE: return OPENINGS;
			case SpecificFile.TACTIC_FILE: return TACTICS;
			case SpecificFile.SAVED_FILE: return SAVED_GAMES;
			default: return null;
			}
		}
	}// end of GuiType enum

	/**
 	 * This shows the edit menu so that the user can edit a file of their choosing
 	 * 
 	 * @param chessFile ChessFile - the ChessFile to be used
 	 */
	public void showEditFile(ChessFile chessFile) {
		createNewPanel();
		addTitleLabel(new JLabel("Edit File"));
		JButton back = createBackButtons(GuiMenus.toSpecificTypeMenu(chessFile.getSpecificFileType()))[0];
		JButton cancel = addMiniMenuButtons(8, new JButton("Cancel"));
		cancel.addActionListener(back.getActionListeners()[0]);
		
		JButton ok = addMiniMenuButtons(8, new JButton("OK"));
		ok.setBounds(GUI_SIZE.width - cancel.getX() - cancel.getWidth(), cancel.getY(), cancel.getWidth(), cancel.getHeight());
		new EditSpecificFile(this, c, chessFile, ok, false);
	}//end of showEditFile
	
	
	/**
	 * This feature will not be used as of now due to time restraints. This feature was originally intended
	 * to be used but we ran out of time.
	 * 
	 * @param chessFile - the chess files to be used
	 */
	public void showPractiseFile(ChessFile chessFile) {
		
	}//end of showPracticeFile

	/**
	 * This will show an entire file that will be sent into this method. It will create the entire frame and then
	 * the display so that this file can be explored.
	 * 
	 * @param chessFile ChessFile - the Chess File to be used
	 */
	public void showOpenFile(ChessFile chessFile) {
		createNewPanel();
		
		addTitleLabel(new JLabel(chessFile.getName()));
		SpecificFile file = FileOrganizer.openChessFile(chessFile);
		
		Display display = new Display(this, file, false);
		
		JButton[] backBtns = createBackButtons(GuiMenus.toSpecificTypeMenu(chessFile.getSpecificFileType()));
		for (int i = 0; i < backBtns.length; i++){
			backBtns[i].addActionListener(display.getLeaveAction());
		}
		
		MovePanel movePanel = display.getMovePanel();
		movePanel.setBounds((int) (GUI_SIZE.width - X_MARGIN * 1.5), (int) (Y_MARGIN * 0.8), (int) (X_MARGIN), (int) (GUI_SIZE.height - Y_MARGIN * 1.3));
		c.add(movePanel);
	
		int smallerOf = (int) (movePanel.getHeight() + Y_MARGIN / 3 > GUI_SIZE.width / 1.2? GUI_SIZE.width / 1.2: movePanel.getHeight() + Y_MARGIN / 3);
		display.setBounds((GUI_SIZE.width - smallerOf) /2, (int) (Y_MARGIN * 0.8), smallerOf, smallerOf);
		c.add(display);
		
		addMiniMenuButtons(3, display.getButton(Display.UNDO));
		addMiniMenuButtons(4, display.getButton(Display.RESTART));
		addMiniMenuButtons(5, display.getButton(Display.NEXT_MOVE));
		addMiniMenuButtons(6, display.getButton(Display.DELETE_MOVE));
		addMiniMenuButtons(8, display.getButton(Display.OPTIONS));
	}//end of showOpenFile

	/**
	 * The GUI to be shown when the user wants to create a new file. 
	 * 
	 * @param type - the type of the file that is desired to be created.
	 */
	public void showNewFile(String type) {
		//create the new chessFile but delete from folders
		createNewPanel();
		addTitleLabel(new JLabel("Create New File"));
		ChessFile file = FileOrganizer.createChessFile("", ChessFile.DEFAULT_DIFFICULTY, ChessFile.DEFAULT_TURN, type);
		
		ActionListener removeTempFile = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				FileOrganizer.deleteChessFile(file);
			}
		};
		
		JButton[] backBtns = createBackButtons(GuiMenus.toSpecificTypeMenu(type));
		for (int i = 0; i < backBtns.length; i++){
			backBtns[i].addActionListener(removeTempFile);
		}
		
		JButton cancel = addMiniMenuButtons(8, new JButton("Cancel"));
		cancel.addActionListener(backBtns[0].getActionListeners()[0]);
		cancel.addActionListener(removeTempFile);
		
		JButton ok = addMiniMenuButtons(8, new JButton("OK"));
		ok.setBounds(GUI_SIZE.width - cancel.getX() - cancel.getWidth(), cancel.getY(), cancel.getWidth(), cancel.getHeight());
		new EditSpecificFile(this, c, file, ok, true);
	}//end of showNewFile

	/**
	 * Creates a new text Field with the desired settings that will be passed into this method.
	 * 
	 * @param i int - the component number
	 * @param label JLabel - the JLabel that would be added to this textField
	 * @param canEdit boolean - if the user is allowed to edit this
	 * @param display String - what will be displayed on this text Field
	 * @return JTextField - the JTextField Created
	 */
	protected JTextField displayOptionTextField(int i, JLabel label, boolean canEdit, String display) {
		int y = getCoord(i + 1, OPTIONS_HEIGHT, Y_MARGIN);
		label.setFont(new Font("Arial", Font.BOLD, OPTIONS_HEIGHT / 3));
		label.setBounds(X_MARGIN + MINI_MENU_OPTIONS_BOUNDS.width / 7, y, (int) (X_MARGIN * 2), OPTIONS_HEIGHT);
		c.add(label);
		
		int x = label.getX() + label.getWidth() + GUI.GUI_SIZE.width / 50;
		JTextField field = new JTextField(display);
		field.setEditable(canEdit);
		field.setBounds(x, label.getY() + label.getHeight() / 4, GUI.GUI_SIZE.width - GUI.X_MARGIN - x, label.getHeight() / 2);
		c.add(field);
		return field;
	}

	/**
	 * Creates a menu of SpecificFiles using the desired settings that will be passed into this method.
	 * 
	 * @param specificFile String - the SpecificFile type
	 * @param chessFile ChessFile[] - the array of chessFiles to be shown
	 * @param sortingMethod - the sorting method in which these chessFiles should be shown.
	 */
	protected void showSpecificFile(String specificFile, ChessFile[] chessFile, ArrayList<Integer> sortingMethod) {
		createNewPanel();
		addTitleLabel(new JLabel(specificFile));
		createBackButtons(GuiMenus.STRATEGIES);
		new SpecificPanel(this, c, specificFile, chessFile, sortingMethod);
	}
	
	/**
	 * Shows the user manual GUI
	 */
	public void showUserManual(){
		createNewPanel();
		addTitleLabel(new JLabel(GuiMenus.USER_MANUAL.toString()));
		createBackButtons(GuiMenus.MAIN_MENU);
		
		JTextArea info = new JTextArea(INSTRUCTIONS);
		info.setLineWrap(true);
		info.setWrapStyleWord(true);
		info.setEditable(false);
		info.setFont(new Font("Arail", Font.PLAIN, GUI_SIZE.height / 50));
		info.setBackground(BACKGROUND_COLOR);
		info.setBorder(BorderFactory.createEtchedBorder());
		JScrollPane pane = new JScrollPane(info);
		pane.setBounds((int) (X_MARGIN * 1.5), Y_MARGIN, (int) (GUI_SIZE.width - X_MARGIN * 2.5), (int) (GUI_SIZE.height - Y_MARGIN * 1.5));
		c.add(pane);
	}
}//end of the GUI class