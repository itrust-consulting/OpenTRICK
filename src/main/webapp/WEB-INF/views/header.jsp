<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<fmt:setLocale value="fr_FR" scope="session"/>
<head>
<meta charset="utf-8" />
<meta content="itrust consulting s.a.r.l - itrust.lu" name="author" />
<meta content=<spring:message code="label.risk_analysis" text="Analysis de risque"/> name="description" />
<meta content="width=device-width" name="viewport" />
<link href=<spring:url value="/images/favicon.ico" /> rel="shortcut icon" />
<title><spring:message code="${title}" text="TRICK Service" /></title>
<link rel="stylesheet" type="text/css" href='<spring:url value="/css/jquery-ui.min.css" />' />
<link rel="stylesheet" type="text/css" href='<spring:url value="/css/bootstrap.min.css" />' />
<link rel="stylesheet" type="text/css" href='<spring:url value="/css/bootstrap-theme.min.css" />' />
<link rel="stylesheet" type="text/css" href='<spring:url value="/css/defaultTheme.css" />' />
<link rel="stylesheet" type="text/css" href='<spring:url value="/css/slider.css" />' />
<link rel="stylesheet" type="text/css" href='<spring:url value="/css/datepicker.css" />' />
<link rel="stylesheet" type="text/css" href='<spring:url value="/css/select2-bootstrap.css" />' />
<link rel="stylesheet" type="text/css" href='<spring:url value="/css/theme.bootstrap.css" />' />
<link rel="stylesheet" type="text/css" href='<spring:url value="/css/main.css" />' />
<script type="text/javascript">
<!--
	var context = '${pageContext.request.contextPath}';
-->
</script>
</head>
