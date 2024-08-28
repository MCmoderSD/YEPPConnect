package de.MCmoderSD.YEPPConnect.utilities.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

@SuppressWarnings("unused")
public class MySQL extends Driver {

    // Attributes
    private final HashMap<Integer, Timestamp> lastChange;

    // Constructor
    public MySQL(String host, int port, String database, String username, String password) {

        // Initialize Driver
        super(host, port, database, username, password);

        // Initialize Attributes
        lastChange = new HashMap<>();

        // Connect to database
        new Thread(this::connect).start();
    }

    // Check if Channel exists in table
    public boolean channelExists(int id) {
        try {
            if (!isConnected()) connect();
            PreparedStatement statement = connection.prepareStatement("SELECT 1 FROM MinecraftWhitelist WHERE channel_id = ?");
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            System.err.printf("Error while checking if channel exists: %s\n", e.getMessage());
        }
        return false;
    }

    // Check for Changes
    public boolean checkForChanges(int id) {
        if (!lastChange.containsKey(id)) return true;
        try {
            if (!isConnected()) connect();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM MinecraftWhitelist WHERE channel_id = ? AND MinecraftWhitelist.last_updated > ?");
            statement.setInt(1, id);
            statement.setTimestamp(2, lastChange.get(id));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) return true;
        } catch (SQLException e) {
            System.err.printf("Error while checking for changes: %s\n", e.getMessage());
        }
        return false;
    }

    // Get Whitelist
    public ArrayList<String> getWhitelist(int id) {
        try {
            if (!isConnected()) connect();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM MinecraftWhitelist WHERE channel_id = ?");
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                if (lastChange.containsKey(id)) lastChange.replace(id, resultSet.getTimestamp("last_updated"));
                else lastChange.put(id, resultSet.getTimestamp("last_updated"));
                return new ArrayList<>(Arrays.asList(resultSet.getString("whitelist").split(" ")));
            }
        } catch (SQLException e) {
            System.err.printf("Error while updating whitelist: %s\n", e.getMessage());
        }
        return null;
    }

    // Query ID
    public int queryID(String table, String name) {
        try {
            if (!isConnected()) connect();
            String query = null;
            if (table.equals("channels")) query = "SELECT id FROM " + "channels" + " WHERE name = ?";
            if (table.equals("users")) query = "SELECT id FROM " + "users" + " WHERE name = ?";
            if (query == null) return -1;
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) return resultSet.getInt("id");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return -1;
    }

    // Query Name
    public String queryName(String table, int id) {
        try {
            if (!isConnected()) connect();
            String query = null;
            if (table.equals("channels")) query = "SELECT name FROM " + "channels" + " WHERE id = ?";
            if (table.equals("users")) query = "SELECT name FROM " + "users" + " WHERE id = ?";
            if (query == null) return null;
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) return resultSet.getString("name");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }
}