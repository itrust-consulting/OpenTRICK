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
<div class="col-md-3">
	<div id="analysismenu" class="bs-sidebar hidden-print affix"
		role="complementary">
		<ul class="nav bs-sidenav">
			<li><a href="#history"> <spring:message
						code="menu.analysis.history" text="History" />
			</a></li>
			<li><a href="#itemInformation"> <spring:message
						code="menu.analysis.iteminformation" text="Item Information" />
			</a></li>
			<li><a href="#parameter"> <spring:message
						code="menu.analysis.parameter" text="Parameters" />
			</a></li>
			<li><a href="#Asset"> <spring:message
						code="menu.analysis.asset" text="Assets" />
			</a></li>
			<li><a href="#Scenario"> <spring:message
						code="menu.analysis.scenario" text="Scenarios" />
			</a></li>
			<li><a href="#Assessment"> <spring:message
						code="menu.analysis.assessment" text="Assessments" />
			</a></li>
			<li><a href="#Measure"> <spring:message
						code="menu.analysis.measure" text="Measures" />
			</a></li>
			<li><a href="#Phase"> <spring:message
						code="menu.analysis.phase" text="Phases" />
			</a></li>
			<li><a href="#RiskInformation"> <spring:message
						code="menu.analysis.riskinformation" text="Risk Information" />
			</a></li>
			<li><a href="#ActionPlan"> <spring:message
						code="menu.analysis.actionplan" text="Action Plans" />
			</a></li>
		</ul>
	</div>
</div>