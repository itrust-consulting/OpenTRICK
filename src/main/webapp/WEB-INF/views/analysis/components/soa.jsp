<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<span id="anchorSOA" class="anchor"></span>
<div class="section" id="section_soa" style="z-index: 3">
	<div class="page-header">
		<h3 id="SOA">
			<spring:message code="label.SOA" text="SOA" />
		</h3>
	</div>
	<c:if test="${empty(measureSplited)}">
		<spring:eval expression="T(lu.itrust.business.component.MeasureManager).SplitByNorm(measures)" var="measureSplited" />
	</c:if>
	<c:forEach items="${measureSplited.keySet()}" var="norm">
		<c:if test="${norm.equals('27002')}">
			<span class="anchor" id="anchorSOA_${norm}"></span>
			<div id="section_SOA_${norm}">
				<div class="panel panel-default">
					<div class="panel-heading">
						<spring:message code="label.measure.${norm}" text="${norm}" />
					</div>
					<div class="panel-body autofitpanelbodydefinition">
						<table class="table table-hover headertofixtable" id="table_SOA_${norm}">
							<thead>
								<tr>
									<th style="width:80px;"><spring:message code="label.measure.ref" text="Ref" /></th>
									<th style="width:350px;"><spring:message code="label.measure.domain" text="Domain" /></th>
									<th class="textaligncenter cellwidth_80"><spring:message code="label.measure.phase" text="Phase" /></th>
									<th><spring:message code="label.measure.SOA.risk" text="Risk" /></th>
									<th><spring:message code="label.measure.SOA.comment" text="Comment" /></th>
									<th><spring:message code="label.measure.SOA.reference" text="Reference" /></th>
								</tr>
							</thead>
							<tfoot>
							</tfoot>
							<tbody>
								<c:forEach items="${measureSplited.get(norm)}" var="measure">
									<c:choose>
										<c:when test="${measure.measureDescription.computable==false }">
											<tr style="background-color: LightGray">
												<c:set var="measureDescriptionText" value="${measure.measureDescription.getMeasureDescriptionTextByAlpha3(language)}" />
												<td><spring:message text="${measure.measureDescription.reference}" /></td>
												<td style="overflow:show;white-space: nowrap;"><spring:message text="${!empty measureDescriptionText? measureDescriptionText.domain : ''}" /></td>
												<td></td>
												<td></td>
												<td></td>
												<td></td>
											</tr>
										</c:when>
										<c:otherwise>
											<tr trick-class="SOA" trick-id="${measure.id}"
												trick-callback="initialiseTableFixedHeaderRows('#section_measure_${norm}');">
												<c:set var="measureDescriptionText" value="${measure.measureDescription.getMeasureDescriptionTextByAlpha3(language)}" />
												<td><a href="#" class="descriptiontooltip" data-toggle="tooltip" data-html="true"
													title="<spring:message text="${!empty measureDescriptionText? measureDescriptionText.description : ''}" />"> <spring:message
															text="${measure.measureDescription.reference}" />
												</a></td>
												<td><div class="headertofixtablelargecolumn"><spring:message text="${!empty measureDescriptionText? measureDescriptionText.domain : ''}" /></div></td>
												<td class="textaligncenter"><c:choose>
														<c:when test="${measure.phase.number == 0}">
																	NA
																</c:when>
														<c:otherwise>
																	${measure.phase.number}
																</c:otherwise>
													</c:choose></td>
												<td>
													<div class="headertofixtablelargecolumn">
														<spring:message text="${measure.measurePropertyList.getSoaRisk()}" />
													</div>
												</td>
												<td class="success" ondblclick="return editField(this.firstElementChild);"  >
													<div class="headertofixtablelargecolumn" trick-field="soaComment" trick-content="text" trick-field-type="string">
														<spring:message text="${measure.measurePropertyList.getSoaComment()}" />
													</div>
												</td>
												<td class="success" ondblclick="return editField(this.firstElementChild);">
													<div class="headertofixtablelargecolumn" trick-field="soaReference" trick-content="text" trick-field-type="string">
														<spring:message text="${measure.measurePropertyList.getSoaReference()}" />
													</div>
												</td>
											</tr>
										</c:otherwise>
									</c:choose>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</c:if>
	</c:forEach>
</div>