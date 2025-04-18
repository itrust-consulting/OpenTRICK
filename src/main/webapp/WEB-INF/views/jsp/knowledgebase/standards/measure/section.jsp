<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div id="section_kb_measure" data-standard-id='${standard.id}' data-language-id='${selectedLanguage.id}'>
	<div class="page-header tab-content-header">
		<div class="container">
			<div class="row-fluid">
				<h3 id="section_title_measure">
					<c:if test="${not empty standard}">
						<spring:message text="${standard.name}: ${standard.label} - ${standard.version}. ${standard.description}" />
					</c:if>
				</h3>
			</div>
		</div>
	</div>
	<ul class="nav nav-pills bordered-bottom" id="menu_measure_description">
		<li data-trick-ignored="true"><a href="#" onclick="return newMeasure();"><span class="glyphicon glyphicon-plus primary"></span> <spring:message code="label.action.add" text="Add" /> </a></li>
		<li class="disabled" data-trick-selectable="true"><a href="#" onclick="return editSingleMeasure();"><span class="glyphicon glyphicon-edit danger"></span> <spring:message
					code="label.menu.edit.norm" text="Edit" /> </a></li>
		<c:if test="${!empty languages}">
			<li data-role='title' class="pull-right"><select id="languageselect" class="form-control" style="width: auto; margin-top: 5px;">
					<c:forEach items="${languages}" var="language">
						<option ${language.id == selectedLanguage.id?'selected="selected"':""} value="${language.id}"><spring:message text="${language.name}" /></option>
					</c:forEach>
			</select></li>
		</c:if>
		<sec:authorize access="hasAnyRole('ROLE_SUPERVISOR')">
			<li class="disabled pull-right" data-trick-selectable="true" title='<spring:message code="label.action.force.delete" text="Force delete"/>'><a href="#" class="text-danger"
				onclick="return deleteMeasure(true)"><span class="glyphicon glyphicon-remove-circle"></span> <spring:message code="label.action.force.delete" text="Force delete" /> </a></li>
		</sec:authorize>
		<li class="disabled pull-right" data-trick-selectable="true"><a href="#" class="text-danger" onclick="return deleteMeasure();"><span class="glyphicon glyphicon-remove"></span>
				<spring:message code="label.menu.delete.norm" text="Delete" /> </a></li>
	</ul>
	<table class="table table-hover" data-fh-scroll-multi="0.995">
		<thead>
			<tr role="row">
				<th width="1%"></th>
				<th><spring:message code="label.reference" text="Reference" /></th>
				<th width="25%"><spring:message code="label.measure.domain" text="Domain" /></th>
				<th width="70%"><spring:message code="label.measure.description" text="Description" /></th>
				<th><spring:message code="label.measure.computable" text="Computable" /></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${measureDescriptions}" var="measureDescription">
				<c:set var="measureDescriptionText" value="${measureDescription.findByLanguage(selectedLanguage)}" />
				<tr class="${measureDescription.computable?'':'active' }" data-trick-id="${measureDescription.id}" onclick="selectElement(this)" ondblclick="return editSingleMeasure('${measureDescription.id}','${standard.id}');">
					<td><input type="checkbox" class="checkbox" onchange="return updateMenu(this,'#section_kb_measure','#menu_measure_description');"></td>
					<td data-trick-field='reference'><spring:message text='${measureDescription.reference}' /></td>
					<c:choose>
						<c:when test="${empty measureDescriptionText}">
							<td data-trick-content='text'></td>
							<td data-trick-content='text'></td>
						</c:when>
						<c:otherwise>
							<td data-trick-content='text'><spring:message text='${measureDescriptionText.domain}' /></td>
							<td data-trick-content='text'><spring:message text='${measureDescriptionText.description}' /></td>
						</c:otherwise>
					</c:choose>


					<td data-trick-field='computable' data-trick-real-value="${measureDescription.computable}"><spring:message code="label.yes_no.${measureDescription.computable}"
							text="${measureDescription.computable?'Yes':'No'}" /></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div id="hidden-standard-data" style="display: none;">
		<c:if test="${not empty standard }">
			<input type="hidden" id="idStandard" value="${standard.id}" />
			<input type="hidden" id="standardLabel" value="${standard.label}" />
			<input type="hidden" id="standardVersion" value="${standard.version}" />
		</c:if>
	</div>
</div>
