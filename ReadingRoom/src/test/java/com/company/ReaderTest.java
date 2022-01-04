package com.company;

import org.junit.Test;

import static org.junit.Assert.*;

public class ReaderTest {

    @Test
    public void creation() {
        ReadingRoom readingRoom = new ReadingRoom(2, 2);
        Resource resource = new Resource(readingRoom);

        Reader reader = new Reader(resource, readingRoom);

        assertEquals(readingRoom, reader.getReadingRoom());
        assertEquals(resource, reader.getBook());
    }
}
