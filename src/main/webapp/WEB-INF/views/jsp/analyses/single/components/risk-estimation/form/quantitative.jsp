<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fct" uri="https://trickservice.com/tags/functions"%>
<spring:message code="label.assessment.likelihood.unit" var="probaUnit" />
<c:set var="scenarioType" value="${fn:toLowerCase(scenario.type.name)}" />
<div class="page-header tab-content-header hidden-xs">
	<div class="container">
		<div class="row-fluid">
			<h3>
				<spring:message text="${asset.name} - ${scenario.name}" />
			</h3>
		</div>
	</div>
</div>
<spring:message code='label.status.na' var="na" />
<div class="form-group">
	<table class='table form-no-fill'>
		<thead>
			<c:choose>
				<c:when test="${show_uncertainty}">
					<tr class="form-group-fill">
						<th class="text-center" width="12.5%"><spring:message code="label.risk_register.category" /></th>
						<th class="text-center" width="12.5%"><spring:message code="label.title.owner" text="Owner" /></th>
						<th class="text-center" width="12.5%"><spring:message code="label.title.impact" /></th>
						<th class="text-center" width="12.5%"><spring:message code="label.title.likelihood" /></th>
						<th class="text-center" width="12.5%"><spring:message code="label.title.uncertainty" /></th>
						<th class='form-estimation form-estimation-left text-center' width="12.5%" title='<spring:message code="label.title.aleo" />'><spring:message code="label.optimistic"
								text='Optimistic' /></th>
						<th class="text-center" width="12.5%" title='<spring:message code="label.title.ale" />'><spring:message code="label.normal.ale" text='Normal ALE' /></th>
						<th class="text-center" width="12.5%" title='<spring:message code="label.title.alep" />'><spring:message code="label.pessimistic" text='Pessimistic' /></th>
					</tr>
				</c:when>
				<c:otherwise>
					<tr class="form-group-fill">
						<th class="text-center" width="20%"><spring:message code="label.risk_register.category" /></th>
						<th class="text-center" width="20%"><spring:message code="label.title.owner" text="Owner" /></th>
						<th class="text-center" width="20%"><spring:message code="label.title.impact" /></th>
						<th class="text-center" width="20%"><spring:message code="label.title.likelihood" /></th>
						<th class='form-estimation form-estimation-left text-center' width="20%"><spring:message code="label.title.ale" /></th>
					</tr>
				</c:otherwise>
			</c:choose>
		</thead>
		<tbody>
			<tr>
				<spring:message text="${assessment.owner}" var="owner" />
				<td class='form-estimation form-estimation-right text-center'><strong><spring:message code="label.scenario.type.${fn:replace(scenarioType,'-','_')}"
							text="${scenarioType}" /></strong></td>
				<td><input name="owner" class="form-control" value="${owner}" placeholder="${owner}" data-trick-type='string'></td>
				<td>
					<div class="input-group">
						<span class="input-group-addon" style="padding: 1px;"><button class="btn btn-default" style="padding: 3px" data-scale-modal="#Scale_Impact">k&euro;</button></span>
						<c:set var="impact" value="${assessment.getImpact('IMPACT')}" />
						<c:choose>
							<c:when test="${empty impact}">
								<input name="IMPACT" class="form-control" value='0' list="dataList-parameter-impact" placeholder="0" data-trick-type='string' title="${impactTypes[0].acronym}0">
							</c:when>
							<c:otherwise>
								<c:choose>
									<c:when test="${impact.real eq impact.raw}">
										<fmt:formatNumber value="${impact.real}" var="realImpact" />
										<c:choose>
											<c:when test="${impact.real<100}">
												<fmt:formatNumber value="${fct:round(impact.real*0.001,3)}" var="impactValue" />
											</c:when>
											<c:when test="${impact.real<1000}">
												<fmt:formatNumber value="${fct:round(impact.real*0.001,2)}" var="impactValue" />
											</c:when>
											<c:when test="${impact.real<10000}">
												<fmt:formatNumber value="${fct:round(impact.real*0.001,1)}" var="impactValue" />
											</c:when>
											<c:otherwise>
												<fmt:formatNumber value="${fct:round(impact.real*0.001,0)}" var="impactValue" />
											</c:otherwise>
										</c:choose>
									</c:when>
									<c:otherwise>
										<spring:message text="${impact.raw}" var="impactValue" />
										<c:choose>
											<c:when test="${impact.real<100}">
												<fmt:formatNumber value="${fct:round(impact.real*0.001,3)}" var="realImpact" />
											</c:when>
											<c:when test="${impact.real<1000}">
												<fmt:formatNumber value="${fct:round(impact.real*0.001,2)}" var="realImpact" />
											</c:when>
											<c:when test="${impact.real<10000}">
												<fmt:formatNumber value="${fct:round(impact.real*0.001,1)}" var="realImpact" />
											</c:when>
											<c:otherwise>
												<fmt:formatNumber value="${fct:round(impact.real*0.001,0)}" var="realImpact" />
											</c:otherwise>
										</c:choose>
									</c:otherwise>
								</c:choose>
								<input name="IMPACT" class="form-control" value="${impactValue}" list="dataList-parameter-impact" placeholder="${impactValue}" data-trick-type='string'
									title="${realImpact}">
							</c:otherwise>
						</c:choose>
					</div>
				</td>
				<td>
					<div class="input-group" align="right">
						<span class="input-group-addon" style="padding: 1px;"><button class="btn btn-default" style="padding: 3px" data-scale-modal="#Scale_Probability,#DynamicParameters">${probaUnit}</button></span>
						<c:set var="likelihood" value="${assessment.likelihood}" />
						<c:choose>
							<c:when test="${empty likelihood}">
								<input name="likelihood" class="form-control" value="${na}" list="dataList-parameter-probability" title="${0}" placeholder="${na}" data-trick-type='string'>
							</c:when>
							<c:when test="${likelihood['class'].simpleName=='RealValue'}">
								<c:choose>
									<c:when test="${likelihood.real==0}">
										<input name="likelihood" class="form-control" value="${na}" list="dataList-parameter-probability" title="${0}" placeholder="${na}" data-trick-type='string'>
									</c:when>
									<c:otherwise>
										<spring:message text="${likelihood.raw}" var="rawProba" />
										<fmt:formatNumber value="${fct:round(likelihood.real,3)}" var="realValue" />
										<input name="likelihood" class="form-control" value='<spring:message text="${likelihood.variable}" />' list="dataList-parameter-probability" title="${realValue}"
											placeholder="${rawProba}" data-trick-type='string'>
									</c:otherwise>
								</c:choose>
							</c:when>
							<c:otherwise>
								<spring:message text="${likelihood.raw}" var="rawProba" />
								<input name="likelihood" class="form-control" value="${rawProba}" list="dataList-parameter-probability" title="${fct:round(likelihood.real,3)}" placeholder="${rawProba}"
									data-trick-type='string'>
							</c:otherwise>
						</c:choose>
					</div>
				</td>
				<c:choose>
					<c:when test="${show_uncertainty}">
						<fmt:formatNumber value="${assessment.uncertainty}" maxFractionDigits="2" var="uncertainty" />
						<td><input name="uncertainty" class="form-control numeric" data-trick-type='double' value='${uncertainty}' placeholder="${uncertainty}"></td>
						<td class='form-estimation form-estimation-left'>
							<div class="input-group" title="<fmt:formatNumber value="${assessment.ALEO}" maxFractionDigits="2" /> &euro;">
								<span class="input-group-addon">k&euro;</span><input name="ALEO" class="form-control numeric" disabled="disabled"
									value='<fmt:formatNumber value="${fct:round(assessment.ALEO*0.001,1)}" />'>
							</div>
						</td>
						<td>
							<div class="input-group" title="<fmt:formatNumber value="${assessment.ALE}" maxFractionDigits="2" /> &euro;">
								<span class="input-group-addon">k&euro;</span><input name="ALE" class="form-control numeric" disabled="disabled"
									value='<fmt:formatNumber value="${fct:round(assessment.ALE*0.001,1)}" />'>
							</div>
						</td>
						<td>
							<div class="input-group" title="<fmt:formatNumber value="${assessment.ALEP}" maxFractionDigits="2" /> &euro;">
								<span class="input-group-addon">k&euro;</span><input name="ALEP" class="form-control numeric" disabled="disabled"
									value='<fmt:formatNumber value="${fct:round(assessment.ALEP*0.001,1)}" />'>
							</div>
						</td>
					</c:when>
					<c:otherwise>
						<td class='form-estimation form-estimation-left'>
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
<div class='form-group form-group-fill'>
	<spring:message code="label.comment" var='comment' />
	<spring:message text="${assessment.comment}" var="commentContent" />
	<label class='label-control'>${comment}</label>
	<textarea id="assessment-comment" rows="${rowLength}" class="form-control" name="comment" title="${comment}" style="resize: vertical;" placeholder="${commentContent}"
		data-trick-type='string'>${commentContent}</textarea>
</div>
<c:if test="${showHiddenComment}">
	<div class='form-group form-group-fill'>
		<spring:message code="label.assessment.hidden_comment" var='hiddenComment' />
		<spring:message text="${assessment.hiddenComment}" var="hiddenCommentContent" />
		<label class='label-control'>${hiddenComment}</label>
		<textarea id="assessment-hiddenComment" rows="${rowLength}" class="form-control" name="hiddenComment" title="${hiddenComment}" style="resize: vertical;"
			placeholder="${hiddenCommentContent}" data-trick-type='string'>${hiddenCommentContent}</textarea>
	</div>
</c:if>