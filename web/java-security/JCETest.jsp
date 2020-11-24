<%-- 
    Document   : JCETest
    Created on : Jul 5, 2013, 2:56:03 PM
    Author     : SuMario
--%>

<%
   import javax.crypto.SecretKeyFactory; 

   javax.crypto.SecretKeyFactory kf;
   String query = request.getQueryString();
   
%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JCE Test Page</title>
    </head>
    <body>
        <h1>JCE Test Page</h1>
        <%
           
            kf = ( query == null ) ? utils.Loader.getSecretKeyFactoryInstance() : utils.Loader.getSecretKeyFactoryInstance(query) ;
            if ( kf == null ) { 
                out.println("ERROR: could not load SecretKeyFactor <br/>");
            } else {
                out.println("OK: ");
            }
            
        %>
        
    </body>
</html>
