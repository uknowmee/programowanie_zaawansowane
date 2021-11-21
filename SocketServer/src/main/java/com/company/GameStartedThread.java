package com.company;

import java.util.Objects;

public class GameStartedThread extends Thread {

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

    @Override
    public void run() {
        while (true) {
            try {
                sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
            gameStarted();
        }
    }
}