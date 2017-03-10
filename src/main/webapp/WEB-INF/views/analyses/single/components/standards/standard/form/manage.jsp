<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<div id="section_manage_standards" class="tab-pane active" style="height: 500px; overflow-y: auto; overflow-x: hidden; margin-top: -10px">
	<ul id="menu_manage_standards" class="nav nav-pills bordered-bottom">
		<li><a href="#available_standards" data-toggle="tab" role='import'><span class="glyphicon glyphicon-import"></span> <spring:message code="label.action.import" /></a></li>
		<li><a href="#standard_form_container" data-toggle="tab" role='add'><span class="glyphicon glyphicon-plus primary"></span> <spring:message code="label.action.add" /></a></li>
		<li data-trick-selectable="true" data-trick-check="isAnalysisOnlyStandard('#section_manage_standards')" class="disabled"><a href="#standard_form_container" data-toggle="tab"
			role='edit'><span class="glyphicon glyphicon-edit primary"></span> <spring:message code="label.action.edit" /></a></li>
		<li data-trick-selectable="true" class="disabled pull-right"><a onclick="return removeStandard();" class="text-danger" href="#"><span class="glyphicon glyphicon-remove"></span>&nbsp;<spring:message
					code="label.action.remove" /></a></li>
	</ul>
	<table class="table table-hover" id="table_current_standard">
		<thead>
			<tr>
				<th>&nbsp;</th>
				<th><spring:message code="label.norm.label" /></th>
				<th><spring:message code="label.norm.version" /></th>
				<th colspan="3"><spring:message code="label.norm.description" /></th>
				<th class="text-center"><spring:message code="label.norm.computable" /></th>
				<th class="text-center"><spring:message code="label.norm.type" /></th>
				<th class="text-center"><spring:message code="label.norm.analysisOnly" /></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${currentStandards}" var="standard">
				<tr ondblclick="return editStandard(this);" onclick="selectElement(this)" data-trick-id="${standard.id}" data-trick-analysisOnly="${standard.analysisOnly}" data-trick-type="${standard.type}"
					data-trick-computable="${standard.computable}">
					<td><input type="checkbox" class="checkbox" onchange="return updateMenu(this,'#section_manage_standards','#menu_manage_standards');"></td>
					<td><spring:message text="${standard.label}" /></td>
					<td><spring:message text="${standard.version}" /></td>
					<td colspan="3"><spring:message text="${standard.description}" /></td>
					<td class="text-center"><spring:message code="label.${standard.computable?'yes':'no'}" /></td>
					<td class="text-center"><spring:message code="label.norm.standard_type.${fn:toLowerCase(standard.type)}" /></td>
					<td class="text-center"><spring:message code="label.${standard.analysisOnly?'yes':'no'}" /></td>
				</tr>
			</c:forEach>
			<c:if test="${empty currentStandards}">
				<tr>
					<td colspan="9"><spring:message code="label.no_standards" /></td>
				</tr>
			</c:if>
		</tbody>
	</table>
</div>