package com.company;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Main reading room class
 */
public class ReadingRoom {

    static final Logger logger = Logger.getLogger(ReadingRoom.class.getName());
    private final Resource book;
    private int numOfReaders;
    private int numOfWriters;
    private boolean writing;
    private boolean reading;
    private boolean noMoreReaders;
    private final List<User> users;
    private static final String READER = "Reader ";
    private static final String WRITER = "Writer ";

    /**
     * Base constructor of reading room
     * @param numOfReaders numbers of readers to be created
     * @param numOfWriters numbers of writers to be created
     */
    ReadingRoom(int numOfReaders, int numOfWriters) {
        this.book = new Resource(this);
        this.numOfReaders = 0;
        this.numOfWriters = 0;
        this.writing = false;
        this.reading = false;
        this.noMoreReaders = false;
        this.users = new ArrayList<>();

        User user;

        for (int i = 0; i < numOfReaders; i++) {
            user = new Reader(book, this);
            users.add(user);
        }
        for (int i = 0; i < numOfWriters; i++) {
            user = new Writer(book, this);
            users.add(user);
        }
    }

    public boolean isWriting() {
        return writing;
    }

    public boolean isReading() {
        return reading;
    }

    public List<User> getUsers() {
        return users;
    }

    public int getNumOfReaders() {
        return numOfReaders;
    }

    public int getNumOfWriters() {
        return numOfWriters;
    }

    private int getSleep() {
        return (int) (Math.random() * 2000);
    }

    public void setNumOfReaders(int numOfReaders) {
        this.numOfReaders = numOfReaders;
    }

    public void setWriting(boolean writing) {
        this.writing = writing;
    }

    public void setReading(boolean reading) {
        this.reading = reading;
    }

    /**
     * Functions in which possibility of entering the room by reader is checked, is also provides w8ing inside the queue for entering the room
     * @param reader Reading room user
     * @return true if he read otherwise false
     */
    public boolean read(Reader reader) {
        synchronized (this) {
            if (writing) {
                return false;
            }

            if (numOfReaders > 2 && !noMoreReaders) {
                noMoreReaders = true;
            }

            while (noMoreReaders) {
                try {
                    this.wait();
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
            }

            logger.info(READER + reader.getName() + " entered the room with " + numOfReaders + " other readers.");
            numOfReaders++;
            reading = true;
        }

        try {
            Thread.sleep((long) 1000 + getSleep());
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
        logger.info(READER + reader.getName() + " read: " + book);

        synchronized (this) {
            numOfReaders--;
            logger.info(READER + reader.getName() + " has finished reading");
            if (numOfReaders == 0) {
                reading = false;
                noMoreReaders = false;
            }
        }
        return true;
    }

    /**
     * Controls if specified writer can update resource in current time
     * @param writer Reading room user
     * @return true if he updated otherwise false
     */
    public boolean write(Writer writer) {

        synchronized (this) {
            if (reading || writing) {
                return false;
            }

            numOfWriters++;
            writing = true;
            logger.info(WRITER + writer.getName() + " has begun writing");

            synchronized (book) {
                boolean slept = false;
                while (!slept) {
                    try {
                        book.wait((long) 1000 + getSleep());
                        slept = true;
                    } catch (InterruptedException ignored) {
                        Thread.currentThread().interrupt();
                    }
                }
            }

            logger.info(WRITER + writer.getName() + " changed from: \n\t\t\t\t Resource=" + Arrays.toString(book.change()) + "\n\t\t\t\t to:\n\t\t\t\t " + book);

            numOfWriters--;
            writing = false;
            logger.info(WRITER + writer.getName() + " has finished writing");
        }
        return true;
    }

    /**
     * Starts each reading room user Thread
     */
    public void start() {
        for (User user : users) {
            user.start();
        }
    }
}
