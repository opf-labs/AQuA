# /******************************************************************************************/
# AQuA Office Analyser recursive folder batch for Linux
# License: APACHE 2.0
# Author: Maurice de Rooij (OPF/NANETH) <maurice.de.rooij@nationaalarchief.nl>, August 2011
# /******************************************************************************************/
# USAGE: office_analyser_folder.sh "/path/to/office/files"
# OUTPUT: filename.doc.analysis.txt with analysis results is written next to every analysed file
# ERRORS: error messages are also written to this analysis file
# /******************************************************************************************/
echo AQuA Office Analyser started
for f in $(find $1 -iname "*.doc")
do
if [ -a $f ]; then
echo Analysing file: $f
java -jar office_analyser.jar $f >$f.analyse.txt 2>&1
fi
done
echo AQuA Office Analyser finished
