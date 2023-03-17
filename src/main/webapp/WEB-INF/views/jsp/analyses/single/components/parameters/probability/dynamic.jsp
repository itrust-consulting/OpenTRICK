<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
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
						<th class="textaligncenter" data-name='action'><spring:message code="label.action" /></th>
					</tr>
				</thead>
				<tbody>
					<fmt:setLocale value="fr" scope="session" />
					<c:forEach items="${mappedParameters['DYNAMIC']}" var="parameter" varStatus="status">
						<spring:message text="${parameter.acronym}" var="dynamicAcronym"/>
						<tr data-trick-class="DynamicParameter" data-trick-id="${parameter.id}">
							<td data-trick-field="acronym" data-trick-field-type="string" class="textaligncenter">${dynamicAcronym}</td>
							<td data-trick-field="value" class="textaligncenter"><fmt:formatNumber value="${parameter.value}" maxFractionDigits="4" minFractionDigits="4" /></td>
							<td class='text-center' data-name='action'><button class='btn btn-danger' onclick="deleteDynamicParameter(${parameter.id},'${dynamicAcronym}')"><i class='glyphicon glyphicon-trash'></i></button></td>
						</tr>
					</c:forEach>
					<fmt:setLocale value="${language}" scope="session" />
				</tbody>
			</table>
		</fieldset>
	</div>
</c:if>