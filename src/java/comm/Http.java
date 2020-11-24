/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package comm;


import general.Version;
import io.file.ReadFile;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;




/**
 *
 * @author SuMario
 */
public class Http extends Version implements Cloneable {
    public static String debugTrace=null;
    public static Logger logger = null;
    URL url = null;
    //static  public int debug=0;
    static  public BufferedOutputStream dout=null;
    private CookieManager cm=null;
    public  boolean autoFollow302=true;
    private HashMap reqmap;
    public StringBuilder connerr;
    private boolean followRedirect=true;
    private String puser;
    private String ppass;
    private ByteArrayOutputStream sink;
    
    public Http(URL u) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        this.url=u;
        setDefaultTrust();
        status = -1; 
        this.debug=Http.debug;
        this.logger= Http.logger;
        this.debugTrace=Http.debugTrace;
    }
    public Http() throws IOException, NoSuchAlgorithmException, KeyManagementException { this(null); }
    
    public synchronized Http getClone() throws IOException, NoSuchAlgorithmException, KeyManagementException
    {
        Http ht = new Http();
        ht.useragent = this.useragent;
        ht.puser = this.puser;
        ht.ppass = this.ppass;
        ht.setDebug(debug);
        ht.setCookieManager(getCookieManager());
        ht.auth = this.auth;

        return ht;
    }
    static public void  setHttpTrace(BufferedOutputStream dout) { Http.dout=dout; }
    private void trace(String f) { if (dout!=null) { try { dout.write(f.getBytes()); dout.flush(); }catch(IOException e){} }}
    
    public void    setFollowRedirect(boolean b) { this.followRedirect = b; } 
    public boolean getFollowRedirect() { return this.followRedirect; } 
    
    private HashMap<String, String> pmap = new HashMap<String, String>();
    private boolean doPost(HttpURLConnection con) throws IOException {
         Iterator<String> iter = pmap.keySet().iterator();
         StringBuilder send=null;
         if ( iter.hasNext() || emptyKey.length()>0L) { 
             try {
                 con.setDoOutput(true);
                 con.setDoInput(true);
                 con.setRequestMethod("POST");
                 con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                 con.setRequestProperty("Accept-Encoding", "gzip, deflate");
                 send=new StringBuilder();
             } catch (ProtocolException ex) {
                 if ( debug > 0 ) {
                     System.out.println("ERROR:"+ex.getMessage());
                     ex.printStackTrace();
                 }
             }
         }
         while( iter.hasNext() ) {
             String k =  iter.next();
             if ( send.length() != 0 ) { send.append("&"); }
             if ( k.isEmpty() && pmap.get(k).isEmpty() ) {
                   // null length post needed 
             } else {
                 if ( pmap.get(k).endsWith("\\=") ) {
                   send.append(URLEncoder.encode(k, "UTF-8")).append("=").append(pmap.get(k));   
                 } else {
                   send.append(URLEncoder.encode(k, "UTF-8")).append("=").append(URLEncoder.encode(pmap.get(k), "UTF-8"));
                 } 
             }     
         }
         if (emptyKey.length()>0L ) { send.append(emptyKey);}
         
         if ( send != null ) {
                printf("Http:doPost()",1," - POST:"+send.toString()); 
                con.setRequestProperty("Content-Length", ""+send.length() );
                
                iter = con.getRequestProperties().keySet().iterator();
                while(iter.hasNext() ) {
                    String k =  iter.next();
                    trace(""+k+": "+con.getRequestProperty(k)+"\n");
                } 
                trace("\n\n");
                
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes( send.toString() );
		wr.flush();
		wr.close();
                pmap.clear();
                
                return true;
         } else {
                iter = con.getRequestProperties().keySet().iterator();
                while(iter.hasNext() ) {
                         String k =  iter.next();
                         trace(""+k+": "+con.getRequestProperty(k)+"\n");
                }
                trace("\n\n");
                return false;
         }
    }
    public boolean setPost(HashMap<String, String> post) { if(post!=null){ this.pmap=post; return true; } return false;}
    public void    setPost(String key, String value) { if (value==null){value=""; } if(key != null && ! key.isEmpty() ){ pmap.put(key, value); } }
    public void    setPost(StringBuilder af) {
        emptyKey=new StringBuilder();
        String[] sp = af.toString().split("&");
        for ( int i=0; i<sp.length; i++ ) {
            if ( ! sp[i].isEmpty() ) {
                String[] at = sp[i].split("=");
                if ( ! at[0].matches(sp[i]) ) {
                    setPost(at[0],sp[i].substring(at[0].length()+1));
                } else {
                    if ( emptyKey.length() > 0 ) { emptyKey.append("&"); }
                    emptyKey.append(sp[i]);
                }
            }
        }
    }
    private StringBuilder emptyKey=new StringBuilder();
    
    public URL getConnect() { return this.url; }
    public  void connect() throws IOException { connect(this.url); }
    
    // "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2486.0 Safari/537.36 Edge/13.10586"  // win10 edge browser
    private String useragent="Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:34.0) Gecko/20100101 Firefox/34.0";
    
    final private int defTimeout=15000;
    private int timeout=defTimeout;
    public int setTimeout(int msec) { timeout=(msec>0 && msec < 60000 )?msec:timeout; return timeout; } 
    public int setDefaultTimeout() { this.timeout=this.defTimeout; return this.timeout; }
    
    
    public void setUserAgent(String agent)
    {
        this.useragent = ((agent != null) && (!agent.isEmpty()) ? 
                          agent 
                         : 
                          "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:27.0) Gecko/20100101 Firefox/27.0"); 
    }
    
    private String auth = null; 
    private String realm = null; 
    private String realmHeader = null; 
    public void setAuthentication(String auth) { this.auth = ((auth == null) || (auth.isEmpty()) ? null : auth); } 
    public void setAuthRealm(String auth) { this.realm = ((auth == null) || (auth.isEmpty()) ? null : auth); } 
    public void setAuthRealmHeader(String hea) { this.realmHeader = ((hea == null) || (hea.isEmpty()) ? "Authenticate" : hea); } 
    
    
    private HashMap<String, String> hmap=new HashMap<String, String>();
    public  void setConnectionDefault(HashMap<String, String> map) { this.hmap=(map!=null)?map:new HashMap<String, String>();  }
    private void setConnectionDefaults(HttpURLConnection con) {
        Iterator<String> iter; 
        this.reqmap.clear();
        if (this.hmap.isEmpty()) {
            this.reqmap.put("User-Agent", useragent);
            this.reqmap.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            this.reqmap.put("Accept-Language", "en-US,en;q=0.5");
            this.reqmap.put("Accept-Encoding", "identity");

            iter = this.reqmap.keySet().iterator();
            while (iter.hasNext()) {
              String k = (String)iter.next();
              con.setRequestProperty(k, (String)this.reqmap.get(k));
            }
        }

        iter = this.hmap.keySet().iterator();
        while (iter.hasNext()) {
            String k = (String)iter.next();
                con.setRequestProperty(k, (String)this.hmap.get(k));
                this.reqmap.put(k, this.hmap.get(k));
        }

             
        con.setConnectTimeout(timeout);
        
                
        //con.getInstanceFollowRedirects(); // (AutoFollowRedirects)
        
        if ( tmap.size() >0 ) {
            iter = tmap.keySet().iterator();
             while(iter.hasNext() ) {
                    String k =  iter.next();
                    con.setRequestProperty(k, tmap.get(k));
             }
             tmap = new  HashMap<String, String>();
        }
        
        

        if (this.auth != null) {
          this.realmHeader = "Authorization";
          if (isBasic()) {
            if (debug > 1) System.out.println(new StringBuilder().append("INFO: set User Auth with  Basic |").append(this.realmHeader).append("=").append(this.auth).append("|").toString());
            con.setRequestProperty(this.realmHeader, new StringBuilder().append("Basic ").append(this.auth).toString());
          } else {
            if (debug > 1) System.out.println(new StringBuilder().append("INFO: set User Auth with  Digest |").append(this.realmHeader).append("=").append(this.auth).append("|").toString());
            con.setRequestProperty(this.realmHeader, new StringBuilder().append("Digest ").append(this.auth).toString());
          }
        }

        
    }
    
   private boolean authBasic= false;
   public boolean isBasic() { return this.authBasic; }
   public boolean isDigest() { return !this.authBasic; } 
   public boolean setAuthDigest() { this.authBasic = false; return isDigest(); } 
   public boolean setAuthBasic() { this.authBasic = true; return isBasic(); }
    
    public void setTrustAll() {
        if (trustAllCerts == null ) { try { setDefaultTrust(); } catch(Exception e){} }
        if (trustAllCerts != null ) {  trustAll=true; }
    }
    private TrustManager[] trustAllCerts=null;
    public boolean trustAll=false;
    
    private void setDefaultTrust() throws NoSuchAlgorithmException, KeyManagementException{
        if ( trustAllCerts == null ) {
            trustAllCerts = new TrustManager[] {new X509TrustManager() {
                int debug=3;
                
                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    if (this.debug >2 ) {
                        printf("Http::getAcceptedIssuers::trustAllCerts",3,"TrustManager return null with ");
                    }
                    if( trustAll ) return null;
                    return null;
                }
                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    if (this.debug >2 ) {
                        printf("Http::checkClientTrusted::trustAllCerts",3," authType:"+authType+":  certs >|"+certs+"|<");
                    }
                }
                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    if (this.debug >2 ) {
                        printf("Http::checkServerTrusted::trustAllCerts",3," authType:"+authType+":  certs >|"+certs+"|<");
                    }
                }
            }
            };
        }
	 
	        // Install the all-trusting trust manager
	SSLContext sc = SSLContext.getInstance("TLS");
	sc.init(null, trustAllCerts, new java.security.SecureRandom());
	HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        
        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
                    @Override
	            public boolean verify(String hostname, SSLSession session) {
	                return true;
	            }
	};
	 
	// Install the all-trusting host verifier
	HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }
    
    private HashMap<String, String> tmap = new  HashMap<String, String>();
    public void   setHeader(String key, String value) {
        if ( key != null && ! key.isEmpty() && value != null && ! value.isEmpty() ) tmap.put(key, value);
    }
    public String getHeaders() {
        StringBuilder sb = new StringBuilder();
        if ( status > 0 ) {
            Iterator<String> iter = reshmap.keySet().iterator();
            while( iter.hasNext() ) {
                  String k = iter.next(); 
                  sb.append(k).append(": ").append(reshmap.get(k)).append("\n");
            }
        }
        return sb.toString();
    }
    
    private HttpURLConnection conn=null;
    
    public int status=-1;
    public HashMap<String, String> reshmap=new HashMap<String, String>();
    public ArrayList<byte[]> binary = new  ArrayList<byte[]>();
    public StringBuilder  ressw = new StringBuilder();
    public boolean asciiResponse=true;
    private String req_method=null;
    
    private FileOutputStream outfile=null;
    public  String connect(URL u, String file) throws IOException {
            outfile=new FileOutputStream(file);  
            connect(u);
            outfile.flush();
            outfile.close();
        try {    
            MessageDigest md = MessageDigest.getInstance("MD5");
            FileInputStream fis = new FileInputStream(file);
 
            byte[] dataBytes = new byte[1024];

            int nread = 0; 
            while ((nread = fis.read(dataBytes)) != -1) {
              md.update(dataBytes, 0, nread);
            };
            byte[] mdbytes = md.digest();

            //convert the byte to hex format method 1
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mdbytes.length; i++) {
              sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch(Exception io ) {
            return "md5sum not working";
        }    
    }
    public  int getResponseCode() throws IOException { if( status == -1){ connect(); }; return status; }
    
    public  void connect(URL u) throws IOException {
            if ( cm == null ) { setCookieManager( newCookieManager() ); }
            if ( this.trustAllCerts == null ) { try { this.setDefaultTrust();}
                                                catch(Exception e) { 
                                                    System.out.println("TrustDefault Error:"+e.getMessage());
                                                    e.printStackTrace();
                                                } 
            } 
            status = -1;  Long size=0L;
            this.connerr = new StringBuilder();
            this.req_method=u.getProtocol();
            asciiResponse=true;
            this.reqmap = new HashMap();
            reshmap.clear();    ressw = new StringBuilder(); binary = new  ArrayList<byte[]>(); 
            printf("Http::connect(URL u)",2,"create connection to : "+u.toString()); 
            trace("\n\n-----------------------------------------REQUEST---START----------------------------------------------------\n"+u.toString()+"\n");
            conn = (HttpURLConnection) u.openConnection();
            printf("Http::connect(URL u)",3,"connection to : "+u.toString()); 
                          setConnectionDefaults(conn);
                          if ( ! doPost(conn)  ) { 
                              printf("Http::connect(URL u)",3," - doPost return false");  
                              conn.setDoInput(true); 
                          } else {
                              printf("Http::connect(URL u)",3," - doPost return true");  
                          }
            printf("Http::connect(URL u)",3,"connection creating now to : "+u.toString());           
            try { status = conn.getResponseCode(); } 
            catch(java.net.ConnectException ce) { status=-1; }
            catch(java.net.SocketException  se) { status=-1; } 
            printf("DEBUG: Http::connect(URL u)",3,"connect done with status "+status);  
            
            if ( status > 0 ) {
                trace("\n\n"+conn.getRequestMethod()+" "+u.toString()+" "+status+" "+conn.getResponseMessage()+"\n");
                String key; Pattern pa = Pattern.compile("image|gzip|rar|java-archive");
                for (int i = 1; (key = conn.getHeaderFieldKey(i)) != null; i++) {
                    String val = conn.getHeaderField(i);
                    reshmap.put( key, val );
                    trace(""+key+": "+val+"\n");
                    printf("Http::connect(URL u)",2," - key="+key+"|<= ");
                    if ( key.toLowerCase().matches("content-type")) {
                        printf("Http::connect(URL u)",2,"content-type found key="+key+"|<= =>"+val+"<=");
                        Matcher ma = pa.matcher(val);
                        if( ma.find() ) {
                            printf("Http::connect(URL u)",2,"content-type binary set with="+val+"|<= ");
                            asciiResponse=false;
                        }
                    }
                    else if (  key.toLowerCase().matches("content-length") ) {
                        printf("Http::connect(URL u)",2,"content-length found key="+key+"|<= =>"+val+"<=");
                        size= Long.parseLong(val);
                    }
                }
            } else { return ; }    

            if ( status == 302 ) {
                
                String loc = reshmap.get("Location");
                if ( loc == null ) {
                        loc = reshmap.get("Content-Location"); 
                }
                printf("Http::connect(URL u)",2,"auto follow redirect ("+((autoFollow302)?"YES":"NO")+")  to:"+loc);
                trace("\n------------------------------------------REQUEST---END---------------------------------------------------\n");
                if ( autoFollow302 ) {     
                    connect(new URL(loc));
                }
            } else { 
                printf("Http::connect(URL u)",2,"read out response to "+u.toString());
                InputStream in = (status<400)? conn.getInputStream():conn.getErrorStream();                
                
                if ( asciiResponse ) {
                    final String h=conn.getContentEncoding();
                    printf("Http::connect(URL u)",2,"ascii response - encoding type :"+h+"\n"+getHeaders()); 
                    if ( h != null && h.equals("gzip") ) {
                        in = new GZIPInputStream(in);
                    }
                    String conttype="UTF-8";
                    if (conn.getContentType().contains("charset")) {
                        String sp[] = conn.getContentType().split(";");
                        for(int l=0; l<sp.length; l++) {
                            if ( sp[l].toLowerCase().startsWith("charset")) {
                                conttype=sp[l].substring("charset".length()+1);
                                l=sp.length;
                            } 
                        }
                    }
                    
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in, conttype));
                    String inputLine;
                    while ((inputLine = reader.readLine()) != null) {
                        ressw.append(inputLine).append("\n");
                    }
                    trace("------------------------------------------CONTENT---BEGIN---------------------------------------------------\n");
                    trace(ressw.toString());
                    trace("------------------------------------------CONTENT---END---------------------------------------------------\n");
                
                
               } else {  // binary response
                  printf("Http::connect(URL u)",3,"binary response");   
                  
                  long A=0L; long B=0L;
                  int c;  int i=0;
                  if ( outfile == null ) {
                        do {  
                          c=in.available();
                          if ( c > 0 ) {
                              int buf=(c <= 8192)?c:8192;
                              byte[] b = new byte[buf];
                              int m = in.read(b);
                              if ( m != buf ) {
                                  byte[] bb = new byte[m];
                                  for(int j=0; j<bb.length; j++) { bb[j]=b[j]; }
                                  b=bb;
                              }
                              A +=b.length; B++;
                              binary.add(b); 
                          }
                          i++;
                        } while( c > 0 );  
                        trace("\n"+A+" bytes of "+size+" bytes in "+B+" packets received \n");
                  } else {
                       byte[] b = new byte[64*1024];  long wait=System.currentTimeMillis()+10000;
                       do {
                          c= in.available();
                          if ( c > 0 ) {
                               int m=in.read(b);
                               for(int j=0; j<m; j++){ outfile.write(b[j]); }
                               A +=m; B++;
                               wait=System.currentTimeMillis()+10000;
                          } else {
                              if ( size >= A || System.currentTimeMillis() > wait ) {
                              } else { c=1; }
                          }
                       } while( c > 0 ); 
                       trace("\n"+A+" bytes of "+size+" bytes in "+B+" packets received \n");
                  }   
                }             
                
                in.close();
                trace("\n------------------------------------------REQUEST---END---------------------------------------------------\n");
                
            }     
        
    }

  public String[] getHeader(String[] header) {
        String[] r = null;

        if ((header == null) || (header.length == 0)) return r;
        ArrayList ar = new ArrayList();
        for (int i = 0; i < header.length; i++) {
          String l = (String)this.reshmap.get(header[i]);
          if ((l == null) || (l.isEmpty())) continue; ar.add(new StringBuilder().append(header[i]).append(": ").append(l).toString());
        }
        if (ar.size() > 0) {
          r = new String[ar.size()]; int i = 0;
          while (ar.size() > 0) {
            String l = (String)ar.remove(0);
            r[i] = l; i++;
          }
        }
        return r;
    }

    
    public URL getLocation() throws MalformedURLException
    {
      String[] t = { "Location" };
               t = getHeader(t);
        if ((t != null) && (t.length > 0)) {
          String[] sp = t[0].split(":");
          String a = t[0].substring(sp[0].length() + 2);
          if (!a.startsWith("http")) {
            if (a.startsWith("/"))
              a = new StringBuilder().append(this.url.getProtocol()).append("://").append(this.url.getHost()).append(this.url.getPort() > -1 ? "" : new StringBuilder().append(":").append(this.url.getPort()).toString()).append(a).toString();
            else {
              a = new StringBuilder().append(this.url.getProtocol()).append("://").append(this.url.getHost()).append(this.url.getPort() > -1 ? "" : new StringBuilder().append(":").append(this.url.getPort()).toString()).append(this.url.getPath()).append(a).toString();
            }
          }
          return new URL(a);
        }
    return null;
  }

    public byte[] getByteData(String url) throws IOException
    {
        this.sink = new ByteArrayOutputStream();
        connect(new URL(url)); sleep(300);
        byte[] b = this.sink.toByteArray();

        this.sink = null;
        return b;
    }

  private void copy(InputStream in, OutputStream out, int bufferSize) throws IOException
  {
    byte[] buf = new byte[bufferSize];
    int len = 0; long d = 0L;
    while ((len = in.read(buf)) >= 0) {
      d += len;
      out.write(buf, 0, len);
    }
  }

    
    public String getResponseHeader(String h) {
        String loc = reshmap.get(h);
        if ( loc == null ) {
             loc = reshmap.get("Content-"+h); 
        }
        return (loc==null)?"":loc;
    }
    
    public StringBuilder getResponse() throws IOException {
        printf("getResponse()",4,"check for connect with "+ressw.length()+"==0"); 
        if ( ressw.length() == 0 ) { connect(); }
        printf("getResponse()",4,"after connecting lenght  is "+ressw.length()+""); 
        return this.ressw;
    }
    public  void disconnect() {
         if ( conn != null ) { conn.disconnect(); }
    }
    
    private CookieManager newCookieManager(){ cm= new CookieManager(null, CookiePolicy.ACCEPT_ALL); return getCookieManager(); }
    public  CookieManager getCookieManager(){  return cm; }
    public  void          setCookieManager(CookieManager cm) { CookieHandler.setDefault(this.cm=cm); }
    
    public  void setProxy(URL p) {  
        System.setProperty("http.proxyHost", p.getHost() );
        System.setProperty("http.proxyPort", ""+ (( p.getPort() == -1)?p.getDefaultPort():p.getPort()) ); 
        System.setProperty("http.nonProxyHosts" , "localhost|127.0.0.1");
        setSSLProxy(p);
    }
    public void setSSLProxy(URL p) {  
        System.setProperty("https.proxyHost", p.getHost() );
        System.setProperty("https.proxyPort", ""+ (( p.getPort() == -1)?p.getDefaultPort():p.getPort()) ); 
        System.setProperty("https.nonProxyHosts" , "localhost|127.0.0.1");
    }
   
    public void setProxyUser(String u)     { this.puser = (u != null ? u : ""); } 
    public void setProxyPassword(String p) { this.ppass = (p != null ? p : ""); }

    
    public static void main(String[] args)  throws Exception {
           Http h = null;
           Http p = null;
           for( int i=0; i<args.length; i++ ) {
               if ( args[i].matches("-d")) { Http.debug++; } 
               else if ( args[i].matches("-dlog")) { Http.debugTrace=args[++i]; }  
               else {
                if ( h == null ) {   
                     h = new Http( new URL(args[i]) );
                     h.debug=Http.debug; h.debugTrace=Http.debugTrace;
                     if ( p != null ) { h.setCookieManager(p.getCookieManager()); }
                     h.connect();
                } else {
                     h.connect( new URL(args[i]) );
                }
                if ( h.asciiResponse ) { 
                    System.out.println(h.getResponse().toString()); 
                }else{   
                     Http.sleep(1000);
                     String of= h.getFileName();
                     ReadFile f = new ReadFile(of);
                     BufferedOutputStream out = f.getOutStream();
                        while( h.binary.size() > 0 ) {
                            out.write(h.binary.remove(0));
                        }
                     out.flush();
                     out.close();
                     System.out.println("file "+of+" written");
                }
                p=h;
               } 
           }
    }

    public boolean verify(String str) throws IOException {
        Pattern  p = Pattern.compile(str);
        boolean  b = p.matcher( this.getResponse().toString()  ).find() ;
        printf("Http::verify",1,"verify for "+str+" => return:"+b);
        return b;
    }
    

    public String fileName=null;
    private String getFileName() {
        if ( fileName != null ) { return fileName; }
        String[] fp = url.getPath().split("/");
        String f = fp[ fp.length-1 ];
        if ( f.isEmpty() ) {
             String[] tp=this.reshmap.get("Content-Type").split("/");
             f="tempfile."+tp[tp.length-1];
        }
        return f;
    }

  public HashMap<String, String> getResponseHeaders() { return this.reshmap; } 
  public HashMap<String, String> getRequestHeaders() { return this.reqmap; }    
  
  public String getRequest() { return this.req.toString(); }
  public String getRequestMethode() { return this.req_method; }
  private StringBuilder req = new StringBuilder();
    
}
