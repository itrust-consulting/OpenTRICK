<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fct" uri="https://trickservice.com/tags/functions"%>
<fmt:setLocale value="fr" scope="session" />
<c:choose>
	<c:when test="${not empty assessments}">
		<jsp:include page="assessments.jsp" />
	</c:when>
	<c:when test="${not empty assessment}">
		<div id="estimation-ui" data-view-type='single' data-view-name='estimation-ui' class='col-lg-10 col-md-9 trick-ui' data-trick-asset-id='${asset.id}' data-trick-scenario-id='${scenario.id}' data-trick-content='asset'>
			<c:choose>
				<c:when test="${type.qualitative}">
					<jsp:include page="../form/qualitative.jsp" />
				</c:when>
				<c:otherwise>
					<c:set var="rowLength" value="${showHiddenComment? '14' : '30'}" scope="request"/>
					<jsp:include page="../form/quantitative.jsp" />
				</c:otherwise>
			</c:choose>
		</div>
	</c:when>
	<c:otherwise>
		<div id="estimation-ui" data-view-type='empty' data-view-name='estimation-ui' class='col-md-9 trick-ui' data-trick-asset-id='-2' data-trick-scenario-id='-2' data-trick-content='asset'></div>
	</c:otherwise>
</c:choose>
