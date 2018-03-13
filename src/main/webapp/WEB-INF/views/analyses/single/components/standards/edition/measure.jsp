<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fct" uri="https://trickservice.com/tags/functions"%>
<fmt:setLocale value="fr" scope="session" />
<div id="measure-ui" class='col-md-10 trick-ui' data-trick-id='${selectedMeasure.id}'>
	<div class="page-header tab-content-header hidden-xs">
		<div class="container">
			<div class="row-fluid">
				<h3>
					<c:choose>
						<c:when test="${isLinkedToProject and not empty selectedMeasure.ticket}">
							<spring:eval expression="T(lu.itrust.business.TS.model.ticketing.builder.ClientBuilder).TicketLink(ticketingName.toLowerCase(),ticketingURL,selectedMeasure.ticket)"
								var="ticketLink" />
							<a href="${ticketLink}" target="_titck_ts" class='btn-link'><spring:message text='${measureDescription.reference} - ${measureDescriptionText.domain}' /> <i
								class="fa fa-external-link" aria-hidden="true"></i></a>
						</c:when>
						<c:otherwise>
							<spring:message text='${measureDescription.reference} - ${measureDescriptionText.domain}' />
						</c:otherwise>
					</c:choose>
				</h3>
			</div>
		</div>
	</div>
	<c:if test="${not empty selectedMeasure }">
		<c:set var="rowSize" value="${isMaturity? 9 : 5}" />
		<fieldset style="display: block; width: 100%; clear: left;">
			<spring:message text="${fn:trim(measureDescriptionText.description)}" var="description" />
			<spring:message text="${measureDescriptionText.language.alpha2}" var="lang" />
			<c:if test="${otherMeasureDescriptionText}">
			<spring:url value="/images/flags/en.png" var="en_enabled"/>
			<spring:url value="/images/flags/fr.png"  var="fr_enabled" />
			<spring:url value="/images/flags/en_disabled.png" var="en_disabled"/>
			<spring:url value="/images/flags/fr_disabled.png" var="fr_disabled"/>
			<div id='description-switch-language' class='btn-group pull-right'>
				<button type="button" class="btn btn-xs btn-link" ${lang=='FR'? '' : 'disabled'}  lang="EN" data-flag-disabled='${en_disabled}' data-flag-enabled='${en_enabled}' ><img alt="English" src="${lang=='FR'? en_enabled : en_disabled}"></button>
				<button type="button" class="btn btn-xs btn-link" ${lang=='FR'? 'disabled' : ''} lang="FR" data-flag-disabled='${fr_disabled}' data-flag-enabled='${fr_enabled}' ><img alt="Français" src="${lang=='FR'? fr_disabled : fr_enabled}"></button>
			</div>
			</c:if>
			<div id="description" class='well well-sm form-no-fill' style="word-wrap: break-word; white-space: pre-wrap; resize: vertical; overflow: auto; height: 129px;">${description}</div>
		</fieldset>
		<fieldset>
			<c:if test="${measureDescription.computable}">
				<fmt:formatNumber value="${selectedMeasure.internalWL}" maxFractionDigits="2" var="internalWL" />
				<fmt:formatNumber value="${selectedMeasure.externalWL}" maxFractionDigits="2" var="externalWL" />
				<fmt:formatNumber value="${fct:round(selectedMeasure.investment,0)}" maxFractionDigits="0" var="investment" />
				<fmt:formatNumber maxFractionDigits="0" value="${fct:round(measure.investment*0.001,0)}" var="kInvestment" />
				<fmt:formatNumber value="${selectedMeasure.lifetime}" maxFractionDigits="2" var="lifetime" />
				<fmt:formatNumber value="${selectedMeasure.internalMaintenance}" maxFractionDigits="2" var='internalMaintenance' />
				<fmt:formatNumber value="${selectedMeasure.externalMaintenance}" maxFractionDigits="2" var='externalMaintenance' />
				<fmt:formatNumber value="${fct:round(selectedMeasure.recurrentInvestment,0)}" maxFractionDigits="0" var="recurrentInvestment" />
				<fmt:formatNumber value="${fct:round(selectedMeasure.recurrentInvestment*0.001,0)}" maxFractionDigits="0" var="kRecurrentInvestment" />
				<fmt:formatNumber value="${fct:round(selectedMeasure.cost,0)}" maxFractionDigits="0" var="cost" />
				<fmt:formatNumber value="${fct:round(selectedMeasure.cost*0.001,0)}" maxFractionDigits="0" var="kCost" />
				<c:set var="implementationRate">
					<c:choose>
						<c:when test="${isMaturity}">
							<select name="implementationRate" class='form-control' data-trick-value='${selectedMeasure.implementationRate.id}' data-trick-type='integer'>
								<c:forEach items="${impscales}" var="parameter">
									<option value="${parameter.id}" ${selectedMeasure.implementationRate==parameter?'selected':''}><fmt:formatNumber value=" ${parameter.value}" maxFractionDigits="0" /></option>
								</c:forEach>
							</select>
						</c:when>
						<c:otherwise>
							<fmt:formatNumber value="${selectedMeasure.implementationRate}" maxFractionDigits="0" var="implementationRateValue" />
							<select name="implementationRate" class='form-control' data-trick-value='${implementationRateValue}' data-trick-type='double'>
								<c:forEach begin="0" step="1" end="100" var="implValue">
									<option value="${implValue}" ${implementationRateValue==implValue?'selected':''}>${implValue}</option>
								</c:forEach>
							</select>
						</c:otherwise>
					</c:choose>
				</c:set>
			</c:if>
			<spring:message code="label.measure.status.m" var="statusM" />
			<spring:message code="label.measure.status.ap" var="statusAP" />
			<spring:message code="label.measure.status.na" var="statusNA" />
			<spring:message code="label.title.measure.status.m" var="titleStatusM" />
			<spring:message code="label.title.measure.status.ap" var="titleStatusAP" />
			<spring:message code="label.title.measure.status.na" var="titleStatusNA" />
			<spring:message code="label.metric.year" var="metricYear" />
			<spring:message code="label.metric.euro" var="metricEuro" />
			<spring:message code="label.metric.keuro" var="metricKEuro" />
			<spring:message code="label.metric.man_day" var="metricMd" />
			<div class='form-group'>
				<table class="table table-condensed form-no-fill">
					<thead>
						<tr class="form-group-fill">
							<th colspan="2" style="text-align: center;"><spring:message code="label.actual.status" /></th>
							<th colspan="4" style="text-align: center;"><spring:message code="label.initial.setup" /></th>
							<th colspan="3" style="text-align: center;"><spring:message code="label.maintenance" /></th>
							<th colspan="3" style="text-align: center;"><spring:message code="label.planning" /></th>
						</tr>
						<tr class="form-group-fill">
							<th title='<spring:message code="label.title.measure.status" />' style="width: 1%; min-width: 60px;"><spring:message code="label.title.measure.status" /></th>
							<th title='<spring:message code="label.title.measure.ir" />' style="width: 1%; min-width: 50px; border-right: 1px solid #ddd"><spring:message code="label.implement" /></th>
							<th title='<spring:message code="label.title.measure.iw" />' style="width: 1%; min-width: 60px;"><spring:message code="label.title.measure.iw" /></th>
							<th title='<spring:message code="label.title.measure.ew" />' style="width: 1%; min-width: 60px;"><spring:message code="label.title.measure.ew" /></th>
							<th title='<spring:message code="label.title.measure.inv" />' style="width: 1%; min-width: 60px;"><spring:message code="label.title.measure.inv" /></th>
							<th title='<spring:message code="label.title.measure.lt" />' style="width: 1%; min-width: 60px; border-right: 1px solid #ddd"><spring:message
									code="label.title.measure.lt" /></th>
							<th title='<spring:message code="label.title.measure.im" />' style="width: 1%; min-width: 60px;"><spring:message code="label.internal" /></th>
							<th title='<spring:message code="label.title.measure.em" />' style="width: 1%; min-width: 60px;"><spring:message code="label.external" /></th>
							<th title='<spring:message code="label.title.measure.ri" />' style="width: 1%; min-width: 60px; border-right: 1px solid #ddd"><spring:message code="label.recurrent" /></th>
							<th title='<spring:message code="label.title.measure.cost" />' style="width: 1%; min-width: 60px;"><spring:message code="label.title.measure.cost" /></th>
							<th title='<spring:message code="label.title.measure.phase" />' style="width: 1%; min-width: 60px;"><spring:message code="label.title.measure.phase" /></th>
							<th title='<spring:message code="label.title.measure.responsible" />' style="width: 2%"><spring:message code="label.title.measure.responsible" /></th>
						</tr>
					</thead>
					<tbody>
						<c:choose>
							<c:when test="${measureDescription.computable}">
								<tr>
									<td><select class='form-control' name="status" data-trick-value='${selectedMeasure.status}' data-trick-type='string'>
											<option value='NA' ${selectedMeasure.status=='NA'?'selected' : ''} title='${titleStatusNA}'>${statusNA}</option>
											<option value='AP' ${selectedMeasure.status=='AP'?'selected' : ''} title='${titleStatusAP}'>${statusAP}</option>
											<option value='M' ${selectedMeasure.status=='M'?'selected' : ''} title='${titleStatusM}'>${statusM}</option>
									</select></td>
									<td style="border-right: 1px solid #ddd"><div class="input-group">
											<span class="input-group-addon">%</span> ${implementationRate}
										</div></td>
									<td><div class="input-group">
											<span class="input-group-addon">${metricMd}</span> <input name="internalWL" value="${internalWL}" class="form-control numeric" placeholder="${internalWL}"
												data-trick-type='double'>
										</div></td>
									<td><div class="input-group">
											<span class="input-group-addon">${metricMd}</span><input name="externalWL" value="${externalWL}" class="form-control numeric" placeholder="${externalWL}"
												data-trick-type='double'>
										</div></td>
									<td><div class="input-group">
											<span class="input-group-addon">${metricKEuro}</span><input name="investment" value="${kInvestment}" title="${investment}${metricEuro}" class="form-control numeric"
												placeholder="${kInvestment}" data-trick-type='double'>
										</div></td>
									<td style="border-right: 1px solid #ddd"><div class="input-group" align="right">
											<span class="input-group-addon">${metricYear}</span><input name="lifetime" value="${lifetime}" class="form-control numeric" placeholder="${lifetime}"
												data-trick-type='double'>
										</div></td>
									<td><div class="input-group">
											<span class="input-group-addon">${metricMd}</span><input name="internalMaintenance" value="${internalMaintenance}" class="form-control numeric"
												placeholder="${internalMaintenance}" data-trick-type='double'>
										</div></td>
									<td><div class="input-group">
											<span class="input-group-addon">${metricMd}</span><input name="externalMaintenance" value="${externalMaintenance}" class="form-control numeric"
												placeholder="${externalMaintenance}" data-trick-type='double'>
										</div></td>
									<td style="border-right: 1px solid #ddd"><div class="input-group" align="right">
											<span class="input-group-addon">${metricKEuro}</span><input name="recurrentInvestment" value="${kRecurrentInvestment}" title="${recurrentInvestment}${metricEuro}"
												class="form-control numeric" placeholder="${kRecurrentInvestment}" data-trick-type='double'>
										</div></td>
									<td><div class="input-group ${selectedMeasure.status!='NA' && selectedMeasure.cost==0?'has-error':''}">
											<span class="input-group-addon">${metricKEuro}</span><input name="cost" value="${kCost}" readonly="readonly" title="${cost}${metricEuro}" class="form-control numeric">
										</div></td>
									<td><select name='phase' class="form-control" style="padding-left: 6px; padding-right: 6px" data-trick-value='${selectedMeasure.phase.id}' data-trick-type='integer'>
											<c:forEach items="${phases}" var="phase">
												<option value="${phase.id}" ${selectedMeasure.phase.number == phase.number?'selected':''}>${phase.number}</option>
											</c:forEach>
									</select></td>
									<td><spring:message text="${selectedMeasure.responsible}" var="responsible" /> <input name="responsible" class="form-control" value='${responsible}'
										placeholder="${responsible}" data-trick-type='string'></td>
								</tr>
							</c:when>
							<c:otherwise>
								<tr>
									<td><select class='form-control' name="status" disabled="disabled"></select></td>
									<td style="border-right: 1px solid #ddd"><div class="input-group">
											<span class="input-group-addon">%</span> <select class="form-control" disabled="disabled"></select>
										</div></td>
									<td><div class="input-group">
											<span class="input-group-addon">${metricMd}</span> <input name="internalWL" class="form-control numeric" disabled="disabled">
										</div></td>
									<td><div class="input-group">
											<span class="input-group-addon">${metricMd}</span><input name="externalWL" class="form-control numeric" disabled="disabled">
										</div></td>
									<td><div class="input-group">
											<span class="input-group-addon">${metricKEuro}</span><input name="investment" class="form-control numeric" disabled="disabled">
										</div></td>
									<td style="border-right: 1px solid #ddd"><div class="input-group" align="right">
											<span class="input-group-addon">${metricYear}</span><input name="lifetime" class="form-control numeric" disabled="disabled">
										</div></td>
									<td><div class="input-group">
											<span class="input-group-addon">${metricMd}</span><input name="internalMaintenance" class="form-control numeric" disabled="disabled">
										</div></td>
									<td><div class="input-group">
											<span class="input-group-addon">${metricMd}</span><input name="externalMaintenance" class="form-control numeric" disabled="disabled">
										</div></td>
									<td style="border-right: 1px solid #ddd"><div class="input-group" align="right">
											<span class="input-group-addon">${metricKEuro}</span><input name="recurrentInvestment" class="form-control numeric" disabled="disabled">
										</div></td>
									<td><div class="input-group">
											<span class="input-group-addon">${metricKEuro}</span><input name="cost" readonly="readonly" class="form-control numeric" disabled="disabled">
										</div></td>
									<td><select name='phase' class="form-control" style="padding-left: 6px; padding-right: 6px" disabled="disabled"></select></td>
									<td><spring:message text="${selectedMeasure.responsible}" var="responsible" /> <input name="responsible" class="form-control" disabled="disabled"></td>
								</tr>
							</c:otherwise>
						</c:choose>
					</tbody>
				</table>
			</div>
			<c:if test="${not isMaturity}">
				<div class='form-group form-group-fill'>
					<spring:message code="label.measure.tocheck" var='tocheck' />
					<spring:message text="${selectedMeasure.toCheck}" var="toCheckContent" />
					<label class='label-control form-group-fill'>${tocheck}</label>
					<textarea rows="${rowSize}" class="form-control" name="toCheck" title="${tocheck}" style="resize: vertical;" placeholder="${toCheckContent}" data-trick-type='string'>${toCheckContent}</textarea>
				</div>
			</c:if>
			<div class='form-group form-group-fill'>
				<spring:message code="label.comment" var='comment' />
				<spring:message text="${selectedMeasure.comment}" var="commentContent" />
				<label class='label-control'>${comment}</label>
				<textarea rows="${rowSize}" class="form-control" name="comment" title="${comment}" style="resize: vertical;" placeholder="${commentContent}" data-trick-type='string'>${commentContent}</textarea>
			</div>
			<div class='form-group form-group-fill'>
				<spring:message code="label.measure.todo" var='todo' />
				<label class='label-control'>${todo}</label>
				<spring:message text="${selectedMeasure.toDo}" var="todoContent" />
				<c:choose>
					<c:when test="${showTodo}">
						<textarea rows="${rowSize}" class="form-control" name="toDo" title="${todo}" style="resize: vertical;" placeholder="${todoContent}" data-trick-type='string'>${todoContent}</textarea>
					</c:when>
					<c:otherwise>
						<textarea rows="${rowSize}" class="form-control" name="toDo" style="resize: vertical;" disabled="disabled"></textarea>
					</c:otherwise>
				</c:choose>
			</div>
		</fieldset>
	</c:if>
</div>