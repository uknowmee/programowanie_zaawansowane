package com.company;

import java.util.Arrays;
import java.util.Random;

public class Resource {

    private final int[] array;
    private final Random random;
    private final ReadingRoom readingRoom;

    Resource(ReadingRoom readingRoom) {
        this.array = new int[10];
        this.random = new Random();
        this.readingRoom = readingRoom;
    }

    public int[] getArray() {
        return array;
    }

    public ReadingRoom getReadingRoom() {
        return readingRoom;
    }

    boolean tryRead(Reader reader) {
        return readingRoom.read(reader);
    }

    boolean tryWrite(Writer writer) {
        return readingRoom.write(writer);
    }

    @Override
    public String toString() {
        return "Resource=" + Arrays.toString(array);
    }

    public int[] change() {
        int[] old = new int[10];

        System.arraycopy(array, 0, old, 0, 10);

        int index = random.nextInt(10);
        int newVal = random.nextInt(10);

        while (array[index] == newVal) {
            newVal = random.nextInt(10);
        }
        array[index] = newVal;

        return old;
    }
}
