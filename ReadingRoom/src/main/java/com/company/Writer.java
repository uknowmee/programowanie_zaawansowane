package com.company;

public class Writer extends User {

    private final Resource book;
    private final ReadingRoom readingRoom;

    Writer(Resource book, ReadingRoom readingRoom) {
        super(book, readingRoom);
        this.book = book;
        this.readingRoom = readingRoom;
    }

    @Override
    public Resource getBook() {
        return book;
    }

    @Override
    public ReadingRoom getReadingRoom() {
        return readingRoom;
    }

    @Override
    protected int getSleep() {
        return (int) (Math.random() * 10);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(getSleep());
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
            book.tryWrite(this);
        }
    }
}
