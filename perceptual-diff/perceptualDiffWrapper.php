<?php
/*
Prototype PHP wrapper for Perceptual Diff v0.1
Author: Maurice de Rooij <maurice.de.rooij@nationaalarchief.nl>
License: APACHE2
*/

# OPTIONS
# location of Perceptual Diff binary
$pdBinary = "\\perceptualdiff\\perceptualdiff.exe"; 
# options to pass to Perceptuall Diff binary
# note: option 'verbose' is default for this wrapper, so it doesn't need to be added
$pdBinaryOptions = "-fov 85";
# folder of images to be compared
$imageDir = "\\perceptualdiff\\perceptualDiffWrapperPHP\\testimages";
# activate debug messages (true/false)
# otherwise it will only generate CSV output when images are identical
$debug = false;

# DO NOT EDIT BELOW THIS LINE UNLESS YOU KNOW WHAT YOU'RE DOING

$files = loadFileNames($imageDir);
$iterator = 0;
for($outerCount=0;$outerCount<count($files);$outerCount++) {
	for($innerCount=$iterator;$innerCount<count($files);$innerCount++) {
		if($innerCount != $iterator) {
			$command = "{$pdBinary} \"{$imageDir}\\{$files[$iterator]}\" \"{$imageDir}\\{$files[$innerCount]}\" -verbose {$pdBinaryOptions}";
			($debug) ? print "COMPARING: \"{$imageDir}\\{$files[$iterator]}\" WITH \"{$imageDir}\\{$files[$innerCount]}\"\n" : "";
			$output = shell_exec($command);
			#echo $output;
			if(preg_match("/PASS/", $output)) {
				$numPixelsDiff = 0;
				if(preg_match("/([0-9]{1,}) pixels are different/", $output, $tmpNum)) {
					$numPixelsDiff = $tmpNum[1];
					}
				else {
					($debug) ? print "UNKNOWN_ERROR,('{$command}')\n" : "";
					}
				echo "IMAGES_IDENTICAL,\"{$imageDir}\\{$files[$iterator]}\",\"{$imageDir}\\{$files[$innerCount]}\",{$numPixelsDiff}\n";
				}
			elseif(preg_match("/FAIL/", $output)) {
				($debug) ? print "IMAGES_NOT_IDENTICAL,\"{$imageDir}\\{$files[$iterator]}\",\"{$imageDir}\\{$files[$innerCount]}\"\n" : "";				
				}
			else {
				($debug) ? print "UNKNOWN_ERROR,('{$command}')\n" : "";
				}
			} // end $innerCount != $iterator
		} // end for $innerCount
	$iterator++;
	} // end for $outerCount

function loadFileNames($imageDir) {
	$files = array();
	if ($handle = opendir($imageDir)) {
	    while (false !== ($file = readdir($handle))) {
	        if ($file != "." && $file != "..") {
	            $files[] = $file;
	        }
	    }
	    closedir($handle);
	}
	return $files;
}
?>