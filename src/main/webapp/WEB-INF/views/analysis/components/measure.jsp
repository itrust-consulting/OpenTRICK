<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<fmt:setLocale value="fr_FR" scope="session" />
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
				<div class="panel-body autofitpanelbodydefinition">
					<table class="table table-hover headertofixtable" id="table_Measure_${norm}">
						<thead>
							<tr>
								<th style="width: 80px;"><spring:message code="label.table.index" text="#" /></th>
								<th style="width: 350px;"><spring:message code="label.measure.domain" text="Domain" /></th>
								<th class="textaligncenter cellwidth_80"><spring:message code="label.measure.st" text="Status" /></th>
								<th class="textaligncenter cellwidth_80"><spring:message code="label.measure.ir" text="IR (%)" /></th>
								<th class="textaligncenter cellwidth_80"><spring:message code="label.measure.iw" text="IW (md)" /></th>
								<th class="textaligncenter cellwidth_80"><spring:message code="label.measure.ew" text="EW (md)" /></th>
								<th class="textaligncenter cellwidth_80"><spring:message code="label.measure.inv" text="INV" /> (k&euro;)</th>
								<th class="textaligncenter cellwidth_80"><spring:message code="label.measure.lt" text="LT (y)" /></th>
								<th class="textaligncenter cellwidth_80"><spring:message code="label.measure.im" text="IM (md)" /></th>
								<th class="textaligncenter cellwidth_80"><spring:message code="label.measure.em" text="EM (md)" /></th>
								<th class="textaligncenter cellwidth_80"><spring:message code="label.measure.ri" text="RI" /> (k&euro;)</th>
								<th class="textaligncenter cellwidth_80"><spring:message code="label.measure.cs" text="CS" /> (k&euro;)</th>
								<th class="textaligncenter cellwidth_80"><spring:message code="label.measure.phase" text="Phase" /></th>
								<th><spring:message code="label.measure.comment" text="Comment" /></th>
								<th><spring:message code="label.measure.todo" text="To do" /></th>
								<c:if test="${measureSplited.get(norm).get(0).getClass().name.equals('lu.itrust.business.TS.NormMeasure')}">
									<th><spring:message code="label.measure.tocheck" text="To check" /></th>
								</c:if>
							</tr>
						</thead>
						<tfoot>
						</tfoot>
						<tbody>
							<c:set var="css" value="${measure.getImplementationRateValue()==100 || measure.getStatus().equals('NA')?'':'class=\"success\"'}" />
							<c:set var="csscentered" value="${measure.getImplementationRateValue()==100 || measure.getStatus().equals('NA')?'':'class=\"success textaligncenter\"'}" />
							<c:forEach items="${measureSplited.get(norm)}" var="measure">
								<c:choose>
									<c:when test="${measure.measureDescription.computable==false }">
										<tr style="background-color: LightGray">
											<c:set var="measureDescriptionText" value="${measure.measureDescription.getMeasureDescriptionTextByAlpha3(language)}" />
											<td><spring:message text="${measure.measureDescription.reference}" /></td>
											<td style="overflow: show; white-space: nowrap;"><spring:message text="${!empty measureDescriptionText? measureDescriptionText.domain : ''}" /></td>
											<td></td>
											<td></td>
											<td></td>
											<td></td>
											<td></td>
											<td></td>
											<td></td>
											<td></td>
											<td></td>
											<td></td>
											<td></td>
											<td></td>
											<td></td>
											<c:if test="${measure.getClass().name.equals('lu.itrust.business.TS.NormMeasure')}">
												<td></td>
											</c:if>
										</tr>
									</c:when>
									<c:otherwise>
										<tr trick-class="Measure" trick-id="${measure.id}" trick-callback="reloadMeasureRow('${measure.id}','${norm}');">
											<c:set var="measureDescriptionText" value="${measure.measureDescription.getMeasureDescriptionTextByAlpha3(language)}" />
											<td><a href="#" class="descriptiontooltip" data-toggle="tooltip" data-html="true"
												title="<spring:message text="${!empty measureDescriptionText? measureDescriptionText.description : ''}" />"> <spring:message
														text="${measure.measureDescription.reference}" />
											</a></td>
											<td><div class="headertofixtablelargecolumn">
													<spring:message text="${!empty measureDescriptionText? measureDescriptionText.domain : ''}" />
												</div></td>
											<td ${css} textaligncenter" trick-field="status" trick-choose="M,AP,NA" trick-field-type="string" ondblclick="return editField(this);"><spring:message
													text="${measure.status}" /></td>
											<c:choose>
												<c:when test="${norm.equalsIgnoreCase('Custom')==true}">
													<td ${css} textaligncenter" trick-field="implementationRate" trick-field-type="double" ondblclick="return editField(this);"><fmt:formatNumber
															value="${measure.getImplementationRateValue()}" maxFractionDigits="0" minFractionDigits="0" /></td>
												</c:when>
												<c:when test="${!norm.equalsIgnoreCase('Maturity')}">
													<td ${csscentered} trick-field="implementationRate" trick-field-type="double" trick-callback="reloadMeausreAndCompliance('${norm}','${measure.id}')"
														ondblclick="return editField(this);"><fmt:formatNumber value="${measure.getImplementationRateValue()}" maxFractionDigits="0" minFractionDigits="0" /></td>
												</c:when>
												<c:otherwise>
													<td ${csscentered} trick-field="implementationRate" trick-field-type="double" ondblclick="return editField(this);" trick-class="MaturityMeasure"
														trick-id="${measure.id}"><fmt:formatNumber value="${measure.getImplementationRateValue()}" maxFractionDigits="0" minFractionDigits="0" /></td>
												</c:otherwise>
											</c:choose>
											<c:set var="internalWL">
												<fmt:formatNumber value="${measure.internalWL}" maxFractionDigits="2" />
											</c:set>
											<td ${csscentered} trick-field="internalWL" trick-field-type="double" ondblclick="return editField(this);" real-value="${fn:replace(internalWL,',' ,'.') }">${internalWL}</td>
											<c:set var="externalWL">
												<fmt:formatNumber value="${measure.externalWL}" maxFractionDigits="2" />
											</c:set>
											<td ${csscentered} trick-field="externalWL" trick-field-type="double" ondblclick="return editField(this);" real-value="${fn:replace(externalWL,',' ,'.') }">${externalWL}</td>
											<td ${csscentered} trick-field="investment" trick-field-type="double" ondblclick="return editField(this);" title="${measure.investment}&euro;"
												real-value="${fn:replace(measure.investment,',' ,'.') }"><fmt:formatNumber value="${measure.investment*0.001}" maxFractionDigits="0" /></td>
											<c:set var="lifetime">
												<fmt:formatNumber value="${measure.lifetime}" maxFractionDigits="2" />
											</c:set>
											<td ${csscentered} trick-field="lifetime" trick-field-type="double" ondblclick="return editField(this);" real-value="${fn:replace(lifetime,',' ,'.') }">${lifetime}</td>
											<td ${csscentered} trick-field="internalMaintenance" trick-field-type="double" ondblclick="return editField(this);"
												real-value="${fn:replace(measure.internalMaintenance,',' ,'.') }"><fmt:formatNumber value="${measure.internalMaintenance}" maxFractionDigits="2" /></td>
											<td ${csscentered} trick-field="externalMaintenance" trick-field-type="double" ondblclick="return editField(this);"><fmt:formatNumber
													value="${measure.externalMaintenance}" maxFractionDigits="2" /></td>
											<c:set var="recurrentInvestment">
												<fmt:formatNumber value="${measure.recurrentInvestment*0.001}" maxFractionDigits="0" />
											</c:set>
											<td ${csscentered} trick-field="recurrentInvestment" trick-field-type="double" ondblclick="return editField(this);" title="${measure.recurrentInvestment}&euro;"
												real-value="${fn:replace(recurrentInvestment,',' ,'.') }">${recurrentInvestment}</td>
											<c:set var="cost">
												<fmt:formatNumber value="${measure.cost*0.001}" maxFractionDigits="0" />
											</c:set>
											<c:choose>
												<c:when test="${measure.getImplementationRateValue()==100 || measure.getStatus().equals('NA')}">
													<td class='textaligncenter' title="${measure.cost}&euro;">${cost}</td>
												</c:when>
												<c:otherwise>
													<td ${cost == 0? "class='textaligncenter danger'" : "class='textaligncenter'" } title="${measure.cost}&euro;">${cost}</td>
												</c:otherwise>
											</c:choose>
											<td ${csscentered} trick-field="phase" trick-field-type="integer" ondblclick="return editField(this);" trick-callback-pre="extractPhase(this)"
												real-value='${measure.phase.number}'><c:choose>
													<c:when test="${measure.phase.number == 0}">NA</c:when>
													<c:otherwise>${measure.phase.number}</c:otherwise>
												</c:choose></td>
											<td ${css} ondblclick="return editField(this.firstElementChild);">
												<div class="headertofixtablelargecolumn" trick-field="comment" trick-content="text" trick-field-type="string">
													<spring:message text="${measure.comment}" />
												</div>
											</td>
											<td ${css} ondblclick="return editField(this.firstElementChild);">
												<div class="headertofixtablelargecolumn" trick-field="toDo" trick-content="text" trick-field-type="string">
													<spring:message text="${measure.toDo}" />
												</div>
											</td>
											<c:if test="${measure.getClass().name.equals('lu.itrust.business.TS.NormMeasure')}">
												<td ${css} ondblclick="return editField(this.firstElementChild);">
													<div class="headertofixtablelargecolumn" trick-field="toCheck" trick-content="text" trick-field-type="string">
														<spring:message text="${measure.toCheck}" />
													</div>
												</td>
											</c:if>
										</tr>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</c:forEach>
</div>
