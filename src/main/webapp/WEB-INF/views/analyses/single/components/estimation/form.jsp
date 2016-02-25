<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fct" uri="http://trickservice.itrust.lu/JSTLFunctions"%>
<spring:message code="label.assessment.likelihood.unit" var="probaUnit"/>
<div class="form-group">
	<table class='table'>
		<thead>
			<c:choose>
				<c:when test="${show_cssf}">
					<c:choose>
						<c:when test="${show_uncertainty}">
							<tr>
								<th width="40%" colspan="4" style="text-align: center;"><spring:message code="label.title.impact" /></th>
								<th width="10%" rowspan="2" style="text-align: center;"><spring:message code="label.title.likelihood" /></th>
								<th width="10%" rowspan="2" style="text-align: center;"><spring:message code="label.title.uncertainty" /></th>
								<th width="10%" rowspan="2" style="text-align: center;"><spring:message code="label.title.owner" text="Owner" /></th>
								<th width="30%" colspan="3" style="text-align: center;"><spring:message code="label.title.ale" /></th>
							</tr>
							<tr>
								<th title='<spring:message code="label.title.assessment.impact_rep" />' style="text-align: center;"><spring:message code="label.impact_rep" text="Reputation" /></th>
								<th title='<spring:message code="label.title.assessment.impact_op" />' style="text-align: center;"><spring:message code="label.impact_op" text="Operational" /></th>
								<th title='<spring:message code="label.title.assessment.impact_leg" />' style="text-align: center;"><spring:message code="label.impact_leg" text="Legal" /></th>
								<th title='<spring:message code="label.title.assessment.impact_fin" />' style="text-align: center;"><spring:message code="label.impact_fin" text="Financial" /></th>
								<th title='<spring:message code="label.title.alep" />' style="text-align: center;"><spring:message code="label.pessimistic" text='Pessimistic' /></th>
								<th title='<spring:message code="label.title.ale" />' style="text-align: center;"><spring:message code="label.normal" text='Normal' /></th>
								<th title='<spring:message code="label.title.aleo" />' style="text-align: center;"><spring:message code="label.optimistic" text='Optimistic' /></th>
							</tr>
						</c:when>
						<c:otherwise>
							<tr>
								<th width="57.14%" colspan="4" style="text-align: center;"><spring:message code="label.title.impact" /></th>
								<th width="14.28%" rowspan="2" style="text-align: center;"><spring:message code="label.title.likelihood" /></th>
								<th width="14.28%" rowspan="2" style="text-align: center;"><spring:message code="label.title.owner" text="Owner" /></th>
								<th width="14.28%" rowspan="2" style="text-align: center;"><spring:message code="label.title.ale" /></th>
							</tr>
							<tr>
								<th title='<spring:message code="label.title.assessment.impact_rep" />' style="text-align: center;"><spring:message code="label.impact_rep" text="Reputation" /></th>
								<th title='<spring:message code="label.title.assessment.impact_op" />' style="text-align: center;"><spring:message code="label.impact_op" text="Operation" /></th>
								<th title='<spring:message code="label.title.assessment.impact_leg" />' style="text-align: center;"><spring:message code="label.impact_leg" text="Legal" /></th>
								<th title='<spring:message code="label.title.assessment.impact_fin" />' style="text-align: center;"><spring:message code="label.impact_fin" text="Finacial" /></th>
							</tr>
						</c:otherwise>
					</c:choose>
				</c:when>
				<c:otherwise>
					<c:choose>
						<c:when test="${show_uncertainty}">
							<tr>
								<th width="14.28%" rowspan="2" style="text-align: center;"><spring:message code="label.title.impact" /></th>
								<th width="14.28%" rowspan="2" style="text-align: center;"><spring:message code="label.title.likelihood" /></th>
								<th width="14.28%" rowspan="2" style="text-align: center;"><spring:message code="label.title.uncertainty" /></th>
								<th width="14.28%" rowspan="2" style="text-align: center;"><spring:message code="label.title.owner" text="Owner" /></th>
								<th width="42.86%" colspan="3" style="text-align: center;"><spring:message code="label.title.ale" /></th>
							</tr>
							<tr>
								<th style="text-align: center;" title='<spring:message code="label.title.alep" />'><spring:message code="label.pessimistic" text='Pessimistic' /></th>
								<th style="text-align: center;" title='<spring:message code="label.title.ale" />'><spring:message code="label.normal" text='Normal' /></th>
								<th style="text-align: center;" title='<spring:message code="label.title.aleo" />'><spring:message code="label.optimistic" text='Optimistic' /></th>
							</tr>
						</c:when>
						<c:otherwise>
							<tr>
								<th width="25%" style="text-align: center;"><spring:message code="label.title.impact" /></th>
								<th width="25%" style="text-align: center;"><spring:message code="label.title.likelihood" /></th>
								<th width="25%" style="text-align: center;"><spring:message code="label.title.owner" text="Owner" /></th>
								<th width="25%" style="text-align: center;"><spring:message code="label.title.ale" /></th>
							</tr>
						</c:otherwise>
					</c:choose>
				</c:otherwise>
			</c:choose>
		</thead>
		<tbody>
			<tr>
				<c:choose>
					<c:when test="${show_cssf}">
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
					</c:when>
					<c:otherwise>
						<td>
							<div class="input-group">
								<span class="input-group-addon">k&euro;</span><input name="impactFin" class="form-control numeric">
							</div>
						</td>
					</c:otherwise>
				</c:choose>
				<td style="border-left: 2px solid #ddd; border-right: 2px solid #ddd;">
					<div class="input-group" align="right">
						<span class="input-group-addon">${probaUnit}</span> <select class="form-control">
							<option value="0 ${probaUnit}"><spring:message code="label.na" text="NA"/></option>
							<c:forEach items="${probabilities}" var="parameter">
								<option  value="${parameter.id}" title="<fmt:formatNumber value="${fct:round(parameter.value,2)}" /> ${probaUnit}"><spring:message text="${parameter.acronym}"/></option>
							</c:forEach>
						</select>
					</div>
				</td>
				<c:choose>
					<c:when test="${show_uncertainty}">
						<td style="border-right: 2px solid #ddd;"><input name="uncertainty" class="form-control numeric"
							value='<fmt:formatNumber value="${assessment.uncertainty}" maxFractionDigits="2" />'></td>
						<td><input name="owner" class="form-control"></td>
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
						<td><input name="owner" class="form-control"></td>
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
<c:if test="${show_cssf}">
	<div class='form-group'>
		<table class='table alert-info'>
			<thead>
				<tr>
					<th colspan="2" style="text-align: center;"><spring:message code='label.inherent.risk' text="Inherent Risk" /></th>
					<th colspan="2" style="text-align: center;"><spring:message code='label.residual.risk' text="Residual Risk" /></th>
				</tr>
				<tr>
					<th width="25%" style="text-align: center;"><spring:message code="label.title.impact" /></th>
					<th width="25%" style="border-right: 2px solid #ddd; text-align: center;"><spring:message code="label.title.likelihood" /></th>
					<th width="25%" style="text-align: center;"><spring:message code="label.title.impact" /></th>
					<th width="25%" style="text-align: center;"><spring:message code="label.title.likelihood" /></th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td><div class="input-group">
							<span class="input-group-addon">k&euro;</span><select class="form-control">
								<option value="0 k&euro;"><spring:message code="label.na" text="NA" /></option>
								<c:forEach items="${impacts}" var="parameter">
									<option value="${parameter.id}" title="<fmt:formatNumber value="${fct:round(parameter.value*0.001,2)}" /> k&euro;"><spring:message
											text="${parameter.acronym}" /></option>
								</c:forEach>
							</select>
						</div></td>
					<td style="border-right: 2px solid #ddd">
						<div class="input-group" align="right">
							<span class="input-group-addon">${probaUnit}</span> <select class="form-control">
								<option value="0 ${probaUnit}"><spring:message code="label.na" text="NA" /></option>
								<c:forEach items="${probabilities}" var="parameter">
									<option value="${parameter.id}" title="<fmt:formatNumber value="${fct:round(parameter.value,2)}" /> ${probaUnit}"><spring:message
											text="${parameter.acronym}" /></option>
								</c:forEach>
							</select>
						</div>
					</td>
					<td><div class="input-group">
							<span class="input-group-addon">k&euro;</span><select class="form-control">
								<option value="0 k&euro;"><spring:message code="label.na" text="NA" /></option>
								<c:forEach items="${impacts}" var="parameter">
									<option value="${parameter.id}" title="<fmt:formatNumber value="${fct:round(parameter.value*0.001,2)}" /> k&euro;"><spring:message
											text="${parameter.acronym}" /></option>
								</c:forEach>
							</select>
						</div></td>
					<td>
						<div class="input-group" align="right">
							<span class="input-group-addon">/y</span> <select class="form-control">
								<option value="0 ${probaUnit}"><spring:message code="label.na" text="NA" /></option>
								<c:forEach items="${probabilities}" var="parameter">
									<option value="${parameter.id}" title="<fmt:formatNumber value="${fct:round(parameter.value,2)}" /> ${probaUnit}"><spring:message
											text="${parameter.acronym}" /></option>
								</c:forEach>
							</select>
						</div>
					</td>
				</tr>
			</tbody>
		</table>
	</div>
</c:if>
<div class='form-group'>
	<fmt:message key="label.comment" var='comment' />
	<spring:message text="${assessment.comment}" var="commentContent" />
	<label class='label-control'>${comment}</label>
	<textarea rows="${rowLength}" class="form-control" name="comment" title="${comment}" style="resize: vertical;" placeholder="${commentContent}" data-trick-type='string'>${commentContent}</textarea>
</div>
<div class='form-group'>
	<fmt:message key="label.assessment.hidden_comment" var='hiddenComment' />
	<spring:message text="${assessment.hiddenComment}" var="hiddenCommentContent" />
	<label class='label-control'>${hiddenComment}</label>
	<textarea rows="${rowLength}" class="form-control" name="comment" title="${hiddenComment}" style="resize: vertical;" placeholder="${hiddenCommentContent}" data-trick-type='string'>${hiddenCommentContent}</textarea>
</div>