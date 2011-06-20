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

Files it does not recognize are moved to a subfolder 'unrecognized' in the same folder. The folder will be created if sufficient permissions allow to do so and it does not exist yet.

Additionally the wrapper is able to create an MD5 fixity-file next to analysed file, if configured to do so in the script. By default this feature is turned off because it is very CPU-hungry. 

INSTALLATION INSTRUCTIONS
-------------------------
Install PHP (Windows/Mac/Linux)
http://www.php.net/

Download getID3() library
http://getid3.sourceforge.net/
Copy to a suitable location and set '$getId3Lib' to this location in 'aquadio.php'. Path can be absolute of relative.

USAGE INSTRUCTIONS
--------------------------
Windows:
aquadio.bat "x:\path\to\content"
or
php "x:\path\to\aquadio\aquadio.php" "x:\path\to\content" 

Mac / Linux:
aquadio.sh "/path/to/content"
php "/path/to/aquadio.php" "/path/to/content"

ADDITIONAL OPTIONS
---------------------------
To generate MD5 fixity files next to the files, you need to configure this by setting the variable '$md5generate' to 'true'. Please note that this is a CPU hogging option.
