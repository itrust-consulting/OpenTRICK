<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="section" id="section_measure" style="z-index: 3">
	<div class="page-header">
		<h3 id="Measure">
			<spring:message code="label.measure" text="Measures" />
		</h3>
	</div>
	<c:if test="${empty(measureSplited)}">
		<spring:eval expression="T(lu.itrust.business.component.MeasureManager).SplitByNorm(measures)" var="measureSplited" />
	</c:if>
	<c:forEach items="${measureSplited.keySet()}" var="norm">
		<span class="anchor" id="anchorMeasure_${norm}"></span>
		<div id="section_measure_${norm}">
			<div class="panel panel-default">
				<div class="panel-heading">
					<spring:message code="label.measure.${norm}" text="${norm}" />
				</div>
				<div class="panel-body panelbodydefinition">
					<table class="tableheader">
						<tr>
							<td>
								<table class="table tableheaderrow">
									<tr>
										<th><spring:message code="label.table.index" text="#" /></th>
										<th><spring:message code="label.measure.domain" text="Domain" /></th>
										<th class="textaligncenter cellwidth_80"><spring:message code="label.measure.st" text="Status" /></th>
										<th class="textaligncenter cellwidth_80"><spring:message code="label.measure.ir" text="IR (%)" /></th>
										<th class="textaligncenter cellwidth_80"><spring:message code="label.measure.iw" text="IW (md)" /></th>
										<th class="textaligncenter cellwidth_80"><spring:message code="label.measure.ew" text="EW (md)" /></th>
										<th class="textaligncenter cellwidth_80"><spring:message code="label.measure.inv" text="INV" /> (k&euro;)</th>
										<th class="textaligncenter cellwidth_80"><spring:message code="label.measure.lt" text="LT (y)" /></th>
										<th class="textaligncenter cellwidth_80"><spring:message code="label.measure.mt" text="MT (%)" /></th>
										<th class="textaligncenter cellwidth_80"><spring:message code="label.measure.cs" text="CS" /> (k&euro;)</th>
										<th class="textaligncenter cellwidth_80"><spring:message code="label.measure.phase" text="Phase" /></th>
										<th><spring:message code="label.measure.comment" text="Comment" /></th>
										<th><spring:message code="label.measure.todo" text="To do" /></th>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td>
								<table class="table table-hover tablescrollarea">
									<tbody>
										<c:forEach items="${measureSplited.get(norm)}" var="measure">
											<c:choose>
												<c:when test="${measure.computable==false }">
													<tr style="background-color: LightGray">
														<c:set var="measureDescriptionText" value="${measure.measureDescription.getMeasureDescriptionTextByAlpha3(language)}" />
														<td><spring:message text="${measure.measureDescription.reference}" /></td>
														<td colspan="12"><spring:message text="${!empty measureDescriptionText? measureDescriptionText.domain : ''}" /></td>
													</tr>
												</c:when>
												<c:otherwise>
													<tr trick-class="Measure" trick-id="${measure.id}"
														trick-callback="reloadMeasureRow('${measure.id}','${norm}');initialiseTableFixedHeaderRows('#section_measure_${norm}');">
														<c:set var="measureDescriptionText" value="${measure.measureDescription.getMeasureDescriptionTextByAlpha3(language)}" />
														
														<td>
															<a href="#" class="descriptiontooltip" data-toggle="tooltip" title="<spring:message text="${!empty measureDescriptionText? measureDescriptionText.description : ''}" />">
																<spring:message text="${measure.measureDescription.reference}" />
															</a>
														</td>
														<td style="width: 650px;"><spring:message text="${!empty measureDescriptionText? measureDescriptionText.domain : ''}" /></td>
														<td class="success textaligncenter cellwidth_80" trick-field="status" trick-choose="M,AP,NA" trick-field-type="string" ondblclick="return editField(this);"><spring:message
																text="${measure.status}" /></td>
														<c:choose>
															<c:when test="${norm.equalsIgnoreCase('Custom')==true}">
																<td class="success textaligncenter cellwidth_80" trick-field="implementationRate" trick-field-type="double" ondblclick="return editField(this);"><spring:message
																		text="${measure.getImplementationRateValue()}" /></td>
															</c:when>
															<c:when test="${norm.equalsIgnoreCase('Maturity')==false}">
																<td class="success textaligncenter cellwidth_80" trick-field="implementationRate" trick-field-type="double"
																	trick-callback="reloadMeausreAndCompliance('${norm}','${measure.id}')" ondblclick="return editField(this);"><spring:message
																		text="${measure.getImplementationRateValue()}" /></td>
															</c:when>
															<c:otherwise>
																<td class="success textaligncenter cellwidth_80" trick-field="implementationRate" trick-field-type="double" ondblclick="return editField(this);"
																	trick-class="MaturityMeasure" trick-id="${measure.id}"><spring:message text="${measure.getImplementationRateValue()}" /></td>
															</c:otherwise>
														</c:choose>
														<td class="success textaligncenter cellwidth_80" trick-field="internalWL" trick-field-type="double" ondblclick="return editField(this);"><spring:message
																text="${measure.internalWL}" /></td>
														<td class="success textaligncenter cellwidth_80" trick-field="externalWL" trick-field-type="double" ondblclick="return editField(this);"><spring:message
																text="${measure.externalWL}" /></td>
														<td class="success textaligncenter cellwidth_80" trick-field="investment" trick-field-type="double" ondblclick="return editField(this);"><spring:message
																text="${measure.investment}" /></td>
														<td class="success textaligncenter cellwidth_80" trick-field="lifetime" trick-field-type="double" ondblclick="return editField(this);"><spring:message
																text="${measure.lifetime}" /></td>
														<td class="success textaligncenter cellwidth_80" trick-field="maintenance" trick-field-type="double" ondblclick="return editField(this);"><spring:message
																text="${measure.maintenance}" /></td>
														<td ${measure.cost == 0? "class='danger'" : "" } title="${measure.cost}"><fmt:formatNumber value="${measure.cost*0.001}" maxFractionDigits="0" /></td>
														<td class="success textaligncenter cellwidth_80" trick-field="phase" trick-field-type="integer" ondblclick="return editField(this);"
															trick-callback-pre="extractPhase(this)" trick-real-value='${measure.phase.number}'><c:choose>
																<c:when test="${measure.phase.number == 0}">
																NA
															</c:when>
																<c:otherwise>
																${measure.phase.number}
															</c:otherwise>
															</c:choose></td>
														<td class="success" trick-field="comment" trick-content="text" trick-field-type="string" ondblclick="return editField(this);"><div class="cellwidth_250">
																<spring:message text="${measure.comment}" />
															</div></td>
														<td class="success" trick-field="toDo" trick-content="text" trick-field-type="string" ondblclick="return editField(this);"><div class="cellwidth_250">
																<spring:message text="${measure.toDo}" />
															</div></td>
													</tr>
												</c:otherwise>
											</c:choose>
										</c:forEach>
									</tbody>
								</table>
							</td>
						</tr>
					</table>
				</div>
			</div>
		</div>
	</c:forEach>
</div>