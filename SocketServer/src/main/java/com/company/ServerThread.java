package com.company;

import org.apache.log4j.Logger;

import java.io.Console;
import java.util.Set;

/**
 * This thread handles server console.
 */
public class ServerThread extends Thread {
    private boolean ifClose;
    private final Logger stLogger;
    private final Server server;
    private static final String SERVER_STRING = "[SERVER]: ";


    /**
     * Base constructor
     *
     * @param logger {@link Logger} - prints server input and log stuff if necessary
     * @param server {@link Server} - server of which console will be handled
     */
    public ServerThread(Logger logger, Server server) {
        this.ifClose = false;
        this.stLogger = logger;
        this.server = server;
    }

    /**
     * Return owners logger
     *
     * @return {@link #stLogger} Logger - owners logger
     */
    public Logger getStLogger() {
        return stLogger;
    }

    /**
     * Return owner
     *
     * @return {@link #server} Server - server which server thread is managing
     */
    public Server getServer() {
        return server;
    }

    /**
     * Return ifClose
     *
     * @return {@link #ifClose} Boolean - true if we should close a server else false
     */
    public boolean isIfClose() {
        return ifClose;
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
                \\msgall - msg all connected users
                \\<username> - msg specified user
                \\CLOSE - exit
                ###########################################################""";

        stLogger.info(message);
        return message;
    }

    public String showDecks() {
        String decks = "";

        for (Deck deck : server.getDecks()) {
            decks = decks.concat(deck.toString());
        }

        return decks;
    }

    /**
     * Print users to server console
     */
    public String showUsers() {
        String usersString = "";
        Set<Server.User> users = Server.getUsers();
        String inDeck = ", in deck: ";

        if (users.isEmpty()) {
            usersString = "[]";
            stLogger.info("[]");
        }
        else if (users.size() == 1) {
            for (Server.User us : users) {
                stLogger.info("[" + us.getUserName() + inDeck + us.getDeckName() + ", " + us.getUserThread() + "]");
                usersString = usersString.concat("[" + us.getUserName() + inDeck + us.getDeckName() + ", " + us.getUserThread() + "]");
            }
        }
        else {
            int i = 0;
            for (Server.User us : users) {
                if (i == 0) {
                    stLogger.info("[" + us.getUserName() + inDeck + us.getDeckName() + ", " + us.getUserThread());
                    usersString = usersString.concat("[" + us.getUserName() + inDeck + us.getDeckName() + ", " + us.getUserThread() + "\n");
                    i++;
                }
                else if (i == users.size() - 1) {
                    stLogger.info(us.getUserName() + inDeck + us.getDeckName() + ", " + us.getUserThread() + "]");
                    usersString = usersString.concat(us.getUserName() + inDeck + us.getDeckName() + ", " + us.getUserThread() + "]");
                }
                else {
                    stLogger.info(us.getUserName() + inDeck + us.getDeckName() + ", " + us.getUserThread());
                    usersString = usersString.concat(us.getUserName() + inDeck + us.getDeckName() + ", " + us.getUserThread() + "\n");
                    i++;
                }
            }
        }

        return usersString;
    }

    /**
     * Handles server action
     *
     * @param text String - server input
     */
    public String action(Server.Split text) {
        switch (text.getCommand()) {
            case "\\help" -> {
                return showCommands();
            }
            case "\\showusers" -> {
                return showUsers();
            }
            case "\\showdecks" -> {
                stLogger.info(showDecks());
                return showDecks();
            }
            case "\\msgall" -> {
                server.broadcast(SERVER_STRING + text.getMessage(), null);
                return "messaged all";
            }
            default -> {
                for (Server.User us : Server.getUsers()) {
                    if (us.getUserCommandName().equals(text.getCommand())) {
                        server.writeToUser(SERVER_STRING + text.getMessage(), us.getUserThread());
                        return SERVER_STRING + text.getMessage();
                    }
                }
                stLogger.info("unknown command!");
                return "unknown command!";
            }
        }
    }

    /**
     * Infinite loop reading the server input
     */
    @Override
    public void run() {

        Server.Split text;
        Console console = System.console();

        while (true) {

            text = new Server.Split(console.readLine());

            if (text.getCommand().equals("\\CLOSE")) {
                break;
            }

            action(text);
        }

        ifClose = true;
        System.exit(0);
    }
}
