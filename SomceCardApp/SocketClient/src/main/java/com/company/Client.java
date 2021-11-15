package com.company;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.*;
import java.net.*;


/**
 * This is the chat client program.
 * Type 'bye' to terminte the program.
 */
public class Client {

    private final String hostname;
    private final int port;
    private String userName;
    static final Logger clientLogger = Logger.getLogger(Client.class.getName());

    public Client(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
        this.userName = "";
    }

    String getUserName() {
        return this.userName;
    }

    void setUserName(String userName) {
        this.userName = userName;
    }

    public void execute() {
        try {
            Socket socket = new Socket(hostname, port);

            clientLogger.info("Connected to the chat server");

            new ReadThread(socket, this, clientLogger).start();
            new WriteThread(socket, this, clientLogger).start();

        } catch (UnknownHostException ex) {
            clientLogger.info("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            clientLogger.info("I/O Error: " + ex.getMessage());
        }

    }

    public static void main(String[] args) {

        PropertyConfigurator.configure("./CommonUtil/src/main/resources/log4j.properties");

        String hostname = "localhost";
        int port = 8987;

        Client client = new Client(hostname, port);
        client.execute();
    }
}
