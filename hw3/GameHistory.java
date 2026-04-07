package ce326.hw3;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

public class GameHistory {
    private ArrayList<GameHistoryEntry> entries;

    // Constructor method
    public GameHistory() {
        entries = new ArrayList<>();
    }

    // Method to add entry to the arraylist of games
    public void addEntry(GameHistoryEntry entry) {
        entries.add(0, entry);
    }
    
    // Method to get the entries of Game History
    public ArrayList<GameHistoryEntry> getEntries() {
    	return entries;
    }

    // Method to save the Game History to the file "game_history.json"
    public void saveGameHistory() {
    	JSONArray gameHistoryArray = new JSONArray();
        
    	// Save every game of the history in json format into an array
        for (GameHistoryEntry entry : entries) {
            JSONObject entryJson = new JSONObject();
            entryJson.put("Date/Time", entry.getDateTime());
            entryJson.put("Level", entry.getLevel());
            entryJson.put("Winner", entry.getWinner());
            entryJson.put("GameLogger", entry.getGameMoves());
            gameHistoryArray.put(entryJson);
        }

        // Transfer the updated game history into the file
        try {
            FileWriter writer = new FileWriter("game_history.json");
            writer.write("[\n");
            for(int i = 0; i < gameHistoryArray.length(); i++) {
            	writer.write(gameHistoryArray.getJSONObject(i).toString());
            	if(i != gameHistoryArray.length()-1)
            		writer.write(",\n");
            }
            writer.write("\n]");
            writer.close();
        } catch (IOException e) {
            System.err.println("Error saving game history to file: " + e.getMessage());
        }
    }
}

