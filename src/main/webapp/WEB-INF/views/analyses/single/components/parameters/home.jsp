<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<spring:eval expression="T(lu.itrust.business.TS.model.analysis.Analysis).SplitParameters(parameters)" var="parametersSplited" scope="request"/>
<spring:eval expression="T(lu.itrust.business.TS.model.analysis.Analysis).SplitSimpleParameters(parametersSplited[0])" var="simpleParameters" scope="request" />
<spring:eval expression="T(lu.itrust.business.TS.model.analysis.Analysis).SplitExtendedParameters(parametersSplited[1])" var="extendedParameters" scope="request" />
<c:set var="maturityParameters" value="${parametersSplited[2]}" scope="request"/>
<jsp:include page="extended.jsp" />
<jsp:include page="others.jsp" />