<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<!-- ################################################################ Set Page Title ################################################################ -->

<c:set scope="request" var="title">title.analysis.import</c:set>

<!-- ###################################################################### HTML #################################################################### -->

<html>

<!-- Include Header -->
<jsp:include page="../header.jsp" />


<!-- ################################################################# Start Container ############################################################## -->

<body>
	<div class="container">

		<!-- ################################################################### Nav Menu ################################################################### -->

		<jsp:include page="../menu.jsp" />

		<!-- #################################################################### Content ################################################################### -->

		<div class="content" id="content">

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

					var ext = fileVal
							.substr(fileVal.length - 7, fileVal.length);

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

			<c:if test="${!empty customers}">
			
				<form:errors cssClass="error" element="div" />
				<jsp:include page="../successErrors.jsp" />

				<div class="form importAnalysisForm">
					<h1><spring:message code="label.analysis.import.title"	text="Import a new Analysis" /></h1>
					<spring:message code="label.analysis.import.description" text="Please select a customer, choose a sqlite file and click on submit" />

					<form id="importform" name="importform" method="post" action="Execute" enctype="multipart/form-data">
						<h2>1. <spring:message code="label.analysis.import.select.customer" /></h2>
						<form:select id="customerId" name="customerId" path="customerId" onchange="customerChanged()">
							<form:option value="-1"><spring:message code="label.action.choose" /></form:option>
							<form:options items="${customers}" itemLabel="contactPerson" itemValue="id" />
						</form:select>
						<br />
						<h2>2. <spring:message code="label.analysis.import.select.sqlite" /></h2>
						<input id="file" onchange="checkFile(true)" type="file" name="file" disabled />
						<br/><br/>
						<input id="validation" type="submit" value="<spring:message code="label.analysis.import.submit" />" />
					</form>
				</div>
			</c:if>
		</div>

		<!-- ################################################################ Include Footer ################################################################ -->

		<jsp:include page="../footer.jsp" />

		<!-- ################################################################ End Container ################################################################# -->

	</div>
</body>

<!-- ################################################################### End HTML ################################################################### -->

</html>