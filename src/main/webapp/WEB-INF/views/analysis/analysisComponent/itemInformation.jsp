<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<div class="section">
	<div class="page-header">
		<h3 id="itemInformation">
			<spring:message code="label.itemInformation" text="Item Information" />
		</h3>
	</div>
	<c:set scope="request" var="itemInformationsSplited"
		value="${analysis.SplitItemInformations(analysis.itemInformations)}" />

	<div class="row">
		<div class="col-md-12">
			<div class="panel panel-info">
				<div class="panel-heading">
					<spring:message code="label.itemInformation.scope" text="Scopes" />
				</div>
				<div class="panel-body">

					<table class="table">
						<thead>
							<tr>
								<th><spring:message
										code="label.itemInformation.description" text="Description" /></th>
								<th><spring:message code="label.itemInformation.value"
										text="Value" /></th>
								<th><spring:message code="label.action" /></th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${itemInformationsSplited[0]}"
								var="itemInformation">
								<tr>
									<!--<td>${itemInformation.id}</td>-->
									<td>${itemInformation.description}</td>
									<td>${itemInformation.value}</td>
									<td><a href="Edit/${itemInformation.id}"><spring:message
												code="label.action.edit" /></a>|<a
										href="Delete/${itemInformation.id}"><spring:message
												code="label.action.delete" /></a></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
		</div>
		<div class="col-md-12">
			<div class="panel panel-info">
				<div class="panel-heading">
					<spring:message code="label.itemInformation.organisation"
						text="Organisations" />
				</div>
				<div class="panel-body">

					<table class="table">
						<thead>
							<tr>
								<th><spring:message
										code="label.itemInformation.description" text="Description" /></th>
								<th><spring:message code="label.itemInformation.value"
										text="Value" /></th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${itemInformationsSplited[1]}"
								var="itemInformation">
								<tr ondblclick="return globalEdit(this, )">
									<td
										ondblclick="return editField(this, 'itemInformation','${itemInformation.id}', 'description');">${itemInformation.description}</td>
									<td
										ondblclick="return editField(this, 'itemInformation','${itemInformation.id}', 'value');">${itemInformation.value}</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
</div>