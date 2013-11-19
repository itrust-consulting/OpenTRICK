<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>

<div class="section">
	<div class="page-header">
		<h3 id="Customers">
			<spring:message code="menu.knowledgebase.customers" />
		</h3>
	</div>
	<c:if test="${!empty customers}">
		<div class="panel panel-default">
			<div class="panel-heading">
				<button class="btn btn-default" data-toggle="modal"
					data-target="#addCustomerModel">
					<spring:message code="label.customer.add.menu"
						text="Add new Customer" />
				</button>
			</div>
			<div class="panel-body">
				<table class="table">
					<thead>
						<tr>
							<th><spring:message code="label.customer.contactPerson" /></th>
							<th><spring:message code="label.customer.organisation" /></th>
							<th><spring:message code="label.customer.address" /></th>
							<th><spring:message code="label.customer.city" /></th>
							<th><spring:message code="label.customer.ZIPCode" /></th>
							<th><spring:message code="label.customer.country" /></th>
							<th><spring:message code="label.customer.telephoneNumber" /></th>
							<th><spring:message code="label.customer.email" /></th>
							<th><spring:message code="label.action" /></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${customers}" var="customer">
							<tr>
								<td>${customer.contactPerson}</td>
								<td>${customer.organisation}</td>
								<td>${customer.address}</td>
								<td>${customer.ZIPCode}</td>
								<td>${customer.city}</td>
								<td>${customer.country}</td>
								<td>${customer.telephoneNumber}</td>
								<td>${customer.email}</td>
								<td><a href="Edit/${customer.id}"><spring:message
											code="label.action.edit" /></a>|<a href="Delete/${customer.id}"><spring:message
											code="label.action.delete" /></a></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</c:if>

	<c:if test="${empty customers}">
		<div class="panel panel-default">
			<div class="panel-heading">
				<button class="btn btn-default" data-toggle="modal"
					data-target="#addCustomerModel">
					<spring:message code="label.customer.add.menu"
						text="Add new Customer" />
				</button>
			</div>
			<div class="panel-body">
				<h4>
					<spring:message code="label.customer.notexist" />
				</h4>
			</div>
		</div>
	</c:if>
</div>