package com.company;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import java.security.NoSuchAlgorithmException;


/**
 * Main program class
 */
public class Main {

    /**
     * Program logger, properties are in commons
     */
    static final Logger logger = Logger.getLogger(Main.class.getName());

    /**
     * main program function
     * @param args program start arguments
     */
    public static void main( String[] args ) throws NoSuchAlgorithmException {

        PropertyConfigurator.configure("./CommonUtil/src/main/resources/log4j.properties");
        logger.info("#########################################");
        logger.info("fresh deck:");
        Deck myDeck = new Deck(logger);
        myDeck.printDeck();

        logger.info("\n\nshuffled deck:");
        myDeck.setCards(myDeck.shuffle());
        myDeck.printDeck();

        logger.info("\n\nfabric deck:");
        myDeck.setCards(myDeck.fabric());
        myDeck.printDeck();

        logger.info("\n\nadding players:");
        for (int i = 0; i < 4; i++) {
            myDeck.addPlayer();
        }

        logger.info("\n\nprinting players:");
        myDeck.printPlayerNames();

        logger.info("\n\ndealing shuffled cards:");
        myDeck.setCards(myDeck.shuffle());
        myDeck.dealTheCards();

        logger.info("\n\ndealt deck:");
        myDeck.printDeck();

        logger.info("\n\ncollected deck");
        myDeck.collectTheCards();
        myDeck.printDeck();
        logger.info("#########################################");


        logger.info(CryptUtil.sha512("thanks for coding me hehehe"));
    }
}
