package com.company;

import org.apache.log4j.Logger;

public class Reader extends User {

    static final Logger logger = Logger.getLogger(ReadingRoom.class.getName());
    private final Resource book;
    private final ReadingRoom readingRoom;

    Reader(Resource book, ReadingRoom readingRoom) {
        super(book, readingRoom);
        this.book = book;
        this.readingRoom = readingRoom;
    }

    @Override
    public void run() {
        logger.info("Created " + getClass().getName() + " in the: " + readingRoom + " who wants to use: " + book);

        while (true) {
            try {
                Thread.sleep(getSleep());
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
            book.tryRead(this);
        }
    }
}
