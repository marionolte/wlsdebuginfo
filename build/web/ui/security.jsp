<%@ page import="com.ericdaugherty.sshwebproxy.SshSession,
                 com.ericdaugherty.sshwebproxy.SshConstants"%>

<%--
   | This page performs a security check to make sure the user is logged in.
   | It should be included on every page except the login page
--%>

<%
    SshSession sshSession = new SshSession( session );
    if( !sshSession.isValid() )
    {
        String restrictedModeHost = System.getProperty( SshConstants.PROPERTIES_RESTRICTED );
        if( restrictedModeHost != null && restrictedModeHost.length() > 0 )
        {
            sshSession.setRestrictedModeHost( restrictedModeHost );
        }
        else
        {
            response.sendRedirect( SshConstants.PAGE_LOGIN );
        }
    }
%>