package com.company;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Scanner;

public class Main {

    static final Logger logger = Logger.getLogger(Main.class.getName());

    public static int scanNumber() {

        try {
            int number = new Scanner(System.in).nextInt();
            if (number > 0) {
                return number;
            }
        } catch (Exception e) {
            logger.info("wrong number!");
            return 0;
        }
        logger.info("wrong number!");
        return 0;
    }

    public static void main(String[] args) {
        PropertyConfigurator.configure("./log4j.properties");

        int numOfReaders;
        int numOfWriters;

        do {
            logger.info("How much readers u want to have?");
            numOfReaders = scanNumber();
        }while (numOfReaders == 0);

        try {
            if (args[0].equals("test")) {
                String input = "3";
                InputStream in = new ByteArrayInputStream(input.getBytes());
                System.setIn(in);
            }
        } catch (Exception ignored) {
//            do nothing
        }

        do {
            logger.info("How much writers u want to have?");
            numOfWriters = scanNumber();
        }while (numOfWriters == 0);

        ReadingRoom room = new ReadingRoom(numOfReaders, numOfWriters);

        room.start();
    }
}
