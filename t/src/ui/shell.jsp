<%@ page import="org.apache.commons.logging.Log,
                 org.apache.commons.logging.LogFactory,
                 com.ericdaugherty.sshwebproxy.*"%>
<%@ include file="nocache.jsp" %>
<%@ include file="security.jsp" %>

<%--
   | Shell page.  Displays the current shell connection output
   | and allows input of shell commands.
--%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
            "http://www.w3.org/TR/html4/strict.dtd">
<html>

<script language='javascript'>

function getKey( event )
{
    if (window.event)
    {
        return window.event.keyCode;
    }
    else if (event)
    {
        return event.which;
    }
    else
    {
        return null;
    }
}

function textBoxKeypress( event )
{
    var key = getKey( event );
    if (key == null) return true;

    if ( key==13 )
    {
        document.ShellClient.writeline.click()
        return false;
    }

}

onkeydown="if ((event.which && event.which == 13) || (event.keyCode && event.keyCode == 13)) {;return false;} else return true;"

function writeControlChar( chars )
{
    document.forms['ShellClient'].<%=SshConstants.PARAMETER_DATA%>.value = document.forms['ShellClient'].<%=SshConstants.PARAMETER_DATA%>.value + chars;
    document.forms['ShellClient'].<%=SshConstants.PARAMETER_DATA%>.focus();
}

function viewBuffer()
{
    window.open("shell-buffer.jsp?<%=request.getQueryString()%>", "Buffer", "width=800, height=600, location=no, menubar=no, status=no, toolbar=yes, scrollbars=yes, resizable=yes");
}

function refresh()
{
    window.location = "<%=request.getRequestURL() + "?" + request.getQueryString()%>";
}

</script>

<head>
    <title>SshWebProxy - Shell</title>
    <style type="text/css" title="currentStyle">
        @import "sshwebproxy.css";
    </style>
</head>

<%
    Log log = LogFactory.getLog( "com.ericdaugherty.sshwebproxy.jsp.shell-jsp" );

    String connectionInfo = request.getParameter( SshConstants.PARAMETER_CONNECTION );
    String channelId = request.getParameter( SshConstants.PARAMETER_CHANNEL );

    log.debug( "Displaying shell for: " + connectionInfo + " " + channelId );

    sshSession = new SshSession( session );

    log.debug( "Getting Connection and Channel" );
    SshConnection sshConnection = sshSession.getSshConnection( connectionInfo );
    ShellChannel shellChannel = null;
    String[] lines = null;
    boolean valid = false;
    int cursorRowIndex = 0;
    int cursorColumnIndex = 0;
    if( sshConnection != null )
    {
        shellChannel = sshConnection.getShellChannel( channelId );
        if( shellChannel != null )
        {
            log.debug( "Filling Buffer and getting Screen." );
            shellChannel.read();
            lines = shellChannel.getScreen();
            valid = true;
            cursorRowIndex = shellChannel.getCursorRow();
            cursorColumnIndex = shellChannel.getCursorColumn();
        }
    }
%>

<body>

<jsp:include page="displayerror.jsp" />

<p class="links">
<a href="<%=SshConstants.PAGE_HOME%>">Home</a>
</p>
<hr/>
<%
if( valid )
{
%>
<div id="shell"><%
    for( int index = 0; index < lines.length; index++ )
    {
        String row = lines[index];

        // Display the cursor if applicable.
        if( cursorRowIndex == index && cursorColumnIndex != -1 )
        {
            int rowSize = row.length();
            for( int columnIndex = 0; columnIndex < rowSize; columnIndex++ )
            {
                if( cursorColumnIndex == columnIndex )
                {
                    out.print( "<span id='shell-cursor'>" );
                    out.print( shellChannel.encodeHTML( row.substring( columnIndex, columnIndex + 1 ) ) );
                    out.print( "</span>" );
                }
                else
                {
                    out.print( shellChannel.encodeHTML( row.substring( columnIndex, columnIndex + 1 ) ) );
                }
            }
            out.println( "" );
        }
        // Display the line with the cursor at the end.
        else if( cursorRowIndex == index && cursorColumnIndex == -1 )
        {
            out.print( shellChannel.encodeHTML( row ) );
            out.println( "<span id='shell-cursor'> </span>" );
        }
        // Diplay the entire line, no cursor.
        else
        {
            out.println( shellChannel.encodeHTML( row ) );
        }
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
<p/>
<%
if( valid && shellChannel.isConnected() )
{
%>
<form name="ShellClient" method="post" action="<%=SshConstants.SERVLET_SHELL%>">
    <input type="hidden" name="<%=SshConstants.PARAMETER_ACTION%>" value="<%=SshConstants.ACTION_WRITE%>" />
    <input type="hidden" name="<%=SshConstants.PARAMETER_CONNECTION%>" value="<%=connectionInfo%>" />
    <input type="hidden" name="<%=SshConstants.PARAMETER_CHANNEL%>" value="<%=channelId%>" />
    <input type="text" name="<%=SshConstants.PARAMETER_DATA%>" onKeyPress="return textBoxKeypress(event)" />
    <input type="submit" name="writeline" value="Write Line" />
    <input type="submit" name="write" value="Write" />
    <input type="button" value="View Buffer" onClick="viewBuffer()" />
    <input type="button" value="Refresh" onClick="refresh()" />
</form>

<script language='javascript'>
     // Set focus to input field.
    document.forms['ShellClient'].<%=SshConstants.PARAMETER_DATA%>.focus();
</script>

<p>
To write control characters, write them as their hex value, prefixed with #.
For example, ESC is #1B.  To write a # Character, write #23.  The control
key is sent as #-1, and causes the next character to be sent as if the
Ctrl key were held down.
</p>
<p>
The following buttons add control characters to the write input field.
</p>

<form name="SpecialChars">

    <input type="button" value="CTRL" onclick="writeControlChar('#-1');" />
    <input type="button" value="ESC" onclick="writeControlChar('#1B');" />
    <input type="button" value="#" onclick="writeControlChar('#23');" />

</form>

<%
}
else
{
%>
Connection Closed.
<%
}
%>

</body>

</html>