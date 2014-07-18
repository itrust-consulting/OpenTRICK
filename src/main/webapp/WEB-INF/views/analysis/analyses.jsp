<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<c:set scope="request" var="title">label.title.analyses</c:set>
<html>
<jsp:include page="../header.jsp" />
<body data-spy="scroll" data-target="#analysismenu" data-offset="40">
	<div id="wrap">
		<jsp:include page="../menu.jsp" />
		<div class="container">
			<div class="section" id="section_analysis">
				<div class="page-header">
					<h1>
						<spring:message code="label.title.all_analyses" text="All Analyses" />
					</h1>
					<jsp:include page="../successErrors.jsp" />
				</div>
				<div class="panel panel-default">
					<div class="panel-heading" style="min-height: 60px">
						<ul class="nav nav-pills" id="menu_analysis">
							<li><a href="#" onclick="return newAnalysis();"> <span class="glyphicon glyphicon-plus primary"></span> <spring:message code="label.menu.add.analysis"
										text="New analysis" /></a></li>
							<li class="disabled" trick-selectable="true"><a href="#" onclick="return selectAnalysis(undefined, 'true')"> <span class="glyphicon glyphicon-folder-open"></span>
									&nbsp;<spring:message code="label.menu.edit.analysis" text="Edit analysis" /></a></li>
							<li class="disabled profilemenu" trick-selectable="true"><a href="#" onclick="return manageAnalysisAccess(null, 'section_analysis');"> <span
									class="glyphicon glyphicon-plus primary"></span> <spring:message code="label.menu.manage.access.analysis" text="Manage Access Rights" /></a></li>
							<li class="disabled" trick-selectable="true"><a href="#" onclick="return addHistory()"> <span class="glyphicon glyphicon-new-window"></span> <spring:message
										code="label.menu.create.analysis.new_version" text="New version" /></a></li>
							<li class="disabled" trick-selectable="true"><a href="#" onclick="return editSingleAnalysis();"> <span class="glyphicon glyphicon-edit"></span> <spring:message
										code="label.analysis.editInfo" text="Edit info" /></a></li>
							<li class="disabled" trick-selectable="true"><a href="#" onclick="return createAnalysisProfile(null, 'section_analysis');"> <span class="glyphicon glyphicon-file"></span>
									<spring:message code="label.menu.create.analysis_profile" text="New profile" /></a></li>
							<li class="disabled" trick-selectable="true"><a href="#" onclick="return exportAnalysis()"> <span class="glyphicon glyphicon-download-alt"></span> <spring:message
										code="label.menu.export.analysis" text="Export" /></a></li>
							<li class="disabled" trick-selectable="true"><a href="#" onclick="return exportAnalysisReport()"> <span class="glyphicon glyphicon-download-alt"></span> <spring:message
										code="label.menu.export.report" text="Export Report" /></a></li>
							<li class="disabled pull-right" trick-selectable="true"><a href="#" onclick="return deleteAnalysis();" class="text-danger"> <span class="glyphicon glyphicon-remove"></span>
									<spring:message code="label.menu.delete.analysis" text="Delete" /></a></li>
						</ul>
					</div>
					<div class="panel-body autofitpanelbodydefinition">
						<div class="center-block">
							<p class="text-center">
								<label><spring:message code="label.filter.analysis.customer" text="Analyses filtered by customer" /></label>
							</p>
							<form class="col-md-offset-5 col-md-2 form-inline">
								<select class="form-control" onchange="return customerChange(this)" style="margin-bottom: 10px">
									<c:forEach items="${customers}" var="icustomer">
										<option value="${icustomer.id}" ${customer != null && icustomer.id == customer? 'selected':'' }>
											<spring:message text="${icustomer.organisation}" />
										</option>
									</c:forEach>
								</select>
							</form>
						</div>
						<table class="table table-hover">
							<thead>
								<tr>
									<th><input type="checkbox" class="checkbox" onchange="return checkControlChange(this,'analysis')"></th>
									<th><spring:message code="label.analysis.version" text="version"/></th>
									<th><spring:message code="label.analysis.comment" text="Comment"/></th>
									<th><spring:message code="label.analysis.creation_date" text="Create date"/></th>
									<th><spring:message code="label.analysis.author" text="Author"/></th>
									<th><spring:message code="label.analysis.based_on_analysis" text="Based on"/></th>
									<th><spring:message code="label.analysis.language" text="Language"/></th>
									<th><spring:message code="label.analysis.rights" text="Access"/></th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${analyses}" var="analysis">
									<tr trick-id="${analysis.id}" trick-rights-id="${analysis.getRightsforUserString(login).right.ordinal()}" data="${analysis.hasData()}"
										ondblclick="return editSingleAnalysis(${analysis.id});">
										<td><input type="checkbox" class="checkbox" onchange="return updateMenu('#section_analysis','#menu_analysis');"></td>
										<td trick-version="${analysis.version}">${analysis.version}</td>
										<td><spring:message text="${analysis.label}"/></td>
										<td><spring:message text="${analysis.creationDate}"/></td>
										<td><spring:message text="${analysis.getLastHistory().author}"/></td>
										<c:choose>
											<c:when test="${analysis.basedOnAnalysis == null}">
												<td><spring:message code="label.analysis.based_on_self" text="None"/></td>
											</c:when>
											<c:when test="${analysis.basedOnAnalysis.id != analysis.id}">
												<td><spring:message text="${analysis.basedOnAnalysis.version}" /></td>
											</c:when>
										</c:choose>
										<td><spring:message text="${analysis.language.name}" /></td>
										<c:set var="right" value="${analysis.getRightsforUserString(login).right}"/>
										<td><spring:message code="label.analysis.right.${fn:toLowerCase(right)}" text="${fn:replace(right,'_', ' ')}"/> </td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>
		<jsp:include page="widgets.jsp" />
		<jsp:include page="../footer.jsp" />
		<jsp:include page="../scripts.jsp" />
		<script type="text/javascript" src="<spring:url value="js/trickservice/analyses.js" />"></script>
		<script type="text/javascript" src="<spring:url value="js/jquery.fileDownload.js" />"></script>
	</div>
</body>
</html>