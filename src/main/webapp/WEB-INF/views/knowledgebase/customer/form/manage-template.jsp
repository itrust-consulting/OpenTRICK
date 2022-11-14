<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div id="section_manage_customer_template" class="tab-pane active" style="height: 500px; overflow-y: auto; overflow-x: hidden; margin-top: -10px">
	<ul id="menu_manage_customer_template" class="nav nav-pills bordered-bottom">
		<c:if test="${customer.canBeUsed}">
			<li><a href="#form_customer_template" data-toggle="tab" role='add'><span class="glyphicon glyphicon-plus primary"></span> <spring:message code="label.action.add" /></a></li>
		</c:if>
		<li data-trick-selectable="true" data-trick-check="!isDefaultCustomTemplate('#section_manage_customer_template')" class="disabled"><a href="#form_customer_template"
			data-toggle="tab" role='edit'><span class="glyphicon glyphicon-edit primary"></span> <spring:message code="label.action.edit" /></a></li>
		<li data-trick-selectable="multi" class="disabled" data-trick-check="checkItemCount('#section_manage_customer_template')"><a role="download" href="#"><span
				class="glyphicon glyphicon-download"></span>&nbsp;<spring:message code="label.action.download" /></a></li>
		<c:if test="${customer.canBeUsed}">
			<li data-trick-selectable="multi" class="disabled pull-right" data-trick-check="!isDefaultCustomTemplate('#section_manage_customer_template')"><a role="delete"
				class="text-danger" href="#"><span class="glyphicon glyphicon-remove"></span>&nbsp;<spring:message code="label.action.remove" /></a></li>
		</c:if>

	</ul>
	<table class="table table-hover" id="table_current_standard">
		<thead>
			<tr>
				<th width="1%"><input type="checkbox" class="checkbox" onchange="return checkControlChange(this,'manage_customer_template')"></th>
				<th width="50%"><spring:message code='label.name' /></th>
				<th><spring:message code='label.type' /></th>
				<th><spring:message code='label.version' /></th>
				<th><spring:message code='label.language' /></th>
				<th width="23%"><spring:message code='label.last_update' /></th>
				<th width="10%"><spring:message code="label.file.size" text="Size" /></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${reportTemplates}" var="template">
				<tr class='${template.editable ? template.outToDate? "warning" : "" : customer.canBeUsed? "text-muted" : ""}' ondblclick="return editReportTempalte(this);" onclick="selectElement(this)"
					data-trick-id="${template.id}" data-trick-editable="${template.editable or not customer.canBeUsed}">
					<td><input type="checkbox" class="checkbox" onchange="return updateMenu(this,'#section_manage_customer_template','#menu_manage_customer_template');"></td>
					<td data-trick-field='label' data-trick-content="text"><spring:message text="${template.label}" /></td>
					<td data-trick-field='type' data-trick-real-value='<spring:message text="${template.type}"/>'><spring:message code='label.analysis.type.${fn:toLowerCase(template.type)}'
							text="${template.type}" /></td>
					<td data-trick-field='version'><spring:message text="${template.version}" /></td>
					<td data-trick-field='language' data-trick-real-value="${template.language.id}"><spring:message text="${template.language.name}" /></td>
					<td><fmt:formatDate value="${template.created}" type="both"/></td>
					<td><fmt:formatNumber value="${template.length/(1024*1024)}" maxFractionDigits="2" /> <spring:message code="label.metric.megabit" text="Mb" /></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>