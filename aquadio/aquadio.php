<?php
/*
AQUAdio - prototype audio characterization tool
Version: 0.1
Author: Maurice de Rooij <maurice.de.rooij@nationaalarchief.nl>
License:GPL
*/


// configuration options
// set_time_limit (120) sets execution time to 120 seconds (PHP default: 30)
set_time_limit(120);
// set memory to 256M
ini_set('memory_limit', '256M');
// location of getID3() library
$getId3Lib = '../getid3/getid3/getid3.php';

/* ************************************************************** */
// DO NOT EDIT BELOW THIS LINE UNLESS YOU KNOW WHAT YOU ARE DOING
if(!defined('STDIN')) {
	die("This tool can only be run from commandline");
	} 

if(!isset($argv[1])) {
	die("This tool needs at least 1 argument: path");
	}

// include getID3() library (can be in a different directory if full path is specified)
require_once($getId3Lib);

// Initialize getID3 engine
$getID3 = new getID3;

if(!@opendir($argv[1])) {
	die("Invalid path specified: '{$argv[1]}'");
	}
$DirectoryToScan = $argv[1];
$dir = opendir($DirectoryToScan);

while (($file = readdir($dir)) !== false) {
	$FullFileName = realpath($DirectoryToScan."/".$file);
	// .3gpp files are being skipped because of bugs in getid3()
	// .xml files are being skipped because we don't want to analyse them 
	if (is_file($FullFileName) && !preg_match("/\.3gpp$|\.xml$/",$FullFileName)) {

		$ThisFileInfo = $getID3->analyze($FullFileName);
		$useEncoding = "ISO-8859-1";
		if($ThisFileInfo["encoding"]) {
			$useEncoding = $ThisFileInfo["encoding"];
			}
		$xmlFilename = "{$FullFileName}.xml";
		if(!@file_put_contents($xmlFilename,array2xml($ThisFileInfo))) {
			die("Could not write file '{$xmlFilename}'");
			}
		}

	// MdR: this needs some more attention!!!
	if (is_file($FullFileName) && preg_match("/\.3gpp$/",$FullFileName)) {
	$moveDir = $DirectoryToScan."/unrecognized";
		if(!is_dir($moveDir)) {
			mkdir($moveDir);
			}
		$moveFile = "./{$moveDir}/{$file}";
		rename($FullFileName, $moveFile);
		}
	}
//  bye bye!
exit;

/*
 * array2xml() will convert any given array into a XML structure.
 * Version:     1.0
 * Created by:  Marcus Carver © 2008
 * Email:       marcuscarver@gmail.com
 * Link:        http://marcuscarver.blogspot.com/
 * Arguments :  $array      - The array you wish to convert into a XML structure.
 *              $standalone - This will add a document header to identify this solely as a XML document.
 *              $beginning  - INTERNAL USE... DO NOT USE!
 * Return:      Gives a string output in a XML structure
 * Use:         echo array2xml($products,'products');
 *              die;
 * Modified by: Maurice de Rooij 
*/

function array2xml($array, $name='file', $standalone=FALSE, $beginning=TRUE) {
  $output = "";
  global $nested, $useEncoding;
  // delete the "tags_html" section (getID3 specific)
  if(isset($array["tags_html"])) {
  	unset($array["tags_html"]);
  	}
  if ($beginning) {
    if ($standalone) header("content-type:text/xml;charset=utf-8");
    $output .= '<'.'?'.'xml version="1.0" encoding="UTF-8"'.'?'.'>' . PHP_EOL;
    $output .= '<' . $name . '>' . PHP_EOL;
    $nested = 0;
  }
  
  // This is required because XML standards do not allow a tag to start with a number or symbol, you can change this value to whatever you like:
  $ArrayNumberPrefix = 'ID3_';
  
	foreach ($array as $root=>$child) {
    if (is_array($child)) {
      $output .= str_repeat(" ", (2 * $nested)) . '  <' . (is_string($root) ? $root : $ArrayNumberPrefix . $root) . '>' . PHP_EOL;
      $nested++;
      $output .= array2xml($child,NULL,NULL,FALSE);
      $nested--;
      $output .= str_repeat(" ", (2 * $nested)) . '  </' . (is_string($root) ? $root : $ArrayNumberPrefix . $root) . '>' . PHP_EOL;
    }
    else {
    	// MdR: this is needed because there might be NULL-characters and such in the ID3v* tags
    	$child = preg_replace('/[\x00-\x1F\x80-\xFF]/', '', $child);
    	// MdR: this is needed because there might be umlauts and such in the ID3v* tags
        $child = mb_convert_encoding($child, $useEncoding, 'UTF-8');
      $output .= str_repeat(" ", (2 * $nested)) . '  <' . (is_string($root) ? $root : $ArrayNumberPrefix . $root) . '><![CDATA[' . $child . ']]></' . (is_string($root) ? $root : $ArrayNumberPrefix . $root) . '>' . PHP_EOL;
    }
  }
  
  if ($beginning) $output .= '</' . $name . '>';
  
  return $output;
}

?>