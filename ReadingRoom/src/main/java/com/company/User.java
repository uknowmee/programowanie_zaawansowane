package com.company;

public class User extends Thread {

    private final Resource book;
    private final ReadingRoom readingRoom;

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

    @Override
    public void run() {
//        different for each type of user
    }
}
