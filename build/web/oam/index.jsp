<%-- 
    Document   : index.jsp
    Created on : Dec 13, 2013, 3:37:50 PM
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
        <title>OAM Debug Page</title>
    </head>
    <body>
        <h1>Index of the OAM Debug Page</h1>
        <a href="Protection.jsp" >Test a  protected page</a><br/><br/><br/>
        Test an URL
        <form action="../testhttp.jsp" method="GET" >
            <input type="text" name="url" value="" /> (insert url and press return)
        </form><br/>
    </body>
</html>
