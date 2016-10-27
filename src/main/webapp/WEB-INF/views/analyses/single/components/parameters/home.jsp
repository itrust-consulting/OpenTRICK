<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<spring:eval expression="T(lu.itrust.business.TS.model.analysis.Analysis).SplitParameters(parameters)" var="mappedParameters" scope="request" />
<c:choose>
	<c:when test="${type=='QUALITATIVE' }">
		
		<jsp:include page="impact/home.jsp" />
		<jsp:include page="probability/home.jsp" />
	</c:when>
	<c:otherwise>
		<jsp:include page="extended.jsp" />
	</c:otherwise>
</c:choose>
<jsp:include page="others.jsp" />