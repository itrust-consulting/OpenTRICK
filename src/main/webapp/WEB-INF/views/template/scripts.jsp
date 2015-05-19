<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<script src="<spring:url value="/js/jquery-2.0.js" />"></script>
<script src="<spring:url value="/js/bootstrap/bootstrap.min.js" />"></script>
<sec:authorize access="authenticated">
	<script src="<spring:url value="/js/jquery-ui.min.js" />"></script>
	<script src="<spring:url value="/js/dom-parser.js" />"></script>
	<script src="<spring:url value="/js/bootstrap/bootbox.min.js" />"></script>
	<script src="<spring:url value="/js/bootstrap/bootstrap-slider.js" />"></script>
	<script src="<spring:url value="/js/bootstrap/bootstrap-datepicker.js" />"></script>
	<script src="<spring:url value="/js/bootstrap/bootstrap-tooltip.js" />"></script>
	<script src="<spring:url value="/js/bootstrap/stickyTableHeaders.js" />"></script>
	<script  src="<spring:url value="js/jquery.fileDownload.js" />"></script>
	<script src="<spring:url value="/js/main.js" />"></script>
	<script src="<spring:url value="/js/trickservice/reloadSection.js" />"></script>
	<script src="<spring:url value="/js/trickservice/login.js" />"></script>
	<script src="<spring:url value="/js/trickservice/timeoutmanager.js" />"></script>
	<script src="<spring:url value="/js/trickservice/taskmanager.js" />"></script>
	<script src="<spring:url value="/js/trickservice/progressbar.js" />"></script>
	<script src="<spring:url value="/js/trickservice/modal.js" />"></script>
	<script src="<spring:url value="/js/trickservice/patch.js" />"></script>
	<script src="<spring:url value="/js/trickservice/profile.js" />"></script>
	<script type="text/javascript">
	<!--
		application['taskManager'] = new TaskManager().Start();
		-->
	</script>
</sec:authorize>