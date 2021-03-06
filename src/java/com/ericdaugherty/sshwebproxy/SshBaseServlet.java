package com.ericdaugherty.sshwebproxy;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import static general.Version.log;

/**
 * Common Servlet base class.  Implements common functions.
 *
 * @author Eric Daugherty
 */
public class SshBaseServlet extends HttpServlet implements SshConstants {

    //***************************************************************
    // Variables
    //***************************************************************

    /** Logger */
    //private static final Log log = LogFactory.getLog( SshBaseServlet.class );

    //***************************************************************
    // Helper Methods
    //***************************************************************

    /**
     * Returns the SshConnection that is associated with this request, or null
     * if the session can not be found.
     *
     * @param request
     * @param sshSession
     * @return the requested SshSession, or null.
     */
    protected SshConnection getConnection( HttpServletRequest request, SshSession sshSession )
    {
        String connectionInfo = request.getParameter( PARAMETER_CONNECTION );
        SshConnection sshConnection = null;
        if( connectionInfo != null && connectionInfo.trim().length() > 0 )
        {
            sshConnection = sshSession.getSshConnection( connectionInfo );
        }

        return sshConnection;
    }
}
