package gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

import chessgame.Display;
import chessgame.MovePanel;
import gui.GUI.GuiMenus;
import pieces.Turn;
import static gui.GUI.*;
/**
 * This is a helper class used to create only the play game GUI the reason that this exists is to avoid clutter in the
 * main class, specifically for variables.
 * 
 * @author Robert and Frank
 */
class PlayGamePanel {
	/* Component numbers for the option buttons
	 */
	private static final int TURN = 0;
	private static final int AI = 1;
	private static final int HELP = 2;
	
	/* stores last selected singlePlayer/multiplayer option (so it can revert when you go back to the panel)*/
	private static boolean singlePlayer = true;
	private static int turn = 1;
	private static int aiLevel = 3;
	private static int help = 1;
	
	private GUI gui;
	private Container c;
	/**
	 * 
	 * @param gui GUI- The GUI that this will be added to.
	 * @param c Container - the container that this is supposed to add its components onto
	 */
	protected PlayGamePanel(GUI gui, Container c) {
		this.gui = gui;
		this.c = c;
		// creating components
		JLabel turnLbl = new JLabel("Turn");
		JButton[] turnBtn = displayOptionButtons(TURN, 3, turnLbl, turn);

		JLabel aiLbl = new JLabel("AI");
		JButton[] aiBtn = displayOptionButtons(AI, 5, aiLbl, aiLevel);

		JLabel helpLbl = new JLabel("Help");
		JButton[] helpBtn = displayOptionButtons(HELP, 2, helpLbl, help);

		/* add players Radio buttons */
		JRadioButton[] players = new JRadioButton[2];
		ButtonGroup playerBG = new ButtonGroup();
		players[0] = new JRadioButton("Single Player");
		players[1] = new JRadioButton("Multiplayer");

		if (singlePlayer) {
			players[0].setSelected(true);
		} else {
			players[1].setSelected(true);
			setVisibleOptionButtons(aiBtn, aiLbl);
			setVisibleOptionButtons(turnBtn, turnLbl);
			shift(helpBtn, helpLbl);
		}

		// action Listener to change the bounds of all the items
		ActionListener rBtnAction = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (((JRadioButton) e.getSource()).getText().equals("Single Player")) {
					singlePlayer = true;
				} else {
					singlePlayer = false;
				}
				setVisibleOptionButtons(aiBtn, aiLbl);
				setVisibleOptionButtons(turnBtn, turnLbl);
				shift(helpBtn, helpLbl);

			}
		};

		for (int i = 0; i < players.length; i++) {
			players[i].setBounds(X_MARGIN + MINI_MENU_OPTIONS_BOUNDS.width / 7 + i * GUI_SIZE.width / 2, Y_MARGIN,
					GUI_SIZE.width / 2, OPTIONS_HEIGHT);
			players[i].setFont(new Font("Arial", Font.BOLD, GUI_SIZE.height / 20));
			players[i].addActionListener(rBtnAction);
			players[i].setForeground(Color.black);
			players[i].setBackground(BACKGROUND_COLOR);
			playerBG.add(players[i]);
			c.add(players[i]);
		}

		JButton play = new JButton(GuiMenus.PLAY.toString());
		play.setBounds((int) (GUI_SIZE.width - X_MARGIN), (int) (GUI_SIZE.height - Y_MARGIN * 0.6),
				(int) (X_MARGIN / 1.5), (int) (Y_MARGIN / 3));
		play.setFont(MINI_MENU_OPTIONS_FONT);
		play.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				gui.showPlay(singlePlayer, turn, aiLevel, help);
			}

		});
		play.setForeground(Color.RED);
		play.setBackground(new Color(240, 248, 255));
		c.add(play);
	}//end of constructor


	
	/**
	 * Creates an Array of JButtons btnGroupLength in size, sets its properties and actionsListener, adds it to 
	 * the container and then returns its reference. The Array Button property and y coordinate is dependent on the coordinate number,
	 * which represents the row (of options) it this method is putting in. The icons on the JButton are gotten from:
	 * "GUI/" + label.getText() + i + ".png", where i is the button index. Finally, it gives the JLabel a nice styling.
	 * <br>
	 * To change the look of the borders the actionListener will do, change the chosen and notChosen final Border variables. <Br>
	 * The font size is dependent on the height and width of the buttons and xPosition (of buttons) is dependent on xStartOfBtnOption
	 * 
	 * @param componentNum (int) - one of the static final ints
	 * @param btnGroupLength (int) - the length of the button options
	 * @param label (JLabel) - the label for these options
	 * @param selected (int) - the element[selected - 1] which is selected
	 * @return JButton[] - a reference to the JButton options that was created
	 */
	protected JButton[] displayOptionButtons(int componentNum, int btnGroupLength, JLabel label, int selected) {
		JButton[] btnGroup = new JButton[btnGroupLength];
		
		//this is to ensure that the option buttons are square
		int availableSpacePerBtn = (GUI_SIZE.width - X_MARGIN * 2) / btnGroup.length;
		int size = OPTIONS_HEIGHT > availableSpacePerBtn ? availableSpacePerBtn : OPTIONS_HEIGHT;
		int y = gui.getCoord(componentNum + 1, OPTIONS_HEIGHT, Y_MARGIN);
		label.setFont(new Font("Arial", Font.BOLD, size / 3));
		label.setBounds(X_MARGIN + MINI_MENU_OPTIONS_BOUNDS.width / 7, y, (int) (X_MARGIN * 2), size);
		c.add(label);
		
		//find actionListener according to componentNum
		ActionListener btnGroupAction;
		switch(componentNum){
		case TURN: 
			btnGroupAction = new ActionListener() {
				@Override public void actionPerformed(ActionEvent e) {
					turn = resetButtons(btnGroup, e) + 1;
				}
			};
			break;
		case AI:
			btnGroupAction = new ActionListener() {
				@Override public void actionPerformed(ActionEvent e) {
					aiLevel = resetButtons(btnGroup, e) + 1;
				}// end of Action Performed
			};
			break;
		case HELP:
			btnGroupAction = new ActionListener() {
				@Override public void actionPerformed(ActionEvent e) {
					help = resetButtons(btnGroup, e) + 1;
				}// end of Action Performed
			};
			break;
		default: 
			btnGroupAction = new ActionListener(){
				@Override public void actionPerformed(ActionEvent e) {
					
				}
			};
		}
		
		for (int i = 0; i < btnGroup.length; i++) {
			ImageIcon imageIcon = new ImageIcon(new ImageIcon("GUI/" + label.getText() + i + ".png").getImage().getScaledInstance(size, size, Image.SCALE_DEFAULT));
			btnGroup[i] = new JButton(imageIcon);
			btnGroup[i].addActionListener(btnGroupAction);
			btnGroup[i].setBounds(gui.getCoord(i, size, X_START_OF_OPTIONS_BUTTONS), y, size, size);
			btnGroup[i].setBorder(NOT_CHOSEN);
			c.add(btnGroup[i]);
		}
		
		if (selected != NOT_SELECTED){
			btnGroup[selected - 1].setBorder(CHOSEN);
		}	
		return btnGroup;
	}//end of displayOptionButtons

	/**
	 * This sets a group of JButtons to visible or invisible, depending if  the single player options is selected
	 * 
	 * @param btnGroup JButton[] - the button group that is being referred to
	 * @param label JLabel - the JLabel being referenced that should also be set to visible
	 */
	protected void setVisibleOptionButtons(JButton[] btnGroup, JLabel label) {
		for (int i = 0; i < btnGroup.length; i++) {
			btnGroup[i].setVisible(singlePlayer);
		}
		label.setVisible(singlePlayer);
	}
	
	/**
	 * shifts a group of JComponents including a label
	 * 
	 * @param group JComponent - the group of Components to be shifted.
	 * @param label JComponent - the label to be shifted
	 */
	protected void shift(JComponent[] group, JComponent label) {
		int shiftFactor = singlePlayer ? SHIFT_FACTOR : -SHIFT_FACTOR;
		int y = label.getY() + shiftFactor;
		for (int i = 0; i < group.length; i++) {
			Rectangle r = group[i].getBounds();
			group[i].setBounds(r.x, y, r.width, r.height);
		}
		Rectangle r = label.getBounds();
		label.setBounds(r.x, y, r.width, r.height);
	}
	
	/**
	 * Set Turn Chooser Part The action Listener updates the help variable and
	 * sets borders according to which button is pressed
	 * 
	 * @param btnGroup JButton[] - the group of JButtons to be reset
	 * @param e - the button that was recently clicked
	 * @return int - index of the button clicked
	 */
	protected int resetButtons(JButton[] btnGroup, ActionEvent e) {
		int toReturn = 0;
		for (int i = 0; i < btnGroup.length; i++) {
			btnGroup[i].setBorder(NOT_CHOSEN);
			if (e.getSource().equals(btnGroup[i])) {
				btnGroup[i].setBorder(CHOSEN);
				toReturn = i;
			}
		} // end of Action Performed
		return toReturn;
	}//end of resetButtons method
}//end of PlayGamePanel Class
