<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="section" id="section_admin_analysis">
	<div class="page-header">
		<h3>
			<spring:message code="label.analysis.title" text="All Analyses" />
		</h3>
		<jsp:include page="../../successErrors.jsp" />
	</div>
	<div class="panel panel-default">
		<div class="panel-heading" style="min-height: 60px">
			<ul class="nav nav-pills" id="menu_analysis">
				<li><a href="#" onclick="return newAnalysis();"> <span class="glyphicon glyphicon-plus primary"></span> <spring:message code="label.analysis.add" text="New analysis" /></a></li>
				<li class="disabled" trick-selectable="true"><a href="#" onclick="return createAnalysisProfile(null,'section_admin_analysis');"> <span class="glyphicon glyphicon-file"></span>
						<spring:message code="label.analysis.createAnalysisProfile" text="Create new profile" /></a></li>
				<li class="disabled profilemenu"><a href="#" onclick="return manageAnalysisAccess(null, 'section_admin_analysis');"> <span class="glyphicon glyphicon-plus primary"></span>
						<spring:message code="label.analysis.manage.access" text="Manage Access Rights" /></a></li>
			</ul>
		</div>
		<div class="panel-body">
			<table class="table table-hover">
				<thead>
					<tr>
						<th><input type="checkbox" class="checkbox" onchange="return checkControlChange(this,'admin_analysis')"></th>
						<th><spring:message code="label.analysis.customer" /></th>
						<th><spring:message code="label.analysis.identifier" /></th>
						<th><spring:message code="label.analysis.label" /></th>
						<th><spring:message code="label.analysis.creationDate" /></th>
						<th><spring:message code="label.analysis.version" /></th>
						<th><spring:message code="label.analysis.author" /></th>
						<th><spring:message code="label.analysis.basedOnAnalysis" /></th>
						<th><spring:message code="label.analysis.language" /></th>
						<th><spring:message code="label.analysis.profile" text="Profile" />
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${analyses}" var="analysis">
						<tr trick-id="${analysis.id}" trick-rights-id="0" data="${analysis.hasData()}" trick-isProfile="${analysis.profile}">
							<td><input type="checkbox" class="checkbox"
								onchange="updateMenu('#section_admin_analysis','#menu_analysis');return disableifprofile('#section_admin_analysis','#menu_analysis');"></td>
							<td>${analysis.customer.organisation}</td>
							<td>${analysis.identifier}</td>
							<td>${analysis.label}</td>
							<td>${analysis.creationDate}</td>
							<td trick-version="${analysis.version}">${analysis.version}</td>
							<td>${analysis.getLastHistory().author}</td>
							<c:choose>
								<c:when test="${analysis.basedOnAnalysis == null}">
									<td><spring:message code="label.analysis.basedonself" /></td>
								</c:when>
								<c:when test="${analysis.basedOnAnalysis.id != analysis.id}">
									<td>${analysis.basedOnAnalysis.version}</td>
								</c:when>
							</c:choose>
							<td>${analysis.language.name}</td>
							<c:choose>
								<c:when test="${analysis.profile == true}">
									<td><spring:message code="label.yes" text="Yes" /></td>
								</c:when>
								<c:when test="${analysis.profile == false}">
									<td><spring:message code="label.no" text="No" /></td>
								</c:when>
							</c:choose>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
</div>
<script type="text/javascript" src="<spring:url value="js/analysis.js" />"></script>