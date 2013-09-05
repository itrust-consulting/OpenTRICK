<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<html>
<head>
<title><spring:message code="label.importAnalysis.title" /></title>
<style >
.error {
	color: #ff0000;
}
</style>
<script type="text/javascript">
	function customerChanged(){
		var e = document.getElementById("customerId");

		var strUser = e.options[e.selectedIndex].value;
		
		if (strUser==-1)
			document.getElementById("file").disabled = true;
		else
			document.getElementById("file").disabled = false;
	}
</script>
</head>
<body>
	<form:errors cssClass="error" element="div"/>
	<a href="${pageContext.request.contextPath}/index">Home</a>
	<c:if test="${!empty customers}">
		<form method="post" action="${pageContext.request.contextPath}/import/analysis/save.html"
			enctype="multipart/form-data">
			<form:select id="customerId" name="customerId" path="customerId" onchange="customerChanged()">
				<form:option value="-1">Select the customer</form:option>
				<form:options items="${customers}" itemLabel="contactPerson" itemValue="id"/>
			</form:select>
			<input id="file" type="file" name="file" disabled/>
			<input type="submit" />
		</form>
	</c:if>
</body>
</html>