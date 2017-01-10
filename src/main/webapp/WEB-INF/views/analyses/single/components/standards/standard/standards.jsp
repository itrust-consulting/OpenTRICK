<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fct" uri="http://trickservice.itrust.lu/JSTLFunctions"%>
<c:if test="${empty locale }">
	<spring:eval expression="T(org.springframework.web.servlet.support.RequestContextUtils).getLocale(pageContext.request)" var="locale" scope="request" />
</c:if>
<c:set var="language" value="${locale.language}" scope="request" />
<fmt:setLocale value="fr" scope="session" />
<spring:message code="label.measure.status.m" var="statusM" />
<spring:message code="label.measure.status.ap" var="statusAP" />
<spring:message code="label.measure.status.na" var="statusNA" />
<spring:message code="label.title.measure.status.m" var="titleStatusM" />
<spring:message code="label.title.measure.status.ap" var="titleStatusAP" />
<spring:message code="label.title.measure.status.na" var="titleStatusNA" />
<c:set var="implementationRateAttr">
	<c:choose>
		<c:when test="${type == 'QUALITATIVE'}">
			data-trick-min-value='0' data-trick-max-value='100' data-trick-step-value='1'
		</c:when>
		<c:otherwise>data-trick-list-value="dataListImplementationRate"</c:otherwise>
	</c:choose>
</c:set>
<div class="tab-pane" id="tab-standards" >
	<div class="page-header tab-content-header">
		<div class="container">
			<div class="row-fluid">
				<h3>
					<select id='measure-collection-selector' class="form-control center-block" style="width: auto;min-width: 18%; font-size: 14px; padding-top: 5px;">
						<c:forEach items="${standards}" var="standard" varStatus="varStatus">
							<option value="section_standard_${standard.id}" data-trick-name="<spring:message text='${standard.label}'/>"><spring:message
									text="${standard.label}" /></option>
							<c:if test="${varStatus.index==0 }">
								<c:set var="firstStandard" value="${standard}"/>
							</c:if>
						</c:forEach>
					</select>
				</h3>
			</div>
		</div>
	</div>
	<c:forEach items="${measuresByStandard.keySet()}" var="standard">
		<spring:eval expression="T(lu.itrust.business.TS.model.standard.measure.helper.MeasureManager).getStandard(standards, standard)" var="selectedStandard"/>
		<c:set var="standardType" value="${selectedStandard.type}"/>
		<c:set var="standardid" value="${selectedStandard.id }"/>
		<c:set var="analysisOnly" value="${selectedStandard.analysisOnly}"/>
		<div id="tab-standard-${standardid}" data-trick-id="${standardid}">
			<div id="section_standard_${standardid}" data-trick-id="${standardid}" data-trick-label="${standard}" style="display: ${firstStandard == selectedStandard? '' : 'none'}">
				<c:if test="${isLinkedToProject or analysisOnly and isEditable}">
					<ul class="nav nav-pills bordered-bottom" id="menu_standard_${standardid}">
						<c:if test="${analysisOnly and isEditable}">
							<li><a onclick="return addMeasure(this,${standardid});" href="#"><span class="glyphicon glyphicon-plus primary"></span> <spring:message code="label.action.add" /></a></li>
							<li data-trick-check="isEditable()" data-trick-selectable="true" class="disabled"><a onclick="return editMeasure(this,${standardid});" href="#"><span
									class="glyphicon glyphicon-edit danger"></span> <spring:message code="label.action.edit" /></a></li>
						</c:if>
						<c:if test="${isLinkedToProject}">
							<c:set var="ttSysName" value="${fn:toLowerCase(ticketingName)}"/>
							<c:choose>
								<c:when test="${isEditable}">
									<li class="disabled" data-trick-selectable="multi" data-trick-single-check="isLinkedTicketingSystem('#section_standard_${standardid}')"><a href="#"
										onclick="return synchroniseWithTicketingSystem('#section_standard_${standardid}')"><spring:message code="label.open.ticket_measure"
												text="Open Measure/Ticket" /></a></li>
									<li class="disabled" data-trick-selectable="multi"><a href="#"
										onclick="return generateTickets('#section_standard_${standardid}')"><spring:message code="label.action.create_or_update.tickets" text="Generate/Update Tickets" /></a></li>
									<li class="disabled" data-trick-selectable="multi" data-trick-single-check="isUnLinkedTicketingSystem('#section_standard_${standardid}')"><a href="#"
										onclick="return linkToTicketingSystem('#section_standard_${standardid}')"><spring:message code="label.link.to.ticketing.system" arguments="${ticketingName}"
												text="Link to ${ticketingName}" /></a></li>
									<li class="disabled" data-trick-selectable="multi" data-trick-single-check="isLinkedTicketingSystem('#section_standard_${standardid}')"><a href="#"
										onclick="return unLinkToTicketingSystem('#section_standard_${standardid}')"><spring:message code="label.unlink.from.ticketing.system" arguments="${ticketingName}"
												text="Unlink from ${ticketingName}" /></a></li>
								</c:when>
								<c:otherwise>
									<li class="disabled" data-trick-selectable="multi" data-trick-single-check="isLinkedTicketingSystem('#section_standard_${standardid}')"><a href="#"
										onclick="return openTicket('#section_standard_${standardid}')"><spring:message code="label.open.ticket" text="Open ticket" /></a></li>
								</c:otherwise>
							</c:choose>
						</c:if>
						<c:if test="${analysisOnly and isEditable}">
							<li style="display: none;" class="dropdown-header"><spring:message code="label.menu.advanced" /></li>
							<li data-trick-check="isEditable()" data-trick-selectable="multi" class="disabled pull-right"><a onclick="return deleteMeasure(null,${standardid});" class="text-danger"
								href="#"><span class="glyphicon glyphicon-remove"></span> <spring:message code="label.action.delete" /></a></li>
						</c:if>
					</ul>
				</c:if>
				<table class="table table-hover table-fixed-header-analysis table-condensed" id="table_Measure_${standardid}" lang="${language}" >
					<thead>
						<tr>
							<c:if test="${isLinkedToProject or analysisOnly and isEditable}">
								<th width="1%"><input type="checkbox" class="checkbox" onchange="return checkControlChange(this,'standard_${standardid}')"></th>
							</c:if>
							<th width="2%" title='<spring:message code="label.reference" />'><spring:message code="label.measure.ref" /></th>
							<c:choose>
								<c:when test="${hasMaturity and standard.equals('27002') }">
									<th width="8%" title='<spring:message code="label.measure.domain" />'><spring:message code="label.measure.domain" /></th>
									<th width="2%" title='<spring:message code="label.title.measure.status" />'><spring:message code="label.measure.status" /></th>
									<th width="2%" title='<spring:message code="label.title.measure.ir" />'><spring:message code="label.measure.ir" /></th>
									<th width="2%" title='<spring:message code="label.title.measure.mer"/>'><spring:message code="label.measure.mer"/></th>
								</c:when>
								<c:otherwise>
									<th width="10%" title='<spring:message code="label.measure.domain" />'><spring:message code="label.measure.domain" /></th>
									<th width="2%" title='<spring:message code="label.title.measure.status" />'><spring:message code="label.measure.status" /></th>
									<th width="2%" title='<spring:message code="label.title.measure.ir" />'><spring:message code="label.measure.ir" /></th>
								</c:otherwise>
							</c:choose>
							<th width="2%" title='<spring:message code="label.title.measure.iw" />' ><spring:message code="label.measure.iw" /></th>
							<th width="2%" title='<spring:message code="label.title.measure.ew" />' ><spring:message code="label.measure.ew" /></th>
							<th width="2%" title='<spring:message code="label.title.measure.inv" />' ><spring:message code="label.measure.inv" /></th>
							<th width="2%" title='<spring:message code="label.title.measure.lt" />' ><spring:message code="label.measure.lt" /></th>
							<th width="2%" title='<spring:message code="label.title.measure.im" />' ><spring:message code="label.measure.im" /></th>
							<th width="2%" title='<spring:message code="label.title.measure.em" />' ><spring:message code="label.measure.em" /></th>
							<th width="2%" title='<spring:message code="label.title.measure.ri" />' ><spring:message code="label.measure.ri" /></th>
							<th width="2%" title='<spring:message code="label.title.measure.cost" />' ><spring:message code="label.measure.cost" /></th>
							<th width="2%" title='<spring:message code="label.title.measure.phase" />' ><spring:message code="label.measure.phase" /></th>
							<th width="3%" title='<spring:message code="label.title.measure.responsible" />' ><spring:message code="label.measure.responsible" /></th>
							<c:choose>
								<c:when test="${standardType.name.equals('NORMAL') || standardType.name.equals('ASSET')}">
									<th width="14%" title='<spring:message code="label.measure.tocheck" />' ><spring:message code="label.measure.tocheck" /></th>
									<th width="25%"  title='<spring:message code="label.comment" />' ><spring:message code="label.comment" /></th>
									<th width="25%" title='<spring:message code="label.measure.todo" />' ><spring:message code="label.measure.todo" /></th>
								</c:when>
								<c:otherwise>
									<th width="32%"  title='<spring:message code="label.comment" />' ><spring:message code="label.comment" /></th>
									<th width="32%" title='<spring:message code="label.measure.todo" />' ><spring:message code="label.measure.todo" /></th>
								</c:otherwise>
							</c:choose>
						</tr>
					</thead>
					<tfoot>
					</tfoot>
					<tbody>
						<c:forEach items="${measuresByStandard.get(standard)}" var="measure">
							<c:set var="css">
								<c:if test="${measure.getImplementationRateValue(valueFactory) < 100 and measure.status!='NA' }">class="success"</c:if>
							</c:set>
							<c:set var="todoCSS">
								<c:choose>
									<c:when test="${empty measure.toDo && fn:contains(css,'success')}">class="danger"</c:when>
									<c:otherwise>${css}</c:otherwise>
								</c:choose>
							</c:set>
							<c:set var="measureDescriptionText" value="${measure.measureDescription.getMeasureDescriptionTextByAlpha2(language)}" />
							<c:set var="dblclickaction">
								<c:if test="${isEditable and ( analysisOnly or type == 'QUANTITATIVE' and measure.measureDescription.computable && selectedStandard.computable && selectedStandard.type!='MATURITY')}">
									ondblclick="return editMeasure(this,${standardid},${measure.id});"
								</c:if>
							</c:set>
							<c:set var="popoverRef">
								<c:if test="${not(empty measure.measureDescription.reference or empty measureDescriptionText.description)}">
									data-toggle="tooltip" data-container="body" data-trigger="click" data-placement='auto'
									data-title='<spring:message text="${measureDescriptionText.description}" />' style='cursor: pointer;'
								</c:if>
							</c:set>
							<c:set var="hasTicket" value="${isLinkedToProject and not empty measure.ticket}"/>
							<c:choose>
								<c:when test="${not measure.measureDescription.computable}">
									<tr data-trick-computable="false" data-trick-level="${measure.measureDescription.level}" data-trick-reference='${measure.measureDescription.reference}' onclick="selectElement(this)" data-trick-class="Measure" style="background-color: #F8F8F8;" 
										data-trick-id="${measure.id}" data-is-linked='${hasTicket}'
										data-trick-callback="reloadMeasureRow('${measure.id}','${standardid}');" ${dblclickaction}>
										<c:if test="${isLinkedToProject or  analysisOnly and isEditable}">
											<td><input type="checkbox" ${not analysisOnly?'disabled':''} class="checkbox"
												onchange="return updateMenu(this,'#section_standard_${standardid}','#menu_standard_${standardid}');"></td>
										</c:if>
										<td>
										<c:choose>
											<c:when test="${hasTicket}">
												<spring:eval expression="T(lu.itrust.business.TS.model.ticketing.builder.ClientBuilder).TicketLink(ttSysName,ticketingURL,measure.ticket)" var="ticketLink" />
												<a href="${ticketLink}" target="_titck_ts"><spring:message text="${measure.measureDescription.reference}" /></a>
											</c:when>
											<c:otherwise><spring:message text="${measure.measureDescription.reference}" /></c:otherwise>
										</c:choose>
										</td>
										<c:choose>
											<c:when test="${hasMaturity and standard.equals('27002') }">
												<td ${popoverRef} colspan="14"><spring:message text="${!empty measureDescriptionText? measureDescriptionText.domain : ''}" /></td>
											</c:when>
											<c:otherwise>
												<td ${popoverRef} colspan="13"><spring:message text="${!empty measureDescriptionText? measureDescriptionText.domain : ''}" /></td>
											</c:otherwise>
										</c:choose>
										<c:choose>
											<c:when test="${standardType.name.equals('NORMAL') || standardType.name.equals('ASSET')}">
												<td class="warning" onclick="return editField(this.firstElementChild);"><pre data-trick-field="toCheck" data-trick-content="text" data-trick-field-type="string"><spring:message text="${measure.toCheck}" /></pre></td>
												<td class="warning" onclick="return editField(this.firstElementChild);"><pre data-trick-field="comment" data-trick-content="text" data-trick-field-type="string"><spring:message text="${measure.comment}" /></pre></td>
											</c:when>
											<c:otherwise>
												<td class='warning' onclick="return editField(this.firstElementChild);"><pre data-trick-field="comment" data-trick-content="text" data-trick-field-type="string"><spring:message text="${measure.comment}" /></pre></td>
											</c:otherwise>
										</c:choose>
										<td></td>
									</tr>
								</c:when>
								<c:otherwise>
									<tr ${isAnalysisOnly?dblclickaction:''} data-trick-computable="true" data-trick-description="${measureDescriptionText.description}" onclick="selectElement(this)" data-trick-level="${measure.measureDescription.level}" 
										data-trick-class="Measure" data-is-linked='${hasTicket}'
										data-trick-id="${measure.id}" data-trick-callback="reloadMeasureRow('${measure.id}','${standardid}');">
										<c:if test="${isLinkedToProject or  analysisOnly and isEditable}">
											<td><input type="checkbox" ${measure.status=='NA'?'disabled':''} class="checkbox"
												onchange="return updateMenu(this,'#section_standard_${standardid}','#menu_standard_${standardid}');"></td>
										</c:if>
										<td ${not isAnalysisOnly ?dblclickaction:''} >
											<c:choose>
												<c:when test="${hasTicket}">
													<spring:eval expression="T(lu.itrust.business.TS.model.ticketing.builder.ClientBuilder).TicketLink(ttSysName,ticketingURL,measure.ticket)" var="ticketLink" />
													<a href="${ticketLink}" target="_titck_ts" class="btn btn-default btn-xs"><spring:message text="${measure.measureDescription.reference}" /></a>
												</c:when>
												<c:otherwise><spring:message text="${measure.measureDescription.reference}" /></c:otherwise>
											</c:choose>
										</td>
										<td ${popoverRef}><spring:message text="${!empty measureDescriptionText? measureDescriptionText.domain : ''}" /></td>
										<td ${css} data-trick-field="status" data-trick-choose="M,AP,NA" data-trick-choose-translate="${statusM},${statusAP},${statusNA}"
											data-trick-choose-title='${titleStatusM},${titleStatusAP},${titleStatusNA}' data-trick-field-type="string" onclick="return editField(this);"
											data-trick-callback="reloadMeasureAndCompliance('${standardid}','${measure.id}')"><c:choose>
												<c:when test="${measure.status=='NA'}">
													${statusNA}
												</c:when>
												<c:when test="${measure.status=='AP'}">
													${statusAP}
												</c:when>
												<c:otherwise>
													${statusM}
												</c:otherwise>
											</c:choose></td>
										<fmt:formatNumber value="${measure.getImplementationRateValue(valueFactory)}" maxFractionDigits="0" minFractionDigits="0" var="implementationRateValue"/>
										<c:choose>
											<c:when test="${standardType.name.equals('MATURITY')}">
												<td ${css} data-trick-field="implementationRate" data-trick-class="MaturityMeasure" data-trick-field-type="double" title="${implementationRateValue} %"
													data-trick-callback="reloadMeasureAndCompliance('${standardid}','${measure.id}');updateMeasureEffience('${measure.measureDescription.reference}');" onclick="return editField(this);">${implementationRateValue}</td>
											</c:when>
											<c:otherwise>
												<td ${css} data-trick-field="implementationRate" data-trick-field-type="string" ${implementationRateAttr} title="${implementationRateValue} %"
													data-trick-callback="reloadMeasureAndCompliance('${standardid}','${measure.id}')" onclick="return editField(this);">${implementationRateValue}</td>
												<c:if test="${hasMaturity and standard.equals('27002') }">
													<td class="text-center" data-trick-field='mer' ><c:choose>
															<c:when test="${empty effectImpl27002[measure.measureDescription.reference]}">0</c:when>
															<c:otherwise>
																<fmt:formatNumber value="${effectImpl27002[measure.measureDescription.reference]}" maxFractionDigits="0" minFractionDigits="0" />
															</c:otherwise>
														</c:choose></td>
												</c:if>
											</c:otherwise>
										</c:choose>
										<td ${css} data-trick-field="internalWL" data-trick-field-type="double" onclick="return editField(this);" data-trick-min-value='0' ><fmt:formatNumber value="${measure.internalWL}"
												maxFractionDigits="2" /></td>
										<td ${css} data-trick-field="externalWL" data-trick-field-type="double" onclick="return editField(this);" data-trick-min-value='0' ><fmt:formatNumber value="${measure.externalWL}"
												maxFractionDigits="2" /></td>
										<td ${css} data-trick-field="investment" data-trick-field-type="double" onclick="return editField(this);" data-trick-min-value='0'
											title='<fmt:formatNumber value="${fct:round(measure.investment,0)}" maxFractionDigits="0" /> &euro;'
											data-real-value='<fmt:formatNumber value="${measure.investment*0.001}" maxFractionDigits="2" />'><fmt:formatNumber maxFractionDigits="0"
												value="${fct:round(measure.investment*0.001,0)}" /></td>
										<td ${css} data-trick-field="lifetime" data-trick-field-type="double" onclick="return editField(this);" data-trick-min-value='0' ><fmt:formatNumber value="${measure.lifetime}" maxFractionDigits="2" /></td>
										<td ${css} data-trick-field="internalMaintenance" data-trick-field-type="double" onclick="return editField(this);"><fmt:formatNumber value="${measure.internalMaintenance}"
												maxFractionDigits="2" /></td>
										<td ${css} data-trick-field="externalMaintenance" data-trick-field-type="double" onclick="return editField(this);" ><fmt:formatNumber value="${measure.externalMaintenance}"
												maxFractionDigits="2" /></td>
										<td ${css} data-trick-field="recurrentInvestment" data-trick-field-type="double" onclick="return editField(this);"
											title='<fmt:formatNumber value="${fct:round(measure.recurrentInvestment,0)}" maxFractionDigits="0" /> &euro;'
											data-real-value='<fmt:formatNumber value="${measure.recurrentInvestment*0.001}" maxFractionDigits="2" />'><fmt:formatNumber
												value="${fct:round(measure.recurrentInvestment*0.001,0)}" maxFractionDigits="0" /></td>
										<c:choose>
											<c:when test="${implementationRateValue>=100 || measure.getStatus().equals('NA')}">
												<td class='textaligncenter' title='<fmt:formatNumber value="${fct:round(measure.cost,0)}" maxFractionDigits="0" /> &euro;'><fmt:formatNumber
														value="${fct:round(measure.cost*0.001,0)}" maxFractionDigits="0" /></td>
											</c:when>
											<c:otherwise>
												<td ${measure.cost == 0? "class='textaligncenter danger'" : "class='textaligncenter'" }
													title='<fmt:formatNumber value="${fct:round(measure.cost,0)}" maxFractionDigits="0" /> &euro;'><fmt:formatNumber value="${fct:round(measure.cost*0.001,0)}"
														maxFractionDigits="0" /></td>
											</c:otherwise>
										</c:choose>
										<td ${css} data-trick-field="phase" data-trick-field-type="integer" onclick="return editField(this);" data-trick-callback-pre="extractPhase(this)"
											data-real-value='${measure.phase.number}'><c:choose>
												<c:when test="${measure.phase.number == 0}">NA</c:when>
												<c:otherwise>${measure.phase.number}</c:otherwise>
											</c:choose></td>
										<td ${css} onclick="return editField(this);" data-trick-field="responsible"  data-trick-field-type="string"><spring:message text="${measure.responsible}" /></td>
										<c:if test="${standardType.name.equals('NORMAL') || standardType.name.equals('ASSET')}">
											<td ${css} onclick="return editField(this.firstElementChild);"><pre data-trick-field="toCheck" data-trick-content="text" data-trick-field-type="string"><spring:message text="${measure.toCheck}" /></pre></td>
										</c:if>
										<td ${css} onclick="return editField(this.firstElementChild);"><pre data-trick-field="comment" data-trick-content="text" data-trick-field-type="string"><spring:message text="${measure.comment}" /></pre></td>
										<td ${todoCSS} onclick="return editField(this.firstElementChild);"><pre data-trick-field="toDo" data-trick-content="text" data-trick-field-type="string"><spring:message text="${measure.toDo}" /></pre></td>
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