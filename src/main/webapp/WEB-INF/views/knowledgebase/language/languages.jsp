<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="section" id="section_language">
	<div class="page-header">
		<h3 id="Languages">
			<spring:message code="menu.knowledgebase.languages" />
		</h3>
	</div>
	<c:if test="${!empty languages}">
		<div class="panel panel-default">
			<div class="panel-heading">
				<button class="btn btn-default" onclick="newLanguage();">
					<spring:message code="label.language.add.menu" text="Add new Language" />
				</button>
			</div>
			<div class="panel-body">
				<table class="table">
					<thead>
						<tr>
							<th><spring:message code="label.language.alpha3" /></th>
							<th><spring:message code="label.language.name" /></th>
							<th><spring:message code="label.language.altName" /></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${languages}" var="language">
							<tr trick-id="${language.id}">
								<td><spring:message text="${language.alpha3}" htmlEscape="true"/></td>
								<td>${language.name}</td>
								<td>${language.altName}</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</c:if>
	<c:if test="${empty languages}">
		<div class="panel panel-default">
			<div class="panel-heading">
				<button class="btn btn-default" data-toggle="modal" data-target="#addLanguageModel">
					<spring:message code="label.language.add.menu" text="Add new Language" />
				</button>
			</div>
			<div class="panel-body">
				<h4>
					<spring:message code="label.language.notexist" />
				</h4>
			</div>
		</div>
	</c:if>
</div>