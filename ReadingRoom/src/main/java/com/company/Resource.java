package com.company;

import java.util.Arrays;
import java.util.Random;

/**
 * Class which describes resource existing in reading room
 */
public class Resource {

    private final int[] array;
    private final Random random;
    private final ReadingRoom readingRoom;

    /**
     * Base resource constructor
     * @param readingRoom reading room in which resource exists
     */
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

    /**
     * returns true if reader could read resource
     * @param reader user who want to read
     * @return if he read true
     */
    boolean tryRead(Reader reader) {
        return readingRoom.read(reader);
    }

    /**
     * returns true if writer could write in the resource
     * @param writer user who want to write
     * @return if he wrote true
     */
    boolean tryWrite(Writer writer) {
        return readingRoom.write(writer);
    }

    @Override
    public String toString() {
        return "Resource=" + Arrays.toString(array);
    }

    /**
     * Function in which Writer change resource, should only be used by Reading room WriterControl function
     * @return old resource array
     */
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
