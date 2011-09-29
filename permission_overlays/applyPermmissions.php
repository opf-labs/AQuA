<?php

/*
 * apply permissions (ownership) to files in the system
 * by default, everything in a volume is owned by admin/admin
 */

/**
 * regularUsers
 * look for regular users in Documents and Settings,
 * create users and user group
 * make everything under users home directories owned by user
 * @param volumeName - name of volume
 * @return boolean - true on success
 * @notes
 * uses global $db
 */
function regularUsers($volumeName) {
		global $db;
		
		$success = false;
		// get file name (name of user), x and y for home directories where the admin user/group is currently the owner
		$lookForUsersSql = "SELECT fileName, x, y FROM Files INNER JOIN Volumes ON Volumes.volumeID = Files.volumeID 
												 INNER JOIN Users ON Users.userID = Files.userID
												 INNER JOIN Groups ON Groups.groupID = Files.groupID
												 WHERE Volumes.volumeName = :volumeName AND filePath = 'Documents and Settings' 
												 AND Users.userName = 'admin' and Groups.groupName = 'admin' AND
												 fileName NOT IN ('Administrator', 'All Users', 'Default User', 'LocalService', 'NetworkService')";
		$addUserSql = "INSERT INTO Users (userID, userName, volumeID) 
			SELECT NULL, :userName, volumeID FROM Volumes WHERE volumeName = :volumeName";
		$addGroupSql = "INSERT INTO Groups (groupID, groupName, volumeID) 
			SELECT NULL, :groupName, volumeID FROM Volumes WHERE volumeName = :volumeName";
		$updatePermissionsSql = "UPDATE Files SET userID = :userID, groupID = :groupID
															WHERE volumeID = (SELECT volumeID FROM Volumes WHERE volumeName = :volumeName)
															AND x >= :x AND y <= :y";
		
		do {
				// prepare statements
				if (!$lookForUsersStmt = $db->prepare($lookForUsersSql)) {
						break;
				}
				if (!$addUserStmt = $db->prepare($addUserSql)) {
						break;
				}
				if (!$addGroupStmt = $db->prepare($addGroupSql)) {
						break;
				}
				if (!$updatePermissionsStmt = $db->prepare($updatePermissionsSql)) {
						break;
				}
				
				// get regular users
				if (!$lookForUsersStmt->execute(array(":volumeName" => $volumeName))) {
						break;
				}
				
				$users = $lookForUsersStmt->fetchAll(PDO::FETCH_ASSOC);
				
				// have user/s, so create user group
				if (!$addGroupStmt->execute(array(":groupName" => "users", ":volumeName" => $volumeName))) {
						break;
				}
				$groupID = $db->lastInsertID();
				
				// loop over users
				foreach ($users as $user) {
						// add user
						if (!$addUserStmt->execute(array(":userName" => $user["fileName"], ":volumeName" => $volumeName))) {
								break 2;
						}
						$userID = $db->lastInsertID();
						
						// update all files under user's home directory
						if (!$updatePermissionsStmt->execute(array(":volumeName" => $volumeName, ":userID" => $userID, ":groupID" => $groupID, ":x" => $user["x"], ":y" => $user["y"]))) {
								break 2;
						}
				}
				
				$success = true;
		} while (false);
		
		return $success;
}

$dbfile = "sqlite:volumes.db";
$db = null;
$error = "";

do {
		// need volume name
		if (!isset($argv[1])) {
				$error = "need name of volume";
				break;
		}
		$volumeName = $argv[1];
		
		// open database
		$db = new PDO($dbfile);
		if (!$db) {
				$error = "couldn't open db";
				break;
		}
		
		// start transaction
		if (!$db->beginTransaction()) {
				$error = "couldn't start transaction";
				break;
		}
		
		// do something
		if (!regularUsers($volumeName)) {
				$error = "couldn't update regular users";
				break;
		}
		
		// commit transaction
		if (!$db->commit()) {
				$error = "couldn't commit transaction";
				break;
		}

} while (false);

// some error occured
if ($error) {
		// roll back transaction
		if ($db && $db->inTransaction()) {
				$db->rollBack();
		}
		
		die($error . "\n");
}

?>
