tiff2RDF
========
Peter Cliff, pete@pixelatedpete.co.uk
=====================================

Command line too that extracts metadata from TIFF files and 
creates RDF suitable for using to visually appraise the collection
for those metadata values.

For instance, you can "see" clusters around certain field values
like sample rate and any images outside of those clusters would
indicate a problem with those files.

It uses Apache Sanselan [http://commons.apache.org/sanselan/] and
JHOVE (via FITS) [http://hul.harvard.edu/jhove/] & [http://code.google.com/p/fits/]
to extract the metadata.

The source code is provided here (one class!). It is pretty 
straightforward to build in Eclipse - just import that one file
and then set about adding the libraries (these are those found
at the URLs above).

For convenience an executable jar file has been provided (see ./bin) that
can be run from the command line. You should refer to the README.txt
file there for instructions.

