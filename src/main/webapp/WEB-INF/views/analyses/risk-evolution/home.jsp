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
				<div class="col-lg-3">
					<div class='panel' style="border: none;">
						<div class="list-group">
							<a class="list-group-item active" id="headingGeneral" role='tab' role='button'>
								<spring:message code='label.title.general' text="General" />
							</a>
						</div>
						<div class="panel-collapse collapse in" id="collapseGeneral" role="tabpanel" aria-labelledby="headingGeneral">
							<div class='panel-body'>
								<div class='form form-horizontal'>
									<div class='form-group'>
										<label><spring:message code='label.customer' text="Customer" /></label> <select class='form-control' name='customer'>
											<option><spring:message code='label.choose' text='Choose...'></spring:message></option>
											<c:forEach var="customer" items="${customers}">
												<option value="${customer.id}"><spring:message text='${customer.organisation}' /></option>
											</c:forEach>
										</select>
									</div>
									<c:forEach var="index" begin="1" end="2" step="1">
										<hr>
										<div class='form-group'>
											<label class='label-control col-sm-4'><spring:message code='label.analysis' text="Analysis" /> ${index}</label>
											<div class='col-sm-8'>
												<select class='form-control' name='analysis'>
												</select>
											</div>
										</div>
										<div class='form-group'>
											<label class='label-control col-sm-4'><spring:message code='label.analysis.version' text="Version" /> ${index}</label>
											<div class='col-sm-8'>
												<select class='form-control' name='analysis'></select>
											</div>
										</div>
									</c:forEach>
								</div>
							</div>
						</div>
						<div class="panel-collapse collapse" id="collapseTotalALE" role="tabpanel" aria-labelledby="headingTotalALE">
							<div class='panel-body'>
								<h4>Total ALE</h4>
							</div>
						</div>
						<div class='clearfix'></div>
					</div>
				</div>

				<div class="col-lg-9">
					<ul class="nav nav-tabs affix affix-top col-xs-12 nav-tab">
						<li class='active'><a id="headingTotalALE" role='tab' role='button' data-toggle='tab' href="#tabTotalALE"> <spring:message code='label.title.total_ale'
									text="Total ALE" />
						</a></li>
						<li><a id="headingAleByScenario" role='tab' role='button' data-toggle='tab' href="#tabByScenario"> <spring:message code='label.title.ale_by_scenario'
									text="ALE By Scenario" />
						</a></li>
						<li><a id="headingAleByScenarioType" role='tab' role='button' data-toggle='tab' href="#tabByScenarioType"> <spring:message code='label.title.ale_by_scenario_type'
									text="ALE by scenario type" />
						</a></li>
						<li><a id="headingAleByAsset" role='tab' role='button' data-toggle='tab' href="#tabAleByAsset"> <spring:message code='label.title.ale_by_asset' text="ALE by asset" />
						</a></li>
						<li><a id="headingAleByAssetType" role='tab' role='button' data-toggle='tab' href="#tabAleByAssetType"> <spring:message code='label.title.total_ale' text="Total ALE" />
						</a></li>
						<li>
					</ul>
					<div class='clearfix'></div>
				</div>
			</div>
			<!-- ################################################################ Include Footer ################################################################ -->
		</div>
		<jsp:include page="../../template/footer.jsp" />
		<!-- ################################################################ End Container ################################################################# -->
		<jsp:include page="../../template/scripts.jsp" />
	</div>
</body>
<!-- ################################################################### End HTML ################################################################### -->
</html>