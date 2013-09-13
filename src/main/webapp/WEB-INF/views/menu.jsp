<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<c:set var="url">
<%=request.getAttribute("javax.servlet.forward.request_uri")%>
</c:set>
<c:set var="menu">
${fn:substringAfter(fn:substringAfter(url,pageContext.request.contextPath),"/")}
</c:set>
<ul>
	<li><a href="${pageContext.request.contextPath}/index" ${menu.equals("index")? "id='active'" : "" }><spring:message code="label.menu.home" text="Home"/></a></li>
	<li><a href="${pageContext.request.contextPath}/import/analysis" ${menu.equals("import/analysis")? "id='active'" : "" }><spring:message code="label.menu.analysis.import" text="Import analysis"/></a></li>
	<li><a href="${pageContext.request.contextPath}/analysis/all" ${menu.equals("analysis/all")? "id='active'" : "" }><spring:message code="label.menu.analysis.all" text="All analysis"/></a></li>
	<li><a href="${pageContext.request.contextPath}/analysis/customers" ${menu.equals("analysis/customers")? "id='active'" : "" }><spring:message code="label.menu.analysis.bycustomers" text="Analysis by customers"/></a></li>
	<li><a href="${pageContext.request.contextPath}/customer/all" ${menu.equals("customer/all")? "id='active'" : "" }><spring:message code="label.menu.analysis.allcustomers" text="All customers"/></a></li>
	<li><a href="${pageContext.request.contextPath}/user/all" ${menu.equals("user/all")? "id='active'" : "" }><spring:message code="label.menu.user.all" text="All users"/></a></li>
	<!--<sec:authorize access="hasRole('ROLE_ADMIN') or hasRole('ROLE_SUPERVISOR')">
		<li><a href="${pageContext.request.contextPath}/admin" ${menu.equals("admin")? "id='active'" : "" }><spring:message code="label.menu.admin_tools" text="Admin Tools"/></a></li>
		<li><a href="${pageContext.request.contextPath}/computer/all" ${menu.equals("computer/all")? "id='active'" : "" }><spring:message code="label.menu.manage_computer" text="Manage Computers"/></a></li>
		<li><a href="${pageContext.request.contextPath}/user/all" ${menu.equals("user/all")? "id='active'" : "" }><spring:message code="label.menu.manage_users" text="Manage Users"/></a></li>
	</sec:authorize>
	<li><a href='<c:url value="${pageContext.request.contextPath}/j_spring_security_logout" />'><spring:message code="label.menu.logout" text="Logout"/></a></li>-->
	<li><a href='<c:url value="${pageContext.request.contextPath}/j_spring_security_logout" />'><spring:message code="label.menu.logout" text="Logout"/></a></li>
</ul>