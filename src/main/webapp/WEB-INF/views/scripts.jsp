<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<script src="<spring:url value="/js/jquery-2.0.js" />"></script>

<script src="<spring:url value="/js/dom-parser.js" />"></script>

<script src="<spring:url value="/js/bootstrap/bootstrap.min.js" />"></script>
<script src="<spring:url value="/js/bootstrap/bootbox.min.js" />"></script>
<script src="<spring:url value="/js/bootstrap/bootstrap-slider.js" />"></script>
<script src="<spring:url value="/js/bootstrap/bootstrap-datepicker.js" />"></script>
<script src="<spring:url value="/js/bootstrap/bootstrap-tooltip.js" />"></script>

<script src="<spring:url value="/js/jquery.fixedheadertable.js" />"></script>

<script src="<spring:url value="/js/tablesorter/jquery.tablesorter.min.js" />"></script>
<script src="<spring:url value="/js/tablesorter/jquery.tablesorter.widgets.js" />"></script>
<script src="<spring:url value="/js/tablesorter/jquery.tablesorter.pager.js" />"></script>

<script src="<spring:url value="/js/main.js" />"></script>

<script src="<spring:url value="/js/trickservice/login.js" />"></script>
<script src="<spring:url value="/js/trickservice/timeoutinterceptor.js" />"></script>
<script src="<spring:url value="/js/trickservice/taskmanager.js" />"></script>
<script src="<spring:url value="js/trickservice/progressbar.js" />"></script>
<script src="<spring:url value="js/trickservice/modal.js" />"></script>
<script type="text/javascript">
	<sec:authorize ifNotGranted="ROLE_ANONYMOUS" >
	<!--
	new TimeoutInterceptor().Start();
	new TaskManager().Start();
	-->
	</sec:authorize>
</script>
<jsp:include page="alertDialog.jsp" />
