<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div id="footer" class="navbar navbar-inverse navbar-fixed-bottom" style="height: 38px;min-height:30px">
	<div class="container">
		<spring:eval expression="T(java.util.Calendar).YEAR" var="YEAR" />
		<spring:eval expression="T(java.util.Calendar).getInstance().get(YEAR)" var="year" />
		<c:set var="copyRight" ><spring:message code="label.copy_right.text" text="2007-${year} itrust consulting - All Rights Reserved"/></c:set>
		<p class="text-muted credit text-center" style="margin-bottom: 10px; margin-top: 10px;">&copy; ${fn:replace(copyRight,'{0}',year)}
		<sec:authorize access="isAuthenticated()" >
			<span class="pull-right">TRICK Service v<spring:eval expression="@propertyConfigurer.getProperty('app.settings.version')" /></span></p>
		</sec:authorize>
	</div>
</div>
