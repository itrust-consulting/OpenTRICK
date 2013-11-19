<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<script src="<spring:url value="js/jquery-2.0.js" />"></script>
<script src="<spring:url value="js/jquery-ui.js" />"></script>
<!-- <script src="<spring:url value="/js/bootstrap.js" />"></script> -->
<script src="<spring:url value="js/bootstrap.min.js" />"></script>
<script src="<spring:url value="js/bootbox.min.js" />"></script>
<script src="<spring:url value="js/dom-parser.js" />"></script>
<script src="<spring:url value="js/main.js" />"></script>
<script type="text/javascript">
	<!--
	var context = '${pageContext.request.contextPath}';
	-->
	<sec:authorize ifNotGranted="ROLE_ANONYMOUS" >
	<!--
		taskManager = new TaskManager();
		taskManager.Start();
	-->
	</sec:authorize>
</script>