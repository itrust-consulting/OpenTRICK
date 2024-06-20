<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<c:set var="lang" value="${locale.language=='fr'? 'fr' : 'en'}" />
<c:forEach items="${userNotifcations}" var="notification">
	<c:choose>
		<c:when test="${notification.type=='SUCCESS'}">
			<div class="alert alert-success" id="error-${notification.id}">
				<a href="#" class="close" data-dismiss="alert" style="margin-right: -10px; margin-top: -12px">×</a>
				<div data-notification-type='success' data-type='static' id='${notification.id}' >
					<spring:message code="${notification.code}" arguments="${notification.parameters}" text="${notification.messages[lang]}" />
				</div>
			</div>
		</c:when>
		<c:when test="${notification.type=='ERROR'}">
			<div class="alert alert-danger" id="error-${notification.id}" >
				<a href="#" class="close" data-dismiss="alert" style="margin-right: -10px; margin-top: -12px">×</a>
				<div data-notification-type='error' data-type='static' id='${notification.id}' >
					<spring:message code="${notification.code}" arguments="${notification.parameters}" text="${notification.messages[lang]}" />
				</div>
			</div>
		</c:when>
		<c:when test="${notification.type=='WARNING'}">
			<div class="alert alert-warning" id="warning-${notification.id}">
				<a href="#" class="close" data-dismiss="alert" style="margin-right: -10px; margin-top: -12px">×</a>
				<div data-notification-type='warning' data-type='static' id='${notification.id}'>
					<spring:message code="${notification.code}" arguments="${notification.parameters}" text="${notification.messages[lang]}" />
				</div>
			</div>
		</c:when>
		<c:otherwise>
			<div class="alert alert-info" id="info-${notification.id}">
				<a href="#" class="close" data-dismiss="alert" style="margin-right: -10px; margin-top: -12px">×</a>
				<div data-notification-type='info' data-type='static' id='${notification.id}' >
					<spring:message code="${notification.code}" arguments="${notification.parameters}" text="${notification.messages[lang]}" />
				</div>
			</div>
		</c:otherwise>
	</c:choose>
</c:forEach>