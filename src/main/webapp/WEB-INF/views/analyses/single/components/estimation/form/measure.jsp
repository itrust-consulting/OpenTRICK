<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="fct" uri="http://trickservice.itrust.lu/JSTLFunctions"%>
<spring:message code="label.assessment.likelihood.unit" var="probaUnit" />
<spring:message code="label.measure.status.m" var="statusM" />
<spring:message code="label.measure.status.ap" var="statusAP" />
<spring:message code="label.measure.status.na" var="statusNA" />
<spring:message code="label.title.measure.status.m" var="titleStatusM" />
<spring:message code="label.title.measure.status.ap" var="titleStatusAP" />
<spring:message code="label.title.measure.status.na" var="titleStatusNA" />
<spring:message code="label.action.remove" var="titleRemove" />
<div class="modal fade" id="riskProfileMeasureManager" tabindex="-1" data-aria-hidden="true" data-aria-labelledby="probaScaleModal" data-keyboard="false">
	<div class="modal-dialog modal-lg">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title">
					<spring:message code="label.risk_profile.management.measure" text="Measure management" />
				</h4>
			</div>
			<div class="modal-body" style="padding-top: 5px;">
				<ul class="nav nav-tabs">
					<li class="active" title="<spring:message code='label.title.selected.measures' />"><a href="#tab_current_measure" data-toggle="tab"><spring:message code='label.measures' /></a></li>
					<li title="<spring:message code='label.title.measure.collection'/>"><a href="#tab_add_measure" data-toggle="tab"><spring:message code='label.measure.collection' /></a></li>
					<li id="riskProfileMessageContainer" style="padding-top: 10px"></li>
				</ul>
				<div class="tab-content" style="height: 580px; overflow-y: auto; overflow-x: hidden">
					<div id="tab_current_measure" class="tab-pane active" style="padding-top: 5px;">
						<table class="table table-hover" id="riskProfileSelectedMeasureContainer">
							<thead>
								<tr>
									<th style="width: 5%;" title='<spring:message code="label.action" />'><spring:message code="label.action" /></th>
									<th style="width: 2%;" title='<spring:message code="label.measure.norm" />'><spring:message code="label.measure.norm" /></th>
									<th style="width: 3%;" title='<spring:message code="label.reference" />'><spring:message code="label.measure.ref" /></th>
									<th style="width: 2%;" title='<spring:message code="label.title.measure.status" />'><spring:message code="label.measure.status" /></th>
									<th style="width: 2%;" title='<spring:message code="label.title.measure.ir" />'><spring:message code="label.measure.ir_no_unit" /></th>
									<th style="width: 2%;" title='<spring:message code="label.title.measure.phase" />'><spring:message code="label.measure.phase" /></th>
									<th title='<spring:message code="label.measure.domain" />'><spring:message code="label.measure.domain" /></th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${riskProfile.measures}" var="measure">
									<c:set var="implementationRateValue" value="${measure.getImplementationRateValue(valueFactory)}" />
									<tr data-trick-class="Measure" data-trick-id="${measure.id}" ${implementationRateValue==100? 'class="warning"' : measure.status=='NA'? 'class="danger"':''} >
										<td><button class="btn btn-xs btn-danger" title="${titleRemove}" >
												<i class="fa fa-times" aria-hidden="true"></i>
											</button></td>
										<td data-real-value='${measure.measureDescription.standard.id}'><spring:message text='${ measure.measureDescription.standard.label}' /></td>
										<c:set var="measureDescriptionText" value="${measure.measureDescription.getMeasureDescriptionTextByAlpha2(langue)}" />
										<c:choose>
											<c:when test="${empty measureDescriptionText or empty(measureDescriptionText.description)}">
												<td><spring:message text="${measure.measureDescription.reference}" /></td>
											</c:when>
											<c:otherwise>
												<td data-toggle='tooltip' data-container='body' data-trigger='click' data-placement='right' style='cursor: pointer;' title='<spring:message
														text="${measureDescriptionText.description}" />'><spring:message
														text="${measure.measureDescription.reference}" /></td>
											</c:otherwise>
										</c:choose>
										<c:choose>
											<c:when test="${measure.status=='NA'}">
												<td title="${titleStatusNA}" data-real-value='NA'>${statusNA}</td>
											</c:when>
											<c:when test="${measure.status=='AP'}">
												<td title="${titleStatusAP}" data-real-value='AP'>${statusAP}</td>
											</c:when>
											<c:otherwise>
												<td title="${titleStatusM}" data-real-value='M'>${statusM}</td>
											</c:otherwise>
										</c:choose>
										<td><fmt:formatNumber value="${implementationRateValue}" maxFractionDigits="0" minFractionDigits="0" /></td>
										<td>${measure.phase.number}</td>
										<td><spring:message text="${!empty measureDescriptionText? measureDescriptionText.domain : ''}" /></td>
								</c:forEach>
							</tbody>
						</table>
					</div>
					<div id="tab_add_measure" class="tab-pane" style="padding-top: 5px;">
						<div class="form-group">
							<label class="label-control col-sm-4"><spring:message code="label.measure.norm" /></label>
							<div class="col-sm-8">
								<select name="standard" id="riskProfileStandardSelector" class="form-control">
									<option disabled="disabled" value="-1" selected="selected"><spring:message code="label.action.choose" /></option>
									<c:forEach items="${standards}" var="standard">
										<option value="${standard.id}"><spring:message text="${standard.label}" /></option>
									</c:forEach>
								</select>
							</div>
						</div>
						<table class="table table-hover" id="riskProfileStandardMeasureContainer">
							<thead>
								<tr>
									<th style="width: 5%;" title='<spring:message code="label.action" />'><spring:message code="label.action" /></th>
									<th style="width: 2%;" title='<spring:message code="label.measure.norm" />'><spring:message code="label.measure.norm" /></th>
									<th style="width: 3%;" title='<spring:message code="label.reference" />'><spring:message code="label.measure.ref" /></th>
									<th style="width: 2%;" title='<spring:message code="label.title.measure.status" />'><spring:message code="label.measure.status" /></th>
									<th style="width: 2%;" title='<spring:message code="label.title.measure.ir" />'><spring:message code="label.measure.ir_no_unit" /></th>
									<th style="width: 2%;" title='<spring:message code="label.title.measure.phase" />'><spring:message code="label.measure.phase" /></th>
									<th title='<spring:message code="label.measure.domain" />'><spring:message code="label.measure.domain" /></th>
								</tr>
							</thead>
							<tbody></tbody>
						</table>
					</div>
				</div>
			</div>
			<div class="modal-footer">
				<button type="button" name="cancel" class="btn btn-default" data-dismiss="modal">
					<spring:message code="label.action.close" />
				</button>
				<button type="button" name="save" class="btn btn-primary">
					<spring:message code="label.action.save" />
				</button>
			</div>
		</div>
	</div>
</div>