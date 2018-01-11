<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div id="footer" class="navbar navbar-inverse navbar-fixed-bottom" style="height: 30px; min-height: 30px">
	<div class="container" style="height: 100%;">
		<spring:eval expression="T(java.util.Calendar).YEAR" var="YEAR" />
		<spring:eval expression="T(java.util.Calendar).getInstance().get(YEAR)" var="year" />
		<c:if test="${empty locale }">
			<spring:eval expression="T(org.springframework.web.servlet.support.RequestContextUtils).getLocale(pageContext.request)" var="locale" />
		</c:if>
		<c:set var="copyRight">
			<spring:message code="label.copy_right.text" text="2015-${year} itrust consulting - All Rights Reserved" />
		</c:set>
		<c:set var="persiteParameter" value="?${not empty open? 'open='.concat(open.value.concat('&')):'' }" />
		<div class="pull-left" style="width: 25%;">
			<c:choose>
				<c:when test="${locale.language=='en'}">
					<a role="changeUILanguage" data-lang='en' style="color: #c3c3c3; display: inline-block; padding: 5px;"><img src="<spring:url value="/images/flags/en_disabled.png" />" /> English</a>&nbsp;
   				<a href="${persiteParameter}lang=fr" role="changeUILanguage" style="color: #ffffff; display: inline-block; padding: 5px;"
						onclick="return switchLangueTo('${persiteParameter}lang=fr')"><img src="<spring:url value="/images/flags/fr.png" />" /> Français</a>
				</c:when>
				<c:when test="${locale.language=='fr'}">
					<a href="${persiteParameter}lang=en" role="changeUILanguage" style="color: #ffffff; display: inline-block; padding: 5px;"
						onclick="return switchLangueTo('${persiteParameter}lang=en')"><img src="<spring:url value="/images/flags/en.png" />" /> English</a>&nbsp;
   				<a role="changeUILanguage" data-lang='fr' style="color: #c3c3c3; display: inline-block; padding: 5px;"><img src="<spring:url value="/images/flags/fr_disabled.png" />" /> Français</a>
				</c:when>
			</c:choose>
		</div>
		<div style="color: white; text-align: center; width: 50%; margin: 0 auto; margin-top: 5px; float: left;">&copy; ${fn:replace(copyRight,'{0}',year)}</div>
		<spring:eval expression="@propertyConfigurer.getProperty('app.settings.version')" var="appVersion" />
		<spring:eval expression="@propertyConfigurer.getProperty('app.settings.version.revision')" var="appVersionRevision" />


		<div class="pull-right" style="color: white; width: 25%; text-align: right; margin-top: 5px;">
			<sec:authorize access="hasAnyRole('ROLE_ADMIN','ROLE_SUPERVISOR','ROLE_CONSULTANT', 'ROLE_USER')">
				<c:if test="${not empty userGuideURL }">

					<c:choose>
						<c:when test="${userGuideURLInternal}">
							<spring:url value="${userGuideURL}?version=${userGuideVersion}" var="userGuideBaseURL" />
							<a href='${userGuideURL}' style="color: #fff;" data-base-url='${userGuideBaseURL}' target="ts-user-guide"><i class="fa fa-book" aria-hidden="true"></i> <spring:message
									code='label.user.manual' /></a>
						</c:when>
						<c:otherwise>
							<spring:message text="${userGuideURL}" var="userGuideBaseURL" />
							<a href='${userGuideBaseURL}' style="color: #fff;" data-base-url='${userGuideBaseURL}' target="ts-user-guide"><i class="fa fa-book" aria-hidden="true"></i> <spring:message
									code='label.user.manual' /></a>
						</c:otherwise>
					</c:choose>
				</c:if>
			</sec:authorize>
			<img alt="itrust consulting" src=<spring:url value="/images/support/itrust.png" />
				style="height: 30px; margin-top: -8px; margin-right: 10px; padding: 0px 10px; margin-bottom: -8px;"> <span title="v ${appVersion}${appVersionRevision}">v
				${appVersion}</span>
		</div>

	</div>
</div>
<sec:authorize access="hasAnyRole('ROLE_ADMIN','ROLE_SUPERVISOR','ROLE_CONSULTANT', 'ROLE_USER')">
	<div class="hidden" id="controller-notifications">
		<jsp:include page="successErrors.jsp" />
		<jsp:include page="notifications.jsp" />
	</div>
</sec:authorize>

<jsp:include page="../template/alertDialog.jsp" />