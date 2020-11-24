<%-- 
    Document   : oamconsole
    Created on : Sep 18, 2014, 5:00:59 PM
    Author     : SuMario
--%>
<%
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
        <title>OAM Console Access</title>
    </head>
    <body>
        <h1>OAM Console Access</h1>
        enter oam console url : 
    </body>
</html>
