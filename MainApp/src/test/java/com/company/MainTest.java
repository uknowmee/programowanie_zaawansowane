package com.company;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.Assert.*;

public class MainTest {

    @Test
    public void scanNumberTest() {

        String input = "0";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        assertEquals(0, Main.scanNumber());

        input = "sdfds";
        in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        assertEquals(0, Main.scanNumber());

        input = "2";
        in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        assertEquals(2, Main.scanNumber());

        input = "-2";
        in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        assertEquals(0, Main.scanNumber());
    }

    @Test
    public void mainRun() {
        String input = "3";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        assertEquals("3", input);

        Main.main(new String[]{"test"});
    }

}
