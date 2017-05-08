<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<fmt:setLocale value="fr" scope="session" />
<div class="tab-pane" id="tab-parameter-impact-probability">
	<div class='section row' id='section_parameter_impact_probability'>
		<div class="page-header tab-content-header">
			<div class="container">
				<div class="row-fluid">
					<h3>
						<spring:message code='label.title.impact_probability' />
					</h3>
				</div>
			</div>
		</div>
		<div class="row">
			<jsp:include page="probability/section.jsp" />
		</div>
		<div class="row">
			<c:if test="${type.quantitative}">
				<jsp:include page="impact/financial.jsp" />
			</c:if>
			<c:if test="${type.qualitative}">
				<jsp:include page="impact/qualitative.jsp" />
			</c:if>
		</div>
	</div>
</div>