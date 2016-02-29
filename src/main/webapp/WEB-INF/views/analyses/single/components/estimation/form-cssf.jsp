<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fct" uri="http://trickservice.itrust.lu/JSTLFunctions"%>
<spring:message code="label.assessment.likelihood.unit" var="probaUnit" />
<c:set var="strategyForm">
	<select name="strategy" class="form-control">
		<c:forEach items="${strategies}" var="strategy">
			<option value="${strategy}" ${riskProfile.riskStrategy==strategy?"selected='selected'":""}><spring:message
					code="label.risk_register.strategy.${strategy.getNameToLower() }" text="${strategy}" /></option>
		</c:forEach>
	</select>
</c:set>
<table class='table alert-warning'>
	<thead>
		<tr>
			<th width="11.11%" rowspan="2" style="text-align: center;"><spring:message code="label.title.likelihood" /></th>
			<th width="44.44%" colspan="4" style="text-align: center;"><spring:message code="label.title.impact" /></th>
			<th width="11.11" rowspan="2" style="border-left: 2px solid window; text-align: center;"><spring:message code="label.title.computed.importance" text="Computed importance" /></th>
			<th width="33.33%" colspan="3" style="border-left: 2px solid window; text-align: center;"><spring:message code="label.title.modelling.raw_eval"
					text="Modelling Raw Evaluation" /></th>
		</tr>
		<tr>
			<th title='<spring:message code="label.title.assessment.impact_rep" />' style="text-align: center;"><spring:message code="label.impact_rep" text="Reputation" /></th>
			<th title='<spring:message code="label.title.assessment.impact_op" />' style="text-align: center;"><spring:message code="label.impact_op" text="Operation" /></th>
			<th title='<spring:message code="label.title.assessment.impact_leg" />' style="text-align: center;"><spring:message code="label.impact_leg" text="Legal" /></th>
			<th title='<spring:message code="label.title.assessment.impact_fin" />' style="text-align: center;"><spring:message code="label.impact_fin" text="Financial" /></th>
			<th style="border-left: 2px solid window; text-align: center;"><spring:message code="label.risk_register.probability" /></th>
			<th style="text-align: center;"><spring:message code="label.risk_register.impact" /></th>
			<th style="text-align: center;"><spring:message code="label.risk_register.importance" text='Importance' /></th>
		</tr>
	</thead>
	<tbody>
		<tr>
			<td style="border-right: 2px solid #ddd;">
				<div class="input-group" align="right">
					<span class="input-group-addon">${probaUnit}</span> <select class="form-control" name="rawProbaImpact.probabitity">
						<c:choose>
							<c:when test="${empty riskProfile.rawProbaImpact.probabitity }">
								<c:forEach items="${probabilities}" var="parameter">
									<option value="${parameter.id}" title="<fmt:formatNumber value="${fct:round(parameter.value,2)}" /> ${probaUnit}"><spring:message text="${parameter.acronym}" /></option>
								</c:forEach>
							</c:when>
							<c:otherwise>
								<option value="${parameter.id}" ${riskProfile.rawProbaImpact.probabitity==parameter?"selected='selected'" }
									title="<fmt:formatNumber value="${fct:round(parameter.value,2)}" /> ${probaUnit}"><spring:message text="${parameter.acronym}" /></option>
							</c:otherwise>
						</c:choose>
					</select>
				</div>
			</td>
			<td>
				<div class="input-group">
					<span class="input-group-addon">k&euro;</span><select class="form-control" name="rawProbaImpact.impactRep">
						<c:choose>
							<c:when test="${empty riskProfile.rawProbaImpact.impactRep}">
								<c:forEach items="${impacts}" var="parameter">
									<option value="${parameter.id}" title="<fmt:formatNumber value="${fct:round(parameter.value*0.001,2)}" /> k&euro;"><spring:message text="${parameter.acronym}" /></option>
								</c:forEach>
							</c:when>
							<c:otherwise>
								<c:forEach items="${impacts}" var="parameter">
									<option value="${parameter.id}" ${riskProfile.rawProbaImpact.impactRep==parameter?"selected='selected'" }
										title="<fmt:formatNumber value="${fct:round(parameter.value*0.001,2)}" /> k&euro;"><spring:message text="${parameter.acronym}" /></option>
								</c:forEach>
							</c:otherwise>
						</c:choose>
					</select>
				</div>
			</td>
			<td>
				<div class="input-group">
					<span class="input-group-addon">k&euro;</span><select class="form-control" name="rawProbaImpact.impactOp">
						<c:choose>
							<c:when test="${empty riskProfile.rawProbaImpact.impactOp}">
								<c:forEach items="${impacts}" var="parameter">
									<option value="${parameter.id}" title="<fmt:formatNumber value="${fct:round(parameter.value*0.001,2)}" /> k&euro;"><spring:message text="${parameter.acronym}" /></option>
								</c:forEach>
							</c:when>
							<c:otherwise>
								<c:forEach items="${impacts}" var="parameter">
									<option value="${parameter.id}" ${riskProfile.rawProbaImpact.impactOp==parameter?"selected='selected'" }
										title="<fmt:formatNumber value="${fct:round(parameter.value*0.001,2)}" /> k&euro;"><spring:message text="${parameter.acronym}" /></option>
								</c:forEach>
							</c:otherwise>
						</c:choose>
					</select>
				</div>
			</td>
			<td>
				<div class="input-group">
					<span class="input-group-addon">k&euro;</span><select class="form-control" name="rawProbaImpact.impactLeg">
						<c:choose>
							<c:when test="${empty riskProfile.rawProbaImpact.impactLeg}">
								<c:forEach items="${impacts}" var="parameter">
									<option value="${parameter.id}" title="<fmt:formatNumber value="${fct:round(parameter.value*0.001,2)}" /> k&euro;"><spring:message text="${parameter.acronym}" /></option>
								</c:forEach>
							</c:when>
							<c:otherwise>
								<c:forEach items="${impacts}" var="parameter">
									<option value="${parameter.id}" ${riskProfile.rawProbaImpact.impactLeg==parameter?"selected='selected'" }
										title="<fmt:formatNumber value="${fct:round(parameter.value*0.001,2)}" /> k&euro;"><spring:message text="${parameter.acronym}" /></option>
								</c:forEach>
							</c:otherwise>
						</c:choose>
					</select>
				</div>
			</td>
			<td>
				<div class="input-group">
					<span class="input-group-addon">k&euro;</span><select class="form-control" name="rawProbaImpact.impactFin">
						<c:choose>
							<c:when test="${empty riskProfile.rawProbaImpact.impactFin}">
								<c:forEach items="${impacts}" var="parameter">
									<option value="${parameter.id}" title="<fmt:formatNumber value="${fct:round(parameter.value*0.001,2)}" /> k&euro;"><spring:message text="${parameter.acronym}" /></option>
								</c:forEach>
							</c:when>
							<c:otherwise>
								<c:forEach items="${impacts}" var="parameter">
									<option value="${parameter.id}" ${riskProfile.rawProbaImpact.impactFin==parameter?"selected='selected'" }
										title="<fmt:formatNumber value="${fct:round(parameter.value*0.001,2)}" /> k&euro;"><spring:message text="${parameter.acronym}" /></option>
								</c:forEach>
							</c:otherwise>
						</c:choose>
					</select>
				</div>
			</td>
			<td style="border-left: 2px solid window;"><input name="computedRawImportance" disabled="disabled" class="form-control numeric"></td>
			<td style="border-left: 2px solid window;"><input name="rawProbability" disabled="disabled" class="form-control numeric"></td>
			<td><input name="rawImpact" disabled="disabled" class="form-control numeric"></td>
			<td><input name="rawImportance" disabled="disabled" class="form-control numeric"></td>
		</tr>
	</tbody>
</table>

<table class='table alert-success'>
	<thead>
		<tr>
			<th width="12.5%" rowspan="2" style="text-align: center;"><spring:message code="label.title.likelihood" /></th>
			<th width="50%" colspan="4" style="text-align: center;"><spring:message code="label.title.impact" /></th>
			<th width="37.5%" colspan="3" style="border-left: 2px solid window; text-align: center;"><spring:message code="label.title.risk_register.net_eval" /></th>
		</tr>
		<tr>
			<th title='<spring:message code="label.title.assessment.impact_rep" />' style="text-align: center;"><spring:message code="label.impact_rep" text="Reputation" /></th>
			<th title='<spring:message code="label.title.assessment.impact_op" />' style="text-align: center;"><spring:message code="label.impact_op" text="Operation" /></th>
			<th title='<spring:message code="label.title.assessment.impact_leg" />' style="text-align: center;"><spring:message code="label.impact_leg" text="Legal" /></th>
			<th title='<spring:message code="label.title.assessment.impact_fin" />' style="text-align: center;"><spring:message code="label.impact_fin" text="Financial" /></th>
			<th style="border-left: 2px solid window; text-align: center;"><spring:message code="label.risk_register.probability" /></th>
			<th style="text-align: center;"><spring:message code="label.risk_register.impact" /></th>
			<th style="text-align: center;"><spring:message code="label.risk_register.importance" /></th>
		</tr>
	</thead>
	<tbody>
		<tr>
			<td style="border-right: 2px solid #ddd;">
				<div class="input-group" align="right">
					<span class="input-group-addon">${probaUnit}</span> <select class="form-control">
						<c:forEach items="${probabilities}" var="parameter">
							<option value="${parameter.id}" title="<fmt:formatNumber value="${fct:round(parameter.value,2)}" /> ${probaUnit}"><spring:message text="${parameter.acronym}" /></option>
						</c:forEach>
					</select>
				</div>
			</td>
			<td>
				<div class="input-group">
					<span class="input-group-addon">k&euro;</span><input name="impactRep" class="form-control numeric">
				</div>
			</td>
			<td>
				<div class="input-group">
					<span class="input-group-addon">k&euro;</span><input name="impactOp" class="form-control numeric">
				</div>
			</td>
			<td>
				<div class="input-group">
					<span class="input-group-addon">k&euro;</span><input name="impactLeg" class="form-control numeric">
				</div>
			</td>
			<td>
				<div class="input-group">
					<span class="input-group-addon">k&euro;</span><input name="impactFin" class="form-control numeric">
				</div>
			</td>

			<td style="border-left: 2px solid window;"><input name="netProbability" disabled="disabled" class="form-control numeric"></td>
			<td><input name="netImpact" disabled="disabled" class="form-control numeric"></td>
			<td><input name="netImportance" disabled="disabled" class="form-control numeric"></td>

		</tr>
	</tbody>
</table>


<table class='table alert-info'>
	<thead>
		<tr>
			<th width="11.11%" rowspan="2" style="text-align: center;"><spring:message code="label.title.likelihood" /></th>
			<th width="44.44%" colspan="4" style="text-align: center;"><spring:message code="label.title.impact" /></th>
			<th width="11.11%" rowspan="2" style="text-align: center; border-left: 2px solid window;"><spring:message code="label.title.computed.importance" text="Computed importance" /></th>
			<th width="33.33%" colspan="3" style="border-left: 2px solid window; text-align: center;"><spring:message code="label.title.modelling.exp_eval"
					text='Modelling Expected Evaluation' /></th>
		</tr>
		<tr>
			<th title='<spring:message code="label.title.assessment.impact_rep" />' style="text-align: center;"><spring:message code="label.impact_rep" text="Reputation" /></th>
			<th title='<spring:message code="label.title.assessment.impact_op" />' style="text-align: center;"><spring:message code="label.impact_op" text="Operation" /></th>
			<th title='<spring:message code="label.title.assessment.impact_leg" />' style="text-align: center;"><spring:message code="label.impact_leg" text="Legal" /></th>
			<th title='<spring:message code="label.title.assessment.impact_fin" />' style="text-align: center;"><spring:message code="label.impact_fin" text="Financial" /></th>
			<th style="border-left: 2px solid window; text-align: center;"><spring:message code="label.risk_register.probability" /></th>
			<th style="text-align: center;"><spring:message code="label.risk_register.impact" /></th>
			<th style="text-align: center;"><spring:message code="label.risk_register.importance" text='Importance' /></th>
		</tr>
	</thead>
	<tbody>
		<tr>
			<td style="border-right: 2px solid #ddd;">
				<div class="input-group" align="right">
					<span class="input-group-addon">${probaUnit}</span> <select class="form-control">
						<c:forEach items="${probabilities}" var="parameter">
							<option value="${parameter.id}" title="<fmt:formatNumber value="${fct:round(parameter.value,2)}" /> ${probaUnit}"><spring:message text="${parameter.acronym}" /></option>
						</c:forEach>
					</select>
				</div>
			</td>
			<td>
				<div class="input-group">
					<span class="input-group-addon">k&euro;</span><select class="form-control">
						<c:forEach items="${impacts}" var="parameter">
							<option value="${parameter.id}" title="<fmt:formatNumber value="${fct:round(parameter.value*0.001,2)}" /> k&euro;"><spring:message text="${parameter.acronym}" /></option>
						</c:forEach>
					</select>
				</div>
			</td>
			<td>
				<div class="input-group">
					<span class="input-group-addon">k&euro;</span><select class="form-control">
						<c:forEach items="${impacts}" var="parameter">
							<option value="${parameter.id}" title="<fmt:formatNumber value="${fct:round(parameter.value*0.001,2)}" /> k&euro;"><spring:message text="${parameter.acronym}" /></option>
						</c:forEach>
					</select>
				</div>
			</td>
			<td>
				<div class="input-group">
					<span class="input-group-addon">k&euro;</span><select class="form-control">
						<c:forEach items="${impacts}" var="parameter">
							<option value="${parameter.id}" title="<fmt:formatNumber value="${fct:round(parameter.value*0.001,2)}" /> k&euro;"><spring:message text="${parameter.acronym}" /></option>
						</c:forEach>
					</select>
				</div>
			</td>
			<td>
				<div class="input-group">
					<span class="input-group-addon">k&euro;</span><select class="form-control">
						<c:forEach items="${impacts}" var="parameter">
							<option value="${parameter.id}" title="<fmt:formatNumber value="${fct:round(parameter.value*0.001,2)}" /> k&euro;"><spring:message text="${parameter.acronym}" /></option>
						</c:forEach>
					</select>
				</div>
			</td>
			<td style="border-left: 2px solid window;"><input name="netImportance" disabled="disabled" class="form-control numeric"></td>
			<td style="border-left: 2px solid window;"><input name="netProbability" disabled="disabled" class="form-control numeric"></td>
			<td><input name="netImpact" disabled="disabled" class="form-control numeric"></td>
			<td><input name="netImportance" disabled="disabled" class="form-control numeric"></td>
		</tr>
	</tbody>
</table>

<table class='table'>
	<thead>

		<c:choose>
			<c:when test="${show_uncertainty}">
				<tr>
					<th width="16.66%" rowspan="2" style="text-align: center;"><spring:message code="label.title.uncertainty" /></th>
					<th width="16.66%" rowspan="2" style="text-align: center;"><spring:message code="label.risk_register.strategy" /></th>
					<th width="16.66%" rowspan="2" style="text-align: center;"><spring:message code="label.title.owner" text="Owner" /></th>
					<th width="50%" colspan="3" style="text-align: center;"><spring:message code="label.title.ale" /></th>
				</tr>
				<tr>
					<th title='<spring:message code="label.title.alep" />' style="text-align: center;"><spring:message code="label.pessimistic" text='Pessimistic' /></th>
					<th title='<spring:message code="label.title.ale" />' style="text-align: center;"><spring:message code="label.normal" text='Normal' /></th>
					<th title='<spring:message code="label.title.aleo" />' style="text-align: center;"><spring:message code="label.optimistic" text='Optimistic' /></th>
				</tr>
			</c:when>
			<c:otherwise>
				<tr>
					<th width="33.33" style="text-align: center;"><spring:message code="label.risk_register.strategy" /></th>
					<th width="33.33" style="text-align: center;"><spring:message code="label.title.owner" text="Owner" /></th>
					<th width="33.33%" style="text-align: center;"><spring:message code="label.title.ale" /></th>
				</tr>
			</c:otherwise>
		</c:choose>
	</thead>
	<tbody>
		<tr>
			<spring:message text="${riskProfile.owner}" var="owner" />
			<c:choose>
				<c:when test="${show_uncertainty}">
					<td style="border-right: 2px solid #ddd;"><input name="uncertainty" class="form-control numeric"
						value='<fmt:formatNumber value="${assessment.uncertainty}" maxFractionDigits="2" />'></td>
					<td>${strategyForm}</td>
					<td style="border-right: 2px solid #ddd;"><input name="owner" class="form-control" value="${owner}" placeholder="${owner}"></td>
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
					<td>${strategyForm}</td>
					<td><input name="owner" class="form-control" value="${owner}" placeholder="owner"></td>
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

<div class='form-group'>
	<spring:message code="label.comment_argumentation" text="Comment / Argumentation" var='comment' />
	<spring:message text="${assessment.comment}" var="commentContent" />
	<label class='label-control'>${comment}</label>
	<textarea class="form-control" name="comment" title="${comment}" style="resize: vertical;" placeholder="${commentContent}" data-trick-type='string'>${commentContent}</textarea>
</div>

<div class='form-group'>
	<spring:message code="label.risk_treatment" text="Risk treatment" var='riskTreatment' />
	<spring:message text='${riskProfile.riskTreatment}' var="riskTreatmentContent" />
	<label class='label-control'>${riskTreatment}</label>
	<textarea class="form-control" name="riskTreatment" title="${riskTreatment}" placeholder="${riskTreatmentContent}" style="resize: vertical;">${riskTreatmentContent}</textarea>
</div>

<div class='form-group'>
	<spring:message code="label.action_paln.including.deadlines" text="Action plan (including deadlines)" var='actionPlan' />
	<label class='label-control'>${actionPlan}</label>
	<spring:message text='${riskProfile.actionPlan}' var="actionPlanContent" />
	<textarea class="form-control" name="actionPlan" title="${actionPlan}" placeholder="${actionPlanContent}" style="resize: vertical;">${actionPlanContent}</textarea>
</div>

<div class='form-group'>
	<spring:message code="label.assessment.hidden_comment" var='hiddenComment' />
	<spring:message text="${assessment.hiddenComment}" var="hiddenCommentContent" />
	<label class='label-control'>${hiddenComment}</label>
	<textarea class="form-control" name="comment" title="${hiddenComment}" style="resize: vertical;" placeholder="${hiddenCommentContent}" data-trick-type='string'>${hiddenCommentContent}</textarea>
</div>