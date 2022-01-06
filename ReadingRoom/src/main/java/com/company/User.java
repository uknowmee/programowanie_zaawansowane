package com.company;

/**
 * Class describing a user
 */
public class User extends Thread {

    protected final Resource book;
    protected final ReadingRoom readingRoom;

    /**
     * Base constructor
     * @param book which user wants to do action with
     * @param readingRoom in which user exists
     */
    User(Resource book, ReadingRoom readingRoom) {
        this.book = book;
        this.readingRoom = readingRoom;
    }

    public Resource getBook() {
        return book;
    }

    public ReadingRoom getReadingRoom() {
        return readingRoom;
    }

    protected int getSleep(){
        return (int) (Math.random() * 1000);
    }

    /**
     * specified for each user
     */
    @Override
    public void run() {
//        different for each type of user
    }
}
