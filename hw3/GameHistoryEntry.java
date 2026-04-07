package ce326.hw3;

import org.json.JSONArray;
import org.json.JSONObject;
import ce326.hw3.GameLogger.Move;


public class GameHistoryEntry {
    private String winner;
    private String level;
    private String dateTime;
    private GameLogger GameLogger;

    // Constructor method
    public GameHistoryEntry(String winner, String level, String dateTime, GameLogger GameLogger) {
        this.winner = winner;
        this.level = level;
        this.dateTime = dateTime;
        this.GameLogger = GameLogger;
    }

    // Method to get the winner of this game
    public String getWinner() {
        return winner;
    }

    // Method to get the level of difficulty of this game
    public String getLevel() {
        return level;
    }

    // Method to get the date and time of this game
    public String getDateTime() {
        return dateTime;
    }

    // Method to get the moves made in this game
	public JSONArray getGameMoves() {
		JSONArray jsonMoves = new JSONArray();
        for (Move move : this.GameLogger.getMoves()) {
            JSONObject jsonMove = new JSONObject();
            jsonMove.put("player", move.getPlayer());
            jsonMove.put("row", move.getRow());
            jsonMove.put("col", move.getCol());
            jsonMoves.put(jsonMove);
        }
		return jsonMoves;
	}
}
