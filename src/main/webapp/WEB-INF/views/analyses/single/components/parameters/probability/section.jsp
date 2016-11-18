<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<div class="${type=='QUANTITATIVE' ? 'col-md-6' : 'col-lg-6' }">
	<fieldset id="Scale_Probability">
		<legend>
			<c:choose>
				<c:when test="${type == 'QUANTITATIVE'}">
					<spring:message code="label.parameter.extended.probability" />
				</c:when>
				<c:otherwise>
					<spring:message code="label.title.parameter.probability" />
				</c:otherwise>
			</c:choose>

		</legend>
		<table class="table table-hover table-fixed-header-analysis table-condensed">
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
				<tr data-trick-class="LikelihoodParameter" hidden="true">
					<td data-trick-field="acronym" colspan="3"><spring:message text="NA" /></td>
					<td data-trick-field="value" colspan="3">0</td>
				</tr>
				<c:set var="length" value="${mappedParameters['PROBA'].size()-1}" />
				<c:forEach items="${mappedParameters['PROBA']}" var="parameter" varStatus="status">
					<tr data-trick-class="LikelihoodParameter" data-trick-id="${parameter.id}">
						<td data-trick-field="level" class="textaligncenter"><spring:message text="${parameter.level}" /></td>
						<td data-trick-field="acronym" class="textaligncenter"><spring:message
								text="${parameter.acronym}" /></td>
						<td data-trick-field="description" data-trick-field-type="string" class="success textaligncenter" onclick="return editField(this);"><spring:message
								text="${parameter.description}" /></td>
						<c:if test="${type == 'QUANTITATIVE'}">
							<c:set var="parameterValue">
								<fmt:formatNumber value="${parameter.value}" />
							</c:set>
							<td data-trick-field="value" data-trick-field-type="double"
								${(parameter.level mod 2)==0? 'onclick="return editField(this);" class="success textaligncenter"': 'class="textaligncenter"'} title="${parameterValue}"
								data-real-value="${parameterValue}"><fmt:formatNumber value="${parameter.value}" maxFractionDigits="2" minFractionDigits="2" /></td>
							<td class="textaligncenter"><fmt:formatNumber value="${parameter.bounds.from}" maxFractionDigits="2" minFractionDigits="2" /></td>
							<td class="textaligncenter"><c:choose>
									<c:when test="${status.index!=length}">
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
	</fieldset>
</div>
<c:if test="${type == 'QUANTITATIVE'}">
	<div class="col-md-6">
		<fieldset id="DynamicParameters">
			<legend>
				<spring:message code="label.parameter.dynamic.probability" />
			</legend>
			<table class="table table-hover">
				<thead>
					<tr>
						<th class="textaligncenter"><spring:message code="label.parameter.acronym" /></th>
						<th class="textaligncenter"><spring:message code="label.parameter.value" /></th>
					</tr>
				</thead>
				<tbody>
					<fmt:setLocale value="fr" scope="session" />
					<c:forEach items="${mappedParameters['DYNAMIC']}" var="parameter" varStatus="status">
						<tr data-trick-class="DynamicParameter" data-trick-id="${parameter.id}">
							<td data-trick-field="acronym" data-trick-field-type="string" class="textaligncenter"><spring:message text="${parameter.acronym}" /></td>
							<td data-trick-field="value" class="textaligncenter"><fmt:formatNumber value="${parameter.value}" maxFractionDigits="4" minFractionDigits="4" /></td>
						</tr>
					</c:forEach>
					<fmt:setLocale value="${language}" scope="session" />
				</tbody>
			</table>
		</fieldset>
	</div>
</c:if>