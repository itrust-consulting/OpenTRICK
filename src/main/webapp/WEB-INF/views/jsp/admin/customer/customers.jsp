<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<div  class="tab-pane" id="tab-customer">
	<div class="section" id="section_customer">
		<ul class="nav nav-pills bordered-bottom" id="menu_customer">
			<li data-trick-ignored="true"><a href="#" onclick="return newCustomer();"><span class="glyphicon glyphicon-plus primary"></span> <spring:message code="label.action.add" text="Add" /> </a></li>
			<li class="disabled" data-trick-selectable="true"><a href="#" onclick="return editSingleCustomer();"><span class="glyphicon glyphicon-edit danger"></span> <spring:message
						code="label.action.edit" text="Edit" /> </a></li>
			<li class="disabled pull-right" data-trick-selectable="true"><a href="#" class="text-danger" onclick="return deleteCustomer();"><span class="glyphicon glyphicon-remove"></span>
					<spring:message code="label.action.delete" text="Delete" /> </a></li>
			<sec:authorize access="hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR')">
				<li class="disabled" data-trick-selectable="true" data-trick-check="isNotCustomerProfile()"><a href="#" onclick="return manageCustomerAccess();"><span
						class="glyphicon glyphicon-edit"></span> <spring:message code="label.menu.manage.access.user_customer" text="Manage user access" /> </a></li>
				<li class="disabled" data-trick-selectable="true" data-trick-check="isCustomerProfile()" ><a href="#" onclick="return manageCustomerTemplate();"><span class="glyphicon glyphicon-list"></span> <spring:message
						code="label.action.manage.template" text="Manage template" /> </a></li>
				<c:if test="${adminaAllowedTicketing}">
					<li class="disabled" data-trick-selectable="true" data-trick-check="isNotCustomerProfile() && isTicketingType('EMAIL')"><a href="#" onclick="return editTicketingSystemEmailTemplate();"><span
							class="glyphicon glyphicon-align-justify"></span> <spring:message code="label.menu.manage.ticketing.system.email.template" text="Manage email template for ticketing system" /> </a></li>
				</c:if>
			</sec:authorize>
		</ul>
		<c:choose>
			<c:when test="${!empty customers}">
				<c:set var="colSpan" value="${adminaAllowedTicketing? '2' : '1'}"/>
				<table class="table table-striped table-hover">
					<thead>
						<tr>
							<th rowspan="${colSpan}"></th>
							<th rowspan="${colSpan}"><spring:message code="label.customer.organisation" text="Company" /></th>
							<th rowspan="${colSpan}"><spring:message code="label.customer.contact_person" text="Contact person" /></th>
							<th rowspan="${colSpan}"><spring:message code="label.customer.phone_number" text="Phone number" /></th>
							<th rowspan="${colSpan}"><spring:message code="label.customer.email" text="Email address" /></th>
							<th rowspan="${colSpan}"><spring:message code="label.customer.address" text="Address" /></th>
							<th rowspan="${colSpan}"><spring:message code="label.customer.zip_code" text="Zip code" /></th>
							<th rowspan="${colSpan}"><spring:message code="label.customer.city" text="City" /></th>
							<th rowspan="${colSpan}"><spring:message code="label.customer.country" text="Country" /></th>
							<sec:authorize access="hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR')">
								<th rowspan="${colSpan}"><spring:message code="label.customer.can_be_used" text="Profile only" /></th>
								<c:if test="${adminaAllowedTicketing}">
									<th class="text-center" colspan="5"><spring:message code="label.ticketing.system"/></th>
								</c:if>
							</sec:authorize>
						</tr>
						<c:if test="${adminaAllowedTicketing}">
							<tr>
								<th><spring:message code="label.ticketing.system.enabled"/></th>
								<th><spring:message code="label.ticketing.system.name"/></th>
								<th><spring:message code="label.ticketing.system.type"/></th>
								<th><spring:message code="label.ticketing.system.tracker"/></th>
								<th><spring:message code="label.ticketing.system.url"/></th>
							</tr>
						</c:if>
					</thead>
					<tbody>
						<c:forEach items="${customers}" var="customer">
							<tr data-trick-id="${customer.id}" onclick="selectElement(this)" data-trick-is-profile="${not customer.canBeUsed}" ondblclick="return editSingleCustomer('${customer.id}');">
								<td><input type="checkbox" class="checkbox" onchange="return updateMenu(this,'#section_customer','#menu_customer');"></td>
								<td data-trick-name='organisation'><spring:message text="${customer.organisation}" /></td>
								<td data-trick-name='contactPerson'><spring:message text="${customer.contactPerson}" /></td>
								<td data-trick-name='phoneNumber'><spring:message text="${customer.phoneNumber}" /></td>
								<td data-trick-name='email'><spring:message text="${customer.email}" /></td>
								<td data-trick-name='address'><spring:message text="${customer.address}" /></td>
								<td data-trick-name='ZIPCode'><spring:message text="${customer.ZIPCode}" /></td>
								<td data-trick-name='city'><spring:message text="${customer.city}" /></td>
								<td data-trick-name='country'><spring:message text="${customer.country}" /></td>
								<sec:authorize access="hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR')">
									<td data-trick-name='canBeUsed' data-real-value="${customer.canBeUsed}"><spring:message code="label.yes_no.${fn:toLowerCase(!customer.canBeUsed)}"/></td>
									<c:if test="${adminaAllowedTicketing}">
										<td data-trick-name='tickecting_system_enabled' data-real-value="${customer.ticketingSystem.enabled==true}"><spring:message code="label.yes_no.${fn:toLowerCase(customer.ticketingSystem.enabled==true)}" /></td>
										<td data-trick-name='tickecting_system_name'><spring:message text="${customer.ticketingSystem.name}" /></td>
										<td data-trick-name='tickecting_system_type' data-real-value="${customer.ticketingSystem.type}"><spring:message code="label.ticketing.system.type.${fn:toLowerCase(customer.ticketingSystem.type)}" text="${customer.ticketingSystem.type}"/></td>
										<td data-trick-name='tickecting_system_tracker'><spring:message text="${customer.ticketingSystem.tracker}" /></td>
										<td data-trick-name='tickecting_system_url'><spring:message text="${customer.ticketingSystem.url}" /></td>
									</c:if>
									
								</sec:authorize>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</c:when>
			<c:otherwise>
				<h4>
					<spring:message code="label.customer.empty" text="No customer" />
				</h4>
			</c:otherwise>
		</c:choose>
	</div>
</div>