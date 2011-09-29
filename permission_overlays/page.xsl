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
	
	<!-- basic page template -->
	<xsl:template match="/">
		<html>
			<head>
				<title>Volume browser</title>
			</head>
			<body>
				<h1>Volume browser</h1>
				
				<!-- go to specific XML -->
				<xsl:apply-templates/>
				
			</body>
		</html>
	</xsl:template>
	
</xsl:stylesheet>
