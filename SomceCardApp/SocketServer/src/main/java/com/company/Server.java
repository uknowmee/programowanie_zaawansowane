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
    private final Set<Deck> decks = new HashSet<>();
    private final ArrayList<Container> queue = new ArrayList<>();
    static final Logger serverLogger = Logger.getLogger(Server.class.getName());

    static class Container{
        private String userName = "";
        private String serverText = "";
        private UserThread userThread;

        public Container(){}

        public Container(String userName, UserThread userThread, String serverText){
            this.userName = userName;
            this.userThread = userThread;
            this.serverText = serverText;
        }

        public String getUserName() {
            return userName;
        }

        public String getServerText() {
            return serverText;
        }

        public UserThread getUserThread() {
            return userThread;
        }
    }


    public Server(int port) {
        this.port = port;
    }

    Set<String> getUserNames() {
        return this.userNames;
    }
    Set<Deck> getDecks() {
        return this.decks;
    }
    ArrayList<Container> getQueue() {
        return this.queue;
    }

    /**
     * Keeps messages from users to server
     */
    void queuePushBack(String userName, UserThread userThread, String command){
        queue.add(new Container(userName, userThread, command));
    }

    /**
     * Returns true if queue is empty else return false
     */
    boolean queueIsEmpty() {
        return queue.isEmpty();
    }

    /**
     * Pops first message from queue
     */
    Container queuePopFront(){
        return queue.remove(0);
    }

    /**
     * Delivers a message from one user to others (broadcasting)
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
     */
    void writeToUser(String message, UserThread toUser) {
        for (UserThread aUser : userThreads) {
            if (aUser.equals(toUser)) {
                aUser.sendMessage(message);
            }
        }
    }

    /**
     * Stores username of the newly connected client.
     */
    void addUserName(String userName) {
        userNames.add(userName);
    }

    /**
     * When a client is disconneted, removes the associated username and UserThread
     */
    void removeUser(String userName, UserThread aUser) {
        boolean removed = userNames.remove(userName);
        if (removed) {
            userThreads.remove(aUser);
            serverLogger.info("The user " + userName + " quitted");
        }
    }

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
                newUser.start();
            }

        } catch (IOException ex) {
            serverLogger.info("Error in the server: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {

        PropertyConfigurator.configure("./CommonUtil/src/main/resources/log4j.properties");

        int port = 8987;

        Server server = new Server(port);
        server.execute();
    }
}
