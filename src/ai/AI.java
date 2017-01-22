package ai;

import static chessgame.ChessBoard.inBounds;
import static pieces.PieceType.*;
import static pieces.Turn.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import chessgame.ChessBoard;
import chessgame.Move;
import io.SpecificFile;
import io.SpecificFile.SpecificFileIterator;
import pieces.Piece;
import pieces.PieceType;
import pieces.Turn;

/**
 * This creates an AI, of a certain level. This can also give
 * recommended moves for a certain position of the board
 * 
 * @author frankliu197
 * @since November 17, 2016
 */
public class AI {
	/**The level of the AI*/
	private int aiLevel;
	
	/**The chessBoard this AI is playing on*/
	private ChessBoard chessBoard;
	
	/**The turn of the AI*/
	private Turn aiTurn;
	
	/**The moves Tree this AI is using this turn to evaulate the best move */
	private MovesTree movesTree;
	
	/**True if the AI wants to declare Draw */
	private boolean toDeclareDraw;
	
	/**True if the AI wants to request Draw */
	private boolean toRequestDraw;

	/**
	 * Creates an AI that plays on the chessBoard
	 * @param level - (int) aiLevel
	 * @param chessBoard - (ChessBoard) the chessBoard to play on
	 * @param turn - turn (The turn of the ai)
	 */
	public AI(int level, ChessBoard chessBoard, Turn turn) {
		this.aiLevel = level;
		this.chessBoard = chessBoard;
		this.aiTurn = turn;
	} //end of constructor method

	/**
	 * Returns the level of the AI
	 * @return int - the level of the AI
	 */
	public int getLevel() {
		return aiLevel;
	}

	/**
	 * Returns a Move for the AI to play
	 * 
	 * @return Move - move to play
	 */
	public Move getMove() {
		movesTree = new MovesTree();
		Move toMove = movesTree.getMove();
		return toMove;
	}

	/**
	 * Returns the turn of the AI
	 * @return Turn - the turn of the ai
	 */
	public Turn getTurn() {
		return aiTurn;
	}

	/**
	 * Returns the piece the ai wants to promote into
	 * @return Piece - the piece to promote into
	 */
	public Piece getPromotion() {
		//I believe that even level 1 players will take a QUEEN, so i'll say QUEEN only for now
		return new Piece(aiTurn, QUEEN);
	}

	/**
	 * This asks if the AI wants a draw. The AI determines this if the AI's next moves are less than 48:52
	 * in win ratios. first one being the AI
	 * @return true if the AI wants a draw and false otherWise
	 */
	public boolean askedForDraw() {
		//to add Values onto moveTree
		getMove();
		return movesTree.requestedDraw();
	}
	
	/**
	 * Returns if the AI wants a draw
	 * @return true if the AI wants a draw and false otherwise
	 */
	public boolean requestsDraw(){
		return toRequestDraw;
	}
	
	/**
	 * Returns if the AI declares a draw
	 * @return true if the AI declares a draw and false otherwise
	 */
	public boolean declaresDraw(){
		return toDeclareDraw;
	}

	/**
	 * This is a moves tree to determine which is the best move for the AI.
	 * The Moves Tree determines the values of all moves in the perspective of the AI.
	 * 
	 * @author frankliu197
	 * @since November 17, 2016
	 */
	protected class MovesTree {	

		/** The General Max number of Branches */
		private static final int BRANCHING_FACTOR = 5;
		
		/** The Number of branches where the MovesTree starts cutting down */
		private static final int OVERDUE_FACTOR = 7;
		
		/** Arranges the nodes from from least value to highest. Used to determine the opponents most logical move. */
		private Comparator<Node> other = new Comparator<Node>() {
			@Override
			public int compare(Node o1, Node o2) {
				if (o1.value > o2.value) {
					return -1;
				} else {
					return 1;
				}
			}
		};

		/** thisAI - arranges from highest value to least. Used to determine the ai's most logic move */
		private Comparator<Node> thisAI = new Comparator<Node>() {
			@Override
			public int compare(Node o1, Node o2) {
				if (o1.value > o2.value) {
					return 1;
				} else {
					return -1;
				}
			}
		};

		/**The current node the tree is on*/
		private Node currentNode;
		
		/**The max depth of this move tree.*/
		private int depth;
		
		/**
		 * Constructor: Creates an empty move Tree with a certain Board
		 * 
		 */
		protected MovesTree() {
			currentNode = new Node(null, null, chessBoard);
			
			//determine depth
			if (aiLevel <= 3) {
				depth = 1;
			} else if (aiLevel < 5) {
				depth = 2;
			} else {
				depth = 3;
			}
		}//end of contructor method

		/**
		 * Chooses a move based on AI levels <Br>
		 * Also resets the Variables if the AI wants draw or not
		 * @return Move - (Move) a move to play
		 */
		protected Move getMove(){
			toRequestDraw = false;
			toDeclareDraw = false;
			
			evaluateNode(currentNode);
			double begPoints = currentNode.value;
			Move move = getMoveHelper();
			currentNode.chessBoard.move(move);
			evaluateNode(currentNode);
			double endPoints = currentNode.value;
			String winMethod = currentNode.chessBoard.checkWin();
			currentNode.chessBoard.undo();
			
			//10 being point ratio for draw
			if (begPoints - endPoints < 5 && requestedDraw()){
				toRequestDraw = true;
			}
			
			if (winMethod.equals(ChessBoard.POSSIBLE_DRAW)){
				toDeclareDraw = true;
			}
			
			return move;
		}
		
		/**
		 * Call this method if Draw is Requested.
		 * @return true if the AI should accept the draw and false otherwise. Again this depends on the aiLevel
		 */
		protected boolean requestedDraw(){
			addValues();
			double lessThenDefault = ChessBoardEvaluator.BASE_VALUES[1] - currentNode.value;
			if (Math.random() * 100 < lessThenDefault * aiLevel * aiLevel){
				return true;
			}
			return false;
		}

		/**
		 * Adds values to this MovesTree.
		 * This is the method that starts the recusion for addValues(Node, int)
		 */
		private void addValues() {
			addValues(currentNode, 0);
		}

		/**
		 * addValues method 3: puts a value using the evaulate function for all 
		 * subnodes of the currentNode
		 * 
		 * @param currentNode - (Node) node to add values on
		 */
		private void addValues(Node currentNode) {
			for (Node n : currentNode.nextNodes) {
				evaluateNode(n);
			}
		}//end of addValues method

		/**
		 * This is a recusive method that first adds all the nodes, and then starts 
		 * evaulating the values of each node from the bottom up. <br> <br>
		 * 
		 * It uses PreOrder traversal to first get all the nodes created,
		 * and once all the subnodes are created, it uses postOrder traversal to 
		 * determine the value of the node
		 * 
		 * @param currentNode - (Node) the currentNode to deal with for this recursive call
		 * @param depth - (int) the depth so far in the tree.
		 */
		private void addValues(Node currentNode, int depth) {
			// Ending Condition. If its game over or if depth is reached
			if (depth >= this.depth || !currentNode.chessBoard.checkWin().equals(ChessBoard.CURRENTLY_PLAYING)) {
				if (Math.random() * 2.5 > aiLevel) {
					// be stupid if aiLevel is 1 or 2, return the default value
					currentNode.value = 50;
				} else {
					evaluateNode(currentNode);
				}
				
				return;
			}

			//set/update important variables
			depth++;
			Turn currentTurn = currentNode.chessBoard.getTurn();
			ArrayList<Node> next = currentNode.nextNodes;
			ArrayList<Move> availableMoves = currentNode.chessBoard.getAvailableMoves();
			
			//Adds a node for each availableMoves
			for (Move move : availableMoves) {
				ChessBoard newChessBoard = currentNode.chessBoard.viewMove(move);
				next.add(new Node(move, currentNode, newChessBoard));
			}

			// cutDown nodes
			if (availableMoves.size() > OVERDUE_FACTOR) {
				addValues(currentNode);
				next.sort(getComparator(currentTurn));
				
				// remove last node (worst one) until reach Branching Factor
				while (next.size() > BRANCHING_FACTOR) {
					next.remove(next.size() - 1);
				} //end of removing nodes
			} //end of cutting down nodes

			//Recusive calls on subnodes and get thier values filled in
			for (int i = 0; i < next.size(); i++) {
				addValues(next.get(i), depth);
			}

			//sort values for this node and set it to the highest value
			next.sort(getComparator(currentTurn));
			currentNode.value = next.get(0).value;
		}//end of addValues method

		/**
		 * Evaluates the value for a given node and places the result in node.value
		 * 
		 * @param n - (Node) node to evaluate
		 */
		private void evaluateNode(Node n) {
			Turn turn = n.chessBoard.getTurn();

			// sort out all non-possible opponent Available Moves. check must be
			// false or it will be an illegal Move (which it is not)
			ArrayList<Move> opponentAvailableMoves = Piece.filterAvailableMoves(n.chessBoard,
					n.chessBoard.getOpponentAvailableMoves(), n.chessBoard.getAvailableMoves(), false,
					ChessBoard.getOppositeTurn(turn)); 
			
			//gives in variables depending on the currentTurn
			if (turn == aiTurn) {
				n.value = ChessBoardEvaluator.evaluate(aiTurn, n.chessBoard, n.chessBoard.getAvailableMoves(),
						opponentAvailableMoves, n.move, turn);
			} else {
				n.value = ChessBoardEvaluator.evaluate(aiTurn, n.chessBoard, opponentAvailableMoves,
						n.chessBoard.getAvailableMoves(), n.move, turn);

			}
		}//end of evaluate Nodes function

		/**
		 * Returns the comparator to use based on the currentTurn
		 * @return Comparator<Node> - a comparator to compare nodes
		 */
		private Comparator<Node> getComparator(Turn movedTurn) {
			// note: the comparators are reversed because we are evaluating the
			// affects after the ai has moved
			return movedTurn == aiTurn ? other : thisAI;
		}

		/**
		 * Chooses a move based on AI levels
		 * @return Move - (Move) a move to play
		 */
		private Move getMoveHelper() {
			addValues();		
			ArrayList<Node> nodes = currentNode.nextNodes;
			
			//sometimes be stupid if aiLevel is 1 (choose the worst move)
			if (Math.random() * 1.5 > aiLevel){
				return currentNode.nextNodes.get(currentNode.nextNodes.size() - 1).move;
			}
			
			// holds the values ratios of every block
			ArrayList<Double> values = new ArrayList<>(nodes.size());
			ArrayList<Double> ratios = new ArrayList<>(nodes.size());
		
			// find total of all nodes, and multiply them by a factor to increase
			// the gap between some of them, since they are usually VERY close
			double total = 0;
			for (int i = 0; i < nodes.size(); i++) {
				double factor = (nodes.get(i).value - nodes.get(nodes.size() - 1).value) * (Math.pow(aiLevel, 10)) + 1;
				values.add(nodes.get(i).value * factor);
				total += values.get(i);
			}
		
			// to get the range (ratios that match this move)
			// so math.random can properly choose a move
			ratios.add(values.get(0) / total);
		
			for (int i = 1; i < values.size(); i++) {
				// get ratio plus original ratio
				ratios.add(values.get(i) / total + ratios.get(i - 1));
			}
		
			/*
			 * find which move this move will be. Increase value (sorted based on
			 * best to worst move) if level is too low
			 */
			double num = Math.random();
			
			// find where this move belongs
			if (inRange(0, ratios.get(0), num)) {
				return nodes.get(0).move;
			}
		
			for (int i = 1; i < ratios.size(); i++) {
				if (inRange(ratios.get(i - 1), ratios.get(i), num)) {
					return nodes.get(i).move;
				}
			}
			return null;
		}

		/**
		 * Checks if the num is >= low and < high.
		 * 
		 * @param low - (double) the lowest possible value num can be (inclusive)
		 * @param high - (double) the highest possible value num can be (exclusive)
		 * @param num - (double) the num to compare
		 * @return boolean - true if the num, low <= num < high
		 */
		private boolean inRange(double low, double high, double num) {
			return num >= low && num < high;
		}

		/**
		 * Changes an ArrayList of Nodes to moves
		 * 
		 * @param nodes - ArrayList (nodes) nodes to convert to moves
		 * @return ArrayList (moves) - the nodes moves put into this arrayList
		 */
		private ArrayList<Move> toMoves(ArrayList<Node> nodes) {
			ArrayList<Move> moves = new ArrayList<>(nodes.size());
			for (Node c : nodes) {
				moves.add(c.move);
			}
			return moves;
		}
	}//end of MovesTree class
	
	/**
	 * A Node of the class. A node is created for each new move available, and it stores 
	 * the following information about that move: <br>
	 * Its Value <br>
	 * Its chessBoard (position) <br>
	 * <br>
	 * And just like any other node would store, it stores the previous Nodes and the next Nodes.
	 * 
	 * @author frankliu197
	 */
	private static class Node {
		/** The Previous Node of this Node */
		private Node previousNode;
		
		/** The move this Node contains */
		private Move move;
		
		/**The ChessBoard this node contains */
		private ChessBoard chessBoard;
		
		/**The value of the Node */
		private double value;
		
		/**An arrayList of next Nodes*/
		private ArrayList<Node> nextNodes = new ArrayList<>();

		/**
		 * Constructor Method: Creates a Node with the following Information given:
		 * 
		 * @param move - (move) move to take to reach the position as given in the chessBoard param
		 * @param previousNode - (Node) the previous Node in this Tree
		 * @param chessBoard - (ChessBoard) the board state of this node
		 */
		private Node(Move move, Node previousNode, ChessBoard chessBoard) {
			this.move = move;
			this.previousNode = previousNode;
			this.chessBoard = chessBoard;
		}

		/**
		 * Searches though the nextNodes ArrayList to see if any of the nodes in the 
		 * nextNode arrayList contains the given move.
		 * 
		 * @param move - (Move) the move to search for
		 * @return Node - the Node that contains "move". Returns null if none exists.
		 */
		private Node searchNode(Move move) {
			for (int i = 0; i < nextNodes.size(); i++) {
				if (nextNodes.get(i).move.equals(move)) {
					return nextNodes.get(i);
				}
			}
			return null;
		}

		/**
		 * toString method: <br>
		 * Displays the following: move.toString + value
		 */
		@Override
		public String toString() {
			return move + " " + value;
		}
	}//end of Node class
	
	
	/**
	 * A class filled with only static methods and Variables that are used for calculating the
	 * value of a certain board.
	 * @author frankliu197
	 * @Since November 17
	 */
	private static class ChessBoardEvaluator {
		/**The WHITE_ROOK_STARTING_POSITIONS. use index 1 for left rook and index 2 for right */
		private static final int[][] WHITE_ROOK_STARTING_POSITIONS = new int[][]{{0, 0}, {7, 0}};
		
		/**The BLACK_ROOK_STARTING_POSITIONS. use index 1 for left rook and index 2 for right */
		private static final int[][] BLACK_ROOK_STARTING_POSITIONS = new int[][]{{0, 7}, {7, 7}};
		
		/**The WHITE_KING_STARTING_POSITION*/
		private static final int[] WHITE_KING_STARTING_POSITION = new int[]{4, 0};
		
		/**The BLACK_KING_STARTING_POSITION*/
		private static final int[] BLACK_KING_STARTING_POSITION = new int[]{4, 7};
		
		
		/* EVAULATE VALUES */
		/**The base values of all the Pieces. Use BASE_VALUES[pieceType.getOrdinal()]*/
		static final double[] BASE_VALUES = { 1, 3, 3, 5, 9, 0};
		
		/**The Value to multiply to a piece if it is being attacked */
		static final double ATTACKED_VALUE = 0.94;
		
		/**The Position of the Pawn to the End, where PAWN_TO_END[0] means the pawn is at the end */
		static final double[] PAWN_TO_END = { 6.5, 3, 1.5, 1.1, 1.02, 1.002, 1, 0};
		
		/**The number to multiply to the pawn behind the other pawn if there is a double Pawn*/
		static final double DOUBLE_PAWN = 0.8;
		
		/**The number to multiply to the pawn behind the other pawn if there is PASSED pawn*/
		static final double PASSED_PAWN = 2;
		
		/**The value to add for every move available*/
		static final double MOVES_AVAILABLE = 0.01;
		
		/**The value to multiply if it can be taken right when the mover ends his/her turn*/
		static final double CAN_BE_TAKEN_NEXT_TURN = 0.2;
		
		/**add CAN_CASTLE[NUMBER_OF_CASTLES] to board value where NUMBER_OF_CASTLES is the number of ways the player can castle*/
		static final double[] CAN_CASTLE = { 0, 0.15, 0.2 };
		
		/**GAME_VALUES[0] = WIN value, GAME_VALUES[1] = TIE, GAME_VALUES[2] = LOSE*/
		static final double[] GAME_VALUES = { 100, 50, 0 };
		
		/**
		 * Returns an accurate value of the current board state. <br><br>
		 * The values are in the perspective of the AI, according to AI Turn: <br>
		 * <b>value = 0</b> - the ai has lost <br>
		 * <b>0 < value < 30 </b> - the ai is losing badly <br>
		 * <b>30 <= value < 50 </b> - the ai is losing <br>
		 * <b> value = 50 </b> - the game is around tied <br>
		 * <b>50 < value <= 70 </b> - the ai is winning by a bit <br>
		 * <b>70 < value < 100 </b> - the  ai is winning by a lot <br>
		 * <b>value = 100</b> - the ai has won <br>
		 * 
		 * @param aiTurn - (turn) turn of the AI
		 * @param chessBoard - (ChessBoard) the chessBoard to do all calculations on
		 * @param availableMoves - (ArrayList) filtered ai moves
		 * @param opponentAvailableMoves - (ArrayList) filtered opponent moves
		 * @param currentTurn - (Turn) currentTurn of the board
		 * @return double - the value of the board
		 */
		static double evaluate(Turn aiTurn, ChessBoard chessBoard, ArrayList<Move> availableMoves,
				ArrayList<Move> opponentAvailableMoves, Move move, Turn turn) {
			
			/* First check if anyone won and 
			 * set those values accordingly
			 */
			String winMethod = chessBoard.checkWin();
			if (winMethod != ChessBoard.CURRENTLY_PLAYING){
				if (aiTurn.winType().equals(winMethod)){
					return GAME_VALUES[0];
				} else if (ChessBoard.getOppositeTurn(aiTurn).winType().equals(winMethod)){
					return GAME_VALUES[2];
					
				} else {
					return GAME_VALUES[1];
				}
			}
			
			/* Get the values of every single piece on
			 * the board
			 */
			double aiTotal = 0;
			double opponentTotal = 0;
			
			for (int x = 0; x < ChessBoard.LENGTH; x++) {
				for (int y = 0; y < ChessBoard.LENGTH; y++) {
					if (chessBoard.containsFriendlyPieceAt(x, y, aiTurn)) {
						aiTotal += valueOf(x, y, chessBoard, availableMoves, opponentAvailableMoves, turn);
					} else if (chessBoard.containsEnemyPieceAt(x, y, aiTurn)) {
						// opponent and available moves swapped
						opponentTotal += valueOf(x, y, chessBoard, opponentAvailableMoves, availableMoves, turn);
					}
				}
			}
			
			/* Check if any castles capable to be done
			 * and add them to the total points if required
			 */
			int aiCastles;
			int opponentCastles; 
			if (aiTurn == Turn.WHITE){
				aiCastles = numberOfAvailableCastles(chessBoard, WHITE_ROOK_STARTING_POSITIONS, WHITE_KING_STARTING_POSITION);
				opponentCastles = numberOfAvailableCastles(chessBoard, BLACK_ROOK_STARTING_POSITIONS, BLACK_KING_STARTING_POSITION);
			} else {
				aiCastles = numberOfAvailableCastles(chessBoard, BLACK_ROOK_STARTING_POSITIONS, BLACK_KING_STARTING_POSITION);
				opponentCastles = numberOfAvailableCastles(chessBoard, WHITE_ROOK_STARTING_POSITIONS, WHITE_KING_STARTING_POSITION);
			}
			aiTotal += CAN_CASTLE[aiCastles];
			opponentTotal += CAN_CASTLE[opponentCastles];
			
			/* Make the aiTotal points a percentage
			 * of the total points available
			 */
			return (aiTotal / (aiTotal + opponentTotal)) * GAME_VALUES[0];
		}

		/**
		 * Evaluates the value of one piece as given. It only evaluates using the final variables:
		 * BASE_VALUES<Br>
		 * CAN_BE_TAKEN_NEXT_TURN <br>
		 * MOVES_AVAILABLE <br>
		 * ATTACKED_VALUE <br>
		 * And all pawn values: DOUBLE_PAWN, PASSED_PAWN, PAWN_TO_END
		 * 
		 * 
		 * @param x - (int) x coordinates of the piece
		 * @param y - (int) y coordinates of the piece
		 * @param chessBoard - (ChessBoard) the chessBoard this piece exists on
		 * @param availableMoves - (ArrayList of Moves) the availableMoves for 
		 * 							pieces that share the same turn as this one
		 * @param opponentAvailableMoves - (ArrayList of Moves) the availableMoves
		 * 							for pieces that have the opposite turn as this
		 * 							piece.
		 * @param turn - (Turn) the currentTurn
		 * @return double - the Value of this Piece
		 * @throws IOException
		 */
		private static double valueOf(int x, int y, ChessBoard chessBoard, ArrayList<Move> availableMoves,
				ArrayList<Move> opponentAvailableMoves, Turn turn) {
			// filter out availableMoves and opponentAvailableMoves
			int possibleMoves = chessBoard.getPieceAt(x, y).filterAvailableMoves(availableMoves, new int[] { x, y }).size();
			int waysToTake = 0;
			
			for (Move move : opponentAvailableMoves) {
				if (move.hasEndPosition(x, y)) {
					waysToTake++;
				}
			}
			double baseValue = BASE_VALUES[chessBoard.getPieceAt(x, y).getPieceType().ordinal()];
			double valueOfPiece = possibleMoves * MOVES_AVAILABLE + baseValue * Math.pow(ATTACKED_VALUE, waysToTake);
			
			if (!chessBoard.containsPieceAt(x, y, turn) &&  waysToTake > 0){
				valueOfPiece *= CAN_BE_TAKEN_NEXT_TURN;
			}
			
			//Pawn only calculations
			if (chessBoard.containsPieceAt(x, y, PieceType.PAWN)) {
				//Variables
				Turn turnOfPiece = chessBoard.getPieceAt(x, y).getTurn();
				int directionOfTravel = turnOfPiece == Turn.WHITE? 1: -1;
				Turn nextPawn = containsPawn(x, y + directionOfTravel, turnOfPiece, chessBoard);
				Turn sidePawn1 = containsPawn(x + 1, y + directionOfTravel, turnOfPiece, chessBoard);
				Turn sidePawn2 = containsPawn(x - 1, y + directionOfTravel, turnOfPiece, chessBoard);	
				
				//pawn
				if (chessBoard.containsPieceAt(x, y, Turn.WHITE)){
					//get the next Pawn in the this row and in the sides. For side pawns, only check for pawns ahead					
					valueOfPiece *= PAWN_TO_END[7 - y];
				} else {
					valueOfPiece *= PAWN_TO_END[y];
				} 
				
				//check of DOUBLE_PAWN
				if (nextPawn == turn){
					valueOfPiece *= DOUBLE_PAWN;
				}
				
				//check for PASSED_PAWN
				if (nextPawn == Turn.NONE && sidePawn1 == Turn.NONE && sidePawn2 == Turn.NONE){
					valueOfPiece *= PASSED_PAWN;
				}
			}
			
			return valueOfPiece;
		}//end of valueOfMethod
		
		

		/** Returns the position of the first pawn in front of the position given. <br>
		 * Accepts Faulty x and y coordinates, in which it will just return Turn.NONE.
		 * 
		 * @param x - (int) x position
		 * @param y - (int) y position
		 * @param turn - (turn) turn of the pawn given in
		 * @param chessBoard - (ChessBoard) the chessBoard to do all
		 * 					all the calculations on
		 * @return Turn - the turn of the pawn if there is
		 * 					any pawns in front of the position given.
		 * 					If there are no pawns, this will return Turn.NONE
		 */
		private static Turn containsPawn(int x, int y, Turn turn, ChessBoard chessBoard){
			//Omit faulty result
			if (!inBounds(x, y)){
				return Turn.NONE;
			}
			
			if (turn == Turn.WHITE){
				//move forwards
				for (int i = y; i < ChessBoard.LENGTH; i++){
					if (chessBoard.containsPieceAt(x, i, PieceType.PAWN)){
						return chessBoard.getPieceAt(x, i).getTurn();
					}
				}
			} else {
				
				//move backwards
				for (int i = y; i > 0; i--){
					if (chessBoard.containsPieceAt(x, i, PieceType.PAWN)){
						return chessBoard.getPieceAt(x, i).getTurn();
					}
				}
			}
			return Turn.NONE;
		}//end of containsPawn method
		
		/**
		 * This method returns the number of ways a player can castles. Both the positions given
		 * in should be for the same Turn.
		 * @param rookPosition - (int[][]) the positions of the two rooks as written in one of the 
		 * 						static variables 
		 * @param kingPosition - (int[]) the positions of the king as written in one of the static variables
		 * @param chessBoard - (chessboard) the chessBoard position that this method is going to use
		 * @return int - number of ways the person with this turn can castle
		 */
		private static int numberOfAvailableCastles(ChessBoard chessBoard, int[][] rookPosition, int[] kingPosition){
			
			if (!chessBoard.hasNotMoved(kingPosition)){
				return 0;
			} 
			
			int numberOfCastles = 0;
			if (chessBoard.hasNotMoved(rookPosition[0])){
				numberOfCastles++;
			} 
			if (chessBoard.hasNotMoved(rookPosition[1])){
				numberOfCastles++;
			}
			return numberOfCastles;
		}//end of numberOfAvailableCastles method
	}//end of ChessBoardEvaulator class
}//end of AI class

