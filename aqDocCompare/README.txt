AQuA Document Compare
=====================
By Peter Cliff pete@pixelatedpete.co.uk

Simple document compare demonstrator which, if nothing else,
shows how Tika can be used to compare document formats.


PLEASE NOTE
===========
This is in no way a fully functional system!

It uses Apache Tika's AutoDetectParser to read the documents so in
theory all the document types supported by Tika can be used, but
do not be surprised if you get 500 Internal Server Errors on
unsupported document types! :-)

PLEASE ALSO NOTE
================
It would be inadvisable to use this in a production environment or
on a public server without first adding taint checking, sanitizing,
etc. to the code. 

This demonstrator was not built with Web security in mind. Use on 
a public facing server at your own risk!
