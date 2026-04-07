package ce326.hw3;

import java.util.ArrayList;
import java.util.List;

public class Connect4AI {

    private int[][] board;

    // Get board from Connect-4 GUI
    public Connect4AI(int[][] board) {
        this.board = board;
    }
    
    // AI makes a move, depending on the depth it can search in the Alpha-Beta Pruning algorithm
    public int getMove(int[][] board, int player, int maxDepth) {
    	int opponent = (player == Connect4GUI.PLAYER_AI) ? Connect4GUI.PLAYER_USER : Connect4GUI.PLAYER_AI;
        int move = OuterAlphaBeta(opponent, maxDepth, -Integer.MAX_VALUE, Integer.MAX_VALUE);
        return move;
	}
    
    // Alpha-Beta Pruning Algorithm which chooses the best column for the AI to make its move
    public int OuterAlphaBeta(int player, int depth, int alpha, int beta) {
        List<Integer> possibleMoves = getPossibleMoves();
        int bestMove = possibleMoves.get(0);
        int bestScore = -Integer.MAX_VALUE;
        int row = 0;

        // For-loop of the available column, each one instantiating in inner Alpha-Beta Pruning algorithm
        for (int move : possibleMoves) {
            row = getAvailableRow(move);
            int score = InnerAlphaBeta(player, depth - 1, alpha, beta, row, move);
            undoMove(row, move);

            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }
        
        return bestMove;
    }

    // Main Alpha-Beta Pruning Method
    private int InnerAlphaBeta(int player, int depth, int alpha, int beta, int row, int col) {
        dropPiece(row, col, player);
        
        int result = checkWinPossibility();
        if (depth == 0 || result != 0) {
        	// Reached end of the look-ahead methodology,
        	// evaluate the current position
            int score = evaluate(player);
            undoMove(row, col);
            return score;
        }

        int nextPlayer = (player == Connect4GUI.PLAYER_USER) ? Connect4GUI.PLAYER_AI : Connect4GUI.PLAYER_USER;

        // User's move, we want the minimum possible value
        if (player == Connect4GUI.PLAYER_USER) {
            int bestScore = Integer.MAX_VALUE;

            List<Integer> possibleMoves = getPossibleMoves();
            for (int move : possibleMoves) {
                int nextRow = getAvailableRow(move);
                int score = InnerAlphaBeta(nextPlayer, depth - 1, alpha, beta, nextRow, move);
                undoMove(nextRow, move);

                bestScore = Math.min(bestScore, score);

                beta = Math.min(beta, bestScore);
                if (beta <= alpha) {
                    break;
                }
            }

            return bestScore;
        }
        
        // AI's move, we want the maximum possible value
        else {
            int bestScore = -Integer.MAX_VALUE;

            List<Integer> possibleMoves = getPossibleMoves();
            for (int move : possibleMoves) {
                int nextRow = getAvailableRow(move);
                int score = InnerAlphaBeta(nextPlayer, depth - 1, alpha, beta, nextRow, move);
                undoMove(nextRow, move);

                bestScore = Math.max(bestScore, score);
                
                alpha = Math.max(alpha, bestScore);
                if (beta <= alpha) {
                    break;
                }
            }

            return bestScore;
        }
    }

    // List of available columns that AI can make a move
    private List<Integer> getPossibleMoves() {
        List<Integer> possibleMoves = new ArrayList<Integer>();
        for (int col = 0; col < Connect4GUI.COLS; col++) {
            if (board[0][col] == 0) {
                possibleMoves.add(col);
            }
        }
        return possibleMoves;
    }

    // Find available row in given column; if the column is full, return -1
    public int getAvailableRow(int col) {
        for(int row = Connect4GUI.ROWS - 1; row >= 0; row--) {
        	if (board[row][col] == 0) {
        		return row;
        	}
        }
        return -1;
    }
    
    // Drop piece TEMPORARILY to test the min-max algorithm
    private void dropPiece(int row, int col, int player) {
        board[row][col] = player;
    }
    
    // Reset the tile to its initial condition, after the use of dropPiece()
    private void undoMove(int row, int col) {
        board[row][col] = 0;
    }
    
    // Check if the current player has won with the current move
    private int checkWinPossibility() {
    	
        // Check horizontal
        for (int row = 0; row < Connect4GUI.ROWS; row++) {
            for (int col = 0; col < Connect4GUI.COLS - 3; col++) {
                int player = board[row][col];
                if (player != 0 && player == board[row][col+1] && player == board[row][col+2] && player == board[row][col+3]) {
                    return player;
                }
            }
        }

        // Check vertical
        for (int row = 0; row < Connect4GUI.ROWS - 3; row++) {
            for (int col = 0; col < Connect4GUI.COLS; col++) {
                int player = board[row][col];
                if (player != 0 && player == board[row+1][col] && player == board[row+2][col] && player == board[row+3][col]) {
                    return player;
                }
            }
        }

        // Check diagonal (top-left to bottom-right)
        for (int row = 0; row < Connect4GUI.ROWS - 3; row++) {
            for (int col = 0; col < Connect4GUI.COLS - 3; col++) {
                int player = board[row][col];
                if (player != 0 && player == board[row+1][col+1] && player == board[row+2][col+2] && player == board[row+3][col+3]) {
                    return player;
                }
            }
        }

        // Check diagonal (bottom-left to top-right)
        for (int row = 3; row < Connect4GUI.ROWS; row++) {
            for (int col = 0; col < Connect4GUI.COLS - 3; col++) {
                int player = board[row][col];
                if (player != 0 && player == board[row-1][col+1] && player == board[row-2][col+2] && player == board[row-3][col+3]) {
                    return player;
                }
            }
        }

        return 0;
    }
    
    ////////////////////////////////////
    //////// EVALUATION METHODS ////////
    ////////////////////////////////////
    
    private int evaluate(int player) {
        int score = 0;

        // Evaluate rows
        for (int row = 0; row < Connect4GUI.ROWS; row++) {
            for (int col = 0; col < Connect4GUI.COLS - 3; col++) {
                int[] window = new int[]{board[row][col], board[row][col+1], board[row][col+2], board[row][col+3]};
                score += evaluateWindow(window, player);
            }
        }

        // Evaluate columns
        for (int col = 0; col < Connect4GUI.COLS; col++) {
            for (int row = 0; row < Connect4GUI.ROWS - 3; row++) {
                int[] window = new int[]{board[row][col], board[row+1][col], board[row+2][col], board[row+3][col]};
                score += evaluateWindow(window, player);
            }
        }

        // Evaluate diagonal (top-left to bottom-right)
        for (int row = 0; row < Connect4GUI.ROWS - 3; row++) {
            for (int col = 0; col < Connect4GUI.COLS - 3; col++) {
                int[] window = new int[]{board[row][col], board[row+1][col+1], board[row+2][col+2], board[row+3][col+3]};
                score += evaluateWindow(window, player);
            }
        }

        // Evaluate diagonal (bottom-left to top-right)
        for (int row = 3; row < Connect4GUI.ROWS; row++) {
            for (int col = 0; col < Connect4GUI.COLS - 3; col++) {
                int[] window = new int[]{board[row][col], board[row-1][col+1], board[row-2][col+2], board[row-3][col+3]};
                score += evaluateWindow(window, player);
            }
        }

        return score;
    }

    private int evaluateWindow(int[] window, int player) {
        int score = 0;
        int oppPlayer = (player == Connect4GUI.PLAYER_USER) ? Connect4GUI.PLAYER_AI : Connect4GUI.PLAYER_USER;

        int numPlayer = 0;
        int numEmpty = 0;
        int numOppPlayer = 0;

        // Get number of player's tiles, AI's tiles and empty tiles in the given window
        for (int i = 0; i < window.length; i++) {
            if (window[i] == player) {
                numPlayer++;
            } else if (window[i] == 0) {
                numEmpty++;
            } else if (window[i] == oppPlayer) {
                numOppPlayer++;
            }
        }
        
        // Calculate the score of this possible move //
        
        // FIRST CASE, winning case
        if (numPlayer == 4) {
            score += 10000;
        
        // SECOND CASE, one tile that has been already selected and three empty
        } else if (numPlayer == 1 && numEmpty == 3) {
            score += 1;
        } else if (numOppPlayer == 1 && numEmpty == 3) {
            score -= 1;
        
        // THIRD CASE, two tiles that have been already selected by the same player and two empty
        } else if (numPlayer == 2 && numEmpty == 2) {
            score += 4;
        } else if (numOppPlayer == 2 && numEmpty == 2) {
            score -= 4;
            
        // FOURTH CASE, three tiles that have been already selected by the same player and one empty
    	} else if (numPlayer == 3 && numEmpty == 1) {
    		score += 16;
    	} else if (numOppPlayer == 3 && numEmpty == 1) {
    		score -= 16;
    	}
        
        // FIFTH CASE, if none of the other cases is true, the score stays 0

        return score;
    }
}

