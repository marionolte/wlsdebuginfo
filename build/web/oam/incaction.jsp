<%-- 
    Document   : incaction.jsp
    Created on : Jan 7, 2015, 7:49:18 AM
    Author     : SuMario
--%>
<%
    String mySid = (String) session.getAttribute("mysid");
    service.Protect prot   = (service.Protect) session.getAttribute("protect");  
    service.Tester  tester = (service.Tester) session.getAttribute("tester");  

    int debug=0;
    String info=request.getParameter("debug");
    if ( info != null && ! info.isEmpty() ) { try { debug=Integer.parseInt(info); } catch (Exception e){ } } 

    if ( debug == 0 && ( prot == null || tester == null ) ) {
        response.sendRedirect("Protection.jsp");
        response.setStatus(302);
        return;
    } else if ( debug > 0 && ( prot == null || tester == null ) ) {
        prot=service.Protect.getInstance();
        tester = new service.Tester(prot);  session.setAttribute("tester", tester);
    }
   
    if( prot != null ) { prot.debug=debug; tester.debug=debug; }

   String url=request.getParameter("url");

   byte[] data;
    
   %><%@ page trimDirectiveWhitespaces="true" %><%
  
     
     data= tester.getContext(url);
     response.setHeader("Content-length", Integer.toString(data.length));
     response.setHeader("Content-Type",   tester.getMimeTyp(url) );
     response.getOutputStream().write(data, 0, data.length);
  
%>