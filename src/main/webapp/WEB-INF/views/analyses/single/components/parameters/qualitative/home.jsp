<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:if test="${empty locale }">
	<spring:eval expression="T(org.springframework.web.servlet.support.RequestContextUtils).getLocale(pageContext.request)" var="locale" scope="request" />
</c:if>
<c:if test='${empty  language}'>
	<c:set var="language" value="${locale.language}" scope="request" />
</c:if>
<jsp:include page="section-impact-probability.jsp"/>
<jsp:include page="section-other.jsp"/>