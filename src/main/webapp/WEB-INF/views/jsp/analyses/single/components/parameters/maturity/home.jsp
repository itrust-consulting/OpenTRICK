<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<fmt:setLocale value="fr" scope="session" />
<div class="tab-pane" id="tab-parameter-maturity">
	<div class='section row' id="section_parameter_maturity">
		<div class="page-header tab-content-header">
			<div class="container">
				<div class="row-fluid">
					<h3>
						<spring:message code='label.title.maturity.parameters' text="Maturity parameters" />
					</h3>
				</div>
			</div>
		</div>
		<div class="col-md-6">
			<fieldset>
				<legend>
					<spring:message code="label.title.parameter.maturity_ilps" />
				</legend>
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
						<c:forEach items="${mappedParameters['ILPS']}" var="parameter">
							<tr data-trick-class="MaturityParameter" data-trick-id="${parameter.id}" data-trick-callback="updateMeasureEffience()">
								<td class="textaligncenter"><spring:message code="label.parameter.maturity.rsml.category.${fn:toLowerCase(parameter.category)}" /></td>
								<td class="textaligncenter"><spring:message code="label.parameter.maturity.rsml.description.${fn:toLowerCase(fn:replace(parameter.description,' ','_'))}" /></td>
								<td class="editable textaligncenter" data-trick-field="SMLLevel0" data-trick-max-value="100" data-trick-min-value="0" data-trick-field-type="double"
									onclick="return editField(this);"><fmt:formatNumber value="${parameter.SMLLevel0*100}" maxFractionDigits="0" /></td>
								<td class="editable textaligncenter" data-trick-field="SMLLevel1" data-trick-max-value="100" data-trick-min-value="0" data-trick-field-type="double"
									onclick="return editField(this);"><fmt:formatNumber value="${parameter.SMLLevel1*100}" maxFractionDigits="0" /></td>
								<td class="editable textaligncenter" data-trick-field="SMLLevel2" data-trick-max-value="100" data-trick-min-value="0" data-trick-field-type="double"
									onclick="return editField(this);"><fmt:formatNumber value="${parameter.SMLLevel2*100}" maxFractionDigits="0" /></td>
								<td class="editable textaligncenter" data-trick-field="SMLLevel3" data-trick-max-value="100" data-trick-min-value="0" data-trick-field-type="double"
									onclick="return editField(this);"><fmt:formatNumber value="${parameter.SMLLevel3*100}" maxFractionDigits="0" /></td>
								<td class="editable textaligncenter" data-trick-field="SMLLevel4" data-trick-max-value="100" data-trick-min-value="0" data-trick-field-type="double"
									onclick="return editField(this);"><fmt:formatNumber value="${parameter.SMLLevel4*100}" maxFractionDigits="0" /></td>
								<td class="editable textaligncenter" data-trick-field="SMLLevel5" data-trick-max-value="100" data-trick-min-value="0" data-trick-field-type="double"
									onclick="return editField(this);"><fmt:formatNumber value="${parameter.SMLLevel5*100}" maxFractionDigits="0" /></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</fieldset>
		</div>
		<div class="col-md-6">
			<fieldset>
				<legend>
					<spring:message code="label.title.parameter.simple.maturity_level" />
				</legend>

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
						<tr data-trick-class="SimpleParameter" data-trick-min-value='0' data-trick-max-value='100' data-trick-callback="updateMeasureEffience()">
							<c:forEach items="${mappedParameters['MAXEFF']}" var="parameter">
								<td data-trick-field="value" data-trick-field-type="double" data-trick-id="${parameter.id}" class="editable textaligncenter" onclick="return editField(this);"><fmt:formatNumber
										value="${parameter.value}" maxFractionDigits="0" /></td>
							</c:forEach>
						</tr>
					</tbody>
				</table>
			</fieldset>
		</div>
		<div class="col-md-6">
			<fieldset id="Maturity_implementation_rate">
				<legend>
					<spring:message code="label.title.parameter.simple.smt" />
				</legend>
				<table class="table table-hover table-condensed">
					<thead>
						<tr>
							<th class="textaligncenter"><spring:message code="label.parameter.level" /></th>
							<th class="textaligncenter"><spring:message code="label.parameter.implementation" /> (%)</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${mappedParameters['IMPSCALE']}" var="parameter">
							<tr data-trick-class="SimpleParameter" data-trick-id="${parameter.id}">
								<td class="textaligncenter"><spring:message code="label.parameter.simple.smt.level_${parameter.description}" text="${parameter.description}" /></td>
								<td data-trick-field="value" data-trick-field-type="double" data-trick-min-value='0' data-trick-max-value='100' class="editable textaligncenter"
									onclick="return editField(this);"><fmt:formatNumber value="${parameter.value}" maxFractionDigits="0" /></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</fieldset>
		</div>
	</div>
</div>