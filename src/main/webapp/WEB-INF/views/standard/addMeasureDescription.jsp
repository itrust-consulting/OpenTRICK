<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>

<!-- ################################################################ Set Page Title ################################################################ -->

<c:set scope="request" var="title">title.knowledgebase.Measure.Add</c:set>

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
					<spring:message code="label.measure.add.menu" />
				</h1>
				<a href="../Measures/Display"><spring:message
						code="menu.navigate.back" /></a>
				<form:errors cssClass="error" element="div" />
				<form:form method="post" action="Create"
					commandName="measureDescription">
					<table class="table">
						<tr>
							<td><form:label path="reference">
									<spring:message code="label.measure.reference" />
								</form:label></td>
							<td><form:input path="reference" /></td>
						</tr>
						<tr>
							<td><form:label path="level">
									<spring:message code="label.measure.level" />
								</form:label></td>
							<td><form:input path="level" /></td>
						</tr>
						<tr>
							<td colspan="2"><spring:message code="language.English" /><input
								type="hidden" name="language_code_eng" id="language_code_eng"
								value="language_code_eng"></td>
						</tr>
						<tr>
							<td><label><spring:message
										code="label.measure.domain" /></label></td>
							<td><textarea id="domain_eng" name="domain_eng"></textarea></td>
						</tr>
						<tr>
							<td><label><spring:message
										code="label.measure.description" /></label></td>
							<td><textarea id="description_eng" name="description_eng"></textarea></td>
						</tr>
						<tr>
							<td colspan="2"><input type="submit"
								value="<spring:message code="label.measure.add.form"/>" /></td>
						</tr>
					</table>
				</form:form>
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