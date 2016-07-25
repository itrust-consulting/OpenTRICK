<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!-- ############################################################### Set Page Title ################################################################# -->
<spring:message var="title" scope="request" code='label.title.risk_evolution' text="Risk evolution" />
<!-- #################################################################### HTML ###################################################################### -->
<html>
<!-- ################################################################ Include Header ################################################################ -->
<jsp:include page="../../template/header.jsp" />
<!-- ############################################################### Start Container ################################################################ -->
<body>
	<div id="wrap">
		<div class="container">
			<!-- ################################################################### Nav Menu ################################################################### -->
			<jsp:include page="../../template/menu.jsp" />
			<!-- ################################################################### Content #################################################################### -->
			<div class="content" id="content" style="margin-top: 10px;">
				<fieldset class="col-lg-3" style="margin-top: 18px;">
					<legend>
						<spring:message code='label.title.general' text="General" />
					</legend>
					<div class='form form-horizontal'>
						<div class='form-group'>
							<label class='label-control col-sm-4'><spring:message code='label.customer' text="Customer" /></label>
							<div class='col-sm-8'>
								<select class='form-control' name='customer' id='customer-selector'>
									<c:forEach var="currentCustomer" items="${customers}">
										<option value="${currentCustomer.id}" ${not empty customer and currentCustomer.id == customer?'selected': ''}><spring:message
												text='${currentCustomer.organisation}' /></option>
									</c:forEach>
								</select>
							</div>
						</div>
						<c:forEach var="index" begin="1" end="2" step="1">
							<hr>
							<div class='form-group' id='analysis_${index}'>
								<label class='label-control col-sm-4'><spring:message code='label.analysis' text="Analysis" /> ${index}</label>
								<div class='col-sm-8'>
									<select class='form-control' name='analysis' id='analysis-selector-${index}' data-parent='#customer-selector' data-target='#analysis-version-selector-${index}'
										data-index='${index}'>
										<option value="-"><spring:message code='label.choose' text='Choose...' /></option>
										<c:forEach items="${analyses}" var="analysis">
											<option value='<spring:message text="${analysis.identifier}"/>'><spring:message text="${analysis.label}" /></option>
										</c:forEach>
									</select>
								</div>
							</div>
							<div class='form-group' id='analysis_${index}'>
								<label class='label-control col-sm-4'><spring:message code='label.analysis.version' text="Version" /> ${index}</label>
								<div class='col-sm-8'>
									<select class='form-control' name='version' data-parent='#analysis-selector-${index}' id='analysis-version-selector-${index}' data-index='${index}'>
										<option value="-"><spring:message code='label.choose' text='Choose...' /></option>
									</select>
								</div>
							</div>
						</c:forEach>
					</div>
					<div class='clearfix'></div>
				</fieldset>
				<div class="col-lg-9">
					<ul class="nav nav-tabs affix affix-top col-lg-12 nav-tab">
						<li class='active'><a id="headingTotalALE" role='tab' role='button' data-toggle='tab' href="#tabTotalALE"> <spring:message code='label.title.total_ale'
									text="Total ALE" />
						</a></li>
						<li><a id="headingAleByScenario" role='tab' role='button' data-toggle='tab' href="#tabAleByScenario"> <spring:message code='label.title.ale_by_scenario'
									text="ALE By Scenario" />
						</a></li>
						<li><a id="headingAleByScenarioType" role='tab' role='button' data-toggle='tab' href="#tabAleByScenarioType"> <spring:message code='label.title.ale_by_scenario_type'
									text="ALE by scenario type" />
						</a></li>
						<li><a id="headingAleByAsset" role='tab' role='button' data-toggle='tab' href="#tabAleByAsset"> <spring:message code='label.title.ale_by_asset' text="ALE by asset" />
						</a></li>
						<li><a id="headingAleByAssetType" role='tab' role='button' data-toggle='tab' href="#tabAleByAssetType"> <spring:message code='label.title.total_ale' text="Total ALE" />
						</a></li>
						<li>
					</ul>
					<div class='tab-content'>
						<div id='tabTotalALE' class='tab-pane active' style="padding-top:20px"></div>
						<div id='tabAleByScenario' class='tab-pane' style="padding-top:20px"></div>
						<div id='tabAleByScenarioType' class='tab-pane' style="padding-top:20px"></div>
						<div id='tabAleByAsset' class='tab-pane' style="padding-top:20px"></div>
						<div id='tabAleByAssetType' class='tab-pane' style="padding-top:20px"></div>
					</div>
					<div class='clearfix'></div>
				</div>
			</div>
			<!-- ################################################################ Include Footer ################################################################ -->
		</div>
		<jsp:include page="../../template/footer.jsp" />
		<!-- ################################################################ End Container ################################################################# -->
		<jsp:include page="../../template/scripts.jsp" />
		<script src="<spring:url value="/js/trickservice/risk-evolution.js" />"></script>
		<script src="<spring:url value="/js/highcharts/highcharts.js" />"></script>
		<script src="<spring:url value="/js/highcharts/highcharts-more.js" />"></script>
	</div>
</body>
<!-- ################################################################### End HTML ################################################################### -->
</html>