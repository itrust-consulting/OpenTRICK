<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:if test="${empty locale }">
	<spring:eval expression="T(org.springframework.web.servlet.support.RequestContextUtils).getLocale(pageContext.request)" var="locale" />
</c:if>
<c:set var="langue" value="${fn:toUpperCase(locale.language)}" />
<fmt:setLocale value="fr" scope="session" />
<div class="tab-pane" id="tab_kb_impact">
	<div class='section row' id='section_kb_impact'>
		<ul class="nav nav-pills bordered-bottom" id="menu_kb_impact" style="margin-bottom: 10px;">
			<li><a href="#tab_kb_impact" onclick="return addScale()"><span class="glyphicon glyphicon-plus primary"></span> <spring:message code="label.action.add" /></a></li>
			<li class="disabled" data-trick-selectable="true"><a href="#tab_kb_impact" onclick="editScale()"><span class="glyphicon glyphicon-edit"></span> <spring:message
						code="label.action.edit" /> </a></li>
			<li class="disabled pull-right" data-trick-selectable="multi"><a href="#tab_kb_impact" onclick="deleteScale()" class="text-danger"><span
					class="glyphicon glyphicon-remove"></span> <spring:message code="label.action.delete" /> </a></li>
		</ul>
		<table class="table table-hover">
			<thead>
				<tr>
					<th width="1%" rowspan="2"><input type="checkbox" class="checkbox" onchange="return checkControlChange(this,'kb_impact')"></th>
					<th rowspan="2"><spring:message code="label.scale.name" text="Name" /></th>
					<th rowspan="2"><spring:message code="label.scale.acronym" text="Acronym" /></th>
					<th rowspan="2"><spring:message code="label.scale.level" text="Level" /></th>
					<th rowspan="2"><spring:message code="label.scale.max_value" text="Max value" /></th>
					<th colspan="${empty languages? 1 : languages.size()}" style="text-align: center;"><spring:message code="label.scale.translantions" text="Tanslantions" /></th>
				</tr>

				<tr>
					<c:forEach items="${languages}" var="language">
						<th><spring:message text="${language.alpha2 == langue? language.name : language.altName}" /></th>
					</c:forEach>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${scales}" var="scale">
					<tr data-trick-id="${scale.id}" onclick="selectElement(this)" ondblclick="return editScale('${scale.id}');">
						<td><input type="checkbox" class="checkbox" onchange="return updateMenu(this,'#section_kb_impact','#menu_kb_impact');"></td>
						<td data-field-name="name"><spring:message text="${scale.type.name}" /></td>
						<td data-field-name="acronym"><spring:message text="${scale.type.acronym}" /></td>
						<td data-field-name="level"><spring:message text="${scale.level}" /></td>
						<td data-field-name="maxValue"><fmt:formatNumber value="${scale.maxValue}" maxFractionDigits="0" /></td>
						<c:forEach items="${languages}" var="language" >
							<td><spring:message text="${scale.type.translations[language.alpha2]}" /></td>
						</c:forEach>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
</div>