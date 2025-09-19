package part1;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * The MessageStorage class manages storing and retrieving messages.
 * - Keeps all messages in memory (in a List).
 * - Saves messages to a JSON file so they persist between runs.
 * - Loads messages back into memory when the app starts.
 */
public class MessageStorage {
    private static final String STORAGE_FILE = "messages.json"; // File where messages are stored
    private List<Message> messages; // List of all messages currently in memory

    /**
     * Constructor: starts with an empty list and tries to load existing messages from disk.
     */
    public MessageStorage() {
        this.messages = new ArrayList<>();
        loadMessages(); // Load messages from JSON file if available
    }

    /**
     * Adds a new message into the in-memory list.
     * (Does not save to disk until saveMessages() is called).
     */
    public void addMessage(Message message) {
        messages.add(message);
    }

    /**
     * Returns all messages currently stored in memory.
     */
    public List<Message> getMessages() {
        return messages;
    }

    /**
     * Saves all current messages to the JSON file.
     * Converts each Message into a JSON object and writes them into an array.
     *
     * @return true if saved successfully, false otherwise
     */
    @SuppressWarnings("unchecked")
    public boolean saveMessages() {
        try {
            JSONArray jsonMessages = new JSONArray();

            // Convert each message into a JSON object
            for (Message message : messages) {
                JSONObject jsonMessage = new JSONObject();
                jsonMessage.put("messageId", message.getMessageId());
                jsonMessage.put("messageNumber", message.getMessageNumber());
                jsonMessage.put("recipient", message.getRecipient());
                jsonMessage.put("content", message.getContent());
                jsonMessage.put("contentHash", message.getContentHash());
                jsonMessage.put("status", message.getStatus());

                jsonMessages.add(jsonMessage);
            }

            // Write all messages to the file
            try (FileWriter file = new FileWriter(STORAGE_FILE)) {
                file.write(jsonMessages.toJSONString());
                file.flush();
                return true;
            }
        } catch (IOException e) {
            System.out.println("Error saving messages: " + e.getMessage());
            return false;
        }
    }

    /**
     * Loads messages from the JSON file into memory.
     * If the file doesn’t exist yet, starts with an empty list.
     *
     * @return true if loaded successfully, false otherwise
     */
    public boolean loadMessages() {
        try {
            JSONParser jsonParser = new JSONParser();

            // Open and parse the JSON file
            try (FileReader reader = new FileReader(STORAGE_FILE)) {
                Object obj = jsonParser.parse(reader);
                JSONArray jsonMessages = (JSONArray) obj;

                // Clear any existing messages before reloading
                messages.clear();

                // Recreate each Message from its JSON data
                for (Object jsonMessageObj : jsonMessages) {
                    JSONObject messageObj = (JSONObject) jsonMessageObj;

                    int messageNumber = ((Long) messageObj.get("messageNumber")).intValue();
                    String recipient = (String) messageObj.get("recipient");
                    String content = (String) messageObj.get("content");
                    String status = (String) messageObj.get("status");

                    // Create a new Message object
                    Message message = new Message(messageNumber, recipient, content);

                    // Restore the saved status
                    if (status != null) {
                        message.setStatus(status);
                    }

                    messages.add(message);
                }
                return true;
            }
        } catch (IOException | ParseException e) {
            // If file not found or unreadable, just start fresh (this happens on first run)
            System.out.println("Could not load messages (this is normal on first run): " + e.getMessage());
            return false;
        }
    }

    /**
     * Determines what the next message number should be.
     * If no messages exist, starts at 1. Otherwise, returns the highest number + 1.
     */
    public int getNextMessageNumber() {
        if (messages.isEmpty()) {
            return 1;
        } else {
            int maxNumber = 0;
            for (Message message : messages) {
                if (message.getMessageNumber() > maxNumber) {
                    maxNumber = message.getMessageNumber();
                }
            }
            return maxNumber + 1;
        }
    }

    /**
     * Finds all messages with a specific status (e.g., "Sent", "Stored").
     */
    public List<Message> getMessagesByStatus(String status) {
        List<Message> filteredMessages = new ArrayList<>();
        for (Message message : messages) {
            if (status.equals(message.getStatus())) {
                filteredMessages.add(message);
            }
        }
        return filteredMessages;
    }

    /**
     * Finds all messages for a specific recipient (case-insensitive).
     */
    public List<Message> getMessagesByRecipient(String recipient) {
        List<Message> filteredMessages = new ArrayList<>();
        for (Message message : messages) {
            if (message.getRecipient().toLowerCase().contains(recipient.toLowerCase())) {
                filteredMessages.add(message);
            }
        }
        return filteredMessages;
    }
}
