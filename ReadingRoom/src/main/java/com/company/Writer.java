package com.company;

import org.apache.log4j.Logger;

/**
 * Writer class
 */
public class Writer extends User {

    static final Logger logger = Logger.getLogger(Writer.class.getName());

    /**
     * Base constructor
     * @param book which user wants to edit
     * @param readingRoom in which user exists
     */
    Writer(Resource book, ReadingRoom readingRoom) {
        super(book, readingRoom);
    }

    /**
     * function which made writer constantly in "need of editing" a book
     */
    @Override
    public void run() {
        logger.info("Created " + getClass().getName() + "in the: " + readingRoom + "who wants to use: " + book);

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
