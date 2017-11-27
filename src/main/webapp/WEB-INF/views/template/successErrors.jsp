<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<c:if test="${empty locale }">
	<spring:eval expression="T(org.springframework.web.servlet.support.RequestContextUtils).getLocale(pageContext.request)" var="locale" />
</c:if>
<c:if test="${!empty(success)}">
	<div class="alert alert-success" id="success">
		<a href="#" class="close" data-dismiss="alert" style="margin-right: -10px; margin-top: -12px">×</a>
		<div data-notification-type='success'>
			<spring:message code="${success}" text="${success}" />
		</div>
	</div>
	<c:remove var="success" scope="request" />
</c:if>
<c:if test="${not empty(error)}">
	<div class="alert alert-danger" id="error">
		<a href="#" class="close" data-dismiss="alert" style="margin-right: -10px; margin-top: -12px">×</a>
		<div data-notification-type='error'>
			<spring:message code="${error}" text="${error}" />
		</div>
	</div>
	<c:remove var="error" scope="request" />
</c:if>
<c:if test="${not empty(errors)}">
	<div class="alert alert-danger" id="error">
		<a href="#" class="close" data-dismiss="alert" style="margin-right: -10px; margin-top: -12px">×</a>
		<div data-notification-type='error'>
			<spring:message code="${errors}" text="${errors}" />
		</div>
	</div>
	<c:remove var="errors" scope="request" />
</c:if>
<c:if test="${not empty(errorTRICKException)}">
	<div class="alert alert-danger" id="error">
		<a href="#" class="close" data-dismiss="alert" style="margin-right: -10px; margin-top: -12px">×</a>
		<div data-notification-type='error'>
			<spring:message code="${errorTRICKException.code}" arguments="${errorTRICKException.parameters}" text="${errorTRICKException.message}" />
		</div>
	</div>
	<c:remove var="errorTRICKException" scope="request" />
</c:if>
<c:if test="${not empty(errorHANDLER)}">
	<div class="alert alert-danger" id="error">
		<a href="#" class="close" data-dismiss="alert" style="margin-right: -10px; margin-top: -12px">×</a>
		<div data-notification-type='error'>
			<spring:message code="${errorHANDLER.code}" arguments="${errorHANDLER.parameters}" text="${errorHANDLER.message}" />
		</div>
	</div>
	<c:remove var="errorHANDLER" scope="request" />
</c:if>
<c:set var="lang" value="${locale.language=='fr'? 'fr' : 'en'}" />
<c:forEach items="${userNotifcations}" var="notification">
	<c:choose>
		<c:when test="${notification.type=='SUCCESS'}">
			<div class="alert alert-success" id="${notification.id}">
				<a href="#" class="close" data-dismiss="alert" style="margin-right: -10px; margin-top: -12px">×</a>
				<div data-notification-type='success'>
					<spring:message code="${notification.code}" arguments="${notification.parameters}" text="${notification.messages[lang]}" />
				</div>
			</div>
		</c:when>
		<c:when test="${notification.type=='ERROR'}">
			<div class="alert alert-danger" id="${notification.id}">
				<a href="#" class="close" data-dismiss="alert" style="margin-right: -10px; margin-top: -12px">×</a>
				<div data-notification-type='error'>
					<spring:message code="${notification.code}" arguments="${notification.parameters}" text="${notification.messages[lang]}" />
				</div>
			</div>
		</c:when>
		<c:when test="${notification.type=='WARNING'}">
			<div class="alert alert-warning" id="${notification.id}">
				<a href="#" class="close" data-dismiss="alert" style="margin-right: -10px; margin-top: -12px">×</a>
				<div data-notification-type='warning'>
					<spring:message code="${notification.code}" arguments="${notification.parameters}" text="${notification.messages[lang]}" />
				</div>
			</div>
		</c:when>
		<c:otherwise>
			<div class="alert alert-info" id="${notification.id}">
				<a href="#" class="close" data-dismiss="alert" style="margin-right: -10px; margin-top: -12px">×</a>
				<div data-notification-type='info'>
					<spring:message code="${notification.code}" arguments="${notification.parameters}" text="${notification.messages[lang]}" />
				</div>
			</div>
		</c:otherwise>
	</c:choose>
</c:forEach>