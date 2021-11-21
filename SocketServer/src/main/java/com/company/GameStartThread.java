package com.company;

import java.util.Objects;

public class GameStartThread extends Thread {

    public static String gameStart() throws InterruptedException {

        sleep(1000);
        for (Deck deck : Server.getDecks()) {
            if (deck.getNumOfPlayers() == deck.getPlayers().size() && !deck.isStarted()) {
                for (String player : deck.getPlayerNames()) {
                    Server.writeToUser("game has started!",
                            Objects.requireNonNull(Server.getUserFromName(player)).getUserThread());
                }
                deck.setStarted(true);
                GameStartedThread gameStartedThread = new GameStartedThread();
                gameStartedThread.start();
                return "game has started!";
            }
        }
        return "";
    }

    @Override
    public void run() {
        while (true) {
            try {
                gameStart();
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }
}
