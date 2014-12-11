<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<c:if test="${!empty(soa)}">
	<div id="tabSOA" class="tab-pane">
		<div class="section" id="section_soa" style="z-index: 3">
			<div class="panel panel-default">
				<div class="panel-heading">
					<spring:message text="27002" />
				</div>
				<div class="panel-body">
					<table class="table table-hover" id="table_SOA_27002">
						<thead>
							<tr>
								<th><fmt:message key="label.measure.ref" /></th>
								<th colspan="4"><fmt:message key="label.measure.domain" /></th>
								<th><fmt:message key="label.measure.phase" /></th>
								<th colspan="6"><fmt:message key="label.measure.soa.risk" /></th>
								<th colspan="8"><fmt:message key="label.measure.soa.comment" /></th>
								<th colspan="8"><fmt:message key="label.measure.SOA.reference" /></th>
							</tr>
						</thead>
						<tfoot>
						</tfoot>
						<tbody>
							<c:forEach items="${soa}" var="measure">
								<c:choose>
									<c:when test="${measure.measureDescription.computable==false }">
										<tr style="background-color: #F8F8F8;">
											<c:set var="measureDescriptionText" value="${measure.measureDescription.getMeasureDescriptionTextByAlpha3(language)}" />
											<td><spring:message text="${measure.measureDescription.reference}" /></td>
											<td colspan="27"><spring:message text="${!empty measureDescriptionText? measureDescriptionText.domain : ''}" /></td>
										</tr>
									</c:when>
									<c:otherwise>
										<tr trick-class="SOA" trick-id="${measure.id}">
											<c:set var="measureDescriptionText" value="${measure.measureDescription.getMeasureDescriptionTextByAlpha3(language)}" />
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
											<td colspan="4"><spring:message text="${!empty measureDescriptionText? measureDescriptionText.domain : ''}" /></td>
											<td><c:choose>
													<c:when test="${measure.phase.number == 0}">NA
																	</c:when>
													<c:otherwise>${measure.phase.number}
																	</c:otherwise>
												</c:choose></td>
											<c:set var="newLine" value="\n" />
											<td colspan="6"><pre><spring:message text="${measure.measurePropertyList.getSoaRisk()}" /></pre></td>
											<td colspan="8" class="success" ondblclick="return editField(this.firstElementChild);"><pre trick-field="soaComment" trick-content="text" trick-field-type="string">
															<spring:message text="${measure.measurePropertyList.getSoaComment()}" />
														</pre></td>
											<td colspan="8" class="success" ondblclick="return editField(this.firstElementChild);"><pre trick-field="soaReference" trick-content="text" trick-field-type="string">
															<spring:message text="${measure.measurePropertyList.getSoaReference()}" />
														</pre></td>
										</tr>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
</c:if>