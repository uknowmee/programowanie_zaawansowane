package com.company;

public class Writer extends Thread {

    private final Resource book;
    private final ReadingRoom readingRoom;

    Writer(Resource book, ReadingRoom readingRoom) {
        this.book = book;
        this.readingRoom = readingRoom;
    }

    public Resource getBook() {
        return book;
    }

    public ReadingRoom getReadingRoom() {
        return readingRoom;
    }

    @Override
    public void run() {
        book.tryWrite(this);
    }
}
