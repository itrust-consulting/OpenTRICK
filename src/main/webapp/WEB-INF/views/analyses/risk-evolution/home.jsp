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
				<div class="col-lg-4">
					<div class="panel-group" id="nav-accordion-risk-evolution" role="tablist" aria-multiselectable="true">

						<div class='panel'>
							<div class="list-group" style="padding: 0">
								<a class="list-group-item active" id="headingGeneral" role='tab' role='button' data-toggle='collapse' data-parent='#nav-accordion-risk-evolution' href="#collapseGeneral">
									<spring:message code='label.title.general' text="General" />
								</a> <a class='list-group-item' id="headingTotalALE" role='tab' role='button' data-toggle='collapse' data-parent='#nav-accordion-risk-evolution' href="#collapseTotalALE"> <spring:message
										code='label.title.total_ale' text="Total ALE" />
								</a>
							</div>
							<div class="panel-collapse collapse in" id="collapseGeneral" role="tabpanel" aria-labelledby="headingGeneral">
								<div class='panel-body'>
									<h4>General</h4>
								</div>
							</div>
							<div class="panel-collapse collapse" id="collapseTotalALE" role="tabpanel" aria-labelledby="headingTotalALE">
								<div class='panel-body'>
									<h4>Total ALE</h4>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="col-lg-8"></div>
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