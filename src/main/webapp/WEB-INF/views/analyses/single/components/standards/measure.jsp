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
		<c:set var="measureDescription" value="${selectedMeasure.measureDescription}" />
		<c:set var="measureDescriptionText" value="${measureDescription.getMeasureDescriptionTextByAlpha2(language)}" />
		<c:set var="isMaturity" value="${measureDescription.standard.type == 'MATURITY'}" />
		<c:set var="showToCheck" value="${measureDescription.computable and not isMaturity}" />
		<c:set var="rowSize" value="${showToCheck? 5 : 10}" />
		<div class="col-md-12">
			<div class='form-group'>
				<h3>
					<spring:message text='${measureDescription.reference} - ${measureDescriptionText.domain}' />
				</h3>
				<textarea rows="8" class="form-control" readonly="readonly" style="resize: vertical;"><spring:message text="${fn:trim(measureDescriptionText.description)}" /></textarea>
			</div>
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
				<fmt:setLocale value="${language}" scope="session" />
				<div class='form-group'>
					<table class="table">
						<thead>
							<tr>
								<th title='<fmt:message key="label.title.measure.status" />' width="6%"><fmt:message key="label.title.measure.status" /></th>
								<th title='<fmt:message key="label.title.measure.ir" />' width="6%" ><fmt:message key="label.measure.ir" /></th>
								<th title='<fmt:message key="label.title.measure.iw" />'><fmt:message key="label.title.measure.iw" /></th>
								<th title='<fmt:message key="label.title.measure.ew" />'><fmt:message key="label.title.measure.ew" /></th>
								<th title='<fmt:message key="label.title.measure.inv" />'><fmt:message key="label.title.measure.inv" /></th>
								<th title='<fmt:message key="label.title.measure.lt" />'><fmt:message key="label.title.measure.lt" /></th>
								<th title='<fmt:message key="label.title.measure.im" />'><fmt:message key="label.title.measure.im" /></th>
								<th title='<fmt:message key="label.title.measure.em" />'><fmt:message key="label.title.measure.em" /></th>
								<th title='<fmt:message key="label.title.measure.ri" />'><fmt:message key="label.title.measure.ri" /></th>
								<th title='<fmt:message key="label.title.measure.cost" />'><fmt:message key="label.title.measure.cost" /></th>
								<th title='<fmt:message key="label.title.measure.phase" />' width="8%"><fmt:message key="label.title.measure.phase" /></th>
								<th title='<fmt:message key="label.title.measure.responsible" />'><fmt:message key="label.title.measure.responsible" /></th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<td><select class='form-control' name="status">
										<option value='NA' ${selectedMeasure.status=='NA'}>NA</option>
										<option value='AP' ${selectedMeasure.status=='AP'}>AP</option>
										<option value='M' ${selectedMeasure.status=='M'}>M</option>
								</select></td>
								<td><select name="implementationRate" class='form-control'></select></td>
								<td><input name="internalWL" value="${internalWL}" class="form-control" placeholder="${internalWL}"></td>
								<td><input name="externalWL" value="${externalWL}" class="form-control" placeholder="${externalWL}"></td>
								<td><input name="investment" value="${kInvestment}" title="${investment}" class="form-control" placeholder="${kInvestment}"></td>
								<td><input name="lifetime" value="${lifetime}" class="form-control" placeholder="${lifetime}"></td>
								<td><input name="internalMaintenance" value="${internalMaintenance}" class="form-control" placeholder="${internalMaintenance}"></td>
								<td><input name="externalMaintenance" value="${externalMaintenance}" class="form-control" placeholder="${externalMaintenance}"></td>
								<td><input name="recurrentInvestment" value="${kRecurrentInvestment}" title="${recurrentInvestment}" class="form-control" placeholder="${kRecurrentInvestment}"></td>
								<td><input name="cost" value="${kCost}" title="${cost}" class="form-control" placeholder="${kCost}"></td>
								<td><select name='phase' class="form-control">
										<c:forEach items="${phases}" var="phase">
											<option value="${phase.number}" ${selectedMeasure.phase.number == phase.number?'selected':''}><fmt:message key='label.index.phase'>
													<fmt:param value="${phase.number}" />
												</fmt:message></option>
										</c:forEach>
								</select></td>
								<td><input name="responsible" class="form-control" value='<spring:message text="${selectedMeasure.responsible}" />'></td>
							</tr>
						</tbody>
					</table>
				</div>
			</c:if>

			<div class='form-group'>
				<fmt:message key="label.comment" var='comment' />
				<label class='label-control'>${comment}</label>
				<textarea rows="${rowSize}" class="form-control" name="comment" title="${comment}" style="resize: vertical;"><spring:message text="${selectedMeasure.comment}" /></textarea>
			</div>

			<div class='form-group'>
				<fmt:message key="label.measure.todo" var='todo' />
				<label class='label-control'>${todo}</label>
				<textarea rows="${rowSize}" class="form-control" name="comment" title="${todo}" style="resize: vertical;"><spring:message text="${selectedMeasure.toDo}" /></textarea>
			</div>
			<c:if test="${showToCheck}">
				<div class='form-group'>
					<fmt:message key="label.measure.tocheck" var='tocheck' />
					<label class='label-control'>${tocheck}</label>
					<textarea rows="5" class="form-control" name="comment" title="${tocheck}" style="resize: vertical;"><spring:message text="${selectedMeasure.toCheck}" /></textarea>
				</div>
			</c:if>

		</div>
	</c:if>
</div>