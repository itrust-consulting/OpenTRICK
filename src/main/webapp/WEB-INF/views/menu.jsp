<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<c:set var="url">
	<%=request
						.getAttribute("javax.servlet.forward.request_uri")%>
</c:set>
<c:set var="menu">
	${fn:substringAfter(fn:substringAfter(url,pageContext.request.contextPath),"/")}
</c:set>
<div class="navbar navbar-default navbar-fixed-top">
	<div class="container">
		<div class="navbar-header">
			<button type="button" class="navbar-toggle" data-toggle="collapse"
				data-target=".navbar-collapse">
				<span class="icon-bar"></span> <span class="icon-bar"></span> <span
					class="icon-bar"></span>
			</button>
		</div>
		<a class="navbar-brand" href="#">TRICK SERVICE</a>
		<div class="collapse navbar-collapse">
			<ul class="nav navbar-nav">
				<li ${menu.equals("home")? "class='active'" : "" }><a
					href="${pageContext.request.contextPath}/home"> <spring:message
							code="label.menu.home" text="Home" />
				</a></li>
				<li ${menu.startsWith("KnowledgeBase")? "class='active'" : ""}><a
					href="${pageContext.request.contextPath}/KnowledgeBase"> <spring:message
							code="label.menu.analysis.knowledgebase" text="KnowLedge Base" />
				</a></li>
				<li
					${menu.startsWith("Analysis") && ! menu.startsWith("Analysis/Import")? "class='active'" : "" }>
					<a href="${pageContext.request.contextPath}/Analysis"> <spring:message
							code="label.menu.analysis.all" text="Analysis" />
				</a> <c:if test="${!empty(sessionScope.selectedAnalysis)}">
						<ul class="dropdown-menu">
							<li><a
								href="${pageContext.request.contextPath}/Analysis/${sessionScope.selectedAnalysis}/Select">Release
									Analysis</a></li>
						</ul>
					</c:if>
				</li>
				<li ${menu.equals("Analysis/Import")? "class='active'" : "" }>
					<a href="${pageContext.request.contextPath}/Analysis/Import"> <spring:message
							code="label.menu.analysis.import" text="Import" />
				</a>
				</li>

			</ul>
			<ul class="nav navbar-nav navbar-right">
				<li ${menu.equals("Admin")? "class='active'" : "" }><a
					href="${pageContext.request.contextPath}/Admin"> <spring:message
							code="label.menu.user.all" text="Admin" />
				</a></li>
				<li><a
					href='<c:url value="${pageContext.request.contextPath}/j_spring_security_logout" />'>
						<spring:message code="label.menu.logout" text="Logout" />
				</a></li>
			</ul>
		</div>
	</div>
</div>