package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * This is an AlertPane, a custom JOption Pane. Where the frame is automatically customize according to bounds<Br>
 * 
 * NOTE: all of its code is copied from its source code. So i give all the credit of this class to 
 * the Java JOptionPane makers.
 * my code as commented
 * @author frankliu197
 */
public class AlertPane {
	/**The default spacing factor between 2 GUI components*/
	private static final double SPACING_FACTOR = 1.2;
	
	/**A Value if you don't want any full screen buttons*/
	public static final int NO_FULL_SCREEN_BUTTONS = -1;
	
	/** Identifier for the question message type. */
	public static final int QUESTION_MESSAGE = JOptionPane.QUESTION_MESSAGE;
	
	/** Identifier for the plain message type. */
	public static final int PLAIN_MESSAGE = JOptionPane.PLAIN_MESSAGE;
	
	/** An option used in confirmation dialog methods. */
	public static final int YES_NO_CANCEL_OPTION = JOptionPane.YES_NO_CANCEL_OPTION;
	
	/** An option used in confirmation dialog methods. */
	public static final int YES_NO_OPTION = JOptionPane.YES_NO_OPTION;
	
	/** The value returned when the yes option is selected. */
	public static final int YES_OPTION = JOptionPane.YES_OPTION;
	
	/** The value returned when the no option is selected. */
	public static final int NO_OPTION = JOptionPane.NO_OPTION;
	
	/** The value returned when cancel option is selected. */
	public static final int CANCEL_OPTION = JOptionPane.CANCEL_OPTION;
	
	/** The value returned when the dialog is closed without a selection. */
	public static final int CLOSED_OPTION = JOptionPane.CLOSED_OPTION;
	
	/**
	 * This method shows an option dialog with the given message, title,
 	 * optionType, messageType, icon, options and initialValue. This method
 	 * returns the option that was selected. 
 	 * @param message (Object) - The message displayed.
 	 * @param title (String) - The title of the dialog.
 	 * @param message (int) - Type The messageType.
 	 * @param options (Object[]) - he options to choose from.
 	 * @param initialValue (Object) - The initial value.
 	 * @param fullScreen (int) - The button index after you want fullsized buttons instead of half sized ones
 	 *
 	 * @return The selected option.
	 */
	public static int showOptionDialog(Object message, String title, int messageType, Object[] options, Object initialValue, int fullScreen){
		JOptionPane pane = new JOptionPane(message, messageType, JOptionPane.YES_NO_CANCEL_OPTION, null,
				options, initialValue);
		
		JDialog dialog = pane.createDialog(null, title);
		dialog.setBounds(GUI.CENTER_OF_GUI);
		pane.setLayout(null);
		formatTitle(pane, GUI.CENTER_OF_GUI, 0);
		formatButtons(pane, fullScreen, GUI.CENTER_OF_GUI, 2, false);
		dialog.show();
		
		return getReturnValue(pane.getValue(), options);		
   }//end of showOptionDialog method
	
	/**
	 * This method creates a loading screen and returns it
	 * @param message the message to show
	 * @return JDialog - the dialog that is showing
	 */
	public static JDialog showLoadingDialog(Object message){
		JOptionPane pane = new JOptionPane(message);
		
		Rectangle load = new Rectangle(GUI.SMALL_CENTER_OF_GUI.x + GUI.SMALL_CENTER_OF_GUI.width / 4, GUI.SMALL_CENTER_OF_GUI.y, GUI.SMALL_CENTER_OF_GUI.width / 2, GUI.SMALL_CENTER_OF_GUI.height / 2);
		JDialog dialog = pane.createDialog(null, "Loading");
		dialog.setBounds(load);
		pane.setLayout(null);
		formatTitle(pane, load, 0);
		
		Container buttonsCon = getContainer(pane, 1);
		buttonsCon.getComponents()[0].hide();
		return dialog;
   }//end of showOptionDialog method

    /** Gets a Container with index i from the container send in getComponents method. It will throw a class
	 * cast exception if the component is not an container
	 * @param c (Container) - the Big container
	 * @param i (int) - the index
	 * @return Container within the big container with index i
	 */
	private static Container getContainer(Container c, int i){
		return (Container) c.getComponent(i);
	}
	
	/**
	 * Formats the title container and the title and some other titleComponents if there are any. The size and dimension of the title depends on CENTER_OF_Gui and small
	 * @param optionPane (JOptionPane) - the JOptionPane to edit
	 * @param bounds (Rectangle) - the bounds of the dialog
	 * @param j (int) - the number of title Components not to format
	 * @return ArrayList<JLabel> - the edited JLabels
	 */
	private static ArrayList<JLabel> formatTitle(JOptionPane optionPane, Rectangle bounds, int j){
		Container titleCon = getContainer(optionPane, 0);
		titleCon.setLayout(null);
		
		int height = (int) (bounds.height * 0.4);
		titleCon.setBounds(0, 0, bounds.width, height);
		
		//code to get JLabel from complex JOptionsPane through further investigation
		Container c = getContainer(titleCon, 0);
		c.setLayout(null);
		c.setBounds(0, 0, bounds.width, (int) height);
		c = getContainer((Container) c, c.getComponents().length - 1);
		c.setLayout(null);
		c.setBounds(0, 0, bounds.width, (int) height);
		
		int totalLbls = c.getComponentCount();
		Font font;
		Rectangle r;
		if (totalLbls > 1){
			font = new Font("Arial", Font.BOLD, bounds.height / (totalLbls - 1)/ 10);
			r = new Rectangle(0, bounds.height / 15, (int) bounds.getWidth(), (int) (bounds.height * 0.25 / (totalLbls - 1)));
		} else {
			font = new Font("Arial", Font.BOLD, bounds.height / 10);
			r = new Rectangle(0, bounds.height / 7, (int) bounds.getWidth(), bounds.height / 5);
		}
		
		ArrayList<JLabel> labels = new ArrayList<>();
		
		for (int i = 0, i2 = 0; i < totalLbls - j; i++, i2++){
			//instance of is not working so no elegant solution here
			try {
				JLabel lbl = (JLabel) c.getComponent(i);
				lbl.setForeground(Color.RED);
				lbl.setBounds(r.x, (int) (r.y + r.height * i2 * GUI.SPACING_FACTOR), r.width, r.height);
				lbl.setFont(font);
				lbl.setHorizontalAlignment(SwingConstants.CENTER);
				lbl.setVerticalAlignment(SwingConstants.CENTER);
				labels.add(lbl);
			} catch (ClassCastException e){
				//did not format a JLabel
				i2 --;
			}
			
		}
		return labels;	
	}//end of formatTitle method
	
	/**
	 * Returns the return value of object corresponding to its int. Answer can be null.
	 * @param answer (Object) - pane.getValue() value
	 * @param options (Object[])- the options to choose from
	 * @return int - the corrosponding value
	 */
	private static int getReturnValue(Object answer, Object[] options){
		if (answer == null){
			return -1;
		}
		for (int i = 0; i < options.length; i++){
			if (answer.equals(options[i])){
				return i;
			}
		}
		return -1;
	}//end of getReturnValue method

	 /**
	  * This method shows a confirmation dialog with the given message,
	  * optionType and title. The frame that owns the dialog will be the same
	  * frame that holds the given parentComponent. This method returns the
	  * option that was chosen.
	  * @param message (Object) - The message displayed.
	  * @return int - The option that was chosen.
	  */
	public static int showConfirmDialog(Object message){
		JOptionPane pane = new JOptionPane(message, PLAIN_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION);
		JDialog dialog = pane.createDialog(null, "Select an Option");
		dialog.setBounds(GUI.SMALL_CENTER_OF_GUI);
		pane.setLayout(null);
		
		formatTitle(pane, GUI.SMALL_CENTER_OF_GUI,  0);
		
		formatButtons(pane, AlertPane.NO_FULL_SCREEN_BUTTONS, GUI.SMALL_CENTER_OF_GUI, 3, true);
		dialog.show();
		
		if (pane.getValue() instanceof Integer){
			return ((Integer) pane.getValue()).intValue();
		}
	    
		return -1;
	}//end of showConfirmDialog
	
	/**
	  * This method shows an INFORMATION_MESSAGE type message dialog.
	  * @param message (Object) - The message displayed.
	  */
	public static void showMessageDialog(Object message) {
		JOptionPane pane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE);
	    JDialog dialog = pane.createDialog(null, null);
	    dialog.setBounds(GUI.SMALL_CENTER_OF_GUI);
	    pane.setLayout(null);
		formatTitle(pane, GUI.SMALL_CENTER_OF_GUI, 0);
	    formatOkButton(pane, GUI.SMALL_CENTER_OF_GUI, true);
	    dialog.show();
	}

	/**
	 * This method formats an Ok button. It assumes that 
	 * @param pane (JOptionPane) - the JOptionPane to edit
	 * @param bounds (Rectangle) - bounds of the dialog
	 * @param isMessage (boolean) - if this is a message pane
	 */
	private static void formatOkButton(JOptionPane pane, Rectangle bounds, boolean isMessage) {
		Container buttonsCon = getContainer(pane, 1);
		buttonsCon.setLayout(null);
		buttonsCon.setBounds(0, (int) (bounds.height * 0.4), bounds.width, (int) (bounds.height * 0.4));
		Component btn = (JButton) buttonsCon.getComponents()[0];
		btn.setFont(new Font("Arial", Font.BOLD, bounds.height / 13));
		btn.setForeground(Color.RED);
		btn.setBounds((int) (bounds.width * 0.4), (int) (bounds.height * 0.1), (int) (bounds.width * 0.2), (int) (bounds.height * 0.2));
	}

	/**
	 * This method will show a QUESTION_MESSAGE input dialog with the given
	 * message. No selectionValues is set so the Look and Feel will usually
	 * give the user a TextField to fill out.This method will return the
	 * value entered by the user and null if they pressed cancel
	 * @param message (Object) - The message displayed.
	 * @return The value entered by the user.
	 */
	public static String showInputDialog(Object message) {
		JOptionPane pane = new JOptionPane(message, QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION);
	   	pane.setWantsInput(true);
	   	JDialog dialog = pane.createDialog(null, null);
	   	dialog.setBounds(GUI.SMALL_CENTER_OF_GUI);
	   	
	   	//small shift
	   	ArrayList<JLabel> label = formatTitle(pane, GUI.SMALL_CENTER_OF_GUI,  1);
	   	for (JLabel l: label){
	   		l.setBounds(l.getX(), l.getY() - GUI.SMALL_CENTER_OF_GUI.height / 8, l.getWidth(), l.getHeight());
	   	}
	   	
	   	
		//had to do this because instance JTextField of doesn't work for some reason
	   	Rectangle boundsTitle = new Rectangle(0, 0, (int) GUI.SMALL_CENTER_OF_GUI.getWidth(), (int) (GUI.SMALL_CENTER_OF_GUI.height * 0.25));
	   	Container titleCon = getContainer(pane, 0);
		
	   	Container c = getContainer(titleCon, 0);
		c = getContainer((Container) c, c.getComponents().length - 1);
		
		JTextField txtField = (JTextField) c.getComponent(c.getComponentCount() - 1);
		txtField.setBounds(boundsTitle.width / 4, (int) (boundsTitle.height * 0.6 * GUI.SPACING_FACTOR), boundsTitle.width / 2, (int) (boundsTitle.height * 0.6));
		txtField.setFont(new Font("Arial", Font.BOLD, GUI.SMALL_CENTER_OF_GUI.height / 16));
		txtField.setHorizontalAlignment(SwingConstants.CENTER);
		
		//format buttons
		formatButtons(pane, AlertPane.NO_FULL_SCREEN_BUTTONS, GUI.SMALL_CENTER_OF_GUI, 3, true);
		Container buttonsCon = getContainer(pane, 1);
		Component[] buttons = buttonsCon.getComponents();
		
		//move buttons up
		for (int i = 0; i < buttonsCon.getComponentCount(); i++){
			buttons[i].setBounds(buttons[i].getX() - buttons[i].getWidth() / 10, buttons[i].getY() - GUI.SMALL_CENTER_OF_GUI.height / 4, buttons[i].getWidth(), buttons[i].getHeight());
		}
		((JButton) buttons[0]).setText("Ok");
		buttons[1].hide();
		
		dialog.show();
		
		String toReturn = (String) pane.getInputValue();
		if (toReturn.equals(JOptionPane.UNINITIALIZED_VALUE)){
			return null;
		}
		return toReturn;
	 }//end of showInputDialog method
	
	/**
	 * Formats the buttons in the JOptionpane. It does half sized buttons until you reach the full size index
	 * @param pane (The JOptionPane) - The OptionPane to format the buttons on
	 * @param fullScreen (int) - after which index do of buttons you want full sized button
	 * @param bounds (Rectangle) - the bounds of the dialog
	 * @param columns (int) - the number of max columns you want
	 * @param small (boolean) - if you want the buttons to be small
	 */
	private static void formatButtons(JOptionPane pane, int fullScreen, Rectangle bounds, int columns, boolean small) {
		Container buttonsCon = getContainer(pane, 1);
		//set bounds to rest of gui after title
		buttonsCon.setBounds(0, bounds.height / 5, bounds.width, bounds.height - bounds.height / 5);
		buttonsCon.setLayout(null);
		Component[] btnArr = buttonsCon.getComponents();
		
		//row, column number
		int r = 0;
		int c = 0;
		
		if (fullScreen == NO_FULL_SCREEN_BUTTONS){
			fullScreen = btnArr.length;
		}
		
		int fullScreenNum = btnArr.length - fullScreen;
		int otherNum = btnArr.length - fullScreenNum;
		int xMargin = bounds.width / 30;
		int yMargin = (int) (buttonsCon.getBounds().height * 0.3);
		int ySize = (int) (buttonsCon.getBounds().height * 0.6 / ((fullScreenNum + otherNum * 0.5) * SPACING_FACTOR));
		Font font = new Font("Arial", Font.BOLD, bounds.height / 20);
		
		if (small){
			yMargin += yMargin * 0.6;
			ySize = (int) (ySize * 0.8);
		} 
		
		for (int i = 0; i < btnArr.length; i++){
			JButton btn = (JButton) btnArr[i];
			btn.setFont(font);
			if (i >= fullScreen){
				btn.setBounds(xMargin, (int) (yMargin + r * ySize * SPACING_FACTOR), bounds.width - xMargin * 2, ySize);
				r++;
			} else {
				btn.setBounds(xMargin + (bounds.width) / columns * c, (int) (yMargin + r * ySize * SPACING_FACTOR), (bounds.width - xMargin * columns * 2) / columns, ySize);
				c++;
				
				if (c == columns){
					c = 0;
					r++;
				}
			}
		}//end for loop
	}//end of formatButtons method
}//end of AlertPane