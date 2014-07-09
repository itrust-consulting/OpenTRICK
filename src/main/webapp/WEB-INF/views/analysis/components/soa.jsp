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
						<table class="table table-hover table-fixed-header" id="table_SOA_${norm}">
							<thead>
								<tr>
									<th><spring:message code="label.measure.ref" text="Ref" /></th>
									<th colspan="2"><spring:message code="label.measure.domain" text="Domain" /></th>
									<th><spring:message code="label.measure.phase" text="Phase" /></th>
									<th colspan="2"><spring:message code="label.measure.SOA.risk" text="Risk" /></th>
									<th colspan="3"><spring:message code="label.measure.SOA.comment" text="Comment" /></th>
									<th colspan="3"><spring:message code="label.measure.SOA.reference" text="Reference" /></th>
								</tr>
							</thead>
							<tfoot>
							</tfoot>
							<tbody>
								<c:forEach items="${measureSplited.get(norm)}" var="measure">
									<c:choose>
										<c:when test="${measure.measureDescription.computable==false }">
											<tr style="background-color: #F8F8F8;">
												<c:set var="measureDescriptionText" value="${measure.measureDescription.getMeasureDescriptionTextByAlpha3(language)}" />
												<td><spring:message text="${measure.measureDescription.reference}" /></td>
												<td colspan="11"><spring:message text="${!empty measureDescriptionText? measureDescriptionText.domain : ''}" /></td>
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
												<td colspan="2"><spring:message text="${!empty measureDescriptionText? measureDescriptionText.domain : ''}" /></td>
												<td><c:choose>
														<c:when test="${measure.phase.number == 0}">NA
																</c:when>
														<c:otherwise>${measure.phase.number}
																</c:otherwise>
													</c:choose></td>
												<td colspan="2">
													<spring:message text="${measure.measurePropertyList.getSoaRisk()}" />
												</td>
												<td colspan="3" class="success" ondblclick="return editField(this.firstElementChild);"  >
													<pre trick-field="soaComment" trick-content="text" trick-field-type="string"><spring:message text="${measure.measurePropertyList.getSoaComment()}" />
													</pre>
												</td>
												<td colspan="3" class="success" ondblclick="return editField(this.firstElementChild);">
													<pre trick-field="soaReference" trick-content="text" trick-field-type="string"><spring:message text="${measure.measurePropertyList.getSoaReference()}" />
													</pre>
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