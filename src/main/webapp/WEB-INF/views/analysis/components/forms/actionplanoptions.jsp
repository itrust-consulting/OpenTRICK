<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="page-header" style="margin-top: 0; padding-top: 0">
	<h3 style="margin-top: 0; padding-top: 0">
		<spring:message code="label.title.norms" text="Standards" />
	</h3>
</div>
<p>
	<spring:message code="label.title.options.select_norm" text="Select a norm to compute the action plan" />
</p>
<p>
	<spring:message code="label.title.options.select_norm.info" text="(No options given means: all norms will be used to compute)" />
</p>
<form action="${pageContext.request.contextPath}/ActionPlan/Compute" method="post" class="form-horizontal" id="actionplancomputationoptionsform">
	<c:if test="${!empty(id)}">
		<input name="id" value="${id}" type="hidden">
	</c:if>
	<table class="table text-center">
		<c:choose>
			<c:when test="${!empty(norms)}">
				<thead>
					<tr>
						<c:forEach items="${norms}" var="analysisnorm">
							<td><b><spring:message text="${analysisnorm.norm.label}" /></b></td>
						</c:forEach>
					</tr>
				</thead>
				<tbody>
					<tr>
						<c:forEach items="${norms}" var="analysisnorm">
							<td><input type="checkbox" name="norm_${analysisnorm.id}" value="1" /></td>
						</c:forEach>
					</tr>
				</tbody>
			</c:when>
		</c:choose>
	</table>
	<h3>
		<spring:message code="label.title.uncertainty" text="Uncertainty computation" />
	</h3>
	<table class="table">
		<tbody>
			<tr>
				<td><p>
						<spring:message code="label.title.options.uncertainty" text="Optimistic and pessimistic computation" />
					</p></td>
				<td><input type="checkbox" name="uncertainty" value="1" /></td>
			</tr>
		</tbody>
	</table>
</form>