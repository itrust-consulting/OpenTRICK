<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
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
			<div class="row nav-container" id="nav-container">
				<!-- #################################################################### Analysis Menu ################################################################### -->
				<c:choose>
					<c:when test="${!empty(sessionScope.selectedAnalysis)}">
						<jsp:include page="analysisMenu.jsp" />
						<jsp:include page="../successErrors.jsp" />
						<div class="content nav-container" id="section_analysis"
							trick-id="${analysis.id}"
							trick-rights-id="${analysis.getRightsforUserString(login).right.ordinal()}">
							<c:set var="histories" value="${analysis.histories}"
								scope="request" />
							<c:set var="histories" value="${analysis.histories}"
								scope="request" />
							<jsp:include page="./components/history.jsp" />
							<c:set var="itemInformations"
								value="${analysis.itemInformations}" scope="request" />
							<jsp:include page="./components/itemInformation.jsp" />
							<c:set var="parameters" value="${analysis.parameters}"
								scope="request" />
							<jsp:include page="./components/parameter.jsp" />
							<c:set var="assets" value="${analysis.assets}" scope="request" />
							<jsp:include page="./components/asset.jsp" />
							<c:set var="scenarios" value="${analysis.scenarios}"
								scope="request" />
							<jsp:include page="./components/scenario.jsp" />
							<c:set var="phases" scope="request"
								value="${analysis.usedPhases}" />
							<jsp:include page="./components/phase.jsp" />
							<spring:eval
								expression="T(lu.itrust.business.component.MeasureManager).ConcatMeasure(analysis.analysisNorms)"
								var="measures" scope="request" />
							<jsp:include page="./components/measure.jsp" />
							<c:set var="actionplans" scope="request"
								value="${analysis.actionPlans}" />
							<jsp:include page="./components/actionplan.jsp" />
							<c:set var="summaries" scope="request"
								value="${analysis.summaries}" />
							<jsp:include page="./components/summary.jsp" />
							<jsp:include page="./components/charts.jsp" />
							<jsp:include page="./components/widgets.jsp" />
						</div>
						<script type="text/javascript"
							src="<spring:url value="js/actionplan.js" />"></script>
					</c:when>
					<c:otherwise>
						<div class="section" id="section_analysis">
							<div class="page-header">
								<h1>
									<spring:message code="label.analysis.title" text="Analyses" />
								</h1>
								<jsp:include page="../successErrors.jsp" />
							</div>
							<div id="deleteRight" hidden="true">${deleteRight}</div>
							<div id="calcRickRegisterRight" hidden="true">${calcRickRegisterRight}</div>
							<div id="calcActionPlanRight" hidden="true">${calcActionPlanRight}</div>
							<div id="modifyRight" hidden="true">${modifyRight}</div>
							<div id="exportRight" hidden="true">${exportRight}</div>
							<div id="readRight" hidden="true">${readRight}</div>
							<div class="panel panel-default"
								onmouseover="if(!$('#menu_analysis').is(':visible')) {updateMenu('#section_analysis', '#menu_analysis');$('#menu_analysis').show();}"
								onmouseout="$('#menu_analysis').hide();">
								<div class="panel-heading" style="min-height: 60px">
									<ul class="nav nav-pills" hidden="true" id="menu_analysis">
										<li><a href="#" onclick="return newAnalysis();"><span
												class="glyphicon glyphicon-plus primary"></span> <spring:message
													code="label.analysis.add" text="New analysis" /> </a></li>
										<li trick-selectable="true"><a href="#"
											onclick="return selectAnalysis(undefined, 'true')"><span
												class="glyphicon glyphicon-pushpin"></span> <spring:message
													code="label.analysis.pin" text="Edit Analysis" /> </a></li>
										<li trick-selectable="true"><a href="#"
											onclick="return addHistory()"><span
												class="glyphicon glyphicon-new-window"></span> <spring:message
													code="label.analysis.create.new_version"
													text="Create new version" /> </a></li>
										<li trick-selectable="true"><a href="#"
											onclick="return editSingleAnalysis();"><span
												class="glyphicon glyphicon-edit danger"></span> <spring:message
													code="label.analysis.editInfo" text="Edit info" /> </a></li>
										<li trick-selectable="true"><a href="#"
											onclick="return deleteAnalysis();"><span
												class="glyphicon glyphicon-remove"></span> <spring:message
													code="label.analysis.delete" text="Delete" /> </a></li>
										<li trick-selectable="true"><a href="#"
											onclick="return exportAnalysis()"><span
												class="glyphicon glyphicon-download-alt"></span> <spring:message
													code="label.analysis.export" text="Export" /> </a></li>
										<li trick-selectable="multi"><a href="#"
											onclick="return calculateActionPlan()"><span
												class="glyphicon glyphicon-list"></span> <spring:message
													code="label.analysis.compute.action_plan"
													text="Compute action plan" /> </a></li>
										<li trick-selectable="multi"><a href="#"
											onclick="return calculateRiskRegister()"><span
												class="glyphicon glyphicon-list-alt"></span> <spring:message
													code="label.analysis.compute.risk_register"
													text="Compute risk register" /> </a></li>
									</ul>
								</div>
								<div class="panel-body">
									<div class="col-md-offset-5 col-md-2">
										<select class="form-control"
											onchange="return customerChange(this)">
											<c:forEach items="${customers}" var="icustomer">
												<option value="${icustomer.id}"
													${icustomer.organisation == customer? 'selected':'' }><spring:message
														text="${icustomer.organisation}" /></option>
											</c:forEach>
										</select>
									</div>
									<table
										class="table table-hover tablesorter-bootstrap" style="max-width: ">
										<thead>
											<tr class="tablesorter-headerRow" >
												<th><input type="checkbox" class="checkbox"
													onchange="return checkControlChange(this,'analysis')"></th>
												<th class="tablesorter-header bootstrap-header"><spring:message
														code="label.analysis.identifier" /></th>
												<th class="tablesorter-header bootstrap-header"><spring:message
														code="label.analysis.customer" /></th>
												<th class="tablesorter-header bootstrap-header"><spring:message
														code="label.analysis.label" /></th>
												<th class="tablesorter-header bootstrap-header"><spring:message
														code="label.analysis.creationDate" /></th>
												<th class="tablesorter-header bootstrap-header"><spring:message
														code="label.analysis.version" /></th>
												<th class="tablesorter-header bootstrap-header"><spring:message
														code="label.analysis.author" /></th>
												<th class="tablesorter-header bootstrap-header"><spring:message
														code="label.analysis.basedOnAnalysis" /></th>
												<th class="tablesorter-header bootstrap-header"><spring:message
														code="label.analysis.language" /></th>
												<th class="tablesorter-header bootstrap-header"><spring:message
														code="label.analysis.rights" /></th>
											</tr>
										</thead>
										<tbody>
											<c:forEach items="${analyses}" var="analysis">
												<tr trick-id="${analysis.id}"
													trick-rights-id="${analysis.getRightsforUserString(login).right.ordinal()}"
													data="${analysis.hasData() }">
													<td><input type="checkbox" class="checkbox"
														onchange="return updateMenu('#section_analysis','#menu_analysis');"></td>
													<td>${analysis.identifier}</td>
													<td>${analysis.customer.organisation}</td>
													<td>${analysis.label}</td>
													<td>${analysis.creationDate}</td>
													<td trick-version="${analysis.version}">${analysis.version}</td>
													<td>${analysis.getLastHistory().author}</td>
													<c:choose>
														<c:when test="${analysis.basedOnAnalysis == null}">
															<td><spring:message
																	code="label.analysis.basedonself" /></td>
														</c:when>
														<c:when
															test="${analysis.basedOnAnalysis.id != analysis.id}">
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
							<ul class="dropdown-menu" role="menu"
								aria-labelledby="dropdownMenu"
								style="display: block; position: static; margin-bottom: 5px;">
								<li name="select"><a tabindex="-1" href="#"><spring:message
											code="label.action.select" text="Select" /></a></li>
								<li name="duplicate"><a tabindex="-1" href="#"><spring:message
											code="label.action.duplicate" text="Create new version" /></a></li>
								<li name="edit_row"><a tabindex="-1" href="#"><spring:message
											code="label.action.edit" text="Edit" /></a></li>
								<li class="divider" name="divider_0"></li>
								<li name="cActionPlan"><a tabindex="-1" href="#"><spring:message
											code="label.action.compute_ActionPlan"
											text="Compute Action Plan" /></a></li>
								<li class="divider" name="divider_1"></li>
								<li name="cRiskRegister"><a tabindex="-1" href="#"><spring:message
											code="label.action.compute_RiskRegister"
											text="Compute Rsik Register" /></a></li>
								<li class="divider" name="divider_2"></li>
								<li name="export"><a tabindex="-1" href="#"><spring:message
											code="label.action.export" text="Export" /></a></li>
								<li class="divider" name="divider_3"></li>
								<li name="delete"><a tabindex="-1" href="#"><spring:message
											code="label.action.delete" text="Delete" /></a></li>
							</ul>
						</div>
						<jsp:include page="widgetContent.jsp" />
						<jsp:include page="components/widgets/historyForm.jsp" />
						<script type="text/javascript"
							src="<spring:url value="js/analysis.js" />"></script>
					</c:otherwise>
				</c:choose>
				<!-- ################################################################ Include Footer ################################################################ -->
			</div>
		</div>
		<!-- ################################################################ End Container ################################################################# -->
		<jsp:include page="../footer.jsp" />
		<jsp:include page="../scripts.jsp" />
		<c:if test="${!empty(sessionScope.selectedAnalysis)}">
			<script type="text/javascript">
				reloadCharts();
			</script>
		</c:if>
	</div>
</body>
<!-- ################################################################### End HTML ################################################################### -->
</html>
