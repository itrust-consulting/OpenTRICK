<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fct" uri="http://trickservice.itrust.lu/JSTLFunctions"%>
<spring:message code="label.assessment.likelihood.unit" var="probaUnit" />
<c:set var="scenarioType" value="${fn:toLowerCase(scenario.type.name)}"/>
<div class="form-group">
	<table class='table'>
		<thead>
			<c:choose>
				<c:when test="${show_uncertainty}">
					<tr>
						<th width="12.5%" style="text-align: center;"><spring:message code="label.risk_register.category" /></th>
						<th width="12.5%" style="text-align: center;"><spring:message code="label.title.impact" /></th>
						<th width="12.5%" style="text-align: center;"><spring:message code="label.title.likelihood" /></th>
						<th width="12.5%" style="text-align: center;"><spring:message code="label.title.uncertainty" /></th>
						<th width="12.5%" style="text-align: center;"><spring:message code="label.title.owner" text="Owner" /></th>
						<th width="12.5%" style="text-align: center;" title='<spring:message code="label.title.alep" />'><spring:message code="label.pessimistic" text='Pessimistic' /></th>
						<th width="12.5%" style="text-align: center;" title='<spring:message code="label.title.ale" />'><spring:message code="label.normal.ale" text='Normal ALE' /></th>
						<th width="12.5%" style="text-align: center;" title='<spring:message code="label.title.aleo" />'><spring:message code="label.optimistic" text='Optimistic' /></th>
					</tr>
				</c:when>
				<c:otherwise>
					<tr>
						<th width="20%" style="text-align: center;"><spring:message code="label.risk_register.category" /></th>
						<th width="20%" style="text-align: center;"><spring:message code="label.title.impact" /></th>
						<th width="20%" style="text-align: center;"><spring:message code="label.title.likelihood" /></th>
						<th width="20%" style="text-align: center;"><spring:message code="label.title.owner" text="Owner" /></th>
						<th width="20%" style="text-align: center;"><spring:message code="label.title.ale" /></th>
					</tr>
				</c:otherwise>
			</c:choose>
		</thead>
		<tbody>
			<tr>
				<td style="border-right: 2px solid #ddd;text-align: center;"><strong><spring:message code="label.scenario.type.${fn:replace(scenarioType,'-','_')}" text="${scenarioType}"/></strong></td>
				<td>
					<div class="input-group">
						<c:catch>
							<fmt:formatNumber value="${fct:round(assessment.impactFin*0.001,2)}" var="impactFin" />
						</c:catch>
						<c:if test="${empty impactFin}">
							<spring:message text="${assessment.impactFin}" var="impactFin" />
						</c:if>
						<span class="input-group-addon" style="padding: 1px;"><button class="btn btn-default" style="padding: 3px" name="impactScale">k&euro;</button></span> <input name="impactFin"
							class="form-control" value="${impactFin}" list="impactList" placeholder="${impactFin}" data-trick-type='string'>
						<datalist id="impactList">
							<c:forEach items="${impacts}" var="parameter">
								<option value='<spring:message text="${parameter.acronym}"/>' title="<fmt:formatNumber value="${fct:round(parameter.value*0.001,2)}" /> k&euro;"><spring:message
										text="${parameter.acronym}" /></option>
							</c:forEach>
						</datalist>
					</div>
				</td>
				<td>
					<div class="input-group" align="right">
						<spring:message text="${assessment.likelihood}" var="likelihood" />
						<span class="input-group-addon" style="padding: 1px;"><button class="btn btn-default" style="padding: 3px" name="probaScale">${probaUnit}</button></span>
						<select class="form-control" name="likelihood" data-trick-type='string' data-trick-value='${likelihood}'>
							<option value="NA" title='0 ${probaUnit}'><spring:message code="label.na" text="NA" /></option>
							<c:forEach items="${probabilities}" var="parameter">
								<option value="${parameter.acronym}" ${likelihood == parameter.acronym? "selected='selected'" : ""}
									title="<fmt:formatNumber value="${fct:round(parameter.value,2)}" />${probaUnit}">${parameter.acronym}</option>
							</c:forEach>
						</select>
					</div>
				</td>
				<spring:message text="${assessment.owner}" var="owner" />
				<c:choose>
					<c:when test="${show_uncertainty}">
						<fmt:formatNumber value="${assessment.uncertainty}" maxFractionDigits="2" var="uncertainty"/>
						<td><input name="uncertainty" class="form-control numeric" data-trick-type='double' value='${uncertainty}' placeholder="${uncertainty}"></td>
						<td style="border-right: 2px solid #ddd;"><input name="owner" class="form-control" value="${owner}" placeholder="${owner}" data-trick-type='string'></td>
						<td>
							<div class="input-group" title="<fmt:formatNumber value="${assessment.ALEP}" maxFractionDigits="2" /> &euro;">
								<span class="input-group-addon">k&euro;</span><input name="ALEP" class="form-control numeric" disabled="disabled"
									value='<fmt:formatNumber value="${fct:round(assessment.ALEP*0.001,1)}" />'>
							</div>
						</td>
						<td>
							<div class="input-group" title="<fmt:formatNumber value="${assessment.ALE}" maxFractionDigits="2" /> &euro;">
								<span class="input-group-addon">k&euro;</span><input name="ALEP" class="form-control numeric" disabled="disabled"
									value='<fmt:formatNumber value="${fct:round(assessment.ALE*0.001,1)}" />'>
							</div>
						</td>
						<td>
							<div class="input-group" title="<fmt:formatNumber value="${assessment.ALEO}" maxFractionDigits="2" /> &euro;">
								<span class="input-group-addon">k&euro;</span><input name="ALEP" class="form-control numeric" disabled="disabled"
									value='<fmt:formatNumber value="${fct:round(assessment.ALEO*0.001,1)}" />'>
							</div>
						</td>
					</c:when>
					<c:otherwise>
						<td style="border-right: 2px solid #ddd;"><input name="owner" class="form-control" value="${owner}" placeholder="${owner}" data-trick-type='string'></td>
						<td>
							<div class="input-group" title="<fmt:formatNumber value="${assessment.ALE}" maxFractionDigits="2" /> &euro;">
								<span class="input-group-addon">k&euro;</span><input name="ALEP" class="form-control numeric" disabled="disabled"
									value='<fmt:formatNumber value="${fct:round(assessment.ALE*0.001,1)}" />'>
							</div>
						</td>
					</c:otherwise>
				</c:choose>
			</tr>
		</tbody>
	</table>
</div>
<div class='form-group'>
	<spring:message code="label.comment" var='comment' />
	<spring:message text="${assessment.comment}" var="commentContent" />
	<label class='label-control'>${comment}</label>
	<textarea rows="${rowLength}" class="form-control" name="comment" title="${comment}" style="resize: vertical;" placeholder="${commentContent}" data-trick-type='string'>${commentContent}</textarea>
</div>
<div class='form-group'>
	<spring:message code="label.assessment.hidden_comment" var='hiddenComment' />
	<spring:message text="${assessment.hiddenComment}" var="hiddenCommentContent" />
	<label class='label-control'>${hiddenComment}</label>
	<textarea rows="${rowLength}" class="form-control" name="hiddenComment" title="${hiddenComment}" style="resize: vertical;" placeholder="${hiddenCommentContent}" data-trick-type='string'>${hiddenCommentContent}</textarea>
</div>