<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<script src="<spring:url value="/js/jquery-2.2.4.min.js" />"></script>
<script src="<spring:url value="/js/bootstrap/bootstrap.min.js" />"></script>
<sec:authorize access="hasAnyRole('ROLE_ADMIN','ROLE_SUPERVISOR','ROLE_CONSULTANT', 'ROLE_USER')">
	<script src="<spring:url value="/js/bootstrap/bootstrap-notify.min.js" />"></script>
	<script src="<spring:url value="/js/jquery-ui.min.js" />"></script>
	<script src="<spring:url value="/js/dom-parser.js" />"></script>
	<script src="<spring:url value="/js/naturalSort.js" />"></script>
	<script src="<spring:url value="/js/bootstrap/stickyTableHeaders.js" />"></script>
	<script src="<spring:url value="/js/jquery.idle.js" />"></script>
	<script src="<spring:url value="/js/main.js" />"></script>
	<script src="<spring:url value="/js/trickservice/reloadSection.js" />"></script>
	<script src="<spring:url value="/js/trickservice/taskmanager.js" />"></script>
	<script src="<spring:url value="/js/trickservice/progressbar.js" />"></script>
	<script src="<spring:url value="/js/trickservice/modal.js" />"></script>
	<sec:authorize access="hasAnyRole('ROLE_SUPERVISOR')">
		<script src="<spring:url value="/js/trickservice/patch.js" />"></script>
	</sec:authorize>
	<script src="<spring:url value="/js/trickservice/profile.js" />"></script>
	
	<script type="text/javascript">
	<!--
		resolveMessage("error.timeout", "<spring:message code='error.timeout' />");
		resolveMessage("label.action.remove", "<spring:message code='label.action.remove' />");
		resolveMessage("label.action.add", "<spring:message code='label.action.add' />");
		resolveMessage("error.not_authorized", "<spring:message code='error.not_authorized' />");
		resolveMessage("error.forbidden", "<spring:message code='error.forbidden' htmlEscape='false'/>");
		resolveMessage("error.unknown.occurred", "<spring:message code='error.unknown.occurred' />");
		resolveMessage("label.index.chapter", "<spring:message code='label.index.chapter' />");
		resolveMessage("error.session.expired", "<spring:message code='error.session.expired' />");
		resolveMessage("error.session.expire.monitor", "<spring:message code='error.session.expire.monitor' />");
		resolveMessage("info.session.expire.in.x.seconds", "<spring:message code='info.session.expire.in.x.seconds' />");
		resolveMessage("info.session.expire.in.x.minutes", "<spring:message code='info.session.expire.in.x.minutes' />");
		resolveMessage("label.title.compute.action_plan", "<spring:message code='label.title.compute.action_plan' />");
		resolveMessage("label.title.compute.risk_register", "<spring:message code='label.title.compute.risk_register' />");
		resolveMessage("label.title.create.analysis.profile", "<spring:message code='label.title.create.analysis.profile' />");
		resolveMessage("label.title.create.analysis.version", "<spring:message code='label.title.create.analysis.version' />");
		resolveMessage("label.title.export.analysis", "<spring:message code='label.title.export.analysis' />");
		resolveMessage("label.title.export.analysis.report", "<spring:message code='label.title.export.analysis.report' />");
		resolveMessage("label.title.export.soa", "<spring:message code='label.title.export.soa' />");
		resolveMessage("label.title.generate.ticket", "<spring:message code='label.title.generate.ticket' />");
		resolveMessage("label.title.import.analysis", "<spring:message code='label.title.import.analysis' />");
		resolveMessage("label.title.import.risk.information", "<spring:message code='label.title.import.risk.information' />");
		resolveMessage("label.title.install.application", "<spring:message code='label.title.install.application' />");
		resolveMessage("label.title.reset.analysis.right", "<spring:message code='label.title.reset.analysis.right' />");
		resolveMessage("label.title.import.measure.collection", "<spring:message code='label.title.import.measure.collection' />");
		resolveMessage("label.title.compute.dynamic.parameter", "<spring:message code='label.title.compute.dynamic.parameter' />");
		resolveMessage("label.title.export.risk_register", "<spring:message code='label.title.export.risk_register' />");
		resolveMessage("label.title.export.risk_sheet", "<spring:message code='label.title.export.risk_sheet' />");
		application['taskManager'] = new TaskManager().Start();
		-->
	</script>
</sec:authorize>
<sec:authorize access="hasRole('ROLE_PRE_AUTHEN')">
	<script src="<spring:url value="/js/trickservice/otp.js" />"></script>
</sec:authorize>