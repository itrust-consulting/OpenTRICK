<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<div class="section" id="section_itemInformation">
	<div class="page-header">
		<h3 id="ItemInformation">
			<spring:message code="label.itemInformation" text="Item Information" />
		</h3>
	</div>
	<spring:eval
		expression="T(lu.itrust.business.TS.Analysis).SplitItemInformations(itemInformations)"
		var="itemInformationsSplited" />
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
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${itemInformationsSplited[0]}"
								var="itemInformation">
								<tr trick-class="ItemInformation"
									trick-id="${itemInformation.id}">
									<td>${itemInformation.description}</td>
									<td trick-field="value" trick-field-type="string" class="success"
										ondblclick="return editField(this);">${itemInformation.value}</td>
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
								<tr trick-class="ItemInformation"
									trick-id="${itemInformation.id}">
									<td>${itemInformation.description}</td>
									<td trick-field="value" trick-field-trype="string" class="success"
										ondblclick="return editField(this);">${itemInformation.value}</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
</div>