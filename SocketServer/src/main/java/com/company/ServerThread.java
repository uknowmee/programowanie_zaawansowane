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
     * Return ifClose
     *
     * @return {@link #ifClose} Boolean - true if we should close a server else false
     */
    public boolean info() {
        return this.ifClose;
    }

    /**
     * Print users to server console
     */
    public void showUsers(){
        Set<Server.User> users = server.getUsers();
        if (users.isEmpty()) {
            stLogger.info("[]");
        }
        else if (users.size() == 1) {
            for (Server.User us : users) {
                stLogger.info("[" + us.getUserName() + " " + us.getUserThread() + "]");
            }
        }
        else {
            int i = 0;
            for (Server.User us : users) {
                if (i == 0) {
                    stLogger.info("[" + us.getUserName() + " " + us.getUserThread());
                    i++;
                }
                else if (i == users.size() - 1) {
                    stLogger.info(us.getUserName() + " " + us.getUserThread() + "]");
                }
                else {
                    stLogger.info(us.getUserName() + " " + us.getUserThread());
                    i++;
                }
            }
        }
    }

    /**
     * Handles server action
     *
     * @param text String - server input
     */
    void action(Server.Split text) {
        switch (text.getCommand()) {
            case "\\help" -> stLogger.info("""
                    ###########################################################
                    commands:\s
                    \\help - print all commands
                    \\showusers - print all users
                    \\showdecks - print all running decks
                    \\msgall - msg all connected users
                    \\<username> - msg specified user
                    \\CLOSE - exit
                    ###########################################################""");
            case "\\showusers" -> showUsers();
            case "\\showdecks" -> stLogger.info(server.getDecks().toString());
            case "\\msgall" -> server.broadcast("[SERVER]: " + text.getMessage(), null);
            default -> {
                boolean done = false;
                for (Server.User us : server.getUsers()) {
                    if (us.getUserCommandName().equals(text.getCommand())) {
                        done = true;
                        server.writeToUser("[SERVER]: " + text.getMessage(), us.getUserThread());
                        break;
                    }
                }
                if (!done) {
                    stLogger.info("unknown command!");
                }
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
