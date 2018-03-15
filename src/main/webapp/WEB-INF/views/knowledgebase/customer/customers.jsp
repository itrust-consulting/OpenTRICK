<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<div class="active tab-pane" id="tab-customer">
	<div class="section" id="section_customer">
		<ul class="nav nav-pills bordered-bottom" id="menu_customer">
			<li data-trick-ignored="true"><a href="#" onclick="return newCustomer();"><span class="glyphicon glyphicon-plus primary"></span> <spring:message code="label.action.add" text="Add" /> </a></li>
			<li class="disabled" data-trick-selectable="true"><a href="#" onclick="return editSingleCustomer();"><span class="glyphicon glyphicon-edit danger"></span> <spring:message
						code="label.action.edit" text="Edit" /> </a></li>
			<li class="disabled" data-trick-selectable="true"><a href="#" onclick="return editManageCustomer();"><span class="glyphicon glyphicon-list"></span> <spring:message
						code="label.action.manage.template" text="Manage template" /> </a></li>
			<li class="disabled pull-right" data-trick-selectable="true"><a href="#" class="text-danger" onclick="return deleteCustomer();"><span class="glyphicon glyphicon-remove"></span> <spring:message
						code="label.action.delete" text="Delete" /> </a></li>
		</ul>
		<c:choose>
			<c:when test="${!empty customers}">
				<table class="table table-striped table-hover">
					<thead>
						<tr>
							<th width="1%"></th>
							<th><spring:message code="label.customer.organisation" text="Company"/></th>
							<th><spring:message code="label.customer.contact_person" text="Contact person" /></th>
							<th><spring:message code="label.customer.phone_number" text="Phone number"/></th>
							<th><spring:message code="label.customer.email" text="Email address"/></th>
							<th><spring:message code="label.customer.address" text="Address"/></th>
							<th><spring:message code="label.customer.zip_code" text="Zip code" /></th>
							<th><spring:message code="label.customer.city" text="City"/></th>
							<th><spring:message code="label.customer.country" text="Country"/></th>
						</tr>
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
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</c:when>
			<c:otherwise>
				<h4>
					<spring:message code="label.customer.empty" text="No customer"/>
				</h4>
			</c:otherwise>
		</c:choose>
	</div>
</div>