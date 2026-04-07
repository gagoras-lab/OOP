package ce326.hw3;

import javax.swing.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Connect4GUI extends JFrame implements ActionListener,KeyListener{
	static final long serialVersionUID = 1L;
	
	static final int ROWS = 6; // Rows of the board
    static final int COLS = 7; // Columns of the board
    static final int PLAYER_USER = 1; // User
    static final int PLAYER_AI = 2; // AI

    private JPanel gamePanel; // Game panel that contains the board
    private JPanel controlPanel; // Control panel that contains the buttons at the top of the frame
    private Connect4Button[][] boardButtons; // The buttons of the board in the GUI
    private JButton newGameButton; // the new game button
    private JButton playerButton; // 1st player button
    private JButton historyButton; // History button
    private JRadioButton playerFirstButton; // First component of the list that decides who plays first ("You")
    private JRadioButton playerSecondButton; // Second component of the list that decides who plays first ("AI")

    private int[][] board; // The board that contains the value of each tile
    private Integer currentPlayer; // Current player making a move
    private boolean gameOver; // Set to true only when a game ends
    private Connect4AI ai;
    private GameHistory gameHistory;
    private GameLogger GameLogger;
    
    private int maxDepth = 3; // Default difficulty set to Medium (depth = 3)
    
    // Main constructor method
    public Connect4GUI() {
    	// Frame parameters
        this.setTitle("Connect 4");
        this.setSize(700, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setLayout(new BorderLayout());

        // Game panel parameters
        gamePanel = new JPanel(new GridLayout(ROWS, COLS));
        gamePanel.setBackground(Color.BLUE);
        
        boardButtons = new Connect4Button[ROWS][COLS];
        board = new int[ROWS][COLS];
        gameHistory = new GameHistory();
        GameLogger = new GameLogger();
        ai = new Connect4AI(board);
        
        currentPlayer = PLAYER_USER;
        gameOver = false;
        
        // Board buttons initialization
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
            	boardButtons[i][j] = new Connect4Button(i,j);
                boardButtons[i][j].addActionListener(this);
                gamePanel.add(boardButtons[i][j]);
            }
        }
        updateBoardGUI();
        
        add(gamePanel, BorderLayout.CENTER);
        
        // Create the control panel with buttons for starting a new game, selecting the starting player
        // and getting access to the game history
        controlPanel = new JPanel();
        
        // Create the button for starting a new game with a new difficulty level
        newGameButton = new JButton("New Game");
        newGameButton.addActionListener(new NewGameActionListener());
        controlPanel.add(newGameButton);
        
        // Create the radio buttons for selecting the starting player
        playerButton = new JButton("1st Player");
        playerButton.addActionListener(new PlayerActionListener());
        controlPanel.add(playerButton);
        
        // Create history overview
        historyButton = new JButton("History");
        historyButton.addActionListener(new HistoryActionListener());
        controlPanel.add(historyButton);
        
        add(controlPanel, BorderLayout.NORTH);
        
        this.setVisible(true);
        
        // Enable the Connect4GUI to receive key events
        this.setFocusable(true);
        
        // KeyListener of the Connect4GUI class for keyboard input
        addKeyListener(new KeyAdapter() {
        	@Override
            public void keyPressed(KeyEvent e) {
        		int keyCode = e.getKeyCode();
        		if (keyCode >= KeyEvent.VK_0 && keyCode <= KeyEvent.VK_6) {
                    int selectedColumn = keyCode - KeyEvent.VK_0;
                    makeMove(selectedColumn);
                }
            }
        });
    }
    
    
    // Constructor method to display a given game from the history
    public Connect4GUI(GameLogger GAME) throws InterruptedException {
    	// Frame parameters
    	this.setTitle("Connect 4");
    	this.setSize(700, 600);
    	this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    	this.setResizable(false);
    	this.setLayout(new BorderLayout());

    	// Game panel parameters
        gamePanel = new JPanel(new GridLayout(ROWS, COLS));
        gamePanel.setBackground(Color.BLUE);
        
        boardButtons = new Connect4Button[ROWS][COLS];
        board = new int[ROWS][COLS];
        
        // Board buttons initialization
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
            	boardButtons[i][j] = new Connect4Button(i,j);
                gamePanel.add(boardButtons[i][j]);
            }
        }
        updateBoardGUI();
        
        add(gamePanel, BorderLayout.CENTER);
        
        this.setVisible(true);
        updatePreview(GAME);
    }
    
    private void updatePreview(GameLogger GAME) throws InterruptedException {
    	// For-loop to show every move of the given game
        // Each turn has a delay of three seconds
        for(int i = 0; i < GAME.size(); i++) {
        	//Thread.sleep(3000);
        	int col = GAME.getElement(i).getCol();
        	int row = GAME.getElement(i).getRow();
        	int currPlayer = GAME.getElement(i).getPlayer();
        	dropPiece(row,col,currPlayer);
        }
    }
    
    // Make move in the given column from the keyboard,
    // used by KeyListener
    private void makeMove(int col) {
        if (gameOver) {
            return;
        }
        
        // USER'S TURN //
        int row = getAvailableRow(col);
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Column is full. Please select another column.");
            return;
        }
        dropPiece(row, col, currentPlayer);
        GameLogger.addMove(currentPlayer, row, col);
        updateBoardGUI();
        
        int result = checkWin();
        if (result == 1) {
        	gameOver = true;
        	JOptionPane.showMessageDialog(this, "You won!");
        	
        	handleEndOfGame();
        	GameLogger = new GameLogger();
            return;
        }
        
        // AI'S TURN //
        currentPlayer = currentPlayer == PLAYER_USER ? PLAYER_AI : PLAYER_USER;
        
        int aiCol = ai.getMove(board, PLAYER_AI, maxDepth);
        int aiRow = getAvailableRow(aiCol);
        dropPiece(aiRow, aiCol, PLAYER_AI);
        GameLogger.addMove(currentPlayer, aiRow, aiCol);
        
        updateBoardGUI();
        
        result = checkWin();
        if (result != 0) {
            gameOver = true;
            
            if (result == 2) {
                JOptionPane.showMessageDialog(this, "You lost!");
                handleEndOfGame();
            } else if (result == -1) {
                JOptionPane.showMessageDialog(this, "Tie game!");
            }
            
            GameLogger = new GameLogger();
            return;
        }

        currentPlayer = PLAYER_USER;
    }

    // Actions performed both by USER and AI
    public void actionPerformed(ActionEvent e) {
        if (gameOver) {
            return;
        }
        
        // USER'S TURN //
        JButton clickedButton = (JButton) e.getSource(); // Clicked button
        int row = -1, col = -1;

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (boardButtons[i][j] == clickedButton) {
                    row = getAvailableRow(j);
                    col = j;
                    break;
                }
            }
        }
        
        // Current move not available
        if (row == -1 || col == -1 || board[row][col] != 0) {
            return;
        }
        
        // Make current move
        dropPiece(row, col, currentPlayer);
        GameLogger.addMove(currentPlayer, row, col);
        int result = checkWin();

        // Game doesn't continue, final results
        if (result != 0) {
            gameOver = true;
            
            if (result == 1) {
            	JOptionPane.showMessageDialog(this, "You won!");
            	handleEndOfGame();
            } else if (result == -1) {
                JOptionPane.showMessageDialog(this, "Tie game!");
            }
            
            GameLogger = new GameLogger();
            return;
        }

        // AI'S MOVE //
        currentPlayer = currentPlayer == PLAYER_USER ? PLAYER_AI : PLAYER_USER;

        if (currentPlayer == PLAYER_AI) {
            int move = ai.getMove(board, PLAYER_AI, maxDepth);
            row = getAvailableRow(move);
            dropPiece(row, move, PLAYER_AI);
            GameLogger.addMove(currentPlayer, row, move);
            
            result = checkWin();
            
            // Game doesn't continue, final results
            if (result != 0) {
                gameOver = true;
                
                if (result == 2) {
                    JOptionPane.showMessageDialog(this, "You lost!");
                    handleEndOfGame();
                } else if (result == -1) {
                    JOptionPane.showMessageDialog(this, "Tie game!");
                }
                
                GameLogger = new GameLogger();
                return;
            }
            currentPlayer = PLAYER_USER;
        }
    }

    // Drop piece from current player, color depending on the player
    // RED = USER
    // YELLOW = AI
    private void dropPiece(int row, int col, int player) {
        board[row][col] = player;
        boardButtons[row][col].setBackground(player == PLAYER_USER ? Color.RED : Color.YELLOW);
    }
    
    // Find first available row in the column selected
    public int getAvailableRow(int col) {
        for(int row = ROWS - 1; row >= 0; row--) {
        	if (board[row][col] == 0) {
        		return row;
        	}
        }
        return -1;
    }

    // Check if the current player has won with the current move
    private int checkWin() {
        // Check for horizontal win
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col <= COLS - 4; col++) {
                int player = board[row][col];
                if (player != 0 && player == board[row][col+1] && player == board[row][col+2] && player == board[row][col+3]) {
                    return player;
                }
            }
        }

        // Check for vertical win
        for (int row = 0; row <= ROWS - 4; row++) {
            for (int col = 0; col < COLS; col++) {
                int player = board[row][col];
                if (player != 0 && player == board[row+1][col] && player == board[row+2][col] && player == board[row+3][col]) {
                    return player;
                }
            }
        }

        // Check for diagonal win (top-left to bottom-right)
        for (int row = 0; row <= ROWS - 4; row++) {
            for (int col = 0; col <= COLS - 4; col++) {
                int player = board[row][col];
                if (player != 0 && player == board[row+1][col+1] && player == board[row+2][col+2] && player == board[row+3][col+3]) {
                    return player;
                }
            }
        }

        // Check for diagonal win (bottom-left to top-right)
        for (int row = 3; row < ROWS; row++) {
            for (int col = 0; col <= COLS - 4; col++) {
                int player = board[row][col];
                if (player != 0 && player == board[row-1][col+1] && player == board[row-2][col+2] && player == board[row-3][col+3]) {
                    return player;
                }
            }
        }

        // Check for tie game
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (board[row][col] == 0) {
                    return 0;
                }
            }
        }
        return -1;
    }


    // Update the board's tiles' colors
    private void updateBoardGUI() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (board[i][j] == 0) {
                    boardButtons[i][j].setBackground(Color.WHITE);
                } else if (board[i][j] == PLAYER_USER) {
                	boardButtons[i][j].setBackground(Color.RED);
                } else if (board[i][j] == PLAYER_AI) {
                	boardButtons[i][j].setBackground(Color.YELLOW);
                }
            }
        }
    }
    
    // Method to add a new entry in the Game History
    private void handleEndOfGame() {
    	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy.MM.dd - HH:mm"); // Date and Time format
    	LocalDateTime now = LocalDateTime.now(); // Date and Time
    	String level = new String();
    	
    	// Save level of difficulty
        switch (maxDepth) {
            case 1:
            	level = "Trivial";
                break;
            case 3:
            	level = "Medium";
                break;
            case 5:
            	level = "Hard";
                break;
            default:
            	level = "Medium";
                break;
        }
        
        // Save winner of the game that just ended
        String winner = new String();
        switch (currentPlayer) {
        case 1:
        	winner = "P";
            break;
        case 2:
        	winner = "AI";
            break;
        default:
        	winner = "AI";
            break;
        }
    	GameHistoryEntry entry = new GameHistoryEntry(winner, level, dtf.format(now), GameLogger);
    	gameHistory.addEntry(entry);
    	gameHistory.saveGameHistory();
    }
    
    // Action listener for starting a new game
    private class NewGameActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	// Display a dialog box to allow the user to select the difficulty level
            String[] difficultyLevels = {"Trivial", "Medium", "Hard"};
            JComboBox<String> difficultyLevelBox = new JComboBox<>(difficultyLevels);
            JOptionPane.showMessageDialog(null, difficultyLevelBox, "Select Difficulty Level", JOptionPane.PLAIN_MESSAGE);
            
            // Get the selected difficulty level
            String selectedDifficulty = (String) difficultyLevelBox.getSelectedItem();
            switch (selectedDifficulty) {
                case "Trivial":
                    maxDepth = 1;
                    break;
                case "Medium":
                    maxDepth = 3;
                    break;
                case "Hard":
                    maxDepth = 5;
                    break;
                default:
                    maxDepth = 3;
                    break;
            }
            resetBoard();
        }
        
        // Reset the board to its initial condition
        private void resetBoard() {
            for (int i = 0; i < ROWS; i++) {
                for (int j = 0; j < COLS; j++) {
                    board[i][j] = 0;
                }
            }
            gameOver = false;
            updateBoardGUI();
            updateFirstPlayer();
        }
        
        // Update the player that starts first in the new game
        private void updateFirstPlayer() {
        	// Set the first player based on user selection
        	if(playerFirstButton != null) {
	            if (!playerFirstButton.isSelected()) {
	                // AI goes first
	            	int move = ai.getMove(board, PLAYER_AI, maxDepth);
	                dropPiece(getAvailableRow(move), move, PLAYER_AI);
	                currentPlayer = PLAYER_USER;
	            } else {
	                // User goes first
	                currentPlayer = PLAYER_USER;
	            }
	        }
        	else {
        		currentPlayer = PLAYER_USER;
        	}
        }
        
    }
    
    // Action listener for starting a new game
    private class PlayerActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	// Create a new window for selecting the starting player
            JFrame startingPlayerWindow = new JFrame("Select Starting Player");
            startingPlayerWindow.setLayout(new GridLayout(2, 1));
            
        	playerFirstButton = new JRadioButton("You", true);
            playerSecondButton = new JRadioButton("AI");
            ButtonGroup startingPlayerGroup = new ButtonGroup();
            startingPlayerGroup.add(playerFirstButton);
            startingPlayerGroup.add(playerSecondButton);
            playerButton.add(playerFirstButton);
            playerButton.add(playerSecondButton);
            
            // Add the radio buttons to the window
            JPanel startingPlayerPanel = new JPanel();
            startingPlayerPanel.setLayout(new GridLayout(2, 1));
            startingPlayerPanel.add(playerFirstButton);
            startingPlayerPanel.add(playerSecondButton);
            startingPlayerWindow.add(startingPlayerPanel);
            
            // Display the starting player window
            startingPlayerWindow.pack();
            startingPlayerWindow.setLocationRelativeTo(null);
            startingPlayerWindow.setVisible(true);
        }
    }
    
    // Action listener for game history
    private class HistoryActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	DefaultListModel<String> gameHistoryListModel = new DefaultListModel<>();
            JList<String> gameHistoryList = new JList<>(gameHistoryListModel);
        	
        	JScrollPane scrollPane = new JScrollPane(gameHistoryList);
            scrollPane.setPreferredSize(new Dimension(300, 200));
            gameHistoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	        add(scrollPane, BorderLayout.EAST);
	        
        	gameHistoryListModel.clear();
            ArrayList<GameHistoryEntry> entries = gameHistory.getEntries();

            // Add entries to the Jlist
            for (GameHistoryEntry entry : entries) {
                String winnerString = "W: " + entry.getWinner();
                String levelString = "L: " + entry.getLevel();
                String dateTimeString = entry.getDateTime();
                String gameElement = dateTimeString + '\t' + levelString + '\t' + winnerString;

                gameHistoryListModel.addElement(gameElement);
            }
            
            // Mouse listener, in case a game gets selected to be displayed
            gameHistoryList.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent evt) {
                    @SuppressWarnings("rawtypes")
					JList list = (JList)evt.getSource();
                    
                    // Game gets displayed only if it gets clicked twice by the left mouse button
                    if (evt.getClickCount() == 2 && (evt.getButton() == MouseEvent.BUTTON1)) {

                        // Double-click detected
                        int index = list.locationToIndex(evt.getPoint());
                        GameLogger game = GamesFromFile(index);
						try {
							setVisible(false);
							Connect4GUI GamePreview = new Connect4GUI(game);
							GamePreview.setVisible(true);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
                    }
                    
                }
            });

            JOptionPane.showMessageDialog(gameHistoryList, scrollPane, "Game History", JOptionPane.PLAIN_MESSAGE);
            setVisible(true);
        }
        
        // Read the games recorded in the game history, saved into the "game_history.json" file
        GameLogger GamesFromFile(int index) {
        	File reader = new File("game_history.json");
        	String content = null;
        	
			try {
				// Get all content of this file
				content = new String(Files.readAllBytes(Paths.get(reader.toURI())), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
        	JSONArray array = new JSONArray(content); //parse your string data to JSON Array
            Map<String, ArrayList<Object>> map = new HashMap<>(); //create a HashMap having a key type as String and values as List of Object, 
            //since you are creating array for each key
            for(int i=0; i<array.length(); i++) //looping on all JSON Objects
            {
                JSONObject obj = array.getJSONObject(i);
                for (String key : obj.keySet()) { //looping on all keys in each JSON Object
                    if(map.get(key) == null)
                        map.put(key, new ArrayList<Object>()); //initializing the list for the 1st use
                    map.get(key).add(obj.get(key));//adding the value to the list of the corresponding key
                }
            }
            
            // Get the game selected by the User
        	JSONArray GameMovesList = new JSONArray();
        	GameMovesList = (JSONArray) map.get("GameLogger").get(index);
        	
        	// Make the game into a game logger, in order to be displayed in the correct order
        	GameLogger GameMoves = new GameLogger();
        	for (int i = 0; i < GameMovesList.length(); ++i) {
        	    JSONObject rec = GameMovesList.getJSONObject(i);
        	    int col = rec.getInt("col");
        	    int row = rec.getInt("row");
        	    int player = rec.getInt("player");
        	    GameMoves.addMove(player, row, col);
        	}
        	
        	return GameMoves;
        }
    }

    // NOT USED, "MANDATORY TO EXIST" METHODS //
	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}
}