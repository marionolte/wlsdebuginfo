/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 


package general;

/**
 * 

A simple logging interface abstracting logging APIs.  In order to be
 * instantiated successfully by {@link LogFactory}, classes that implement
 * this interface must have a constructor that takes a single String
 * parameter representing the "name" of this Log.

 *
 * 

 The six logging levels used by Log are (in order):
 * 


     * 
    trace (the least serious)

     * 
    debug

     * 
    info

     * 
    warn

     * 
    error

     * 
    fatal (the most serious)

     * 


 * The mapping of these log levels to the concepts used by the underlying
 * logging system is implementation dependent.
 * The implemention should ensure, though, that this ordering behaves
 * as expected.


 *
 * 

Performance is often a logging concern.
 * By examining the appropriate property,
 * a component can avoid expensive operations (producing information
 * to be logged).

 *
 * 

 For example,
 * 

 *    if (log.isDebugEnabled()) {
 *        ... do something expensive ...
 *        log.debug(theResult);
 *    }
 * 


 * 


 *
 * 

Configuration of the underlying logging system will generally be done
 * external to the Logging APIs, through whatever mechanism is supported by
 * that system.

 *
 * @author Scott Sanders
 * @author Rod Waldhoff
 * @version $Id: Log.java,v 1.19 2004/06/06 21:16:04 rdonkin Exp $
 */
public interface Log {


    // ----------------------------------------------------- Logging Properties


    /**
     * 

 Is debug logging currently enabled? 

     *
     * 

 Call this method to prevent having to perform expensive operations
     * (for example, String concatenation)
     * when the log level is more than debug. 

     */
    public boolean isDebugEnabled();


    /**
     * 

 Is error logging currently enabled? 

     *
     * 

 Call this method to prevent having to perform expensive operations
     * (for example, String concatenation)
     * when the log level is more than error. 

     */
    public boolean isErrorEnabled();


    /**
     * 

 Is fatal logging currently enabled? 

     *
     * 

 Call this method to prevent having to perform expensive operations
     * (for example, String concatenation)
     * when the log level is more than fatal. 

     */
    public boolean isFatalEnabled();


    /**
     * 

 Is info logging currently enabled? 

     *
     * 

 Call this method to prevent having to perform expensive operations
     * (for example, String concatenation)
     * when the log level is more than info. 

     */
    public boolean isInfoEnabled();


    /**
     * 

 Is trace logging currently enabled? 

     *
     * 

 Call this method to prevent having to perform expensive operations
     * (for example, String concatenation)
     * when the log level is more than trace. 

     */
    public boolean isTraceEnabled();


    /**
     * 

 Is warn logging currently enabled? 

     *
     * 

 Call this method to prevent having to perform expensive operations
     * (for example, String concatenation)
     * when the log level is more than warn. 

     */
    public boolean isWarnEnabled();


    // -------------------------------------------------------- Logging Methods


    /**
     * 

 Log a message with trace log level. 

     *
     * @param message log this message
     */
    public void trace(Object message);


    /**
     * 

 Log an error with trace log level. 

     *
     * @param message log this message
     * @param t log this cause
     */
    public void trace(Object message, Throwable t);


    /**
     * 

 Log a message with debug log level. 

     *
     * @param message log this message
     */
    public void debug(Object message);


    /**
     * 

 Log an error with debug log level. 

     *
     * @param message log this message
     * @param t log this cause
     */
    public void debug(Object message, Throwable t);


    /**
     * 

 Log a message with info log level. 

     *
     * @param message log this message
     */
    public void info(Object message);


    /**
     * 

 Log an error with info log level. 

     *
     * @param message log this message
     * @param t log this cause
     */
    public void info(Object message, Throwable t);


    /**
     * 

 Log a message with warn log level. 

     *
     * @param message log this message
     */
    public void warn(Object message);


    /**
     * 

 Log an error with warn log level. 

     *
     * @param message log this message
     * @param t log this cause
     */
    public void warn(Object message, Throwable t);


    /**
     * 

 Log a message with error log level. 

     *
     * @param message log this message
     */
    public void error(Object message);


    /**
     * 

 Log an error with error log level. 

     *
     * @param message log this message
     * @param t log this cause
     */
    public void error(Object message, Throwable t);


    /**
     * 

 Log a message with fatal log level. 

     *
     * @param message log this message
     */
    public void fatal(Object message);


    /**
     * 

 Log an error with fatal log level. 

     *
     * @param message log this message
     * @param t log this cause
     */
    public void fatal(Object message, Throwable t);


}

