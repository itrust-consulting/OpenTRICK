<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<div class="section" id="section_measure">
	<div class="page-header">
		<h3 id="Measure">
			<spring:message code="label.measure" text="Measures" />
		</h3>
	</div>
	<spring:eval
		expression="T(lu.itrust.business.component.MeasureManager).SplitByNorm(measures)"
		var="measureSplited" />
	<c:forEach items="${measureSplited.keySet()}" var="norm">

		<div class="panel panel-default" id="section_measure_${norm}">
			<div class="panel-heading">
				<spring:message code="label.measure.${norm}" text="${norm}" />
			</div>
			<div class="panel-body" style="max-height: 700px; overflow: auto;">
				<table class="table table-hover">
					<thead>
						<tr>
							<td><spring:message code="label.table.index" text="#" /></td>
							<td colspan="2"><spring:message code="label.measure.domain"
									text="Domain" /></td>
							<td><spring:message code="label.measure.st" text="Status" /></td>
							<td><spring:message code="label.measure.ir" text="IR (%)" /></td>
							<td><spring:message code="label.measure.iw" text="IW (md)" /></td>
							<td><spring:message code="label.measure.ew" text="EW (md)" /></td>
							<td><spring:message code="label.measure.inv" text="INV" />
								(k&euro;)</td>
							<td><spring:message code="label.measure.lt" text="LT (y)" /></td>
							<td><spring:message code="label.measure.mt" text="MT (%)" /></td>
							<td><spring:message code="label.measure.cs" text="CS" />
								(k&euro;)</td>
							<td><spring:message code="label.measure.phase" text="Phase" /></td>
							<td colspan="2"><spring:message code="label.measure.comment"
									text="Comment" /></td>
							<td colspan="2"><spring:message code="label.measure.todo"
									text="To do" /></td>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${measureSplited.get(norm)}" var="measure">
							<c:choose>
								<c:when test="${measure.measureDescription.level<3 }">
									<tr ${measure.measureDescription.level<2? "class='danger'" : "class='warning'" }>
										<td><spring:message
												text="${measure.measureDescription.reference}" /></td>
										<c:set var="measureDescriptionText"
											value="${measure.measureDescription.getMeasureDescriptionTextByAlpha3(language)}" />
										<td  colspan="14"><spring:message
												text="${!empty measureDescriptionText? measureDescriptionText.domain : ''}" /></td>
									</tr>
								</c:when>
								<c:otherwise>
									<tr trick-class="Measure" trick-id="${measure.id}" trick-callback="reloadMeasureRow('${measure.id}','${norm}')">
										<td><spring:message
												text="${measure.measureDescription.reference}" /></td>
										<c:set var="measureDescriptionText"
											value="${measure.measureDescription.getMeasureDescriptionTextByAlpha3(language)}" />
										<td colspan="2"><spring:message
												text="${!empty measureDescriptionText? measureDescriptionText.domain : ''}" /></td>

										<td class="success" trick-field="status"
											trick-choose="M,AP,NA" trick-field-type="string"
											ondblclick="return editField(this);"><spring:message
												text="${measure.status}" htmlEscape="true" /></td>
										<c:choose>
											<c:when test="${norm.equalsIgnoreCase('Custom')==true}">
												<td class="success" trick-field="implementationRate"
													trick-field-type="double"
													ondblclick="return editField(this);"><spring:message
														text="${measure.getImplementationRateValue()}" /></td>
											</c:when>
											<c:when test="${norm.equalsIgnoreCase('Maturity')==false}">
												<td class="success" trick-field="implementationRate"
													trick-field-type="double"
													trick-callback="reloadMeausreAndCompliance('${norm}','${measure.id}')"
													ondblclick="return editField(this);"><spring:message
														text="${measure.getImplementationRateValue()}" /></td>
											</c:when>
											<c:otherwise>
												<td class="success" trick-field="implementationRate"
													trick-field-type="double"
													ondblclick="return editField(this);"
													trick-class="MaturityMeasure" trick-id="${measure.id}"><spring:message
														text="${measure.getImplementationRateValue()}" /></td>
											</c:otherwise>
										</c:choose>
										<td class="success" trick-field="internalWL"
											trick-field-type="double"
											ondblclick="return editField(this);"><spring:message
												text="${measure.internalWL}" /></td>
										<td class="success" trick-field="externalWL"
											trick-field-type="double"
											ondblclick="return editField(this);"><spring:message
												text="${measure.externalWL}" /></td>
										<td class="success" trick-field="investment"
											trick-field-type="double"
											ondblclick="return editField(this);"><spring:message
												text="${measure.investment}" /></td>
										<td class="success" trick-field="lifetime"
											trick-field-type="double"
											ondblclick="return editField(this);"><spring:message
												text="${measure.lifetime}" /></td>
										<td class="success" trick-field="maintenance"
											trick-field-type="double"
											ondblclick="return editField(this);"><spring:message
												text="${measure.maintenance}" /></td>
										<td ${measure.cost == 0? "class='danger'" : "" } title="${measure.cost}" ><fmt:formatNumber
												value="${measure.cost*0.001}" maxFractionDigits="0" /></td>
										<td class="success" trick-field="phase"
											trick-field-type="integer"
											ondblclick="return editField(this);"
											trick-callback-pre="extractPhase(this)"
											trick-real-value='${measure.phase.number}'><c:choose>
												<c:when test="${measure.phase.number == 0}">
										NA
								</c:when>
												<c:otherwise>
								${measure.phase.number}
								</c:otherwise>
											</c:choose></td>
										<td colspan="2" class="success" trick-field="comment"
											trick-field-type="string"
											ondblclick="return editField(this);"><spring:message
												text="${measure.comment}" /></td>
										<td colspan="2" class="success" trick-field="toDo"
											trick-field-type="string"
											ondblclick="return editField(this);"><spring:message
												text="${measure.toDo}" /></td>
									</tr>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</c:forEach>
</div>
