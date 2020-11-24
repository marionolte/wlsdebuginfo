<%-- 
    Document   : getProperty
    Created on : Jun 16, 2015, 11:06:01 AM
    Author     : SuMario
--%>
<%
    String k = request.getParameter("key");
    String v = request.getParameter("value");
    if ( k != null && ! k.isEmpty() ) {
        System.setProperty(k,v);
        response.sendRedirect("./getProperty.jsp");
        response.setStatus(302);
    } 
    if ( v == null ) {v=""; }
    if ( k == null ) {k=""; }
%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Properties</title>
    </head>
    <body>
        <h1>JSP to set java system properties</h1>
        <%
            out.println("<form action=./setProperty.jsp method=GET >");
            out.println("  Key : <input   type=text name=key   value=\'"+k+"\' />");
            out.println("  Value : <input type=text name=value value=\'"+v+"\' /><br/>");
            out.println("  <input type=submit name=Set />");
            out.println("</form>");

             out.println("<br/><a href=\"./getProperty.jsp\" >get java system properties</a> &nbsp; <a href=\"./\" >index</a><br/>");
        %>
    </body>
</html>
