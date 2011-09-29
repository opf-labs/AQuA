<?php

$dbfile = "sqlite:volumes.db"; // DSN for sqlite database
$db = null; // database object

$action = "showVolumes"; // default action
$output = ""; // collect output in this

/**
 * arrToXML
 * turn 2d array into XML document
 * @param arr - 2d array
 * @param root - name of root node
 * @param row - name to give to each child of root
 * @return DOM object
 */
function arrToXML($arr, $root, $row) {
		// new DOM
		$dom = new DomDocument();
		
		// create and add root
		$rootEl = $dom->createElement($root);
		$dom->appendChild($rootEl);
		
		// loop over rows in table
		foreach ($arr as $r) {
				// create node for row and add to root
				$rowEl = $dom->createElement($row);
				$rootEl->appendChild($rowEl);
				
				// loop over cels in row
				foreach ($r as $n => $v) {
						// create node for cell and add to row
						$cellEl = $dom->createElement($n, $v);
						$rowEl->appendChild($cellEl);
				}
		}
		
		return $dom;
}

/**
 * xmlToHTML
 * transform XML document to HTML string
 * @param xml - DOM containing XML
 * @param stylesheet - name of stylesheet to use
 * @param params - optional array of parameter names and values
 * @return HTML as string
 */
function xmlToHTML($xml, $stylesheet, $params=null) {
		$output = "";
		
		do {
				// get stylesheet into DOM
				$xsltDom = new DomDocument();
				if (!$xsltDom->load($stylesheet)) {
						$output = "couldn't load stylesheet";
						break;
				}
				
				// get DOM into XSLT processor
				$xslt = new XSLTProcessor();
				$xslt->importStylesheet($xsltDom);
				
				// add any parameters
				if (is_array($params)) {
						$xslt->setParameter("", $params);
				}
				
				// transform XML to get output
				$output = $xslt->TransformToXML($xml);
				if (false === $output) {
						$output = "couldn't transform xml";
				}
		} while (false);
		
		return $output;
}

/**
 * showVolumes
 * default action is to show volumes in system
 * @param none
 * @return HTML output
 * @notes
 * uses global db object
 */
function showVolumes() {
		global $db;
		$output = "";
		
		do {
				if (!$showVolumesStmt = $db->prepare("SELECT volumeID, volumeName FROM Volumes ORDER BY volumeName")) {
						$output = "couldn't prepare statement";
						break;
				}
				
				if (!$showVolumesStmt->execute()) {
						$output = "couldn't execute statement";
						break;
				}
				
				$resultsArr = $showVolumesStmt->fetchAll(PDO::FETCH_ASSOC);
				$resultsXML = arrToXML($resultsArr, "volumes", "volume");
				$output = xmlToHTML($resultsXML, "showVolumes.xsl");
		} while (false);
		
		return $output;
}

/**
 * browseVolume
 * navigate the selected volume, using the supplied depth, x and y
 * @param volumeID - volumeID
 * @param depth - the depth, root=1
 * @param x - the x coord of the context node
 * @param y - the y coord of the context node
 * @return HTML output string
 * @notes
 * uses global db object
 */
function browseVolume($volumeID, $depth, $x, $y) {
		global $db;
		$output = "";
		
		do {
				$stmt = null;
				$sql = "SELECT f.fileID, f.volumeID, f.x, f.y, f.depth, f.fileType, f.filePath, f.fileName, u.userName, g.groupName FROM Files AS f
								 INNER JOIN Users AS u ON f.userID = u.userID
								 INNER JOIN Groups AS g On f.groupID = g.groupID
								 WHERE f.volumeID = :volumeID AND f.depth = 2 ORDER BY f.fileType, f.fileName";
				$args = array(":volumeID" => $volumeID);
				
				// using depth, x and y too
				if ($depth && $x && $y) {
						$sql = "SELECT f.fileID, f.volumeID, f.x, f.y, f.depth, f.fileType, f.filePath, f.fileName, u.userName, g.groupName FROM Files AS f
										 INNER JOIN Users AS u ON f.userID = u.userID
										 INNER JOIN Groups AS g On f.groupID = g.groupID
										 WHERE f.volumeID = :volumeID AND f.depth = :depth AND f.x > :x AND f.y < :y ORDER BY f.fileType, f.fileName";
						$args[":depth"] = $depth;
						$args[":x"] = $x;
						$args[":y"] = $y;
				}

				if (!$stmt = $db->prepare($sql)) {
						$output = "couldn't prepare statement";
						break;
				}
				
				if (!$stmt->execute($args)) {
						$output = "couldn't execute statement";
						break;
				}
				
				// remember current params in new page
				$params = array("volumeID" => $volumeID, "depth" => $depth, "x" => $x, "y" => $y);
				
				$resultsArr = $stmt->fetchAll(PDO::FETCH_ASSOC);
				$resultsXML = arrToXML($resultsArr, "files", "file");
				$output = xmlToHTML($resultsXML, "browseVolume.xsl", $params);
		} while (false);
		
		return $output;
}

/**
 * browseVolumeUp
 * Go up to parent of directory specified by depth, x and y in given volume
 * @param volumeID - volumeID
 * @param depth - the depth, root=1
 * @param x - the x coord of the context node
 * @param y - the y coord of the context node
 * @return HTML output string
 * @notes
 * uses global db object
 */
function browseVolumeUp($volumeID, $depth, $x, $y) {
		global $db;
		$output = "";
		
		do {
				$stmt = null;
				$sql = "SELECT f1.fileID, f1.volumeID, f1.x, f1.y, f1.depth, f1.fileType, f1.userID, f1.groupID, f1.filePath, f1.fileName FROM Files AS f1
								 INNER JOIN Files AS f2 WHERE f2.volumeID = :volumeID AND f2.depth = :depthParent AND f2.x < :x AND f2.y > :y AND 
								 f1.x > f2.x AND f1.y < f2.y AND f1.depth = :depthChild AND f1.volumeID = :volumeID ORDER BY f1.fileType, f1.fileName";
				$args = array(":volumeID" => $volumeID, ":depthParent" => $depth - 1,":depthChild" => $depth, ":x" => $x, ":y" => $y);

				if (!$stmt = $db->prepare($sql)) {
						$output = "couldn't prepare statement";
						break;
				}
				
				if (!$stmt->execute($args)) {
						$output = "couldn't execute statement";
						break;
				}
				
				// remember current params in new page
				$params = array("volumeID" => $volumeID, "depth" => $depth, "x" => $x, "y" => $y);
				
				$resultsArr = $stmt->fetchAll(PDO::FETCH_ASSOC);
				$resultsXML = arrToXML($resultsArr, "files", "file");
				$output = xmlToHTML($resultsXML, "browseVolume.xsl", $params);
		} while (false);
		
		return $output;
}

do {
		// open database
		$db = new PDO($dbfile);
		if (!$db) {
				$output = "couldn't open db";
				break;
		}
		
		// get the action
		if (isset($_REQUEST["action"])) {
				$action = $_REQUEST["action"];
		}
		
		switch ($action) {
		 case "browseVolume":
				// need volume ID
				$volumeID = 0;

				if (!isset($_REQUEST["volumeID"]) || !((int) $_REQUEST["volumeID"])) {
						$output = "need volume ID";
						break;
				}
				$volumeID = $_REQUEST["volumeID"];
				
				// depth, x and y are optional, but need all or none
				$depth = 0;
				$x = 0;
				$y = 0;

				if (isset($_REQUEST["depth"]) && (int) $_REQUEST["depth"]) {
						$depth = (int) $_REQUEST["depth"];
				}
				if (isset($_REQUEST["x"]) && (int) $_REQUEST["x"]) {
						$x = (int) $_REQUEST["x"];
				}
				if (isset($_REQUEST["y"]) && (int) $_REQUEST["y"]) {
						$y = (int) $_REQUEST["y"];
				}
				
				if (($depth || $x || $y) && !($depth && $x && $y)) {
						$output = "need all/none of depth, x and y";
						break;
				}
				
				$output = browseVolume($volumeID, $depth, $x, $y);
				break;
				
		 case "browseVolumeUp":
				// need volume ID
				$volumeID = 0;
				$depth = 0;
				$x = 0;
				$y = 0;

				if (!isset($_REQUEST["volumeID"]) || !((int) $_REQUEST["volumeID"])) {
						$output = "need volume ID";
						break;
				}
				
				if (!isset($_REQUEST["depth"]) || !(int) $_REQUEST["depth"]) {
						$output = "need depth";
						break;
				}
				
				if (!isset($_REQUEST["x"]) || !(int) $_REQUEST["x"]) {
						$output = "need x";
						break;
				}
				
				if (!isset($_REQUEST["y"]) || !(int) $_REQUEST["y"]) {
						$output = "need y";
				}

				$volumeID = (int) $_REQUEST["volumeID"];
				$depth = (int) $_REQUEST["depth"];
				$x = (int) $_REQUEST["x"];
				$y = (int) $_REQUEST["y"];
				
				$output = browseVolumeUp($volumeID, $depth, $x, $y);
				break;
		 case "showVolumes":
		 default:
				$output = showVolumes();
				break;
		}
} while (false);

print $output;

?>
