<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
		version="1.0" 
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns="http://www.w3.org/1999/xhtml"
		>

	<xsl:output
			method="xml"
			encoding="UTF-8"
			indent="yes"
			doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
			doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
			/>
	
	<xsl:include href="page.xsl"/>
	
	<xsl:param name="volumeID"/>
	<xsl:param name="depth"/>
	<xsl:param name="x"/>
	<xsl:param name="y"/>
	
	<xsl:template match="files">
		<h2>List of files</h2>
		
		<h3>
			<xsl:apply-templates select="file[1]/filePath"/>
		</h3>
		
		<!-- not at root, so show link to parent -->
		<xsl:if test="$depth &gt; 2">
			<p>
				<a href="index.php?action=browseVolumeUp&amp;volumeID={$volumeID}&amp;depth={$depth - 1}&amp;x={$x}&amp;y={$y}">
					<xsl:text>up</xsl:text>
				</a>
			</p>
		</xsl:if>
		<table>
			<tr>
				<th>File/Directory</th>
				<th>User</th>
				<th>Group</th>
			</tr>
			<xsl:apply-templates/>
		</table>
	</xsl:template>
	
	<xsl:template match="file">
		<tr>
			<td>
				<a>
					<xsl:apply-templates select="fileType"/>
					<xsl:apply-templates select="fileName"/>
				</a>
			</td>
			<xsl:apply-templates select="userName"/>
			<xsl:apply-templates select="groupName"/>
		</tr>
	</xsl:template>
	
	<xsl:template match="fileType[.='d']">
		<xsl:attribute name="href">
			<xsl:value-of select="concat('index.php?action=browseVolume&amp;volumeID=', ../volumeID, '&amp;depth=', ../depth + 1, '&amp;x=', ../x, '&amp;y=', ../y)"/>
		</xsl:attribute>
	</xsl:template>
	
	<xsl:template match="fileType[not(.='d')]">
		<xsl:attribute name="href">
			<xsl:value-of select="concat('index.php?action=fetchFile&amp;fileID=', ../fileID)"/>
		</xsl:attribute>
	</xsl:template>
	
	<xsl:template match="userName|groupName">
		<td>
			<xsl:apply-templates/>
		</td>
	</xsl:template>
	
</xsl:stylesheet>
