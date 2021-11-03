package com.company;


import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;


/**
 * class representing classic Deck
 */
public class Deck {

    /**
     * List of Cards (card heap)
     */
    private ArrayList<Card> cards;

    /**
     * List of Players
     */
    private final ArrayList<Player> players;

    /**
     * Class describing a single Card
     */
    private static class Card {

        private final Rank rank;
        private final Suit suit;

        private enum Rank{

            Dziewiatka("Dziewiatka",9),
            Dziesiatka("Dziesiatka",10),
            Walet("Walet",11),
            Dama("Dama",12),
            Krol("Krol",13),
            As("As",14);

            private final int value;

            Rank(String designation, int value) {
                this.value = value;
            }
        }
        private enum Suit{

            Pik("Pik"),
            Trefl("Trefl"),
            Kier("Kier"),
            Karo("Karo");

            Suit(String suitToSet) {}
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Card)) {
                return false;
            }

            Card card = (Card) o;

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

        private Card(String suitToSet, String rankToSet){

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

        private Player(String playerName){

            this.playerName = playerName;
            this.playerCards = new ArrayList<>();
        }
    }

    /**
     * Void method which set decks cards list
     * @param cards - can be obtained from shuffle or fabric methods
     */
    public void setCards(ArrayList<Card> cards){
        this.cards = cards;
    }

    /**
     * Void method which prints whole deck, list of cards, players and certain player cards
     */
    public void printDeck(){

        System.out.println("\tcards on heap: " + cards.size());
        for (Card card : cards) {
//            System.out.printf("""
//                    \t\t%s  (%d)    %s
//                    """, card.rank.name(), card.rank.value, card.suit.name());
            System.out.println("\t\t\t" + card.rank.name() + " (" + card.rank.value + ") " + card.suit.name());
        }

        System.out.println("\tcards in players hands: " + (24 - cards.size()));
        for (Player player : players) {
            System.out.println("\t\t" + player.playerName + "s cards: " + player.playerCards.size());
            for (Card card : player.playerCards) {
//                System.out.printf("""
//                    \t\t\t%s  (%d)    %s
//                    """, card.rank.name(), card.rank.value, card.suit.name());
                System.out.println("\t\t\t" + card.rank.name() + " (" + card.rank.value + ") " + card.suit.name());
            }
        }
    }

    /**
     * Void method which prints every player name
     */
    public void printPlayerNames() {

        for (Player player : players) {
            System.out.println(player.playerName);
        }
    }

    /**
     * Void method which can be used to add new player
     */
    public void addPlayer() {

        Scanner scanner = new Scanner(System.in);
        System.out.println("name the player: ");
        players.add(new Player(scanner.next()));
    }

    /**
     * Void method which deals cards between players
     */
    public void dealTheCards() {

        if (players.size()!=0) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("how much cards do u wanna deal to each player: ");

            int cardsToDeal = scanner.nextInt();
            while (cardsToDeal*players.size() > cards.size() || cardsToDeal <=0){
                System.out.println("wrong number of cards to deal, define once again: ");
                cardsToDeal = scanner.nextInt();
            }
            for (int i = 0; i < cardsToDeal; i++) {
                for (Player player : players) {
                    player.playerCards.add(cards.remove(cards.size()-1));
                }
            }
        }
        else {
            System.out.println("cant deal cards");
        }
    }

    /**
     * Void method which collects cards from each player and put them back to the card heap list
     */
    public void collectTheCards() {

        if (players.size()!=0) {
            for (Player player : players) {
                for (int i = player.playerCards.size()-1; i >= 0; i--) {
                    cards.add(player.playerCards.remove(i));
                }
            }
        }
    }

    /**
     * Method is used by setCards method
     * @return ArrayList of all cards
     */
    public ArrayList<Card> shuffle() {

        ArrayList<Card> sorted = new ArrayList<>(this.cards);
        Card card;
        Random random = new Random();
        int first;
        int second;

        for (int i = 0; i < 100; i++) {
            first = random.nextInt(24);
            second = random.nextInt(24);
            while (first == second){
                second = random.nextInt(24);
            }
            card = sorted.get(first);
            sorted.set(first, sorted.get(second));
            sorted.set(second, card);
        }

        return sorted;
    }

    /**
     * Method is used by setCards method
     * @return ArrayList of all cards
     */
    public ArrayList<Card> fabric() {

        ArrayList<Card> fabric = new ArrayList<>();
        fabric.ensureCapacity(24);

        for (Card.Suit suit : Card.Suit.values()) {
            for (Card.Rank rank : Card.Rank.values()) {
                fabric.add(new Card(suit.name(), rank.name()));
            }
        }
        return fabric;
    }

    /**
     * Base constructor
     */
    public Deck() {

        this.players = new ArrayList<>();
        this.cards = new ArrayList<>();
        this.cards.ensureCapacity(24);
        this.cards = this.fabric();
    }

    /**
     * Special constructor which sets new Deck using the given list of cards
     * @param cards list of Cards from shuffle or fabric method
     */
    public Deck(ArrayList<Card> cards) {

        this.cards = cards;
        this.players = new ArrayList<>();
    }
}
