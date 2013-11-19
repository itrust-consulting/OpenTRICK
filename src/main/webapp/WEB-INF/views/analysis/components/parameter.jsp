<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<div class="section" id="section_parameter">
	<div class="page-header">
		<h3 id="parameter">
			<spring:message code="label.parameter" text="Parameter" />
		</h3>
	</div>
	<spring:eval
		expression="T(lu.itrust.business.TS.Analysis).SplitParameters(parameters)"
		var="parametersSplited" />
	<spring:eval
		expression="T(lu.itrust.business.TS.Analysis).SplitSimpleParameters(parametersSplited[0])"
		var="simpleParameters" />
	<spring:eval
		expression="T(lu.itrust.business.TS.Analysis).SplitExtendedParameters(parametersSplited[1])"
		var="extendedParameters" />
	<div class="row">
		<div class="col-md-12">
			<div class="panel panel-primary">
				<div class="panel-heading">
					<spring:message code="label.parameter.simple.various"
						text="Various parameters" />
				</div>
				<div class="panel-body">
					<table class="table">
						<thead>
							<tr>
								<th><spring:message
										code="label.parameter.simple.internal_setup"
										text="Internal setup" /></th>
								<th><spring:message
										code="label.parameter.simple.external_setup"
										text="External setup" /></th>
								<th><spring:message
										code="label.parameter.simple.default_life_time"
										text="Default lifetime" /></th>
								<th><spring:message
										code="label.parameter.simple.default_maintenance"
										text="Default maintenance" /></th>
								<th><spring:message code="label.parameter.simple.tuning"
										text="Tunning" /></th>
								<th><spring:message code="label.parameter.simple.soa"
										text="SOA" /></th>
								<th><spring:message
										code="label.parameter.simple.mandatory_phase"
										text="Mandatory phase" /></th>
								<th><spring:message
										code="label.parameter.simple.importance_threshold"
										text="Importance threshold" /></th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<c:forEach var="index" begin="0" end="7">
									<td>${simpleParameters[0].size() >index ? simpleParameters[0].get(index).value : '0.0'}
									</td>
								</c:forEach>
							</tr>
						</tbody>
					</table>
				</div>
			</div>
		</div>
		<div class="col-md-12">
			<div class="panel panel-primary">
				<div class="panel-heading">
					<spring:message code="label.parameter.extended.impact"
						text="Impact of thread" />
				</div>
				<div class="panel-body">
					<table class="table">
						<thead>
							<tr>
								<th><spring:message code="label.parameter.level"
										text="Level" /></th>
								<th><spring:message code="label.parameter.acronym"
										text="Acronym" /></th>
								<th><spring:message code="label.parameter.qualification"
										text="Qualification" /></th>
								<th><spring:message code="label.parameter.value"
										text="Value" /> k&euro;</th>
								<th><spring:message code="label.parameter.range.min"
										text="Range Min" /></th>
								<th><spring:message code="label.parameter.range.max"
										text="Range max" /></th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${extendedParameters[0]}" var="parameter"
								varStatus="status">
								<tr>
									<!--<td>${itemInformation.id}</td>-->
									<td>${status.index}</td>
									<td
										ondblclick="return editField(this, 'extendedParameter','${extendedParameters[0].get(index).id}', 'acronym', 'string');">${parameter.acronym}</td>
									<td
										ondblclick="return editField(this, 'extendedParameter','${extendedParameters[0].get(index).id}', 'description', 'string');">${parameter.description}</td>
									<td
										ondblclick="return editField(this, 'extendedParameter','${extendedParameters[0].get(index).id}', 'value', 'double');">${parameter.value*0.001}</td>
									<td>${parameter.bounds.from*0.001}</td>
									<td>${parameter.bounds.to*0.001}</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
		</div>
		<div class="col-md-12">
			<div class="panel panel-primary">
				<div class="panel-heading">
					<spring:message code="label.parameter.extended.probability"
						text="Probability of threat occurence" />
				</div>
				<div class="panel-body">
					<table class="table">
						<thead>
							<tr>
								<th><spring:message code="label.parameter.level"
										text="Level" /></th>
								<th><spring:message code="label.parameter.acronym"
										text="Acronym" /></th>
								<th><spring:message code="label.parameter.qualification"
										text="Qualification" /></th>
								<th><spring:message code="label.parameter.value"
										text="Value" /> /y</th>
								<th><spring:message code="label.parameter.range.min"
										text="Range Min" /></th>
								<th><spring:message code="label.parameter.range.max"
										text="Range max" /></th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${extendedParameters[1]}" var="parameter"
								varStatus="status">
								<tr>
									<!--<td>${itemInformation.id}</td>-->
									<td>${status.index}</td>
									<td
										ondblclick="return editField(this, 'parameter','${extendedParameters[1].get(index).id}', 'acronym', 'string');">${parameter.acronym}</td>
									<td
										ondblclick="return editField(this, 'parameter','${extendedParameters[1].get(index).id}', 'description', 'string');">${parameter.description}</td>
									<td
										ondblclick="return editField(this, 'parameter','${extendedParameters[1].get(index).id}', 'value', 'double');"><fmt:formatNumber
											value="${parameter.value}" maxFractionDigits="3"
											minFractionDigits="1" /></td>
									<td>${parameter.bounds.from*0.001}</td>
									<td>${parameter.bounds.to*0.001}</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
		</div>
		<div class="col-md-5">
			<div class="panel panel-primary">
				<div class="panel-heading">
					<spring:message code="label.parameter.simple.smt"
						text="Implementation scale of SMT" />
				</div>
				<div class="panel-body">
					<table class="table">
						<thead>
							<tr>
								<th><spring:message code="label.parameter.Level"
										text="Level" /></th>
								<th><spring:message code="label.parameter.percentage"
										text="Implementation" /> %</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${simpleParameters[2]}" var="parameter">
								<tr>
									<!--<td>${itemInformation.id}</td>-->
									<td><spring:message
											code="label.parameter.simple.smt.level_${parameter.description}"
											text="${parameter.description}" /></td>
									<td
										ondblclick="return editField(this, 'parameter','${parameter.id}', 'value', 'double');">${parameter.value}</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
		</div>
		<div class="col-md-7">
			<div class="panel panel-primary">
				<div class="panel-heading">
					<spring:message code="label.parameter.simple.maturity_level"
						text="Maximal efficiency rate per security maturity level" />
				</div>
				<div class="panel-body">
					<table class="table">
						<thead>
							<tr>
								<th><spring:message code="label.parameter.simple.sml0"
										text="SML0" /></th>
								<th><spring:message code="label.parameter.simple.sml1"
										text="SML1" /></th>
								<th><spring:message code="label.parameter.simple.sml2"
										text="SML2" /></th>
								<th><spring:message code="label.parameter.simple.sml3"
										text="SML3" /></th>
								<th><spring:message code="label.parameter.simple.sml4"
										text="SML4" /></th>
								<th><spring:message code="label.parameter.simple.sml5"
										text="SML5" /></th>
										<th></th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<c:forEach items="${simpleParameters[1]}" var="parameter">
									<td
										ondblclick="return editField(this, 'parameter','${parameter.id}', 'value', 'double');">${parameter.value}</td>
								</c:forEach>
								<td>%</td>
							</tr>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
</div>