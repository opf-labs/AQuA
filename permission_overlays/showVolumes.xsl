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
	
	<xsl:template match="volumes">
		<h2>List of volumes</h2>
		
		<ul>
			<xsl:apply-templates/>
		</ul>
	</xsl:template>
	
	<xsl:template match="volume">
		<li>
			<a href="index.php?action=browseVolume&amp;volumeID={volumeID}">
				<xsl:apply-templates select="volumeName"/>
			</a>
		</li>
	</xsl:template>
	
</xsl:stylesheet>
