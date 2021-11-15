package com.company;

import org.apache.log4j.Logger;

import java.io.Console;


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

    @Override
    public void run() {

        String text;
        Console console = System.console();

        while (true) {

            text = console.readLine();

            if (text.equals("\\bye")) {
                break;
            }

            if (text.contains("\\help")) {
                stLogger.info("""
                        ###########################################################
                        commands:\s
                        \\show users - print all users
                        \\show decks - print all running decks
                        \\msg all - msg all connected users
                        \\bye - exit
                        ###########################################################
                        """);
            }
            else if (text.contains("\\show users")) {
                stLogger.info(server.getUserNames().toString());
            }
            else if (text.contains("\\show decks")) {
                stLogger.info(server.getDecks().toString());
            }
            else if (text.contains("\\msg all")) {
                server.broadcast(text, null);
            }
            else {
                stLogger.info("unknown command!");
            }
        }

        ifClose = true;
        System.exit(0);
    }
}
