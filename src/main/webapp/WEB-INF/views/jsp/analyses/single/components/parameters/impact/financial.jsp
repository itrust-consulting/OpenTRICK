<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<div class="col-sm-6 impact-quantitaitve" data-type='${type}'>
	<fieldset id="Scale_Impact">
		<legend>
			<c:choose>
				<c:when test="${type.qualitative}">
					<spring:message code="label.title.parameter.quantitative.impact" />
				</c:when>
				<c:otherwise>
					<spring:message code="label.title.parameter.extended.impact" />
				</c:otherwise>
			</c:choose>
			
		</legend>
		<table class="table table-hover table-fixed-header-analysis table-condensed">
			<thead>
				<tr>
					<th class="textaligncenter" width="1%"><spring:message code="label.parameter.level" /></th>
					<th class="textaligncenter" width="1%"><spring:message code="label.parameter.acronym" /></th>
					<th data-th-name='qualification'><spring:message code="label.parameter.qualification" /></th>
					<th class="textaligncenter" width="8%"><spring:message code="label.parameter.value" /> k&euro;</th>
					<th class="textaligncenter" width="8%"><spring:message code="label.parameter.range.min" /></th>
					<th class="textaligncenter" width="8%"><spring:message code="label.parameter.range.max" /></th>
				</tr>
			</thead>
			<tbody>
				<c:set var="length" value="${mappedParameters['IMPACT'].size()-1}" />
				<c:forEach items="${mappedParameters['IMPACT'] }" var="parameter" varStatus="status">
					<tr data-trick-class="ImpactParameter" data-trick-id="${parameter.id}">
						<td class="textaligncenter"><spring:message text="${parameter.level}" /></td>
						<td data-trick-field="acronym" data-trick-field-type="string" class="textaligncenter"><spring:message
								text="${parameter.acronym}" /></td>
						<td data-trick-field="description" data-trick-content="text" data-trick-field-type="string" class="editable" onclick="return editField(this);"><spring:message
								text="${parameter.description}" /></td>
						<td data-trick-field="value" data-trick-field-type="double" title='<fmt:formatNumber value="${parameter.value}" maxFractionDigits="0" />&euro;'
							onclick="return editField(this);" class="editable textaligncenter"><fmt:formatNumber
								value="${parameter.value*0.001}" maxFractionDigits="0" /></td>
						<td class="textaligncenter"><fmt:formatNumber value="${parameter.bounds.from*0.001}" maxFractionDigits="0" /></td>
						<td class="textaligncenter"><c:choose>
								<c:when test="${status.index != length}">
									<fmt:formatNumber value="${parameter.bounds.to*0.001}" maxFractionDigits="0" />
								</c:when>
								<c:otherwise>
									<span style="font-size: 17px;">+&#8734;</span>
								</c:otherwise>
							</c:choose></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</fieldset>
</div>