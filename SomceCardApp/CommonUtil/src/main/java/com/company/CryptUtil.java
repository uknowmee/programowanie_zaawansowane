package com.company;

import org.apache.commons.codec.digest.Sha2Crypt;

/**
 * Class generating sha-512
 */
public class CryptUtil {

    /**
     * static method to generate sha-512
     * @param value string_defined_by_user
     * @return string defined_by_user in sha-512 format
     */
    public String sha512(String value) {
        return Sha2Crypt.sha512Crypt(value.getBytes());
    }
}
