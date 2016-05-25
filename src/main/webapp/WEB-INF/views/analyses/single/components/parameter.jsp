<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<spring:eval expression="T(lu.itrust.business.TS.model.analysis.Analysis).SplitParameters(parameters)" var="parametersSplited" />
<spring:eval expression="T(lu.itrust.business.TS.model.analysis.Analysis).SplitSimpleParameters(parametersSplited[0])" var="simpleParameters" />
<spring:eval expression="T(lu.itrust.business.TS.model.analysis.Analysis).SplitExtendedParameters(parametersSplited[1])" var="extendedParameters" />
<%-- <spring:eval expression="T(lu.itrust.business.TS.model.analysis.Analysis).SplitMaturityParameters(parametersSplited[2])" var="maturityParameters" /> --%>
<fmt:setLocale value="fr" scope="session" />
<div class="row tab-pane" id="tabParameterImpactProba">
	<div class="col-md-6">
		<div class="panel panel-default" id="Scale_Impact">
			<div class="panel-heading">
				<spring:message code="label.title.parameter.extended.impact" />
			</div>
			<div class="panel-body">
				<table class="table table-hover table-condensed">
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
						<c:forEach items="${extendedParameters[0]}" var="parameter" varStatus="status">
							<tr data-trick-class="ExtendedParameter" data-trick-id="${parameter.id}">
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
										<c:when test="${status.index!=10}">
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
			</div>
		</div>
	</div>
	<div class="col-md-6">
		<div class="panel panel-default" id="Scale_Probability">
			<div class="panel-heading">
				<spring:message code="label.parameter.extended.probability" />
			</div>
			<div class="panel-body">
				<table class="table table-hover table-condensed">
					<thead>
						<tr>
							<th class="textaligncenter"><spring:message code="label.parameter.level" /></th>
							<th class="textaligncenter"><spring:message code="label.parameter.acronym" /></th>
							<th class="textaligncenter"><spring:message code="label.parameter.qualification" /></th>
							<th class="textaligncenter"><spring:message code="label.parameter.value" /> <spring:message code="label.assessment.likelihood.unit" /></th>
							<th class="textaligncenter"><spring:message code="label.parameter.range.min" /></th>
							<th class="textaligncenter"><spring:message code="label.parameter.range.max" /></th>
						</tr>
					</thead>
					<tbody>
						<tr data-trick-class="ExtendedParameter" hidden="true">
							<td data-trick-field="acronym" colspan="3"><spring:message text="NA" /></td>
							<td data-trick-field="value" colspan="3">0</td>
						</tr>
						<c:forEach items="${extendedParameters[1]}" var="parameter" varStatus="status">
							<tr data-trick-class="ExtendedParameter" data-trick-id="${parameter.id}">
								<!--<td>${itemInformation.id}</td>-->
								<td class="textaligncenter"><spring:message text="${parameter.level}" /></td>
								<td data-trick-field="acronym" data-trick-field-type="string" class="success textaligncenter" onclick="return editField(this);"><spring:message
										text="${parameter.acronym}" /></td>
								<td data-trick-field="description" data-trick-field-type="string" class="success textaligncenter" onclick="return editField(this);"><spring:message
										text="${parameter.description}" /></td>
								<c:set var="parameterValue">
									<fmt:formatNumber value="${parameter.value}" />
								</c:set>
								<td data-trick-field="value" data-trick-field-type="double"
									${(parameter.level mod 2)==0? 'onclick="return editField(this);" class="success textaligncenter"': 'class="textaligncenter"'} title="${parameterValue}"
									data-real-value="${parameterValue}"><fmt:formatNumber value="${parameter.value}" maxFractionDigits="2" minFractionDigits="2" /></td>
								<td class="textaligncenter"><fmt:formatNumber value="${parameter.bounds.from}" maxFractionDigits="2" minFractionDigits="2" /></td>
								<td class="textaligncenter"><c:choose>
										<c:when test="${status.index!=10}">
											<fmt:formatNumber value="${parameter.bounds.to}" maxFractionDigits="2" minFractionDigits="2" />
										</c:when>
										<c:otherwise>
											<span style="font-size: 17px;">+&#8734;</span>
										</c:otherwise>
									</c:choose></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</div>
	<div class="col-md-6">
		<span id="anchorParameter_Dynamic" class="anchor"></span>
		<div class="panel panel-default" id="DynamicParameters">
			<div class="panel-heading">
				<spring:message code="label.parameter.dynamic.probability" />
			</div>
			<div class="panel-body">
				<table class="table table-hover">
					<thead>
						<tr>
							<th class="textaligncenter"><spring:message code="label.parameter.acronym" /></th>
							<th class="textaligncenter"><spring:message code="label.parameter.value" /> <spring:message code="label.assessment.likelihood.unit" /></th>
						</tr>
					</thead>
					<tbody>
						<fmt:setLocale value="fr" scope="session" />
						<c:forEach items="${simpleParameters[4]}" var="parameter" varStatus="status">
							<tr data-trick-class="ExtendedParameter" data-trick-id="${parameter.id}">
								<!--<td>${itemInformation.id}</td>-->
								<td data-trick-field="acronym" data-trick-field-type="string" class="textaligncenter"><spring:message text="${parameter.acronym}" /></td>
								<td class="textaligncenter"><fmt:formatNumber value="${parameter.value}" maxFractionDigits="4" minFractionDigits="4" /></td>
							</tr>
						</c:forEach>
						<fmt:setLocale value="${language}" scope="session" />
					</tbody>
				</table>
			</div>
		</div>
	</div>
</div>
<div class="row tab-pane" id="tabParameterOther">
	<div class="col-md-6">
		<div class="panel panel-default">
			<div class="panel-heading">
				<spring:message code="label.title.parameter.maturity_ilps" />
			</div>
			<div class="panel-body">
				<table class="table table-hover table-fixed-header-analysis table-condensed" id="tableMaturityIlps">
					<thead>
						<tr>
							<th class="textaligncenter"><spring:message code="label.parameter.maturity.category" /></th>
							<th class="textaligncenter"><spring:message code="label.parameter.maturity.task" /></th>
							<th class="textaligncenter"><spring:message code="label.parameter.maturity.sml0" /> (%)</th>
							<th class="textaligncenter"><spring:message code="label.parameter.maturity.sml1" /> (%)</th>
							<th class="textaligncenter"><spring:message code="label.parameter.maturity.sml2" /> (%)</th>
							<th class="textaligncenter"><spring:message code="label.parameter.maturity.sml3" /> (%)</th>
							<th class="textaligncenter"><spring:message code="label.parameter.maturity.sml4" /> (%)</th>
							<th class="textaligncenter"><spring:message code="label.parameter.maturity.sml5" /> (%)</th>
						</tr>
					</thead>
					<tfoot></tfoot>
					<tbody>
						<c:forEach items="${parametersSplited[2]}" var="parameter">
							<tr data-trick-class="MaturityParameter" data-trick-id="${parameter.id}">
								<td class="textaligncenter"><spring:message code="label.parameter.maturity.rsml.category.${fn:toLowerCase(parameter.category)}" /></td>
								<td class="textaligncenter"><spring:message code="label.parameter.maturity.rsml.description.${fn:toLowerCase(fn:replace(parameter.description,' ','_'))}" /></td>

								<td class="success textaligncenter" data-trick-field="SMLLevel0" data-trick-max-value="100" data-trick-min-value="0" data-trick-field-type="double"
									onclick="return editField(this);"><fmt:formatNumber value="${parameter.SMLLevel0*100}" maxFractionDigits="0" /></td>
								<td class="success textaligncenter" data-trick-field="SMLLevel1" data-trick-max-value="100" data-trick-min-value="0" data-trick-field-type="double"
									onclick="return editField(this);"><fmt:formatNumber value="${parameter.SMLLevel1*100}" maxFractionDigits="0" /></td>
								<td class="success textaligncenter" data-trick-field="SMLLevel2" data-trick-max-value="100" data-trick-min-value="0" data-trick-field-type="double"
									onclick="return editField(this);"><fmt:formatNumber value="${parameter.SMLLevel2*100}" maxFractionDigits="0" /></td>
								<td class="success textaligncenter" data-trick-field="SMLLevel3" data-trick-max-value="100" data-trick-min-value="0" data-trick-field-type="double"
									onclick="return editField(this);"><fmt:formatNumber value="${parameter.SMLLevel3*100}" maxFractionDigits="0" /></td>
								<td class="success textaligncenter" data-trick-field="SMLLevel4" data-trick-max-value="100" data-trick-min-value="0" data-trick-field-type="double"
									onclick="return editField(this);"><fmt:formatNumber value="${parameter.SMLLevel4*100}" maxFractionDigits="0" /></td>
								<td class="success textaligncenter" data-trick-field="SMLLevel5" data-trick-max-value="100" data-trick-min-value="0" data-trick-field-type="double"
									onclick="return editField(this);"><fmt:formatNumber value="${parameter.SMLLevel5*100}" maxFractionDigits="0" /></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</div>
	<div class="col-md-6">
		<div class="panel panel-default">
			<div class="panel-heading">
				<spring:message code="label.title.parameter.simple.various" />
			</div>
			<div class="panel-body">
				<table class="table table-hover table-condensed">
					<thead>
						<tr>
							<th class="textaligncenter"><spring:message code="label.parameter.simple.internal_setup" /></th>
							<th class="textaligncenter"><spring:message code="label.parameter.simple.external_setup" /></th>
							<th class="textaligncenter"><spring:message code="label.parameter.simple.default_life_time" /></th>
							<th class="textaligncenter"><spring:message code="label.parameter.simple.max_rrf" /></th>
							<th class="textaligncenter"><spring:message code="label.parameter.simple.soa" /></th>
							<th class="textaligncenter"><spring:message code="label.parameter.simple.mandatory_phase" /></th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<c:forEach items="${simpleParameters[0]}" var="parameter">
								<c:choose>
									<c:when test="${parameter.description=='max_rrf' or parameter.description=='soaThreshold'}">
										<td data-trick-class="Parameter" data-trick-id="${parameter.id}" data-trick-min-value='0' data-trick-max-value='100' class="success textaligncenter"
											data-trick-field="value" data-trick-field-type="double" onclick="return editField(this);"><fmt:formatNumber value="${parameter.value}" maxFractionDigits="0"
												pattern="#" /></td>
									</c:when>
									<c:when test="${parameter.description== 'lifetime_default'}">
										<td data-trick-class="Parameter" data-trick-id="${parameter.id}" data-trick-min-value='1e-19' class="success textaligncenter" data-trick-field="value"
											data-trick-field-type="double" onclick="return editField(this);"><fmt:formatNumber value="${parameter.value}" maxFractionDigits="0" pattern="#" /></td>
									</c:when>
									<c:when test="${parameter.description=='mandatoryPhase'}">
										<td data-trick-class="Parameter" data-trick-callback-pre="extractPhase(this,true)" data-trick-id="${parameter.id}" class="success textaligncenter"
											data-trick-field="value" data-trick-field-type="double" onclick="return editField(this);"><fmt:formatNumber value="${parameter.value}" maxFractionDigits="0"
												pattern="#" /></td>
									</c:when>
									<c:otherwise>
										<td data-trick-class="Parameter" data-trick-id="${parameter.id}" class="success textaligncenter" data-trick-field="value" data-trick-field-type="double"
											onclick="return editField(this);"><fmt:formatNumber value="${parameter.value}" maxFractionDigits="0" pattern="#" /></td>
									</c:otherwise>
								</c:choose>

							</c:forEach>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
	</div>

	<spring:message code='label.nil' var="nil"/>
	<spring:message code='label.all' var="all"/>
	<spring:message code='label.compliant' var="compliant"/>
	<div class="col-md-6">
		<div class="panel panel-default">
			<div class="panel-heading">
				<spring:message code="label.title.parameter.simple.cssf" />
			</div>
			<div class="panel-body">
				<table class="table table-hover table-condensed">
					<thead>
						<tr>
							<th class="textaligncenter"><spring:message code="label.parameter.simple.cssf.impact_threshold" /></th>
							<th class="textaligncenter"><spring:message code="label.parameter.simple.cssf.probability_threshold" /></th>
							<th class="textaligncenter"><spring:message code="label.parameter.simple.cssf.direct_size" /></th>
							<th class="textaligncenter"><spring:message code="label.parameter.simple.cssf.indirect_size" /></th>
							<th class="textaligncenter"><spring:message code="label.parameter.simple.cssf.cia_size" /></th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<c:forEach items="${simpleParameters[1]}" var="parameter">
								<c:choose>
									<c:when test="${parameter.description=='cssfImpactThreshold' or parameter.description=='cssfProbabilityThreshold'}">
										<td data-trick-class="Parameter" data-trick-id="${parameter.id}" data-trick-min-value='0' data-trick-max-value='10' data-trick-step-value='1' class="success textaligncenter"
											data-trick-field="value" data-trick-field-type="double" onclick="return editField(this);"><fmt:formatNumber value="${parameter.value}" maxFractionDigits="0"
												pattern="#" /></td>
									</c:when>
									<c:when test="${parameter.description== 'cssfCIASize' or parameter.description== 'cssfDirectSize' or parameter.description== 'cssfIndirectSize'}">
										<fmt:formatNumber value="${parameter.value}" maxFractionDigits="0" pattern="#" var="cssfSize" />
										<td data-trick-class="Parameter" data-trick-id="${parameter.id}" data-trick-choose-translate='${nil},${all},${compliant}' data-trick-min-value='-2' data-trick-step-value='1'
											data-trick-max-value='1000' class="success textaligncenter" data-trick-field="value" data-trick-field-type="double" onclick="return editField(this);">
											<c:choose>
												<c:when test="${parameter.value <= -2 }">
													${nil}
													</c:when>
													<c:when test="${parameter.value == -1}">
													${all}
													</c:when>
												<c:when test="${parameter.value == 0}">
													${compliant}
													</c:when>
												<c:otherwise>${cssfSize}</c:otherwise>
											</c:choose>
										</td>
									</c:when>
								</c:choose>
							</c:forEach>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
	</div>

	<div class="col-md-6">
		<div class="panel panel-default">
			<div class="panel-heading">
				<spring:message code="label.title.parameter.simple.maturity_level" />
			</div>
			<div class="panel-body">
				<table class="table table-hover table-condensed">
					<thead>
						<tr>
							<th class="textaligncenter"><spring:message code="label.parameter.simple.sml0" /> (%)</th>
							<th class="textaligncenter"><spring:message code="label.parameter.simple.sml1" /> (%)</th>
							<th class="textaligncenter"><spring:message code="label.parameter.simple.sml2" /> (%)</th>
							<th class="textaligncenter"><spring:message code="label.parameter.simple.sml3" /> (%)</th>
							<th class="textaligncenter"><spring:message code="label.parameter.simple.sml4" /> (%)</th>
							<th class="textaligncenter"><spring:message code="label.parameter.simple.sml5" /> (%)</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<c:forEach items="${simpleParameters[2]}" var="parameter">
								<td data-trick-class="Parameter" data-trick-id="${parameter.id}" data-trick-min-value='0' data-trick-max-value='100' class="success textaligncenter"
									data-trick-field="value" data-trick-field-type="double" onclick="return editField(this);"><fmt:formatNumber value="${parameter.value}" maxFractionDigits="0" /></td>
							</c:forEach>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
	</div>
	<div class="col-md-6">
		<div class="panel panel-default" id="Maturity_implementation_rate">
			<div class="panel-heading">
				<spring:message code="label.title.parameter.simple.smt" />
			</div>
			<div class="panel-body">
				<table class="table table-hover table-condensed">
					<thead>
						<tr>
							<th class="textaligncenter"><spring:message code="label.parameter.level" /></th>
							<th class="textaligncenter"><spring:message code="label.parameter.implementation" /> (%)</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${simpleParameters[3]}" var="parameter">
							<tr data-trick-class="Parameter" data-trick-id="${parameter.id}">
								<td class="textaligncenter"><spring:message code="label.parameter.simple.smt.level_${parameter.description}" text="${parameter.description}"/></td>
								<td data-trick-field="value" data-trick-field-type="double" data-trick-min-value='0' data-trick-max-value='100' class="success textaligncenter"
									onclick="return editField(this);"><fmt:formatNumber value="${parameter.value}" maxFractionDigits="0" /></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</div>