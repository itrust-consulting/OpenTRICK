<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:if test="${empty locale }">
	<spring:eval expression="T(org.springframework.web.servlet.support.RequestContextUtils).getLocale(pageContext.request).language" var="locale" />
</c:if>
<c:set var="langue" value="${fn:toUpperCase(locale)}" />
<fmt:setLocale value="fr" scope="session" />
<div class="tab-pane" id="tab-scale-type">
	<div class='section row' id='section_kb_scale_type'>
		<ul class="nav nav-pills bordered-bottom" id="menu_kb_scale_type" style="margin-bottom: 10px;">
			<li><a href="#tab_kb_scale_type" onclick="return addScaleType()"><span class="glyphicon glyphicon-plus primary"></span> <spring:message code="label.action.add" /></a></li>
			<li class="disabled" data-trick-selectable="true"><a href="#tab_kb_scale_type" onclick="editScaleType()"><span class="glyphicon glyphicon-edit"></span> <spring:message
						code="label.action.edit" /> </a></li>
			<li class="disabled pull-right" data-trick-selectable="multi"><a href="#tab_kb_scale_type" onclick="deleteScaleType()" class="text-danger"><span
					class="glyphicon glyphicon-remove"></span> <spring:message code="label.action.delete" /> </a></li>
		</ul>
		<table class="table table-hover">
			<thead>
				<tr>
					<th width="1%" rowspan="3"><input type="checkbox" class="checkbox" onchange="return checkControlChange(this,'kb_scale_type')"></th>
					<th rowspan="3"><spring:message code="label.scale.name" text="Name" /></th>
					<th rowspan="3"><spring:message code="label.scale.acronym" text="Acronym" /></th>
					<th colspan="${empty languages? 1 : languages.size()*2}" style="text-align: center;"><spring:message code="label.scale.translantions" text="Tanslantions" /></th>
				</tr>
				<tr>
					<c:forEach items="${languages}" var="language">
						<th colspan="2" style="text-align: center;" ><spring:message text="${language.alpha2 == langue? language.name : language.altName}" /></th>
					</c:forEach>
				</tr>
				<tr>
					<c:forEach items="${languages}" var="language">
						<th><spring:message code='label.translation'/></th>
						<th><spring:message code='label.translation.short' /></th>
					</c:forEach>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${scaleTypes}" var="scaleType">
					<tr data-trick-id="${scaleType.id}" onclick="selectElement(this)" ondblclick="return editScaleType('${scaleType.id}');">
						<td><input type="checkbox" class="checkbox" onchange="return updateMenu(this,'#section_kb_scale_type','#menu_kb_scale_type');"></td>
						<td data-field-name="name"><spring:message text="${scaleType.name}" /></td>
						<td data-field-name="acronym"><spring:message text="${scaleType.acronym}" /></td>
						<c:forEach items="${languages}" var="language" >
							<td><spring:message text="${scaleType.translations[language.alpha2].name}" /></td>
							<td><spring:message text="${scaleType.translations[language.alpha2].shortName}" /></td>
						</c:forEach>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
</div>