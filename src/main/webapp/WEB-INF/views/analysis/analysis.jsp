<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<!-- ################################################################ Set Page Title ################################################################ -->
<c:set scope="request" var="title">title.analysis</c:set>
<!-- ###################################################################### HTML #################################################################### -->
<html>
<!-- Include Header -->
<jsp:include page="../header.jsp" />
<!-- ################################################################# Start Container ############################################################## -->
<body data-spy="scroll" data-target="#nav-container" data-offset="40">
	<div id="wrap">
		<!-- ################################################################### Nav Menu ################################################################### -->
		<jsp:include page="../menu.jsp" />
		<div class="container">
			<!-- #################################################################### Content ################################################################### -->
			<div class="page-header">
				<h1>
					<spring:message code="label.analysis.title" text="Analyses" />
				</h1>
				<jsp:include page="../successErrors.jsp" />
			</div>
			<div class="content" id="content" role="main" data-spy="scroll">
				<!-- #################################################################### Analysis Menu ################################################################### -->
				<c:if test="${!empty(sessionScope.selectedAnalysis)}">
					<jsp:include page="analysisMenu.jsp" />
					<jsp:include page="../successErrors.jsp" />
					<div class="content nav-container" id="content">
						<c:set var="histories" value="${analysis.histories}" scope="request" />
						<jsp:include page="./components/history.jsp" />
						<c:set var="itemInformations" value="${analysis.itemInformations}" scope="request" />
						<jsp:include page="./components/itemInformation.jsp" />
						<c:set var="parameters" value="${analysis.parameters}" scope="request" />
						<jsp:include page="./components/parameter.jsp" />
						<c:set var="assets" value="${analysis.assets}" scope="request" />
						<jsp:include page="./components/asset.jsp" />
						<c:set var="scenarios" value="${analysis.scenarios}" scope="request" />
						<jsp:include page="./components/scenario.jsp" />
						<spring:eval expression="T(lu.itrust.business.component.MeasureManager).ConcatMeasure(analysis.analysisNorms)" var="measures" scope="request" />
						<jsp:include page="./components/measure.jsp" />
						<c:set var="phases" scope="request" value="${analysis.usedPhases}" />
						<jsp:include page="./components/phase.jsp" />
						<jsp:include page="./components/charts.jsp" />
						<jsp:include page="./components/widgets.jsp" />
					</div>
				</c:if>
				<c:if test="${!empty analyses and empty(sessionScope.selectedAnalysis)}">
					<div class="section" id="section_analysis">
						<div id="deleteRight" hidden="true">
							${deleteRight}
						</div>
						<div id="calcRickRegisterRight" hidden="true">
							${calcRickRegisterRight}
						</div>
						<div id="calcActionPlanRight" hidden="true">
							${calcActionPlanRight}
						</div>
						<div id="modifyRight" hidden="true">
							${modifyRight}
						</div>
						<div id="exportRight" hidden="true">
							${exportRight}
						</div>
						<div id="readRight" hidden="true">
							${readRight}
						</div>
						<div class="panel panel-default">
							<div class="panel-heading">
								<button class="btn btn-default" onclick="newAnalysis();">
									<spring:message code="label.analysis.create" text="Create new Analysis" />
								</button>
							</div>
							<div class="panel-body">
								<table class="table">
									<thead>
										<tr>
											<th><spring:message code="label.analysis.identifier" /></th>
											<th><spring:message code="label.analysis.customer" /></th>
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
											<tr trick-id="${analysis.id}" trick-rights-id="${analysis.getRightsforUserString(login).right.ordinal()}" empty="${analysis.isEmpty() }">
												<td>${analysis.identifier}</td>
												<td>${analysis.customer.organisation}</td>
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
					<div id="contextMenu" class="dropdown clearfix" style="position: absolute; display: none;" trick-selected-id="-1">
						<ul class="dropdown-menu" role="menu" aria-labelledby="dropdownMenu" style="display: block; position: static; margin-bottom: 5px;">
							<li name="select"><a tabindex="-1" href="#"><spring:message code="label.action.select" text="Select" /></a></li>
							<li name="duplicate"><a tabindex="-1" href="#"><spring:message code="label.action.duplicate" text="Create new version" /></a></li>
							<li name="edit_row"><a tabindex="-1" href="#"><spring:message code="label.action.edit" text="Edit" /></a></li>
							<li class="divider" name="divider_0"></li>
							<li name="cActionPlan"><a tabindex="-1" href="#"><spring:message code="label.action.compute_ActionPlan" text="Compute Action Plan" /></a></li>
							<li class="divider" name="divider_1"></li>
							<li name="cRiskRegister"><a tabindex="-1" href="#"><spring:message code="label.action.compute_RiskRegister" text="Compute Rsik Register" /></a></li>
							<li class="divider" name="divider_2"></li>
							<li name="export"><a tabindex="-1" href="#"><spring:message code="label.action.export" text="Export" /></a></li>
							<li class="divider" name="divider_3"></li>
							<li name="delete"><a tabindex="-1" href="#"><spring:message code="label.action.delete" text="Delete" /></a></li>
						</ul>
					</div>
					<jsp:include page="widgetContent.jsp" />
					<jsp:include page="components/widgets/historyForm.jsp" />
				</c:if>
				<!-- ################################################################ Include Footer ################################################################ -->
			</div>
		</div>
		<!-- ################################################################ End Container ################################################################# -->
		<jsp:include page="../footer.jsp" />
		<jsp:include page="../scripts.jsp" />
	</div>
</body>
<!-- ################################################################### End HTML ################################################################### -->
</html>