<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="grid_12">
<div class="uiBlock">
<c:if test="${not empty errors}">
<div class="actionFailure">
<h3>Errors</h3>
<p>${errors}</p>
</div>
<br />
</c:if>

<h3>Results</h3>
${normresult}
${wordcountresult}

<h3>Frequency</h3>
${freqresult}
<div class="clouds">
<div class="grid_6 alpha">
<div class="cloud">
${ocloud}
</div>
</div>
<div class="grid_6 omega">
<div class="cloud">
${pcloud}
</div>
</div>
</div>
<div class="clear"></div>
<br />
<h3>Tika Metadata</h3>
${metacompare}

<h3>Original</h3>
<c:if test="${not empty ourl}">
<p><a href="${ourl}">See as xhtml</a></p>
</c:if>
<pre>${fileOne}</pre>
<h3>Migration</h3>
<c:if test="${not empty purl}">
<p><a href="${purl}">See as xhtml</a></p>
</c:if>
<pre>${fileTwo}</pre>
</div>
</div>