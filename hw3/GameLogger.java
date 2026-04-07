package ce326.hw3;

import java.util.ArrayList;

public class GameLogger {
	
    private ArrayList<Move> moves;

    // Constructor method of Game Logger, initialization a new array-list
    public GameLogger() {
        this.moves = new ArrayList<>();
    }

    // Add a new entry/move to the Game Logger
    public void addMove(int player, int row, int col) {
        Move move = new Move(player, row, col);
        moves.add(move);
    }
    
    // Method to get the moves in the current Game Logger
    public ArrayList<Move> getMoves() {
    	return moves;
    }

    // Method to clear the array-list that holds the moves made in the current game
    // Locally, used whenever a new game starts
	public void clearMoves() {
		this.moves.clear();
	}
	
	// Get the size (number of moves made) in this Game Logger
	public int size() {
		return this.moves.size();
	}
	
	// Get a specific move of the Game Logger, in the given index of the array-list
	public Move getElement(int index) {
		return this.moves.get(index);
	}
	
	// Add an entry/move that has been initialized before
	public void add(Move newMove) {
		moves.add(newMove);
	}

	// Class "Move", used locally by the Game Logger to save each move made during the game
    public static class Move {
        private int player;
        private int row;
        private int col;

        // Constructor method of "Move"
        public Move(int player, int row, int col) {
            this.player = player;
            this.row = row;
            this.col = col;
        }

        // Method to get the player that made this move
        public int getPlayer() {
            return player;
        }

        // Method to get the row of this move
        public int getRow() {
            return row;
        }

        // Method to get the column of this move
        public int getCol() {
            return col;
        }
    }

}
