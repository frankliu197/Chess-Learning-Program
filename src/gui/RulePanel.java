package gui;

import static gui.GUI.*;

import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import chessgame.Display;
import io.ChessFile;
import io.FileOrganizer;
import io.RuleFile;
/**
 * This is a helper class used to create the rule panel that will be added onto the main GUI
 * 
 * @author Robert
 *
 */
class RulePanel {
	private RuleButton[] ruleBtn;
	private JLabel describe;
	private Display display;
	private JTextArea description;
	private GUI gui;
	private Container c;
	private JLabel title;
	
	/**
	 * The constructor for Rule Panel. This creates a new default rule Panel that will then be inplememnted into
	 * the main GUI for use. A new rule Panel will be constructed every time it is re-entered.
	 * 
	 * @param gui GUI - the gui that the panel will be added to
	 * @param c Container - the container that the panel should be added to.
	 */
	protected RulePanel(GUI gui, Container c){
		this.gui = gui;
		this.c = c;

		RuleFile[] rules = FileOrganizer.getRules();
		ruleBtn = new RuleButton[rules.length];
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(rules.length, 1));
		
		JScrollPane scroll = new JScrollPane();
		scroll.setBounds(X_MARGIN/10, (int) (Y_MARGIN * 1.2), GUI_SIZE.width/4, (int) (GUI_SIZE.getHeight()-(int) (Y_MARGIN * 1.3)));
		scroll.getViewport().add(panel);
		c.add(scroll);	
		
		//create ChessBoard display
		display = new Display(gui, rules[0]);
		display.setBounds(scroll.getWidth() + scroll.getX() + X_MARGIN/10, (int) (Y_MARGIN * 0.6), c.getWidth() - (scroll.getWidth() + scroll.getX() + X_MARGIN/10), scroll.getHeight());
		c.add(display);
	
		
		describe = new JLabel("Description:");
		describe.setBounds(display.getX(), display.getHeight() + display.getY() - GUI_SIZE.height / 20, display.getWidth(), (int) (GUI_SIZE.height / 20));
		describe.setFont(new Font("Arial", Font.PLAIN, GUI_SIZE.height / 30));
		c.add(describe);
		
		description = new JTextArea();
		description.setBorder(BorderFactory.createEtchedBorder());
		description.setWrapStyleWord(true);
		description.setEditable(false);
		description.setLineWrap(true);
		description.setBounds(display.getX(), describe.getHeight() + describe.getY() + GUI_SIZE.height / 1000, display.getWidth(), GUI_SIZE.height / 8);
		c.add(description);
		
		
		for (int i = 0; i < rules.length; i++) {
			ruleBtn[i] = new RuleButton(rules[i]);
			ruleBtn[i].setBounds(X_MARGIN/10, i * (int) (Y_MARGIN * 0.2), X_MARGIN - X_MARGIN/10, (int) (Y_MARGIN * 0.2));
			panel.add(ruleBtn[i]);
		}
		
		//set off the first one
		ruleBtn[0].getActionListeners()[0].actionPerformed(null);
	}//end of constructor
	
	/**
	 * This is only used by the Rule Panel in order to create the Rule Buttons in the Scroll pane that is
	 * in the Rule Panel. This class represents one of these buttons and is separated in order to store variables
	 * that are button specific and as well as letting us create an Action Listener that is also buttons specific.
	 * 
	 * @author Robert and Frank
	 */
	class RuleButton extends JButton {
		
		/**
		 * Creates a RuleButton with a given chessFile showing the given properties
		 * @param RuleFile - the rule file to get information from
		 */
		protected RuleButton(RuleFile file) {
			setLayout(new GridLayout(1, 2));
			
			addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					for (JButton b: RulePanel.this.ruleBtn){
						b.setBorder(NOT_CHOSEN);
					}
					setBorder(CHOSEN);
					
					Rectangle r = display.getBounds();
					display.setVisible(false);
					display = new Display(gui, file);
					display.setBounds(r);
					c.add(display);
					description.setText(file.getDescription());
				}//end of actionPerformed
			});
			
			JLabel label = new JLabel(file.getName());
			label.setFont(new Font("Arial", Font.BOLD, GUI_SIZE.height / 50));
			label.setHorizontalAlignment(SwingConstants.CENTER);
			add(label);
			
			JCheckBox checkBox = new JCheckBox();
			checkBox.setBackground(getBackground());
			checkBox.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					file.setMastered(checkBox.isSelected());
				}
			});
			checkBox.setSelected(file.isMastered());
			checkBox.setHorizontalAlignment(SwingConstants.RIGHT);
			add(checkBox);
		}//end of SpecificFileButton constructor
	}//end of RuleButton Class
}//end of RulePanelClass
