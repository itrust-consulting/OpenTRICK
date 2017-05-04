<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<spring:eval expression="T(lu.itrust.business.TS.model.analysis.Analysis).SplitParameters(parameters)" var="mappedParameters" scope="request" />
<div class="tab-pane" id="tab-parameter">
	<div class='section row'>
		<div class="page-header tab-content-header">
			<div class="container">
				<div class="row-fluid">
					<h3>
						<spring:message code='label.title.parameter'/>
					</h3>
				</div>
			</div>
		</div>
	</div>
	<div class="row">
		<jsp:include page="other.jsp" />
		<c:if test="${(type.quantitative or not type.qualitative) and hasMaturity}">
			<jsp:include page="maturity/home.jsp" />
		</c:if>
	</div>
</div>
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
		<div id='section_quantitative_parameter'>
			<div class="row">
				<jsp:include page="probability/section.jsp" />
			</div>
			<div class="row">
				<jsp:include page="impact/financial.jsp" />
				<c:if test="${type.qualitative}">
					<jsp:include page="impact/qualitative.jsp" />
				</c:if>
			</div>
		</div>
	</div>
</div>
<c:if test="${(type.quantitative or not type.qualitative) and hasMaturity}">
	<jsp:include page="maturity/home.jsp" />
</c:if>
