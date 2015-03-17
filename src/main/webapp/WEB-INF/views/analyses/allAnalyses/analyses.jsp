<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<c:set scope="request" var="title">label.title.analyses</c:set>
<sec:authentication var="user" property="principal" />
<html>
<jsp:include page="../../template/header.jsp" />
<body>
	<div id="wrap">
		<jsp:include page="../../template/menu.jsp" />
		<div class="container">
			<div class="section" id="section_analysis">
				<jsp:include page="../../template/successErrors.jsp" />
				<ul class="nav nav-pills bordered-bottom" style="margin-top: 5px; margin-bottom: 5px;" id="menu_analysis">
					<li><a href="#" onclick="return customAnalysis(this);"> <span class="glyphicon glyphicon-plus"></span> <spring:message code="label.menu.build.analysis"
								text="Build an analysis" /></a></li>
					<li class="disabled" data-trick-selectable="true" data-trick-check="hasRight('READ','open')"><a href="#" onclick="return selectAnalysis(undefined, 'true')"> <span
							class="glyphicon glyphicon-folder-open"></span> &nbsp;<spring:message code="label.menu.open.analysis" text="Open analysis" /></a></li>
					<li class="disabled profilemenu" data-trick-selectable="true" data-trick-check="canManageAccess()"><a href="#"
						onclick="return manageAnalysisAccess(null, 'section_analysis');"> <span class="glyphicon glyphicon-plus primary"></span> <spring:message
								code="label.menu.manage.access.analysis" text="Manage Access Rights" /></a></li>
					<li class="disabled" data-trick-selectable="true" data-trick-check="hasRight('READ')"><a href="#" onclick="return addHistory()"> <span
							class="glyphicon glyphicon-new-window"></span> <spring:message code="label.menu.create.analysis.new_version" text="New version" /></a></li>
					<li class="disabled" data-trick-selectable="true" data-trick-check="hasRight('MODIFY')"><a href="#" onclick="return editSingleAnalysis();"
						data-trick-check="hasRight('MODIFY')"> <span class="glyphicon glyphicon-edit"></span> <spring:message code="label.edit.info" text="Edit info" /></a></li>
					<li class="disabled" data-trick-selectable="true" data-trick-check="hasRight('READ')"><a href="#" onclick="return createAnalysisProfile(null, 'section_analysis');"> <span
							class="glyphicon glyphicon-file"></span> <spring:message code="label.menu.create.analysis_profile" text="New profile" />
					</a></li>
					<li class="disabled" data-trick-selectable="true" data-trick-check="hasRight('EXPORT')"><a href="#" onclick="return exportAnalysis()"> <span
							class="glyphicon glyphicon-download-alt"></span> <spring:message code="label.menu.export.analysis" text="Export" /></a></li>
					<li class="disabled" data-trick-selectable="true" data-trick-check="hasRight('EXPORT')"><a href="#" onclick="return exportAnalysisReport()"> <span
							class="glyphicon glyphicon-download-alt"></span> <spring:message code="label.menu.export.report" text="Export Report" /></a></li>
					<li class="disabled pull-right" data-trick-selectable="true" data-trick-check="hasRight('DELETE')"><a href="#" onclick="return deleteAnalysis();" class="text-danger">
							<span class="glyphicon glyphicon-remove"></span> <spring:message code="label.menu.delete.analysis" text="Delete" />
					</a></li>
				</ul>
				<div class="center-block">
					<div class="col-md-6">
						<p class="text-center">
							<label><spring:message code="label.filter.analysis.customer" text="Analyses filtered by customer" /></label>
						</p>
						<form class="col-md-offset-4 col-md-4 form-inline">
							<select id="customerSelectorFilter" class="form-control" onchange="return customerChange('#customerSelectorFilter','#nameSelectorFilter')" style="margin-bottom: 10px">
								<c:forEach items="${customers}" var="icustomer">
									<option value="${icustomer.id}" ${not empty(customer) && icustomer.id == customer? 'selected':'' }>
										<spring:message text="${icustomer.organisation}" />
									</option>
								</c:forEach>
							</select>
						</form>
					</div>
					<div class="col-md-6">
						<p class="text-center">
							<label><spring:message code="label.filter.analysis.name" text="Analyses filtered by name" /></label>
						</p>
						<form class="col-md-offset-4 col-md-4  form-inline">
							<select id="nameSelectorFilter" class="form-control" onchange="return customerChange('#customerSelectorFilter','#nameSelectorFilter')" style="margin-bottom: 10px">
								<option value="ALL"><spring:message code="label.all" text="ALL"/></option>
								<c:forEach items="${names}" var="name">
									<option value="${name}" ${not empty(analysisSelectedName) && analysisSelectedName == name? 'selected':'' }>
										<spring:message text="${name}" />
									</option>
								</c:forEach>
							</select>
						</form>
					</div>
				</div>
				<table class="table table-hover" style="border-top: 1px solid #dddddd;">
					<thead>
						<tr>
							<th width="1%"></th>
							<th width="20%"><spring:message code="label.analysis.label" text="Name" /></th>
							<th width="40%"><spring:message code="label.analysis.comment" text="Comment" /></th>
							<th><spring:message code="label.analysis.version" text="version" /></th>
							<th><spring:message code="label.analysis.creation_date" text="Create date" /></th>
							<th><spring:message code="label.analysis.author" text="Author" /></th>
							<th><spring:message code="label.analysis.based_on_analysis" text="Based on" /></th>
							<th><spring:message code="label.analysis.language" text="Language" /></th>
							<th><spring:message code="label.analysis.rights" text="Access" /></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${analyses}" var="analysis">
							<tr data-trick-id="${analysis.id}" data-trick-rights-id="${analysis.getRightsforUserString(login).right.ordinal()}" ondblclick="return editSingleAnalysis(${analysis.id});"
								data-analysis-owner="${user.username == analysis.owner.login}">
								<td><input type="checkbox" class="checkbox" onchange="return updateMenu(this,'#section_analysis','#menu_analysis');"></td>
								<td><spring:message text="${analysis.label}" /></td>
								<td><pre>
										<spring:message text="${analysis.lastHistory.comment}" />
									</pre></td>
								<td data-trick-version="${analysis.version}">${analysis.version}</td>
								<td><fmt:formatDate value="${analysis.creationDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
								<td><spring:message text="${analysis.lastHistory.author}" /></td>
								<c:choose>
									<c:when test="${analysis.basedOnAnalysis == null}">
										<td><spring:message code="label.analysis.based_on_self" text="None" /></td>
									</c:when>
									<c:when test="${analysis.basedOnAnalysis.id != analysis.id}">
										<td><spring:message text="${analysis.basedOnAnalysis.version}" /></td>
									</c:when>
								</c:choose>
								<td><spring:message text="${analysis.language.name}" /></td>
								<c:set var="right" value="${analysis.getRightsforUserString(login).right}" />
								<td><spring:message code="label.analysis.right.${fn:toLowerCase(right)}" text="${fn:replace(right,'_', ' ')}" /></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
		<jsp:include page="widgets.jsp" />
		<jsp:include page="../../template/footer.jsp" />
		<jsp:include page="../../template/scripts.jsp" />
		<script type="text/javascript" src="<spring:url value="js/trickservice/analyses.js" />"></script>
	</div>
</body>
</html>