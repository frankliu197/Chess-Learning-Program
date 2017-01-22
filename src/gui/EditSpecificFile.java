

package gui;

import static gui.GUI.*;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import gui.GUI.GuiMenus;
import io.ChessFile;
import io.Difficulty;
import io.FileOrganizer;
import io.SpecificFile;
import pieces.Turn;
/**
 * This is a helper class used to help make JPanels of a specific Type. This class specifically will be used for the edit
 * specificFiles menu.
 * 
 * @author Robert and Frank
 *
 */
class EditSpecificFile {
	private JTextField name;
	private JRadioButton[] difficulty;
	private JRadioButton[] turn;
	private JCheckBox checkBox;
	private String selectedDifficulty;
	private String selectedTurn;
	private GUI gui;
	private Container c;
	private ChessFile chessFile;
	
	/**
	 * Creates a SpecificFile Panel with the given SpecificFile Type.
	 * 
	 * @param gui GUI - the Gui that this will be added to.
	 * @param c Container - the container that the components should be added to.
	 * @param chessFile Chess File - the chessFile that will be edited in this GUI
	 * @param ok JButton - the JButton that will be used to finalize all edits and return to the main GUI.
	 * @param newFile boolean - if this is a completely new File.
	 */
	protected EditSpecificFile(GUI gui, Container c, ChessFile chessFile, JButton ok, boolean newFile) {
		String type = chessFile.getSpecificFileType();
		this.chessFile = chessFile;
		this.gui = gui;
		this.c = c;		
		
		JLabel[] label = new JLabel[ChessFile.PROPERTY_LIST.length];
		label[0] = new JLabel(ChessFile.NAME);
		name = gui.displayOptionTextField(0, label[0], true, chessFile.getName());
		
		int i = 1;
		
		//show different properties
		if (ChessFile.canShow(ChessFile.DIFFICULTY, type)){
			label[i] = new JLabel(ChessFile.DIFFICULTY);
			Difficulty[] values = Difficulty.values();
			difficulty = displayJRadioButtons(i, 4, difficulty, chessFile.getDifficulty(), label[i], ChessFile.canEdit(ChessFile.DIFFICULTY, type), values);
			i++;
		}
		
		//add type
		if (ChessFile.canShow(ChessFile.TURN, type)){
			label[i] = new JLabel(ChessFile.TURN);
			Turn[] values = Turn.values();
			
			if (newFile && type != SpecificFile.GAME_FILE && type != SpecificFile.SAVED_FILE){
				turn = displayJRadioButtons(i, 2, turn, chessFile.getTurn(), label[i], true, values);
			} else {
				turn = displayJRadioButtons(i, 2, turn, chessFile.getTurn(), label[i], ChessFile.canEdit(ChessFile.TURN, type), values);
			}
			i++;
		}//
		
		//add mastered
		if (ChessFile.canShow(ChessFile.MASTERED, type)){
			label[i] = new JLabel(ChessFile.MASTERED);
			int y = gui.getCoord(i + 1, OPTIONS_HEIGHT, Y_MARGIN);
			label[i].setFont(new Font("Arial", Font.BOLD, OPTIONS_HEIGHT / 3));
			label[i].setBounds(X_MARGIN + MINI_MENU_OPTIONS_BOUNDS.width / 7, y, (int) (X_MARGIN * 2), OPTIONS_HEIGHT);
			c.add(label[i]);
			
			checkBox = new JCheckBox();
			checkBox.setHorizontalAlignment(SwingConstants.CENTER);
			checkBox.setSelected(chessFile.isMastered());
			checkBox.setBackground(BACKGROUND_COLOR);
			checkBox.setBounds(name.getX(), label[i].getY(), name.getHeight(), name.getHeight());
			c.add(checkBox);
		}
		
		
		ok.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String nameOf = name.getText().trim();

				if (nameOf.isEmpty()){
					AlertPane.showMessageDialog("Please choose a valid name for your file.");
					return;
				}
				
				Difficulty d = Difficulty.valueOf(getSelected(difficulty));
				
				Turn t;
				if (turn != null){
					t = Turn.valueOf(getSelected(turn));
				} else {
					t = ChessFile.DEFAULT_TURN;
				}
				
				boolean m;
				if (checkBox != null){
					m = checkBox.isSelected();
				} else {
					m = false;
				}
				
				if (!EditSpecificFile.this.chessFile.getName().equals(nameOf)){
					if (FileOrganizer.chessFileExists(nameOf, chessFile.getSpecificFileType())){
						if (AlertPane.showConfirmDialog("Name has been taken. Overwrite?") != AlertPane.YES_OPTION){
							return;
						}
					}
				}
				
				
				chessFile.updateInformation(nameOf, d, t, m);
				gui.changePanelsTo(GuiMenus.toSpecificTypeMenu(chessFile.getSpecificFileType()));
			}

			/**
			 * Returns the JButton that is selected
			 * @param btnGroup
			 * @return
			 */
			private int getSelected(JRadioButton[] btnGroup) {
				for(int i = 0; i < btnGroup.length; i++){
					if (btnGroup[i].isSelected()){
						return i;
					}
				}
				return -1;
			}
		});		
	}//end of constructor

	/**
	 * The values >  numOfValues - 1 will be ignored
	 * @param difficulty Difficulty - the difficulty of the chessfile
	 * @param chessFile ChessFile- the chessFile to be edited
	 * @param i int - the position of the Radio Button
	 * @param JLabel - JLabel to format
	 * @throws OutOfBoundsException if numOfValues > allValues
	 */
	private JRadioButton[] displayJRadioButtons(int i, int numOfValues, JRadioButton[] btnGroup, Enum value, JLabel label, boolean editable, Enum[] allValues) {
		int y = gui.getCoord(i + 1, OPTIONS_HEIGHT, Y_MARGIN);
		label.setFont(new Font("Arial", Font.BOLD, OPTIONS_HEIGHT / 3));
		label.setBounds(X_MARGIN + MINI_MENU_OPTIONS_BOUNDS.width / 7, y, (int) (X_MARGIN * 2), OPTIONS_HEIGHT);
		c.add(label);
		
		//JLabels for radio buttons
		JLabel[] lbls = new JLabel[numOfValues];
		btnGroup = new JRadioButton[numOfValues];
		ButtonGroup bg = new ButtonGroup();
		int availableSpacePerBtn = (GUI_SIZE.width - X_MARGIN * 2) / btnGroup.length;
		int size = OPTIONS_HEIGHT > availableSpacePerBtn ? availableSpacePerBtn : OPTIONS_HEIGHT;
		
		for (int j = 0; j < numOfValues; j++){
			lbls[j] = new JLabel(allValues[j].toString());
			lbls[j].setHorizontalAlignment(SwingConstants.CENTER);
			lbls[j].setFont(new Font("Arial", Font.BOLD, (int) (OPTIONS_HEIGHT / 4.8)));
			lbls[j].setForeground(Color.blue);
			lbls[j].setBounds(gui.getCoord(j, size, X_START_OF_OPTIONS_BUTTONS), y, (int) (size * 1.4), size / 2);
			c.add(lbls[j]);
			
			btnGroup[j] = new JRadioButton();
			btnGroup[j].setEnabled(editable);
			btnGroup[j].setHorizontalAlignment(SwingConstants.CENTER);
			btnGroup[j].setBounds(gui.getCoord(j, size, X_START_OF_OPTIONS_BUTTONS), y + size / 2, (int) (size * 1.3), size / 2);
			btnGroup[j].setBackground(GUI.BACKGROUND_COLOR);
			bg.add(btnGroup[j]);
			
			c.add(btnGroup[j]);
		}
		
		btnGroup[value.ordinal()].setSelected(true);
		return btnGroup;
	}//end of display JRadioButtons method
}//end of EditSpecificFile class
