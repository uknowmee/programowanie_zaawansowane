package com.company;

import org.junit.Test;

import static org.junit.Assert.*;

public class ReaderTest {

    @Test
    public void imWriter() {
        assertEquals("Im com.company.Reader", Reader.imReader());
    }
}
