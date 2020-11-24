/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.exception;

import java.io.IOException;

/**
 *
 * @author SuMario
 */
public class SocketException extends RuntimeException {

    public SocketException(String message) {
        super(message);
    }

    public SocketException(IOException io) {
        super(io);
    }

}
