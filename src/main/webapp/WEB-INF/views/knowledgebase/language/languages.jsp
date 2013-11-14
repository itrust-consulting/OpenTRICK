<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<div class="section">
	<div class="page-header">
		<h3 id="Languages">
			<spring:message code="menu.knowledgebase.languages" />
		</h3>
	</div>
	<a href="Add"><spring:message code="label.language.add.menu" /></a>
	<c:if test="${!empty languages}">
		<div class="panel panel-default">
			<div class="panel-heading">
				&nbsp;
			</div>
			<div class="panel-body">
				<table class="table">
					<thead>
						<tr>
							<th><spring:message code="label.language.id" text="id" /></th>
							<th><spring:message code="label.language.alpha3" /></th>
							<th><spring:message code="label.language.name" /></th>
							<th><spring:message code="label.language.altName" /></th>
							<th><spring:message code="label.action" /></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${languages}" var="language">
							<tr>
								<td>${language.id}</td>
								<td>${language.alpha3}</td>
								<td>${language.name}</td>
								<td>${language.altName}</td>
								<td>
									<a href="Edit/${language.id}"><spring:message code="label.action.edit" /></a>|
									<a href="Delete/${language.id}"><spring:message code="label.action.delete" /></a>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</c:if>

	<c:if test="${empty languages}">
		<h4>
			<spring:message code="label.language.notexist" />
		</h4>
	</c:if>
</div>