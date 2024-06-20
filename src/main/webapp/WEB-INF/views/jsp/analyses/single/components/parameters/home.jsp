<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<spring:eval expression="T(lu.itrust.business.ts.model.analysis.helper.AnalysisUtils).SplitParameters(parameters)" var="mappedParameters" scope="request" />
<jsp:include page="other.jsp" />
<jsp:include page="impact_probability.jsp" />
<c:if test="${type.quantitative and hasMaturity}">
	<jsp:include page="maturity/home.jsp" />
</c:if>
