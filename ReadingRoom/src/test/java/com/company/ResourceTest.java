package com.company;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

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
}
