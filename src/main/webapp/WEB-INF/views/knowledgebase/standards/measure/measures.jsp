<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div id="measures_header">
		<spring:message code="label.title.measures" text="Measures" />
		:
		<spring:message text="${standard.label} - ${standard.version} - ${standard.description}" />
		<input type="hidden" id="idStandard" value="${standard.id}" /> <input type="hidden" id="standardLabel" value="${standard.label}" /> <input type="hidden" id="standardVersion" value="${standard.version}" />
		<div class="row" style="margin-top: 5px;margin-bottom:-15px">
			<c:if test="${!empty languages}">
				<div class="col-md-1">
					<select id="languageselect" class="form-control" style="width: auto;">
						<c:forEach items="${languages}" var="language">
							<option ${language.id == selectedLanguage.id?'selected="selected"':""} value="${language.id}"><spring:message text="${language.name}" /></option>
						</c:forEach>
					</select>
				</div>
			</c:if>
			<div class="col-md-1${!empty languages?'1':'2'}">
				<ul class="nav nav-pills" id="menu_measure_description">
					<li><a href="#" onclick="return newMeasure();"><span class="glyphicon glyphicon-plus primary"></span> <spring:message code="label.menu.add.norm" text="Add" /> </a></li>
					<li class="disabled" data-trick-selectable="true"><a href="#" onclick="return editSingleMeasure();"><span class="glyphicon glyphicon-edit danger"></span> <spring:message
								code="label.menu.edit.norm" text="Edit" /> </a></li>
					<li class="disabled pull-right" data-trick-selectable="true"><a href="#" class="text-danger" onclick="return deleteMeasure();"><span class="glyphicon glyphicon-remove"></span>
							<spring:message code="label.menu.delete.norm" text="Delete" /> </a></li>
				</ul>
			</div>
		</div>
</div>
<div id="measures_body">
		<c:choose>
			<c:when test="${!empty measureDescriptions}">
				<table id="measurestable" class="table table-hover ">
					<thead>
						<tr role="row">
							<th width="1%"></th>
							<th><spring:message code="label.measure.level" text="Level" /></th>
							<th><spring:message code="label.measure.reference" text="Reference" /></th>
							<th width="25%"><spring:message code="label.measure.domain" text="Domain" /></th>
							<th width="70%"><spring:message code="label.measure.description" text="Description" /></th>
							<th><spring:message code="label.measure.computable" text="Computable" /></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${measureDescriptions}" var="measureDescription">
							<tr data-trick-id="${measureDescription.id}" ondblclick="return editSingleMeasure('${measureDescription.id}','${standard.id}');">
								<td><input type="checkbox" class="checkbox" onchange="return updateMenu(this,'#section_measure_description','#menu_measure_description',undefined);"></td>
								<td>${measureDescription.level}</td>
								<td><spring:message text='${measureDescription.reference}' /></td>
								<td><spring:message text='${measureDescription.measureDescriptionTexts[0].domain.equals("")==false?measureDescription.measureDescriptionTexts[0].domain:""}' /></td>
								<td><pre><spring:message text='${measureDescription.measureDescriptionTexts[0].description.equals("")==false?measureDescription.measureDescriptionTexts[0].description:""}' /></pre></td>
								<td data-trick-computable="${measureDescription.computable}"><spring:message code="label.yes_no.${measureDescription.computable}"
										text="${measureDescription.computable?'Yes':'No'}" /></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</c:when>
			<c:otherwise>
				<h4>
					<spring:message code="label.measure.empty" text="No measure" />
				</h4>
			</c:otherwise>
		</c:choose>
</div>