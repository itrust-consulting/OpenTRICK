<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="tab-pane" id="tab-analyses">
	<div class="section" id="section_profile_analysis">
		<ul class="nav nav-pills bordered-bottom" id="menu_analysis">
			<li class="disabled" data-trick-selectable="true"><a href="#" onclick="return selectAnalysis(undefined, 'true')"> <span class="glyphicon glyphicon-folder-open"></span>&nbsp;&nbsp;<spring:message
						code="label.action.open" /></a></li>
			<li class="disabled" data-trick-selectable="true"><a href="#" onclick="return editSingleAnalysis();"> <span class="glyphicon glyphicon-edit"></span> <spring:message
						code="label.properties" /></a></li>
			<li class="disabled" data-trick-selectable="true"><a href="#" onclick="return exportAnalysis()"> <span class="glyphicon glyphicon-export"></span> <spring:message
						code="label.menu.export.analysis" text="Export" /></a></li>
			<li class="disabled" data-trick-selectable="true" data-trick-check="!isDefaultProfile()"><a href="#" onclick="return setAsDefaultProfile(undefined, 'true')">&nbsp;&nbsp;<span
					class="glyphicon glyphicon-check"></span> <spring:message code="label.menu.analysis.set_default.profile" text="Set as default" /></a></li>
			<li class="disabled pull-right" data-trick-selectable="true" data-trick-check="!isDefaultProfile()"><a href="#" class="text-danger"
				onclick="return deleteAnalysisProfile();"> <span class="glyphicon glyphicon-remove"></span> <spring:message code="label.menu.delete.analysis" text="Delete" /></a></li>
		</ul>
		<table class="table table-striped table-hover">
			<thead>
				<tr>
					<th width="1%"></th>
					<th width="50%"><spring:message code="label.analysis.label" text="Name" /></th>
					<th width="5%"><spring:message code="label.analysis.type" text="Type" /></th>
					<th><spring:message code="label.analysis.creation_date" text="Create date" /></th>
					<th><spring:message code="label.analysis.owner" text="Owner" /></th>
					<th><spring:message code="label.analysis.language" text="Language" /></th>
					<th><spring:message code="label.analysis.profile.default" text="Default" /></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${analyses}" var="analysis">
					<tr data-trick-id="${analysis.id}" data-trick-profile-default="${analysis.defaultProfile}" onclick="selectElement(this)" data-trick-type="${analysis.type}"
						data-trick-rights-id="0" data-empty="${analysis.hasData()}" ondblclick="return editSingleAnalysis(${analysis.id});">
						<td><input type="checkbox" class="checkbox" onchange="return updateMenu(this,'#section_profile_analysis','#menu_analysis');"></td>
						<td><c:choose>
								<c:when test="${analysis.defaultProfile}">
									<i class="fa fa-file" aria-hidden="true" title='<spring:message code="label.analysis.default.profile"/>'></i>
								</c:when>
								<c:when test="${analysis.profile}">
									<i class="fa fa-file-o" aria-hidden="true" title='<spring:message code="label.analysis.profile"/>'></i>
								</c:when>
								<c:when test="${not analysis.data}">
									<i class="fa fa-folder-o" aria-hidden="true" title='<spring:message code="label.analysis.empty"/>'></i>
								</c:when>
								<c:when test="${analysis.archived}">
									<i class="fa fa-archive" aria-hidden="true" title='<spring:message code="label.analysis.archived"/>'></i>
								</c:when>
								<c:otherwise>
									<i class="fa fa-folder" aria-hidden="true" title='<spring:message code="label.analysis.editable"/>'></i>
								</c:otherwise>
							</c:choose> <span style="margin-left: 5px;"><spring:message text="${analysis.label}" /></span></td>
						<td><spring:message code='label.analysis.type.${fn:toLowerCase(analysis.type)}' text="${fn:toLowerCase(analysis.type)}" /></td>
						<td><fmt:formatDate value="${analysis.creationDate}" pattern="yyyy-MM-dd HH:mm" /></td>
						<td><spring:message text="${analysis.owner.getFirstName()} ${analysis.owner.getLastName()}" /></td>
						<td><spring:message text="${analysis.language.name}" /></td>
						<td><spring:message code="label.yes_no.${fn:toLowerCase(analysis.defaultProfile)}" text="${analysis.defaultProfile?'Yes':'No'}" /></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
</div>