#!/bin/sh

PRG=$0

# If not available, set the JAVA_HOME folder
# JAVA_HOME=

# If not available, set the DROID_HOME folder
# DROID_HOME=

# And set the full path to the heritrix implementation
HERITRIX-IMP=./heritrix-imp.jar

JAVA=$JAVA_HOME%/bin/java.exe
DROID=$DROID_HOME%/droid-command-line-6.0.jar


folder=$PRG
cd $folder

for f in ${folder}/*.arc ; do
    $JAVA -cp $HERITRIX-IMP org.arc2.ArcReader "$folder\$f"
    $JAVA -jar $DROID -p $folder%\$f.droid -a "$folder\.$f" -R
    $JAVA -jar $DROID -p $folder%\$f.droid -e "$folder\$f.csv"
    $JAVA -cp $HERITRIX-IMP org.arc2.CollateCsv "$folder\$f.csv"
)