<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<form action="${pageContext.request.contextPath}/ActionPlan/Compute" method="post" class="form-horizontal" id="actionplancomputationoptionsform">
	<c:if test="${!empty(id)}">
		<input name="id" value="${id}" type="hidden">
	</c:if>
	<h3>
		<spring:message code="title.norms" text="Norms" />
	</h3>
	<spring:message code="title.options.selectnorm" text="<p>Select a norm to compute the action plan</p><p>(No options given means: all norms will be used to compute)</p>"
		htmlEscape="false"></spring:message>
	<table class="table text-center">
		<c:choose>
			<c:when test="${!empty(norms)}">
				<thead>
					<tr>
						<c:forEach items="${norms}" var="analysisnorm">
							<td><b><spring:message text="${analysisnorm.norm.label}" htmlEscape="true" /></b></td>
						</c:forEach>
					</tr>
				</thead>
				<tbody>
					<tr>
						<c:forEach items="${norms}" var="analysisnorm">
							<td><input type="checkbox" name="<spring:message text="norm_${analysisnorm.id}" htmlEscape="true"/>" value="1" /></td>
						</c:forEach>
					</tr>
				</tbody>
			</c:when>
		</c:choose>
	</table>
	<h3>
		<spring:message code="title.uncertainty" text="Uncertainty computation" />
	</h3>
	<table class="table">
		<tbody>
			<tr>
				<td><spring:message code="title.options.uncertainty" text="<p>Optimistic and pessimistic computation</p>" htmlEscape="false" /></td>
				<td><input type="checkbox" name="<spring:message text="uncertainty" htmlEscape="true"/>" value="1" /></td>
			</tr>
		</tbody>
	</table>
</form>