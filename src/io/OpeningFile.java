package io;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;

import chessgame.ChessBoard;
import chessgame.Move;
import pieces.Piece;

/** 
 * This class contains the openingFile along with its iterator
 * It is actually a tree.
 * 
 * @author Frank
 * @since October 12, 2016
 */
public class OpeningFile extends SpecificFile {
	/** The root Chess node of the tree */
	private Node rootChessNode;
	
	/** the notes for the opening itself */
	private String openingNotes;

	/**
	 * Creates a OpeningFile (tree) with no Nodes <br>
	 * NOTE: used fileOrganizer to write this object to file
	 * @param chessFile (ChessFile) - location of the corresponding chessFile
	 */
	protected OpeningFile(ChessFile chessFile) {
		super(chessFile);
		rootChessNode = new Node(null, null);
	}

	@Override
	public ArrayList<Move> getMoveHistory() {
		throw new IllegalStateException();
	}

	
	@Override
	public SpecificFileIterator getSpecificFileIterator() {
		return new OpeningFileIterator();
	}

	@Override
	public Piece[][] getEndingPosition() {
		throw new IllegalStateException();
	}

	@Override
	public Piece[][] getStartingPosition() {
		return ChessBoard.getDefaultBoard();
	}
	
	@Override
	public void resetInformation(Piece[][] startingPosition, ArrayList<Move> moveHistory, Piece[][] endingPosition) {
		SpecificFileIterator iterator = getSpecificFileIterator();
		iterator.deleteMovesRegardless();
		for (Move move: moveHistory){
			iterator.addMove(move);
		}
	}//end of resetInformation method

	/**
	 * The OpeningFileIterator is the class that interacts the information in
	 * the tree with the other packages. It also allows some functions to change
	 * the information in the tree.
	 */
	public class OpeningFileIterator extends SpecificFileIterator {
		/** The current node that the iterator is on */
		private Node currentNode;

		/**
		 * Creates a new OpeningIterator with no special Properties
		 */
		private OpeningFileIterator() {
			currentNode = rootChessNode;
		}

		@Override
		public void addMove(Move move) {
			if (currentNode.searchNodes(move) == null) {
				currentNode.nextNodes.add(new Node(move, currentNode));
				currentNode = currentNode.searchNodes(move);
			} else {
				throw new IllegalStateException();
			}
		}

		@Override
		public void addStartingPosition(Piece piece, int[] position) {
			throw new IllegalStateException();
		}

		@Override
		public Move deleteMove() {
			if (!currentNode.nextNodes.isEmpty()) {
				throw new IllegalStateException();
			}
			return deleteMovesRegardless();
		}

		@Override
		public Move deleteMovesRegardless() {
			Move toReturn = undo();
			//if (currentNode != rootChessNode) {
				//currentNode = currentNode.previousNode;
				//currentNode.nextNodes.clear();
			//}
			currentNode.nextNodes.clear();
			return toReturn;
		}

		@Override
		public Move getMoveDone() {
			if (currentNode != rootChessNode) {
				return currentNode.move;
			} else {
				throw new IllegalArgumentException();
			}
		}

		@Override
		public Move toNextMove() {
			if (currentNode.nextNodes.isEmpty()){
				return null;
			}
			currentNode = currentNode.nextNodes.get(0);
			return currentNode.move;
		}

		@Override
		public String getNotes() {
			if (currentNode.move == null) {
				return openingNotes;
			}
			return currentNode.move.getNotes();
		}

		@Override
		public ArrayList<Move> getPossibleMoves() {
			ArrayList<Move> moves = new ArrayList<>(currentNode.nextNodes.size());
		
			// fill array with moves
			for (Node n: currentNode.nextNodes) {
				moves.add(n.move);
			}
			return moves;
		}

		@Override
		public void removeStartingPosition(int[] position) {
			throw new IllegalStateException();
		
		}

		@Override
		public boolean setNextMove(Move move) {
			if (currentNode.searchNodes(move) != null) {
				currentNode = currentNode.searchNodes(move);
				return true;
			}
			return false;
		}

		@Override
		public void setNotes(String notes) {
			if (currentNode.move != null) {
				currentNode.move.setNotes(notes);
			} else {
				openingNotes = notes;
			}
		}

		@Override
		public Piece[][] toEndingPosition() {
			return OpeningFile.this.getEndingPosition();
		}

		@Override
		public Move undo() {
			if (currentNode != rootChessNode) {
				Move toReturn = currentNode.move;
				currentNode = currentNode.previousNode;
				return toReturn;
			} else {
				return null;
			}
		}

		@Override
		public Move getNextMove() {
			if (currentNode.nextNodes.isEmpty()){
				return null;
			}
			return currentNode.nextNodes.get(0).move;
		}
		
	}// end of OpeningFileIterator class

	/**
	 * This is a small part(Node) of the Tree that contains the opponents move,
	 * your response moves, and a list of subnodes.
	 */
	private static class Node implements Serializable, Cloneable {
		private Node previousNode;
		private Move move;
		private ArrayList<Node> nextNodes = new ArrayList<>(1);

		/**
		 * Creates a ChessNode with the following information given in
		 * 
		 * @param move (move) - move taken to get the this node from the previous node
		 * @param previousChessNode (ChessNode) - the previous chess node
		 */
		private Node(Move move, Node previousChessNode) {
			this.move = move;
			this.previousNode = previousChessNode;
		}

		/**
		 * Searches for the ChessNode with corresponding move in the nextNodes list
		 * 
		 * @param move (Move) - the move to be searched
		 * @return ChessNode - the ChessNode that contains "move" in the nextNodes list
		 */
		private Node searchNodes(Move move) {
			for (int i = 0; i < nextNodes.size(); i++) {
				if (nextNodes.get(i).move.equals(move)) {
					return nextNodes.get(i);
				}
			}
			return null;
		}//end of searchNodes method
	}// end of ChessNode class
}// end of OpeningFile Class
