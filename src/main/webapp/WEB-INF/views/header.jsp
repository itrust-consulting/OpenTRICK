<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<head>
 <spring:htmlEscape defaultHtmlEscape="true" />
<title>
	<spring:message code="${title}" text="TRICK Service" />
</title>
<!--<link rel="stylesheet" type="text/css" href='<spring:url value="/css/jquery-ui.css" />' /> -->
<link rel="stylesheet" type="text/css" href='<spring:url value="/css/jquery-ui.min.css" />' />
<!--<link rel="stylesheet" type="text/css" href='<spring:url value="/css/bootstrap.css" />' />  -->
<link rel="stylesheet" type="text/css" href='<spring:url value="/css/bootstrap.min.css" />' />
<!--  <link rel="stylesheet" type="text/css" href='<spring:url value="/css/bootstrap-theme.css" />' />-->
<link rel="stylesheet" type="text/css" href='<spring:url value="/css/bootstrap-theme.min.css" />' />
<link rel="stylesheet" type="text/css" href='<spring:url value="/css/main.css" />' />
</head>