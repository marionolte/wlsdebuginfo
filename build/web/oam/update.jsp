<%-- 
    Document   : update.jsp
    Created on : Dec 3, 2014, 7:00:56 PM
    Author     : SuMario
--%>
<%
    String mySid = (String) session.getAttribute("mysid");
    service.Protect prot= (service.Protect) session.getAttribute("protect");    
    if ( prot == null ) {
        response.sendRedirect("Protection.jsp");
        response.setStatus(302);
        return;
    }

    int debug=0;
    String info=request.getParameter("debug");
    if ( info != null && ! info.isEmpty() ) { try { debug=Integer.parseInt(info); } catch (Exception e){ } } 
    prot.debug=debug; 
    
    service.Tester  tester = (service.Tester) session.getAttribute("tester");
    if ( tester == null ) { tester = new service.Tester(prot);  session.setAttribute("tester", tester); }    

    StringBuilder sw=new StringBuilder();
    java.util.ArrayList<service.http.Header> map = null; 
    java.util.HashMap<String,String> post = null; 
    if (request.getContentLength() > -1 ) { 
        service.Handler hdl = new service.Handler();  hdl.debug=debug;
        post=hdl.getPost( request.getInputStream() ); 
    }

    int polls=0;
    try {  polls = Integer.parseInt( (String) session.getAttribute("polls")); } catch(Exception e) { polls=0; }

  boolean b=false;
  String ERROR="FAILED";
  service.http.Header hea;
  
  
 %>   
<%@page contentType="text/plain" pageEncoding="UTF-8"%>
<%
  
  if ( request.getParameter("main") != null ) {
     if ( request.getParameter("main").equals("start") ) {
        tester.reset();
        ERROR="FAILED";
        if (  prot != null ) {
            b=false; 
            if ( post == null)  { 
                if ( debug > 0 ) { out.print("User : "+request.getParameter("username")+" <br/>\n"); }
                     prot.setUser(    request.getParameter("username")      );
                if ( debug > 0 ) { out.print("Pass : "+request.getParameter("password")+" <br/>\n"); }
                     prot.setPassword(request.getParameter("password")  );
                if ( debug > 0 ) { out.print("URL  : "+request.getParameter("url")+" <br/>\n"); }
                   b=prot.setUrl(     request.getParameter("url")       );
                if ( debug > 0 ) { out.print("URL check returns : "+b+" <br/>\n"); }
                   prot.setProxy(    request.getParameter("proxy")     );  
                if ( debug > 0 ) { out.print("Proxy set to :"+prot.getProxy());}
                   prot.setProxyUser(request.getParameter("puser")    );
                   prot.setProxyPassword(request.getParameter("ppass")    );
                   prot.useUserAgent( ( ( request.getParameter("useragent") == "true" )?true:false ) ) ;
                if ( debug > 0 ) { out.print("UserAgent set to :"+prot.getUserAgent()+":");}
            } else {
                java.util.Iterator<String> itter = post.keySet().iterator();
                while( itter.hasNext() ) {
                    String f=itter.next();
                    sw.append("|"+f+"|=|"+post.get(f)+"|\n");
                }
                     prot.setUser(         post.get("username")      );
                     prot.setPassword(     post.get("password")  );
                   b=prot.setUrl(          post.get("url")       );
                     prot.setProxy(        post.get("proxy")     );
                     prot.setProxyUser(    post.get("puser")     );
                     prot.setProxyPassword(post.get("ppass")     );
                     prot.useUserAgent( (post.get("useragent") == "true")?true:false );
                     
            }
            if ( b ) { ERROR="OK";  prot.setConfigured(true); } 
             else  { sw.append(prot.getErrorMsg()); prot.setConfigured(false);}
           session.setAttribute("protect", prot);
        }
        out.print(sw.toString()+"\n"+ERROR);
     } else if ( request.getParameter("main").equals("startstop") ) {
        if ( tester.isRunning() ) { tester.close(); }
        prot.setConfigured(false);
        out.print("OK");
     } else if ( request.getParameter("main").equals("startmessage") ) {
       map=tester.getHeaders(); 
       try {
            int a = Integer.parseInt( request.getParameter("msg") );
            hea = map.get(a);
          out.print("<textarea id=msgtext name=msgtext >\n");
          if ( hea.conerr.length() == 0 ) {
            
            out.print("REQUEST HEADER:<br/>\n"     +hea.getHeaderRequestMsg().replaceAll("\n","<br/>\n")   +"\nREQUEST END HEADER:<br/>\n"
                    + "RESPONSE BEGIN HEADER:<br/>\n"+hea.getHeaderResponseMsg().replaceAll("\n","<br/>\n")  +"\nRESPONSE END HEADER:<br/>\n"
                    + "RESPONSE BEGIN BODY:<br/>\n<pre>"  +hea.htmlAction(request.getRequestURI().replaceAll("/update.jsp","/incaction.jsp?url="),hea.getHeaderResponseBody())+"\n</pre><br/>\nRESPONSE END BODY:\n");
          } else {
            out.print(hea.conerr.toString());
          }
          out.print("</textarea>\n");
       } catch(Exception e) {
            out.print("ERROR:  message could not be loaded");
            e.printStackTrace();
            response.setStatus(500);
       }
     } else if ( request.getParameter("main").equals("msg") ) {
        if ( ! tester.isRunning() && ! tester.isCompleted() ) { tester.start(); }
        
        map=tester.getHeaders();
        out.print(map.size()+" messages  &nbsp; \n");
        out.print("completed :"+( (tester.isCompleted())?"Yes":"No")+" &nbsp;<br />\n");
        //out.print("closed    :"+( (tester.isClosed()   )?"Yes":"No")+" <br/>\n");
        int j=0;
        while( map.size()>0 ) { 
            hea = map.remove(0);
            out.print("<a href=\"#\" onClick=\"javascript:getMessage("+j+");return false;\" onSubmit=\"javascript:return false;\" >");
            out.print((j+1)+": ("+hea.getStatus()+" - "+hea.getUrl());
            out.print("</a>) <br/>\n"); 
            j++;
        }
        
        if ( debug > 0 ) {
            out.print("running   :"+( (tester.isRunning()  )?"Yes":"No")+" <br/>\n");
            out.print("count runs: "+tester.count+" <br/>\n");
            out.print("poll count: "+tester.poll+" <br/>\n");
            out.print("prot configured: "+tester.isConfigured()+"<br/>\n");
            if ( tester.err.length() > 0 ) {
                out.print(tester.err.toString());
            }
        }
        if ( tester.isCompleted() ) {
            out.print("\n<!-- COMPLETE -->");
        }

     } else {
        out.print("not supported");
     }
  } else {
        out.print("not supported");
  }
%>