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

<div id="menu">
<ul>
	<li ${menu.equals("home")? "class='active'" : "" }>
		<a href="${pageContext.request.contextPath}/home">
			<spring:message code="label.menu.home" text="Home" />
		</a>
	</li>
	<li ${menu.startsWith("KnowLedgeBase")? "class='active'" : ""}>
		<a href="${pageContext.request.contextPath}/KnowLedgeBase/Display">
			<spring:message code="label.menu.analysis.knowledgebase" text="KnowLedge Base" />
		</a>
	</li>
	<li ${menu.startsWith("Analysis/") && ! menu.startsWith("Analysis/Import")? "class='active'" : "" }>
		<a href="${pageContext.request.contextPath}/Analysis/Display" >
			<spring:message	code="label.menu.analysis.all" text="Analysis" />
		</a>
	</li>
	<li ${menu.equals("Analysis/Import/Display")? "class='active'" : "" }>
		<a href="${pageContext.request.contextPath}/Analysis/Import/Display" >
			<spring:message code="label.menu.analysis.import" text="Import" />
		</a>
	</li>
	<li ${menu.equals("export/analysis")? "class='active'" : "" }>
		<a href="${pageContext.request.contextPath}/export/analysis" >
			<spring:message	code="label.menu.analysis.export" text="Export" />
		</a>
	</li>
	<li ${menu.equals("Admin/Display")? "class='active'" : "" }>
		<a href="${pageContext.request.contextPath}/Admin/Display" >
			<spring:message	code="label.menu.user.all" text="Admin" />
		</a>
	</li>
	<li>
		<a href='<c:url value="${pageContext.request.contextPath}/j_spring_security_logout" />'>
			<spring:message code="label.menu.logout" text="Logout" />
		</a>
	</li>
	<c:if test="${sessionScope.selectedAnalysis!=null}">
	<li><a>Selected Analysis: ${sessionScope.selectedAnalysis}</a></li>
	</c:if>
</ul>
</div>