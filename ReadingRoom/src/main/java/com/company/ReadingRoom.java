package com.company;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReadingRoom {

    static final Logger logger = Logger.getLogger(ReadingRoom.class.getName());
    private final Resource book;
    private final int numOfReaders;
    private final int numOfWriters;
    private boolean writing;
    private boolean reading;
    private final List<Reader> readers;
    private final List<Writer> writers;

    ReadingRoom(int numOfReaders, int numOfWriters) {
        this.book = new Resource(this);
        this.numOfReaders = numOfReaders;
        this.numOfWriters = numOfWriters;
        this.writing = false;
        this.reading = false;
        this.readers = new ArrayList<>();
        this.writers = new ArrayList<>();
    }

    public boolean read(Reader reader) {
        logger.info(reader.getName() + " read: " + book + "\n\n");
        return true;
    }

    public boolean write(Writer writer) {
        logger.info(writer.getName() + " changed from: \n\t\t\t\t Resource=" + Arrays.toString(book.change()) + "\n\t\t\t\t to:\n\t\t\t\t " + book + "\n\n");
        return true;
    }

    public void start() {
        for (int i = 0; i < numOfReaders; i++) {
            new Reader(book, this).start();
        }
        for (int i = 0; i < numOfWriters; i++) {
            new Writer(book, this).start();
        }
    }
}
