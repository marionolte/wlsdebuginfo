<%@ page import="com.ericdaugherty.sshwebproxy.SshConstants"%>
<%@ include file="nocache.jsp" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
            "http://www.w3.org/TR/html4/strict.dtd">
<html>

<head>
    <title>SshWebProxy - Home</title>
    <link type="text/css" href="sshwebproxy.css" rel="stylesheet">
</head>

<body>

<jsp:include page="displayerror.jsp" />

<form name="ShellClient" method="post" action="<%=SshConstants.SERVLET_ADMIN%>">
    <input type="hidden" name="<%=SshConstants.PARAMETER_ACTION%>" value="<%=SshConstants.ACTION_LOGIN%>" />
    Username: <input type="text" name="<%=SshConstants.PARAMETER_USERNAME%>" />
    <br/>
    Password: <input type="password" name="<%=SshConstants.PARAMETER_PASSWORD%>" />
    <br/>
    <input type="submit" name="write" value="Login"/>
</form>

</body>

</html>