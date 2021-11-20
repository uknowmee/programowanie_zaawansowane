package com.company;

import java.io.*;
import java.net.*;
import java.util.Objects;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * This thread handles connection for each connected client, so the server
 * can handle multiple clients at the same time.
 */
public class UserThread extends Thread {
    private final Server server;
    private final Socket socket;
    private PrintWriter writer;
    private final Logger utLogger;
    private static final String UNKNOWN_COMMAND = "unknown command!";
    private static final String PLAYERS = " players";

    /**
     * Base constructor
     *
     * @param socket {@link Socket} - socket of connection
     * @param logger {@link Logger} - prints server input and log stuff if necessary
     * @param server {@link Server} - server of which console will be handled
     */
    public UserThread(Socket socket, Server server, Logger logger) {
        this.socket = socket;
        this.server = server;
        this.utLogger = logger;
    }

    /**
     * Return owner
     *
     * @return {@link Server} Server - server which server thread is managing
     */
    public Server getServer() {
        return server;
    }

    /**
     * Return connection socket
     *
     * @return {@link #socket} Socket - socket of connection with client
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * Return owners logger
     *
     * @return {@link #utLogger} Logger - owners logger
     */
    public Logger getUtLogger() {
        return utLogger;
    }

    public void setWriter(PrintWriter writer) {
        this.writer = writer;
    }

    /**
     * Sends a message to the client.
     *
     * @param message String - message to client
     */
    String sendMessage(String message) {
        writer.println(message);
        return message;
    }

    /**
     * Print commands to server console
     */
    public String showCommands() {
        String message = """
                ###########################################################
                commands:\s
                \\help - print all commands
                \\showusers - print all users
                \\showdecks - print all running decks
                \\adddeck <nameOfDeck> <numberOfPlayers>
                \\joindeck <nameOfDeck>
                \\leavedeck
                \\msgall - msg all connected users
                \\<username> - msg specified user
                \\bye - exit
                ###########################################################
                """;

        server.writeToUser(message, this);
        return message;
    }

    public String showUsers() {
        String usersString = "";
        Set<Server.User> users = Server.getUsers();
        String inDeck = ", in deck: ";

        if (users.isEmpty()) {
            usersString = "[]";
        }
        else if (users.size() == 1) {
            for (Server.User us : users) {
                usersString = "[" + us.getUserName() + inDeck + us.getDeckName() + ", " + us.getUserThread() + "]";
            }
        }
        else {
            int i = 0;
            for (Server.User us : users) {
                if (i == 0) {
                    usersString = usersString.concat("[" + us.getUserName() + inDeck + us.getDeckName() + "\n");
                    i++;
                }
                else if (i == users.size() - 1) {
                    usersString = usersString.concat(us.getUserName() + inDeck + us.getDeckName() + "]");
                }
                else {
                    usersString = usersString.concat(us.getUserName() + inDeck + us.getDeckName() + "\n");
                    i++;
                }
            }
        }

        return usersString;
    }

    public String showDecks() {
        String decks = "";

        for (Deck deck : server.getDecks()) {
            decks = decks.concat(deck.toString() + "\n");
        }
        server.writeToUser(decks, this);

        return decks;
    }

    public String addDeck(String userName, Server.Split split) {

        Server.Split split1 = new Server.Split(split.getMessage());
        String name = split1.getCommand();
        String numOfPlayers = split1.getMessage();

        for (Deck deck : server.getDecks()) {
            if (deck.getName().equals(name)) {
                return UNKNOWN_COMMAND;
            }
        }

        String ret = "You have created a deck named: " + name + " for: ";
        switch (numOfPlayers) {
            case "2":
                server.addDeck(name, this, 2);
                Server.userChangeDeck(userName, name, true);
                return ret + 2 + PLAYERS;
            case "3":
                server.addDeck(name, this, 3);
                Server.userChangeDeck(userName, name, true);
                return ret + 3 + PLAYERS;
            case "4":
                server.addDeck(name, this, 4);
                Server.userChangeDeck(userName, name, true);
                return ret + 4 + PLAYERS;
            default:
                return "Invalid deck name or number of players";
        }
    }

    public String joinDeck(String userName, Server.Split split) {
        for (Deck deck : server.getDecks()) {
            if (deck.getName().equals(split.getMessage()) &&
                    deck.getNumOfPlayers() > deck.getPlayers().size()) {
                deck.playerJoin(userName);
                Server.userChangeDeck(userName, split.getMessage(), true);
                return "You have joined a deck named: ".concat(deck.getName());
            }
        }
        return UNKNOWN_COMMAND;
    }

    public String leaveDeck(String userName, Server.Split split) {
        for (Deck deck : server.getDecks()) {
            if (deck.getPlayerNames().contains(userName)) {
                if (deck.getPlayers().size() > 1) {
                    deck.playerLeave(userName);
                    Server.userChangeDeck(userName, split.getMessage(), false);
                }
                else {
                    Server.userChangeDeck(userName, split.getMessage(), false);
                    server.removeDeck(deck);
                }
                return "You have left a deck named: ".concat(deck.getName());
            }
        }
        return UNKNOWN_COMMAND;
    }

    /**
     * Handles default user action
     *
     * @param split    Split - client message container
     * @param userName - String client name
     */
    public String defaultAction(Server.Split split, String userName) {
        if (split.getCommand().equals("\\".concat(userName))) {
            server.writeToUser(UNKNOWN_COMMAND, this);
            return UNKNOWN_COMMAND;
        }
        else {
            for (Server.User user : Server.getUsers()) {
                if (user.getUserCommandName().equals(split.getCommand())) {
                    server.writeToUser(userName + ": " + split.getMessage(), user.getUserThread());
                    return userName + ": " + split.getMessage();
                }
            }
        }
        return UNKNOWN_COMMAND;
    }

    /**
     * Handles user action
     *
     * @param userName      String - user name
     * @param clientMessage - client input
     */
    public String action(String userName, String clientMessage) {
        Server.Split split = new Server.Split(clientMessage);

        utLogger.info(userName + ": " + split.getCommand() + " " + split.getMessage());

        switch (split.getCommand()) {
            case "\\help" -> {
                return showCommands();
            }
            case "\\showusers" -> {
                return sendMessage(showUsers());
            }
            case "\\showdecks" -> {
                return showDecks();
            }
            case "\\adddeck" -> {
                if (Objects.requireNonNull(Server.getUserFromName(userName)).getInDeck().equals(false)) {
                    return sendMessage(addDeck(userName, split));
                }
                else {
                    server.writeToUser(UNKNOWN_COMMAND, this);
                    return UNKNOWN_COMMAND;
                }
            }
            case "\\joindeck" -> {
                if (Objects.requireNonNull(Server.getUserFromName(userName)).getInDeck().equals(false)) {
                    return sendMessage(joinDeck(userName, split));
                }
                else {
                    server.writeToUser(UNKNOWN_COMMAND, this);
                    return "you already are in deck";
                }
            }
            case "\\leavedeck" -> {
                if (Objects.requireNonNull(Server.getUserFromName(userName)).getInDeck().equals(true)) {
                    return sendMessage(leaveDeck(userName, split));
                }
                else {
                    server.writeToUser("you are not in deck", this);
                    return "you are already not in deck";
                }
            }
            case "\\msgall" -> {
                server.broadcast(userName + ": " + split.getMessage(), this);
                return "messaged all";
            }
            default -> {
                return defaultAction(split, userName);
            }
        }
    }

    /**
     * Reads client input Infinite loop and handles it in {@link #action(String, String)}
     */
    @Override
    public void run() {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);

            String userName = reader.readLine();

            int i = 1;
            while (server.getUserNames().contains(userName)) {
                userName = userName.concat(String.valueOf(i));
                i += 1;
            }
            server.addUserName(userName, this);
            sendMessage("Your userName is: " + userName);

            String serverMessage;
            String clientMessage;

            server.writeToUser("write: \\help to see commands", this);
            do {
                clientMessage = reader.readLine();

                action(userName, clientMessage);

            } while (!clientMessage.equals("\\bye"));

            server.removeUser(userName, this);
            socket.close();

            serverMessage = userName + " has quit.";
            server.broadcast(serverMessage, this);

        } catch (IOException ex) {
            utLogger.info("Error in UserThread: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
