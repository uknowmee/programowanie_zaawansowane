package com.company;

import org.apache.commons.codec.digest.Sha2Crypt;
import org.junit.Test;

import static org.junit.Assert.*;

public class CryptUtilTest {
    @Test
    public void add() {
        assertEquals(3.3 + 4.35, CryptUtil.add(3.3, 4.35), 0.0);
    }

    @Test
    public void sha512() {
        assertEquals(98, CryptUtil.sha512("hejka").length());
        assertEquals(98, CryptUtil.sha512("hasdejka").length());
        assertEquals(98, CryptUtil.sha512("hejdasdka").length());
        assertEquals(98, CryptUtil.sha512("hejdasdka").length());
    }
}
