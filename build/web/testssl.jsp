<%-- 
    Document   : testssl
    Created on : Jan 15, 2015, 9:15:08 AM
    Author     : SuMario
--%>
<%
 java.net.URL u = null;
 String name=null; int port=-1;
 StringBuilder sw = new StringBuilder();
 int debug=0;
 String info=request.getParameter("debug");
 if ( info != null && ! info.isEmpty() ) { try { debug=Integer.parseInt(info); } catch (Exception e){ } }
 info=request.getParameter("url");
 if ( info != null && !info.isEmpty() ) {
    try { 
       u=new java.net.URL(info);
       name = u.getHost();
       if ( u.getProtocol().matches("http") ) {
          port = (u.getPort()==-1)?80:u.getPort();
       } else {
          port = (u.getPort()==-1)?443:u.getPort();
       }
    } catch (java.net.MalformedURLException un) {
       info=request.getParameter("url");

       try {
           String[] sp = info.split(":");
           if ( sp[0].isEmpty() || sp.length < 2 || sp[1].isEmpty() ) { throw new java.lang.NumberFormatException("not a valid host "); }
           name=sp[0];
           int a = Integer.parseInt(sp[1]);
           if (a <= 0 || a > 65535) {  throw new java.lang.NumberFormatException("not a valid port number"); }
           port=a;
       } catch(java.lang.NumberFormatException e){  
          sw.append("ERROR: "+e.getMessage()); port=-1;
       } 
    }
    if ( name != null && ! name.isEmpty() && port >0 )  {
       java.net.InetSocketAddress inet = new java.net.InetSocketAddress(name, port);

       net.ssl.TestSSLServer.debug=debug;
       net.ssl.TestSSLServer t=new net.ssl.TestSSLServer();
       sw.append(t.test(inet).toString());
    }
}else {
    sw.append("ERROR: no url or host provided\n");
}
%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Test SSL Page</title>
    </head>
    <body>
        <h1>Test SSLServer connection !</h1>
        <% out.println(sw.toString().replaceAll("\n","<br/>\n")); %>
    </body>
</html>
