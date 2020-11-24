<%-- 
    Document   : getProperty
    Created on : Jun 16, 2015, 11:06:01 AM
    Author     : SuMario
--%>
<%
    String k = request.getParameter("key");
    String v = request.getParameter("value");
    if ( k != null ) {
        System.setProperty(k,v);
    }
%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Properties</title>
    </head>
    <body>
        <h1>JSP to get java system properties</h1>
        <%
            out.println("<a href=\"./setProperty.jsp\" >set a system property</a> &nbsp; <a href=\"./\" >index</a><br/>");

            java.util.Properties  prop = System.getProperties();
            java.util.Iterator itter = prop.keySet().iterator();
            out.println("<table >");
            out.println("<tr><td> Key </td><td> Value </td></tr>");
            while(itter.hasNext()) {
                String key = (String) itter.next();
                String value = prop.getProperty(key);
                out.println("<tr><td> " + key + " </td><td> " + value + "</td></tr>");
            }
            out.println("</table>");

            out.println("<a href=\"./setProperty.jsp\" >set a system property</a> &nbsp; <a href=\"./\" >index</a><br/>");
        %>
    </body>
</html>
