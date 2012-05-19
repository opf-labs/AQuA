Heritrix-imp

Heritrix is used to crawl websites, see http://crawler.archive.org/

Each web resource is downloaded ( images, javascript, html, etc. ) and stored in an arc document; plus
some metadata such as the file's mimetype.

Problem:
As the mimetype is dependent on the server's response, it is an unreliable indicator as to the
file's identity.

Solution
Get another opinion as to the profile. This app's script will use DROID (see http://www.nationalarchives.gov.uk/)
to approximate the file's identity. But basically the analysis can be run with any profiling tool,
as long as it produces a csv with three DROID like keys: MIME_TYPE, URI and FILE 

How it works
 - For each ARC document in a folder, the script opens up that archive and dumps the files into a subfolder.
 - For each file therein; Droid profiling is applied.
 - The interm result is a csv file per arc document
 - A final comparison is made; resulting in a svn file per archive with three fields:
 1. The URL of the harvested file that resides in an Arc file;
 2. The webcrawlers original mimetype
 3. the Droid Mimetype

From a simple column comparison using the user's favorite csv display tool and some sorting
with it, the anomalies are immediatly made on a per file basis.

Prerequisites:
Java, Droid and a binary of heritrix-imp

To install
- Build this project with maven:
$ mvn clean package

And set the parameters in start.sh or start.bat accordingly

Command line
For linux:
./start [folder that contains .arc files]

For Windows:
start.bat [folder that contains .arc files]

Known issues
 - Droid console occassionally "hangs" after a shutdown of its profiling process;
 and shows a message like 2011-10-01 12:54:31,704  INFO Closing profile: 1317466465484
 Pressing Cntr+C seems to do the trick. Do not terminate the batch job and let the script continue.

 - The Heritrix does not process .gz files ( may be because of the developers JVM version ).
Use uncompressed .arc files only