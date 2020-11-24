SSHWebProxy
http://www.ericdaugherty.com/dev/sshwebproxy

For full Installation and Usage details, please refer to:
http://www.ericdaugherty.com/dev/sshwebproxy/gettingstarted.html

###
Modes of Operation

SSHWebProxy has two modes of operation, 'normal', and 'restricted'.  The 'normal'
mode of operation allows individual users to be configured to access the system.
Once they are granted access, they may use create an SSH Connection to any host.

The 'restricted' mode of operation allows anyone to create an ssh connection to one
host.  There is no additional security applied beyond the authentication handled by
the host.

##
Normal Installation

This distribution contains a .WAR file that may be deployed in any
J2EE compliant Servlet container.  If you wish to use all the default
values, you can simply deploy the WAR and begin using it.

The server requires a properties file to function properly.  Be default,
the server will copy the properties file (sshwebproxy.properties) to the default
directory.  The default directory is the directory that the application server 
was started from.  This may not be the directory the script was run from, 
as several application server start scripts change the directory before starting 
the server.

To specify a properties file location other than the default directory, add
a command line parameter to the start script of the application server.  The
command line parameter should be:

-Dsshwebproxy.properties=<file location>

If you specify a location for the properties file, you must make sure that a
file exists at that location.  If not, the server will not start.  A sample
properties file is included with this distribution.

The properties are loaded automatically by the application every time the file is changed.
Therefore, users can be added or edited while the application is deployed without having
to restart or redeploy the application.

###
Restricted Mode

To install SSHWebProxy, simply extract the sshwebproxy.war from the binary download
and deploy it on your Servlet container.

In restricted mode, the only configuration neccessary is the host that all
users are restricted to. In this mode, the SSHWebProxy application will not prompt
the users to authenticate but will just connect them directly to the target host
(and the host will authenticate them).

To run in restricted mode, a command line parameter must be added to the start
script of the application server. The command line parameter should be:

-Dsshwebproxy.restricted=<hostname>


In many cases, the restricted mode will be used to limit connections to the local server.
In this case, the property would be:

-Dsshwebproxy.restricted=localhost



