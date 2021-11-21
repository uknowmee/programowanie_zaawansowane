package com.company;

import java.util.Objects;

/**
 *
 */
public class GameStartedThread extends Thread {

    /**
     * Print standard response from deck to any started game
     * @return ret String - response
     */
    public static String gameStarted() {

        for (Deck deck : Server.getDecks()) {
            if (deck.getNumOfPlayers() == deck.getPlayers().size() && deck.isStarted()) {
                for (String player : deck.getPlayerNames()) {
                    Server.writeToUser(deck.getResponseString(),
                            Objects.requireNonNull(Server.getUserFromName(player)).getUserThread());
                }
                return deck.getResponseString();
            }
        }
        return "";
    }

    /**
     * Runs {@link #gameStarted()} in infinite loop
     */
    @Override
    public void run() {
        while (true) {
            try {
                sleep(10000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            gameStarted();
        }
    }
}