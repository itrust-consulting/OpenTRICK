<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="tab-pane" id="tab-analyses">
	<div class="section" id="section_profile_analysis">
		<jsp:include page="../../template/successErrors.jsp" />
		<ul class="nav nav-pills bordered-bottom" id="menu_analysis">
			<li class="disabled" data-trick-selectable="true"><a href="#" onclick="return selectAnalysis(undefined, 'true')"> <span class="glyphicon glyphicon-folder-open"></span>&nbsp;&nbsp;<spring:message
						code="label.menu.open.profile" text="Open Profile" /></a></li>
			<li class="disabled" data-trick-selectable="true"><a href="#" onclick="return setAsDefaultProfile(undefined, 'true')"> <span class="glyphicon glyphicon-pushpin"></span> <spring:message
						code="label.menu.analysis.set_default.profile" text="Set as default" /></a></li>
			<li class="disabled" data-trick-selectable="true"><a href="#" onclick="return editSingleAnalysis();"> <span class="glyphicon glyphicon-align-justify"></span> <spring:message
						code="label.edit.info" text="Edit info" /></a></li>
			<li class="disabled" data-trick-selectable="true"><a href="#" onclick="return exportAnalysis()"> <span
							class="glyphicon glyphicon glyphicon-export"></span> <spring:message code="label.menu.export.analysis" text="Export" /></a></li>
			<li class="disabled pull-right" data-trick-selectable="true"><a href="#" class="text-danger" onclick="return deleteAnalysis();"> <span class="glyphicon glyphicon-remove"></span> <spring:message
						code="label.menu.delete.analysis" text="Delete" /></a></li>
		</ul>
		<table class="table table-hover">
			<thead>
				<tr>
					<th width="1%"></th>
					<th width="50%"><spring:message code="label.analysis.label" text="Name"/></th>
					<th width="5%"><spring:message code="label.analysis.type" text="Type" /></th>
					<th><spring:message code="label.analysis.creation_date" text="Create date"/></th>
					<th><spring:message code="label.analysis.owner" text="Owner" /></th>
					<th><spring:message code="label.analysis.language" text="Language"/></th>
					<th><spring:message code="label.analysis.profile.default" text="Default" /></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${analyses}" var="analysis">
					<tr data-trick-id="${analysis.id}" onclick="selectElement(this)" data-trick-type="${analysis.type}" data-trick-rights-id="0" data-empty="${analysis.hasData()}" ondblclick="return editSingleAnalysis(${analysis.id});">
						<td><input type="checkbox" class="checkbox" onchange="return updateMenu(this,'#section_profile_analysis','#menu_analysis');"></td>
						<td><spring:message text="${analysis.label}"/></td>
						<td><spring:message code='label.analysis.type.${fn:toLowerCase(analysis.type)}' text="${fn:toLowerCase(analysis.type)}" /></td>
						<td><fmt:formatDate value="${analysis.creationDate}" pattern="yyyy-MM-dd HH:mm"/></td>
						<td><spring:message text="${analysis.owner.getFirstName()} ${analysis.owner.getLastName()}"/></td>
						<td><spring:message text="${analysis.language.name}"/></td>
						<td><spring:message code="label.yes_no.${fn:toLowerCase(analysis.defaultProfile)}" text="${analysis.defaultProfile?'Yes':'No'}" /></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
</div>