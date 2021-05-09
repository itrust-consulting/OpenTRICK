<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<div class="col-md-6 probability-likelihood">
	<fieldset id="Scale_Probability">
		<legend>
			<c:choose>
				<c:when test="${type == 'QUALITATIVE'}">
					<spring:message code="label.title.probability" />
				</c:when>
				<c:otherwise>
					<spring:message code="label.parameter.extended.probability" />
				</c:otherwise>
			</c:choose>
		</legend>
		<table class="table table-hover table-fixed-header-analysis table-condensed">
			<thead>
				<tr>
					<th width="1%" class="textaligncenter"><spring:message code="label.parameter.level" /></th>
					<c:if test="${type.quantitative}">
						<th class="textaligncenter" width="1%"><spring:message code="label.parameter.acronym" /></th>
					</c:if>
					<c:if test="${type.qualitative}">
						<th class="textaligncenter" width="${type.quantitative?'10' : '20' }%"><spring:message code="label.parameter.label" /></th>
					</c:if>
					<th><spring:message code="label.parameter.qualification" /></th>
					<c:if test="${type.quantitative}">
						<th class="textaligncenter" width="8%"><spring:message code="label.parameter.value" /> <spring:message code="label.assessment.likelihood.unit" /></th>
						<th class="textaligncenter" width="8%"><spring:message code="label.parameter.range.min" /></th>
						<th class="textaligncenter" width="8%"><spring:message code="label.parameter.range.max" /></th>
					</c:if>
				</tr>
			</thead>
			<tbody>
				<c:if test="${type.quantitative}">
					<tr data-trick-class="LikelihoodParameter" hidden="true">
						<td data-trick-field="acronym" colspan="3"><spring:message code='label.status.na' /></td>
						<td data-trick-field="value" colspan="3">0</td>
					</tr>
				</c:if>
				<c:set var="length" value="${mappedParameters['PROBA'].size()-1}" />
				<c:set var='mod2' value="${length mod 2 != 0}" />
				<c:forEach items="${mappedParameters['PROBA']}" var="parameter" varStatus="status">
					<tr data-trick-class="LikelihoodParameter" data-trick-id="${parameter.id}" ${type == 'QUALITATIVE' and parameter.level==0? 'style="display:none"':''}>
						<td data-trick-field="level" class="textaligncenter"><c:choose>
								<c:when test="${parameter.level==0}">
									<spring:message code='label.status.na' />
								</c:when>
								<c:otherwise>
									<spring:message text="${parameter.level}" />
								</c:otherwise>
							</c:choose></td>

						<c:if test="${type.quantitative}">
							<td data-trick-field="acronym" class="textaligncenter"><spring:message text="${parameter.acronym}" /></td>
						</c:if>

						<c:if test="${type.qualitative}">
							<td data-trick-field="label" class="textaligncenter" data-trick-acronym-value='<spring:message text="${parameter.acronym}" />' ><c:choose>
									<c:when test="${parameter.level==0}">
										<spring:message code='label.parameter.label.na' text="${parameter.label}" />
									</c:when>
									<c:otherwise>
										<spring:message text="${parameter.label}" />
									</c:otherwise>
								</c:choose></td>
						</c:if>

						<td data-trick-field="description" data-trick-content="text" data-trick-field-type="string" class="editable" onclick="return editField(this);"><spring:message
								text="${parameter.description}" /></td>
						<c:if test="${type.quantitative}">
							<c:set var="parameterValue">
								<fmt:formatNumber value="${parameter.value}" />
							</c:set>
							<td data-trick-field="value" data-trick-field-type="double"
								${mod2 or parameter.level mod 2 == 0? 'onclick="return editField(this);" class="editable textaligncenter"': 'class="textaligncenter"'} title="${parameterValue}"
								data-real-value="${parameterValue}"><fmt:formatNumber value="${parameter.value}" maxFractionDigits="2" /></td>
							<td class="textaligncenter"><fmt:formatNumber value="${parameter.bounds.from}" maxFractionDigits="2" /></td>
							<td class="textaligncenter"><c:choose>
									<c:when test="${status.index!=length}">
										<fmt:formatNumber value="${parameter.bounds.to}" maxFractionDigits="2" />
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
<c:if test="${type.quantitative and showDynamicAnalysis}">
	<div class="col-md-6 probability-dynamic">
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