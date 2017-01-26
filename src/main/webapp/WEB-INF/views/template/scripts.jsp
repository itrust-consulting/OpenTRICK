<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<script src="<spring:url value="/js/jquery-2.2.4.min.js" />"></script>
<script src="<spring:url value="/js/bootstrap/bootstrap.min.js" />"></script>
<sec:authorize access="authenticated">
	<script src="<spring:url value="/js/bootstrap/bootstrap-notify.min.js" />"></script>
	<script src="<spring:url value="/js/jquery-ui.min.js" />"></script>
	<script src="<spring:url value="/js/dom-parser.js" />"></script>
	<script src="<spring:url value="/js/naturalSort.js" />"></script>
	<script src="<spring:url value="/js/bootstrap/stickyTableHeaders.js" />"></script>
	<script src="<spring:url value="/js/jquery.idle.js" />"></script>
	<script src="<spring:url value="/js/main.js" />"></script>
	<script src="<spring:url value="/js/trickservice/timeoutmanager.js" />"></script>
	<script src="<spring:url value="/js/trickservice/reloadSection.js" />"></script>
	<script src="<spring:url value="/js/trickservice/login.js" />"></script>
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
		
		application['taskManager'] = new TaskManager().Start();
		-->
	</script>
</sec:authorize>