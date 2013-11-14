<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<div class="section">
	<div class="page-header">
		<h3 id="Norms">
			<spring:message code="menu.knowledgebase.norms" />
		</h3>

		<a href="Add"><spring:message code="label.norm.add.menu" /></a>
		<c:if test="${!empty norms}">
			<div class="panel panel-default">
				<div class="panel-heading">&nbsp;</div>
				<div class="panel-body">
					<table class="table">
						<thead>
							<tr>
								<th><spring:message code="label.norm.id" text="id" /></th>
								<th><spring:message code="label.norm.label" /></th>
								<th><spring:message code="label.action" /></th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${norms}" var="norm">
								<tr>
									<td>${norm.id}</td>
									<td>${norm.label}</td>
									<td>
										<a href="${norm.label}/Measures"><spring:message code="label.action.norm.showMeasures" /></a> |
										<a href="Edit/${norm.label}"><spring:message code="label.action.edit" /></a>| 
										<a href="Delete/${norm.label}"><spring:message code="label.action.delete" /></a>
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
		</c:if>

		<c:if test="${empty norms}">
			<h4>
				<spring:message code="label.norm.notexist" />
			</h4>
		</c:if>
	</div>
</div>