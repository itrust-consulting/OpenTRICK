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
				<li class="disabled" trick-selectable="true"><a href="#" onclick="return selectAnalysis(undefined, 'true')"> <span class="glyphicon glyphicon-pushpin"></span> <spring:message
							code="label.analysis.pin" text="Edit Analysis" /></a></li>
				<li class="disabled" trick-selectable="true"><a href="#" onclick="return editSingleAnalysis();"> <span class="glyphicon glyphicon-edit"></span> <spring:message
							code="label.analysis.editInfo" text="Edit info" /></a></li>
				<li class="disabled" trick-selectable="true"><a href="#" onclick="return deleteAnalysis();"> <span class="glyphicon glyphicon-remove"></span> <spring:message
							code="label.analysis.delete" text="Delete" /></a></li>
			</ul>
		</div>
		<div class="panel-body">
			<table class="table table-hover">
				<thead>
					<tr>
						<th><input type="checkbox" class="checkbox" onchange="return checkControlChange(this,'analysis')"></th>
						<th><spring:message code="label.analysis.identifier" /></th>
						<th><spring:message code="label.analysis.label" /></th>
						<th><spring:message code="label.analysis.creationDate" /></th>
						<th><spring:message code="label.analysis.author" /></th>
						<th><spring:message code="label.analysis.language" /></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${analyses}" var="analysis">
						<tr trick-id="${analysis.id}" trick-rights-id="0" data="${analysis.hasData()}" ondblclick="return editSingleAnalysis(${analysis.id});">
							<td><input type="checkbox" class="checkbox" onchange="return updateMenu('#section_profile_analysis','#menu_analysis');"></td>
							<td>${analysis.identifier}</td>
							<td>${analysis.label}</td>
							<td>${analysis.creationDate}</td>
							<td>${analysis.getLastHistory().author}</td>
							<td>${analysis.language.name}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
</div>
<jsp:include page="widgetContent.jsp" />
<script type="text/javascript" src="<spring:url value="js/knowledgebase.js" />"></script>
