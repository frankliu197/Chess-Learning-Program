package io;

import java.io.File;
import java.io.Serializable;

/**
 * This is an abstract class place holder for all Files
 * @author frankliu197
 * @since December 15
 */
public abstract class GeneralFile implements Serializable{
	/** The file Location of the General File */
	private File file;
	
	/**
	 * Creates a generalIO file with the given file location.
	 * NOTE: it will not save this to file. To save to file, use FileOrganizer.
	 * @param file (File) - fileLocation of this generalFile
	 */
	protected GeneralFile(File file){
		this.file = file;
	}
	
	/**
	 * Returns the file location of this generalFile
	 * @return fileLocation (File) - the file location of this general file
	 */
	protected File getFileLocation(){
		return file;
	}
	
	/**
	 * Sets the fileLocation the the given file. This will delete the old file and create the new one for you. <Br>
	 * ote that is will only effect the given file, not any files related to it
	 * @return fileLocation (File) - the fileLocation
	 */
	protected void setFileLocation(File file){
		this.file.delete();
		this.file = file;
		FileOrganizer.saveFile(this);
	}
}//end of general file class
