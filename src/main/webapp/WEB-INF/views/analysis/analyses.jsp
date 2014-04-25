<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="section" id="section_analysis">
	<div class="page-header">
		<h1>
			<spring:message code="label.analysis.title" text="All Analyses" />
		</h1>
		<jsp:include page="../successErrors.jsp" />
	</div>
	<div class="panel panel-default">
		<div class="panel-heading" style="min-height: 60px">
			<ul class="nav nav-pills" id="menu_analysis">
				<li><a href="#" onclick="return newAnalysis();"> <span class="glyphicon glyphicon-plus primary"></span> <spring:message code="label.analysis.add" text="New analysis" /></a></li>
				<li class="disabled" trick-selectable="true"><a href="#" onclick="return selectAnalysis(undefined, 'true')"> <span class="glyphicon glyphicon-pushpin"></span> <spring:message
							code="label.analysis.pin" text="Edit Analysis" /></a></li>
				<li class="disabled profilemenu" trick-selectable="true"><a href="#" onclick="return manageAnalysisAccess(null, 'section_analysis');"> <span
						class="glyphicon glyphicon-plus primary"></span> <spring:message code="label.analysis.manage.access" text="Manage Access Rights" /></a></li>
				<li class="disabled" trick-selectable="true"><a href="#" onclick="return addHistory()"> <span class="glyphicon glyphicon-new-window"></span> <spring:message
							code="label.analysis.create.new_version" text="New version" /></a></li>
				<li class="disabled" trick-selectable="true"><a href="#" onclick="return editSingleAnalysis();"> <span class="glyphicon glyphicon-edit"></span> <spring:message
							code="label.analysis.editInfo" text="Edit info" /></a></li>
				<li class="disabled" trick-selectable="true"><a href="#" onclick="return createAnalysisProfile(null, 'section_analysis');"> <span class="glyphicon glyphicon-file"></span>
						<spring:message code="label.analysis.createAnalysisProfile" text="New profile" /></a></li>
				<li class="disabled" trick-selectable="true"><a href="#" onclick="return deleteAnalysis();"> <span class="glyphicon glyphicon-remove"></span> <spring:message
							code="label.analysis.delete" text="Delete" /></a></li>
				<li class="disabled" trick-selectable="true"><a href="#" onclick="return exportAnalysis()"> <span class="glyphicon glyphicon-download-alt"></span> <spring:message
							code="label.analysis.export" text="Export" /></a></li>
				<li class="disabled" trick-selectable="multi"><a href="#" onclick="return calculateActionPlan()"> <span class="glyphicon glyphicon-list"></span> <spring:message
							code="label.analysis.compute.action_plan" text="Action plan" /></a></li>
				<li class="disabled" trick-selectable="multi"><a href="#" onclick="return calculateRiskRegister()"> <span class="glyphicon glyphicon-list-alt"></span> <spring:message
							code="label.analysis.compute.risk_register" text="Risk register" /></a></li>
			</ul>
		</div>
		<div class="panel-body panelbodydefinition">
			<p class="text-center">
				<spring:message code="label.analysis.filter.customer" text="Analyses filtered by customer: " />
			<div class="col-md-offset-5 col-md-2">
				<select class="form-control" onchange="return customerChange(this)" style="margin-bottom: 10px">
					<c:forEach items="${customers}" var="icustomer">
						<option value="${icustomer.id}" ${customer != null && icustomer.id == customer? 'selected':'' }>
							<spring:message text="${icustomer.organisation}" />
						</option>
					</c:forEach>
				</select>
			</div>
			</p>
			<table class="table table-hover">
				<thead>
					<tr>
						<th><input type="checkbox" class="checkbox" onchange="return checkControlChange(this,'analysis')"></th>
						<th><spring:message code="label.analysis.version" /></th>
						<th><spring:message code="label.analysis.label" /></th>
						<th><spring:message code="label.analysis.creationDate" /></th>
						<th><spring:message code="label.analysis.author" /></th>
						<th><spring:message code="label.analysis.basedOnAnalysis" /></th>
						<th><spring:message code="label.analysis.language" /></th>
						<th><spring:message code="label.analysis.rights" /></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${analyses}" var="analysis">
						<tr trick-id="${analysis.id}" trick-rights-id="${analysis.getRightsforUserString(login).right.ordinal()}" data="${analysis.hasData()}"
							ondblclick="return editSingleAnalysis(${analysis.id});">
							<td><input type="checkbox" class="checkbox" onchange="return updateMenu('#section_analysis','#menu_analysis');"></td>
							<td trick-version="${analysis.version}">${analysis.version}</td>
							<td>${analysis.label}</td>
							<td>${analysis.creationDate}</td>
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
							<td>${analysis.getRightsforUserString(login).right.name() }</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
</div>
<jsp:include page="../analysis/widgetContent.jsp" />
<jsp:include page="../analysis/components/widgets/historyForm.jsp" />