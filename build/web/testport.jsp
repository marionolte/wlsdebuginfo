<%-- 
    Document   : testport
    Created on : Jan 16, 2015, 10:00:42 AM
    Author     : SuMario
--%>
<%
  String host="localhost";
  String info=request.getParameter("url");
  net.tools.PortScanner ps;
  StringBuilder result=new StringBuilder();
  if ( info != null ) {
     ps = new net.tools.PortScanner(info);
     ps.setMaxPort(request.getParameter("max"));
     ps.setMinPort(request.getParameter("min"));
     ps.test();
     host=ps.host;
     result.append("PortScanner runs against host: "+ps.host+" for "+ps.getRunTime()/1000+" seconds and found between the ports "+ps.getMinPort()+" to "+ps.getMaxPort()+"\n"+ps.getInfo().toString()+ps.getError().toString());
  } else {
     host="&gt;NOT PROVIDED&lt;";
     result.append("no host provided");
  }
%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Test Host Reachable via port scan</title>
    </head>
    <body>
        <h1>Test Host Reachable via port scan to host <% out.print(host); %></h1>
        <% out.print(result.toString().replaceAll("\n","<br/>\n")); %>
    </body>
</html>
