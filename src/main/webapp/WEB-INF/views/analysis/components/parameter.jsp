<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<span class="anchor" id="anchorParameter"></span>
<div class="section" id="section_parameter">
	<div class="page-header">
		<h3 id="Parameter">
			<spring:message code="label.parameter" text="Parameters" />
		</h3>
	</div>
	<spring:eval expression="T(lu.itrust.business.TS.Analysis).SplitParameters(parameters)" var="parametersSplited" />
	<spring:eval expression="T(lu.itrust.business.TS.Analysis).SplitSimpleParameters(parametersSplited[0])" var="simpleParameters" />
	<spring:eval expression="T(lu.itrust.business.TS.Analysis).SplitExtendedParameters(parametersSplited[1])" var="extendedParameters" />
	<spring:eval expression="T(lu.itrust.business.TS.Analysis).SplitMaturityParameters(parameters)" var="maturityParameters" />

	<div class="row">
		<div class="col-md-6">
			<span id="anchorParameter_Impact" class="anchor"></span>
			<div class="panel panel-default" id="Scale_Impact">
				<div class="panel-heading">
					<spring:message code="label.parameter.extended.impact" text="Impact of threat" />
				</div>
				<div class="panel-body">
					<table class="table table-hover">
						<thead>
							<tr>
								<th class="textaligncenter"><spring:message code="label.parameter.level" text="Level" /></th>
								<th class="textaligncenter"><spring:message code="label.parameter.acronym" text="Acronym" /></th>
								<th class="textaligncenter"><spring:message code="label.parameter.qualification" text="Qualification" /></th>
								<th class="textaligncenter"><spring:message code="label.parameter.value" text="Value" /> k&euro;</th>
								<th class="textaligncenter"><spring:message code="label.parameter.range.min" text="Range Min" /></th>
								<th class="textaligncenter"><spring:message code="label.parameter.range.max" text="Range max" /></th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${extendedParameters[0]}" var="parameter" varStatus="status">
								<tr trick-class="ExtendedParameter" trick-id="${parameter.id}">
									<!--<td>${itemInformation.id}</td>-->
									<td class="textaligncenter"><spring:message text="${parameter.level}" /></td>
									<td trick-field="acronym" trick-field-type="string" class="success textaligncenter" trick-callback="updateAssessmentAcronym('${parameter.id}')"
										ondblclick="return editField(this);"><spring:message text="${parameter.acronym}" /></td>
									<td trick-field="description" trick-field-type="string" class="success textaligncenter" ondblclick="return editField(this);"><spring:message
											text="${parameter.description}" /></td>
									<td trick-field="value" trick-field-type="double" title='<fmt:formatNumber value="${parameter.value}" maxFractionDigits="0" />&euro;'
										${(parameter.level mod 2)==0? 'ondblclick="return editField(this);" class="success textaligncenter"': 'class="textaligncenter"'}><fmt:formatNumber
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
			<span id="anchorParameter_Probability" class="anchor"></span>
			<div class="panel panel-default" id="Scale_Probability">
				<div class="panel-heading">
					<spring:message code="label.parameter.extended.probability" text="Probability of threat occurrence" />
				</div>
				<div class="panel-body">
					<table class="table table-hover">
						<thead>
							<tr>
								<th class="textaligncenter"><spring:message code="label.parameter.level" text="Level" /></th>
								<th class="textaligncenter"><spring:message code="label.parameter.acronym" text="Acronym" /></th>
								<th class="textaligncenter"><spring:message code="label.parameter.qualification" text="Qualification" /></th>
								<th class="textaligncenter"><spring:message code="label.parameter.value" text="Value" /> /y</th>
								<th class="textaligncenter"><spring:message code="label.parameter.range.min" text="Range Min" /></th>
								<th class="textaligncenter"><spring:message code="label.parameter.range.max" text="Range max" /></th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${extendedParameters[1]}" var="parameter" varStatus="status">
								<tr trick-class="ExtendedParameter" trick-id="${parameter.id}">
									<!--<td>${itemInformation.id}</td>-->
									<td class="textaligncenter"><spring:message text="${parameter.level}" /></td>
									<td trick-field="acronym" trick-field-type="string" class="success textaligncenter" trick-callback="updateAssessmentAcronym('${parameter.id}')"
										ondblclick="return editField(this);"><spring:message text="${parameter.acronym}" /></td>
									<td trick-field="description" trick-field-type="string" class="success textaligncenter" ondblclick="return editField(this);"><spring:message
											text="${parameter.description}" /></td>
									<c:set var="parameterValue">
										<fmt:formatNumber value="${parameter.value}" />
									</c:set>
									<td trick-field="value" trick-field-type="double"
										${(parameter.level mod 2)==0? 'ondblclick="return editField(this);" class="success textaligncenter"': 'class="textaligncenter"'} title="${parameterValue}"
										real-value="${parameterValue}"><fmt:formatNumber value="${parameter.value}" maxFractionDigits="2" minFractionDigits="2" /></td>
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
	</div>

	<div class="row">
		<div class="col-md-6">
			<span id="anchorParameter_ILPS" class="anchor"></span>
			<div class="panel panel-default">
				<div class="panel-heading">
					<spring:message code="label.parameter.maturity_ilps" text="Required level of implmentation per SML" />
				</div>
				<div class="panel-body autofitpanelbodydefinition" style="max-height: 655px; overflow: auto; min-height: 655px">
					<table class="table table-hover" id="tableMaturityIlps">
						<thead>
							<tr>
								<th class="textaligncenter"><spring:message code="label.parameter.maturity.Category" text="Category" /></th>
								<th class="textaligncenter"><spring:message code="label.parameter.maturity.Task" text="Task" /></th>
								<th class="textaligncenter"><spring:message code="label.parameter.maturity.sml0" text="SML0" /> (%)</th>
								<th class="textaligncenter"><spring:message code="label.parameter.maturity.sml1" text="SML1" /> (%)</th>
								<th class="textaligncenter"><spring:message code="label.parameter.maturity.sml2" text="SML2" /> (%)</th>
								<th class="textaligncenter"><spring:message code="label.parameter.maturity.sml3" text="SML3" /> (%)</th>
								<th class="textaligncenter"><spring:message code="label.parameter.maturity.sml4" text="SML4" /> (%)</th>
								<th class="textaligncenter"><spring:message code="label.parameter.maturity.sml5" text="SML5" /> (%)</th>
							</tr>
						</thead>
						<tfoot></tfoot>
						<tbody>
							<c:forEach items="${maturityParameters}" var="parameter">
								<tr trick-class="MaturityParameter" trick-id="${parameter.id}">
									<td class="textaligncenter"><spring:message code="label.parameter.maturity.RSML.${parameter.category}" text="${parameter.category}" /></td>
									<td class="textaligncenter"><spring:message code="label.parameter.maturity.RSML.${parameter.description}" text="${parameter.description}" /></td>
									<td class="success textaligncenter" trick-field="SMLLevel0" trick-field-type="double" ondblclick="return editField(this);"><fmt:formatNumber
											value="${parameter.SMLLevel0*100}" maxFractionDigits="0" /></td>
									<td class="success textaligncenter" trick-field="SMLLevel1" trick-field-type="double" ondblclick="return editField(this);"><fmt:formatNumber
											value="${parameter.SMLLevel1*100}" maxFractionDigits="0" /></td>
									<td class="success textaligncenter" trick-field="SMLLevel2" trick-field-type="double" ondblclick="return editField(this);"><fmt:formatNumber
											value="${parameter.SMLLevel2*100}" maxFractionDigits="0" /></td>
									<td class="success textaligncenter" trick-field="SMLLevel3" trick-field-type="double" ondblclick="return editField(this);"><fmt:formatNumber
											value="${parameter.SMLLevel3*100}" maxFractionDigits="0" /></td>
									<td class="success textaligncenter" trick-field="SMLLevel4" trick-field-type="double" ondblclick="return editField(this);"><fmt:formatNumber
											value="${parameter.SMLLevel4*100}" maxFractionDigits="0" /></td>
									<td class="success textaligncenter" trick-field="SMLLevel5" trick-field-type="double" ondblclick="return editField(this);"><fmt:formatNumber
											value="${parameter.SMLLevel5*100}" maxFractionDigits="0" /></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
		</div>
		<div class="col-md-6">
			<span id="anchorParameter_ImplementationRate" class="anchor"></span>
			<div class="panel panel-default" id="Maturity_implementation_rate">
				<div class="panel-heading">
					<spring:message code="label.parameter.simple.smt" text="Implementation scale of SMT" />
				</div>
				<div class="panel-body">
					<table class="table table-hover">
						<thead>
							<tr>
								<th class="textaligncenter"><spring:message code="label.parameter.Level" text="Level" /></th>
								<th class="textaligncenter"><spring:message code="label.parameter.percentage" text="Implementation" /> (%)</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${simpleParameters[2]}" var="parameter">
								<tr trick-class="Parameter" trick-id="${parameter.id}">
									<td class="textaligncenter"><spring:message code="label.parameter.simple.smt.level_${parameter.description}" text="${parameter.description}" /></td>
									<td trick-field="value" trick-field-type="double" class="success textaligncenter" ondblclick="return editField(this);"><fmt:formatNumber value="${parameter.value}"
											maxFractionDigits="0" /></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
		</div>
		<div class="col-md-6">
			<span id="anchorParameter_Various" class="anchor"></span>
			<div class="panel panel-default">
				<div class="panel-heading">
					<spring:message code="label.parameter.simple.various" text="Various parameters" />
				</div>
				<div class="panel-body">
					<table class="table table-hover">
						<thead>
							<tr>
								<th class="textaligncenter"><spring:message code="label.parameter.simple.internal_setup" text="Internal setup" /></th>
								<th class="textaligncenter"><spring:message code="label.parameter.simple.external_setup" text="External setup" /></th>
								<th class="textaligncenter"><spring:message code="label.parameter.simple.default_life_time" text="Default lifetime" /></th>
								<th class="textaligncenter"><spring:message code="label.parameter.simple.tuning" text="Tuning" /></th>
								<th class="textaligncenter"><spring:message code="label.parameter.simple.soa" text="SOA" /></th>
								<th class="textaligncenter"><spring:message code="label.parameter.simple.mandatory_phase" text="Mandatory phase" /></th>
								<th class="textaligncenter"><spring:message code="label.parameter.simple.importance_threshold" text="Importance threshold" /></th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<c:forEach items="${simpleParameters[0]}" var="parameter">
									<td trick-class="Parameter" trick-id="${parameter.id}" class="success textaligncenter" trick-field="value" trick-field-type="double" ondblclick="return editField(this);"><fmt:formatNumber
											value="${parameter.value}" maxFractionDigits="0" pattern="#" /></td>
								</c:forEach>
							</tr>
						</tbody>
					</table>
				</div>
			</div>
		</div>
		<div class="col-md-6">
			<span id="anchorParameter_MaxEfficiency" class="anchor"></span>
			<div class="panel panel-default">
				<div class="panel-heading">
					<spring:message code="label.parameter.simple.maturity_level" text="Maximal efficiency rate per security maturity level" />
				</div>
				<div class="panel-body">
					<table class="table table-hover">
						<thead>
							<tr>
								<th class="textaligncenter"><spring:message code="label.parameter.simple.sml0" text="SML0" /> (%)</th>
								<th class="textaligncenter"><spring:message code="label.parameter.simple.sml1" text="SML1" /> (%)</th>
								<th class="textaligncenter"><spring:message code="label.parameter.simple.sml2" text="SML2" /> (%)</th>
								<th class="textaligncenter"><spring:message code="label.parameter.simple.sml3" text="SML3" /> (%)</th>
								<th class="textaligncenter"><spring:message code="label.parameter.simple.sml4" text="SML4" /> (%)</th>
								<th class="textaligncenter"><spring:message code="label.parameter.simple.sml5" text="SML5" /> (%)</th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<c:forEach items="${simpleParameters[1]}" var="parameter">
									<td trick-class="Parameter" trick-id="${parameter.id}" class="success textaligncenter" trick-field="value" trick-field-type="double" ondblclick="return editField(this);"><fmt:formatNumber
											value="${parameter.value}" maxFractionDigits="0" /></td>
								</c:forEach>
							</tr>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
</div>