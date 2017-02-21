<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<fieldset>
	<legend>
		<c:choose>
			<c:when test="${isEditable}">
				<spring:message code="label.title.parameter.risk.acceptance.threshold" />
				<span class="pull-right">
					<button class='btn btn-xs btn-link' onclick="return manageRiskAcceptance(true)" style="font-size: 15px">
						<i class="fa fa-cog" aria-hidden="true"></i>
						<spring:message code='label.action.manage' />
					</button>
				</span>
			</c:when>
			<c:otherwise>
				<spring:message code="label.title.parameter.risk.acceptance.threshold" />
			</c:otherwise>
		</c:choose>
	</legend>
	<table class="table table-hover table-condensed" id="table_parameter_risk_acceptance">
		<thead>
			<tr>
				<th class="textaligncenter"><spring:message code="label.importance.threshold" /></th>
				<th style="width: 20%" class="textaligncenter"><spring:message code="label.parameter.label" /></th>
				<th style="width: 50%" class="textaligncenter"><spring:message code="label.description" /></th>
				<th class="textaligncenter"><spring:message code="label.color" /></th>
			</tr>
		</thead>
		<tbody>
			<c:choose>
				<c:when test="${empty mappedParameters['RISK_ACCEPTANCE']}">
					<tr class='warning'>
						<td colspan="4"><spring:message code='info.risk_acceptance.current.empty' /></td>
					</tr>
				</c:when>
				<c:otherwise>
					<c:forEach items="${mappedParameters['RISK_ACCEPTANCE']}" var="parameter">
						<tr data-trick-class="RiskAcceptanceParameter" data-trick-id="${parameter.id}" ${isEditable? 'ondblclick="return manageRiskAcceptance()"':''}
							data-trick-callback='reloadRiskHeatMapSection()'>
							<td class='textaligncenter' data-trick-field="value"><fmt:formatNumber value="${parameter.value}" maxFractionDigits="0" /></td>
							<td class='textaligncenter editable' data-trick-field='label' data-trick-field-type='string' onclick="return editField(this);"><spring:message text="${parameter.label}" /></td>
							<spring:message text="${parameter.color}" var="color" />
							<td class='textaligncenter editable' data-trick-field='description' data-trick-content="text" data-trick-field-type='string' onclick="return editField(this);"><spring:message
									text="${parameter.description}" /></td>
							<td style="background-color: ${color};" data-trick-field='color'></td>
						</tr>
					</c:forEach>
				</c:otherwise>
			</c:choose>
		</tbody>
	</table>
</fieldset>