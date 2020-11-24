<%-- 
    Document   : printenv
    Created on : Apr 8, 2013, 8:30:13 AM
    Author     : SuMario
--%>
<%
    String mySid = (String) session.getAttribute("mysid");
    if ( mySid == null  )  {
        mySid=session.getId();
    }
    session.setAttribute("mysid", mySid);
%>

<%@page import="java.util.Enumeration"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>printenv debug JSP</title>
    </head>
    <body>
        <h1>printenv - information</h1>
        
        <%
            out.println("HEADER INFORMATION:<BR/>");
                Enumeration headers;
		String curHeader;
		
		headers = request.getHeaderNames();
		
		while(headers.hasMoreElements())
		{
			curHeader = (String) headers.nextElement();
			
			out.println(curHeader+"="+request.getHeader(curHeader)+"<BR />");
		}
            
           out.println("<p />\nREQUEST INFORMARION:<BR/>");
           
           	out.println("Scheme="+request.getScheme()+"<BR/>");
    		out.println("Method="+request.getMethod()+"<BR/>");
    		out.println("Request URI="+request.getRequestURI()+"<BR/>");
    		out.println("Request protocol="+request.getProtocol()+"<BR/>");
    		out.println("REMOTE IP="+ request.getRemoteAddr()+"<BR/>");
                out.println("Servlet path="+request.getServletPath()+"<BR/>");
    		out.println("Path Info="+request.getPathInfo()+"<BR/>");
    		out.println("Path Translated="+request.getPathTranslated()+"<BR/>");
    		out.println("Query String="+request.getQueryString()+"<BR/>");
                out.println("AuthType="+request.getAuthType()+"<BR/>");
                out.println("Content length="+request.getContentLength()+"<BR />");
          
           String serverUrl =  request.getScheme() +"://"+ request.getServerName()+ ":"+request.getServerPort();
           out.println("<BR />URL from request  are:"+serverUrl+ request.getContextPath() + "/what-ever-page<BR />" );     
        %>    
    </body>
</html>
