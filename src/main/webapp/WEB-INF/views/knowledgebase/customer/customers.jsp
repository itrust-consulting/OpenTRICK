<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<div class="section" id="section_customer">
	<div class="page-header">
		<h3 id="Customers">
			<spring:message code="menu.knowledgebase.customers" />
		</h3>
	</div>
	<div class="panel panel-default"
		onmouseover="if(!$('#menu_customer').is(':visible')) {updateMenu('#section_customer', '#menu_customer');$('#menu_customer').show();}"
		onmouseout="$('#menu_customer').hide();">
		<div class="panel-heading" style="min-height: 60px">
			<ul class="nav nav-pills" hidden="true" id="menu_customer">
				<li><a href="#" onclick="return newCustomer();"><span
						class="glyphicon glyphicon-plus primary"></span> <spring:message
							code="label.customer.add" text="Add" /> </a></li>
				<li trick-selectable="true"><a href="#"
					onclick="return editSingleCustomer();"><span
						class="glyphicon glyphicon-edit danger"></span> <spring:message
							code="label.customer.edit" text="Edit" /> </a></li>
				<li trick-selectable="true"><a href="#"
					onclick="return deleteCustomer();"><span
						class="glyphicon glyphicon-remove"></span> <spring:message
							code="label.customer.delete" text="Delete" /> </a></li>
			</ul>
		</div>
		<div class="panel-body">
			<c:choose>
				<c:when test="${!empty customers}">
					<table class="table">
						<thead>
							<tr>
								<th><input type="checkbox" class="checkbox"
									onchange="return checkControlChange(this,'customer')"></th>
								<th><spring:message code="label.customer.organisation" /></th>
								<th><spring:message code="label.customer.contactPerson" /></th>
								<th><spring:message code="label.customer.telephoneNumber" /></th>
								<th><spring:message code="label.customer.email" /></th>
								<th><spring:message code="label.customer.address" /></th>
								<th><spring:message code="label.customer.city" /></th>
								<th><spring:message code="label.customer.ZIPCode" /></th>
								<th><spring:message code="label.customer.country" /></th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${customers}" var="customer">
								<tr trick-id="${customer.id}">
									<td><input type="checkbox" class="checkbox"
										onchange="return updateMenu('#section_customer','#menu_customer');">
									</td>
									<td>${customer.organisation}</td>
									<td>${customer.contactPerson}</td>
									<td>${customer.telephoneNumber}</td>
									<td>${customer.email}</td>
									<td>${customer.address}</td>
									<td>${customer.city}</td>
									<td>${customer.ZIPCode}</td>
									<td>${customer.country}</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</c:when>
				<c:otherwise>
					<h4>
						<spring:message code="label.customer.notexist" />
					</h4>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
</div>