tiff2RDF
========
Peter Cliff, pete@pixelatedpete.co.uk
=====================================

NOTE: 
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

Instructions
------------
1) Download and unpack FITS [http://code.google.com/p/fits/downloads/detail?name=fits-0.5.0.zip]

   Make a note of where you unpack it.

2) Put all the image file you want to process in a directory. 

   DO NOT put any other files in that directory, but you can have sub-directories of images in it.

   eg. C:\Images\35mm
                \Photos
                \Scans

   etc.

   tiff2RDF was developed during a mashup day and so does not include any error detection, etc.
   Making it more robust would probably be a "good thing" if you've the time! :-)

3) Open the command line (if y'are using Linux you probably already know how to get a terminal), on 
   Windows go Start -> Run and then type "cmd" in the box and hit "Run". 

4) Now we need to set the FITS_HOME env variable. You can do this the easy, but non-permanent way thus:

   Windows:
   -------
   At the command prompt type:

   > set FITS_HOME=[directory]

   Where [directory] is the place you unpacked FITS to and noted down in (1).

   Linux:
   -----
   Varies. I use bash so it is:

   > export FITS_HOME=[directory]o

Aside: I know, it would be good to make that a property instead and added via -D!

5) Now, finally, we can run the command:

   > java -jar tiff2RDF.jar [imageDirectory]

   Where [imageDirectory] is where you put the images in step 2!

   This will parse the images (can take a while) and output an RDF file.

   Note: This was only tested on a collection 35 images. For larger collections the
   code may not work. See how you go with (say, 100, then a 1000, etc.)

Visualising it!
==============

Having got the RDF file, you can now feed that into an RDF visualisation tool. For
the mashup we used Welkin [http://simile.mit.edu/welkin/]. Might be interesting
to try the RDF in Longwell as well [http://simile.mit.edu/longwell/].
