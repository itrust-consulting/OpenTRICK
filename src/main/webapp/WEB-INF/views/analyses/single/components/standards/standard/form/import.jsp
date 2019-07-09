<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<table class="table table-hover" id='importStandardTable'>
	<thead>
		<tr>
			<th><spring:message code="label.norm.name" /></th>
			<th><spring:message code="label.norm.label" /></th>
			<th><spring:message code="label.norm.version" /></th>
			<th width="50%"><spring:message code="label.norm.description" /></th>
			<th class="text-center"><spring:message code="label.norm.computable" /></th>
			<th class="text-center"><spring:message code="label.norm.type" /></th>
			<th class="text-center"><spring:message code="label.actions" /></th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${availableStandards}" var="standard">
			<spring:message text="${standard.label}" var="label" />
			<tr ondblclick="return editStandard(this);" data-trick-name='${label}' data-trick-id="${standard.id}" data-trick-analysisOnly="${standard.analysisOnly}"
				data-trick-type="${standard.type}" data-trick-computable="${standard.computable}">
				<td hidden="hidden"><input type="checkbox" class="checkbox" onchange="return updateMenu(this,'#section_manage_standards','#menu_manage_standards');"></td>
				<td><spring:message text="${standard.name}" /></td>
				<td>${label}</td>
				<td><spring:message text="${standard.version}" /></td>
				<td><spring:message text="${standard.description}" /></td>
				<td class="text-center"><spring:message code="label.${standard.computable?'yes':'no'}" /></td>
				<td class="text-center"><spring:message code="label.norm.standard_type.${fn:toLowerCase(standard.type)}" /></td>
				<td hidden="hidden" class="text-center"><spring:message code="label.${standard.analysisOnly?'yes':'no'}" /></td>
				<td class="text-center"><button class="btn btn-xs btn-primary">
						<i class="fa fa-plus-circle"></i>
					</button></td>
			</tr>
		</c:forEach>
		<c:if test="${empty availableStandards}">
			<tr>
				<td colspan="8"><spring:message code="label.no_standards" /></td>
			</tr>
		</c:if>
	</tbody>
</table>