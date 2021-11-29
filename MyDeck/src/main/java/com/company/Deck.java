package com.company;


import org.apache.log4j.Logger;

import java.util.*;


/**
 * Class representing classic Deck, 24 cords existing players.<br>
 * It can run poker  draw game with 2, 3 or 4 players.
 */
public class Deck {
    private final Random random;
    private final Logger logger;

    private final String name;
    private final ArrayList<Player> players;
    private final int numOfPlayers;
    private ArrayList<Card> cards;
    private boolean started;
    private int bank;
    private int bid;
    private boolean all;
    private Response response;

    /**
     * Class describing a single Card
     */
    protected static class Card {
        private final Rank rank;
        private final Suit suit;
        private final int num;

        /**
         * Enum representing the rank of single card
         */
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

        /**
         * Enum representing color of the single card
         */
        private enum Suit {

            PIK("Pik", 0),
            TREFL("Trefl",1),
            KIER("Kier",2),
            KARO("Karo",3);

            private final String suitOfCard;
            private final int numOfColor;

            Suit(String suitToSet, int numOfColor) {
                this.suitOfCard = suitToSet;
                this.numOfColor = numOfColor;
            }
        }

        public int getRank() {
            return rank.value;
        }

        public int getSuit() {
            return suit.numOfColor;
        }

        /**
         * Checks if 2 cards are equal
         *
         * @param o {@link Card} - card to be compared with this
         * @return ret Boolean - true if they are equals
         */
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
        public String toString() {
            return this.rank + " (" + this.rank.value + ") " + this.suit;
        }

        /**
         * Base constructor
         *
         * @param suitToSet {@link String} - suit which card will be connected with
         * @param rankToSet {@link String} - rank which card will be connected with
         */
        private Card(String suitToSet, String rankToSet, int num) {
            this.rank = Rank.valueOf(rankToSet);
            this.suit = Suit.valueOf(suitToSet);
            this.num = num;
        }
    }

    /**
     * Class describing a single Player
     */
    private static class Player {

        private final ArrayList<Card> playerCards;
        private final String playerName;
        private boolean fold;
        private boolean check;
        private boolean call;
        private boolean bet;
        private boolean raise;
        private boolean all;
        private boolean turn;
        private final boolean kicked;
        private int credit;
        private int diff;
        private int points;
        private int type;


        /**
         * Base constructor
         *
         * @param playerName {@link String} - name of the player
         */
        protected Player(String playerName) {
            this.playerName = playerName;
            this.playerCards = new ArrayList<>();
            this.credit = 100;
            this.fold = false;
            this.check = false;
            this.call = false;
            this.bet = false;
            this.raise = false;
            this.turn = false;
            this.kicked = false;
        }

        /**
         *
         * @return
         */
        public ArrayList<Card> getPlayerCards() {
            return playerCards;
        }

        /**
         * @return
         */
        public boolean isKicked() {
            return kicked;
        }

        /**
         * @param value
         * @return
         */
        private int reduceCredit(int value) {
            credit -= value;
            return credit;
        }

        /**
         * @return
         */
        @Override
        public String toString() {
            return playerName;
        }
    }

    /**
     * Static response from Deck with running game to server
     */
    static class Response {
        private int round;
        private int part;
        private ArrayList<Player> players;
        private ArrayList<Player> playing;
        private ArrayList<Player> notPlaying;
        private String whoLast;
        private String whoNow;
        private String whoNext;
        private Boolean moveAccepted;
        private String winner;
        private String lastTried;
        private long time;

        /**
         *
         */
        Response() {
            this.round = 0;
            this.part = 0;
            this.moveAccepted = false;
            this.winner = "";
            this.lastTried = "";
        }

        /**
         * @param players
         */
        Response(ArrayList<Player> players) {
            this.round = 1;
            this.part = 1;
            this.players = players;
            this.playing = new ArrayList<>(players);
            this.notPlaying = new ArrayList<>();
            this.whoLast = "";
            this.whoNow = this.players.get(0).playerName;
            this.whoNext = this.players.get(1).playerName;
        }

        public int getRound() {
            return round;
        }

        public int getPart() {
            return part;
        }

        public ArrayList<Player> getPlayers() {
            return players;
        }

        public ArrayList<Player> getPlaying() {
            return playing;
        }

        public ArrayList<String> getPlayingNames() {
            ArrayList<String> playerNames = new ArrayList<>();
            for (Player player : playing) {
                playerNames.add(player.playerName);
            }
            return playerNames;
        }

        public ArrayList<Player> getNotPlaying() {
            return notPlaying;
        }

        public String getWhoLast() {
            return whoLast;
        }

        public String getWhoNow() {
            return whoNow;
        }

        public String getWhoNext() {
            return whoNext;
        }

        public Boolean getMoveAccepted() {
            return moveAccepted;
        }

        public String getWinner() {
            return winner;
        }

        public String getLastTried() {
            return lastTried;
        }

        public long getTime() {
            return time;
        }

        /**
         *
         */
        public void skipping() {
            while (playing.get(0).fold) {
                Player player = playing.get(0);
                playing.remove(0);
                playing.add(player);
            }
            whoNow = playing.get(0).playerName;
            if (playing.size() == 1) {
                whoNext = playing.get(0).playerName;
            }
            else {
                whoNext = playing.get(1).playerName;
            }
        }

        private void update(Boolean accepted) {
            moveAccepted = accepted;
            if (Boolean.TRUE.equals(accepted)) {
                Player player = playing.get(0);
                playing.remove(0);
                whoLast = player.playerName;
                if (player.credit >= 0) {
                    playing.add(player);
                }
                else {
                    notPlaying.add(player);
                }
                skipping();
                playing.get(0).turn = true;
            }
        }

        /**
         * @param userName
         * @param message
         * @param accepted
         */
        private void playerUpdate(String userName, String message, Boolean accepted) {
            update(accepted);
            lastTried = userName + ": " + message;
            time = System.currentTimeMillis();
        }

        /**
         * Returns response as string
         *
         * @return ret String - response represented by string
         */
        @Override
        public String toString() {
            return "last: " + whoLast + ", now: " + whoNow + ", next: " + whoNext;
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

        this.bid = 0;
        this.bank = 0;
        this.all = false;

        this.started = false;
    }

    /**
     * Returns deck response as string
     *
     * @return ret String - deck response represented by string
     */
    public String getResponseString() {
        return response.toString() + ", bank: " + bank + ", bid: " + bid;
    }

    /**
     * Returns deck response as class object
     *
     * @return {@link #response} {@link Response} - object representing response of the deck
     */
    public Response getResponse() {
        return response;
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
     * @param name
     * @return
     */
    public String getPlayingCardsFromName(String name) {
        StringBuilder ret = new StringBuilder();
        for (Player player : response.playing) {
            if (player.playerName.equals(name)) {
                for (Card card : player.getPlayerCards()) {
                    ret.append("\t").append(card).append("\n");
                }
                return ret.toString();
            }
        }
        return null;
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
     * @param player
     * @return
     */
    public Boolean isKicked(String player) {
        for (Player pl : players) {
            if (pl.playerName.equals(player)) {
                return pl.isKicked();
            }
        }
        return false;
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
     *
     * @return {@link #started} Boolean - true if game started
     */
    public boolean isStarted() {
        return started;
    }

    /**
     * @return
     */
    public int getBank() {
        return bank;
    }

    /**
     * @return
     */
    public int getBid() {
        return bid;
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
     *
     * @param started Boolean - this.started to set
     */
    public void setStarted(boolean started) {
        this.started = started;
    }

    /**
     * Returns deck as string
     *
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

    /**
     * @param userName
     */
    public void playerJoin(String userName) {
        players.add(new Player(userName));
    }

    /**
     * @param userName
     */
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
     * Void method which deals cards between players
     */
    public void dealTheCards() {

        if (!response.playing.isEmpty()) {
            int cardsToDeal = 5;
            for (int i = 0; i < cardsToDeal; i++) {
                for (Player player : response.playing) {
                    player.playerCards.add(cards.remove(cards.size() - 1));
                }
            }
        }
    }

    /**
     *
     */
    public void sortCards() {
        Card card;
        for (Player player : response.playing) {
            for (int i = 0; i < player.playerCards.size() - 1; i++) {
                for (int j = 0; j < player.playerCards.size() - 1 - i; j++) {
                    if (player.playerCards.get(j).num > player.playerCards.get(j+1).num) {
                        card = player.playerCards.get(j+1);
                        player.playerCards.set(j+1, player.playerCards.get(j));
                        player.playerCards.set(j, card);
                    }
                }
            }
        }
    }

    /**
     * Void method which collects cards from each player and put them back to the card heap list
     */
    public void collectTheCards() {

        if (!response.playing.isEmpty()) {
            for (Player player : response.playing) {
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

        int num = 0;
        ArrayList<Card> fabric = new ArrayList<>();
        for (Card.Rank rank : Card.Rank.values()) {
            for (Card.Suit suit : Card.Suit.values()) {
                fabric.add(new Card(suit.name(), rank.name(), num));
                num++;
            }
        }
        cards = fabric;
    }

    public Response fold(String userName, Split split) {
        if (response.playing.get(0).credit > 0) {
            response.playing.get(0).fold = true;
            response.playing.get(0).turn = false;
            response.playerUpdate(userName, split.getCommand(), true);

        }
        else {
            response.playerUpdate(userName, split.getCommand() + " [not allowed]", false);
        }
        return response;
    }

    public Response check(String userName, Split split) {
        if (bid == 0 && response.playing.get(0).credit > 0) {
            response.playing.get(0).check = true;
            response.playing.get(0).turn = false;
            response.playerUpdate(userName, split.getCommand(), true);
        }
        else {
            response.playerUpdate(userName, split.getCommand() + " [not allowed]", false);
        }
        return response;
    }

    public Response call(String userName, Split split) {
        if (response.playing.get(0).credit > bid && bid != 0) {
            response.playing.get(0).call = true;
            response.playing.get(0).turn = false;
            response.playing.get(0).credit -= response.playing.get(0).diff;
            bank += response.playing.get(0).diff;
            response.playing.get(0).diff = 0;
            response.playerUpdate(userName, split.getCommand(), true);
        }
        else {
            response.playerUpdate(userName, split.getCommand() + " [not allowed]", false);
        }
        return response;
    }

    public Response bet(String userName, Split split) {
        if (bid == 0 && response.playing.get(0).credit > 2) {
            int myInt;
            try {
                myInt = Integer.parseInt(split.getMessage());
                if (2 < myInt && myInt < response.playing.get(0).credit) {
                    bid = myInt;
                    bank += myInt;
                    response.playing.get(0).credit -= myInt;
                    response.playing.get(0).bet = true;
                    response.playing.get(0).turn = false;
                    for (int i = 1; i < response.playing.size(); i++) {
                        if (!response.playing.get(i).fold) {
                            response.playing.get(i).diff += myInt;
                            response.playing.get(i).check = false;
                        }
                    }
                    response.playerUpdate(userName, split.getCommand() + " " + myInt, true);
                }
                else if (myInt <= 2) {
                    response.playerUpdate(userName, myInt + " is too small", false);
                }
                else {
                    response.playerUpdate(userName, myInt + " is too big", false);

                }
            } catch (Exception e) {
                response.playerUpdate(userName, "wrong number: " + split.getMessage(), false);
            }
        }
        else {
            response.playerUpdate(userName, split.getCommand() + " [not allowed]", false);
        }
        return response;
    }

    public Response raise(String userName, Split split) {
        if (bid != 0 && response.playing.get(0).credit > 2 * bid && !all) {
            int myInt;
            try {
                myInt = Integer.parseInt(split.getMessage());
                if (2 * bid < myInt && myInt < response.playing.get(0).credit) {
                    bank += myInt;
                    response.playing.get(0).credit -= myInt;
                    response.playing.get(0).raise = true;
                    response.playing.get(0).turn = false;
                    for (int i = 1; i < response.playing.size(); i++) {
                        if (!response.playing.get(i).fold) {
                            response.playing.get(i).diff += myInt - bid;
                            response.playing.get(i).call = false;
                            response.playing.get(i).check = false;
                            response.playing.get(i).bet = false;
                            response.playing.get(i).raise = false;
                        }
                    }
                    bid = myInt;
                    response.playerUpdate(userName, split.getCommand() + " " + myInt, true);
                }
                else if (myInt <= 2) {
                    response.playerUpdate(userName, split.getCommand() + " " + myInt + " [is too small]", false);
                }
                else if (myInt >= response.playing.get(0).credit) {
                    response.playerUpdate(userName, split.getCommand() + " " + myInt + " [is too big]", false);

                }
            } catch (Exception e) {
                response.playerUpdate(userName, split.getCommand() + " " + split.getMessage() + " [wrong number]", false);
            }
        }
        else {
            response.playerUpdate(userName, split.getCommand() + " [not allowed]", false);
        }
        return response;
    }

    public Response all(String userName, Split split) {
        if (response.playing.get(0).credit - bid <= 0 && bid != 0) {
            response.playing.get(0).all = true;
            response.playing.get(0).turn = false;
            bank += response.playing.get(0).credit;
            response.playing.get(0).credit = 0;
            all = true;
            response.playerUpdate(userName, split.getCommand(), true);
        }
        else {
            response.playerUpdate(userName, split.getCommand() + " [not allowed]", false);
        }
        return response;
    }

    public Response cya(String userName, Split split) {
        if (response.playing.get(0).credit < 0) {
            response.playerUpdate(userName, split.getCommand(), true);
        }
        else {
            response.playerUpdate(userName, split.getCommand() + " [not allowed]", false);

        }
        return response;
    }

    public Response exchange(String userName, Split split) {
        return response;
    }

    /**
     * @param userName
     * @param text
     * @return
     */
    public Response updateResponse(String userName, String text) {

        if (response.playing.size() == 1) {
            response.winner = response.playing.get(0).playerName;
            return response;
        }

        Split split = new Split(text);

        for (Player pl : response.playing) {
            if (pl.playerName.equals(userName) && pl.turn) {
                switch (split.getCommand()) {
                    case "\\fold" -> fold(userName, split);
                    case "\\check" -> check(userName, split);
                    case "\\call" -> call(userName, split);
                    case "\\bet" -> bet(userName, split);
                    case "\\raise" -> raise(userName, split);
                    case "\\all" -> all(userName, split);
                    case "\\cya" -> cya(userName, split);
                    case "\\exchange" -> exchange(userName, split);
                    default -> response.playerUpdate(userName, text, false);

                }
                break;
            }
            else if (pl.playerName.equals(userName)) {
                if (Arrays.asList("\\fold", "\\call", "\\bet",
                        "\\raise", "\\all", "\\cya", "\\exchange").contains(split.getCommand())) {

                    response.playerUpdate(userName, text + " [NOT YOUR TURN]", false);
                }
                else if (Objects.equals("\\info", split.getCommand())) {
                    response.playerUpdate(userName, text, false);
                }
            }
        }
        return response;
    }

    /**
     *
     */
    public void startResponse() {
        this.bank += this.players.size() * 2;
        for (Player player : players) {
            player.credit -= 2;
        }
        players.get(0).turn = true;
        this.response = new Response(players);
        shuffle();
        dealTheCards();
        sortCards();

        for (Player pl : response.playing) {
            Evaluate evaluate = new Evaluate(pl.playerCards);
            evaluate.eval();
            pl.points = evaluate.getPoints();
            pl.type = evaluate.getType();
        }

        this.response.time = System.currentTimeMillis();
    }
}
