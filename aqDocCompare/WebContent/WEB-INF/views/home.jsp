<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="grid_12">
<div class="uiBlock">
<h2>Choose files...</h2>

<form action="<c:url value="/compare" />" method="POST" enctype="multipart/form-data">
<label for="original">Original Document</label>
<input name="original" type="file" />
<label for="migration">Migrated Document</label>
<input name="migration" type="file" />
<input type="submit" value="Compare" />
</form>

</div>
</div>

