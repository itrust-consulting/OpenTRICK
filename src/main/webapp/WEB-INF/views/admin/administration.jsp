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
		<div class="container" data-ug-root="administration">
			<!-- #################################################################### Content ################################################################### -->
			<ul class="nav nav-tabs affix affix-top nav-tab">
				<li class="active"><a href="#tab-status" data-toggle="tab"><spring:message code="label.menu.installation.status" text="Status" /></a></li>
				<li><a href="#tab-user" data-toggle="tab"><spring:message code="menu.admin.user" text="User" /></a></li>
				<li><a href="#tab-ids" data-toggle="tab"><spring:message code="menu.admin.ids" text="IDS" /></a></li>
				<li><a href="#tab-customer" data-toggle="tab"><spring:message code="menu.knowledgebase.customers" text="Customers" /></a></li>
				<li><a href="#tab-analyses" data-toggle="tab"><spring:message code="label.analysis.title" text="All Analyses" /></a></li>
				<li><a href="#tab-ts-setting" data-toggle="tab"><spring:message code="label.settings" text="Settings" /></a></li>
				<li><a href="#tab-broadcasting" data-toggle="tab"><spring:message code="label.administrator.messages" text="Messages" /></a></li>
				<li><a href="#tab-log" data-toggle="tab"><spring:message code="label.logs" text="Logs" /></a></li>
			</ul>
			<div class="tab-content" id="tab-container">
				<jsp:include page="status.jsp" />
				<jsp:include page="user/users.jsp" />
				<jsp:include page="ids/home.jsp" />
				<jsp:include page="customer/customers.jsp" />
				<jsp:include page="analysis/analyses.jsp" />
				<jsp:include page="tsSetting/home.jsp" />
				<jsp:include page="notification/home.jsp" />
				<jsp:include page="log/home.jsp" />
				<jsp:include page="widget.jsp" />
				<jsp:include page="../template/tab-option.jsp" />
			</div>
			<!-- ################################################################ End Container ################################################################# -->
		</div>
		<!-- ################################################################ Include Footer ################################################################ -->
		<jsp:include page="../template/footer.jsp" />
	</div>
	<jsp:include page="../template/scripts.jsp" />
	<script type="text/javascript" src="<c:url value="js/trickservice/administration.js" />"></script>
	<script type="text/javascript" src="<c:url value="js/trickservice/customer.js" />"></script>
	<script type="text/javascript" src="<c:url value="js/trickservice/user.js" />"></script>
	<script type="text/javascript" src="<c:url value="js/trickservice/ids.js" />"></script>
	<script type="text/javascript">
		resolveMessage("label.notification.date.until", "<spring:message code='label.notification.date.until' htmlEscape='false'/>");
		resolveMessage("label.notification.date.from", "<spring:message code='label.notification.date.from' />");
		resolveMessage("label.created.date", "<spring:message code='label.created.date' />");
		resolveMessage("label.log.level.info", "<spring:message code='label.log.level.info' />");
		resolveMessage("label.log.level.warning", "<spring:message code='label.log.level.warning' />");
		resolveMessage("label.log.level.error", "<spring:message code='label.log.level.error' />");
		resolveMessage("label.log.level.success", "<spring:message code='label.log.level.success' />");
	</script>
</body>
<!-- ################################################################### End HTML ################################################################### -->
</html>