<%@ page import="org.apache.commons.logging.Log,
                 org.apache.commons.logging.LogFactory,
                 com.ericdaugherty.sshwebproxy.SshConstants,
                 com.ericdaugherty.sshwebproxy.SshSession,
                 com.ericdaugherty.sshwebproxy.SshConnection,
                 com.ericdaugherty.sshwebproxy.ShellChannel"%>
<%@ include file="nocache.jsp" %>
<%@ include file="security.jsp" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
            "http://www.w3.org/TR/html4/strict.dtd">
<html>

<head>
    <title>SshWebProxy - Shell Buffer</title>
    <style type="text/css" title="currentStyle">
        @import "sshwebproxy.css";
    </style>
</head>

<%
    Log log = LogFactory.getLog( "com.ericdaugherty.sshwebproxy.jsp.shell-buffer-jsp" );

    String connectionInfo = request.getParameter( SshConstants.PARAMETER_CONNECTION );
    String channelId = request.getParameter( SshConstants.PARAMETER_CHANNEL );

    log.debug( "Displaying shell buffer for: " + connectionInfo + " " + channelId );

    sshSession = new SshSession( session );

    log.debug( "Getting Connection and Channel" );
    SshConnection sshConnection = sshSession.getSshConnection( connectionInfo );
    ShellChannel shellChannel = null;
    String[] lines = null;
    boolean valid = false;
    if( sshConnection != null )
    {
        shellChannel = sshConnection.getShellChannel( channelId );
        if( shellChannel != null )
        {
            log.debug( "Getting Buffer." );
            lines = shellChannel.getBuffer();
            valid = true;
        }
    }
%>

<body>

<%
if( valid )
{
%>
<div id="shell"><%
    for( int index = 0; index < lines.length; index++ )
    {
        out.println( shellChannel.encodeHTML( lines[index] ) );
    }
%></div>
<%
}
else
{
%>
    Invalid Connection or Channel.
<%
}
%>

</body>

</html>