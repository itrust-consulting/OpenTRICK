<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<c:if test="${!empty(soa)}">
	<div id="tabSOA" class="tab-pane">
		<div class="section" id="section_soa" style="z-index: 3">
			<div class="page-header tab-content-header">
				<div class="container">
					<div class="row-fluid">
						<h3>
							<spring:message text="27002" />
						</h3>
					</div>
				</div>
			</div>
			<table class="table table-hover table-fixed-header-analysis table-condensed" id="table_SOA_27002">
				<thead>
					<tr>
						<th style="width: 5%;"><fmt:message key="label.measure.ref" /></th>
						<th style="width: 15%;"><fmt:message key="label.measure.domain" /></th>
						<th style="width: 5%;"><fmt:message key="label.measure.phase" /></th>
						<th style="width: 15%;"><fmt:message key="label.measure.soa.risk" /></th>
						<th><fmt:message key="label.measure.soa.comment" /></th>
						<th><fmt:message key="label.measure.SOA.reference" /></th>
					</tr>
				</thead>
				<tfoot>
				</tfoot>
				<tbody>
					<c:forEach items="${soa}" var="measure">
						<c:choose>
							<c:when test="${not measure.measureDescription.computable}">
								<tr style="background-color: #F8F8F8;">
									<c:set var="measureDescriptionText" value="${measure.measureDescription.getMeasureDescriptionTextByAlpha2(language)}" />
									<td><spring:message text="${measure.measureDescription.reference}" /></td>
									<td colspan="5"><spring:message text="${!empty measureDescriptionText? measureDescriptionText.domain : ''}" /></td>
								</tr>
							</c:when>
							<c:when test="${ not empty measure.measurePropertyList.soaRisk }">
								<c:set var="css">
									<c:if test="${measure.implementationRateValue<100 and measure.implementationRateValue < soaThreshold}">class="success"</c:if>
								</c:set>
								<tr data-trick-class="SOA" data-trick-id="${measure.id}">
									<c:set var="measureDescriptionText" value="${measure.measureDescription.getMeasureDescriptionTextByAlpha2(language)}" />
									<c:choose>
										<c:when test="${empty measureDescriptionText or empty(measureDescriptionText.description)}">
											<td class="popover-element" data-toggle="popover" data-container="body" data-placement="right" data-trigger="hover" data-html="true" data-content=''
												title='<spring:message
													text="${measure.measureDescription.reference}" />'><spring:message text="${measure.measureDescription.reference}" /></td>
										</c:when>
										<c:otherwise>
											<td class="popover-element" data-toggle="popover" data-container="body" data-placement="right" data-trigger="hover" data-html="true"
												data-content='<pre><spring:message text="${measureDescriptionText.description}" /></pre>'
												title='<spring:message
													text="${measure.measureDescription.reference}" />'><spring:message text="${measure.measureDescription.reference}" /></td>
										</c:otherwise>
									</c:choose>
									<td><spring:message text="${!empty measureDescriptionText? measureDescriptionText.domain : ''}" /></td>
									<td><c:choose>
											<c:when test="${measure.phase.number == 0}">NA
															</c:when>
											<c:otherwise>${measure.phase.number}
															</c:otherwise>
										</c:choose></td>
									<c:set var="newLine" value="\n" />
									<td><pre>
											<spring:message text="${measure.measurePropertyList.getSoaRisk()}" />
										</pre></td>
									<td ${css} onclick="return editField(this.firstElementChild);"><pre data-trick-field="soaComment" data-trick-content="text" data-trick-field-type="string"><spring:message text="${measure.measurePropertyList.getSoaComment()}" /></pre></td>
									<td ${css} onclick="return editField(this.firstElementChild);"><pre data-trick-field="soaReference" data-trick-content="text" data-trick-field-type="string"><spring:message text="${measure.measurePropertyList.getSoaReference()}" /></pre></td>
								</tr>
							</c:when>
						</c:choose>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
</c:if>