package com.company;

import org.apache.log4j.Logger;

/**
 * Reader class
 */
public class Reader extends User {

    static final Logger logger = Logger.getLogger(Reader.class.getName());

    /**
     * Base constructor
     * @param book which user wants to read from
     * @param readingRoom in which user exists
     */
    Reader(Resource book, ReadingRoom readingRoom) {
        super(book, readingRoom);
    }

    /**
     * function which made reader constantly in "need of reading" a book
     */
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
