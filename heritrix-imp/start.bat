Rem If not available, set the JAVA_HOME folder
Rem set JAVA_HOME=

Rem If not available, set the DROID_HOME folder
Rem set DROID_HOME=

Rem And set the full path to the heritrix implementation
set HERITRIX-IMP=.\heritrix-imp.jar

set JAVA=%JAVA_HOME%\bin\java.exe
set DROID=%DROID_HOME%\droid-command-line-6.0.jar


set folder=%1
cd %folder%

for %%f in (*.arc) do (
    %JAVA% -cp %HERITRIX-IMP% org.arc2.ArcReader "%folder%\%%f"
    %JAVA% -jar %DROID% -p %folder%\%%f.droid -a "%folder%\.%%f" -R
    %JAVA% -jar %DROID% -p %folder%\%%f.droid -e "%folder%\%%f.csv"
    %JAVA% -cp %HERITRIX-IMP% org.arc2.CollateCsv "%folder%\%%f.csv"
)