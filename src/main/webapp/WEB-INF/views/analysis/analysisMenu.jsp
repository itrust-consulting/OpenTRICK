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

<div id="analysismenu">
	<ul>
		<li ${menu.contains("History")? "class='active'" : "" }>
			<a href="${pageContext.request.contextPath}/Analysis/${sessionScope.selectedAnalysis}/History/Display" >
				<spring:message code="menu.analysis.history" text="History" />
			</a>
		</li>
		<li ${menu.contains("ItemInformation")? "class='active'" : "" }>
			<a href="${pageContext.request.contextPath}/Analysis/${sessionScope.selectedAnalysis}/ItemInformation/Display" >
				<spring:message code="menu.analysis.iteminformation" text="Item Information" />
			</a>
		</li>
		<li ${menu.contains("RiskInformation")? "class='active'" : "" }>
			<a href="${pageContext.request.contextPath}/Analysis/${sessionScope.selectedAnalysis}/RiskInformation/Display">
				<spring:message code="menu.analysis.riskinformation" text="Risk Information" />
			</a>
		</li>
		<li ${menu.contains("Parameter")? "class='active'" : "" }>
			<a href="${pageContext.request.contextPath}/Analysis/${sessionScope.selectedAnalysis}/Parameter/Display">
				<spring:message code="menu.analysis.parameter" text="Parameters" />
			</a>
		</li>
		<li ${menu.contains("Asset")? "class='active'" : "" }>
			<a href="${pageContext.request.contextPath}/Analysis/${sessionScope.selectedAnalysis}/Asset/Display">
				<spring:message	code="menu.analysis.asset" text="Assets" />
			</a>
		</li>
		<li ${menu.contains("Scenario")? "class='active'" : "" }>
			<a href="${pageContext.request.contextPath}/Analysis/${sessionScope.selectedAnalysis}/Scenario/Display">
				<spring:message	code="menu.analysis.scenario" text="Scenarios" />
			</a>
		</li>
		<li ${menu.contains("Assessment")? "class='active'" : "" }>
			<a href="${pageContext.request.contextPath}/Analysis/${sessionScope.selectedAnalysis}/Assessment/Display">
				<spring:message	code="menu.analysis.assessment" text="Assessments" />
			</a>
		</li>
		<li ${menu.contains("Measure")? "class='active'" : "" }>
			<a href="${pageContext.request.contextPath}/Analysis/${sessionScope.selectedAnalysis}/Measure/Display">
				<spring:message	code="menu.analysis.measure" text="Measures" />
			</a>
		</li>
		<li ${menu.contains("Phase")? "class='active'" : "" }>
			<a href="${pageContext.request.contextPath}/Analysis/${sessionScope.selectedAnalysis}/Phase/Display">
				<spring:message	code="menu.analysis.phase" text="Phases" />
			</a>
		</li>
		<li ${menu.contains("ActionPlan")? "class='active'" : "" }>
			<a href="${pageContext.request.contextPath}/Analysis/${sessionScope.selectedAnalysis}/ActionPlan/Display">
				<spring:message	code="menu.analysis.actionplan" text="Action Plans" />
			</a>
		</li>
	</ul>
</div>