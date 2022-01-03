package com.company;

import org.junit.Test;

import static org.junit.Assert.*;

public class WriterTest {

    @Test
    public void imWriter() {
        assertEquals("Im com.company.Writer", Writer.imWriter());
    }
}
