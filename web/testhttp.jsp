<%-- 
    Document   : testhttp
    Created on : Dec 8, 2014, 7:52:46 AM
    Author     : SuMario
--%>

<%
 java.net.URL u = null;
 comm.Http h=null;
 int status=-1;
 StringBuilder sw=new StringBuilder();

 try { 
    u = new java.net.URL(request.getParameter("url"));
    h = new comm.Http(u);  h.setFollowRedirect(false); 
    h.setUserAgent(request.getHeader("user-agent"));
    h.connect();
    sw.append("REQUEST  HEADER - BEGIN\n").append(h.getRequest()).append("REQUEST  HEADER - END\n\n");
    sw.append("RESPONSE HEADER - BEGIN\n").append(h.getHeaders()).append("RESPONSE HEADER - END\n\n");
    sw.append("RESPONSE BODY - BEGIN\n").append(h.getResponse().toString()).append("RESPONSE BODY - END\n\n");
    status=h.status;
 } catch(Exception e) {
   sw.append( e.getMessage() );
   status = 0;
 }
%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Test HTTP Page</title>
    </head>
    <body>
        <h1>Test Http Page!</h1>
<%
    out.print("return status : "+status+" - "+service.HttpEnum.get(status)+" <br/>\nfor entering the url =>"+u.toString()+"<= <br/>\n");
   
    out.print(("<pre>\n"+sw.toString()+"</pre>\n"));//.replaceAll("\n","<br/>\n"));
%>        
    </body>
</html>
