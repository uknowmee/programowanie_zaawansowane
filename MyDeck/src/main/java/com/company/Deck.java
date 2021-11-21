package com.company;


import org.apache.log4j.Logger;

import java.util.*;


/**
 * class representing classic Deck
 */
public class Deck {
    private final Random random;
    private final Logger logger;
    private ArrayList<Card> cards;
    private final ArrayList<Player> players;
    private final String name;
    private final int numOfPlayers;
    private final Response response;
    private boolean started;

    /**
     * Class describing a single Card
     */
    private static class Card {

        private final Rank rank;
        private final Suit suit;

        private enum Rank {

            DZIEWIATKA("Dziewiatka", 9),
            DZIESIATKA("Dziesiatka", 10),
            WALET("Walet", 11),
            DAMA("Dama", 12),
            KROL("Krol", 13),
            AS("As", 14);

            private final int value;
            private final String designation;

            Rank(String designation, int value) {
                this.value = value;
                this.designation = designation;
            }
        }

        private enum Suit {

            PIK("Pik"),
            TREFL("Trefl"),
            KIER("Kier"),
            KARO("Karo");

            private final String suitOfCard;

            Suit(String suitToSet) {this.suitOfCard = suitToSet;}
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Card card)) {
                return false;
            }

            if (rank != card.rank) {
                return false;
            }
            return suit == card.suit;
        }

        @Override
        public int hashCode() {
            int result = rank.hashCode();
            result = 31 * result + suit.hashCode();
            return result;
        }

        private Card(String suitToSet, String rankToSet) {

            this.rank = Rank.valueOf(rankToSet);
            this.suit = Suit.valueOf(suitToSet);
        }
    }

    /**
     * Class describing a single Player
     */
    private static class Player {

        private final ArrayList<Card> playerCards;
        private final String playerName;

        /**
         * Base constructor
         *
         * @param playerName {@link String} - name of the player
         */
        private Player(String playerName) {

            this.playerName = playerName;
            this.playerCards = new ArrayList<>();
        }
    }

    /**
     * Static response from Deck with running game to server
     */
    private static class Response{

        /**
         * Returns response as string
         * @return ret String - response represented by string
         */
        @Override
        public String toString() {
            return "not implemented yet";
        }
    }

    /**
     * Base constructor
     *
     * @param serverLogger {@link Logger} - log stuff
     * @param deckName     {@link String} - name of the deck
     * @param userName     {@link String} - name of deck creator
     * @param numOfPlayers {@link Integer} - max number of players
     */
    public Deck(Logger serverLogger, String deckName,
                String userName, int numOfPlayers) {

        this.response = new Response();
        this.random = new Random();
        this.logger = serverLogger;

        this.players = new ArrayList<>();
        this.players.add(new Player(userName));

        this.cards = new ArrayList<>();
        fabric();

        this.name = deckName;
        this.numOfPlayers = numOfPlayers;

        this.started = false;
    }

    /**
     * Return cards which are not in player hands
     *
     * @return {@link #cards} ArrayList - cards on heap
     */
    public ArrayList<Card> getCards() {
        return cards;
    }

    /**
     * Returns deck response as string
     * @return ret String - deck response represented by string
     */
    public String getResponseString() {
        return response.toString();
    }

    /**
     * Return players connected to deck
     *
     * @return {@link #players} ArrayList - players on deck
     */
    public ArrayList<Player> getPlayers() {
        return players;
    }

    /**
     * Return player names connected to deck
     *
     * @return playerNames ArrayList - player names on deck
     */
    public ArrayList<String> getPlayerNames() {
        ArrayList<String> playerNames = new ArrayList<>();
        for (Player player : players) {
            playerNames.add(player.playerName);
        }
        return playerNames;
    }

    /**
     * Return name of the deck
     *
     * @return {@link #name} String - deck name
     */
    public String getName() {
        return name;
    }

    /**
     * Method says if certain game already started
     * @return {@link #started} Boolean - true if game started
     */
    public boolean isStarted() {
        return started;
    }

    /**
     * Return number of maximum players
     *
     * @return {@link #numOfPlayers} Integer - max number of players
     */
    public int getNumOfPlayers() {
        return numOfPlayers;
    }

    /**
     * GameStartThread set this to true when starting the game
     * @param started Boolean - this.started to set
     */
    public void setStarted(boolean started) {
        this.started = started;
    }

    /**
     * Returns deck as string
     * @return ret String - deck represented by string
     */
    @Override
    public String toString() {
        StringBuilder deck = new StringBuilder("Deck named: " + name +
                ", with maximum of: " + numOfPlayers + " players\n" + "\tdecks players:\n");

        for (Player player : players) {
            deck.append("\t\t").append(player.playerName).append("\n");
        }

        return deck.toString();
    }

    public void playerJoin(String userName) {
        players.add(new Player(userName));
    }

    public void playerLeave(String userName) {
        for (Player player : players) {
            if (player.playerName.equals(userName)) {
                players.remove(player);
                break;
            }
        }
    }

    /**
     * Void method which prints whole deck, list of cards, players and certain player cards
     */
    public void printDeck() {

        logger.info("\tcards on heap: " + cards.size());
        for (Card card : cards) {
            logger.info("\t\t\t" + card.rank.designation + " (" + card.rank.value + ") " + card.suit.suitOfCard);
        }

        logger.info("\tcards in players hands: " + (24 - cards.size()));
        for (Player player : players) {
            logger.info("\t\t" + player.playerName + "s cards: " + player.playerCards.size());
            for (Card card : player.playerCards) {
                logger.info("\t\t\t" + card.rank.designation + " (" + card.rank.value + ") " + card.suit.suitOfCard);
            }
        }
    }

    /**
     * Void method which prints every player name
     */
    public void printPlayerNames() {

        for (Player player : players) {
            logger.info(player.playerName);
        }
    }

    /**
     * Void method which deals cards between players
     */
    public void dealTheCards() {

        if (!players.isEmpty()) {
            Scanner scanner = new Scanner(System.in);
            logger.info("how much cards do u wanna deal to each player: ");

            int cardsToDeal = scanner.nextInt();
            while (cardsToDeal * players.size() > cards.size() || cardsToDeal <= 0) {
                logger.info("wrong number of cards to deal, define once again: ");
                cardsToDeal = scanner.nextInt();
            }
            for (int i = 0; i < cardsToDeal; i++) {
                for (Player player : players) {
                    player.playerCards.add(cards.remove(cards.size() - 1));
                }
            }
        }
        else {
            logger.info("cant deal cards");
        }
    }

    /**
     * Void method which collects cards from each player and put them back to the card heap list
     */
    public void collectTheCards() {

        if (!players.isEmpty()) {
            for (Player player : players) {
                for (int i = player.playerCards.size() - 1; i >= 0; i--) {
                    cards.add(player.playerCards.remove(i));
                }
            }
        }
    }

    /**
     * Shuffle cards in deck
     */
    public void shuffle() {

        ArrayList<Card> sorted = new ArrayList<>(this.cards);
        Card card;
        int first;
        int second;

        for (int i = 0; i < 100; i++) {
            first = this.random.nextInt(sorted.size());
            second = this.random.nextInt(sorted.size());
            while (first == second) {
                second = this.random.nextInt(sorted.size());
            }
            card = sorted.get(first);
            sorted.set(first, sorted.get(second));
            sorted.set(second, card);
        }

        cards = sorted;
    }

    /**
     * Method is used by setCards method
     */
    public void fabric() {

        ArrayList<Card> fabric = new ArrayList<>();

        for (Card.Suit suit : Card.Suit.values()) {
            for (Card.Rank rank : Card.Rank.values()) {
                fabric.add(new Card(suit.name(), rank.name()));
            }
        }
        cards = fabric;
    }
}
