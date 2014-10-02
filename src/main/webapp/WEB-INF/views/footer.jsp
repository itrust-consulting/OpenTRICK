 <%@page import="java.util.Locale"%>
 <%@page import="javax.servlet.http.HttpServletResponse" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div id="footer" class="navbar navbar-inverse navbar-fixed-bottom" style="height: 38px;min-height:30px">
	<div class="container" style="height:100%;">
		<spring:eval expression="T(java.util.Calendar).YEAR" var="YEAR" />
		<spring:eval expression="T(java.util.Calendar).getInstance().get(YEAR)" var="year" />
		<c:set var="copyRight" ><spring:message code="label.copy_right.text" text="2007-${year} itrust consulting - All Rights Reserved"/></c:set>
			<div class="pull-left" style="width:25%;">
					
			<c:set value="${pageContext.response.locale}" var="language"/>
     		
   			<c:choose>
   			
   			<c:when test="${language.getISO3Language()=='eng'}">
   				<a role="changeUILanguage" style="color:#c3c3c3;display:inline-block; padding:5px;margin-top:5px;"><img src="<spring:url value="/images/flags/en_disabled.png" />" /> English</a>&nbsp;
   				<a href="?lang=fr" role="changeUILanguage" style="color:#ffffff;display:inline-block; padding:5px;"><img src="<spring:url value="/images/flags/fr.png" />" /> Français</a>
   			</c:when>
   			<c:when test="${language.getISO3Language()=='fra'}">
   				<a href="?lang=en" role="changeUILanguage" style="color:#ffffff;display:inline-block; padding:5px;"><img src="<spring:url value="/images/flags/en.png" />" /> English</a>&nbsp;
   				<a role="changeUILanguage" style="color:#c3c3c3;display:inline-block; padding:5px; margin-top:5px;"><img src="<spring:url value="/images/flags/fr_disabled.png" />" /> Français</a>
   			</c:when>
   			</c:choose>
   			
			</div>
			<div style="color:white;text-align:center;width:50%;margin: 0 auto;margin-top: 10px; float:left;"> &copy; ${fn:replace(copyRight,'{0}',year)}</div>
			<div class="pull-right" style="color:white;float:right;width:25%;text-align:right;margin-top: 10px;">v<spring:eval expression="@propertyConfigurer.getProperty('app.settings.version')" /></div>
	</div>
</div>
