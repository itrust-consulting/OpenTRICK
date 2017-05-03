<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<spring:eval expression="T(lu.itrust.business.TS.model.analysis.Analysis).SplitParameters(parameters)" var="mappedParameters" scope="request" />
<c:choose>
	<c:when test="${type.qualitative}">
		<!-- probability and various -->
		<jsp:include page="qualitative/home.jsp" />
		<c:if test="${type.quantitative and hasMaturity}">
			<jsp:include page="maturity/home.jsp" />
		</c:if>
	</c:when>
	<c:otherwise>
		<jsp:include page="quantitative/home.jsp" />
		<c:if test="${hasMaturity}">
			<jsp:include page="maturity/home.jsp" />
		</c:if>
	</c:otherwise>
</c:choose>
