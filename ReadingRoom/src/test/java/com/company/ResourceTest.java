package com.company;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class ResourceTest {

    @Test
    public void fresh() {

        ReadingRoom readingRoom = new ReadingRoom(2, 2);
        Resource source = new Resource(readingRoom);

        assertEquals(10, source.getArray().length);
        assertEquals(readingRoom, source.getReadingRoom());
        assertEquals("Resource=" + Arrays.toString(new int[10]), source.toString());
    }

    @Test
    public void change() {

        ReadingRoom readingRoom = new ReadingRoom(2, 2);
        Resource source = new Resource(readingRoom);
        int i = 10000;

        while (i > 0) {
            assertNotEquals(source.change(), source.getArray());
            i--;
        }
    }

    @Test
    public void resourceChanges() {
        ReadingRoom readingRoom = new ReadingRoom(12, 3);
        Resource resource = new Resource(readingRoom);

        Reader reader1 = new Reader(resource, readingRoom);

        Writer writer1 = new Writer(resource, readingRoom);

        assertTrue(resource.tryRead(reader1));
        assertTrue(resource.tryWrite(writer1));

        readingRoom.setWriting(true);
        assertFalse(resource.tryRead(reader1));
        assertFalse(resource.tryWrite(writer1));

        readingRoom.setWriting(false);
        readingRoom.setReading(true);
        readingRoom.setNumOfReaders(2);
        assertTrue(resource.tryRead(reader1));

        readingRoom.setReading(true);
        assertEquals(2, readingRoom.getNumOfReaders());
        assertFalse(resource.tryWrite(writer1));
    }
}
