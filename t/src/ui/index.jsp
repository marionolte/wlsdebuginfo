<%@ page import="java.util.Iterator,
                 java.util.Collection,
                 com.ericdaugherty.sshwebproxy.*"%>
<%@ include file="nocache.jsp" %>
<%@ include file="security.jsp" %>

<%--
   | Home page.  Displays current open Connections and Channels
   | and allows new Connections and Channels to be opened.
--%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
            "http://www.w3.org/TR/html4/strict.dtd">
<html>

<head>
    <title>SshWebProxy - Home</title>
    <link type="text/css" href="sshwebproxy.css" rel="stylesheet">
</head>

<%
    sshSession = new SshSession( session );
    Collection connections = sshSession.getConnections();
%>

<body>
    <p class="header">SSHWebProxy Home</p>

    <jsp:include page="displayerror.jsp" />

<%
if( connections != null && connections.size() > 0 )
{
%>
    <p class="sub-header">Current Connections:</p>
    <table border='2'>
        <tr>
            <td colspan='4'>Connections</td>
        </tr>
<%
    Iterator connectionIterator = connections.iterator();
    SshConnection connection;
    String connectionInfo;
    while( connectionIterator.hasNext() )
    {
        connection = (SshConnection) connectionIterator.next();
        connectionInfo = connection.getConnectionInfo();
%>
        <tr>
            <td><%= connectionInfo %></td>
            <td>
                <form name="<%=connectionInfo%>-close" method="post" action="<%=SshConstants.SERVLET_CONNECTION%>">
                    <input type="hidden" name="<%=SshConstants.PARAMETER_ACTION%>" value="<%=SshConstants.ACTION_CLOSE_CONNECTION%>" />
                    <input type="hidden" name="<%=SshConstants.PARAMETER_CONNECTION%>" value="<%=connectionInfo%>" />
                    <input type="submit" value="Close" />
                </form>
            </td>
            <td>
                <form name="<%=connectionInfo%>-shell" method="post" action="<%=SshConstants.SERVLET_CONNECTION%>">
                    <input type="hidden" name="<%=SshConstants.PARAMETER_ACTION%>" value="<%=SshConstants.ACTION_OPEN_SHELL_CHANNEL%>" />
                    <input type="hidden" name="<%=SshConstants.PARAMETER_CONNECTION%>" value="<%=connectionInfo%>" />
                    <input type="submit" value="New Shell Window" />
                </form>
            </td>
            <td>
                <form name="<%=connectionInfo%>-shell" method="post" action="<%=SshConstants.SERVLET_CONNECTION%>">
                    <input type="hidden" name="<%=SshConstants.PARAMETER_ACTION%>" value="<%=SshConstants.ACTION_OPEN_FILE_CHANNEL%>" />
                    <input type="hidden" name="<%=SshConstants.PARAMETER_CONNECTION%>" value="<%=connectionInfo%>" />
                    <input type="submit" value="New File Window" />
                </form>
            </td>
        </tr>
        <tr>
            <td colspan='4'>

<%
            Collection channels = connection.getChannels();
            if( channels != null && channels.size() > 0 )
            {
%>
                <table border='2'>
                    <tr>
                        <td colspan='4'>Windows</td>
                    </tr>
<%
            Iterator channelIterator = channels.iterator();
            SshChannel sshChannel;
            String channelId;
            while( channelIterator.hasNext() )
            {
                sshChannel = (SshChannel) channelIterator.next();
                channelId = sshChannel.getChannelId();
%>
                    <tr>
                        <td><a href="<%= sshChannel.getPage()%>"><%= channelId %> - <%=sshChannel.getChannelType()%></a></td>
                        <td><%= sshChannel.isConnected() ? "Open" : "Closed" %></td>
                        <td>
                            <form name="<%=connectionInfo%>-<%=channelId%>-close" method="post" action="<%=SshConstants.SERVLET_CONNECTION%>">
                                <input type="hidden" name="<%=SshConstants.PARAMETER_ACTION%>" value="<%=SshConstants.ACTION_CLOSE_CHANNEL%>" />
                                <input type="hidden" name="<%=SshConstants.PARAMETER_CONNECTION%>" value="<%=connectionInfo%>" />
                                <input type="hidden" name="<%=SshConstants.PARAMETER_CHANNEL%>" value="<%=channelId%>" />
                                <input type="submit" value="Close" />
                            </form>
                        </td>
                    </tr>
<%
            }
%>
                </table>
            </td>
<%
        }
%>
        </tr>
<%
    }
%>
    </table>
<%
}
%>

    <p>


    <p>
        Connection Setup
    </p>
    <form ENCTYPE="multipart/form-data" name="ConnectionSetup" method="post" action="<%=SshConstants.SERVLET_CONNECTION%>">
        <input type="hidden" name="<%=SshConstants.PARAMETER_ACTION%>" value="<%=SshConstants.ACTION_OPEN_CONNECTION%>" />
<%
if( sshSession.isRestrictedMode() )
{
%>
        Host: <%= sshSession.getRestrictedModeHost() %> Port: 22
<%
}
else
{
%>
        Host: <input type="text" name="<%=SshConstants.PARAMETER_HOST%>" value="" />
        Port: <input type="text" name="<%=SshConstants.PARAMETER_PORT%>" value="22" />
<%
}
%>
        <br/>
        Username: <input type="text" name="<%=SshConstants.PARAMETER_USERNAME%>" value="" />
        <br/>
        Authentication Type:
        <input type="radio" name="<%= SshConstants.PARAMETER_AUTHENTICATION_TYPE %>" value="<%= SshConstants.AUTHENTICATION_TYPE_PASSWORD %>" <%--onClick="enablePasswordInput();"--%> checked />Password
        <input type="radio" name="<%= SshConstants.PARAMETER_AUTHENTICATION_TYPE %>" value="<%= SshConstants.AUTHENTICATION_TYPE_KEY %>" <%--onClick="enableKeyInput();"--%> />Key
        <br/>
        Password: <input type="password" name="<%=SshConstants.PARAMETER_PASSWORD%>" value="" />
        <br/>
        Key File: <input type="file" name="<%=SshConstants.PARAMETER_KEY_FILE%>" />
        <br/>
        Key PassPhrase: <input type="password" name="<%=SshConstants.PARAMETER_KEY_PASSWORD%>" />
        <br/>
        Channel Type:
        <input type="radio" value="<%=SshConstants.CHANNEL_TYPE_NONE%>" name="<%=SshConstants.PARAMETER_CHANNEL_TYPE%>">None</input>
        <input type="radio" value="<%=SshConstants.CHANNEL_TYPE_SHELL%>" name="<%=SshConstants.PARAMETER_CHANNEL_TYPE%>" checked>Shell</input>
        <input type="radio" value="<%=SshConstants.CHANNEL_TYPE_FILE%>" name="<%=SshConstants.PARAMETER_CHANNEL_TYPE%>">File</input>
        <br/>
        <input type="submit" value="Open" />
    </form>

    <script language='javascript'>
         // Set focus to host field.
        document.forms['ConnectionSetup'].<%=SshConstants.PARAMETER_HOST%>.focus();
    </script>

</body>

</html>