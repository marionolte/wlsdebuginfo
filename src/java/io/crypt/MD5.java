/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.crypt;

import general.Version;
import java.security.MessageDigest;


/**
 *
 * @author SuMario
 */
public class MD5 extends Version{
    
    
    /** Hexadecimal characters for MD5 encoding. */
    private static final char[] HEXADECIMAL = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    static {
        
    }

    
    public static String toMD5Hash(String plaintext) {
        if (plaintext == null) {
            throw new IllegalArgumentException("Null plaintext parameter");
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            md.update(plaintext.getBytes("UTF-8"));

            byte[] binaryData = md.digest();

            char[] buffer = new char[32];

            for (int i = 0; i < 16; i++) {
                int low = (int) (binaryData[i] & 0x0f);
                int high = (int) ((binaryData[i] & 0xf0) >> 4);
                buffer[i * 2] = HEXADECIMAL[high];
                buffer[i * 2 + 1] = HEXADECIMAL[low];
            }

            return new String(buffer);

        } catch (Exception e) {
            throw new RuntimeException(e);

        }
    }
    
    public static void main(String[] args) {
           System.out.println("MD5  :"+toMD5Hash(args[0])+":");
    }
}   
