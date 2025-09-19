package part1;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * The Message class represents a single chat message.
 * Each message has a unique ID, recipient, content, a SHA-256 hash of its content,
 * and a status (e.g., Created, Sent, Stored, or Discarded).
 */
public class Message {
    private String messageId;    // A randomly generated unique 10-digit ID
    private int messageNumber;   // A sequential number for ordering messages
    private String recipient;    // The person this message is sent to
    private String content;      // The message body (limited to 250 characters)
    private String contentHash;  // A hash of the message content for integrity checking
    private String status;       // Status of the message (Created, Sent, Stored, Discarded, etc.)

    /**
     * Constructor: called when creating a new message.
     * Generates a unique ID, ensures content length is valid,
     * and calculates a hash of the content.
     *
     * @param messageNumber sequential number assigned to the message
     * @param recipient     who the message is being sent to
     * @param content       the text of the message (max 250 chars)
     */
    public Message(int messageNumber, String recipient, String content) {
        this.messageId = generateMessageId();   // Generate a unique ID
        this.messageNumber = messageNumber;     // Assign sequential number
        this.recipient = recipient;             // Set recipient name
        this.status = "Created";                // Default status when first made

        // Limit the message length to 250 characters
        if (content.length() > 250) {
            this.content = content.substring(0, 250);
        } else {
            this.content = content;
        }

        // Create a SHA-256 hash of the message content for verification
        this.contentHash = calculateContentHash(this.content);
    }

    /**
     * Creates a random 10-digit message ID.
     * Example: "1234567890"
     *
     * @return unique 10-digit string
     */
    private String generateMessageId() {
        Random random = new Random();
        // Generate a random number in the 10-digit range
        long randomNum = 1000000000L + random.nextInt(900000000);
        return String.valueOf(randomNum);
    }

    /**
     * Generates a SHA-256 hash of the message content.
     * Used for verifying integrity (detecting tampering or corruption).
     *
     * @param content the message text to hash
     * @return hash as a hex string
     */
    private String calculateContentHash(String content) {
        try {
            // Use Java's built-in MessageDigest for SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(content.getBytes(StandardCharsets.UTF_8));

            // Convert the raw byte array into a readable hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0'); // pad single-digit hex values
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // This should never happen with SHA-256, but just in case
            return "HASH_ERROR";
        }
    }

    // ----------- Getters (read-only access to private fields) -----------

    /** @return the unique message ID */
    public String getMessageId() {
        return messageId;
    }

    /** @return the sequential message number */
    public int getMessageNumber() {
        return messageNumber;
    }

    /** @return the recipient of the message */
    public String getRecipient() {
        return recipient;
    }

    /** @return the text content of the message */
    public String getContent() {
        return content;
    }

    /** @return the SHA-256 hash of the message content */
    public String getContentHash() {
        return contentHash;
    }

    /** @return the current status of the message */
    public String getStatus() {
        return status;
    }

    // ----------- Setters (modify certain fields) -----------

    /**
     * Update the status of the message.
     * Example: "Sent", "Stored", or "Discarded".
     *
     * @param status new status string
     */
    public void setStatus(String status) {
        this.status = status;
    }

    // ----------- Utility Methods -----------

    /**
     * Creates a user-friendly string representation of the message.
     * Useful for displaying in dialogs or reports.
     *
     * @return formatted string with key message info
     */
    @Override
    public String toString() {
        return "Message #" + messageNumber +
               "\nID: " + messageId +
               "\nTo: " + recipient +
               "\nContent: " + content +
               "\nStatus: " + status +
               "\nContent Hash: " + contentHash.substring(0, 15) + "..."; // Show only first 15 chars of hash
    }
}
