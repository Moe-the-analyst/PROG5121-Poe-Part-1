package part1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * ChatMenu provides the main interface for the QuickChat application.
 * It allows users to send messages, view reports on stored messages, or exit.
 */
public class ChatMenu extends JFrame {
    // Main menu buttons
    private JButton btnSendMessages;
    private JButton btnShowRecent;
    private JButton btnExit;

    // Welcome label
    private JLabel lblWelcome;

    // Handles storing, retrieving, and saving messages
    private MessageStorage messageStorage;

    /**
     * Constructor for the ChatMenu
     */
    public ChatMenu() {
        initComponents(); // Setup UI components
        messageStorage = new MessageStorage(); // Initialize message storage
    }

    /**
     * Initializes the user interface for the main menu
     */
    private void initComponents() {
        // Basic frame setup
        setTitle("QuickChat Menu");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center window on screen

        // Welcome label at the top
        lblWelcome = new JLabel("Welcome to QuickChat");
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 18));
        lblWelcome.setHorizontalAlignment(SwingConstants.CENTER);

        // Create buttons
        btnSendMessages = new JButton("Send Messages");
        btnShowRecent = new JButton("Show Recent Messages");
        btnExit = new JButton("Exit");

        // Use a BorderLayout for the frame
        setLayout(new BorderLayout());
        add(lblWelcome, BorderLayout.NORTH); // Add title at the top

        // Create a panel to hold buttons in a vertical grid
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        buttonPanel.add(btnSendMessages);
        buttonPanel.add(btnShowRecent);
        buttonPanel.add(btnExit);

        add(buttonPanel, BorderLayout.CENTER); // Add buttons in the middle

        // Add button functionality (event listeners)
        btnSendMessages.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSendMessages(); // Open message sending workflow
            }
        });

        btnShowRecent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showReportMenu(); // Open the reports menu
            }
        });

        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0); // Exit application
            }
        });
    }

    /**
     * Handles the "Send Messages" workflow
     */
    private void handleSendMessages() {
        // Ask user how many messages they want to send
        String input = JOptionPane.showInputDialog(this,
                "How many messages would you like to send?",
                "Number of Messages",
                JOptionPane.QUESTION_MESSAGE);

        // If user cancels the input, stop here
        if (input == null) {
            return;
        }

        int numMessages;
        try {
            numMessages = Integer.parseInt(input); // Convert input to number
            if (numMessages <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a positive number.",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            // If input was not a valid number
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid number.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Keep track of how many messages actually get sent or stored
        int messagesSent = 0;
        int currentMessageNumber = messageStorage.getNextMessageNumber();

        // Loop through however many messages the user wants to create
        for (int i = 0; i < numMessages; i++) {
            if (messagesSent >= numMessages) {
                JOptionPane.showMessageDialog(this,
                        "You've reached your message limit of " + numMessages,
                        "Limit Reached",
                        JOptionPane.INFORMATION_MESSAGE);
                break;
            }

            // Step 1: Ask for recipient
            String recipient = JOptionPane.showInputDialog(this,
                    "Message " + (i+1) + " of " + numMessages + "\nEnter recipient:",
                    "Message Recipient",
                    JOptionPane.QUESTION_MESSAGE);

            // Handle recipient being cancelled or empty
            if (recipient == null || recipient.trim().isEmpty()) {
                int choice = JOptionPane.showConfirmDialog(this,
                        "Skip this message?",
                        "Skip Message",
                        JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    continue; // Skip
                } else {
                    i--; // Retry same message
                    continue;
                }
            }

            // Step 2: Ask for message content
            String content = JOptionPane.showInputDialog(this,
                    "Message " + (i+1) + " of " + numMessages + "\nEnter message content (max 250 chars):",
                    "Message Content",
                    JOptionPane.QUESTION_MESSAGE);

            if (content == null) {
                int choice = JOptionPane.showConfirmDialog(this,
                        "Skip this message?",
                        "Skip Message",
                        JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    continue;
                } else {
                    i--; // Retry
                    continue;
                }
            }

            // Create a new Message object
            Message message = new Message(currentMessageNumber++, recipient, content);

            // Ask user what to do with the created message
            String[] options = {"Send", "Store", "Discard"};
            int choice = JOptionPane.showOptionDialog(this,
                    message.toString() + "\n\nWhat would you like to do with this message?",
                    "Message Created",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);

            // Handle user’s decision
            switch (choice) {
                case 0: // Send immediately
                    message.setStatus("Sent");
                    messageStorage.addMessage(message);
                    messageStorage.saveMessages();
                    JOptionPane.showMessageDialog(this,
                            "Message sent successfully!",
                            "Message Sent",
                            JOptionPane.INFORMATION_MESSAGE);
                    messagesSent++;
                    break;
                case 1: // Store for later
                    message.setStatus("Stored");
                    messageStorage.addMessage(message);
                    messageStorage.saveMessages();
                    JOptionPane.showMessageDialog(this,
                            "Message stored for later sending.",
                            "Message Stored",
                            JOptionPane.INFORMATION_MESSAGE);
                    messagesSent++;
                    break;
                case 2: // Discard
                default:
                    message.setStatus("Discarded");
                    JOptionPane.showMessageDialog(this,
                            "Message discarded.",
                            "Message Discarded",
                            JOptionPane.INFORMATION_MESSAGE);
                    break;
            }
        }

        // Show summary once all messages are done
        if (messagesSent > 0) {
            JOptionPane.showMessageDialog(this,
                    "You have sent/stored " + messagesSent + " message(s).",
                    "Message Summary",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Opens the report menu with options to view different message reports
     */
    private void showReportMenu() {
        JFrame reportFrame = new JFrame("Message Reports");
        reportFrame.setSize(400, 350);
        reportFrame.setLocationRelativeTo(null);
        reportFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel reportPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.anchor = GridBagConstraints.CENTER;

        // Add buttons for different reports
        JButton showAllMessagesButton = new JButton("Show All Messages");
        showAllMessagesButton.addActionListener(e -> showAllMessages());
        reportPanel.add(showAllMessagesButton, gbc);

        JButton showSentMessagesButton = new JButton("Show Sent Messages");
        showSentMessagesButton.addActionListener(e -> showMessagesByStatus("Sent"));
        reportPanel.add(showSentMessagesButton, gbc);

        JButton showStoredMessagesButton = new JButton("Show Stored Messages");
        showStoredMessagesButton.addActionListener(e -> showMessagesByStatus("Stored"));
        reportPanel.add(showStoredMessagesButton, gbc);

        JButton searchByRecipientButton = new JButton("Search by Recipient");
        searchByRecipientButton.addActionListener(e -> {
            String recipient = JOptionPane.showInputDialog(null, "Enter recipient name to search:");
            if (recipient != null && !recipient.trim().isEmpty()) {
                searchMessagesByRecipient(recipient.trim());
            } else {
                JOptionPane.showMessageDialog(null, "Recipient name cannot be empty.");
            }
        });
        reportPanel.add(searchByRecipientButton, gbc);

        JButton showMessageHashButton = new JButton("Show Message Hashes");
        showMessageHashButton.addActionListener(e -> showMessageHashes());
        reportPanel.add(showMessageHashButton, gbc);

        reportFrame.add(reportPanel);
        reportFrame.setVisible(true);
    }

    /**
     * Show all stored messages in a scrollable dialog
     */
    private void showAllMessages() {
        List<Message> messages = messageStorage.getMessages();
        if (messages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No messages found.");
            return;
        }

        StringBuilder report = new StringBuilder();
        report.append("=== ALL MESSAGES ===\n\n");
        for (Message message : messages) {
            report.append("Message #").append(message.getMessageNumber()).append("\n");
            report.append("ID: ").append(message.getMessageId()).append("\n");
            report.append("To: ").append(message.getRecipient()).append("\n");
            report.append("Content: ").append(message.getContent()).append("\n");
            report.append("Status: ").append(message.getStatus()).append("\n");
            report.append("Hash: ").append(message.getContentHash().substring(0, 15)).append("...\n");
            report.append("----------------------------\n");
        }

        // Show the report inside a scrollable text area
        JTextArea textArea = new JTextArea(report.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));

        JOptionPane.showMessageDialog(null, scrollPane, "All Messages", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Show messages filtered by a specific status (Sent/Stored)
     */
    private void showMessagesByStatus(String status) {
        List<Message> messages = messageStorage.getMessages();
        List<Message> filteredMessages = messages.stream()
                .filter(msg -> status.equals(msg.getStatus()))
                .toList();

        if (filteredMessages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No " + status.toLowerCase() + " messages found.");
            return;
        }

        StringBuilder report = new StringBuilder();
        report.append("=== ").append(status.toUpperCase()).append(" MESSAGES ===\n\n");
        for (Message message : filteredMessages) {
            report.append("Message #").append(message.getMessageNumber()).append("\n");
            report.append("ID: ").append(message.getMessageId()).append("\n");
            report.append("To: ").append(message.getRecipient()).append("\n");
            report.append("Content: ").append(message.getContent()).append("\n");
            report.append("Hash: ").append(message.getContentHash().substring(0, 15)).append("...\n");
            report.append("----------------------------\n");
        }

        JTextArea textArea = new JTextArea(report.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));

        JOptionPane.showMessageDialog(null, scrollPane, status + " Messages", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Search messages by recipient name
     */
    private void searchMessagesByRecipient(String recipient) {
        List<Message> messages = messageStorage.getMessages();
        List<Message> filteredMessages = messages.stream()
                .filter(msg -> msg.getRecipient().toLowerCase().contains(recipient.toLowerCase()))
                .toList();

        if (filteredMessages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No messages found for recipient: " + recipient);
            return;
        }

        StringBuilder report = new StringBuilder();
        report.append("=== MESSAGES FOR: ").append(recipient.toUpperCase()).append(" ===\n\n");
        for (Message message : filteredMessages) {
            report.append("Message #").append(message.getMessageNumber()).append("\n");
            report.append("ID: ").append(message.getMessageId()).append("\n");
            report.append("To: ").append(message.getRecipient()).append("\n");
            report.append("Content: ").append(message.getContent()).append("\n");
            report.append("Status: ").append(message.getStatus()).append("\n");
            report.append("----------------------------\n");
        }

        JTextArea textArea = new JTextArea(report.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));

        JOptionPane.showMessageDialog(null, scrollPane, "Messages for " + recipient, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Show all message hashes with content previews
     */
    private void showMessageHashes() {
        List<Message> messages = messageStorage.getMessages();
        if (messages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No messages found.");
            return;
        }

        StringBuilder report = new StringBuilder();
        report.append("=== MESSAGE HASHES ===\n\n");
        for (Message message : messages) {
            report.append("Message #").append(message.getMessageNumber()).append("\n");
            report.append("To: ").append(message.getRecipient()).append("\n");
            report.append("Content Preview: ").append(
                message.getContent().length() > 30 ? 
                message.getContent().substring(0, 30) + "..." : 
                message.getContent()
            ).append("\n");
            report.append("Full Hash: ").append(message.getContentHash()).append("\n");
            report.append("----------------------------\n");
        }

        JTextArea textArea = new JTextArea(report.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        JOptionPane.showMessageDialog(null, scrollPane, "Message Hashes", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Main method to run QuickChat
     */
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new ChatMenu().setVisible(true));
    }
}
