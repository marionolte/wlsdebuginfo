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
        <title>Java Server Pages - Debug Welcome Page - Version <%out.print(version);%></title>
    </head>
    <body>
        <h1>Java Server Pages - Debug Welcome Page - Version <%out.print(version);%></h1>
        
        <a href="./printenv.jsp" >display the header information</a><br/><br/>

	<a href="./cve/cve.jsp" >CVE checks</a><br/><br/>
        
        <h3>Testing Header</h3><form action="./testhttp.jsp" method="GET" >
            <input type="text" name="url" value="" size="150" /> (insert url and press return)
        </form><br/><br/>
        
        <h3>Testing SSL</h3><form action="./testssl.jsp" method="GET" >
            <input type="text" name="url" value="" size="150" /> (insert url or host:port and press return to test ssl server)
        </form><br/><br/><br/>
        <a href="./oam/Protection.jsp" >Test a protected page</a><br/>
        
        <br /><br />
        <h3>Port Scan</h3><form action="./testport.jsp" method="GET" >
            min port : <input type="text" name="min" value="1" size="10" /> &nbsp;
            max port : <input type="text" name="max" value="2048" size="10" /> &nbsp; 
                       <input type="Submit" name="Submit" value="scan ports" size="10" />
            <input type="text" name="url" value="" size="150" /> (insert url or hostname and click scan ports)
        </form><br/><br/>
        
        <h3>Set/Get Java System Properties</h3>
        <a href="./getProperty.jsp" >get</a> <a href="./setProperty.jsp" >set</a><br/>
    </body>
</html>
