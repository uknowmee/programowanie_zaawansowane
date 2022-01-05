package com.company;

import org.junit.Test;

import static org.junit.Assert.*;

public class ReadingRoomTest {

    @Test
    public void twoAndTwoStartTest() {
        ReadingRoom readingRoom = new ReadingRoom(2, 2);

        assertFalse(readingRoom.isReading());
        assertFalse(readingRoom.isWriting());

        assertEquals(4, readingRoom.getUsers().size());
        assertEquals(0, readingRoom.getNumOfReaders());
        assertEquals(0, readingRoom.getNumOfWriters());
        readingRoom.start();
    }
}
