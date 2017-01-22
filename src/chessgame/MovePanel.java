package chessgame;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import io.ChessFile;
import io.FileOrganizer;
import io.SpecificFile;
import io.SpecificFile.SpecificFileIterator;
import pieces.Turn;
import static pieces.Turn.*;

/**
 * This is a JComponent for displaying moves, and other features of the game (if
 * other options are selected). Note that this does not do anything on its own, but needs some other class
 * to help it function
 * 
 * @author Frank Liu, Robert Desai
 * @since December 10
 */
public class MovePanel extends JPanel {
	/** Default option for the move panel */
	public static final int DEFAULT = 0;
	
	/** Practice tactics option in the move panel */
	public static final int TACTIC_PRACTISE = 1;
	
	/** The specific file options */
	public static final int SPECIFIC_FILE = 2;
	
	/** The opening practise option */
	public static final int OPENING_PRACTISE = 3;
	
	/** The number of Turn columns on the move panel */
	private static final int TURN_NUMBERS = 2;
	
	/** The size of the border (1 edge ) */
	private static final int BORDER_SIZE = 3;
	
	/** The default text put in to show future moves in the move Text Pane */
	private static final String NEXT_MOVES_TEXT = "\n\n\n Next Moves:\n";
	
	/**The background of the movePanel */
	private static final Color LIGHT_BROWN = new Color(255, 222, 173);
	
	/*
	 * Gui variables
	 */
	private Display display;
	private int option;
	private JScrollPane moveScroll, descriptionScroll, nextMovesScroll;
	private JTextPane[] turnTxtPane;
	private JTextArea description;
	private JPanel pane;
	private JLabel[] turnLbl;
	private JButton showAnswerBtn;
	private JLabel descriptionLabel;
	
	/**
	 * @param display (Display) - display that the move panel is going to use
	 * Creates a move panel and all its components with the given option and display
	 */
	protected MovePanel(Display display) {
		this(DEFAULT, display);
	}

	/**
	 * Creates a move panel and all its components with the given option and display
	 * @param option (int) - The options as given from the static option variables
	 * @param display (Display) - Display that the move panel is going to use
	 */
	protected MovePanel(int option, Display display) {
		this.option = option;
		this.display = display;
		setLayout(null);
		setBackground(LIGHT_BROWN);

		// create all components
		pane = new JPanel();
		pane.setLayout(new GridLayout(1, 2));

		// set moves first
		turnLbl = new JLabel[TURN_NUMBERS];
		turnLbl[0] = new JLabel("White");
		turnLbl[1] = new JLabel("Black");
		turnTxtPane = new JTextPane[TURN_NUMBERS];

		for (int i = 0; i < TURN_NUMBERS; i++) {
			turnLbl[i].setHorizontalAlignment(SwingConstants.CENTER);
			add(turnLbl[i]);

			turnTxtPane[i] = new JTextPane();
			turnTxtPane[i].setEditable(false);
			turnTxtPane[i].setBackground(LIGHT_BROWN);
			pane.add(turnTxtPane[i]);
			
			//center turnTxtPane. Source: http://stackoverflow.com/questions/3213045/centering-text-in-a-jtextarea-or-jtextpane-horizontal-text-alignment
			StyledDocument doc = turnTxtPane[i].getStyledDocument();
			SimpleAttributeSet center = new SimpleAttributeSet();
			StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
			doc.setParagraphAttributes(0, doc.getLength(), center, false);
		}

		moveScroll = new JScrollPane();
		moveScroll.getViewport().add(pane);
		moveScroll.setBorder(BorderFactory.createLoweredBevelBorder());
		add(moveScroll);

		/*showAnswerBtn = new JButton("Answer");
		showAnswerBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				//to not shift the iterator
				display.drawLine(display.getIteratorPossibleMoves());
			}
		});
		add(showAnswerBtn);*/

		//descriptions
		descriptionLabel = new JLabel("Description: ");
		add(descriptionLabel);

		description = new JTextArea();
		description.setWrapStyleWord(true);
		description.setLineWrap(true);
		add(description);

		descriptionScroll = new JScrollPane(description);
		add(descriptionScroll);
	
		setBorder(BorderFactory.createRaisedBevelBorder());
		
	
		//set default values
		turnLbl[display.getTurn().ordinal()].setForeground(Color.RED);

		
		
	}//end of movePanel method

	/**
	 * Note: Will cause errors if chessFile is empty
	 * @param option (int) - The options as given from the static option variables
	 */
	public void changeOption(int option) {
		
		this.option = option;
		setBounds(getBounds());
		if (option == OPENING_PRACTISE) {
			addFutureMoves();
		}
	}

	/**
	 * Gets rid of the last move put into the move panel
	 */
	protected void undo() {
		removeNextMoveText();
		int i = 0;
		if (display.getTurn() == BLACK) {
			i = 1;
		}
		String txt = turnTxtPane[i].getText();
		turnTxtPane[i].setText(txt.substring(0, txt.lastIndexOf("\n")));
		switchTurns();
	}
	
	/**
	 * Switches the highlighted turn for the move panel
	 */
	private void switchTurns() {
		if (turnLbl[0].getForeground().equals(Color.RED)){
			turnLbl[1].setForeground(Color.RED);
			turnLbl[0].setForeground(Color.BLACK);
		} else {
			turnLbl[0].setForeground(Color.RED);
			turnLbl[1].setForeground(Color.BLACK);
		}
	}

	/**
	 * Adds move to the move panel. Make sure the move has been done in the display already
	 * @param move (Move) - Adds a move to the move panel
	 */
	protected void addMove(Move move) {
		removeNextMoveText();
		
		if (move.contains(WHITE)) {
			turnTxtPane[0].setText(turnTxtPane[0].getText() + "\n" + move.toString());
		} else if (move.contains(BLACK)) {
			turnTxtPane[1].setText(turnTxtPane[1].getText() + "\n" + move.toString());
		}

		if (option == OPENING_PRACTISE && !display.isPlayerTurn()) {
			addFutureMoves();
		}
		switchTurns();
	}//end of addMove method

	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		
		// set new height
		int heightOfMoveScrollPane;
		int heightOfLabels = height / 20;
		heightOfMoveScrollPane = height - heightOfLabels - BORDER_SIZE * 2;
		int remainingHeight = 0;
		if (option != DEFAULT){
			heightOfMoveScrollPane -= height / 8 * option ;
			remainingHeight = height - heightOfMoveScrollPane;
		}
		
		moveScroll.setBounds(BORDER_SIZE, heightOfLabels, width - BORDER_SIZE * 2, heightOfMoveScrollPane);
		
		pane.setBounds(moveScroll.getBounds());
		
		int halfWidth = (width - BORDER_SIZE * 2) / 2;
		for (int i = 0; i < TURN_NUMBERS; i++) {
			turnLbl[i].setBounds(i * halfWidth, 0, halfWidth, heightOfLabels);
			turnLbl[i].setFont(new Font("Arial", Font.BOLD, (int) (heightOfLabels * 0.7)));
			turnTxtPane[i].setFont(new Font("Arial", Font.BOLD, (int) (heightOfLabels * 0.5)));
		}

		if (option == TACTIC_PRACTISE) {
			//not available now
		} else if (option == OPENING_PRACTISE) {
			//not available now
		} else if (option == SPECIFIC_FILE) {
			descriptionLabel.setBounds(moveScroll.getX() * 4, moveScroll.getY() + moveScroll.getHeight() + remainingHeight / 30, moveScroll.getWidth() - moveScroll.getX() * 8, remainingHeight / 5);
			descriptionLabel.setFont(new Font("Arial", Font.BOLD, remainingHeight /10));
			descriptionLabel.setBackground(Color.black);
			description.setFont(new Font("Arial", Font.PLAIN, remainingHeight /12));
			descriptionScroll.setBounds(descriptionLabel.getX(), descriptionLabel.getY() + descriptionLabel.getHeight() + remainingHeight / 100, descriptionLabel.getWidth(), height - (descriptionLabel.getY() + descriptionLabel.getHeight() + remainingHeight / 10));
		}
	}//end of setBounds method

	@Override
	public void setBounds(Rectangle r) {
		this.setBounds(r.x, r.y, r.width, r.height);
	}
	
	/**
	 * Adds future moves to the move scroll pane
	 */
	private void addFutureMoves() {
		ArrayList<Move> moves = display.getIteratorPossibleMoves();
		//not available now

	}

	/**
	 * Resets the text in movePanel
	 */
	public void reset() {
		turnTxtPane[0].setText("");
		turnTxtPane[1].setText("");
		turnLbl[0].setForeground(Color.black);
		turnLbl[1].setForeground(Color.black);
		
		if (display.getIteratorTurn() != null){
			turnLbl[display.getIteratorTurn().ordinal()].setForeground(Color.RED);
		} else {
			turnLbl[Turn.WHITE.ordinal()].setForeground(Color.RED);
		}
		
	}
	
	/**
	 * Gets the text from the description area
	 * @return String - the text from description area
	 */
	protected String getDescriptionText(){
		return description.getText();
	}
	
	/**
	 * Sets the text for the description area
	 * @param string - the text to set
	 */
	protected void setDescriptionText(String string){
		description.setText(string);
	}
	
	/**
	 * Sets the possible Moves for the Possible Moves Area
	 * @param moves - the possible moves
	 */
	protected void setPossibleMoves(ArrayList<Move> moves){
		int i = display.getTurn().ordinal();
		turnTxtPane[i].setText(turnTxtPane[i].getText() + NEXT_MOVES_TEXT);
		
		if (moves.isEmpty()){
			turnTxtPane[i].setText(turnTxtPane[i].getText() + "NONE");
		}
	
		for (Move m: moves){
			turnTxtPane[i].setText(turnTxtPane[i].getText() + m + "\n");
		}
	}//end of setPossibleMoves
	
	/**
	 * Removes the junk NEXT_MOVES_TEXT from the move panel
	 */
	private void removeNextMoveText(){
		for (int i = 0; i < turnTxtPane.length; i++){
			String txt = turnTxtPane[i].getText();
			if (txt.indexOf(NEXT_MOVES_TEXT) != -1){
				turnTxtPane[i].setText(txt.substring(0, txt.indexOf(NEXT_MOVES_TEXT)));
			}
		}
	}
}//end of MovePanel class
