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
	<select name="riskProfile.riskStrategy" class="form-control">
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
					<span class="input-group-addon" style="padding: 1px;"><button class="btn btn-default" style="padding: 3px" name='probaScale'>${probaUnit}</button></span>
					<c:choose>
						<c:when test="${empty riskProfile.rawProbaImpact.probability }">
							<select class="form-control" name="riskProfile.rawProbaImpact.probability" data-trick-value='0' data-trick-type='integer'>
								<c:forEach items="${probabilities}" var="parameter">
									<option value="${parameter.id}" title="<fmt:formatNumber value="${fct:round(parameter.value,2)}" /> ${probaUnit}"><spring:message text="${parameter.acronym}" /></option>
								</c:forEach>
							</select>
						</c:when>
						<c:otherwise>
							<select class="form-control" name="riskProfile.rawProbaImpact.probability" data-trick-value='${riskProfile.rawProbaImpact.probability.id}' data-trick-type='integer'>
								<c:forEach items="${probabilities}" var="parameter">
									<option value="${parameter.id}" ${riskProfile.rawProbaImpact.probability==parameter?"selected='selected'" :"" }
										title="<fmt:formatNumber value="${fct:round(parameter.value,2)}" /> ${probaUnit}"><spring:message text="${parameter.acronym}" /></option>
								</c:forEach>
							</select>
						</c:otherwise>
					</c:choose>
				</div>
			</td>
			<td>
				<div class="input-group">
					<span class="input-group-addon" style="padding: 1px;"><button class="btn btn-default" style="padding: 3px" name='impactScale'>k&euro;</button></span>
					<c:choose>
						<c:when test="${empty riskProfile.rawProbaImpact.impactRep}">
							<select class="form-control" name="riskProfile.rawProbaImpact.impactRep" data-trick-value='0' data-trick-type='integer'>
								<c:forEach items="${impacts}" var="parameter">
									<option value="${parameter.id}" title="<fmt:formatNumber value="${fct:round(parameter.value*0.001,2)}" /> k&euro;"><spring:message text="${parameter.acronym}" /></option>
								</c:forEach>
							</select>
						</c:when>
						<c:otherwise>
							<select class="form-control" name="riskProfile.rawProbaImpact.impactRep" data-trick-value='${riskProfile.rawProbaImpact.impactRep.id}' data-trick-type='integer'>
								<c:forEach items="${impacts}" var="parameter">
									<option value="${parameter.id}" ${riskProfile.rawProbaImpact.impactRep==parameter?"selected='selected'" : ""}
										title="<fmt:formatNumber value="${fct:round(parameter.value*0.001,2)}" /> k&euro;"><spring:message text="${parameter.acronym}" /></option>
								</c:forEach>
							</select>
						</c:otherwise>
					</c:choose>
				</div>
			</td>
			<td>
				<div class="input-group">
					<span class="input-group-addon" style="padding: 1px;"><button class="btn btn-default" style="padding: 3px" name="impactScale">k&euro;</button></span>
					<c:choose>
						<c:when test="${empty riskProfile.rawProbaImpact.impactOp}">
							<select class="form-control" name="riskProfile.rawProbaImpact.impactOp" data-trick-value='0' data-trick-type='integer'>
								<c:forEach items="${impacts}" var="parameter">
									<option value="${parameter.id}" title="<fmt:formatNumber value="${fct:round(parameter.value*0.001,2)}" /> k&euro;"><spring:message text="${parameter.acronym}" /></option>
								</c:forEach>
							</select>
						</c:when>
						<c:otherwise>
							<select class="form-control" name="riskProfile.rawProbaImpact.impactOp" data-trick-value='${riskProfile.rawProbaImpact.impactOp.id}' data-trick-type='integer'>
								<c:forEach items="${impacts}" var="parameter">
									<option value="${parameter.id}" ${riskProfile.rawProbaImpact.impactOp==parameter?"selected='selected'" : "" }
										title="<fmt:formatNumber value="${fct:round(parameter.value*0.001,2)}" /> k&euro;"><spring:message text="${parameter.acronym}" /></option>
								</c:forEach>
							</select>
						</c:otherwise>
					</c:choose>
				</div>
			</td>
			<td>
				<div class="input-group">
					<span class="input-group-addon" style="padding: 1px;"><button class="btn btn-default" style="padding: 3px" name='impactScale'>k&euro;</button></span>
					<c:choose>
						<c:when test="${empty riskProfile.rawProbaImpact.impactLeg}">
							<select class="form-control" name="riskProfile.rawProbaImpact.impactLeg" data-trick-value='0' data-trick-type='integer'>
								<c:forEach items="${impacts}" var="parameter">
									<option value="${parameter.id}" title="<fmt:formatNumber value="${fct:round(parameter.value*0.001,2)}" /> k&euro;"><spring:message text="${parameter.acronym}" /></option>
								</c:forEach>
							</select>
						</c:when>
						<c:otherwise>
							<select class="form-control" name="riskProfile.rawProbaImpact.impactLeg" data-trick-value='${riskProfile.rawProbaImpact.impactLeg.id}' data-trick-type='integer'>
								<c:forEach items="${impacts}" var="parameter">
									<option value="${parameter.id}" ${riskProfile.rawProbaImpact.impactLeg==parameter?"selected='selected'" : "" }
										title="<fmt:formatNumber value="${fct:round(parameter.value*0.001,2)}" /> k&euro;"><spring:message text="${parameter.acronym}" /></option>
								</c:forEach>
							</select>
						</c:otherwise>
					</c:choose>
				</div>
			</td>
			<td>
				<div class="input-group">
					<span class="input-group-addon" style="padding: 1px;"><button class="btn btn-default" style="padding: 3px" name='impactScale'>k&euro;</button></span>
					<c:choose>
						<c:when test="${empty riskProfile.rawProbaImpact.impactFin}">
							<select class="form-control" name="riskProfile.rawProbaImpact.impactFin" data-trick-value='0' data-trick-type='integer'>
								<c:forEach items="${impacts}" var="parameter">
									<option value="${parameter.id}" title="<fmt:formatNumber value="${fct:round(parameter.value*0.001,2)}" /> k&euro;"><spring:message text="${parameter.acronym}" /></option>
								</c:forEach>
							</select>
						</c:when>
						<c:otherwise>
							<select class="form-control" name="riskProfile.rawProbaImpact.impactFin" data-trick-value='${riskProfile.rawProbaImpact.impactFin.id}' data-trick-type='integer'>
								<c:forEach items="${impacts}" var="parameter">
									<option value="${parameter.id}" ${riskProfile.rawProbaImpact.impactFin==parameter?"selected='selected'" : "" }
										title="<fmt:formatNumber value="${fct:round(parameter.value*0.001,2)}" /> k&euro;"><spring:message text="${parameter.acronym}" /></option>
								</c:forEach>
							</select>
						</c:otherwise>
					</c:choose>
				</div>
			</td>
			<td style="border-left: 2px solid window;"><input name="rawComputedImportance" disabled="disabled" class="form-control numeric" value="${riskProfile.computedRawImportance}"></td>
			<c:choose>
				<c:when test="${empty rawModelling }">
					<td style="border-left: 2px solid window;"><input name="rawProbability" disabled="disabled" class="form-control numeric"></td>
					<td><input name="rawImpact" disabled="disabled" class="form-control numeric"></td>
					<td><input name="rawImportance" disabled="disabled" class="form-control numeric"></td>
				</c:when>
				<c:otherwise>
					<td style="border-left: 2px solid window;"><input name="rawProbability" disabled="disabled" class="form-control numeric"
						value='<fmt:formatNumber value="${rawModelling.probability}" maxFractionDigits="0"/>'></td>
					<td><input name="rawImpact" disabled="disabled" class="form-control numeric" value='<fmt:formatNumber value="${rawModelling.impact}" maxFractionDigits="0" />'></td>
					<td><input name="rawImportance" disabled="disabled" class="form-control numeric" value='<fmt:formatNumber value="${rawModelling.importance}" maxFractionDigits="0" />'></td>
				</c:otherwise>
			</c:choose>
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
					<spring:message text="${assessment.likelihood}" var="likelihood" />
					<span class="input-group-addon" style="padding: 1px;"><button class="btn btn-default" style="padding: 3px" name='probaScale'>${probaUnit}</button></span> <select
						class="form-control" name="likelihood" data-trick-type='string' data-trick-value='${likelihood}'>
						<option value="NA" title='0 ${probaUnit}'><spring:message code="label.na" text="NA" /></option>
						<c:forEach items="${probabilities}" var="parameter">
							<option value="${parameter.acronym}" ${likelihood == parameter.acronym? "selected='selected'" : ""}
								title="<fmt:formatNumber value="${fct:round(parameter.value,2)}" />${probaUnit}">${parameter.acronym}</option>
						</c:forEach>
					</select>
				</div>
			</td>
			<td>
				<div class="input-group">
					<span class="input-group-addon" style="padding: 1px;"><button class="btn btn-default" style="padding: 3px" name="impactScale">k&euro;</button></span>
					<c:catch>
						<fmt:formatNumber value="${fct:round(assessment.impactRep*0.001,2)}" var="impactRep" />
					</c:catch>
					<c:if test="${empty impactRep}">
						<spring:message text="${assessment.impactRep}" var="impactRep" />
					</c:if>
					<input name="impactRep" class="form-control" value="${impactRep}" data-trick-type='string' placeholder="${impactRep}" list="impactList">
				</div>
			</td>
			<td>
				<div class="input-group">
					<span class="input-group-addon" style="padding: 1px;"><button class="btn btn-default" style="padding: 3px" name="impactScale">k&euro;</button></span>
					<c:catch>
						<fmt:formatNumber value="${fct:round(assessment.impactOp*0.001,2)}" var="impactOp" />
					</c:catch>
					<c:if test="${empty impactOp}">
						<spring:message text="${assessment.impactOp}" var="impactOp" />
					</c:if>
					<input name="impactOp" class="form-control" value="${impactOp}" data-trick-type='string' placeholder="${impactOp}" list="impactList">
				</div>
			</td>
			<td>
				<div class="input-group">
					<span class="input-group-addon" style="padding: 1px;"><button class="btn btn-default" style="padding: 3px" name="impactScale">k&euro;</button></span>
					<c:catch>
						<fmt:formatNumber value="${fct:round(assessment.impactLeg*0.001,2)}" var="impactLeg" />
					</c:catch>
					<c:if test="${empty impactLeg}">
						<spring:message text="${assessment.impactLeg}" var="impactLeg" />
					</c:if>
					<input name="impactLeg" class="form-control" value="${impactLeg}" data-trick-type='string' placeholder="${impactLeg}" list="impactList">
				</div>
			</td>
			<td>
				<div class="input-group">
					<span class="input-group-addon" style="padding: 1px;"><button class="btn btn-default" style="padding: 3px" name="impactScale">k&euro;</button></span>
					<c:catch>
						<fmt:formatNumber value="${fct:round(assessment.impactFin*0.001,2)}" var="impactFin" />
					</c:catch>
					<c:if test="${empty impactFin}">
						<spring:message text="${assessment.impactFin}" var="impactFin" />
					</c:if>
					<input name="impactFin" class="form-control" value="${impactFin}" data-trick-type='string' placeholder="${impactFin}" list="impactList">
				</div>
			</td>
			<c:choose>
				<c:when test="${empty netModelling }">
					<td style="border-left: 2px solid window;"><input name="netProbability" disabled="disabled" class="form-control numeric"></td>
					<td><input name="netImpact" disabled="disabled" class="form-control numeric"></td>
					<td><input name="netImportance" disabled="disabled" class="form-control numeric"></td>
				</c:when>
				<c:otherwise>
					<td style="border-left: 2px solid window;"><input name="netProbability" disabled="disabled" class="form-control numeric"
						value='<fmt:formatNumber value="${netModelling.probability}" maxFractionDigits="0"/>'></td>
					<td><input name="netImpact" disabled="disabled" class="form-control numeric" value='<fmt:formatNumber value="${netModelling.impact}" maxFractionDigits="0" />'></td>
					<td><input name="netImportance" disabled="disabled" class="form-control numeric" value='<fmt:formatNumber value="${netModelling.importance}" maxFractionDigits="0" />'></td>
				</c:otherwise>
			</c:choose>
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
					<span class="input-group-addon" style="padding: 1px;"><button class="btn btn-default" style="padding: 3px" name="probaScale">${probaUnit}</button></span>
					<c:choose>
						<c:when test="${empty riskProfile.expProbaImpact.probability}">
							<select class="form-control" name="riskProfile.expProbaImpact.probability" data-trick-value='0' data-trick-type='integer'>
								<c:forEach items="${probabilities}" var="parameter">
									<option value="${parameter.id}" title="<fmt:formatNumber value="${fct:round(parameter.value,2)}" /> ${probaUnit}"><spring:message text="${parameter.acronym}" /></option>
								</c:forEach>
							</select>
						</c:when>
						<c:otherwise>
							<select class="form-control" name="riskProfile.expProbaImpact.probability" data-trick-value='${riskProfile.expProbaImpact.probability.id}' data-trick-type='integer'>
								<c:forEach items="${probabilities}" var="parameter">
									<option value="${parameter.id}" ${riskProfile.expProbaImpact.probability==parameter?"selected='selected'":""}
										title="<fmt:formatNumber value="${fct:round(parameter.value,2)}" /> ${probaUnit}"><spring:message text="${parameter.acronym}" /></option>
								</c:forEach>
							</select>
						</c:otherwise>
					</c:choose>
				</div>
			</td>
			<td>
				<div class="input-group">
					<span class="input-group-addon" style="padding: 1px;"><button class="btn btn-default" style="padding: 3px" name="impactScale">k&euro;</button></span>
					<c:choose>
						<c:when test="${empty riskProfile.expProbaImpact.impactRep}">
							<select class="form-control" name="riskProfile.expProbaImpact.impactRep" data-trick-value='0' data-trick-type='integer'>
								<c:forEach items="${impacts}" var="parameter">
									<option value="${parameter.id}" title="<fmt:formatNumber value="${fct:round(parameter.value*0.001,2)}" /> k&euro;"><spring:message text="${parameter.acronym}" /></option>
								</c:forEach>
							</select>
						</c:when>
						<c:otherwise>
							<select class="form-control" name="riskProfile.expProbaImpact.impactRep" data-trick-value='${riskProfile.expProbaImpact.impactRep.id}' data-trick-type='integer'>
								<c:forEach items="${impacts}" var="parameter">
									<option value="${parameter.id}" ${riskProfile.expProbaImpact.impactRep==parameter?"selected='selected'" :""}
										title="<fmt:formatNumber value="${fct:round(parameter.value*0.001,2)}" /> k&euro;"><spring:message text="${parameter.acronym}" /></option>
								</c:forEach>
							</select>
						</c:otherwise>
					</c:choose>
				</div>
			</td>
			<td>
				<div class="input-group">
					<span class="input-group-addon" style="padding: 1px;"><button class="btn btn-default" style="padding: 3px" name="impactScale">k&euro;</button></span>
					<c:choose>
						<c:when test="${empty riskProfile.expProbaImpact.impactOp}">
							<select class="form-control" name="riskProfile.expProbaImpact.impactOp" data-trick-value='0' data-trick-type='integer'>
								<c:forEach items="${impacts}" var="parameter">
									<option value="${parameter.id}" title="<fmt:formatNumber value="${fct:round(parameter.value*0.001,2)}" /> k&euro;"><spring:message text="${parameter.acronym}" /></option>
								</c:forEach>
							</select>
						</c:when>
						<c:otherwise>
							<select class="form-control" name="riskProfile.expProbaImpact.impactOp" data-trick-value='${riskProfile.expProbaImpact.impactOp.id}' data-trick-type='integer'>
								<c:forEach items="${impacts}" var="parameter">
									<option value="${parameter.id}" ${riskProfile.expProbaImpact.impactOp==parameter?"selected='selected'":"" }
										title="<fmt:formatNumber value="${fct:round(parameter.value*0.001,2)}" /> k&euro;"><spring:message text="${parameter.acronym}" /></option>
								</c:forEach>
							</select>
						</c:otherwise>
					</c:choose>

				</div>
			</td>
			<td>
				<div class="input-group">
					<span class="input-group-addon" style="padding: 1px;"><button class="btn btn-default" style="padding: 3px" name="impactScale">k&euro;</button></span>
					<c:choose>
						<c:when test="${empty riskProfile.expProbaImpact.impactLeg}">
							<select class="form-control" name="riskProfile.expProbaImpact.impactLeg" data-trick-value='0' data-trick-type='integer'>
								<c:forEach items="${impacts}" var="parameter">
									<option value="${parameter.id}" title="<fmt:formatNumber value="${fct:round(parameter.value*0.001,2)}" /> k&euro;"><spring:message text="${parameter.acronym}" /></option>
								</c:forEach>
							</select>
						</c:when>
						<c:otherwise>
							<select class="form-control" name="riskProfile.expProbaImpact.impactLeg" data-trick-value='${riskProfile.expProbaImpact.impactLeg.id}' data-trick-type='integer'>
								<c:forEach items="${impacts}" var="parameter">
									<option value="${parameter.id}" ${riskProfile.expProbaImpact.impactLeg==parameter?"selected='selected'" :""}
										title="<fmt:formatNumber value="${fct:round(parameter.value*0.001,2)}" /> k&euro;"><spring:message text="${parameter.acronym}" /></option>
								</c:forEach>
							</select>
						</c:otherwise>
					</c:choose>

				</div>
			</td>
			<td>
				<div class="input-group">
					<span class="input-group-addon" style="padding: 1px;"><button class="btn btn-default" style="padding: 3px" name="impactScale">k&euro;</button></span>
					<c:choose>
						<c:when test="${empty riskProfile.expProbaImpact.impactFin}">
							<select class="form-control" name="riskProfile.expProbaImpact.impactFin" data-trick-value="0" data-trick-type='integer'>
								<c:forEach items="${impacts}" var="parameter">
									<option value="${parameter.id}" title="<fmt:formatNumber value="${fct:round(parameter.value*0.001,2)}" /> k&euro;"><spring:message text="${parameter.acronym}" /></option>
								</c:forEach>
							</select>
						</c:when>
						<c:otherwise>
							<select class="form-control" name="riskProfile.expProbaImpact.impactFin" data-trick-value="${riskProfile.expProbaImpact.impactFin.id}" data-trick-type='integer'>
								<c:forEach items="${impacts}" var="parameter">
									<option value="${parameter.id}" ${riskProfile.expProbaImpact.impactFin==parameter?"selected='selected'" :""}
										title="<fmt:formatNumber value="${fct:round(parameter.value*0.001,2)}" /> k&euro;"><spring:message text="${parameter.acronym}" /></option>
								</c:forEach>
							</select>
						</c:otherwise>
					</c:choose>
				</div>
			</td>
			<td style="border-left: 2px solid window;"><input name="expComputedImportance" disabled="disabled" class="form-control numeric" value="${riskProfile.computedExpImportance}"></td>
			<c:choose>
				<c:when test="${empty expModelling }">
					<td style="border-left: 2px solid window;"><input name="expProbability" disabled="disabled" class="form-control numeric"></td>
					<td><input name="expImpact" disabled="disabled" class="form-control numeric"></td>
					<td><input name="expImportance" disabled="disabled" class="form-control numeric"></td>
				</c:when>
				<c:otherwise>
					<td style="border-left: 2px solid window;"><input name="expProbability" disabled="disabled" class="form-control numeric"
						value='<fmt:formatNumber value="${expModelling.probability}" maxFractionDigits="0"/>'></td>
					<td><input name="expImpact" disabled="disabled" class="form-control numeric" value='<fmt:formatNumber value="${expModelling.impact}" maxFractionDigits="0" />'></td>
					<td><input name="expImportance" disabled="disabled" class="form-control numeric" value='<fmt:formatNumber value="${expModelling.importance}" maxFractionDigits="0" />'></td>
				</c:otherwise>
			</c:choose>
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
			<spring:message text="${assessment.owner}" var="owner" />
			<c:choose>
				<c:when test="${show_uncertainty}">
					<fmt:formatNumber value="${assessment.uncertainty}" maxFractionDigits="2" var="uncertainty" />
					<td style="border-right: 2px solid #ddd;"><input name="uncertainty" class="form-control numeric" value='${uncertainty}' placeholder="${uncertainty}"
						data-trick-type='double'></td>
					<td>${strategyForm}</td>
					<td style="border-right: 2px solid #ddd;"><input name="owner" class="form-control" value="${owner}" placeholder="${owner}" data-trick-type='string'></td>
					<td>
						<div class="input-group" title="<fmt:formatNumber value="${assessment.ALEP}" maxFractionDigits="2" /> &euro;">
							<span class="input-group-addon">k&euro;</span><input name="ALE" class="form-control numeric" disabled="disabled"
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
					<td><input name="owner" class="form-control" value="${owner}" placeholder="${owner}" data-trick-type='string'></td>
					<td>
						<div class="input-group" title="<fmt:formatNumber value="${assessment.ALE}" maxFractionDigits="2" /> &euro;">
							<span class="input-group-addon">k&euro;</span><input name="ALE" class="form-control numeric" disabled="disabled"
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
	<textarea class="form-control" name="riskProfile.riskTreatment" title="${riskTreatment}" style="resize: vertical;" placeholder="${riskTreatmentContent}" data-trick-type='string'>${riskTreatmentContent}</textarea>
</div>

<div class='form-group'>
	<spring:message code="label.action_paln.including.deadlines" text="Action plan (including deadlines)" var='actionPlan' />
	<label class='label-control'>${actionPlan}</label>
	<spring:message text='${riskProfile.actionPlan}' var="actionPlanContent" />
	<textarea class="form-control" name="riskProfile.actionPlan" title="${actionPlan}" style="resize: vertical;" placeholder="${actionPlanContent}" data-trick-type='string'>${actionPlanContent}</textarea>
</div>

<div class='form-group'>
	<spring:message code="label.assessment.hidden_comment" var='hiddenComment' />
	<spring:message text="${assessment.hiddenComment}" var="hiddenCommentContent" />
	<label class='label-control'>${hiddenComment}</label>
	<textarea class="form-control" name="hiddenComment" title="${hiddenComment}" style="resize: vertical;" placeholder="${hiddenCommentContent}" data-trick-type='string'>${hiddenCommentContent}</textarea>
</div>

<datalist id="impactList">
	<c:forEach items="${impacts}" var="parameter">
		<option value='<spring:message text="${parameter.acronym}"/>' title="<fmt:formatNumber value="${fct:round(parameter.value*0.001,2)}" /> k&euro;"><spring:message
				text="${parameter.acronym}" /></option>
	</c:forEach>
</datalist>