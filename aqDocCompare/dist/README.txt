AQDC - AQuA Document Compare
----------------------------
I've included the war file and a self-contained Jetty instance 
including the war file for convenience. Some one might want
to check the licensing of all the libraries, but I'm pretty
sure they're "free software" of one flavour or another.

Trying it Out
=============
To run the standalone Jetty server you first need to ensure you
have a Java Runtime on your machine. I've configured it to run
on port 8085 in case you already have tomcat running! :-)

Next unzip the file somewhere handay and then:

*nix
====
On the command line:

> cd aqdc_jetty
> java -jar start

Then open your browser and go to:

http://localhost:8085/

Windows XP
==========
Same as *nix really. 
Use Start -> Run and then type "cmd" and click "Run" to get a command line.

> cd aqdc_jetty
> java -jar start

Nb. that if you are using IE you may be required to "Enable Intranet Settings" 
before it'll work. You will probably also be promoted to unblock Java 
in the Windows firewall.

I do not have access to a Windows 7 machine to test this but other
than a few more security hoops, I suspect it'll work.
