<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!-- ################################################################ Set Page Title ################################################################ -->
<c:set scope="request" var="title">label.title.administration</c:set>
<!-- ###################################################################### HTML #################################################################### -->
<!DOCTYPE html>
<html>
<!-- Include Header -->
<jsp:include page="../template/header.jsp" />
<!-- ################################################################# Start Container ############################################################## -->
<body>
	<div id="wrap" class="wrap">
		<!-- ################################################################### Nav Menu ################################################################### -->
		<jsp:include page="../template/menu.jsp" />
		<div class="container">
			<!-- #################################################################### Content ################################################################### -->
			<ul class="nav nav-tabs affix affix-top col-xs-12 nav-tab">
				<li class="active"><a href="#tab_status" data-toggle="tab"><spring:message code="label.menu.installation.status" text="Status" /></a></li>
				<li><a href="#tab_user" data-toggle="tab"><spring:message code="menu.admin.user" text="User" /></a></li>
				<li><a href="#tab_ids" data-toggle="tab"><spring:message code="menu.admin.ids" text="IDS" /></a></li>
				<li><a href="#tab_customer" data-toggle="tab"><spring:message code="menu.knowledgebase.customers" text="Customers" /></a></li>
				<li><a href="#tab_analyses" data-toggle="tab"><spring:message code="label.analysis.title" text="All Analyses" /></a></li>
				<li><a href="#tab_tsSetting" data-toggle="tab"><spring:message code="label.settings" text="Settings" /></a></li>
				<li><a href="#tab_log" data-toggle="tab"><spring:message code="label.logs" text="Logs" /></a></li>
				<li id="tabOption" style="display: none;" class="dropdown-submenu pull-right"><a href="#" title='<fmt:message key="label.options" />' class="dropdown-toggle"
					data-toggle="dropdown" style="padding-bottom: 5px; padding-top: 5px"><span class="fa fa-bars fa-2x"></span></a></li>
				
			</ul>
			<jsp:include page="../template/successErrors.jsp" />
			<div class="tab-content" id="tab-container">
				<jsp:include page="status.jsp" />
				<jsp:include page="user/users.jsp" />
				<jsp:include page="ids/home.jsp" />
				<jsp:include page="customer/customers.jsp" />
				<jsp:include page="analysis/analyses.jsp" />
				<jsp:include page="tsSetting/home.jsp" />
				<jsp:include page="log/home.jsp" />
				<jsp:include page="widget.jsp" />
			</div>
			<!-- ################################################################ End Container ################################################################# -->
		</div>
		<!-- ################################################################ Include Footer ################################################################ -->
		<jsp:include page="../template/footer.jsp" />
	</div>
	<jsp:include page="../template/scripts.jsp" />
	<script type="text/javascript" src="<spring:url value="js/trickservice/administration.js" />"></script>
	<script type="text/javascript" src="<spring:url value="js/trickservice/customer.js" />"></script>
	<script type="text/javascript" src="<spring:url value="js/trickservice/user.js" />"></script>
	<script type="text/javascript" src="<spring:url value="js/trickservice/ids.js" />"></script>
</body>
<!-- ################################################################### End HTML ################################################################### -->
</html>