<?php

$filename = "";
$dbfile = "sqlite:volumes.db";

$parser = null;
$fp = null;

$db = null;
$insertVolumeStmt = null;
$insertFileStmt = null;

$error = "";
$values = null;
$fileValues = null;
$where = null;
$counter = 1;

/**
 * insert volume into database
 * @param none
 * @return volumeID returned as last insert ID
 * @notes
 * uses global variables $insertVolumeStmt, $values and $db
 */
function insertVolume() {
		global $insertVolumeStmt, $values, $db;
		
		$insertVolumeStmt->execute(array(":volumeName" => $values["image_filename"]));
		
		return $db->lastInsertID();
}

/**
 * insert user into database
 * @param userName - name of user
 * @return userID returned as last insert ID
 * @notes
 * uses global variables $insertUserStmt, $values and $db
 */
function insertUser($userName) {
		global $insertUserStmt, $values, $db;
		
		$insertUserStmt->execute(array(":volumeID" => $values["volumeID"], ":userName" => $userName));
		
		return $db->lastInsertID();
}

/**
 * insert group into database
 * @param groupName - name of group
 * @return groupID returned as last insert ID
 * @notes
 * uses global variables $insertGroupStmt, $values and $db
 */
function insertGroup($groupName) {
		global $insertGroupStmt, $values, $db;
		
		$insertGroupStmt->execute(array(":volumeID" => $values["volumeID"], ":groupName" => $groupName));
		
		return $db->lastInsertID();
}

/**
 * insert file into database
 * @param none
 * @return fileID returned as last insert ID
 * @notes
 * uses global variables $insertFileStmt, $fileValues, $values, $db, $counter
 */
function insertFile() {
		global $insertFileStmt, $fileValues, $values, $db, $counter;
		
		if (!isset($fileValues[0])) {
				print "uh ho\n";
				exit;
		}
		print "adding " . $fileValues[0]["fileName"] . "\n";
		
		++ $counter; // increment counter for next y
		$args = array(":volumeID" => $values["volumeID"],
									":fileType" => $fileValues[0]["fileType"], 
									":filePath" => dirname($fileValues[0]["fileName"]),
									":fileName" => basename($fileValues[0]["fileName"]),
									":x" => $fileValues[0]["x"],
									":y" => $counter,
									":depth" => count($fileValues),
									":userID" => $values["userID"],
									":groupID" => $values["groupID"],
									":mode" => $fileValues[0]["mode"]);
		
		$insertFileStmt->execute($args);

		return $db->lastInsertID();
}

/**
 * opening element handler
 * initialise some values ready for accepting new character data
 * @param parser - the parser resource
 * @param element - the name of the element
 * @param attributes - array of attribute names => values
 * @return none
 */
function startElement($parser, $element, $attributes) {
		global $where, $fileValues, $values, $counter;
		
		switch ($element) {
		 case "image_filename":
				$values["image_filename"] = "";
				array_unshift($where, $element);
				break;
		 case "fileobject":
				array_unshift($where, $element);
				break;
		 case "filename":
				$values["filename"] = "";
				array_unshift($where, $element);
				break;
		 case "name_type":
				$values["name_type"] = "";
				array_unshift($where, $element);
				break;
		 case "mode":
				$values["mode"] = 0;
				array_unshift($where, $element);
				break;
		 case "fiwalk":
				// initialise
				$fileValues = array(array("fileName" => ".", "fileType" => "d", "volumeID" => 0, "x" => $counter, "y" => 0, "mode" => 0));
				$where = array("");
				$values = array("image_filename" => "", "filename" => "", "name_type" => "", "volumeID" => 0, "userID" => 0, "groupID" => 0, "mode" => 0);
				break;
		 default:
				break;
		}
}

/**
 * character data handler
 * @param parser - the parser resource
 * @param data - character data
 * @return none
 */
function characterData($parser, $data) {
		global $where, $values;
		
		switch ($where[0]) {
		 case "image_filename":
				$values["image_filename"] = $data;
				break;
		 case "fileobject":
				break;
		 case "filename":
				$values["filename"] .= $data; // concatenate, as can be longer than one chunk
				break;
		 case "name_type":
				$values["name_type"] = $data;
				break;
		 case "mode":
				$values["mode"] = $data;
		 default:
				break;
		}
}

/**
 * closing element handler
 * @param parser - the parser resource
 * @param element - the name of the element
 * @return none
 */
function endElement($parser, $element) {
		global $where, $values, $fileValues, $counter;

		switch ($element) {
		 case "image_filename":
				// insert new volume, getting volumeID
				$values["volumeID"] = insertVolume();
				// add admin user/group for this volume
				$values["userID"] = insertUser("admin");
				$values["groupID"] = insertGroup("admin");
				array_shift($where);
				break;

		 case "fileobject":
				do {
						// skip . and .. directories
						// and anything called '.' (without quotes)
						// and anything under $OrphanFiles
						if ("/." == substr($values["filename"], -2) ||
								"/.." == substr($values["filename"], -3) ||
								"\$OrphanFiles" == substr($values["filename"], 0, 12) ||
								"." == $values["filename"]) {
								break;
						}
						
						print "processing " . $values["filename"] . "\n";
						
						// get most recent file (directory)
						$lastDir = $fileValues[0]["fileName"];
						
						// get dirname and basename of current file
						$p = dirname($values["filename"]);
						$f = basename($values["filename"]);
						
						print "comparing $lastDir and $p\n";
						// go up until current file/dir is child of last directory
						while ($lastDir != $p) {
								insertFile();
								array_shift($fileValues);
								$lastDir = dirname($lastDir);
						}
						
						// add current file/dir
						++ $counter; // increment counter for next x
						array_unshift($fileValues, array("fileName" => $values["filename"], "fileType" => $values["name_type"], "x" => $counter, "mode" => $values["mode"]));
						
						// directory, so handle it later in while loop above
						if ("d" == $values["name_type"]) {
								break;
						}
						
						// file, so insert and forget about it
						insertFile();
						array_shift($fileValues);
						
				} while (false);
				
				array_shift($where);
				break;
				
				// just remove most recent item from where array, to forget that we've been here
		 case "filename":
		 case "name_type":
		 case "mode":
				array_shift($where);
				break;
		 case "fiwalk":
				// catch any remaining files/directories in the queue
				while ($fileValues) {
						insertFile();
						array_shift($fileValues);
				}
				break;
		 default:
				break;
		}
}

do {
		// get name of XML file
		if (!isset($argv[1])) {
				$error = "no filename given";
				break;
		}
		$filename = $argv[1];
		
		// create parser
		$parser = xml_parser_create("ISO-8859-1");
		if (!$parser) {
				$error = "couldn't create parser";
				break;
		}
		
		// turn off case folding
		xml_parser_set_option($parser, XML_OPTION_CASE_FOLDING, 0);
		
		// set up handler functions
		if (!xml_set_element_handler($parser, "startElement", "endElement")) {
				$error = "couldn't set up element handlers";
				break;
		}
		
		if (!xml_set_character_data_handler($parser, "characterData")) {
				$error = "couldn't set up character data handler";
				break;
		}
		
		// open XML file
		if (!($fp = fopen($filename, "r"))) {
				$error = "couldn't open $filename";
				break;
		}
		
		// open database
		$db = new PDO($dbfile);
		if (!$db) {
				$error = "couldn't open db";
				break;
		}
		
		// create prepared statements
		$insertVolumeStmt = $db->prepare("INSERT INTO Volumes (volumeID, volumeName) VALUES (NULL, :volumeName)");
		$insertUserStmt = $db->prepare("INSERT INTO Users (userID, userName, volumeID) VALUES (NULL, :userName, :volumeID)");
		$insertGroupStmt = $db->prepare("INSERT INTO Groups (groupID, groupName, volumeID) VALUES (NULL, :groupName, :volumeID)");
		$insertFileStmt = $db->prepare("INSERT INTO Files (fileID, volumeID, fileType, filePath, fileName, x, y, depth, userID, groupID, mode) VALUES (NULL, :volumeID, :fileType, :filePath, :fileName, :x, :y, :depth, :userID, :groupID, :mode)");

		// start transaction
		if (!$db->beginTransaction()) {
				$error = "couldn't start transaction";
				break;
		}
		
		// read in the XML file
		while ($data = fread($fp, 4096)) {
				if (!xml_parse($parser, $data, feof($fp))) {
						$error = sprintf("XML error: %s at line %d",
														 xml_error_string(xml_get_error_code($parser)),
														 xml_get_current_line_number($parser));
						break;
				}
		}
		
		// finish transaction
		if (!$db->commit()) {
				$error = "couldn't commit transaction";
				break;
		}
} while(false);

if ($error) {
		if ($db->inTransaction()) {
				$db->rollBack();
		}
		die($error);
}

?>
