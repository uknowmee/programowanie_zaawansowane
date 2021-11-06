package com.company;


/**
 * Main program class
 */
public class Main {

    /**
     * main program function
     * @param args program start arguments
     */
    public static void main( String[] args ) {

        System.out.println("#########################################");
        System.out.println("fresh deck:");
        Deck myDeck = new Deck();
        myDeck.printDeck();

        System.out.println("\n\nshuffled deck:");
        myDeck.setCards(myDeck.shuffle());
        myDeck.printDeck();

        System.out.println("\n\nfabric deck:");
        myDeck.setCards(myDeck.fabric());
        myDeck.printDeck();

        System.out.println("\n\nadding players:");
        for (int i = 0; i < 4; i++) {
            myDeck.addPlayer();
        }

        System.out.println("\n\nprinting players:");
        myDeck.printPlayerNames();

        System.out.println("\n\ndealing shuffled cards:");
        myDeck.setCards(myDeck.shuffle());
        myDeck.dealTheCards();

        System.out.println("\n\ndealt deck:");
        myDeck.printDeck();

        System.out.println("\n\ncollected deck");
        myDeck.collectTheCards();
        myDeck.printDeck();
        System.out.println("#########################################");


        System.out.println(CryptUtil.sha512("thanks for coding me hehehe"));
    }
}
