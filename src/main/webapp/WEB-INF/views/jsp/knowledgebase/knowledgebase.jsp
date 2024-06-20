<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!-- ###################################################################### HTML #################################################################### -->
<c:if test="${empty locale }">
	<spring:eval expression="T(org.springframework.web.servlet.support.RequestContextUtils).getLocale(pageContext.request)" var="locale" scope="request" />
</c:if>
<!DOCTYPE html>
<html lang="${locale.language}">
<!-- Include Header -->
<c:set scope="request" var="title" value="label.title.knowledgebase" />
<jsp:include page="../template/header.jsp" />
<!-- ################################################################# Start Container ############################################################## -->
<body>
	<div id="wrap" class="wrap">
		<!-- ################################################################### Nav Menu ################################################################### -->
		<jsp:include page="../template/menu.jsp" />
		<div class="container" data-ug-root="knowledgebase">
			<!-- #################################################################### Content ################################################################### -->
			<ul class="nav nav-tabs affix affix-top nav-tab">
				<li class="active"><a href="#tab-customer" data-toggle="tab"><spring:message code="title.menu.knowledgebase.customers" text="Customers" /></a></li>
				<li><a href="#tab-language" data-toggle="tab"><spring:message code="title.menu.knowledgebase.languages" text="Language" /></a></li>

				<li class="dropdown-submenu"><a href="#" class="dropdown-toggle" data-toggle="dropdown"> <spring:message code="label.menu.knowledgebase.standards_measures"
							text="Standards-measures" /><span class="caret"></span></a>
					<ul class="dropdown-menu">
						<li><a href="#tab-standard" data-toggle="tab"><spring:message code="label.menu.knowledgebase.standards" text="Standards" /></a></li>
						<li id="control_tab_measure" style="display: none;"><a href="#tab-measure" data-toggle="tab"><spring:message code="label.menu.knowledgebase.measures" text="Measures" /></a></li>
					</ul></li>

				<li><a href="#tab-scale-type" data-toggle="tab"> <spring:message code="label.menu.knowledgebase.impacts" text="Impacts" /></a></li>
				<li><a href="#tab-analyses" data-toggle="tab"><spring:message code="label.analysis.profile.title" text="Analysis profiles" /></a></li>
			</ul>
			<div class="tab-content" id="tab-container">
				<jsp:include page="customer/customers.jsp" />
				<jsp:include page="language/languages.jsp" />
				<jsp:include page="standards/standard/standards.jsp" />
				<jsp:include page="standards/measure/measures.jsp" />
				<jsp:include page="analysis/analyses.jsp" />
				<jsp:include page="scale/home.jsp" />
				<jsp:include page="../template/tab-option.jsp" />
			</div>

			<!-- ################################################################ End Container ################################################################# -->
		</div>
		<!-- ################################################################ Include Footer ################################################################ -->
		<jsp:include page="../template/footer.jsp" />
		<jsp:include page="widget.jsp" />
	</div>
	<jsp:include page="../template/scripts.jsp" />
	<script type="text/javascript" src="<c:url value="js/trickservice/knowledgebase.js" />"></script>
	<script type="text/javascript" src="<c:url value="js/jquery.fileDownload.js" />"></script>
	<script type="text/javascript" src="<c:url value="js/trickservice/measuredescription.js" />"></script>
	<script type="text/javascript" src="<c:url value="js/trickservice/standard.js" />"></script>
	<script type="text/javascript" src="<c:url value="js/trickservice/language.js" />"></script>
	<script type="text/javascript" src="<c:url value="js/trickservice/customer.js" />"></script>
	<script type="text/javascript" src="<c:url value="js/trickservice/scale.js" />"></script>
</body>
<!-- ################################################################### End HTML ################################################################### -->
</html>