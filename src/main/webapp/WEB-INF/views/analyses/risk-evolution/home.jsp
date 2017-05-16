<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
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
					<div class="form-group">
						<label for="type" class="col-sm-4 label-control" data-helper-content='<spring:message code="help.analysis.type"/>'><spring:message code="label.analysis.type"
								text="Type" /></label>
						<div class="col-sm-8" align="center">
							<select class='form-control' name='type' id='type-selector'>
								<c:forEach items="${types}" var="type" varStatus="status">
									<option value="${type}"><spring:message code="label.analysis.type.${fn:toLowerCase(type)}" text="${type}" /></option>
								</c:forEach>
							</select>
						</div>
					</div>
					<div class='form-group'>
						<label class='label-control col-sm-4' data-helper-content='<spring:message code="help.analysis.customer" />' ><spring:message code='label.customer' text="Customer" /></label>
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
								<button data-control="${index}" title="<spring:message code='label.action.remove' text='Remove'/>" class="pull-right btn btn-xs" disabled="disabled"
									style="margin-top: -19px; margin-right: -14px; margin-bottom: 5px;">
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
			<div class="col-md-9 wrap">
				<ul class="nav nav-tabs nav-tab risk-evolution col-xs-12" role='nav-tabs'>
					<li class='active' data-type='QUANTITATIVE'><a id="heading-total-aLE" role='tab' role='button' data-toggle='tab' href="#tab-total-ale"> <spring:message
								code='label.title.total_ale' text="Total ALE" />
					</a></li>
					<li data-type='QUANTITATIVE'><a id="heading-ale-scenario-type" role='tab' role='button' data-toggle='tab' href="#tab-ale-scenario-type"> <spring:message
								code='label.title.ale_by.scenario_type' text="ALE by scenario type" />
					</a></li>
					<li data-type='QUANTITATIVE'><a id="heading-ale-asset-type" role='tab' role='button' data-toggle='tab' href="#tab-ale-asset-type"> <spring:message
								code='label.title.ale_by.asset_type' text="ALE by asset type" />
					</a></li>
					<li data-type='QUANTITATIVE'><a id="heading-ale-scenario" role='tab' role='button' data-toggle='tab' href="#tab-ale-scenario"> <spring:message
								code='label.title.ale_by.scenario' text="ALE By Scenario" />
					</a></li>
					<li data-type='QUANTITATIVE'><a id="heading-ale-Asset" role='tab' role='button' data-toggle='tab' href="#tab-ale-asset"> <spring:message
								code='label.title.ale_by.asset' text="ALE by asset" />
					</a></li>

					<li class='hidden' data-type='QUALITATIVE'><a id="heading-total-risk" role='tab' role='button' data-toggle='tab' href="#tab-total-risk"> <spring:message
								code='label.title.total_risk' text="Total risk" />
					</a></li>
					<li class='hidden' data-type='QUALITATIVE'><a id="heading-risk-scenario-type" role='tab' role='button' data-toggle='tab' href="#tab-risk-scenario-type"> <spring:message
								code='label.title.risk_by.scenario_type' text="Risk by scenario type" />
					</a></li>
					<li class='hidden' data-type='QUALITATIVE'><a id="heading-risk-asset-type" role='tab' role='button' data-toggle='tab' href="#tab-risk-asset-type"> <spring:message
								code='label.title.risk_by.asset_type' text="Risk by asset type" />
					</a></li>
					<li class='hidden' data-type='QUALITATIVE'><a id="heading-risk-scenario" role='tab' role='button' data-toggle='tab' href="#tab-risk-scenario"> <spring:message
								code='label.title.risk_by.scenario' text="Risk By Scenario" />
					</a></li>
					<li class='hidden' data-type='QUALITATIVE'><a id="heading-risk-asset" role='tab' role='button' data-toggle='tab' href="#tab-risk-asset"> <spring:message
								code='label.title.risk_by.asset' text="Risk by asset" />
					</a></li>

					<li data-type='QUANTITATIVE QUALITATIVE'><a id="heading-compliance" role='tab' role='button' data-toggle='tab' href="#tab-compliance"> <spring:message code='label.title.compliance'
								text="Compliance" />
					</a></li>

				</ul>
				<div class='tab-content' style="padding-top: 50px">
					<div id='tab-total-ale' class='tab-pane active'></div>
					<div id='tab-ale-scenario-type' class='tab-pane'></div>
					<div id='tab-ale-asset-type' class='tab-pane'></div>
					<div id='tab-ale-scenario' class='tab-pane'></div>
					<div id='tab-ale-asset' class='tab-pane'></div>
					<div id='tab-total-risk' class='tab-pane'></div>
					<div id='tab-risk-scenario-type' class='tab-pane'></div>
					<div id='tab-risk-asset-type' class='tab-pane'></div>
					<div id='tab-risk-scenario' class='tab-pane'></div>
					<div id='tab-risk-asset' class='tab-pane'></div>
					<div id='tab-compliance' class='tab-pane'></div>
				</div>
				<div class='clearfix'></div>
			</div>
			<!-- ################################################################ Include Footer ################################################################ -->
		</div>
		<jsp:include page="../../template/footer.jsp" />
		<!-- ################################################################ End Container ################################################################# -->
		<jsp:include page="../../template/scripts.jsp" />
		<script src="<spring:url value="/js/chartjs/Chart.bundle.min.js" />"></script>
		<script src="<spring:url value="/js/chartjs/plugins.js" />"></script>
		<script src="<spring:url value="/js/trickservice/risk-evolution.js" />"></script>
		<script type="text/javascript">
			application["settings"] = ${ empty riskEvolutionSettings? '{}' : riskEvolutionSettings };
		</script>
		<div id='view-helper' class='hidden'>
			<jsp:include page="../single/components/parameters/risk-acceptance.jsp" />
		</div>
	</div>
</body>
<!-- ################################################################### End HTML ################################################################### -->
</html>