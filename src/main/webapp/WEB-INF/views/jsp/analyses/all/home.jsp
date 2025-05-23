<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authentication var="user" property="principal" />
<c:if test="${empty locale }">
	<spring:eval expression="T(org.springframework.context.i18n.LocaleContextHolder).getLocale()" var="locale" scope="request" />
</c:if>
<!DOCTYPE html>
<html lang="${locale.language}">
<c:set scope="request" var="title" value="label.title.analyses" />
<jsp:include page="../../template/header.jsp" />
<body>
	<div id="wrap" class="wrap">
		<jsp:include page="../../template/menu.jsp" />
		<div class="container" data-ug-root="analyses">
			<div class="section" id="section_analysis">
				<ul class="nav nav-pills bordered-bottom" style="margin-top: 5px; margin-bottom: 5px;" id="menu_analysis" data-trick-callback='updateDropdown'>
					<li data-trick-ignored="true" class="nav-dropdown-menu"><a class="pull-left" href="#" onclick="return customAnalysis(this);"> <span class="glyphicon glyphicon-plus"></span>
							<spring:message code="label.menu.build.analysis" text="Build an analysis" /></a> <a href="#" class="pull-left dropdown-toggle disabled" data-toggle="dropdown"
						aria-haspopup="true" aria-expanded="false"><i class="fa fa-sort-desc fa-f20" aria-hidden="true"></i></a>
						<ul class="dropdown-menu">
							<li class="disabled" data-trick-selectable="true" data-trick-check="hasRight('EXPORT')"><a href="#" onclick="return addHistory()"> <span
									class="glyphicon glyphicon-duplicate"></span> <spring:message code="label.menu.create.analysis.new_version" text="New version" /></a></li>
							<li class="disabled" data-trick-selectable="true" data-trick-check="!isArchived() && hasRight('EXPORT')"><a href="#"
								onclick="return createAnalysisProfile(null, 'section_analysis');"> <span class="glyphicon glyphicon-file"></span> <spring:message
										code="label.menu.create.analysis_profile" text="New profile" />
							</a></li>
						</ul></li>

					<li class="disabled" data-trick-selectable="true" data-trick-check="hasRight('MODIFY') && !isArchived()"><a href="#" href="#"
						onclick="return selectAnalysis(undefined,OPEN_MODE.EDIT)"><span class="glyphicon glyphicon-folder-open"></span> &nbsp;<spring:message code="label.action.open" text="Edit" /></a></li>

					<li class="disabled" data-trick-selectable="true" data-trick-check="hasRight('READ')"><a href="#" onclick="return selectAnalysis(undefined,OPEN_MODE.READ)"> <span
							class="glyphicon glyphicon-eye-open"></span>&nbsp;<spring:message code="label.action.open.read_only" text="Read only" /></a></li>

					<li class="disabled" data-trick-selectable="true" data-trick-check="!isArchived() && hasRight('ALL')"><a href="#" onclick="return editSingleAnalysis();"> <span
							class="glyphicon glyphicon-edit"></span> <spring:message code="label.properties" /></a></li>

					<c:if test="${allowedTicketing and not empty ticketingURL}">
						<li class="disabled" data-trick-selectable="true" data-trick-check="!isArchived() && !isLinked() && hasRight('ALL')"><a href="#" onclick="return linkToProject()"> <span
								class="glyphicon glyphicon-link"></span> <spring:message code="label.menu.link.project" arguments="${ticketingName}" text="Link to ${ticketingName}" /></a></li>

						<li class="disabled" data-trick-selectable="multi" data-trick-single-check="!isArchived() && isLinked() && hasRight('ALL')"><a href="#"
							onclick="return unLinkToProject()"> <span class="glyphicon glyphicon-scissors"></span> <spring:message code="label.menu.unlink.project" arguments="${ticketingName}"
									text="Unlink from ${ticketingName}" /></a></li>
					</c:if>

					<li class="disabled ${allowIDS? 'nav-dropdown-menu': ''}" data-trick-selectable="true" data-trick-check="!isArchived() && canManageAccess()"><a href="#" class='pull-left'
						onclick="return manageAnalysisAccess(null, 'section_analysis');"> <span class="fa fa-users"></span> <spring:message code="label.menu.manage.access.analysis"
								text="Manage Access Rights" /></a> <c:if test="${allowIDS}">
							<a href="#" class="pull-left dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><i class="fa fa-sort-desc fa-f20" aria-hidden="true"></i></a>
							<ul class="dropdown-menu">
								<li class="disabled" data-trick-selectable="true" data-trick-check="!isArchived() && canManageAccess() && isAnalysisType('QUANTITATIVE')"><a href="#"
									onclick="return manageAnalysisIDSAccess('section_analysis');"> <span class="fa fa-rss-square"></span> <spring:message code="label.menu.manage.ids.access.analysis"
											text="Manage IDS" /></a></li>
							</ul>
						</c:if></li>

					<li class="disabled nav-dropdown-menu" data-trick-selectable="true" data-trick-check="hasRight('EXPORT') && !isArchived()"><a class="pull-left" href="#"
						onclick="return exportAnalysisReport()"> <span class="glyphicon glyphicon-export"></span> <spring:message code="label.action.export.report" text="Export" /></a> <a href="#"
						class="pull-left dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><i class="fa fa-sort-desc fa-f20" aria-hidden="true"></i></a>
						<ul class="dropdown-menu">
							<li class="disabled" data-trick-selectable="true" data-trick-check="hasRight('EXPORT')"><a href="#" onclick="return exportAnalysis()"> <span
									class="glyphicon glyphicon glyphicon-export"></span> <spring:message code="label.action.export.database" text="Export" /></a></li>
						</ul></li>

					<c:if test="${not empty customers}">
						<li data-trick-ignored="true"><a href="#" onclick="return importAnalysis()"> <span class="glyphicon glyphicon-import"></span> <spring:message
									code="label.action.import" />
						</a></li>
					</c:if>

					<li class="disabled pull-right" data-trick-selectable="true" data-trick-check="isOwner() || !isArchived() && hasRight('ALL')"><a href="#"
						onclick="return deleteAnalysis();" class="text-danger"> <span class="glyphicon glyphicon-remove"></span> <spring:message code="label.menu.delete.analysis" text="Delete" />
					</a></li>

					<li class="disabled pull-right" data-trick-selectable="true" data-trick-check="!isArchived() && canManageAccess()"><a href="#" onclick="return archiveAnalysis();"
						class="text-warning"> <span class="glyphicon glyphicon-folder-close"></span> <spring:message code="label.menu.archive.analysis" text="Archive" />
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
				<table class="table table-striped table-hover" style="border-top: 1px solid #dddddd;">
					<thead>
						<tr>
							<th width="1%"><c:if test="${allowedTicketing and not empty ticketingURL}">
									<input type="checkbox" class="checkbox" onchange="return checkControlChange(this,'analysis');">
								</c:if></th>
							<th width="20%"><spring:message code="label.analysis.label" text="Name" /></th>
							<th width="5%"><spring:message code="label.analysis.type" text="Type" /></th>
							<th width="35%"><spring:message code="label.analysis.comment" text="Comment" /></th>
							<c:if test="${allowedTicketing and not empty ticketingURL}">
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
							<tr class='${analysis.archived?"text-muted":""}' data-trick-id="${analysis.id}" onclick="selectElement(this)" data-trick-archived='${analysis.archived}'
								data-is-linked='${not empty analysis.project}' data-trick-type='${analysis.type}' data-trick-rights-id="${analysis.findRightsforUserString(login).right.ordinal()}"
								ondblclick="return editSingleAnalysis(${analysis.id});" data-analysis-owner="${user.username == analysis.owner.login}">
								<td><input type="checkbox" class="checkbox" onchange="return updateMenu(this,'#section_analysis','#menu_analysis');"></td>
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
								<td data-trick-content='text'><spring:message text="${analysis.lastHistory.comment}" /></td>
								<c:if test="${allowedTicketing and not empty ticketingURL}">
									<th><c:choose>
											<c:when test="${not empty analysis.project}">
												<spring:eval expression="T(lu.itrust.business.ts.model.ticketing.builder.ClientBuilder).ProjectLink(ticketingName.toLowerCase(),ticketingURL,analysis.project)"
													var="projectLink" />
												<a class="btn-link" href="${projectLink}" target="_titck_ts"><i class="fa fa-external-link" aria-hidden="true"></i> <spring:message text="${analysis.project}" /></a>
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
								<c:set var="right" value="${analysis.findRightsforUserString(login).right}" />
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
	<script src="<c:url value="/js/jquery-ui.min.js" />"></script>
	<script type="text/javascript" src="<c:url value="/js/trickservice/analyses.js" />"></script>
	<script type="text/javascript" src="<c:url value="/js/trickservice/analysisExport.js" />"></script>
	<c:if test="${adminaAllowedTicketing}">
		<script type="text/javascript" src="<c:url value="/js/trickservice/ticketing-system.js" />"></script>
	</c:if>
</body>
</html>