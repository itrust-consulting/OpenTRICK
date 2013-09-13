<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<head>
 <spring:htmlEscape defaultHtmlEscape="true" />
<title><spring:message code="${title}" text="TRICK Service" /></title>
<link rel="stylesheet"
	href="<spring:url value="/css/jquery.css" />" />
<link rel="stylesheet" type="text/css"
	href='<spring:url value="/css/main.css" />' />
<script src="<spring:url value="/js/jquery-2.0.js" />"></script>
<script src="<spring:url value="/js/jquery-ui.js" />"></script>
<script src="<spring:url value="/js/dom-parser.js" />"></script>
<script src="<spring:url value="/js/main.js" />"></script>
<script type="text/javascript">
var context = '${pageContext.request.contextPath}';
</script>
</head>