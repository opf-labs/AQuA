<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Archive your web mail</title>
</head>
<body>
<form method="post" action="archive">
<center><h1>Archive your web mail</h1></center>
<br>
<table>
<tr><td>Username</td><td> <input type="text" size="50" name="username" /> </td></tr>
<tr><td>Password</td><td> <input type="password" size="50" name="password" /> </td></tr>
<tr><td>Provider</td><td> 
<select name="provider">
<option value="GOOGLE_IMAP" selected="selected">Gmail with IMAP</option>
<option value="YAHOO_IMAP">Yahoo with IMAP</option>
<option value="HOTMAIL">Hotmail with POP3</option>
<option value="GOOGLE">Gmail with POP3</option>
<option value="YAHOO">Yahoo with POP3</option>
</select>
</td></tr>
<tr><td>Folder to harvest</td><td> <input type="text" size="50" name="folder" value="INBOX"/> </td></tr>
<tr><td>&nbsp;</td><td> <input type="Submit" value="Archive" /> </td></tr> 
</table> 
</form>
<p><span style="color:red;">Warning :</span> with POP3 protocol, we can only harvest the INBOX folder.</p>
</body>
</html>