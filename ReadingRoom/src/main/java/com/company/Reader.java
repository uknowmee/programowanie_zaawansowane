package com.company;

public class Reader extends Thread {

    private final Resource book;
    private final ReadingRoom readingRoom;

    Reader(Resource book, ReadingRoom readingRoom) {
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
        book.tryRead(this);
    }
}
