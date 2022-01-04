package com.company;

import org.junit.Test;

import static org.junit.Assert.*;

public class WriterTest {

    @Test
    public void creation() {
        ReadingRoom readingRoom = new ReadingRoom(2, 2);
        Resource resource = new Resource(readingRoom);

        Writer writer = new Writer(resource, readingRoom);

        assertEquals(readingRoom, writer.getReadingRoom());
        assertEquals(resource, writer.getBook());
    }
}
