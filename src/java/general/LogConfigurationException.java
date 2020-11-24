/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general;

/**
 *
 * @author SuMario
 */
public class LogConfigurationException extends Exception {

    LogConfigurationException(String msg, IllegalAccessException e) {
        throw new RuntimeException(msg+" - reason:"+e.getMessage());
    }

    LogConfigurationException(Exception e) {
        throw new RuntimeException("exception - reason:"+e.getMessage());
    }
    
}
