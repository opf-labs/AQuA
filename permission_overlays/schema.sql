CREATE TABLE Volumes (
  volumeID INTEGER PRIMARY KEY,
	volumeName VARCHAR
);

CREATE TABLE Users (
  userID INTEGER PRIMARY KEY,
	userName VARCHAR,
	volumeID INTEGER
);

CREATE TABLE Groups (
  groupID INTEGER PRIMARY KEY,
	groupName VARCHAR,
	volumeID INTEGER
);

CREATE TABLE Files (
  fileID INTEGER PRIMARY KEY,
	volumeID INTEGER,
	x INTEGER,
	y INTEGER,
	depth INTEGER,
	fileType VARCHAR,
	userID INTEGER,
	groupID INTEGER,
	mode INTEGER,
	filePath VARCHAR,
	fileName VARCHAR
);

CREATE TABLE Flags (
  fileID INTEGER,
	flagValue VARCHAR,
	PRIMARY KEY (fileID, flagValue)
);

CREATE INDEX depthIDX ON Files (volumeID, depth);
CREATE INDEX depthXYIDX ON Files (volumeID, depth, x, y);
CREATE INDEX filePathIDX ON Files (volumeID, filePath);
