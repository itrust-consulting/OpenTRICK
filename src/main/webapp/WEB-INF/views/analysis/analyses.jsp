<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<div class="section" id="section_analysis">
	<div class="page-header">
		<h1>
			<c:choose>
				<c:when test="${empty(KowledgeBaseView)}">
					<spring:message code="label.analysis.title" text="All Analyses" />
				</c:when>
				<c:otherwise>
					<spring:message code="label.analysis.profile.title" text="All profiles" />
				</c:otherwise>
			</c:choose>
		</h1>
		<jsp:include page="../successErrors.jsp" />
	</div>
	<div class="panel panel-default">
		<div class="panel-heading" style="min-height: 60px">
			<ul class="nav nav-pills" id="menu_analysis">
				<c:if test="${empty(KowledgeBaseView)}">
					<li><a href="#" onclick="return newAnalysis();"> <span
							class="glyphicon glyphicon-plus primary"></span> <spring:message
								code="label.analysis.add" text="New analysis" /></a></li>
				</c:if>
				<li class="disabled" trick-selectable="true"><a href="#"
					onclick="return selectAnalysis(undefined, 'true')"> <span
						class="glyphicon glyphicon-pushpin"></span> <spring:message
							code="label.analysis.pin" text="Edit Analysis" /></a></li>
				<c:if test="${empty(KowledgeBaseView)}">
					<li class="disabled" trick-selectable="true"><a href="#"
						onclick="return addHistory()"> <span
							class="glyphicon glyphicon-new-window"></span> <spring:message
								code="label.analysis.create.new_version"
								text="Create new version" /></a></li>
				</c:if>
				<li class="disabled" trick-selectable="true"><a href="#"
					onclick="return editSingleAnalysis();"> <span
						class="glyphicon glyphicon-edit"></span> <spring:message
							code="label.analysis.editInfo" text="Edit info" /></a></li>
				<c:if test="${empty(KowledgeBaseView)}">
					<li class="disabled" trick-selectable="true"><a href="#"
						onclick="return createAnalysisProfile();"> <span
							class="glyphicon glyphicon-file"></span> <spring:message
								code="label.analysis.createAnalysisProfile"
								text="Create new profile" /></a></li>
				</c:if>
				<li class="disabled" trick-selectable="true"><a href="#"
					onclick="return deleteAnalysis();"> <span
						class="glyphicon glyphicon-remove"></span> <spring:message
							code="label.analysis.delete" text="Delete" /></a></li>
				<c:if test="${empty(KowledgeBaseView)}">
					<li class="disabled" trick-selectable="true"><a href="#"
						onclick="return exportAnalysis()"> <span
							class="glyphicon glyphicon-download-alt"></span> <spring:message
								code="label.analysis.export" text="Export" /></a></li>
					<li class="disabled" trick-selectable="multi"><a href="#"
						onclick="return calculateActionPlan()"> <span
							class="glyphicon glyphicon-list"></span> <spring:message
								code="label.analysis.compute.action_plan"
								text="Compute action plan" /></a></li>
					<li class="disabled" trick-selectable="multi"><a href="#"
						onclick="return calculateRiskRegister()"> <span
							class="glyphicon glyphicon-list-alt"></span> <spring:message
								code="label.analysis.compute.risk_register"
								text="Compute risk register" /></a></li>
				</c:if>
			</ul>
		</div>
		<div class="panel-body">
			<c:if test="${empty(KowledgeBaseView)}">
				<p class="text-center">
					<spring:message code="label.analysis.filter.customer"
						text="Analyses filtered by customer: " />
				<div class="col-md-offset-5 col-md-2">
					<select class="form-control" onchange="return customerChange(this)"
						style="margin-bottom: 10px">
						<c:forEach items="${customers}" var="icustomer">
							<option value="${icustomer.id}"
								${icustomer.organisation == customer? 'selected':'' }>
								<spring:message text="${icustomer.organisation}" />
							</option>
						</c:forEach>
					</select>
				</div>
				</p>
			</c:if>
			<table class="table table-hover">
				<thead>
					<tr>
						<th><input type="checkbox" class="checkbox"
							onchange="return checkControlChange(this,'analysis')"></th>
						<th><spring:message code="label.analysis.identifier" /></th>
						<th><spring:message code="label.analysis.label" /></th>
						<th><spring:message code="label.analysis.creationDate" /></th>
						<th><spring:message code="label.analysis.version" /></th>
						<th><spring:message code="label.analysis.author" /></th>
						<th><spring:message code="label.analysis.basedOnAnalysis" /></th>
						<th><spring:message code="label.analysis.language" /></th>
						<th><spring:message code="label.analysis.rights" /></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${analyses}" var="analysis">
						<tr trick-id="${analysis.id}"
							trick-rights-id="${empty(KowledgeBaseView)?analysis.getRightsforUserString(login).right.ordinal() : '0'}"
							data="${analysis.hasData() }"
							ondblclick="return editSingleAnalysis();">
							<td><input type="checkbox" class="checkbox"
								onchange="return updateMenu('#section_analysis','#menu_analysis');"></td>
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
							<td>${analysis.getRightsforUserString(login).right.name() }</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
</div>
<div id="contextMenu" class="dropdown clearfix"
	style="position: absolute; display: none;" trick-selected-id="-1">
	<ul class="dropdown-menu" role="menu" aria-labelledby="dropdownMenu"
		style="display: block; position: static; margin-bottom: 5px;">
		<li name="select"><a tabindex="-1" href="#"><spring:message
					code="label.action.select" text="Select" /></a></li>
		<c:if test="${empty(KowledgeBaseView)}">
			<li name="duplicate"><a tabindex="-1" href="#"><spring:message
						code="label.action.duplicate" text="Create new version" /></a></li>
		</c:if>
		<li name="edit_row"><a tabindex="-1" href="#"><spring:message
					code="label.action.edit" text="Edit" /></a></li>
		<c:if test="${empty(KowledgeBaseView)}">
			<li class="divider" name="divider_0"></li>
			<li name="cActionPlan"><a tabindex="-1" href="#"><spring:message
						code="label.action.compute_ActionPlan" text="Compute Action Plan" /></a></li>
		</c:if>
		<li class="divider" name="divider_1"></li>
		<c:if test="${empty(KowledgeBaseView)}">
			<li name="cRiskRegister"><a tabindex="-1" href="#"><spring:message
						code="label.action.compute_RiskRegister"
						text="Compute Rsik Register" /></a></li>
			<li class="divider" name="divider_2"></li>
			<li name="export"><a tabindex="-1" href="#"><spring:message
						code="label.action.export" text="Export" /></a></li>
			<li class="divider" name="divider_3"></li>
		</c:if>
		<li name="delete"><a tabindex="-1" href="#"><spring:message
					code="label.action.delete" text="Delete" /></a></li>
	</ul>
</div>
<jsp:include page="../analysis/widgetContent.jsp" />
<jsp:include page="../analysis/components/widgets/historyForm.jsp" />
<script type="text/javascript"
	src="<spring:url value="js/analysis.js" />"></script>