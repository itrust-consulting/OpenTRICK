<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<html>
<head>
<title><spring:message code="label.importAnalysis.title" /></title>
<link rel="stylesheet" type="text/css"
	href='<spring:url value="/css/main.css" />' />
<style>
.error {
	color: #ff0000;
}
</style>
<script type="text/javascript">
	function customerChanged() {
		var e = document.getElementById("customerId");

		var strUser = e.options[e.selectedIndex].value;

		if (strUser == -1) {
			document.getElementById("file").disabled = true;
			document.getElementById("validation").disabled = true;
		} else {
			document.getElementById("file").disabled = false;
			checkFile(false);
		}
	}

	function checkFile(b) {
		var fileVal = document.getElementById("file").value;

		var ext = fileVal.substr(fileVal.length - 7, fileVal.length);

		switch (ext) {
		case '.sqlite':
			document.getElementById("validation").disabled = false;
			break;
		default:
			document.getElementById("validation").disabled = true;
			if (b)
				alert("You must import a .sqlite file!");
			break;
		}
	}
</script>
</head>
<body>
	<div class="container">
		<div class="menu">
			<jsp:include page="menu.jsp" />
		</div>
		<div class="content" id="content">
			
			<form:errors cssClass="error" element="div" />

			<c:if test="${!empty customers}">
				
			</c:if>

			<jsp:include page="successErrors.jsp" />
			<div class="form importAnalysisForm">
				<h1>
					<spring:message code="label.analysis.import.titlePage"
								    text="Importing an analysis" />
				</h1>
				<spring:message code="label.analysis.import.descriptionPage"
					 		    text="Please select a customer, choose a sqlite file and click on submit" />
				
				<form method="post"
					  action="${pageContext.request.contextPath}/import/analysis/save.html"
					  enctype="multipart/form-data">
					<h2>1. Select a customer</h2>
					<form:select id="customerId" name="customerId" path="customerId" onchange="customerChanged()">
						<form:option value="-1">&nbsp;</form:option>
						<form:options items="${customers}" itemLabel="contactPerson" itemValue="id" />
					</form:select>
					<br/>
					<h2>2. Select a sqlite file</h2>
					<input id="file" onchange="checkFile(true)" type="file" name="file" disabled />
					<br/> 
					<br/>
					<input id="validation" type="submit" value="Start import" disabled />
				</form>
			</div>
		</div>
		<div class="footer">
			<jsp:include page="footer.jsp" />
		</div>
	</div>
</body>
</html>