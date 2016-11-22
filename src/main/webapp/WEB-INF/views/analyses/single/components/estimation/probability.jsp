<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<table class="table">
	<thead>
		<tr>
			<th class="textaligncenter"><spring:message code="label.parameter.level" /></th>
			<th class="textaligncenter"><spring:message code="label.parameter.acronym" /></th>
			<th class="textaligncenter"><spring:message code="label.parameter.qualification" /></th>
			<c:if test="${type == 'QUANTITATIVE'}">
				<th class="textaligncenter"><spring:message code="label.parameter.value" /> <spring:message code="label.assessment.likelihood.unit" /></th>
				<th class="textaligncenter"><spring:message code="label.parameter.range.min" /></th>
				<th class="textaligncenter"><spring:message code="label.parameter.range.max" /></th>
			</c:if>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${probabilities}" var="parameter" varStatus="status">
			<tr data-trick-class="ExtendedParameter" data-trick-id="${parameter.id}" style="text-align: center;">
				<td><spring:message text="${parameter.level}" /></td>
				<td><spring:message text="${parameter.acronym}" /></td>
				<td><spring:message text="${parameter.description}" /></td>
				<c:if test="${type == 'QUANTITATIVE'}">
					<c:set var="parameterValue">
						<fmt:formatNumber value="${parameter.value}" />
					</c:set>
					<td title="${parameterValue}"><fmt:formatNumber value="${parameter.value}" maxFractionDigits="2" minFractionDigits="2" /></td>
					<td><fmt:formatNumber value="${parameter.bounds.from}" maxFractionDigits="2" minFractionDigits="2" /></td>
					<td><c:choose>
							<c:when test="${status.index!=10}">
								<fmt:formatNumber value="${parameter.bounds.to}" maxFractionDigits="2" minFractionDigits="2" />
							</c:when>
							<c:otherwise>
								<span style="font-size: 17px;">+&#8734;</span>
							</c:otherwise>
						</c:choose></td>
				</c:if>
			</tr>
		</c:forEach>
	</tbody>
</table>