package com.company;

import org.apache.log4j.Logger;

import java.io.*;
import java.net.*;

/**
 * This thread is responsible for reading server's input and printing it
 * to the console.
 * It runs in an infinite loop until the client disconnects from the server.
 */
public class ReadThread extends Thread {

    private BufferedReader reader;
    private final Client client;
    private final Logger rtLogger;


    public ReadThread(Socket socket, Client client, Logger logger) {
        this.client = client;
        this.rtLogger = logger;

        try {
            InputStream input = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));
        } catch (IOException ex) {
            rtLogger.info("Error getting input stream: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                String response = reader.readLine();

                if (!client.getUserName().equals("")) {
                    rtLogger.info(response);
                }

            } catch (IOException ex) {
                rtLogger.info("Error reading from server: " + ex.getMessage());
                ex.printStackTrace();
                break;
            }
        }
        System.exit(0);
    }
}
