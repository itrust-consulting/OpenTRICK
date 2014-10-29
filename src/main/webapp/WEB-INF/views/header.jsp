<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<head>
<meta charset="utf-8" />
<meta content="itrust consulting s.a.r.l - itrust.lu" name="author" />
<meta content="<spring:message code="label.risk_analysis" text="Analysis de risque"/>" name="description" />
<meta content="width=device-width" name="viewport" />
<link href=<spring:url value="/images/favicon.ico" /> rel="shortcut icon" />
<title><spring:message code="${title}" text="TRICK Service" /></title>
<link rel="stylesheet" type="text/css" href='<spring:url value="/css/bootstrap.min.css" />' />
<link rel="stylesheet" type="text/css" href='<spring:url value="/css/jquery-ui.min.css" />' />
<link rel="stylesheet" type="text/css" href='<spring:url value="/css/font-awesome.min.css" />' />
<link rel="stylesheet" type="text/css" href='<spring:url value="/css/slider.css" />' />
<link rel="stylesheet" type="text/css" href='<spring:url value="/css/datepicker.css" />' />
<link rel="stylesheet" type="text/css" href='<spring:url value="/css/select2-bootstrap.css" />' />
<link rel="stylesheet" type="text/css" href='<spring:url value="/css/main.css" />' />
<script type="text/javascript">
<!--
	var context = '${pageContext.request.contextPath}';
	-->
</script>

<script src="<spring:url value="/js/jquery-2.0.js" />"></script>

<script src="<spring:url value="/js/jquery-ui.min.js" />"></script>

<script src="<spring:url value="/js/dom-parser.js" />"></script>

<script src="<spring:url value="/js/bootstrap/bootstrap.min.js" />"></script>
<script src="<spring:url value="/js/bootstrap/bootbox.min.js" />"></script>
<script src="<spring:url value="/js/bootstrap/bootstrap-slider.js" />"></script>
<script src="<spring:url value="/js/bootstrap/bootstrap-datepicker.js" />"></script>
<script src="<spring:url value="/js/bootstrap/bootstrap-tooltip.js" />"></script>
<script src="<spring:url value="/js/bootstrap/jquery.floatThead.js" />"></script>

<script src="<spring:url value="/js/tablesorter/jquery.tablesorter.min.js" />"></script>
<script src="<spring:url value="/js/tablesorter/jquery.tablesorter.widgets.js" />"></script>
<script src="<spring:url value="/js/tablesorter/jquery.tablesorter.pager.js" />"></script>

<script src="<spring:url value="/js/main.js" />"></script>

<script src="<spring:url value="/js/trickservice/reloadSection.js" />"></script>
<script src="<spring:url value="/js/trickservice/login.js" />"></script>
<script src="<spring:url value="/js/trickservice/timeoutinterceptor.js" />"></script>
<script src="<spring:url value="/js/trickservice/taskmanager.js" />"></script>
<script src="<spring:url value="/js/trickservice/progressbar.js" />"></script>
<script src="<spring:url value="/js/trickservice/modal.js" />"></script>
<script src="<spring:url value="/js/trickservice/patch.js" />"></script>

</head>