<%-- 
    Document   : Protection
    Created on : Dec 2, 2014, 8:38:27 AM
    Author     : SuMario
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String mySid = (String) session.getAttribute("mysid");
    if ( mySid == null  )  {
        mySid=session.getId();
    }
    session.setAttribute("mysid", mySid);

    int debug=0;
    String info=request.getParameter("debug");
    if ( info != null && ! info.isEmpty() ) { try { debug=Integer.parseInt(info); } catch (Exception e){ } } 

    service.Protect prot = (service.Protect) session.getAttribute("protect");
    if ( prot == null ) {
         prot=service.Protect.getInstance();
    }
    prot.setUserAgent(request.getHeader( "User-Agent") );
    session.setAttribute("protect", prot);
    prot.debug=debug;
    
    
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Test Protected page </title>
        
        <script type="text/javascript" src="./js/main.js" ></script>
        <script type="text/javascript" src="./js/tinymce/tinymce.min.js"></script>
        <script type="text/javascript">
                tinymce.init({
                selector: "textarea"
                 });
        </script>
    </head>
    <body onload="javascript:loader();debug=<%out.print(""+debug);%>;" onresize="javascript:loader();"> 
        <table id="maintab" width="100%" height="100%" border="2" >
            <tr><td id="top"  width="100%" height="20%" colspan="2">top</td></tr>
            <tr><td id="show" width="80%"  height="80%"  > 
                    <table id="showtab" >
                        <tr><td id="head" name="head"> </td>
                            <td id="body" name="body"> </td></tr>
                    </table>
                </td>
                <td id="msg"  width="20%"  height="80%"  > show right</td></tr>
        </table>
    </body>
</html>
