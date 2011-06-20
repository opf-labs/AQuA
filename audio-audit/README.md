Audio Audit
-----------

Prerequisites:

BWF MetaEdit
jHears (which requires SoX and Java)


Actions
-------

Cygwin/paths: the script was developed using Cygwin on Windows - it might be necessary
	to strip the 'cygpath' references when the environment is finalised.

Fingerprinting: currently optional ('-f') as jHears frequently does not find matches
	between the Master and Listening copies. Some tweaking of the server's .properties
	will likely be necessary (jHears also has an additional 'calibrator' sub-project
	which may be of some use). Potentially jHears could be replaced - 'libofa' is a
	likely candidate but during experimentation, did not fall into the 'just works'
	category.