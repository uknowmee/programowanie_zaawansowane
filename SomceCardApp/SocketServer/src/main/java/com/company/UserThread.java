package com.company;

import java.io.*;
import java.net.*;

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


    public UserThread(Socket socket, Server server, Logger logger) {
        this.socket = socket;
        this.server = server;
        this.utLogger = logger;
    }

    /**
     * Sends a message to the client.
     */
    void sendMessage(String message) {
        writer.println(message);
    }

    /**
     * Handles user action
     */
    void action() {
        Server.Container container;
        container = server.queuePopFront();

        Server.Split split = new Server.Split(container.getServerText());

        utLogger.info(container.getUserName() + ": " + split.getCommand() + " " + split.getMessage());

        switch (split.getCommand()) {
            case "\\help" -> server.writeToUser("""
                    ###########################################################
                    commands:\s
                    \\help - print all commands
                    \\showusers - print all users
                    \\showdecks - print all running decks
                    \\msgall - msg all connected users
                    \\<username> - msg specified user
                    \\bye - exit
                    ###########################################################
                    """, container.getUserThread());
            case "\\showusers" -> server.writeToUser(server.getUserNames().toString(), container.getUserThread());
            case "\\showdecks" -> server.writeToUser(server.getDecks().toString(), container.getUserThread());
            case "\\msgall" -> server.broadcast(
                    container.getUserName() + ": " + split.getMessage(), container.getUserThread());
            default -> {
                boolean done = false;
                if (!"\\".concat(container.getUserName()).equals(container.getUserCommandName())) {
                    server.writeToUser("unknown command!", container.getUserThread());
                    break;
                }
                else {
                    for (Server.User user : server.getUsers()) {
                        if (user.getUserCommandName().equals(split.getCommand())) {
                            server.writeToUser(container.getUserName() + ": " + split.getMessage(), user.getUserThread());
                            done = true;
                            break;
                        }
                    }
                }
                if (!done) {
                    server.writeToUser("unknown command!", container.getUserThread());
                }
            }
        }
    }

    @Override
    public void run() {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);

            String userName = reader.readLine();
            server.addUserName(userName, this);

            String serverMessage;
            String clientMessage;

            server.writeToUser("write: \\help to see commands", this);
            do {
                clientMessage = reader.readLine();

                server.queuePushBack(userName, this, clientMessage);
                if (!server.queueIsEmpty()) {
                    action();
                }
            } while (!clientMessage.equals("\\bye"));

            server.removeUser(userName, this);
            socket.close();

            serverMessage = userName + " has quitted.";
            server.broadcast(serverMessage, this);

        } catch (IOException ex) {
            utLogger.info("Error in UserThread: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
