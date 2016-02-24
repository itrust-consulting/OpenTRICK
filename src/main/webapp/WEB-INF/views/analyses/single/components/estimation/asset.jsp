<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fct" uri="http://trickservice.itrust.lu/JSTLFunctions"%>
<c:choose>
	<c:when test="${not empty assessments}">
		<div id="estimation-ui" class='col-lg-10 trick-ui' data-trick-id='-1' data-trick-content='asset'>
			<fieldset style="display: block; width: 100%; clear: left;">
				<legend>
					<spring:message text='${asset.name}' />
				</legend>
				<div id="description" class='well well-sm' style="word-wrap: break-word; white-space: pre-wrap; resize: vertical; overflow: auto; height: 110px;"
				><spring:message text="${fn:trim(asset.comment)}" /><hr style="border-color: #ddd;"><spring:message text="${fn:trim(asset.hiddenComment)}" /></div>
			</fieldset>
			<table class="table table-hover table-fixed-header-analysis">
				<thead>
					<tr>
						<th style="width: 10%" title='<fmt:message key="label.assessment.scenario" />'><fmt:message key="label.assessment.scenario" /></th>
						<c:if test="${show_cssf}">
							<th style="width: 2%" title='<fmt:message key="label.title.assessment.impact_rep" />'><fmt:message key="label.assessment.impact_rep" /></th>
							<th style="width: 2%" title='<fmt:message key="label.title.assessment.impact_op" />'><fmt:message key="label.assessment.impact_op" /></th>
							<th style="width: 2%" title='<fmt:message key="label.title.assessment.impact_leg" />'><fmt:message key="label.assessment.impact_leg" /></th>
							<th style="width: 2%" title='<fmt:message key="label.title.assessment.impact_fin" />'><fmt:message key="label.assessment.impact_fin" /></th>
						</c:if>
						<c:if test="${!show_cssf}">
							<th style="width: 2%" title='<fmt:message key="label.title.impact" />'><fmt:message key="label.assessment.impact" /></th>
						</c:if>
						<th style="width: 2%" title='<fmt:message key="label.title.likelihood" />'><fmt:message key="label.assessment.likelihood" /></th>
						<c:choose>
							<c:when test="${show_uncertainty}">
								<th style="width: 2%" title='<fmt:message key="label.title.uncertainty" />'><fmt:message key="label.assessment.uncertainty" /></th>
								<th style="width: 2%" title='<fmt:message key="label.title.alep" />'><fmt:message key="label.assessment.alep" /></th>
								<th style="width: 2%" title='<fmt:message key="label.title.ale" />'><fmt:message key="label.assessment.ale" /></th>
								<th style="width: 2%" title='<fmt:message key="label.title.aleo" />'><fmt:message key="label.assessment.aleo" /></th>
							</c:when>
							<c:otherwise>
								<th style="width: 2%" title='<fmt:message key="label.title.ale" />'><fmt:message key="label.assessment.ale" /></th>
							</c:otherwise>
						</c:choose>
						<th style="width: 30%" title='<fmt:message key="label.assessment.comment" />'><fmt:message key="label.assessment.comment" /></th>
						<th style="width: 30%" title='<fmt:message key="label.assessment.hidden_comment" />'><fmt:message key="label.assessment.hidden_comment" /></th>
					</tr>
				</thead>
				<tbody>
					<spring:eval expression="T(lu.itrust.business.TS.model.assessment.helper.AssessmentManager).Sort(assessments)" var="sortedAssessments" />
					<c:forEach items="${sortedAssessments}" var="assessment">
						<tr data-trick-id="${assessment.id}">
							<td style="height: 32px;"><spring:message text="${assessment.scenario.name}" /></td>
							<fmt:setLocale value="fr" scope="session" />
							<c:if test="${show_cssf}">
								<c:choose>
									<c:when test="${parameters.containsKey(assessment.impactRep)}">
										<td title='<fmt:formatNumber value="${fct:round(parameters.get(assessment.impactRep),0)}" /> &euro;'><spring:message text="${assessment.impactRep}" /></td>
									</c:when>
									<c:otherwise>
										<td title='<fmt:formatNumber value="${fct:round(assessment.impactRep,0)}" /> &euro;'><c:catch>
												<fmt:formatNumber value="${fct:round(assessment.impactRep*0.001,0)}" var="impactRep" />
											</c:catch> <c:choose>
												<c:when test="${!empty impactRep}">
													<spring:message text="${impactRep}" />
												</c:when>
												<c:otherwise>
													<spring:message text="${assessment.impactRep}" />
												</c:otherwise>
											</c:choose></td>
									</c:otherwise>
								</c:choose>
								<c:choose>
									<c:when test="${parameters.containsKey(assessment.impactOp)}">
										<td title='<fmt:formatNumber value="${fct:round(parameters.get(assessment.impactOp),0)}" /> &euro;'><spring:message text="${assessment.impactOp}" /></td>
									</c:when>
									<c:otherwise>
										<td title='<fmt:formatNumber value="${fct:round(assessment.impactOp,0)}" /> &euro;'><c:catch>
												<fmt:formatNumber value="${fct:round(assessment.impactOp*0.001,0)}" var="impactOp" />
											</c:catch> <c:choose>
												<c:when test="${!empty impactOp}">
													<spring:message text="${impactOp}" />
												</c:when>
												<c:otherwise>
													<spring:message text="${assessment.impactOp}" />
												</c:otherwise>
											</c:choose></td>
									</c:otherwise>
								</c:choose>
								<c:choose>
									<c:when test="${parameters.containsKey(assessment.impactLeg)}">
										<td title='<fmt:formatNumber value="${fct:round(parameters.get(assessment.impactLeg),0)}" /> &euro;'><spring:message text="${assessment.impactLeg}" /></td>
									</c:when>
									<c:otherwise>
										<td title='<fmt:formatNumber value="${fct:round(assessment.impactLeg,0)}" /> &euro;'><c:catch>
												<fmt:formatNumber value="${fct:round(assessment.impactLeg*0.001,0)}" var="impactLeg" />
											</c:catch> <c:choose>
												<c:when test="${!empty impactLeg}">
													<spring:message text="${impactLeg}" />
												</c:when>
												<c:otherwise>
													<spring:message text="${assessment.impactLeg}" />
												</c:otherwise>
											</c:choose></td>
									</c:otherwise>
								</c:choose>
							</c:if>
							<c:choose>
								<c:when test="${parameters.containsKey(assessment.impactFin)}">
									<td title='<fmt:formatNumber value="${fct:round(parameters.get(assessment.impactFin),0)}" /> &euro;'><spring:message text="${assessment.impactFin}" /></td>
								</c:when>
								<c:otherwise>
									<td title='<fmt:formatNumber value="${fct:round(assessment.impactFin,0)}" /> &euro;'><c:catch>
											<fmt:formatNumber value="${fct:round(assessment.impactFin*0.001,0)}" var="impactFin" />
										</c:catch> <c:choose>
											<c:when test="${not empty impactFin}">
												<spring:message text="${impactFin}" />
											</c:when>
											<c:otherwise>
												<spring:message text="${assessment.impactFin}" />
											</c:otherwise>
										</c:choose></td>
								</c:otherwise>
							</c:choose>
							<c:choose>
								<c:when test="${parameters.containsKey(assessment.likelihood)}">
									<td title='<fmt:formatNumber value="${fct:round(parameters.get(assessment.likelihood),2)}" maxFractionDigits="2" /> <fmt:message key="label.assessment.likelihood.unit" />'><spring:message
											text="${assessment.likelihood}" /></td>
								</c:when>
								<c:otherwise>
									<td><c:catch>
											<fmt:formatNumber value="${fct:round(assessment.likelihood,2)}" maxFractionDigits="2" var="likelihood" />
										</c:catch> <c:choose>
											<c:when test="${not empty likelihood }">
												<spring:message text="${likelihood}" />
											</c:when>
											<c:otherwise>
												<spring:message text="${assessment.likelihood}" />
											</c:otherwise>
										</c:choose></td>
								</c:otherwise>
							</c:choose>
							<c:if test="${show_uncertainty}">
								<td><fmt:formatNumber value="${assessment.uncertainty}" maxFractionDigits="2" /></td>
								<td title="<fmt:formatNumber value="${assessment.ALEP}" maxFractionDigits="2" /> &euro;"><fmt:formatNumber value="${fct:round(assessment.ALEP*0.001,1)}" /></td>
							</c:if>
							<td title="<fmt:formatNumber value="${assessment.ALE}" maxFractionDigits="2" /> &euro;"><fmt:formatNumber value="${fct:round(assessment.ALE*0.001,1)}" /></td>
							<c:if test="${show_uncertainty}">
								<td title="<fmt:formatNumber value="${assessment.ALEO}" maxFractionDigits="2" /> &euro;"><fmt:formatNumber value="${fct:round(assessment.ALEO*0.001,1)}" /></td>
							</c:if>
							<fmt:setLocale value="${language}" scope="session" />
							<td><pre><spring:message text="${assessment.comment}" /></pre></td>
							<td><pre><spring:message text="${assessment.hiddenComment}" /></pre></td>
						</tr>
					</c:forEach>
					<fmt:setLocale value="${language}" scope="session" />
					<tr class="panel-footer" style="font-weight: bold;">
						<c:choose>
							<c:when test="${show_uncertainty}">
								<c:choose>
									<c:when test="${show_cssf}">
										<td colspan="7"><fmt:message key="label.total.ale" /></td>
									</c:when>
									<c:otherwise>
										<td colspan="4"><fmt:message key="label.total.ale" /></td>
									</c:otherwise>
								</c:choose>
								<fmt:setLocale value="fr" scope="session" />
								<td title="<fmt:formatNumber value="${alep.value}" maxFractionDigits="2" /> &euro;"><fmt:formatNumber value="${fct:round(alep.value*0.001,1)}" /></td>
								<td title="<fmt:formatNumber value="${ale.value}" maxFractionDigits="2" /> &euro;"><fmt:formatNumber value="${fct:round(ale.value*0.001,1)}" /></td>
								<td title="<fmt:formatNumber value="${aleo.value}" maxFractionDigits="2" /> &euro;"><fmt:formatNumber value="${fct:round(aleo.value*0.001,1)}" /></td>
							</c:when>
							<c:otherwise>
								<c:choose>
									<c:when test="${show_cssf}">
										<td colspan="6"><fmt:message key="label.total.ale" /></td>
									</c:when>
									<c:otherwise>
										<td colspan="3"><fmt:message key="label.total.ale" /></td>
									</c:otherwise>
								</c:choose>
								<fmt:setLocale value="fr" scope="session" />
								<td title="<fmt:formatNumber value="${ale.value}" maxFractionDigits="2" /> &euro;"><fmt:formatNumber value="${fct:round(ale.value*0.001,1)}" /></td>
							</c:otherwise>
						</c:choose>
						<td colspan="2">&nbsp;</td>
					</tr>
				</tbody>
			</table>
		</div>
	</c:when>
	<c:when test="${not empty assessment}">
		<div id="estimation-ui" class='col-lg-10 trick-ui' data-trick-id='${scenario.id}' data-trick-content='asset'>
			<fieldset style="display: block; width: 100%; clear: left;">
				<legend>
					<spring:message text='${scenario.name}' />
				</legend>
				<div id="description" class='well well-sm' style="word-wrap: break-word; white-space: pre-wrap; resize: vertical; overflow: auto; height: 40px;"><spring:message text="${fn:trim(scenario.description)}" /></div>
			</fieldset>
			<div class="form-group">
				<table class='table'>
					<thead>
						<c:choose>
							<c:when test="${show_cssf}">
								<c:choose>
									<c:when test="${show_uncertainty}">
										<tr>
											<th width="44.44%" colspan="4" style="text-align: center;"><spring:message code="label.title.impact" /></th>
											<th width="11.11%" rowspan="2" style="text-align: center;"><spring:message code="label.title.likelihood" /></th>
											<th width="11.11%" rowspan="2" style="text-align: center;"><spring:message code="label.title.uncertainty" /></th>
											<th width="22.22%" colspan="3" style="text-align: center;"><spring:message code="label.title.ale" /></th>
										</tr>
										<tr>
											<th title='<spring:message code="label.title.assessment.impact_rep" />' style="text-align: center;"><spring:message code="label.impact_rep" text="Reputation" /></th>
											<th title='<spring:message code="label.title.assessment.impact_op" />' style="text-align: center;"><spring:message code="label.impact_op" text="Operational" /></th>
											<th title='<spring:message code="label.title.assessment.impact_leg" />' style="text-align: center;"><spring:message code="label.impact_leg" text="Legal" /></th>
											<th title='<spring:message code="label.title.assessment.impact_fin" />' style="text-align: center;"><spring:message code="label.impact_fin" text="Financial" /></th>
											<th title='<spring:message code="label.title.alep" />' style="text-align: center;"><spring:message code="label.pessimistic" text='Pessimistic'/></th>
											<th title='<spring:message code="label.title.ale" />' style="text-align: center;"><spring:message code="label.normal" text='Normal'/></th>
											<th title='<spring:message code="label.title.aleo" />' style="text-align: center;"><spring:message code="label.optimistic" text='Optimistic'/></th>
										</tr>
									</c:when>
									<c:otherwise>
										<tr>
											<th width="66.66%" colspan="4" style="text-align: center;"><spring:message code="label.title.impact" /></th>
											<th width="16.66%" rowspan="2" style="text-align: center;"><spring:message code="label.title.likelihood" /></th>
											<th width="16.66%" rowspan="2" style="text-align: center;"><spring:message code="label.title.ale" /></th>
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
											<th width="16.66%" rowspan="2" style="text-align: center;"><spring:message code="label.title.impact" /></th>
											<th width="16.66%" rowspan="2" style="text-align: center;"><spring:message code="label.title.likelihood" /></th>
											<th width="16.66%" rowspan="2" style="text-align: center;"><spring:message code="label.title.uncertainty" /></th>
											<th width="50%" colspan="3" style="text-align: center;"><spring:message code="label.title.ale" /></th>
										</tr>
										<tr>
											<th style="text-align: center;" title='<spring:message code="label.title.alep" />'><spring:message code="label.pessimistic" text='Pessimistic'/></th>
											<th style="text-align: center;" title='<spring:message code="label.title.ale" />'><spring:message code="label.normal" text='Normal'/></th>
											<th style="text-align: center;" title='<spring:message code="label.title.aleo" />'><spring:message code="label.optimistic" text='Optimistic'/></th>
										</tr>
									</c:when>
									<c:otherwise>
										<tr>
											<th width="33.33%" style="text-align: center;"><spring:message code="label.title.impact" /></th>
											<th width="33.33%" style="text-align: center;"><spring:message code="label.title.likelihood" /></th>
											<th width="33.33%" style="text-align: center;"><spring:message code="label.title.ale" /></th>
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
									<span class="input-group-addon">/y</span> <select class="form-control">
										<option>Test 1</option>
										<option>Test 2</option>
										<option>Test 3</option>
										<option>Test 4</option>
									</select>
								</div>
							</td>
							<c:choose>
								<c:when test="${show_uncertainty}">
									<td style="border-right: 2px solid #ddd;"><input name="uncertainty" class="form-control numeric"
										value='<fmt:formatNumber value="${assessment.uncertainty}" maxFractionDigits="2" />'></td>
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
					<fmt:message key="label.comment" var='comment' />
					<spring:message text="${assessment.comment}" var="commentContent" />
					<label class='label-control'>${comment}</label>
					<textarea rows="12" class="form-control" name="comment" title="${comment}" style="resize: vertical;" placeholder="${commentContent}" data-trick-type='string'>${commentContent}</textarea>
				</div>
				<div class='form-group'>
					<fmt:message key="label.assessment.hidden_comment" var='hiddenComment' />
					<spring:message text="${assessment.hiddenComment}" var="hiddenCommentContent" />
					<label class='label-control'>${hiddenComment}</label>
					<textarea rows="12" class="form-control" name="comment" title="${hiddenComment}" style="resize: vertical;" placeholder="${hiddenCommentContent}" data-trick-type='string'>${hiddenCommentContent}</textarea>
				</div>
			</div>
		</div>
	</c:when>
	<c:otherwise>
		<div id="estimation-ui" class='col-lg-10' data-trick-id='-2' data-trick-content='asset'></div>
	</c:otherwise>
</c:choose>
