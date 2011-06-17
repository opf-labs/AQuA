<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>AQDC</title>

	<link href="<c:url value="/resources/css/960_12_col_fluid.css" />" media="screen" rel="stylesheet" type="text/css" />
	<link href="<c:url value="/resources/css/reset.css" />" media="screen" rel="stylesheet" type="text/css" />
	<link href="<c:url value="/resources/css/text.css" />" media="screen" rel="stylesheet" type="text/css" />
	<link href="<c:url value="/resources/css/aqdc.css" />" media="screen" rel="stylesheet" type="text/css" />
</head>
<body>
<div class="container_12">
<div class="grid_12"> <!-- start header block -->
<div id="header">
<h1>&nbsp;AQDC</h1>
</div>
</div>
<div class="clear"></div><!-- end header block -->

<!-- start content block -->
<tiles:insertAttribute name="content" />
<div class="clear"></div><!-- end content block -->

</div><!-- container_12 -->
</body>
</html>