@echo off
REM /******************************************************************************************/
REM AQuA Office Analyser recursive folder batch for Windows
REM License: APACHE 2.0
REM Author: Maurice de Rooij (OPF/NANETH) <maurice.de.rooij@nationaalarchief.nl>, August 2011
REM /******************************************************************************************/
REM USAGE: office_analyser_folder.bat "x:\path\to\office\files"
REM NOTE: ALWAYS enclose foldername in quotes to prevent Windows breaking names which contain whitespaces
REM OUTPUT: filename.doc.analysis.txt with analysis results is written next to every analysed file
REM ERRORS: error messages are also written to this analysis file
REM /******************************************************************************************/
@echo AQuA Office Analyser started
for /r %1 %%i in (*.doc) do echo Analysing file: %%i && java -jar office_analyser.jar "%%i" >"%%i.analyse.txt" 2>&1
@echo AQuA Office Analyser finished
