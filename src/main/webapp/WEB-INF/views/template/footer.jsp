<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<c:set var="url">
	<%=request.getAttribute("javax.servlet.forward.request_uri")%>
</c:set>
<div id="footer" class="navbar navbar-inverse navbar-fixed-bottom" style="height: 30px; min-height: 30px">
	<div class="container" style="height: 100%;">
		<spring:eval expression="T(java.util.Calendar).YEAR" var="YEAR" />
		<spring:eval expression="T(java.util.Calendar).getInstance().get(YEAR)" var="year" />
		<spring:eval expression="T(org.springframework.web.servlet.support.RequestContextUtils).getLocale(pageContext.request)" var="locale" />
		<c:set var="copyRight">
			<spring:message code="label.copy_right.text" text="2015-${year} itrust consulting - All Rights Reserved" />
		</c:set>
		<c:set var="persiteParameter">
			?
			<c:if test="${isReadOnly}">
				readOnly=true&
			</c:if>
		</c:set>
		<div class="pull-left" style="width: 25%;">
			<c:choose>
				<c:when test="${locale.language=='en'}">
					<a role="changeUILanguage" style="color: #c3c3c3; display: inline-block; padding: 5px;"><img src="<spring:url value="/images/flags/en_disabled.png" />" /> English</a>&nbsp;
   				<a href="${persiteParameter}lang=fr" role="changeUILanguage" style="color: #ffffff; display: inline-block; padding: 5px;"><img
						src="<spring:url value="/images/flags/fr.png" />" /> Français</a>
				</c:when>
				<c:when test="${locale.language=='fr'}">
					<a href="${persiteParameter}lang=en" role="changeUILanguage" style="color: #ffffff; display: inline-block; padding: 5px;"><img
						src="<spring:url value="/images/flags/en.png" />" /> English</a>&nbsp;
   				<a role="changeUILanguage" style="color: #c3c3c3; display: inline-block; padding: 5px;"><img src="<spring:url value="/images/flags/fr_disabled.png" />" /> Français</a>
				</c:when>
			</c:choose>
		</div>
		<div style="color: white; text-align: center; width: 50%; margin: 0 auto; margin-top: 5px; float: left;">&copy; ${fn:replace(copyRight,'{0}',year)}</div>
		<div class="pull-right" style="color: white; float: right; width: 25%; text-align: right; margin-top: 5px;">
			v
			<spring:eval expression="@propertyConfigurer.getProperty('app.settings.version')" />
		</div>
	</div>
</div>
<jsp:include page="../template/alertDialog.jsp" />