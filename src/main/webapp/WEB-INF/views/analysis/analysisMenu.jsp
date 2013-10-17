<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec"	uri="http://www.springframework.org/security/tags"%>
<c:set var="url">
	<%=request.getAttribute("javax.servlet.forward.request_uri")%>
</c:set>
<c:set var="menu">
	${fn:substringAfter(fn:substringAfter(url,pageContext.request.contextPath),"/")}
</c:set>

<div class="menu">
<ul>
	<li>
		<a href="${pageContext.request.contextPath}/home" ${menu.equals("home")? "id='active'" : "" }>
			<spring:message code="label.menu.home" text="Home" />
		</a>
	</li>
	<li>
		<a href="${pageContext.request.contextPath}/KnowLedgeBase/Display" ${menu.startsWith("KnowLedgeBase")? "id='active'" : "" }>
			<spring:message code="label.menu.analysis.knowledgebase" text="KnowLedge Base" />
		</a>
	</li>
	<li>
		<a href="${pageContext.request.contextPath}/Analysis/Display" ${menu.equals("analysis/all")? "id='active'" : "" }>
			<spring:message	code="label.menu.analysis.all" text="Analysis" />
		</a>
	</li>
	<li>
		<a href="${pageContext.request.contextPath}/Analysis/Import/Display" ${menu.startsWith("Analysis")? "id='active'" : "" }>
			<spring:message code="label.menu.analysis.import" text="Import" />
		</a>
	</li>
	<li>
		<a href="${pageContext.request.contextPath}/export/analysis" ${menu.equals("export/analysis")? "id='active'" : "" }>
			<spring:message	code="label.menu.analysis.export" text="Export" />
		</a>
	</li>
	<li>
		<a href="${pageContext.request.contextPath}/Admin/Display" ${menu.equals("Admin/Display")? "id='active'" : "" }>
			<spring:message	code="label.menu.user.all" text="Admin" />
		</a>
	</li>
	<li>
		<a href='<c:url value="${pageContext.request.contextPath}/j_spring_security_logout" />'>
			<spring:message code="label.menu.logout" text="Logout" />
		</a>
	</li>
	<c:if test="${sessionScope.selectedAnalysis!=null}">
	<li class="selectedAnalysis">|||Selected Analysis: ${sessionScope.selectedAnalysis}|||</li>
	</c:if>
</ul>
</div>