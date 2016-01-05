<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fct" uri="http://trickservice.itrust.lu/JSTLFunctions"%>
<div id="measure-ui" class='col-lg-10' data-trick-id='${selectedMeasure.id}'>
	<c:if test="${not empty selectedMeasure }">
		<c:set var="rowSize" value="${showTodo ? (isMaturity? 10 : 5) : (isMaturity ? 25: 10)}" />
		<div class="col-md-12">
			<fieldset class="col-xs-12">
				<legend>
					<spring:message text='${measureDescription.reference} - ${measureDescriptionText.domain}' />
				</legend>
				<spring:message text="${fn:trim(measureDescriptionText.description)}" var="description" />
				<c:if test="${not empty description }">
					<div id="description" data-default-height="${39+(countLine>6? 5 : (countLine-1))*18}" class='well well-sm'
						style="word-wrap: break-word; white-space: pre-wrap; resize: vertical; overflow: auto; ${countLine>6? 'height:129px':''};">${description}</div>
				</c:if>
			</fieldset>
			<fieldset class="col-xs-12">

				<c:if test="${measureDescription.computable}">

					<fmt:setLocale value="fr" scope="session" />
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
					<c:choose>
						<c:when test="${isMaturity}">
							<c:set var="implementationRate">
								<select name="implementationRate" class='form-control' data-trick-value='${selectedMeasure.implementationRate.id}' data-trick-type='integer'>
									<c:forEach items="${impscales}" var="parameter">
										<option value="${parameter.id}" ${selectedMeasure.implementationRate==parameter?'selected':''}><fmt:formatNumber value=" ${parameter.value}"
												maxFractionDigits="0" /></option>
									</c:forEach>
								</select>
							</c:set>
						</c:when>
						<c:otherwise>
							<fmt:formatNumber value="${selectedMeasure.implementationRate}" maxFractionDigits="0" var="implementationRate" />
						</c:otherwise>
					</c:choose>
					<fmt:setLocale value="${language}" scope="session" />
					<fmt:message key="label.metric.year" var="metricYear" />
					<fmt:message key="label.metric.euro" var="metricEuro" />
					<fmt:message key="label.metric.keuro" var="metricKEuro" />
					<fmt:message key="label.metric.man_day" var="metricMd" />
					<div class='form-group'>
						<table class="table table-condensed">
							<thead>
								<tr>
									<th colspan="2" style="text-align: center;"><fmt:message key="label.actual.status" /></th>
									<th colspan="4" style="text-align: center;"><fmt:message key="label.initial.setup" /></th>
									<th colspan="3" style="text-align: center;"><fmt:message key="label.maintenance" /></th>
									<th colspan="3" style="text-align: center;"><fmt:message key="label.planning" /></th>
								</tr>
								<tr>
									<th title='<fmt:message key="label.title.measure.status" />' style="width: 2%; min-width: 60px;"><fmt:message key="label.title.measure.status" /></th>
									<c:choose>
										<c:when test="${isMaturity}">
											<th title='<fmt:message key="label.title.measure.ir" />' style="width: 1%; min-width: 60px; border-right: 2px solid #ddd"><fmt:message key="label.implementation" /></th>
										</c:when>
										<c:otherwise>
											<th title='<fmt:message key="label.title.measure.ir" />' style="width: 1%; min-width: 50px; border-right: 2px solid #ddd"><fmt:message key="label.implement" /></th>
										</c:otherwise>
									</c:choose>
									<th title='<fmt:message key="label.title.measure.iw" />' style="width: 2%; min-width: 60px;"><fmt:message key="label.title.measure.iw" /></th>
									<th title='<fmt:message key="label.title.measure.ew" />' style="width: 2%; min-width: 60px;"><fmt:message key="label.title.measure.ew" /></th>
									<th title='<fmt:message key="label.title.measure.inv" />' style="width: 1%; min-width: 60px;"><fmt:message key="label.title.measure.inv" /></th>
									<th title='<fmt:message key="label.title.measure.lt" />' style="width: 2%; min-width: 60px; border-right: 2px solid #ddd"><fmt:message key="label.title.measure.lt" /></th>
									<th title='<fmt:message key="label.title.measure.im" />' style="width: 2%; min-width: 60px;"><fmt:message key="label.internal" /></th>
									<th title='<fmt:message key="label.title.measure.em" />' style="width: 2%; min-width: 60px;"><fmt:message key="label.external" /></th>
									<th title='<fmt:message key="label.title.measure.ri" />' style="width: 2%; min-width: 60px; border-right: 2px solid #ddd"><fmt:message key="label.recurrent" /></th>
									<th title='<fmt:message key="label.title.measure.cost" />' style="width: 2%; min-width: 60px;"><fmt:message key="label.title.measure.cost" /></th>
									<th title='<fmt:message key="label.title.measure.phase" />' style="width: 2%; min-width: 60px;"><fmt:message key="label.title.measure.phase" /></th>
									<th title='<fmt:message key="label.title.measure.responsible" />' style="width: 3%"><fmt:message key="label.title.measure.responsible" /></th>
								</tr>
							</thead>
							<tbody>
								<tr>
									<td><select class='form-control' name="status" data-trick-value='${selectedMeasure.status}'>
											<option value='NA' ${selectedMeasure.status=='NA'?'selected' : ''}>NA</option>
											<option value='AP' ${selectedMeasure.status=='AP'?'selected' : ''}>AP</option>
											<option value='M' ${selectedMeasure.status=='M'?'selected' : ''}>M</option>
									</select></td>
									<td style="border-right: 2px solid #ddd"><div class="input-group">
											<span class="input-group-addon">%</span>
											<c:choose>
												<c:when test="${isMaturity}">
													 ${implementationRate}
												</c:when>
												<c:otherwise>
													<input class="form-control numeric" name="implementationRate" value="${implementationRate}" placeholder="${implementationRate}" data-trick-type='integer' type="number"
														max="100" min="0">
												</c:otherwise>
											</c:choose>
										</div></td>
									<td><div class="input-group">
											<span class="input-group-addon">${metricMd}</span> <input name="internalWL" value="${internalWL}" class="form-control numeric" placeholder="${internalWL}" type="number"
												min="0" data-trick-type='integer'>
										</div></td>
									<td><div class="input-group">
											<span class="input-group-addon">${metricMd}</span><input name="externalWL" value="${externalWL}" class="form-control numeric" placeholder="${externalWL}" type="number"
												min="0" data-trick-type='double' >
										</div></td>
									<td><div class="input-group">
											<span class="input-group-addon">${metricKEuro}</span><input name="investment" value="${kInvestment}" title="${investment}${metricEuro}" class="form-control numeric"
												placeholder="${kInvestment}" type="number" min="0" data-trick-type='double' >
										</div></td>
									<td style="border-right: 2px solid #ddd"><div class="input-group" align="right">
											<span class="input-group-addon">${metricYear}</span><input name="lifetime" value="${lifetime}" class="form-control numeric" placeholder="${lifetime}" type="number"
												min="0" data-trick-type='double'>
										</div></td>
									<td><div class="input-group">
											<span class="input-group-addon">${metricMd}</span><input name="internalMaintenance" value="${internalMaintenance}" class="form-control numeric"
												placeholder="${internalMaintenance}" type="number" min="0" data-trick-type='double'>
										</div></td>
									<td><div class="input-group">
											<span class="input-group-addon">${metricMd}</span><input name="externalMaintenance" value="${externalMaintenance}" class="form-control numeric"
												placeholder="${externalMaintenance}" type="number" min="0" data-trick-type='double'>
										</div></td>
									<td style="border-right: 2px solid #ddd"><div class="input-group" align="right">
											<span class="input-group-addon">${metricKEuro}</span><input name="recurrentInvestment" value="${kRecurrentInvestment}" title="${recurrentInvestment}${metricEuro}"
												class="form-control numeric" placeholder="${kRecurrentInvestment}" type="number" min="0" data-trick-type='double'>
										</div></td>
									<td><div class="input-group">
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
							</tbody>
						</table>
					</div>

				</c:if>

				<div class='form-group'>
					<fmt:message key="label.comment" var='comment' />
					<spring:message text="${selectedMeasure.comment}" var="commentContent" />
					<label class='label-control'>${comment}</label>
					<textarea rows="${rowSize}" class="form-control" name="comment" title="${comment}" style="resize: vertical;" placeholder="${commentContent}" data-trick-type='string' >${commentContent}</textarea>
				</div>
				<c:if test="${showTodo}">
					<div class='form-group'>
						<fmt:message key="label.measure.todo" var='todo' />
						<label class='label-control'>${todo}</label>
						<spring:message text="${selectedMeasure.toDo}" var="todoContent" />
						<textarea rows="${rowSize}" class="form-control" name="toDo" title="${todo}" style="resize: vertical;" placeholder="${todoContent}" data-trick-type='string' >${todoContent}</textarea>
					</div>
				</c:if>
				<c:if test="${not isMaturity}">
					<div class='form-group'>
						<fmt:message key="label.measure.tocheck" var='tocheck' />
						<spring:message text="${selectedMeasure.toCheck}" var="toCheckContent" />
						<label class='label-control'>${tocheck}</label>
						<textarea rows="${rowSize}" class="form-control" name="toCheck" title="${tocheck}" style="resize: vertical;" placeholder="${toCheckContent}" data-trick-type='string' >${toCheckContent}</textarea>
					</div>
				</c:if>
			</fieldset>
		</div>
	</c:if>
</div>