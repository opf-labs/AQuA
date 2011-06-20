Office Format Analysis
----------------------

Have files in Word 97-2003 format, but issues within.
- This is not enough to ensure it is openable.
- e.g. Word 2000 files that will not open in 2003.
- Document how they fail.

Identification of creator software version
------------------------------------------
MS Has ProgID for different versions. http://msdn.microsoft.com/en-us/library/ms690196.aspx

http://msdn.microsoft.com/en-us/library/921afb2d-6fcd-45a9-9c2a-78e1fc08c5e5(v=PROT.10)#id7
<7> Section 2.2.4: In Windows the ClassName field contains the ProgID (see [MSDN-ProgID]) of a Component Object Model (COM) component (see [MSDN-COM]) belonging to the creating application.

http://msdn.microsoft.com/en-us/library/aa911706.aspx
CompObjStream http://msdn.microsoft.com/en-us/library/dd941977(v=prot.10).aspx
Compound File Binary Format http://msdn.microsoft.com/en-us/library/dd942138.aspx

See also http://support.microsoft.com/kb/928516


### Data types table ###
From http://support.microsoft.com/kb/162059

This table has some relevant definitions, but is referring to data types and not creating applications. 
Unfortunately, it's not clear how serious this is, i.e. if you save a 97-2003 Word document from Word 2010, 
does it embed the ProdID for the format or from the source application. I fear it's not supposed to matter 
but does anyway.

<pre>
Document Type                                                 		Subkey
----------------------------------------------------------------------------------------------------------
Microsoft Office Excel 95 Worksheet                      			Excel.Sheet.5
Microsoft Office Excel 97-2003 Worksheet                 			Excel.Sheet.8 
Microsoft Office Excel 2007-2010 Worksheet               			Excel.Sheet.12
Microsoft Office Excel 2007-2010 Macro-Enabled Worksheet 			Excel.SheetMacroEnabled.12
Microsoft Office Excel 2007-2010 Binary Worksheet        			Excel.SheetBinaryMacroEnabled.12  
Microsoft Office Word 95 Document                        			Word.Document.6
Microsoft Office Word 97-2003 Document                   			Word.Document.8
Microsoft Office Word 2007-2010 Document                 			Word.Document.12
Microsoft Office Word 2007-2010 Macro-Enabled Document   			Word.DocumentMacroEnabled.12
Rich Text Format                                         			Word.RTF.8
Microsoft Office PowerPoint 95 Presentation              			PowerPoint.Show.7
Microsoft Office PowerPoint 97-2003 Presentation         			PowerPoint.Show.8
Microsoft Office PowerPoint 2007-2010 Macro-Enabled Presentation    PowerPoint.ShowMacroEnabled.12
Microsoft Office PowerPoint 97-2003 Slide Show           			PowerPoint.SlideShow.8
Microsoft Office PowerPoint 2007-2010 Slide Show         			PowerPoint.SlideShow.12
Microsoft Office PowerPoint 2007-2010 Macro-Enabled Slide Show     	PowerPoint.SlideShowMacroEnabled.12
Microsoft Excel 7.0 worksheet                						Excel.Sheet.5
Microsoft Excel 97 worksheet	             						Excel.Sheet.8
Microsoft Excel 2000 worksheet               						Excel.Sheet.8
Microsoft Word 7.0 document                  						Word.Document.6
Microsoft Word 97 document                   						Word.Document.8
Microsoft Word 2000 document                 						Word.Document.8
Microsoft Project 98 project                 						MSProject.Project.8
Microsoft PowerPoint 2000 document           						PowerPoint.Show.8
</pre>



doc2x works, but leaves the embedded Visio as is.
http://b2xtranslator.sourceforge.net/


Embedded object analysis/extraction
-----------------------------------
e.g. Visio Drawing inside Word Document.

Dependency analysis 
-------------------
e.g. font reference extraction



