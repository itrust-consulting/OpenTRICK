<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<head>
<meta charset="utf-8" />
<meta name="_csrf" content="${_csrf.token}" />
<meta name="_csrf_header" content="${_csrf.headerName}" />
<meta content="itrust consulting s.a.r.l - itrust.lu" name="author" />
<meta content="<spring:message code="label.risk_analysis" text="Risk analysis"/>" name="description" />
<meta content="width=device-width" name="viewport" />
<link href=<spring:url value="/images/favicon.ico" /> rel="shortcut icon" type="image/x-icon" />
<title><spring:message code="${title}" text="OpenTRICK" /></title>
<link rel="stylesheet" type="text/css" href='<c:url value="/css/bootstrap.min.css" />' />
<link rel="stylesheet" type="text/css" href='<c:url value="/css/jquery-ui.min.css" />' />
<link rel="stylesheet" type="text/css" href='<c:url value="/css/font-awesome.min.css" />' />
<link rel="stylesheet" type="text/css" href='<c:url value="/css/bootstrap.vertical-tabs.min.css" />' />
<c:forEach items="${customCSSs}" var="customCSS">
  <link rel="stylesheet" type="text/css" href='<c:url value="${customCSS}" />'  />
</c:forEach>
<link rel="stylesheet" type="text/css" href='<c:url value="/css/main.css" />'  />
<script type="text/javascript">
<!--
	var context = '${pageContext.request.contextPath}';
	-->
</script>
<!-- It will be updated in menu -->
<c:set var="isAdministration" value="${false}" scope="request" />
</head>