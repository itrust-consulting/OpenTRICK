<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<div class="col-md-6">
	<fieldset id="Scale_Impact">
		<legend>
			<spring:message code="label.title.parameter.extended.impact" />
		</legend>
		<table class="table table-hover table-fixed-header-analysis table-condensed">
			<thead>
				<tr>
					<th class="textaligncenter"><spring:message code="label.parameter.level" /></th>
					<th class="textaligncenter"><spring:message code="label.parameter.acronym" /></th>
					<th class="textaligncenter"><spring:message code="label.parameter.qualification" /></th>
					<th class="textaligncenter"><spring:message code="label.parameter.value" /> k&euro;</th>
					<th class="textaligncenter"><spring:message code="label.parameter.range.min" /></th>
					<th class="textaligncenter"><spring:message code="label.parameter.range.max" /></th>
				</tr>
			</thead>
			<tbody>
				<c:set var="length" value="${mappedParameters['IMPACT'].size()-1}" />
				<c:forEach items="${mappedParameters['IMPACT'] }" var="parameter" varStatus="status">
					<tr data-trick-class="ImpactParameter" data-trick-id="${parameter.id}">
						<!--<td>${itemInformation.id}</td>-->
						<td class="textaligncenter"><spring:message text="${parameter.level}" /></td>
						<td data-trick-field="acronym" data-trick-field-type="string" class="success textaligncenter" onclick="return editField(this);"><spring:message
								text="${parameter.acronym}" /></td>
						<td data-trick-field="description" data-trick-field-type="string" class="success textaligncenter" onclick="return editField(this);"><spring:message
								text="${parameter.description}" /></td>
						<td data-trick-field="value" data-trick-field-type="double" title='<fmt:formatNumber value="${parameter.value}" maxFractionDigits="0" />&euro;'
							${(parameter.level mod 2)==0? 'onclick="return editField(this);" class="success textaligncenter"': 'class="textaligncenter"'}><fmt:formatNumber
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