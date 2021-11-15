package com.company;

import org.apache.log4j.Logger;

import java.io.Console;
import java.util.Set;


public class ServerThread extends Thread {

    private boolean ifClose;
    private final Logger stLogger;
    private final Server server;


    public ServerThread(Logger logger, Server server) {
        this.ifClose = false;
        this.stLogger = logger;
        this.server = server;
    }

    public boolean info() {
        return this.ifClose;
    }

    /**
     * Handles server action
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
            case "\\showusers" -> {
                Set<Server.User> users = server.getUsers();
                for (Server.User us : users) {
                    stLogger.info(us.getUserName() + " " + us.getUserThread());
                }
            }
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
