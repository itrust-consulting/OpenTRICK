<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>

<!-- ################################################################ Set Page Title ################################################################ -->

<c:set scope="request" var="title">title.knowledgebase.Norm.Update</c:set>

<!-- ###################################################################### HTML #################################################################### -->

<html>

<!-- Include Header -->
<jsp:include page="../header.jsp" />


<!-- ################################################################# Start Container ############################################################## -->

<body>

	<div id="wrap">
		<!-- ################################################################### Nav Menu ################################################################### -->

		<jsp:include page="../menu.jsp" />

		<div class="container">
			<!-- #################################################################### Content ################################################################### -->

			<div class="content" id="content">

				<h1>
					<spring:message code="label.norm.update.form" />
					: ${norm.label}
				</h1>

				<a href="../Display"><spring:message code="menu.navigate.back" /></a>

				<form:errors cssClass="error" element="div" />
				<c:if test="${!empty norm}">
					<form:form method="post" action="../Update/${norm.id}"
						commandName="norm">
						<table class="table">
							<tr>
								<td><spring:message code="label.norm.id" /></td>
								<td><input type="hidden" id="id" name="id"
									value="${norm.id}" />${norm.id}</td>
							</tr>
							<tr>
								<td><spring:message code="label.norm.label" /></td>
								<td><input id="label" name="label" type="text"
									value="${norm.label}"></td>
							</tr>
							<tr>
								<td colspan="2"><input type="submit"
									value="<spring:message code="label.norm.update.form" />"></td>
							</tr>
						</table>
					</form:form>
				</c:if>
			</div>


			<!-- ################################################################ End Container ################################################################# -->

		</div>

		<!-- ################################################################ Include Footer ################################################################ -->

		<jsp:include page="../footer.jsp" />
		<jsp:include page="../scripts.jsp" />
	</div>
</body>

<!-- ################################################################### End HTML ################################################################### -->

</html>