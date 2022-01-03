package com.company;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.util.Scanner;

public class Main {

    static final Logger clientLogger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {

        PropertyConfigurator.configure("./log4j.properties");

        clientLogger.info(Reader.imReader());
        clientLogger.info(Writer.imWriter());

        Scanner scanner = new Scanner(System.in);

        int number = scanner.nextInt();
        clientLogger.info("your number is: " + number);

        number = multiByTwo(number);

        clientLogger.info("your number multiplied by 2 is: " + number);
    }

    public static int multiByTwo(int number) {
        return number * 2;
    }
}
