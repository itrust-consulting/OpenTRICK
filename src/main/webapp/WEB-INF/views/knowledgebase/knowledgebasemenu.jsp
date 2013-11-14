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
	<%=request.getAttribute("javax.servlet.forward.request_uri")%>
</c:set>
<c:set var="menu">
	${fn:substringAfter(fn:substringAfter(url,pageContext.request.contextPath),"/")}
</c:set>
<div class="col-md-3">
	<div id="knowledgebasemenu" class="bs-sidebar hidden-print affix" role="complementary">
		<ul class="nav bs-sidenav">
			<li class="active">
				<a href="#AssetTypes"> <spring:message code="menu.knowledgebase.assettypes" text="Asset Types" /></a>
			</li>
			<li>
				<a href="#ScenarioTypes"> <spring:message code="menu.knowledgebase.scenariotypes" text="Scenario Types" /></a>
			</li>
			<li>
				<a href="#Customers"> <spring:message code="menu.knowledgebase.customers" text="Customers" /></a>
			</li>
			<li>
				<a href="#Languages"> <spring:message code="menu.knowledgebase.languages" text="Languages" /></a>
			</li>
			<li>
				<a href="#Norms"> <spring:message code="menu.knowledgebase.norms" text="Norms" /></a>
			</li>
		</ul>
	</div>
</div>