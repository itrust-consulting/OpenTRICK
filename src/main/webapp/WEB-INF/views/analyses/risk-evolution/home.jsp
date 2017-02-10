<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!-- ############################################################### Set Page Title ################################################################# -->
<spring:message var="title" scope="request" code='label.title.risk_evolution' text="Risk evolution" />
<!-- #################################################################### HTML ###################################################################### -->
<!DOCTYPE html>
<html>
<!-- ################################################################ Include Header ################################################################ -->
<jsp:include page="../../template/header.jsp" />
<!-- ############################################################### Start Container ################################################################ -->
<body>
	<div id="wrap">
		<!-- ################################################################### Nav Menu ################################################################### -->
		<jsp:include page="../../template/menu.jsp" />
		<!-- ################################################################### Content #################################################################### -->
		<div class="container trick-container max-height">
			<fieldset class="col-lg-3 max-height affixNav " style="overflow-x: hidden; padding-right: 16px; margin-top: 17px;" role="left-menu">
				<legend>${title}</legend>
				<div class='form form-horizontal'>
					<div class='form-group'>
						<label class='label-control col-sm-4'><spring:message code='label.customer' text="Customer" /></label>
						<div class='col-sm-8'>
							<select class='form-control' name='customer' id='customer-selector'>
								<option value="-"><spring:message code='label.action.choose' text='Choose...' /></option>
								<c:forEach var="currentCustomer" items="${customers}">
									<option value="${currentCustomer.id}"><spring:message text='${currentCustomer.organisation}' /></option>
								</c:forEach>
							</select>
						</div>
					</div>
					<hr>
					<c:forEach var="index" begin="1" end="10" step="1">
						<div data-role='form-container' ${index>1? 'hidden class="panel panel-analysis-slave"' : 'class="panel panel-analysis-main"'}>
							<div class='panel-heading' style="padding-top: 20px;">
								<button data-control="${index}" title="<spring:message code='label.action.remove' text='Remove'/>" class="pull-right btn btn-xs"
									disabled="disabled" style="margin-top: -19px; margin-right: -14px; margin-bottom: 5px;">
									<i class="fa fa-times" aria-hidden="true"></i>
								</button>
								<div class='form-group' id='analysis_${index}'>
									<label class='label-control col-sm-4'><spring:message code='label.analysis' text="Analysis" /></label>
									<div class='col-sm-8'>
										<select class='form-control' name='analysis' id='analysis-selector-${index}' data-parent='#customer-selector' data-target='#analysis-version-selector-${index}'
											data-index='${index}'>
											<option value="-"><spring:message code='label.action.choose' text='Choose...' /></option>
										</select>
									</div>
								</div>
								<div class='form-group' id='analysis_${index}'>
									<label class='label-control col-sm-4'><spring:message code='label.analysis.version' text="Version" /></label>
									<div class='col-sm-8'>
										<select class='form-control' name='version' data-parent='#analysis-selector-${index}' id='analysis-version-selector-${index}' data-index='${index}'>
											<option value="-"><spring:message code='label.action.choose' text='Choose...' /></option>
										</select>
									</div>
								</div>
							</div>
						</div>
					</c:forEach>
				</div>
				<div class='clearfix'></div>
			</fieldset>
			<div class="col-lg-9 max-height">
				<ul class="nav nav-tabs col-xs-12 nav-tab" role='nav-tabs'>
					<li class='active'><a id="headingTotalALE" role='tab' role='button' data-toggle='tab' href="#tabTotalALE"> <spring:message code='label.title.total_ale'
								text="Total ALE" />
					</a></li>
					<li><a id="headingAleByScenarioType" role='tab' role='button' data-toggle='tab' href="#tabAleByScenarioType"> <spring:message code='label.title.ale_by_scenario_type'
								text="ALE by scenario type" />
					</a></li>

					<li><a id="headingAleByAssetType" role='tab' role='button' data-toggle='tab' href="#tabAleByAssetType"> <spring:message code='label.title.ale_by_asset_type'
								text="ALE by asset type" />
					</a></li>

					<li><a id="headingAleByScenario" role='tab' role='button' data-toggle='tab' href="#tabAleByScenario"> <spring:message code='label.title.ale_by_scenario'
								text="ALE By Scenario" />
					</a></li>

					<li><a id="headingAleByAsset" role='tab' role='button' data-toggle='tab' href="#tabAleByAsset"> <spring:message code='label.title.ale_by_asset' text="ALE by asset" />
					</a></li>


					<li><a id="headingCompliance" role='tab' role='button' data-toggle='tab' href="#tabCompliance"> <spring:message code='label.title.compliance' text="Compliance" />
					</a></li>

				</ul>
				<div class='tab-content max-height' style="padding-bottom: 30px;">
					<div id='tabTotalALE' class='tab-pane active max-height' style="padding-top: 70px"></div>
					<div id='tabAleByScenarioType' class='tab-pane max-height' style="padding-top: 70px"></div>
					<div id='tabAleByAssetType' class='tab-pane max-height' style="padding-top: 70px"></div>
					<div id='tabAleByScenario' class='tab-pane max-height' style="padding-top: 70px"></div>
					<div id='tabAleByAsset' class='tab-pane max-height' style="padding-top: 70px"></div>
					<div id='tabCompliance' class='tab-pane max-height' style="padding-top: 70px"></div>
				</div>
				<div class='clearfix'></div>
			</div>
			<!-- ################################################################ Include Footer ################################################################ -->
		</div>
		<jsp:include page="../../template/footer.jsp" />
		<!-- ################################################################ End Container ################################################################# -->
		<jsp:include page="../../template/scripts.jsp" />
		<script src="<spring:url value="/js/highcharts/highcharts.js" />"></script>
		<script src="<spring:url value="/js/highcharts/highcharts-more.js" />"></script>
		<script src="<spring:url value="/js/trickservice/risk-evolution.js" />"></script>
	</div>
</body>
<!-- ################################################################### End HTML ################################################################### -->
</html>