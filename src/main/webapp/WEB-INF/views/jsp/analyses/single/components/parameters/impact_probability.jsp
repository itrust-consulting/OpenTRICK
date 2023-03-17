<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<fmt:setLocale value="fr" scope="session" />
<div class="tab-pane" id="tab-parameter-impact-probability">
	<div class='section' id='section_parameter_impact_probability'>
		<div class="page-header tab-content-header">
			<div class="container">
				<div class="row-fluid">
					<h3>
						<spring:message code='label.title.impact_probability' />
					</h3>
				</div>
			</div>
		</div>

		<c:choose>
			<c:when test="${type.quantitative and not type.qualitative}"> 
				<div class="row">
				    <jsp:include page="impact/financial.jsp" />
				    <jsp:include page="probability/likelihood.jsp" />
					<jsp:include page="probability/dynamic.jsp" />
				</div>
			</c:when>
			<c:otherwise>
				<c:if test="${type.quantitative}">
					<div class="row">
					    <jsp:include page="probability/likelihood.jsp" />
						<jsp:include page="impact/financial.jsp" />
					</div>
				</c:if>
				<jsp:include page="impact/qualitative.jsp" />
			</c:otherwise>
		</c:choose> 
	</div>
</div>