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
     *
     * @param userName      String - user name
     * @param clientMessage - client input
     */
    void action(String userName, String clientMessage) {
        Server.Split split = new Server.Split(clientMessage);

        utLogger.info(userName + ": " + split.getCommand() + " " + split.getMessage());

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
                    """, this);
            case "\\showusers" -> server.writeToUser(server.getUserNames().toString(), this);
            case "\\showdecks" -> server.writeToUser(server.getDecks().toString(), this);
            case "\\msgall" -> server.broadcast(
                    userName + ": " + split.getMessage(), this);
            default -> {
                boolean done = false;
                if (split.getCommand().equals(userName)) {
                    server.writeToUser("unknown command!", this);
                    break;
                }
                else {
                    for (Server.User user : server.getUsers()) {
                        if (user.getUserCommandName().equals(split.getCommand())) {
                            server.writeToUser(userName + ": " + split.getMessage(), user.getUserThread());
                            done = true;
                            break;
                        }
                    }
                }
                if (!done) {
                    server.writeToUser("unknown command!", this);
                }
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

            int i=1;
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
