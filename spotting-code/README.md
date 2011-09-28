Spotting Code
-------------

This simple solution describes how to use file and ohcount to compare their abilities to spot text files that are actually code.

n.b. MacPorts has a ohcount package, so installing that way is very easy.

For file, I used

<code>
find . -type f -exec file --mime-type {} \;  > ../file.txt
</code>

For ohcount I used

<code>
find . -type f -exec ohcount -d {} \; > ../ohcount.txt
</code>

I then reformatted file.txt to use tab separators, imported the data into Excel, generated hit rates and made pie charts.

For more, see

* http://wiki.opf-labs.org/display/REQ/Use+ohcount+to+detect+source+code+text+files

