<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<div id="section_manage_standards" class="tab-pane active" style="height: 510px; overflow-y: auto; overflow-x: hidden; margin-top: -10px">
	<ul id="menu_manage_standards" class="nav nav-pills bordered-bottom">
		<li data-trick-ignored="true" ><a href="#importFromKb" data-toggle="tab" role='import-kb'><span class="glyphicon glyphicon-plus"></span> <spring:message code="label.action.add" /></a></li>
		<li data-trick-ignored="true"><a href="#standard_form_container" data-toggle="tab" role='add'><span class="glyphicon glyphicon-pencil primary"></span> <spring:message code="label.action.create" /></a></li>
		<li data-trick-ignored="true" ><a href="#importFromFile" data-toggle="tab" role='import-file'><span class="glyphicon glyphicon-import"></span> <spring:message code="label.action.import" /></a></li>
		<li data-trick-selectable="true" data-trick-check="isAnalysisOnlyStandard('#section_manage_standards')" class="disabled"><a href="#standard_form_container" data-toggle="tab"
			role='edit'><span class="glyphicon glyphicon-edit primary"></span> <spring:message code="label.action.edit" /></a></li>
		<li data-trick-selectable="true" data-trick-check="isAnalysisOnlyStandard('#section_manage_standards')" class="disabled"><a href="#importFromFile" data-toggle="tab"
			role='import-file-update'><span class="glyphicon glyphicon-refresh primary"></span> <spring:message code="label.action.update" /></a></li>
		<c:if test="${canExport}">
			<li data-trick-selectable="true" data-trick-check="isAnalysisOnlyStandard('#section_manage_standards')" class="disabled" ><a href="#" onclick="return exportStandard();" role='export-file'><span class="glyphicon glyphicon-export"></span> <spring:message code="label.action.export" /></a></li>
		</c:if>
		
		
		<li data-trick-selectable="true" class="disabled pull-right"><a onclick="return removeStandard();" class="text-danger" href="#"><span class="glyphicon glyphicon-remove"></span>&nbsp;<spring:message
					code="label.action.remove" /></a></li>
	</ul>
	<table class="table table-hover" id="table_current_standard">
		<thead>
			<tr>
				<th>&nbsp;</th>
				<th><spring:message code="label.norm.name" /></th>
				<th><spring:message code="label.norm.label" /></th>
				<th><spring:message code="label.norm.version" /></th>
				<th width="30%"><spring:message code="label.norm.description" /></th>
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
					<td data-name='name'><spring:message text="${standard.name}" /></td>
					<td data-name='label'><spring:message text="${standard.label}" /></td>
					<td data-name='version'><spring:message text="${standard.version}" /></td>
					<td data-name='description'><spring:message text="${standard.description}" /></td>
					<td data-name='computable' data-real-value='${standard.computable}' class="text-center"><spring:message code="label.${standard.computable?'yes':'no'}" /></td>
					<td data-name='type' data-real-value='${standard.type}' class="text-center"><spring:message code="label.norm.standard_type.${fn:toLowerCase(standard.type)}" /></td>
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