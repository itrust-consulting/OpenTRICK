<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fct" uri="http://trickservice.itrust.lu/JSTLFunctions"%>
<fmt:setLocale value="fr" scope="session" />
<c:choose>
	<c:when test="${not empty assessments}">
		<jsp:include page="assessments.jsp" />
	</c:when>
	<c:when test="${not empty assessment}">
		<div id="estimation-ui" data-view='estimation-ui' class='col-md-10 trick-ui' data-trick-asset-id='${asset.id}' data-trick-scenario-id='${scenario.id}' style="padding-bottom: ${show_cssf? '28': '18'}px;" data-trick-content='scenario'>
			<%-- <fieldset style="display: block; width: 100%; clear: left;">
				<legend>
					<spring:message text='${asset.name}' />
				</legend>
				<div id="description" class='well well-sm' 
				style="word-wrap: break-word; white-space: pre-wrap; resize: vertical; overflow: auto; height: 40px;"><spring:message text="${fn:trim(asset.comment)}" /></div>
			</fieldset> --%>
			<c:choose>
				<c:when test="${type == 'QUALITATIVE'}">
					<jsp:include page="../form/qualitative.jsp" />
				</c:when>
				<c:otherwise>
					<c:set var="rowLength" value="${show_uncertainty? '12' : '13'}" scope="request"/>
					<jsp:include page="../form/quantitative.jsp" />
				</c:otherwise>
			</c:choose>
		</div>
	</c:when>
	<c:otherwise>
		<div id="estimation-ui" data-view='estimation-ui' class='col-md-10 trick-ui' ${scenario.id} data-trick-content='scenario'></div>
	</c:otherwise>
</c:choose>

