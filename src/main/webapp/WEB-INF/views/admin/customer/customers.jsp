<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="section" id="section_customer">
	<div class="page-header">
		<h3 id="Customers">
			<spring:message code="menu.knowledgebase.customers" />
		</h3>
	</div>
	<div class="panel panel-default">
		<div class="panel-heading" style="min-height: 60px">
			<ul class="nav nav-pills" id="menu_customer">
				<li><a href="#" onclick="return newCustomer();"><span class="glyphicon glyphicon-plus primary"></span> <spring:message code="label.customer.add" text="Add" /> </a></li>
				<li class="disabled" trick-selectable="true"><a href="#" onclick="return editSingleCustomer();"><span class="glyphicon glyphicon-edit danger"></span> <spring:message
							code="label.customer.edit" text="Edit" /> </a></li>
				<li class="disabled pull-right" trick-selectable="true"><a href="#" class="text-danger" onclick="return deleteCustomer();"><span class="glyphicon glyphicon-remove"></span> <spring:message
							code="label.customer.delete" text="Delete" /> </a></li>
				<c:if test="${!empty(adminView)}">
					<sec:authorize access="hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR')">
						<li class="disabled" trick-selectable="true"><a href="#" onclick="return manageUsers();"><span class="glyphicon glyphicon-remove"></span> <spring:message
									code="label.customer.manage.users" text="Manage user access" /> </a></li>
					</sec:authorize>
				</c:if>
			</ul>
		</div>
		<div class="panel-body autofitpanelbodydefinition">
			<c:choose>
				<c:when test="${!empty customers}">
					<table class="table">
						<thead>
							<tr>
								<th><input type="checkbox" class="checkbox" onchange="return checkControlChange(this,'customer')"></th>
								<th><spring:message code="label.customer.organisation" /></th>
								<th><spring:message code="label.customer.contactPerson" /></th>
								<th><spring:message code="label.customer.phoneNumber" /></th>
								<th><spring:message code="label.customer.email" /></th>
								<th><spring:message code="label.customer.address" /></th>
								<th><spring:message code="label.customer.city" /></th>
								<th><spring:message code="label.customer.ZIPCode" /></th>
								<th><spring:message code="label.customer.country" /></th>
								<c:if test="${!empty(adminView)}">
									<sec:authorize access="hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR')">
										<th><spring:message code="label.customer.can_be_used" text="Profile only" /></th>
									</sec:authorize>
								</c:if>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${customers}" var="customer">
								<tr trick-id="${customer.id}" ondblclick="return editSingleCustomer('${customer.id}');">
									<td><input type="checkbox" class="checkbox" onchange="return updateMenu('#section_customer','#menu_customer');"></td>
									<td><spring:message text="${customer.organisation}" /></td>
									<td><spring:message text="${customer.contactPerson}" /></td>
									<td><spring:message text="${customer.phoneNumber}" /></td>
									<td><spring:message text="${customer.email}" /></td>
									<td><spring:message text="${customer.address}" /></td>
									<td><spring:message text="${customer.city}" /></td>
									<td><spring:message text="${customer.ZIPCode}" /></td>
									<td><spring:message text="${customer.country}" /></td>
									<c:if test="${!empty(adminView)}">
										<sec:authorize access="hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR')">
											<td trick-real-value="${customer.canBeUsed}"><spring:message code="label.yes_no.${!customer.canBeUsed}" text="${!customer.canBeUsed}" /></td>
										</sec:authorize>
									</c:if>
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