<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authentication var="user" property="principal" />
<c:if test="${empty locale }">
	<spring:eval expression="T(org.springframework.web.servlet.support.RequestContextUtils).getLocale(pageContext.request)" var="locale" scope="request" />
</c:if>
<!DOCTYPE html>
<html lang="${locale.language}">
<c:set scope="request" var="title" value="label.title.analyses" />
<jsp:include page="../../template/header.jsp" />
<body>
	<div id="wrap" class="wrap">
		<jsp:include page="../../template/menu.jsp" />
		<div class="container">
			<div class="section" id="section_analysis">
				<jsp:include page="../../template/successErrors.jsp" />
				<ul class="nav nav-pills bordered-bottom" style="margin-top: 5px; margin-bottom: 5px;" id="menu_analysis">
					<li><a href="#" onclick="return customAnalysis(this);"> <span class="glyphicon glyphicon-plus"></span> <spring:message code="label.menu.build.analysis"
								text="Build an analysis" /></a></li>
					<li class="disabled" data-trick-selectable="true" data-trick-check="hasRight('READ')"><a href="#" onclick="return selectAnalysis(undefined,OPEN_MODE.READ)"> <span
							class="glyphicon glyphicon-eye-open"></span> &nbsp;<spring:message code="label.action.read_only" text="Read only" /></a></li>
					<li class="disabled" data-trick-selectable="true" data-trick-check="hasRight('MODIFY')"><a href="#" href="#" onclick="return selectAnalysis(undefined,OPEN_MODE.EDIT)"><span
							class="glyphicon glyphicon-edit"></span> <spring:message code="label.action.edit" text="Edit" /></a></li>

					<li class="disabled" data-trick-selectable="true" data-trick-check="canManageAccess()"><a href="#" onclick="return manageAnalysisAccess(null, 'section_analysis');"> <span
							class="fa fa-users"></span> <spring:message code="label.menu.manage.access.analysis" text="Manage Access Rights" /></a></li>

					<c:if test="${allowIDS}">
						<li class="disabled" data-trick-selectable="true" data-trick-check="canManageAccess() && isAnalysisType('QUANTITATIVE')"><a href="#" onclick="return manageAnalysisIDSAccess('section_analysis');"> <span
								class="fa fa-rss-square"></span> <spring:message code="label.menu.manage.ids.access.analysis" text="Manage IDS" /></a></li>
					</c:if>

					<c:if test="${allowedTicketing}">
						<li class="disabled" data-trick-selectable="true" data-trick-check="!isLinked() && hasRight('ALL')"><a href="#" onclick="return linkToProject()"> <span
								class="glyphicon glyphicon-link"></span> <spring:message code="label.menu.link.project" arguments="${ticketingName}" text="Link to ${ticketingName}" /></a></li>

						<li class="disabled" data-trick-selectable="multi" data-trick-single-check="isLinked() && hasRight('ALL')"><a href="#" onclick="return unLinkToProject()"> <span
								class="glyphicon glyphicon-scissors"></span> <spring:message code="label.menu.unlink.project" arguments="${ticketingName}" text="Unlink from ${ticketingName}" /></a></li>
					</c:if>

					<li class="disabled" data-trick-selectable="true" data-trick-check="hasRight('EXPORT')"><a href="#" onclick="return addHistory()"> <span
							class="glyphicon glyphicon-duplicate"></span> <spring:message code="label.menu.create.analysis.new_version" text="New version" /></a></li>

					<li class="disabled" data-trick-selectable="true" data-trick-check="hasRight('MODIFY')"><a href="#" onclick="return editSingleAnalysis();"
						data-trick-check="hasRight('MODIFY')"> <span class="glyphicon glyphicon-align-justify"></span> <spring:message code="label.edit.info" text="Edit info" /></a></li>

					<li class="disabled" data-trick-selectable="true" data-trick-check="hasRight('EXPORT')"><a href="#" onclick="return createAnalysisProfile(null, 'section_analysis');">
							<span class="glyphicon glyphicon-file"></span> <spring:message code="label.menu.create.analysis_profile" text="New profile" />
					</a></li>
					<li class="disabled" data-trick-selectable="true" data-trick-check="hasRight('EXPORT')"><a href="#" onclick="return exportAnalysis()"> <span
							class="glyphicon glyphicon glyphicon-export"></span> <spring:message code="label.menu.export.analysis" text="Export" /></a></li>

					<li class="disabled" data-trick-selectable="true" data-trick-check="hasRight('EXPORT')"><a href="#" onclick="return exportAnalysisReport()"> <span
							class="glyphicon glyphicon-download-alt"></span> <spring:message code="label.menu.export.report" text="Export Report" /></a></li>

					<li class="disabled pull-right" data-trick-selectable="true" data-trick-check="hasRight('MODIFY')"><a href="#" onclick="return deleteAnalysis();" class="text-danger">
							<span class="glyphicon glyphicon-remove"></span> <spring:message code="label.menu.delete.analysis" text="Delete" />
					</a></li>
				</ul>
				<div class="center-block">
					<div class="col-md-6">
						<p class="text-center">
							<label><spring:message code="label.filter.analysis.customer" text="Analyses filtered by customer" /></label>
						</p>
						<form class="col-md-offset-4 col-md-4">
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
						<form class="col-md-offset-4 col-md-4">
							<select id="nameSelectorFilter" class="form-control" onchange="return customerChange('#customerSelectorFilter','#nameSelectorFilter')" style="margin-bottom: 10px">
								<option value="ALL"><spring:message code="label.all" text="ALL" /></option>
								<c:forEach items="${names}" var="name">
									<option value='<spring:message text="${name}" />' ${not empty(analysisSelectedName) && analysisSelectedName == name? 'selected':'' }>
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
							<th width="1%"><c:if test="${allowedTicketing}">
									<input type="checkbox" class="checkbox" onchange="return checkControlChange(this,'analysis');">
								</c:if></th>
							<th width="20%"><spring:message code="label.analysis.label" text="Name" /></th>
							<th width="5%"><spring:message code="label.analysis.type" text="Type" /></th>
							<th width="35%"><spring:message code="label.analysis.comment" text="Comment" /></th>
							<c:if test="${allowedTicketing}">
								<th><spring:message code="label.link.to.project" arguments="${ticketingName}" text="${ticketingName}" /></th>
							</c:if>
							<th><spring:message code="label.analysis.version" text="version" /></th>
							<th width="8%"><spring:message code="label.analysis.creation_date" text="Create date" /></th>
							<th><spring:message code="label.analysis.author" text="Author" /></th>
							<th><spring:message code="label.analysis.based_on_analysis" text="Based on" /></th>
							<th><spring:message code="label.analysis.language" text="Language" /></th>
							<th><spring:message code="label.analysis.right" text="Access" /></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${analyses}" var="analysis">
							<tr data-trick-id="${analysis.id}" onclick="selectElement(this)" data-is-linked='${not empty analysis.project}' data-trick-type='${analysis.type}'
								data-trick-rights-id="${analysis.getRightsforUserString(login).right.ordinal()}" ondblclick="return editSingleAnalysis(${analysis.id});"
								data-analysis-owner="${user.username == analysis.owner.login}">
								<td><input type="checkbox" class="checkbox" onchange="return updateMenu(this,'#section_analysis','#menu_analysis');"></td>
								<td><spring:message text="${analysis.label}" /></td>
								<td><spring:message code='label.analysis.type.${fn:toLowerCase(analysis.type)}' text="${fn:toLowerCase(analysis.type)}" /></td>
								<td data-trick-content='text'><spring:message text="${analysis.lastHistory.comment}" /></td>
								<c:if test="${allowedTicketing}">
									<th><c:choose>
											<c:when test="${not empty analysis.project}">
												<spring:eval expression="T(lu.itrust.business.TS.model.ticketing.builder.ClientBuilder).ProjectLink(ticketingName.toLowerCase(),ticketingURL,analysis.project)"
													var="projectLink" />
												<a class="btn-link" href="${projectLink}" target="_titck_ts"><spring:message text="${analysis.project}" /> <i class="fa fa-external-link" aria-hidden="true"></i></a>
											</c:when>
										</c:choose></th>
								</c:if>
								<td data-trick-version="${analysis.version}">${analysis.version}</td>
								<td><fmt:formatDate value="${analysis.creationDate}" pattern="yyyy-MM-dd HH:mm" /></td>
								<td><spring:message text="${analysis.owner.firstName} ${analysis.owner.lastName}" /></td>
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
	</div>
	<jsp:include page="../../template/scripts.jsp" />
	<script type="text/javascript" src="<spring:url value="/js/trickservice/analyses.js" />"></script>
	<script type="text/javascript" src="<spring:url value="/js/trickservice/analysisExport.js" />"></script>
</body>
</html>