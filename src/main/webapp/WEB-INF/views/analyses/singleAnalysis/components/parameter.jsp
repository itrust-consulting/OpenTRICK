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
			<fmt:message key="label.title.parameter" />
		</h3>
	</div>
	<spring:eval expression="T(lu.itrust.business.TS.data.analysis.Analysis).SplitParameters(parameters)" var="parametersSplited" />
	<spring:eval expression="T(lu.itrust.business.TS.data.analysis.Analysis).SplitSimpleParameters(parametersSplited[0])" var="simpleParameters" />
	<spring:eval expression="T(lu.itrust.business.TS.data.analysis.Analysis).SplitExtendedParameters(parametersSplited[1])" var="extendedParameters" />
	<spring:eval expression="T(lu.itrust.business.TS.data.analysis.Analysis).SplitMaturityParameters(parameters)" var="maturityParameters" />
	<div class="row">
		<div class="col-md-6">
			<span id="anchorParameter_Impact" class="anchor"></span>
			<div class="panel panel-default" id="Scale_Impact">
				<div class="panel-heading">
					<fmt:message key="label.title.parameter.extended.impact" />
				</div>
				<div class="panel-body">
					<table class="table table-hover">
						<thead>
							<tr>
								<th class="textaligncenter"><fmt:message key="label.parameter.level" /></th>
								<th class="textaligncenter"><fmt:message key="label.parameter.acronym" /></th>
								<th class="textaligncenter"><fmt:message key="label.parameter.qualification" /></th>
								<th class="textaligncenter"><fmt:message key="label.parameter.value" /> k&euro;</th>
								<th class="textaligncenter"><fmt:message key="label.parameter.range.min" /></th>
								<th class="textaligncenter"><fmt:message key="label.parameter.range.max" /></th>
							</tr>
						</thead>
						<tbody>
							<fmt:setLocale value="fr" scope="session" />
							<c:forEach items="${extendedParameters[0]}" var="parameter" varStatus="status">
								<tr trick-class="ExtendedParameter" trick-id="${parameter.id}">
									<!--<td>${itemInformation.id}</td>-->
									<td class="textaligncenter"><spring:message text="${parameter.level}" /></td>
									<td trick-field="acronym" trick-field-type="string" class="success textaligncenter" ondblclick="return editField(this);"><spring:message text="${parameter.acronym}" /></td>
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
							<fmt:setLocale value="${fn:substring(analysis.language.alpha3,0, 2)}" scope="session" />
						</tbody>
					</table>
				</div>
			</div>
		</div>
		<div class="col-md-6">
			<span id="anchorParameter_Probability" class="anchor"></span>
			<div class="panel panel-default" id="Scale_Probability">
				<div class="panel-heading">
					<fmt:message key="label.parameter.extended.probability" />
				</div>
				<div class="panel-body">
					<table class="table table-hover">
						<thead>
							<tr>
								<th class="textaligncenter"><fmt:message key="label.parameter.level" /></th>
								<th class="textaligncenter"><fmt:message key="label.parameter.acronym" /></th>
								<th class="textaligncenter"><fmt:message key="label.parameter.qualification" /></th>
								<th class="textaligncenter"><fmt:message key="label.parameter.value" /> <fmt:message key="label.assessment.likelihood.unit" /></th>
								<th class="textaligncenter"><fmt:message key="label.parameter.range.min" /></th>
								<th class="textaligncenter"><fmt:message key="label.parameter.range.max" /></th>
							</tr>
						</thead>
						<tbody>
							<tr trick-class="ExtendedParameter" hidden="true">
								<td trick-field="acronym" colspan="3"><spring:message text="NA" /></td>
								<td trick-field="value" colspan="3">0</td>
							</tr>
							<fmt:setLocale value="fr" scope="session" />
							<c:forEach items="${extendedParameters[1]}" var="parameter" varStatus="status">
								<tr trick-class="ExtendedParameter" trick-id="${parameter.id}">
									<!--<td>${itemInformation.id}</td>-->
									<td class="textaligncenter"><spring:message text="${parameter.level}" /></td>
									<td trick-field="acronym" trick-field-type="string" class="success textaligncenter" ondblclick="return editField(this);"><spring:message text="${parameter.acronym}" /></td>
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
							<fmt:setLocale value="${fn:substring(analysis.language.alpha3,0, 2)}" scope="session" />
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
					<fmt:message key="label.title.parameter.maturity_ilps" />
				</div>
				<div class="panel-body autofitpanelbodydefinition" style="max-height: 619px; overflow: auto; min-height: 619px">
					<table class="table table-hover table-fixed-header" id="tableMaturityIlps">
						<thead>
							<tr>
								<th class="textaligncenter"><fmt:message key="label.parameter.maturity.category" /></th>
								<th class="textaligncenter"><fmt:message key="label.parameter.maturity.task" /></th>
								<th class="textaligncenter"><fmt:message key="label.parameter.maturity.sml0" /> (%)</th>
								<th class="textaligncenter"><fmt:message key="label.parameter.maturity.sml1" /> (%)</th>
								<th class="textaligncenter"><fmt:message key="label.parameter.maturity.sml2" /> (%)</th>
								<th class="textaligncenter"><fmt:message key="label.parameter.maturity.sml3" /> (%)</th>
								<th class="textaligncenter"><fmt:message key="label.parameter.maturity.sml4" /> (%)</th>
								<th class="textaligncenter"><fmt:message key="label.parameter.maturity.sml5" /> (%)</th>
							</tr>
						</thead>
						<tfoot></tfoot>
						<tbody>
							<c:forEach items="${maturityParameters}" var="parameter">
								<tr trick-class="MaturityParameter" trick-id="${parameter.id}">
									<td class="textaligncenter"><fmt:message key="label.parameter.maturity.rsml.category.${fn:toLowerCase(parameter.category)}" /></td>
									<td class="textaligncenter"><fmt:message key="label.parameter.maturity.rsml.description.${fn:toLowerCase(fn:replace(parameter.description,' ','_'))}" /></td>
									<fmt:setLocale value="fr" scope="session" />
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
									<fmt:setLocale value="${fn:substring(analysis.language.alpha3,0, 2)}" scope="session" />
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
					<fmt:message key="label.title.parameter.simple.various" />
				</div>
				<div class="panel-body">
					<table class="table table-hover">
						<thead>
							<tr>
								<th class="textaligncenter"><fmt:message key="label.parameter.simple.internal_setup" /></th>
								<th class="textaligncenter"><fmt:message key="label.parameter.simple.external_setup" /></th>
								<th class="textaligncenter"><fmt:message key="label.parameter.simple.default_life_time" /></th>
								<th class="textaligncenter"><fmt:message key="label.parameter.simple.max_rrf" /></th>
								<th class="textaligncenter"><fmt:message key="label.parameter.simple.soa" /></th>
								<th class="textaligncenter"><fmt:message key="label.parameter.simple.mandatory_phase" /></th>
								<th class="textaligncenter"><fmt:message key="label.parameter.simple.importance_threshold" /></th>
							</tr>
						</thead>
						<tbody>
							<fmt:setLocale value="fr" scope="session" />
							<tr>
								<c:forEach items="${simpleParameters[0]}" var="parameter">
									<td trick-class="Parameter" trick-id="${parameter.id}" class="success textaligncenter" trick-field="value" trick-field-type="double" ondblclick="return editField(this);"><fmt:formatNumber
											value="${parameter.value}" maxFractionDigits="0" pattern="#" /></td>
								</c:forEach>
							</tr>
							<fmt:setLocale value="${fn:substring(analysis.language.alpha3,0, 2)}" scope="session" />
						</tbody>
					</table>
				</div>
			</div>
		</div>
		<div class="col-md-6">
			<span id="anchorParameter_MaxEfficiency" class="anchor"></span>
			<div class="panel panel-default">
				<div class="panel-heading">
					<fmt:message key="label.title.parameter.simple.maturity_level" />
				</div>
				<div class="panel-body">
					<table class="table table-hover">
						<thead>
							<tr>
								<th class="textaligncenter"><fmt:message key="label.parameter.simple.sml0" /> (%)</th>
								<th class="textaligncenter"><fmt:message key="label.parameter.simple.sml1" /> (%)</th>
								<th class="textaligncenter"><fmt:message key="label.parameter.simple.sml2" /> (%)</th>
								<th class="textaligncenter"><fmt:message key="label.parameter.simple.sml3" /> (%)</th>
								<th class="textaligncenter"><fmt:message key="label.parameter.simple.sml4" /> (%)</th>
								<th class="textaligncenter"><fmt:message key="label.parameter.simple.sml5" /> (%)</th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<fmt:setLocale value="fr" scope="session" />
								<c:forEach items="${simpleParameters[1]}" var="parameter">
									<td trick-class="Parameter" trick-id="${parameter.id}" class="success textaligncenter" trick-field="value" trick-field-type="double" ondblclick="return editField(this);"><fmt:formatNumber
											value="${parameter.value}" maxFractionDigits="0" /></td>
								</c:forEach>
								<fmt:setLocale value="${fn:substring(analysis.language.alpha3,0, 2)}" scope="session" />
							</tr>
						</tbody>
					</table>
				</div>
			</div>
		</div>
		<div class="col-md-6">
			<span id="anchorParameter_ImplementationRate" class="anchor"></span>
			<div class="panel panel-default" id="Maturity_implementation_rate">
				<div class="panel-heading">
					<fmt:message key="label.title.parameter.simple.smt" />
				</div>
				<div class="panel-body">
					<table class="table table-hover">
						<thead>
							<tr>
								<th class="textaligncenter"><fmt:message key="label.parameter.level" /></th>
								<th class="textaligncenter"><fmt:message key="label.parameter.implementation" /> (%)</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${simpleParameters[2]}" var="parameter">
								<tr trick-class="Parameter" trick-id="${parameter.id}">
									<td class="textaligncenter"><fmt:message key="label.parameter.simple.smt.level_${parameter.description}" /></td>
									<fmt:setLocale value="fr" scope="session" />
									<td trick-field="value" trick-field-type="double" class="success textaligncenter" ondblclick="return editField(this);"><fmt:formatNumber value="${parameter.value}"
											maxFractionDigits="0" /></td>
									<fmt:setLocale value="${fn:substring(analysis.language.alpha3,0, 2)}" scope="session" />
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
</div>