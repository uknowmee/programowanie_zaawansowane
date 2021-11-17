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
    private final Set<User> users = new HashSet<>();
    private final Set<Deck> decks = new HashSet<>();
    static final Logger serverLogger = Logger.getLogger(Server.class.getName());

    /**
     * Class describing connected user
     */
    static class User {
        private final UserThread userThread;
        private String userName;
        private String userCommandName;

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
    }

    /**
     * Helper class which might contain:<br>
     * user command and message got user input in {@link UserThread} <br>
     * server command and message got from console in {@link ServerThread}<br>
     * while handling their single String input
     */
    static class Split {
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
     * @return {@link #userNames} Set - names of the connected users
     */
    Set<String> getUserNames() {
        return this.userNames;
    }

    /**
     * Return users
     *
     * @return {@link #users} Set - connected users
     */
    Set<User> getUsers() {
        return this.users;
    }

    /**
     * Return decks
     *
     * @return {@link #decks} Set - existing decks
     */
    Set<Deck> getDecks() {
        return this.decks;
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
     * @param message     String - Client or Server message
     * @param toUser UserThread - user which will see message
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
     * Stores username of the newly connected client then search through {@link #users} and update it.
     *
     * @param userName String - name which will be added to {@link #userNames}
     * @param thread UserThread - thread which will be added to {@link #userThreads}
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
     * When a client is disconnected, removes the associated<br>
     * username,<br>
     * UserThread,<br>
     * user (from {@link #users})
     *
     * @param userName String - username to be removed from {@link #userNames}
     * @param aUser UserThread - thread to be removed from {@link #userThreads}
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

            while (!stop.info()) {
                Socket socket = serverSocket.accept();
                serverLogger.info("New user connected");

                UserThread newUser = new UserThread(socket, this, serverLogger);
                userThreads.add(newUser);
                users.add(new User(newUser, ""));
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
