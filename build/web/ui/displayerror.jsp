<%@ page import="com.ericdaugherty.sshwebproxy.SshSession"%>

<%--
   | This page displays the current error message, if there is one,
   | and clears it from the session.
--%>

<%
    SshSession sshSession = new SshSession( session );
    String errorMessage = sshSession.getErrorMessage();
    if( errorMessage != null && errorMessage.length() > 0 )
{
    sshSession.setErrorMessage( null );
%>
    <p class="error"><%= errorMessage %></p>
<%
}
%>