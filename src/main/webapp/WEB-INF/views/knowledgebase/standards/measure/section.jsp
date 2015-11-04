<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div id="section_kb_measure" data-standard-id='${standard.id}' data-language-id='${selectedLanguage.id}'>
	<div class="page-header tab-content-header">
		<div class="container">
			<div class="row-fluid">
				<h3 id="section_title_measure">
					<c:if test="${not empty standard}">
						<spring:message text="${standard.label} - ${standard.version}. ${standard.description}" />
					</c:if>
				</h3>
			</div>
		</div>
	</div>
	<ul class="nav nav-pills bordered-bottom" id="menu_measure_description">
		<li><a href="#" onclick="return newMeasure();"><span class="glyphicon glyphicon-plus primary"></span> <spring:message code="label.menu.add.norm" text="Add" /> </a></li>
		<li class="disabled" data-trick-selectable="true"><a href="#" onclick="return editSingleMeasure();"><span class="glyphicon glyphicon-edit danger"></span> <spring:message
					code="label.menu.edit.norm" text="Edit" /> </a></li>
		<c:if test="${!empty languages}">
			<li data-role='title' class="pull-right">
				<div>
					<select id="languageselect" class="form-control" style="width: auto;">
						<c:forEach items="${languages}" var="language">
							<option ${language.id == selectedLanguage.id?'selected="selected"':""} value="${language.id}"><spring:message text="${language.name}" /></option>
						</c:forEach>
					</select>
				</div>
			</li>
		</c:if>
		<sec:authorize access="hasAnyRole('ROLE_SUPERVISOR')">
			<li class="disabled pull-right" data-trick-selectable="true" title='<spring:message code="label.action.force.delete" text="Force delete"/>'><a href="#" class="text-danger"
				onclick="return forceDeleteMeasure();"><span class="glyphicon glyphicon-remove"></span> <spring:message code="label.action.force.delete" text="Force delete" /> </a></li>
		</sec:authorize>
		<li class="disabled pull-right" data-trick-selectable="true"><a href="#" class="text-danger" onclick="return deleteMeasure();"><span class="glyphicon glyphicon-remove"></span>
				<spring:message code="label.menu.delete.norm" text="Delete" /> </a></li>
	</ul>
	<table class="table table-hover ">
		<thead>
			<tr role="row">
				<th width="1%"></th>
				<th><spring:message code="label.measure.level" text="Level" /></th>
				<th><spring:message code="label.reference" text="Reference" /></th>
				<th width="25%"><spring:message code="label.measure.domain" text="Domain" /></th>
				<th width="70%"><spring:message code="label.measure.description" text="Description" /></th>
				<th><spring:message code="label.measure.computable" text="Computable" /></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${measureDescriptions}" var="measureDescription">
				<tr data-trick-id="${measureDescription.id}" ondblclick="return editSingleMeasure('${measureDescription.id}','${standard.id}');">
					<td><input type="checkbox" class="checkbox" onchange="return updateMenu(this,'#section_kb_measure','#menu_measure_description');"></td>
					<td>${measureDescription.level}</td>
					<td><spring:message text='${measureDescription.reference}' /></td>
					<td><spring:message text='${measureDescription.measureDescriptionTexts[0].domain.equals("")==false?measureDescription.measureDescriptionTexts[0].domain:""}' /></td>
					<td><pre>
									<spring:message text='${measureDescription.measureDescriptionTexts[0].description.equals("")==false?measureDescription.measureDescriptionTexts[0].description:""}' />
								</pre></td>
					<td data-trick-computable="${measureDescription.computable}"><spring:message code="label.yes_no.${measureDescription.computable}"
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
