package gui;

import static gui.GUI.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import gui.GUI.GuiMenus;
import io.ChessFile;
import io.Difficulty;
import io.FileOrganizer;
import io.FileOrganizer.SortingMethod;
import pieces.Turn;

/**
 * Shows a SpecificPanel for any specificFile
 * @author Robert and Frank
 * @since December 30
 */
class SpecificPanel implements ActionListener{
	/**The list of buttons, where each displays the information of a specificFileButton*/
	private ArrayList<SpecificButton> buttons = new ArrayList<>();
	
	/**The Properties to show on the SpecificPanel*/
	private ArrayList<String> properties = new ArrayList<>();
	
	/**The sorting method way of each property, according to index in ChessFile.getSortingMethod(), -1 means not sorted with this attribute, 0 and 1 means sorted with attribute but opposite direction of each other */
	private ArrayList<Integer> sortingMethod = new ArrayList<>();
	
	private int selected;
	private JScrollPane scroll;
	private JButton[] optionsBtns;
	private static final int DELETE = 0, EDIT = 1, PRACTISE = 2, OPEN = 3, TOTAL_OPTIONS = 4;
	private final ActionListener[] optionsAction;
	private ActionListener createAction;
	private GUI gui;
	private static final String[] optionsTxt = {"Delete", "Edit", "Practise", "Open"};
	private Container c;
	private static final GridBagConstraints constraint;
	
	
	/**
	 * sets default grid constraints
	 */
	static {
		//GridBagConstraint for button
	    constraint = new GridBagConstraints();
	    constraint.anchor = GridBagConstraints.PAGE_START;
	    constraint.fill = GridBagConstraints.HORIZONTAL;
	    constraint.gridx = 0;
	    constraint.gridy = GridBagConstraints.RELATIVE;
	    constraint.weightx = 1.0;
	    constraint.weighty = 1.0;
	}
	
	/**
	 * Options Action Listener and sets default grid constraints
	 */
	{
		
		optionsAction = new ActionListener[TOTAL_OPTIONS];
		
		optionsAction[DELETE] = new ActionListener(){
			@Override public void actionPerformed(ActionEvent e){
				SpecificButton btn = buttons.remove(selected);
				ChessFile chessFile = btn.chessFile;
				FileOrganizer.deleteChessFile(chessFile);
				btn.setVisible(false);
				setOptionsButtonsEnabled(false);
			}
		};
		
		optionsAction[EDIT] = new ActionListener(){
			@Override public void actionPerformed(ActionEvent e){
				gui.showEditFile(buttons.get(selected).chessFile);
				setOptionsButtonsEnabled(false);
			}
		};
		
		optionsAction[PRACTISE] = new ActionListener(){
			@Override public void actionPerformed(ActionEvent e){
				gui.showPractiseFile(buttons.get(selected).chessFile);
				setOptionsButtonsEnabled(false);
			}
		};
		
		optionsAction[OPEN] = new ActionListener(){
			@Override public void actionPerformed(ActionEvent e){
				gui.showOpenFile(buttons.get(selected).chessFile);
				setOptionsButtonsEnabled(false);
			}
		};
	}
	
	/**
	 * Creates a SpecificFile Panel with the given specificFileType
	 * @param GUI gui - the container to add to
	 * @param specificFile (String) - specificFileType to create SpecificPanel for
	 */
	public SpecificPanel(GUI gui, Container c, String specificFile, ChessFile[] chessFile, ArrayList<Integer> sortMethod) {
		this.gui = gui;
		this.c = c;
		this.sortingMethod = sortMethod;
		
		//add new file action
		createAction = new ActionListener(){
			@Override public void actionPerformed(ActionEvent e){
				gui.showNewFile(specificFile);
				setOptionsButtonsEnabled(false);
			}
		};
		
		//add properties to show
		for (String s: ChessFile.PROPERTY_LIST){
			if (ChessFile.canShow(s, specificFile)){
				properties.add(s);
			}
		}
		
		scroll = new JScrollPane();
		scroll.setBounds(X_MARGIN, (int) (Y_MARGIN * 1.5),  GUI_SIZE.width - X_MARGIN * 2, GUI_SIZE.height - (int) (Y_MARGIN * 2.3));
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		panel.setSize(scroll.getSize());
		panel.setBackground(new Color(255, 240, 153));
		
		JPanel menuPane = new JPanel();
		menuPane.setBounds(scroll.getX(), scroll.getY() - Y_MARGIN / 3, scroll.getWidth(),Y_MARGIN / 3);
	    menuPane.setLayout(new GridLayout(1, 0));
	    c.add(menuPane);
	    
	    for (int i = 0; i < properties.size(); i++){
	    	JButton button = new JButton(properties.get(i));
	    	button.setFont(new Font("Arial", Font.BOLD, Y_MARGIN / 6));
	    	button.setForeground(Color.RED);
	    	
	    	final int i1 = i;
	    	button.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					int j = sortingMethod.get(i1);
					
					//set J to 1 if 0 and set to 0 if -1
					j = j ==0? 1: 0;
					j = j == -1? 0: j;
					
					for (int i = 0; i < sortingMethod.size(); i++){
						sortingMethod.set(i, -1);
					}
					
					sortingMethod.set(i1, j);
					FileOrganizer.sortChessFiles(chessFile, ChessFile.getSortingMethod(properties.get(i1))[j]);
					
					gui.showSpecificFile(specificFile, chessFile, sortingMethod);
				}
	    	});
	    	
			menuPane.add(button);
	    }//end of for loop	    
		
		for (int i = 0; i < chessFile.length; i++){
			SpecificButton b = new SpecificButton(chessFile[i], properties, i);
			b.addActionListener(this);
			buttons.add(b);
			sortingMethod.add(1);
			panel.add(b, constraint);
		}
		
		panel.updateUI();
		
		scroll.setViewportView(panel);
		c.add(scroll);
		
		int btnWidth = scroll.getWidth() / (TOTAL_OPTIONS + 1);
		int btnSpacing = btnWidth / (TOTAL_OPTIONS - 1);
		optionsBtns = new JButton[TOTAL_OPTIONS];
		optionsBtns[0] = new JButton(optionsTxt[0]);
		optionsBtns[0].setBounds(scroll.getX(), (int) (GUI_SIZE.height - Y_MARGIN * 0.5), btnWidth, GUI.MINI_MENU_OPTIONS_BOUNDS.height);
		for (int i = 0; i < optionsBtns.length; i++){
			if (i > 0){
				optionsBtns[i] = new JButton(optionsTxt[i]);
				Rectangle b = optionsBtns[i - 1].getBounds();
				optionsBtns[i].setBounds(b.x + btnWidth + btnSpacing, b.y, btnWidth, b.height);
			}

			optionsBtns[i].setFont(new Font("Arial", Font.BOLD, GUI_SIZE.height / 40));				
			optionsBtns[i].addActionListener(optionsAction[i]);
			
			c.add(optionsBtns[i]);
		}
		
		setOptionsButtonsEnabled(false);
		optionsBtns[PRACTISE].setVisible(false);
		//optionsBtns[PRACTISE].setVisible(ChessFile.canBePractised(specificFile)); 
		optionsBtns[EDIT].setVisible(ChessFile.canBeEdited(specificFile));
		
		JButton create = new JButton("New");
		create.setFont(new Font("Arial", Font.BOLD, GUI_SIZE.height / 40));
		create.addActionListener(createAction);
		create.setBounds(GUI.GUI_SIZE.width - GUI.MINI_MENU_OPTIONS_BOUNDS.x - GUI.MINI_MENU_OPTIONS_BOUNDS.width, GUI.MINI_MENU_OPTIONS_BOUNDS.y, GUI.MINI_MENU_OPTIONS_BOUNDS.width, GUI.MINI_MENU_OPTIONS_BOUNDS.height);
		c.add(create);
	}//end of constructor


	private void setOptionsButtonsEnabled(boolean b) {
		for (JButton btn: optionsBtns){
			btn.setEnabled(b);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		boolean enableBtn = false;
		JButton source = (JButton) e.getSource();
		for (int i = 0; i < buttons.size(); i++){
			JButton b = buttons.get(i);
			
			if (source.equals(b) && source.getBorder().equals(NOT_CHOSEN)){
				selected = i;
				b.setBorder(CHOSEN);
				enableBtn = true;
			}  else {
				
				b.setBorder(NOT_CHOSEN);
			}			
		}
		
		setOptionsButtonsEnabled(enableBtn);
	}//end of actionPerformed method


	/**
	 * A specificFile button class showing the properties of a chessFile
	 * @author frankliu197
	 */
	class SpecificButton extends JButton {
		private ChessFile chessFile;
		private ArrayList<JLabel> label;
		private JCheckBox checkBox;
		
		/**
		 * Creates a SpecificFileButton with a given chessFile showing the given properties
		 * @param chessFile - chessfile to get information from
		 * @param properties - properties to show
		 */
		protected SpecificButton(ChessFile chessFile, ArrayList<String> properties, int iterationNum) {
			this.chessFile = chessFile;
			setLayout(new GridLayout(1, 0));
			setBorder(NOT_CHOSEN);
			setPreferredSize(new Dimension(scroll.getWidth() - 20, GUI_SIZE.height/ 15));
			
			label = new ArrayList<JLabel>();
			checkBox = new JCheckBox();
			
			checkBox.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					chessFile.setMastered(checkBox.isSelected());
				}
			});
			
			for (int i = 0; i < properties.size(); i++){
				if (properties.get(i).equals(ChessFile.MASTERED)){
					checkBox.setSelected(chessFile.isMastered());
					checkBox.setHorizontalAlignment(SwingConstants.CENTER);
					checkBox.setVerticalAlignment(SwingConstants.CENTER);
					checkBox.setBackground(getBackground());
					//checkBox.setBounds(lblWidth * i + lblWidth / 5 * 2, getHeight() / 3, lblWidth / 5, getHeight() / 3);
					add(checkBox);
				} else {
					JLabel lbl = new JLabel(chessFile.getProperty(properties.get(i)));
					lbl.setFont(new Font("Arial", Font.BOLD, GUI.GUI_SIZE.height / 30));
					//lbl.setBounds(lblWidth * i, 0, lblWidth, getHeight());
					lbl.setHorizontalAlignment(SwingConstants.CENTER);
					lbl.setVerticalAlignment(SwingConstants.CENTER);
					add(lbl);
					label.add(lbl);
				}
			}
		}//end of SpecificFileButton constructor
	}//end of specificButton class
}//end of SpecificPanel class

