<%-- 
    Document   : index
    Created on : Jun 19, 2019, 4:45:13 AM
    Author     : SuMario
--%>
<%@ page import="java.util.*,java.io.*,java.net.*" %>
<%

    String version=service.Version.getVersion();
    String mySid = (String) session.getAttribute("mysid");
    if ( mySid == null  )  {
        mySid=session.getId();
    }
    session.setAttribute("mysid", mySid);
    String OK="#000080";
    String FAILED="#FF1111";
%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>CNVD-C-2019-48814 - CVE Base Check - Version <%out.print(version);%></title>
    </head>
    <body>
        <h1>CNVD-C-2019-48814 - CVE Base Check - Version <%out.print(version);%></h1>
        
<pre>
Test with whoami :
<%
	Process p; 
	String  cli="whoami";
	if ( System.getProperty("os.name").toLowerCase().indexOf("windows") != -1 ) {
		cli="cmd.exe /c whoami";
	}
	p = Runtime.getRuntime().exec(cli);
	DataInputStream in = new DataInputStream( p.getInputStream()) ;
	int c=0;
	String r = in.readLine();
	if ( r != null ) { c +=r.length();  }
	while ( r != null ) {
		c += r.length();
		out.println(r);
		r= in.readLine();
	}
out.println("\n</pre>");
	if ( c != 0 ) {
   		out.println("<span style=\"color:"+FAILED+"\" >FAILED</span>");
	} else {
		out.println("<span style=\"color:"+OK+"\" >OK</span>");	
	}
%>

    </body>
</html>
