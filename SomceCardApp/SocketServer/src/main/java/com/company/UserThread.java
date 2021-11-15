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
    void action(){
        Server.Container container;
        container = server.queuePopFront();
        utLogger.info(container.toString());

        if (container.getServerText().equals("\\bye")) {
            System.exit(0);
        }

        if (container.getServerText().contains("\\help")) {
            server.writeToUser("""
                        ###########################################################
                        commands:\s
                        \\show users - print all users
                        \\show decks - print all running decks
                        \\msg all - msg all connected users
                        \\bye - exit
                        ###########################################################
                        """, container.getUserThread());
        }
        else if (container.getServerText().contains("\\show users")) {
            server.writeToUser(server.getUserNames().toString(), container.getUserThread());
        }
        else if (container.getServerText().contains("\\show decks")) {
            server.writeToUser(server.getDecks().toString(), container.getUserThread());
        }
        else if (container.getServerText().contains("\\msg all")) {
            server.broadcast(container.getServerText(), container.getUserThread());
        }
        else {
            server.writeToUser("unknown command!", container.getUserThread());
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
            server.addUserName(userName);

            String serverMessage;
            String clientMessage;

            server.writeToUser("write: \\help to see commands", this);
            do {
                clientMessage = reader.readLine();

                server.queuePushBack(userName, this, clientMessage);
                if (!server.queueIsEmpty()) {
                    action();
                }
            } while (!clientMessage.equals("bye"));

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
