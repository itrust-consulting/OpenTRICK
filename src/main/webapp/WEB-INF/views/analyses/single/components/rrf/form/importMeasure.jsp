<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<div data-view-content-name='rrf'>
	<h4><spring:message code="label.title.rrf.import.measure_characteristics" /></h4>
	<form action="${pageContext.request.contextPath}/Analysis/RRF/Import/Save" class="form">
		<div class="form-group">
			<label class='control-label' data-helper-content='<spring:message code="help.rrf.customer" />'> <spring:message code="label.customers" />
			</label> <select class="form-control" name="customer">
				<c:forEach items="${customers}" var="customer">
					<option value="${customer.id}">
						<spring:message text="${customer.organisation}" />
					</option>
				</c:forEach>
			</select>
		</div>
		<div class="form-group">
			<label class='control-label' data-helper-content='<spring:message code="help.rrf.analysis" />'><spring:message code="label.analyses" /></label> <select class="form-control"
				name="analysis">
				<c:forEach items="${analyses}" var="analysis">
					<c:choose>
						<c:when test="${analysis.profile}">
							<option value="${analysis.id}" title='<spring:message text="${analysis.label}"/>' data-trick-id='${analysis.customer.id}'><spring:message text="${analysis.label}" />
							</option>
						</c:when>
						<c:otherwise>
							<option value="${analysis.id}" title='<spring:message text="${analysis.label} - v.${analysis.version}"/>' data-trick-id='${analysis.customer.id}'><spring:message
									text="${analysis.label} - v.${analysis.version}" />
							</option>
						</c:otherwise>
					</c:choose>

				</c:forEach>
			</select>

		</div>
		<div class="form-group">
			<label data-helper-content='<spring:message code="help.rrf.standard" />'><spring:message code="label.standards" /></label> <select class="form-control" name="standards"
				multiple="multiple">
				<c:forEach items="${analyses}" var="analysis">
					<c:forEach items="${analysis.analysisStandards}" var="analysisStandard">
						<c:if test="${standards.contains(analysisStandard.standard)}">
							<option title="${analysisStandard.standard.label} - v.${analysisStandard.standard.version}" value="${analysisStandard.standard.id}" data-trick-id="${analysis.id}"><spring:message
									text="${analysisStandard.standard.label}" /></option>
						</c:if>
					</c:forEach>
				</c:forEach>
			</select>
		</div>
	</form>
</div>