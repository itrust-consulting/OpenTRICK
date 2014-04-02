<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!-- ################################################################ Set Page Title ################################################################ -->
<c:set scope="request" var="title">title.analysis</c:set>
<!-- ###################################################################### HTML #################################################################### -->
<html>
<!-- Include Header -->
<jsp:include page="../header.jsp" />
<!-- ################################################################# Start Container ############################################################## -->
<body data-spy="scroll" data-target="#analysismenu" data-offset="40">
	<div id="wrap">
		<!-- ################################################################### Nav Menu ################################################################### -->
		<jsp:include page="../menu.jsp" />
		<div class="container">
			<!-- #################################################################### Content ################################################################### -->
			<!-- #################################################################### Analysis Menu ################################################################### -->
			<c:choose>
				<c:when test="${!empty(sessionScope.selectedAnalysis)}">
					<spring:eval expression="T(lu.itrust.business.component.MeasureManager).ConcatMeasure(analysis.analysisNorms)" var="measures" scope="request" />
					<jsp:include page="analysisMenu.jsp" />
					<jsp:include page="../successErrors.jsp" />
					<div id="nav-container" trick-id="${analysis.id}" trick-rights-id="${analysis.profile? 0 : analysis.getRightsforUserString(login).right.ordinal()}">
						<c:if test="${!KowledgeBaseView}">
							<h2>${analysis.label}|${ analysis.version }</h2>
							<c:set var="histories" value="${analysis.histories}" scope="request" />
							<jsp:include page="./components/history.jsp" />
						</c:if>
						<c:if test="${KowledgeBaseView}">
							<h2>${analysis.identifier}|${ analysis.version }</h2>
						</c:if>
						<c:set var="itemInformations" value="${analysis.itemInformations}" scope="request" />
						<jsp:include page="./components/itemInformation.jsp" />
						<c:set var="parameters" value="${analysis.parameters}" scope="request" />
						<jsp:include page="./components/parameter.jsp" />
						<c:set var="assets" value="${analysis.assets}" scope="request" />
						<jsp:include page="./components/asset.jsp" />
						<c:set var="scenarios" value="${analysis.scenarios}" scope="request" />
						<jsp:include page="./components/scenario.jsp" />
						<c:set var="phases" scope="request" value="${analysis.usedPhases}" />
						<jsp:include page="./components/phase.jsp" />
						<jsp:include page="./components/measure.jsp" />
						<c:if test="${!KowledgeBaseView }">
							<jsp:include page="./components/soa.jsp" />
							<c:set var="actionplans" scope="request" value="${analysis.actionPlans}" />
							<jsp:include page="./components/actionplan.jsp" />
							<script type="text/javascript" src="<spring:url value="js/actionplan.js" />"></script>
							<c:set var="summaries" scope="request" value="${analysis.summaries}" />
							<jsp:include page="./components/summary.jsp" />
							<c:set var="riskregister" scope="request" value="${analysis.riskRegisters}" />
							<jsp:include page="./components/riskregister.jsp" />
							<script type="text/javascript" src="<spring:url value="js/riskregister.js" />"></script>
							<jsp:include page="./components/charts.jsp" />
							<jsp:include page="./components/widgets.jsp" />
						</c:if>
					</div>
				</c:when>
				<c:otherwise>
					<jsp:include page="analyses.jsp" />
				</c:otherwise>
			</c:choose>
			<!-- ################################################################ Include Footer ################################################################ -->
		</div>
	</div>
	<!-- ################################################################ End Container ################################################################# -->
	<jsp:include page="../footer.jsp" />
	<jsp:include page="../scripts.jsp" />
	<c:if test="${empty(sessionScope.selectedAnalysis)}">
		<script type="text/javascript">
			analysisTableSortable();
		</script>
	</c:if>
	<c:if test="${!empty(sessionScope.selectedAnalysis)}">
		<script type="text/javascript" src="http://datatables.net/release-datatables/media/js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="http://datatables.net/release-datatables/extras/FixedColumns/media/js/FixedColumns.js"></script>
		<script type="text/javascript">
			var el = null;

			

			$(document).ready(function() {

				// load charts
				reloadCharts();

				$("input[type='checkbox']").removeAttr("checked");
				
				$('#table_SOA_27002').fixedHeaderTable({ footer: false, cloneHeadToFoot: false, fixedColumn: false, width:"100%",themeClass: 'table table-hover' });
				$('#table_SOA_27002').css("margin-top","-49px");
				$('div [class="fht-table-wrapper table table-hover"]').css("margin","0");
				$('div [class="fht-table-wrapper table table-hover"]').css("padding","0");
				
				$('.descriptiontooltip').click(function() {

					if (el != null && el.attr("data-original-title") != $(this).attr("data-original-title")) {
						el.popover("hide");
						el = null;
					}

					el = $(this);

					$(this).popover({
						trigger : 'manual',
						placement : 'bottom',
						html : true
					}).popover('toggle');
					return false;
				});

				$("div [class='fht-tbody']").scroll(function() {
					console.log("ohe");
					if (el != null) {
						el.popover('hide');
						$('.popover').remove();
						el = null;
					}
				});
				
				
			});
		</script>
	</c:if>
</body>
<!-- ################################################################### End HTML ################################################################### -->
</html>
