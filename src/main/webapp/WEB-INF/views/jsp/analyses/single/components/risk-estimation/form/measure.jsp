<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<%@ taglib prefix="fct" uri="https://trickservice.com/tags/functions"%>
<spring:message code="label.assessment.likelihood.unit" var="probaUnit" />
<spring:message code="label.measure.status.m" var="statusM" />
<spring:message code="label.measure.status.ap" var="statusAP" />
<spring:message code="label.measure.status.ex" var="statusEX" />
<spring:message code="label.measure.status.na" var="statusNA" />
<spring:message code="label.title.measure.status.m" var="titleStatusM" />
<spring:message code="label.title.measure.status.ap" var="titleStatusAP" />
<spring:message code="label.title.measure.status.ex" var="titleStatusEX" />
<spring:message code="label.title.measure.status.na" var="titleStatusNA" />
<spring:message code="label.action.remove" var="titleRemove" />
<div class="modal fade" id="riskProfileMeasureManager" tabindex="-1" data-aria-hidden="true" data-aria-labelledby="probaScaleModal" data-keyboard="false">
	<div class="modal-dialog modal-lg">
		<div class="modal-content">
			<div class="modal-header bg-primary">
				<div id='risk-profile-measure-manager-header'>
					<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
					<h4 class="modal-title">
						<spring:message code="label.risk_profile.management.measure" text="Measure management" />
					</h4>
				</div>
				<div id='measure-form-header' style="display: none;">
					<button type="button" name="back" class="close" data-aria-hidden="true">&times;</button>
					<h4 class="modal-title">
						<spring:message code="label.tile.add.measure" />
					</h4>
				</div>
			</div>
			<div class="modal-body">
				<div id='risk-profile-measure-manager-container'>
					<div style="height: 580px; overflow-y: auto; overflow-x: hidden">
						<div id="riskProfileMessageContainer"></div>
						<div class='form-horizontal'>
							<div class="form-group">
								<label class="control-label text-right col-sm-4"><spring:message code="label.analysis.standards" /></label>
								<div class="col-sm-5">
									<select name="standard" id="riskProfileStandardSelector" class="form-control">
										<option disabled="disabled" value="0" selected="selected" data-trick-custom='false'><spring:message code="label.action.choose" /></option>
										<c:forEach items="${standards}" var="standard">
											<option value="${standard.id}" data-trick-custom='${standard.analysisOnly}'><spring:message text="${standard.name}" /></option>
										</c:forEach>
									</select>
								</div>
								<div class='col-sm-3'>
									<button class='btn btn-link' name="add-measure" style="display: none;">
										<i class='fa fa-plus' aria-hidden="true"></i> <spring:message code="label.action.add.measure" />
									</button>
								</div>
							</div>
						</div>
						<table class="table table-hover" id="riskProfileStandardMeasureContainer">
							<thead>
								<tr>
									<th style="width: 5%;" title='<spring:message code="label.action" />'><spring:message code="label.action" /></th>
									<th style="width: 2%;" title='<spring:message code="label.measure.norm" />'><spring:message code="label.measure.norm" /></th>
									<th style="width: 3%;" title='<spring:message code="label.reference" />'><spring:message code="label.measure.ref" /></th>
									<th title='<spring:message code="label.measure.domain" />'><spring:message code="label.measure.domain" /></th>
									<th style="width: 2%;" title='<spring:message code="label.title.measure.status" />'><spring:message code="label.measure.status" /></th>
									<th style="width: 2%;" title='<spring:message code="label.title.measure.ir" />'><spring:message code="label.measure.ir_no_unit" /></th>
									<th style="width: 2%;" title='<spring:message code="label.title.measure.phase" />'><spring:message code="label.measure.phase" /></th>
									<th style="width: 2%;" title='<spring:message code="label.title.measure.responsible" />'><spring:message code="label.measure.responsible" /></th>
								</tr>
							</thead>
							<tbody></tbody>
						</table>
					</div>

					<table style="display: none;" id="riskProfileSelectedMeasureContainer">
						<thead>
							<tr>
								<th style="width: 5%;" title='<spring:message code="label.action" />'><spring:message code="label.action" /></th>
								<th style="width: 2%;" title='<spring:message code="label.measure.norm" />'><spring:message code="label.measure.norm" /></th>
								<th style="width: 3%;" title='<spring:message code="label.reference" />'><spring:message code="label.measure.ref" /></th>
								<th title='<spring:message code="label.measure.domain" />'><spring:message code="label.measure.domain" /></th>
								<th style="width: 2%;" title='<spring:message code="label.title.measure.status" />'><spring:message code="label.measure.status" /></th>
								<th style="width: 2%;" title='<spring:message code="label.title.measure.ir" />'><spring:message code="label.measure.ir_no_unit" /></th>
								<th style="width: 2%;" title='<spring:message code="label.title.measure.phase" />'><spring:message code="label.measure.phase" /></th>
								<th style="width: 2%;" title='<spring:message code="label.title.measure.responsible" />'><spring:message code="label.measure.responsible" /></th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${riskProfile.measures}" var="measure">
								<c:set var="implementationRateValue" value="${measure.getImplementationRateValue(valueFactory)}" />
								<tr data-trick-class="Measure" data-trick-id="${measure.id}" ${implementationRateValue==100? 'class="warning"' : measure.status=='NA'? 'class="danger"':''}>
									<td><button class="btn btn-xs btn-danger" title="${titleRemove}">
											<i class="fa fa-times" aria-hidden="true"></i>
										</button></td>
									<td data-real-value='${measure.measureDescription.standard.id}'><spring:message text='${ measure.measureDescription.standard.name}' /></td>
									<c:set var="measureDescriptionText" value="${measure.measureDescription.getMeasureDescriptionTextByAlpha2(langue)}" />
									<c:choose>
										<c:when test="${empty measureDescriptionText or empty(measureDescriptionText.description)}">
											<td><spring:message text="${measure.measureDescription.reference}" /></td>
										</c:when>
										<c:otherwise>
											<td data-toggle='tooltip' data-container='body' data-trigger='click' data-placement='right' style='cursor: pointer;'
												title='<spring:message
														text="${measureDescriptionText.description}" />'><spring:message text="${measure.measureDescription.reference}" /></td>
										</c:otherwise>
									</c:choose>
									<td><spring:message text="${!empty measureDescriptionText? measureDescriptionText.domain : ''}" /></td>
									<c:choose>
										<c:when test="${measure.status=='NA'}">
											<td title="${titleStatusNA}" data-real-value='NA'>${statusNA}</td>
										</c:when>
										<c:when test="${measure.status=='AP'}">
											<td title="${titleStatusAP}" data-real-value='AP'>${statusAP}</td>
										</c:when>
										<c:when test="${measure.status=='EX'}">
											<td title="${titleStatusEX}" data-real-value='EX'>${statusEX}</td>
										</c:when>
										<c:otherwise>
											<td title="${titleStatusM}" data-real-value='M'>${statusM}</td>
										</c:otherwise>
									</c:choose>
									<td><fmt:formatNumber value="${implementationRateValue}" maxFractionDigits="0" minFractionDigits="0" /></td>
									<td title='<fmt:formatDate value="${measure.phase.endDate}" pattern="YYYY-MM-dd" />'>${measure.phase.number}</td>
									<td><spring:message text="${measure.responsible}" /></td>
							</c:forEach>
						</tbody>
					</table>
				</div>
				<div id="measure-form-container" style="display: none"></div>
			</div>
			<div class="modal-footer">
				<div id='risk-profile-measure-manager-buttons'>
					<button type="button" name="save" class="btn btn-primary">
						<spring:message code="label.action.save" />
					</button>
					<button type="button" name="cancel" class="btn btn-default" data-dismiss="modal">
						<spring:message code="label.action.close" />
					</button>
				</div>
				<div id='measure-form-buttons' style="display: none">
					<button type="button" name="save-measure" class="btn btn-primary">
						<spring:message code="label.action.save" />
					</button>
					<button type="button" name="back" class="btn btn-default">
						<spring:message code="label.action.back" />
					</button>
				</div>
			</div>
		</div>
	</div>
</div>