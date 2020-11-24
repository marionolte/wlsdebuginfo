var timerrun=false;
var ready=false;
var stopped=false;
var debug=0;

function loader() {
                var w = window.innerWidth;
                var h = window.innerHeight;
                var nh=getFirst(h-(h * 20 / 100),".")-50;
                if ( nh < 50 ) { nh=50;}
                var bh=getFirst(h-(h * 80 / 100),".")-50;
                if ( bh <50 ) { bh=50; }
                // alert("size "+h+"x"+w+"  like to set :"+nh+"|"+bh+"|");
                document.getElementById("top").style.height = bh+"px" ;
                document.getElementById("show").style.height = nh+"px" ;
                document.getElementById("msg").style.height =  nh+"px" ;
                
                //alert( document.getElementById("top").style.height +" - "+document.getElementById("show").style.height+" - "+document.getElementById("msg").style.height);
                if ( ! timerrun ) {
                    timer(); timerrun=true;
                }
}

function getFirst(val, splitter) {
    var fu =  (""+val).split(splitter);
    return fu[0];
}

function getLast(val, splitter) {
    var fu = (""+val).split(splitter);
    return fu[ fu.length-1 ];
}

function show(obj) {  
    document.getElementById(obj).className='visiblediv';  
}  
function close(obj) {  
    document.getElementById(obj).className='hiddendiv';  
}

function stop() {
    document.getElementById("configGet").disabled=false;
    document.getElementById("configStop").disabled=true;
    confighttp=request();
    confighttp.onreadystatechange=function() {
            if (confighttp.readyState===4 && confighttp.status===200 )
            {
                //alert("SERVER MSG|"+confighttp.responseText+"|   Last|"+getLast(confighttp.responseText, "\n")+"|");
                if ( getLast(confighttp.responseText, "\n") === "OK" ) {
                    ready=false ; stopped=true;
                    alert("Request Stopped");
                } else {
                    alert("ERROR: could not stop the validation request"); 
                }    
            }
         };
    confighttp.open("GET", document.getElementById("configForm").action+"stop" ,true);
    confighttp.send();
    stopped=true; ready=false;
}

function selectChanged(value) {
    if ( debug == 0 ) {
        alert("INFO: Profile "+value+" are ignored in this version");
    } 
    change(false);
}

function changed(submit){
   var el =document.getElementById("configUrl"); 
   if ( ! submit ) {
     //alert("|"+el.value+"|");
     if (el.value.length > 10 ) { 
        document.getElementById("configGet").disabled=false;
        document.getElementById("configStop").disabled=true;
        //document.getElementById("head").innerHTML="&nbsp;"
     } else {
        document.getElementById("configGet").disabled=true;
        document.getElementById("configStop").disabled=true;
     }
   } else {
      document.getElementById("configGet").disabled=true; 
      document.getElementById("configStop").disabled=false;
      //alert("POST |"+document.getElementById("configForm").action+"|");
      var data="&username="+encodeURIComponent(document.getElementById("configUser").value)
               +"&password="+encodeURIComponent(document.getElementById("configPassword").value)
               +"&url="+encodeURIComponent(el.value);
      if ( document.getElementById("configUserAgent").checked ) {
	       data=data+"&useragent=true";
      } else {
	       data=data+"&useragent=false";
      }
      if ( proxy ) {
          data=data+"&proxy="+encodeURIComponent(document.getElementById("configProxyNamA").value)
                   +"&puser="+encodeURIComponent(document.getElementById("configProxyUser").value)
                   +"&ppass="+encodeURIComponent(document.getElementById("configProxyPass").value);
          
      } 
      
      var sidp = document.getElementById("configProfile");
      data=data+"&profile="+sidp.options[sidp.selectedIndex].value;
      
      confighttp=request();
      confighttp.open("POST", document.getElementById("configForm").action ,true);
      confighttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
      //confighttp.open("GET", document.getElementById("configForm").action+data ,true);
      confighttp.onreadystatechange=function() {
            if (confighttp.readyState===4 && confighttp.status===200 )
            {
                //alert("SERVER MSG|"+confighttp.responseText+"|");
                if ( getLast(confighttp.responseText, "\n") === "OK" ) {
                    ready=true; stopped=false;
                    document.getElementById("configGet").disabled=true;
                } else {
                    alert("ERROR: specified url ==>"+el.value+" \n          are invalid - please correct"); 
                    ready=false;  stopped=true;
                    document.getElementById("configGet").disabled=false;
                    document.getElementById("configStop").disabled=true;
                }    
            }
         };
      confighttp.send(data);
      
   } 
}

var useProxy=false;
var proxy="";
function proxyFlip() {
    if ( document.getElementById("configProxy").checked == 1 ) {
        useProxy=true; proxy=document.getElementById("configProxyName").value;
        document.getElementById("configProxyName").disabled=false;
        document.getElementById("configProxyUser").disabled=false;
        document.getElementById("configProxyPass").disabled=false;
    } else {
        useProxy=false; proxy="";
        document.getElementById("configProxyName").disabled=true;
        document.getElementById("configProxyUser").disabled=true;
        document.getElementById("configProxyPass").disabled=true;
        
    }
}
function proxyChange() {
    proxy=document.getElementById("configProxyName").value;
}

var msgloaded=false;
var msginfo="";
var msgid=-1;
function getMessage(id) {
    var xml=request(); msgid=-1; msginfo="loading";
    xml.open("GET", document.getElementById("configForm").action+"message&msg="+id ,true)
    xml.onreadystatechange=function() {
            if (xml.readyState===4 && xml.status===200 )
            {
                //eval(xml.responseText);
                document.getElementById("body").innerHTML=xml.responseText;
                msginfo="complete loaded";
                var el = document.getElementById("msgtext");
                el.style.height = "100px" ;
                el.style.width  = "100px" ;
                tinymce.init({
                     selector: "textarea",
                     plugins: "autoresize"
                });
            }
         };
      xml.send();
      msgloaded=true;
}

var top="";
var show="";
var msg="";
var date= new Date();

function timer() {
  date = new Date();
  var eh=document.getElementById("head");
  var eb=document.getElementById("body");
  //document.getElementById("top").innerHTML=top;
  if ( ! msgloaded ) { eh.innerHTML=show; eb.innerHTML="&nbsp;"; }
  //else { eh.innerHTML="Message "+msg+" are "+msginfo;
  //       
  //}
  document.getElementById("msg").innerHTML=msg;
  
  testinfo();
  
  window.setTimeout("timer()", 1300);
}  

var confighttp="";
var tophttp="";
var msghttp="";
var toprun=false;
function testinfo() {
    if ( ! toprun ) {
         //alert("collect top");
        
         tophttp=request();
         tophttp.onreadystatechange=function() {
            if (tophttp.readyState===4 && tophttp.status===200 )
            {
                //alert("set text :"+tophttp.responseText);
                top=""+tophttp.responseText;
                document.getElementById("top").innerHTML = ""+tophttp.responseText;;
                tophttp="";
            }
         };
         tophttp.open("GET","topinfo.html",true);
         tophttp.setRequestHeader("If-Modified-Since", "Sat, 1 Jan 2005 00:00:00 GMT");
         tophttp.setRequestHeader("Last-Modified", "Sat, 1 Jan 2005 00:00:00 GMT");
         tophttp.setRequestHeader("Cache-Control", "no-cache");
         tophttp.setRequestHeader("Pragma", "no-cache");
         tophttp.send();
         toprun=true;
         
    } else {
        
        //if ( (""+document.getElementById("top").innerHTML) == "top" ) { alert("set top tab with :"+top);
        //     document.getElementById("top").innerHTML = ""+top; }
    }
    
    if ( ! ready ) { 
      if ( ! stopped ) {  
        msg="No Action started";   if ( ! msgloaded ) { show=msg; }
      }   
    } else {
        if ( ! msgloaded ) { show="Action started" ; }
        //alert("MSG:"+msg+":");
        if ( msg.match("No Action started") ) { msg="poll for msg"; } else {
           if ( ! stopped ) { 
             msghttp=request();
             msghttp.onreadystatechange=function() {
                    if (msghttp.readyState===4 && msghttp.status===200 )
                    {
                        //alert("set text :"+tophttp.responseText);
                        var te=getLast(msghttp.responseText, "\n");
                        //alert("te|"+te+"|");
                        if ( te === "<!-- COMPLETE -->" ) {
                           document.getElementById("configGet").disabled=false;
                           document.getElementById("configStop").disabled=true;  
                           stopped=true;  
                        }
                          msg=""+msghttp.responseText;
                          document.getElementById("msg").innerHTML = ""+msghttp.responseText;;
                          msghttp="";
                          
                    }
             };
	     var ur="update.jsp?main=msg";
	     if ( debug > 0 ) { ur=ur+"&debug="+debug; }
             msghttp.open("GET", ur, true);
             msghttp.send();
          }  
        }
    }
    
}

function request(){
 var activexmodes=["Msxml2.XMLHTTP", "Microsoft.XMLHTTP"]; //activeX versions to check for in IE
 if ( window.ActiveXObject ){ //Test for support for ActiveXObject in IE first (as XMLHttpRequest in IE7 is broken)
  for (var i=0; i<activexmodes.length; i++){
   try{
    return new ActiveXObject(activexmodes[i]);
   }
   catch(e){
    //suppress error
   }
  }
 }
 else if (window.XMLHttpRequest) // if Mozilla, Safari etc
  return new XMLHttpRequest();
 else
  return false;
}
