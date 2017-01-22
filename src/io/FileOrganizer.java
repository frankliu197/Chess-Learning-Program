package io;

import io.ChessFile;
import pieces.Turn;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * This class contains many static methods that help with manipulating ChessFiles and 
 * SpecificFiles
 * 
 * @author frankliu197
 * @since October 7, 2016
 */
public class FileOrganizer {

	/**
	 * Constructor method. <br> This has no use for this class except to prevent a creation
	 * of this object
	 */
	private FileOrganizer() {}

	/**
	 * Returns if a chessfile with a certain name and specificFileType exists.
	 * @param name (String) - name of the file
	 * @param specificFileType (String) - a SpecificFileType constant
	 * @return boolean if a chessFile already exists	 
	 */
	public static boolean chessFileExists(String name, String specificFileType){
		ChessFile chessFile = new ChessFile(name.trim(), ChessFile.DEFAULT_DIFFICULTY, specificFileType, ChessFile.DEFAULT_TURN);
		
		return chessFile.getFileLocation().exists();
	}

	/**
	 * Creates a chessFile with the given information<br>
	 * This will automatically store the chessFile into file<br>
	 * WARNING: this will also overwrite any files
	 * 
	 * @param name (String) - name of the file
	 * @param difficulty (Difficulty) - difficulty of the file
	 * @param turn (Turn) - turn of the file
	 * @param specificFileType (String) - a SpecificFileType constant
	 * @return chessFile (ChessFile) - the new chessFile created with the given information
	 */
	public static ChessFile createChessFile(String name, Difficulty difficulty, Turn turn, String specificFileType) {
		ChessFile chessFile = new ChessFile(name.trim(), difficulty, specificFileType, turn);
		
		//create files
		try {
			chessFile.getFileLocation().createNewFile();
			chessFile.getSpecificFileLocation().createNewFile();
		} catch (IOException e1) {
			assert false : "IOException";
		}
	
		// write the objects to file
		try (ObjectOutputStream specificFileOOS = newOOS(chessFile.getSpecificFileLocation());
				ObjectOutputStream chessFileOOS = newOOS(chessFile.getFileLocation());) {
	
			// write the chessFile
			chessFileOOS.writeObject(chessFile);
	
			// create the SpecificFile and write it in
			switch (specificFileType) {
			case SpecificFile.TACTIC_FILE:
				specificFileOOS.writeObject(new TacticFile(chessFile));
				break;
			case SpecificFile.GAME_FILE:
				specificFileOOS.writeObject(new GameFile(chessFile));
				break;
			case SpecificFile.OPENING_FILE:
				specificFileOOS.writeObject(new OpeningFile(chessFile));
				break;
			case SpecificFile.SAVED_FILE:
				specificFileOOS.writeObject(new SavedFile(chessFile));
			}
	
		} catch (IOException e) {
			e.printStackTrace();
		}
		return chessFile;
	}//end of deltelChessFile method

	/**
	 * Deletes the chessFile you sent in
	 * 
	 * @param chessFile (ChessFile) - chessFile to delete
	 */
	public static void deleteChessFile(ChessFile chessFile) {
		chessFile.getFileLocation().delete();
		chessFile.getSpecificFileLocation().delete();
	}

	/**
	 * Gives you a list of SpecificFiles for the specificFileType of file you want
	 * 
	 * @param specificFileType (string) - a specificFileType Constant
	 * @return chessFile[] - all ChessFiles of that specificFileType
	 */
	public static ChessFile[] getChessFiles(String specificFileType) {
		File file = new File(ChessFile.FOLDER_NAME + "/" + specificFileType);
		File[] list = file.listFiles();
		ChessFile[] chessFileList = new ChessFile[list.length];
	
		//get all chessFile info put into the new arrayList
		for (int i = 0; i < list.length; i++) {
			try (ObjectInputStream ois = newOIS(list[i]);) {
				chessFileList[i] = (ChessFile) ois.readObject();
			} catch (IOException e) {
				assert false : "IOFile does not exist";
			} catch (ClassNotFoundException e) {
				assert false : "Cannot read chess tree";
			}
		}
		return chessFileList;
	}//end of getChessFiles method

	/**
	 * Get all the rule Files
	 * @return Rules[] - all rule files
	 */
	public static RuleFile[] getRules() {
		File file = new File(RuleFile.FOLDER_NAME);
		File[] list = file.listFiles();
		RuleFile[] rulesList = new RuleFile[list.length];

		for (int i = 0; i < list.length; i++) {
			try (ObjectInputStream ois = newOIS(list[i]);) {
				rulesList[i] = (RuleFile) ois.readObject();
			} catch (IOException e) {
				assert false : "IOFile does not exist";
			} catch (ClassNotFoundException e) {
				assert false : "Cannot read chess tree";
			}
		}
		
		Arrays.sort(rulesList, new Comparator<RuleFile>(){

			@Override
			public int compare(RuleFile o1, RuleFile o2) {
				if (o1.getLessonNumber() < o2.getLessonNumber()){
					return -1;
				} else {
					return 1;
				}
			}
			
		});
		return rulesList;
	}//end of getRules method

	/**
	 * Opens the SpecificFile with with the following specified ChessFile <br> \
	 * NOTE: NOT RECOMMENDED FOR SAVED FILE
	 * 
	 * @param chessFile (ChessFile) - The ChessFile to open
	 * @return SpecificFile - returns the specificFile that the chessFile
	 *         contains
	 */
	public static SpecificFile openChessFile(ChessFile chessFile) {
		try (ObjectInputStream ois = newOIS(chessFile.getSpecificFileLocation())) {
			return (SpecificFile) ois.readObject();
		} catch (IOException e) {
			assert false : "IOFile does not exist";
		} catch (ClassNotFoundException e) {
			assert false : "Cannot read chess tree";
		}
		return null;
	}//end of openChessFile method

	/**
	 * Finds and returns all matching ChessFiles with the given name
	 * 
	 * @param chessFiles (ChessFile[]) - array to search in
	 * @param name (String) - the string to search for in the chessFile names
	 * @return chessFile[] - all matching files with the given string
	 */
	public static ChessFile[] search(ChessFile[] chessFiles, String name) {
		ArrayList<ChessFile> matches = new ArrayList<>();
	
		for (int i = 0; i < chessFiles.length; i++) {
			//make it case insensitive
			if (chessFiles[i].getName().toLowerCase().contains(name.toLowerCase())) {
				matches.add(chessFiles[i]);
			}
		}
		return matches.toArray(new ChessFile[matches.size()]);
	}//end of search method

	/**
	 * Sorts the following chessFiles[] with the given sortingMethod
	 * 
	 * @param chessFiles (ChessFile[]) - files to sort
	 * @param sortingMethod (SortingMethod) - sortingMethod to use
	 */
	public static void sortChessFiles(ChessFile[] files, SortingMethod sortingMethod) {
		mergeSort(files, 0, files.length, sortingMethod);
	}//end of sortChessFiles

	/**
	 * This saves the new General File state at getFileLocation. It will create a new file if it doesn't exists<br>
	 * Note: Does not touch any other file related to it. E.g. if this is a chessFile,
	 * it does not save the specificFile.
	 * 
	 * @param generalFile (GeneralFile) - the file to save
	 */
	protected static void saveFile(GeneralFile generalFile){
		File location = generalFile.getFileLocation();
		
		try {
			location.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		try (ObjectOutputStream oos = newOOS(location)) {
			oos.writeObject(generalFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}//end of saveGeneralFile

	/**
	 * Sorts the chessFiles using mergeSort
	 * 
	 * @param files (ChessFile[]) - the chessFile array to sort
	 * @param start (int) - the index of the array to start sorting at (inclusive)
	 * @param end (int) - the index of the the array to end sorting at (exclusive)
	 */
	private static void mergeSort(ChessFile[] files, int start, int end, SortingMethod sortingMethod) {
		if (end - start < 3) {
			// insertion sort it
			for (int j = start + 1; j < end; j++) {
				int i = j - 1;
				ChessFile key = files[j];
				while (i >= start && sortingMethod.compare(key, files[i]) < 0) {
					files[i + 1] = files[i];
					i--;
				}
				files[i + 1] = key;
			}
		} else {
			int middle = (start + end) / 2;
			mergeSort(files, start, middle, sortingMethod);
			mergeSort(files, middle, end, sortingMethod);
	
			int pointer1 = 0, pointer2 = middle - start, p1end = middle - start, p2end = end - start;
			int counter = start;
	
			ChessFile[] original = Arrays.copyOfRange(files, start, end);
	
			while (pointer1 < p1end && pointer2 < p2end) {
				if (sortingMethod.compare(original[pointer1], original[pointer2]) < 0) {
					files[counter++] = original[pointer1++];
				} else {
					files[counter++] = original[pointer2++];
				}
			}
	
			while (pointer1 < p1end) {
				files[counter++] = original[pointer1++];
			}
	
			while (pointer2 < p2end) {
				files[counter++] = original[pointer2++];
			}
		}
	}//end of mergeSort method

	/**
	 * Creates an ObjectOutputStream with the given file
	 * 
	 * @param file (File) - the file to read from
	 * @return ObjectOutputStream - with the given fileName:<br>
	 *         new ObjectOutputStream(new BufferedOutputStream(new
	 *         FileOutputStream(fileName)))
	 * @throws FileNotFoundException - if the file exists does not exist
	 */
	private static ObjectInputStream newOIS(File file) throws FileNotFoundException, IOException {
		return new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
	}

	/**
	 * Creates an ObjectOutputStream with the given file
	 * 
	 * @param file (File) - the file to use (create new file, or destroy old file and
	 *            create this file)
	 * @return ObjectOutputStream - with the given fileName:<br>
	 *         new ObjectOutputStream(new BufferedOutputStream(new
	 *         FileOutputStream(fileName)))
	 * @throws FileNotFoundException - if the file exists but is a directory rather than a regular
	 *             file, does not exist but cannot be created, or cannot be
	 *             opened for any other reason
	 * @throws IOException - An IO exception occurred
	 */
	private static ObjectOutputStream newOOS(File file) throws FileNotFoundException, IOException {
		return new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
	}
	
	/**
	 * This class contains a bunch of sorting methods for FileOrganizer
	 * @author frankliu197
	 * @since November 2
	 */
	public enum SortingMethod implements Comparator<ChessFile> {
		A_TO_Z, Z_TO_A, EASY_TO_HARD, HARD_TO_EASY, WHITE_TO_BLACK, BLACK_TO_WHITE, MASTERED, NOT_MASTERED;
		
		/**
		 * This is the general compare method for comparator. It compares the two chessFiles
		 * by determining the proper comparator and asking it to compare for it.
		 * 
		 * @param chessFile1 (chessFile) - ChessFile to compare
		 * @param chessFile2 (chessFile) - other chessFile to compare
		 * @return int - larger than 1 if chessFile1 is before chessFile2 according to the comparator,
		 *        0 if equal and -1 otherwise
		 */
		@Override
		public int compare(ChessFile chessFile1, ChessFile chessFile2){
			//note *-1 gives the opposite value
			switch (this){
			case A_TO_Z: return compareAToZ(chessFile1, chessFile2);
			case Z_TO_A: return compareAToZ(chessFile1, chessFile2) * -1;
			case EASY_TO_HARD: return compareEasyToHard(chessFile1, chessFile2);
			case HARD_TO_EASY: return compareEasyToHard(chessFile1, chessFile2) * -1;
			case WHITE_TO_BLACK: return compareWhiteToBlack(chessFile1, chessFile2);
			case BLACK_TO_WHITE: return compareEasyToHard(chessFile1, chessFile2) * -1;
			case MASTERED: return compareMastered(chessFile1, chessFile2);
			case NOT_MASTERED : return compareMastered(chessFile1, chessFile2) * -1;
			default: throw new IllegalArgumentException("INVALID SORTING ENUM");
			}
		}
		
		/**
		 * Compares the name of the chessFile
		 * from a to z. Numbers come before letters.<br>
		 * NOTE: this method does not just contain chessFile1.getName().compareTo(chessFile2.getName())
		 * 		because <b>1</b>9 will come before 2 in this case. This numbers will be tested together<br>
		 * NOTE: This is not case sensitive.
		 * 
		 * @param chessFile1 (chessFile) - ChessFile to compare
		 * @param chessFile2 (chessFile) - other chessFile to compare
		 * @return int - larger than 1 if chessFile2's name happens before
		 *         chessFile1, 0 if equal and -1 otherwise
		 */
		private int compareAToZ(ChessFile chessFile1, ChessFile chessFile2) {
			String name1 = chessFile1.getName().toLowerCase();
			String name2 = chessFile2.getName().toLowerCase();

			for (int l1 = 0, l2 = 0; l1 < name1.length() && l2 < name2.length(); l1++, l2++) {
				//compare digits first
				if (Character.isDigit(name1.charAt(l1)) && Character.isDigit(name2.charAt(l2))) {
					//both are digits
					
					// store original
					int o1 = l1;
					int o2 = l2;

					//get all digits together and compare them
					while (l1 < name1.length() && Character.isDigit(name1.charAt(l1))) {
						l1++;
					}

					while (l2 < name2.length() && Character.isDigit(name2.charAt(l2))) {
						l2++;
					}

					int num1 = Integer.parseInt(name1.substring(o1, l1));
					int num2 = Integer.parseInt(name2.substring(o2, l2));
					if (num1 > num2) {
						return 1;
					} else if (num1 < num2) {
						return -1;
					} else {
						continue;
					}
					
				} else if (Character.isDigit(name2.charAt(l2))) {
					return 1;
				} else if (Character.isDigit(name1.charAt(l1))){
					return -1;
				}
				
				//compare characters
				int compareValue = new Character(name1.charAt(l1)).compareTo(name2.charAt(l2));
				if (compareValue != 0) {
					return compareValue;
				}
			}
			return 0;
		}//end of CompareAToZ method
		
		/**
		 * Compares the difficulty of the
		 * chessFile from easy to hard
		 * 
		 * @param chessFile1 (chessFile) - ChessFile to compare
		 * @param chessFile2 (chessFile) - other chessFile to compare
		 * @return int - larger than 1 if chessFile2's is more difficult, 0
		 *         if equal and -1 otherwise
		 */
		private int compareEasyToHard(ChessFile chessFile1, ChessFile chessFile2) {
			return chessFile1.getDifficulty().ordinal() - chessFile2.getDifficulty().ordinal();
		}
		
		/**
		 * Compares the difficulty of the
		 * chessFile from white to black
		 * 
		 * @param chessFile1 (chessFile) - ChessFile to compare
		 * @param chessFile2 (chessFile) - other chessFile to compare
		 * @return int - 0 if the turns are the same, 1 is chessFile2's turn
		 *         is white and chessFile1 is black, and -1 if it is the
		 *         otherway around
		 */
		private int compareWhiteToBlack(ChessFile chessFile1, ChessFile chessFile2) {
			return chessFile1.getTurn().ordinal() - chessFile2.getTurn().ordinal();
		}
		
		/**
		 * Compares it based on mastered to
		 * not mastered
		 * 
		 * @param chessFile1 (chessFile) - ChessFile to compare
		 * @param chessFile2 (chessFile) - other chessFile to compare
		 * @return int - 0 if both are chessFiles.isMastered are the same, 1
		 *         if chessFile2 is not mastered and chessFile1 is mastered,
		 *         and -1 if it is the otherway around
		 */
		private int compareMastered(ChessFile chessFile1, ChessFile chessFile2) {
			//convert boolean value to int and substract them
			return (chessFile1.isMastered() ? 1 : 0) - (chessFile2.isMastered() ? 1 : 0);
		}
	}//end of SortingMethod class

	/**
	 * Deletes all the the temparary files if the user gets rid of the program unexpectedly
	 * Temparary files are chessFiles with an empty name.
	 */
	public static void deleteTempFiles() {
		String[] fileTypes = {SpecificFile.GAME_FILE, SpecificFile.OPENING_FILE, SpecificFile.SAVED_FILE, SpecificFile.TACTIC_FILE};
		for (String type: fileTypes){
			ChessFile[] files = FileOrganizer.getChessFiles(type);
			for (ChessFile file: files){
				if (file.getName().equals("")){
					FileOrganizer.deleteChessFile(file);
				}
			}
		}
	}//end of deleteTempFile method
}//end of FileOrganizer class