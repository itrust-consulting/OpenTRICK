<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="section" id="section_profile_analysis">
	<div class="page-header">
		<h3>
			<spring:message code="label.analysis.profile.title" text="Analysis profiles" />
		</h3>
		<jsp:include page="../../successErrors.jsp" />
	</div>
	<div class="panel panel-default">
		<div class="panel-heading" style="min-height: 60px">
			<ul class="nav nav-pills" id="menu_analysis">
				<li class="disabled" trick-selectable="true"><a href="#" onclick="return selectAnalysis(undefined, 'true')"> <span class="glyphicon glyphicon-folder-open"></span>&nbsp;&nbsp;<spring:message
							code="label.menu.open.analysis" text="Open Analysis" /></a></li>
				<li class="disabled" trick-selectable="true"><a href="#" onclick="return setAsDefaultProfile(undefined, 'true')"> <span class="glyphicon glyphicon-pushpin"></span> <spring:message
							code="label.menu.analysis.set_default.profile" text="Set as default" /></a></li>
				<li class="disabled" trick-selectable="true"><a href="#" onclick="return editSingleAnalysis();"> <span class="glyphicon glyphicon-edit"></span> <spring:message
							code="label.menu.edit.analysis.info" text="Edit info" /></a></li>
				<li class="disabled pull-right" trick-selectable="true"><a href="#" class="text-danger" onclick="return deleteAnalysis();"> <span class="glyphicon glyphicon-remove"></span> <spring:message
							code="label.menu.delete.analysis" text="Delete" /></a></li>
			</ul>
		</div>
		<div class="panel-body autofitpanelbodydefinition">
			<table class="table table-hover table-fixed-header">
				<thead>
					<tr>
						<th><input type="checkbox" class="checkbox" onchange="return checkControlChange(this,'analysis')" disabled="disabled"></th>
						<th colspan="4"><spring:message code="label.analysis.identifier" text="Name"/></th>
						<th colspan="10"><spring:message code="label.analysis.comment" text="Comment"/></th>
						<th colspan="3"><spring:message code="label.analysis.creation_date" text="Create date"/></th>
						<th colspan="3"><spring:message code="label.analysis.owner" text="Owner" /></th>
						<th colspan="3"><spring:message code="label.analysis.language" text="Language"/></th>
						<th colspan="2"><spring:message code="label.analysis.profile.default" text="Default" /></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${analyses}" var="analysis">
						<tr trick-id="${analysis.id}" trick-rights-id="0" data="${analysis.hasData()}" ondblclick="return editSingleAnalysis(${analysis.id});">
							<td><input type="checkbox" class="checkbox" onchange="return updateMenu(this,'#section_profile_analysis','#menu_analysis');"></td>
							<td colspan="4"><spring:message text="${analysis.identifier}"/></td>
							<td colspan="10"><spring:message text="${analysis.label}"/></td>
							<td colspan="3"><spring:message text="${analysis.creationDate}"/></td>
							<td colspan="3"><spring:message text="${analysis.owner.getFirstName()} ${analysis.owner.getLastName()}"/></td>
							<td colspan="3"><spring:message text="${analysis.language.name}"/></td>
							<td colspan="2"><spring:message code="label.yes_no.${fn:toLowerCase(analysis.defaultProfile)}" text="${analysis.defaultProfile?'Yes':'No'}" /></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
</div>