package com.company;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.*;
import java.net.*;
import java.util.*;


/**
 * This is the chat server program.
 */
public class Server {
    private final int port;
    private final Set<String> userNames = new HashSet<>();
    private final Set<UserThread> userThreads = new HashSet<>();
    private static final Set<User> users = new HashSet<>();
    private final Set<Deck> decks = new HashSet<>();
    static final Logger serverLogger = Logger.getLogger(Server.class.getName());

    /**
     * Class describing connected user
     */
    public static class User {
        private final UserThread userThread;
        private String userName;
        private String userCommandName;
        private Boolean inDeck;
        private String deckName;

        /**
         * Base constructor
         *
         * @param userThreads {@link UserThread} - thread running on server which handles user
         * @param userName    {@link String} - username
         */
        User(UserThread userThreads, String userName) {
            this.userThread = userThreads;
            this.userName = userName;
            this.userCommandName = "\\";
            this.inDeck = false;
            this.deckName = "";
        }

        /**
         * Returns tru if user is in the deck
         *
         * @return {@link User#inDeck} Boolean - used when checking if user is in deck
         */
        public Boolean getInDeck() {
            return inDeck;
        }

        /**
         * Returns username with "\" at the beginning
         *
         * @return {@link User#userCommandName} String - used when writing to user
         */
        public String getUserCommandName() {
            return userCommandName;
        }

        /**
         * Returns user thread
         *
         * @return {@link #userThread} UserThread - thread running on server which handles user
         */
        public UserThread getUserThread() {
            return userThread;
        }

        /**
         * Returns username
         *
         * @return {@link #userName} String - username
         */
        public String getUserName() {
            return userName;
        }

        /**
         * returns deck name
         * @return {@link #deckName} String - current deck name
         */
        public String getDeckName() {
            return deckName;
        }

        /**
         * Sets inDeck properties
         */
        public void setInDeck(Boolean inDeck, String deckName) {
            if (Boolean.TRUE.equals(inDeck)) {
                this.deckName = deckName;
            }
            else {
                this.deckName = "";
            }

            this.inDeck = inDeck;
        }
    }

    /**
     * Helper class which might contain:<br>
     * user command and message got user input in {@link UserThread} <br>
     * server command and message got from console in {@link ServerThread}<br>
     * while handling their single String input
     */
    public static class Split {
        private final String command;
        private String message;

        /**
         * Base constructor
         *
         * @param text {@link String} - users input will be split to:<br>
         *             {@link #command} - users command<br>
         *             {@link #message} - users message
         */
        public Split(String text) {
            String[] splitText = text.split(" ");

            this.command = splitText[0];
            this.message = "";

            for (int i = 1; i < splitText.length; i++) {
                if (i == splitText.length - 1) {
                    message = message.concat(splitText[i]);
                    break;
                }
                message = message.concat(splitText[i] + " ");
            }
        }

        /**
         * Returns user command
         *
         * @return {@link #command} String - command of the user
         */
        public String getCommand() {
            return command;
        }

        /**
         * Returns user command
         *
         * @return {@link #message} String - message of the user
         */
        public String getMessage() {
            return message;
        }
    }

    /**
     * Base constructor
     *
     * @param port {@link Integer} - port of the host
     */
    public Server(int port) {
        this.port = port;
    }

    /**
     * Return usernames
     *
     * @return {@link #port} Int - port of the server
     */
    public int getPort() {
        return port;
    }

    /**
     * Return usernames
     *
     * @return {@link #userNames} Set - names of the connected users
     */
    public Set<String> getUserNames() {
        return this.userNames;
    }

    /**
     * Return users
     *
     * @return {@link #users} Set - connected users
     */
    public static Set<User> getUsers() {
        return Server.users;
    }

    /**
     * Return user threads
     *
     * @return {@link #userThreads} Set - connected user threads
     */
    public Set<UserThread> getUserThreads() {
        return userThreads;
    }

    /**
     * Return User from users based on his name if user does not exist RETURNS NULL
     *
     * @param name String - users name
     * @return {@link User} User - user connected to server
     */
    public static User getUserFromName(String name) {
        for (User user : getUsers()) {
            if (user.getUserName().equals(name)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Return decks
     *
     * @return {@link #decks} Set - existing decks
     */
    public Set<Deck> getDecks() {
        return this.decks;
    }

    /**
     * Return User from users based on his name if user does not exist RETURNS NULL
     *
     * @param name String - users name
     */
    public static void userChangeDeck(String name, String deckName, Boolean inDeck) {
        for (User user : getUsers()) {
            if (user.getUserName().equals(name)) {
                user.setInDeck(inDeck, deckName);
            }
        }
    }

    /**
     * Delivers a message from one user to others (broadcasting)
     *
     * @param message     String - Client or Server message
     * @param excludeUser UserThread - user which won't see message
     */
    void broadcast(String message, UserThread excludeUser) {
        for (UserThread aUser : userThreads) {
            if (aUser != excludeUser) {
                aUser.sendMessage(message);
            }
        }
    }

    /**
     * Delivers a message to user
     *
     * @param message String - Client or Server message
     * @param toUser  UserThread - user which will see message
     */
    void writeToUser(String message, UserThread toUser) {
        for (UserThread aUser : userThreads) {
            if (aUser.equals(toUser)) {
                aUser.sendMessage(message);
                break;
            }
        }
    }

    /**
     * Stores userThread of the newly connected client.
     *
     * @param newUser UserThread - thread which will be added to {@link #userThreads}
     */
    void addUserThread(UserThread newUser) {
        userThreads.add(newUser);
    }

    /**
     * Stores username of the newly connected client then search through {@link #users} and update it.
     *
     * @param userName String - name which will be added to {@link #userNames}
     * @param thread   UserThread - thread which will be added to {@link #userThreads}
     */
    void addUserName(String userName, UserThread thread) {
        userNames.add(userName);
        for (User user : users) {
            if (user.userThread.equals(thread)) {
                user.userName = userName;
                user.userCommandName = user.userCommandName.concat(userName);
            }
        }
    }

    /**
     * Stores userThread of the newly connected client.
     *
     * @param newUser UserThread - thread which will be added to {@link #userThreads}
     */
    void addUser(UserThread newUser) {
        users.add(new User(newUser, ""));
    }

    public void addDeck(String deckName, UserThread userThread, int numOfPlayers) {
        String userName = "";
        for (User user : users) {
            if (user.getUserThread().equals(userThread)) {
                userName = user.getUserName();
            }
        }
        decks.add(new Deck(serverLogger, deckName, userName, numOfPlayers));
    }

    public void removeDeck(Deck deck) {
        decks.remove(deck);
    }

    /**
     * When a client is disconnected, removes the associated<br>
     * username,<br>
     * UserThread,<br>
     * user (from {@link #users})
     *
     * @param userName String - username to be removed from {@link #userNames}
     * @param aUser    UserThread - thread to be removed from {@link #userThreads}
     */
    void removeUser(String userName, UserThread aUser) {
        boolean removed = userNames.remove(userName);
        if (removed) {
            userThreads.remove(aUser);

            for (User user : users) {
                if (user.userThread.equals(aUser)) {
                    users.remove(user);
                    break;
                }
            }

            serverLogger.info("The user " + userName + " quit");
        }
    }

    /**
     * Method which waits for connection from client (Infinite loop)<br>
     * if succeed it start thread to handle client:<br>
     * {@link ServerThread#ServerThread(Logger, Server)}
     */
    public void execute() {
        try (ServerSocket serverSocket = new ServerSocket(this.port)) {

            serverLogger.info("Chat Server is listening on port " + this.port);
            serverLogger.info("write: \\help to see commands");


            ServerThread stop = new ServerThread(serverLogger, this);
            stop.start();

            while (!stop.isIfClose()) {
                Socket socket = serverSocket.accept();
                serverLogger.info("New user connected");

                UserThread newUser = new UserThread(socket, this, serverLogger);
                this.addUserThread(newUser);
                this.addUser(newUser);
                newUser.start();
            }

        } catch (IOException ex) {
            serverLogger.info("Error in the server: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Main class making one Server object and starting {@link #execute()} method
     *
     * @param args String - is specified while starting program
     */
    public static void main(String[] args) {
        PropertyConfigurator.configure("./CommonUtil/src/main/resources/log4j.properties");

        int port = 8987;

        Server server = new Server(port);
        server.execute();
    }
}
