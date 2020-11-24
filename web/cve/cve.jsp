<%-- 
    Document   : index
    Created on : Jul 3, 2013, 4:45:13 PM
    Author     : SuMario
--%>
<%
    String version=service.Version.getVersion();
    String mySid = (String) session.getAttribute("mysid");
    if ( mySid == null  )  {
        mySid=session.getId();
    }
    session.setAttribute("mysid", mySid);
%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Java Server Pages - CVE Base Checked - Version <%out.print(version);%></title>
    </head>
    <body>
        <h1>Java Server Pages - CVE Base Checked - Version <%out.print(version);%></h1>
        
        <a href="./CNVD-C-2019-48814.jsp" >CNVD-C-2019-48814 - whoami test</a><br/><br/>
	<a href="./CVE-2019-2725.jsp" >CVE-2019-2725 - zero day test</a><br/><br/>

    </body>
</html>
