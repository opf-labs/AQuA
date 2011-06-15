Name: AQUAdio
Purpose: prototype audio characterization tool
Version: 0.1
Platforms: Windows, Linux, Mac
Author: Maurice de Rooij <maurice.de.rooij@nationaalarchief.nl>
License: GPL

AQUAdio is a wrapper script around the Open Source getID3() PHP-library.
It extracts all possible information from audiofiles (MP3, WAV, MP4, AIFF, etc.) such as audio properties (bitrate, #channels, sample-frequency, etc.) and metadata (ID3v1, ID3v2, BWAVE metadata, etc) and writes the results to an XML file next to the audiofile.
An XSLT stylesheet can be applied to the XML to show the information in a styled way
The XML files can be also used by applications such as indexers. 

Files it does not recognize are moved to a subfolder 'unrecognized' in the same folder.

INSTALLATION INSTRUCTIONS
-------------------------
Install PHP (Windows/Mac/Linux)
http://www.php.net/

Download getID3() library
http://getid3.sourceforge.net/
Copy to a suitable location and set '$getId3Lib' to this location in 'aquadio.php'

USAGE INSTRUCTIONS
--------------------------
Windows:
aquadio.bat "x:\path\to\content"
or
php "x:\path\to\aquadio\aquadio.php" "x:\path\to\content" 

Mac / Linux:
php "/path/to/aquadio.php" "/path/to/content"
