<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fct" uri="https://trickservice.com/tags/functions"%>
<c:if test="${empty locale }">
	<spring:eval expression="T(org.springframework.context.i18n.LocaleContextHolder).getLocale()" var="locale" scope="request" />
</c:if>
<c:set var="language" value="${locale.language}" scope="request" />
<fmt:setLocale value="fr" scope="session" />
<spring:message code="label.measure.status.m" var="statusM" />
<spring:message code="label.measure.status.ap" var="statusAP" />
<spring:message code="label.measure.status.ex" var="statusEX" />
<spring:message code="label.measure.status.op" var="statusOP" />
<spring:message code="label.measure.status.na" var="statusNA" />
<spring:message code="label.title.measure.status.m" var="titleStatusM" />
<spring:message code="label.title.measure.status.ap" var="titleStatusAP" />
<spring:message code="label.title.measure.status.op" var="titleStatusOP" />
<spring:message code="label.title.measure.status.ex" var="titleStatusEX" />
<spring:message code="label.title.measure.status.na" var="titleStatusNA" />
<c:forEach begin="1" step="1" end="3" var="impValue">
	<spring:message code="label.measure.importance.value" arguments="${impValue}" var="imp${impValue}" />
	<spring:message code="label.title.measure.importance.value" arguments="${impValue}" var="titleImp${impValue}" />
</c:forEach>
<c:set var="implementationRateAttr">
	<c:choose>
		<c:when test="${type=='QUALITATIVE' or not showDynamicAnalysis}">
			data-trick-min-value='0' data-trick-max-value='100' data-trick-step-value='1'
		</c:when>
		<c:otherwise>data-trick-list-value="dataListImplementationRate"</c:otherwise>
	</c:choose>
</c:set>
<div class="tab-pane" id="tab-standards" data-callback='checkForCollectionUpdate'>
	<div class="page-header tab-content-header">
		<div class="container">
			<div class="row-fluid">
				<h3>
					<span class="col-sm-4" style="display: block; padding: 5px;"><spring:message code="label.title.analysis.measures_by_collection" /></span> <span
						class="col-sm-offset-1 col-sm-7"> <select id='measure-collection-selector' class="form-control" style="max-width: 280px;">
							<c:forEach items="${standards}" var="standard" varStatus="varStatus">
								<option value="tab-standard-${standard.id}" data-trick-name="<spring:message text='${standard.name}'/>"><spring:message text="${standard.name}" /></option>
								<c:if test="${varStatus.index==0}">
									<c:set var="firstStandard" value="${standard}" />
								</c:if>
							</c:forEach>
					</select>
					</span>
				</h3>
			</div>
		</div>
	</div>
	<c:forEach items="${measuresByStandard.keySet()}" var="standard">
		<spring:eval expression="T(lu.itrust.business.ts.component.MeasureManager).getStandard(standards, standard)" var="selectedStandard" />
		<c:set var="standardType" value="${selectedStandard.type}" />
		<c:set var="standardid" value="${selectedStandard.id}" />
		<c:set var="isAnalysisOnly" value="${selectedStandard.analysisOnly}" />
		<c:url value="/Analysis/Standard/${selectedStandard.id}/Export/Measure" var="measureExportUrl" />
		<div id="tab-standard-${standardid}" data-trick-id="${standardid}" data-trick-has-menu='${isAnalysisOnly or isLinkedToProject}' data-targetable='true'
			data-trick-export-url='${measureExportUrl}' style="display: ${firstStandard == selectedStandard? '' : 'none'}">
			<div id="section_standard_${standardid}" data-trick-id="${standardid}" data-trick-label="${standard}">
				<c:if test="${isLinkedToProject and (isEditable or not(isEditable or isNoClientTicketing)) or isAnalysisOnly and isEditable}">
					<ul class="nav nav-pills bordered-bottom" id="menu_standard_${standardid}">
						<c:if test="${isAnalysisOnly and isEditable}">
							<li data-trick-ignored="true"><a onclick="return addMeasure(this,${standardid});" href="#"><span class="glyphicon glyphicon-plus primary"></span> <spring:message
										code="label.action.add" /></a></li>
							<li data-trick-check="isEditable()" data-trick-selectable="true" class="disabled"><a onclick="return editMeasure(this,${standardid});" href="#"><span
									class="glyphicon glyphicon-edit danger"></span> <spring:message code="label.action.edit" /></a></li>
						</c:if>
						<c:if test="${isLinkedToProject}">
						<c:set var="ttSysName" value="${fn:toLowerCase(ticketingName)}" />
							<c:choose>
								<c:when test="${ttSysName == 'email'}">
									<li class="disabled" data-trick-selectable="multi" data-trick-single-check="!isLinkedTicketingSystem('#section_standard_${standardid}')"><a href="#" onclick="return createTickets('#section_standard_${standardid}')"><spring:message
														code="label.action.create.email.tickets" text="Create ticket by email" /></a></li>
									<li class="disabled" data-trick-selectable="multi"><a href="#" onclick="return generateTickets('#section_standard_${standardid}')"><spring:message
														code="label.action.update.tickets" text="Re-create ticket by email" /></a></li>
									<li class="disabled" data-trick-selectable="multi" data-trick-single-check="isLinkedTicketingSystem('#section_standard_${standardid}')"><a href="#"
												onclick="return unLinkToTicketingSystem('#section_standard_${standardid}')"><spring:message code="label.action.clear.email.status"
														text="Clean ticket status" /></a></li>
								</c:when>
								<c:when test="${isNoClientTicketing}">
									<li class="disabled" data-trick-selectable="multi"><a href="#" onclick="return createTickets('#section_standard_${standardid}')"><spring:message
														code="label.action.export" text="Export" /></a></li>
								</c:when>
								<c:otherwise>
									<c:choose>
										<c:when test="${isEditable}">
											<li class="disabled" data-trick-selectable="multi" data-trick-single-check="isLinkedTicketingSystem('#section_standard_${standardid}')"><a href="#"
												onclick="return synchroniseWithTicketingSystem('#section_standard_${standardid}')"><spring:message code="label.open.ticket_measure" text="Open Measure/Ticket" /></a></li>
											<li class="disabled" data-trick-selectable="multi"><a href="#" onclick="return generateTickets('#section_standard_${standardid}')"><spring:message
														code="label.action.create_or_update.tickets" text="Generate/Update Tickets" /></a></li>
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
								</c:otherwise>
							</c:choose>
						</c:if>
						<c:if test="${isAnalysisOnly and isEditable}">
							<li style="display: none;" class="dropdown-header"><spring:message code="label.menu.advanced" /></li>
							<li data-trick-check="isEditable()" data-trick-selectable="multi" class="disabled pull-right"><a onclick="return deleteMeasure(null,${standardid});" class="text-danger"
								href="#"><span class="glyphicon glyphicon-remove"></span> <spring:message code="label.action.delete" /></a></li>
						</c:if>
					</ul>
				</c:if>
				<table class="table table-hover table-fixed-header-analysis table-condensed" id="table_Measure_${standardid}" lang="${language}">
					<thead>
						<tr>
							<c:if test="${isLinkedToProject or isAnalysisOnly and isEditable}">
								<th width="1%"><input type="checkbox" class="checkbox" onchange="return checkControlChange(this,'standard_${standardid}')"></th>
							</c:if>
							<th width="2%" title='<spring:message code="label.reference" />'><spring:message code="label.measure.ref" /></th>
							<c:choose>
								<c:when test="${hasMaturity and standard.equals('27002') }">
									<th width="8%" title='<spring:message code="label.measure.domain" />'><spring:message code="label.measure.domain" /></th>
									<th width="2%" title='<spring:message code="label.title.measure.status" />'><spring:message code="label.measure.status" /></th>
									<th width="2%" title='<spring:message code="label.title.measure.ir" />'><spring:message code="label.measure.ir" /></th>
									<th width="2%" title='<spring:message code="label.title.measure.mer"/>'><spring:message code="label.measure.mer" /></th>
								</c:when>
								<c:otherwise>
									<th width="10%" title='<spring:message code="label.measure.domain" />'><spring:message code="label.measure.domain" /></th>
									<th width="2%" title='<spring:message code="label.title.measure.status" />'><spring:message code="label.measure.status" /></th>
									<th width="2%" title='<spring:message code="label.title.measure.ir" />'><spring:message code="label.measure.ir" /></th>
								</c:otherwise>
							</c:choose>
							<th width="2%" title='<spring:message code="label.title.measure.iw" />'><spring:message code="label.measure.iw" /></th>
							<th width="2%" title='<spring:message code="label.title.measure.ew" />'><spring:message code="label.measure.ew" /></th>
							<th width="2%" title='<spring:message code="label.title.measure.inv" />'><spring:message code="label.measure.inv" /></th>
							<th width="2%" title='<spring:message code="label.title.measure.lt" />'><spring:message code="label.measure.lt" /></th>
							<th width="2%" title='<spring:message code="label.title.measure.im" />'><spring:message code="label.measure.im" /></th>
							<th width="2%" title='<spring:message code="label.title.measure.em" />'><spring:message code="label.measure.em" /></th>
							<th width="2%" title='<spring:message code="label.title.measure.ri" />'><spring:message code="label.measure.ri" /></th>
							<th width="2%" title='<spring:message code="label.title.measure.cost" />'><spring:message code="label.measure.cost" /></th>
							<th width="2%" title='<spring:message code="label.title.measure.phase" />'><spring:message code="label.measure.phase" /></th>
							<th width="2%" title='<spring:message code="label.title.measure.importance" />'><spring:message code="label.measure.importance" /></th>
							<th width="3%" title='<spring:message code="label.title.measure.responsible" />'><spring:message code="label.measure.responsible" /></th>
							<c:choose>
								<c:when test="${standardType.name.equals('NORMAL') || standardType.name.equals('ASSET')}">
									<th width="14%" title='<spring:message code="label.measure.tocheck" />'><spring:message code="label.measure.tocheck" /></th>
									<th width="25%" title='<spring:message code="label.comment" />'><spring:message code="label.comment" /></th>
									<th width="25%" title='<spring:message code="label.measure.todo" />'><spring:message code="label.measure.todo" /></th>
								</c:when>
								<c:otherwise>
									<th width="32%" title='<spring:message code="label.comment" />'><spring:message code="label.comment" /></th>
									<th width="32%" title='<spring:message code="label.measure.todo" />'><spring:message code="label.measure.todo" /></th>
								</c:otherwise>
							</c:choose>
						</tr>
					</thead>
					<tfoot>
					</tfoot>
					<tbody>
						<c:forEach items="${measuresByStandard.get(standard)}" var="measure">
							<c:set value="${measure.getImplementationRateValue(valueFactory)}" var="implementationRate" />
							<c:set var="css">
								<c:if test="${implementationRate < 100 and !(measure.status eq 'NA' || measure.status eq 'EX') }">class="editable"</c:if>
							</c:set>
							<c:set var="todoCSS">
								<c:choose>
									<c:when test="${empty measure.toDo && fn:contains(css,'editable')}">class="danger"</c:when>
									<c:otherwise>${css}</c:otherwise>
								</c:choose>
							</c:set>
							<c:set var="measureDescriptionText" value="${measure.measureDescription.getMeasureDescriptionTextByAlpha2(language)}" />
							<c:set var="dblclickaction">
								<c:if
									test="${isEditable and ( isAnalysisOnly or type.quantitative and measure.measureDescription.computable && selectedStandard.computable && selectedStandard.type!='MATURITY')}">
									ondblclick="return editMeasure(this,${standardid},${measure.id});"
								</c:if>
							</c:set>
							<c:set var="popoverRef">
								<c:if test="${not(empty measure.measureDescription.reference or empty measureDescriptionText.description)}">
									data-toggle="tooltip" data-container="body" data-trigger="click" data-placement='auto'
									data-title='<spring:message text="${measureDescriptionText.description}" />' style='cursor: pointer;'
								</c:if>
							</c:set>
							<c:set var="hasTicket" value="${isLinkedToProject and not empty measure.ticket}" />
							<c:choose>
								<c:when test="${not measure.measureDescription.computable}">
									<tr data-trick-computable="false" data-trick-reference='${measure.measureDescription.reference}' onclick="selectElement(this)" data-trick-class="Measure"
										class='active' data-trick-id="${measure.id}" data-is-linked='${hasTicket}' data-trick-callback="reloadMeasureRow('${measure.id}','${standardid}');"
										${dblclickaction}>
										<c:if test="${isLinkedToProject or isAnalysisOnly and isEditable}">
											<td><input type="checkbox" ${not isAnalysisOnly?'disabled':''} class="checkbox"
												onchange="return updateMenu(this,'#section_standard_${standardid}','#menu_standard_${standardid}');"></td>
										</c:if>
										<td><c:choose>
												<c:when test="${hasTicket}">
													<c:choose>
														<c:when test="${isNoClientTicketing}"><span style="white-space: nowrap;"><i class="fa fa-paper-plane" style="font-size: 8px;" aria-hidden="true"></i> <spring:message text="${measure.measureDescription.reference}" /></span></c:when>
														<c:otherwise>
															<spring:eval expression="T(lu.itrust.business.ts.model.ticketing.builder.ClientBuilder).TicketLink(ttSysName,ticketingURL,measure.ticket)" var="ticketLink" />
															<a href="${ticketLink}" target="_ticket_ts" class="btn btn-link btn-xs" style="padding-top:0; padding-left: 0"><span style="white-space: nowrap;"><i class="fa fa-link" style="font-size: 12px;" aria-hidden="true"></i> <spring:message text="${measure.measureDescription.reference}" /></span></a>
														</c:otherwise>
													</c:choose>
												</c:when>
												<c:otherwise>
												    <c:choose>
														<c:when test="${isNoClientTicketing}"><span style="white-space: nowrap;"><i class="fa fa-paper-plane-o" style="font-size: 8px;" aria-hidden="true"></i> <spring:message text="${measure.measureDescription.reference}" /></span></c:when>
														<c:when test="${isLinkedToProject}">
															<span style="white-space: nowrap;"><i class="fa fa-chain-broken" style="font-size: 10px" aria-hidden="true"></i> <spring:message text="${measure.measureDescription.reference}" /></span>
														</c:when>
														<c:otherwise>
															<spring:message text="${measure.measureDescription.reference}" />
														</c:otherwise>
													</c:choose>
												</c:otherwise>
											</c:choose></td>
										<c:choose>
											<c:when test="${hasMaturity and standard.equals('27002') }">
												<td ${popoverRef} colspan="15"><spring:message text="${!empty measureDescriptionText? measureDescriptionText.domain : ''}" /></td>
											</c:when>
											<c:otherwise>
												<td ${popoverRef} colspan="14"><spring:message text="${!empty measureDescriptionText? measureDescriptionText.domain : ''}" /></td>
											</c:otherwise>
										</c:choose>
										<c:choose>
											<c:when test="${standardType.name.equals('NORMAL') || standardType.name.equals('ASSET')}">
												<td class="warning" onclick="return editField(this);" data-trick-field="toCheck" data-trick-content="text" data-trick-field-type="string"><spring:message
														text="${measure.toCheck}" /></td>
												<td class="warning" onclick="return editField(this);" data-trick-field="comment" data-trick-content="text" data-trick-field-type="string"><spring:message
														text="${measure.comment}" /></td>
											</c:when>
											<c:otherwise>
												<td class='warning' onclick="return editField(this);" data-trick-field="comment" data-trick-content="text" data-trick-field-type="string"><spring:message
														text="${measure.comment}" /></td>
											</c:otherwise>
										</c:choose>
										<td></td>
									</tr>
								</c:when>
								<c:otherwise>
									<tr ${isAnalysisOnly?dblclickaction:''} data-trick-computable="true" data-trick-description="${measureDescriptionText.description}" onclick="selectElement(this)"
										data-trick-class="Measure" data-is-linked='${hasTicket}' data-trick-id="${measure.id}" data-trick-callback="reloadMeasureRow('${measure.id}','${standardid}');">
										<c:if test="${isLinkedToProject or  isAnalysisOnly and isEditable}">
											<td><input type="checkbox" ${measure.status eq 'NA' || measure.status eq 'EX' ?'disabled':''} class="checkbox"
												onchange="return updateMenu(this,'#section_standard_${standardid}','#menu_standard_${standardid}');"></td>
										</c:if>
										<td ${not isAnalysisOnly ?dblclickaction:''}><c:choose>
												<c:when test="${hasTicket}">
													<c:choose>
														<c:when test="${isNoClientTicketing}"><span style="white-space: nowrap;"><i class="fa fa-paper-plane" style="font-size: 8px;" aria-hidden="true"></i> <spring:message text="${measure.measureDescription.reference}" /></span></c:when>
														<c:otherwise>
															<spring:eval expression="T(lu.itrust.business.ts.model.ticketing.builder.ClientBuilder).TicketLink(ttSysName,ticketingURL,measure.ticket)" var="ticketLink" />
															<a href="${ticketLink}" target="_ticket_ts" style="padding-top:0; padding-left: 0" class="btn btn-link btn-xs"><span style="white-space: nowrap;"><i class="fa fa-link" style="font-size: 12px;" aria-hidden="true"></i> <spring:message text="${measure.measureDescription.reference}" /></span></a>
														</c:otherwise>
													</c:choose>
												</c:when>
												<c:otherwise>
												    <c:choose>
														<c:when test="${isNoClientTicketing}"><span style="white-space: nowrap;"><i class="fa fa-paper-plane-o" style="font-size: 8px;" aria-hidden="true"></i> <spring:message text="${measure.measureDescription.reference}" /></span></c:when>
														<c:when test="${isLinkedToProject}">
															<span style="white-space: nowrap;"><i class="fa fa-chain-broken" style="font-size: 10px" aria-hidden="true"></i> <spring:message text="${measure.measureDescription.reference}" /></span>
														</c:when>
														<c:otherwise>
															<spring:message text="${measure.measureDescription.reference}" />
														</c:otherwise>
													</c:choose>
													
												</c:otherwise>
											</c:choose></td>
										<td ${popoverRef}><spring:message text="${!empty measureDescriptionText? measureDescriptionText.domain : ''}" /></td>
										<td ${css} data-trick-field="status" data-trick-choose="M,AP,OP,EX,NA" data-trick-choose-translate="${statusM},${statusAP},${statusOP},${statusEX},${statusNA}"
											data-trick-choose-title='${titleStatusM},${titleStatusAP},${titleStatusOP},${titleStatusEX},${titleStatusNA}' data-trick-field-type="string" onclick="return editField(this);"
											data-trick-callback="reloadMeasureAndCompliance('${standardid}','${measure.id}')"><c:choose>
												<c:when test="${measure.status=='NA'}">
													${statusNA}
												</c:when>
												<c:when test="${measure.status=='AP'}">
													${statusAP}
												</c:when>
												<c:when test="${measure.status=='OP'}">
													${statusOP}
												</c:when>
												<c:when test="${measure.status=='EX'}">
													${statusEX}
												</c:when>
												<c:otherwise>
													${statusM}
												</c:otherwise>
											</c:choose></td>
										<fmt:formatNumber value="${implementationRate}" maxFractionDigits="0" minFractionDigits="0" var="implementationRateValue" />
										<c:choose>
											<c:when test="${standardType.name.equals('MATURITY')}">
												<td ${css} data-trick-field="implementationRate" data-trick-class="MaturityMeasure" data-trick-field-type="double" title="${implementationRateValue} %"
													data-trick-callback="reloadMeasureAndCompliance('${standardid}','${measure.id}');updateMeasureEffience('${measure.measureDescription.reference}');"
													onclick="return editField(this);">${implementationRateValue}</td>
											</c:when>
											<c:otherwise>
												<td ${css} data-trick-field="implementationRate" data-trick-field-type="string" ${implementationRateAttr} title="${implementationRateValue} %"
													data-trick-callback="reloadMeasureAndCompliance('${standardid}','${measure.id}')" onclick="return editField(this);">${implementationRateValue}</td>
												<c:if test="${hasMaturity and standard.equals('27002') }">
													<td class="text-center" data-trick-field='mer'><c:choose>
															<c:when test="${empty effectImpl27002[measure.measureDescription.reference]}">0</c:when>
															<c:otherwise>
																<fmt:formatNumber value="${effectImpl27002[measure.measureDescription.reference]}" maxFractionDigits="0" minFractionDigits="0" />
															</c:otherwise>
														</c:choose></td>
												</c:if>
											</c:otherwise>
										</c:choose>
										<td ${css} data-trick-field="internalWL" data-trick-field-type="double" onclick="return editField(this);" data-trick-min-value='0'><fmt:formatNumber
												value="${measure.internalWL}" maxFractionDigits="2" /></td>
										<td ${css} data-trick-field="externalWL" data-trick-field-type="double" onclick="return editField(this);" data-trick-min-value='0'><fmt:formatNumber
												value="${measure.externalWL}" maxFractionDigits="2" /></td>
										<td ${css} data-trick-field="investment" data-trick-field-type="double" onclick="return editField(this);" data-trick-min-value='0'
											title='<fmt:formatNumber value="${fct:round(measure.investment,0)}" maxFractionDigits="0" /> &euro;'
											data-real-value='<fmt:formatNumber value="${measure.investment*0.001}" maxFractionDigits="2" />'><fmt:formatNumber maxFractionDigits="0"
												value="${fct:round(measure.investment*0.001,0)}" /></td>
										<td ${css} data-trick-field="lifetime" data-trick-field-type="double" onclick="return editField(this);" data-trick-min-value='0'><fmt:formatNumber
												value="${measure.lifetime}" maxFractionDigits="2" /></td>
										<td ${css} data-trick-field="internalMaintenance" data-trick-field-type="double" onclick="return editField(this);"><fmt:formatNumber
												value="${measure.internalMaintenance}" maxFractionDigits="2" /></td>
										<td ${css} data-trick-field="externalMaintenance" data-trick-field-type="double" onclick="return editField(this);"><fmt:formatNumber
												value="${measure.externalMaintenance}" maxFractionDigits="2" /></td>
										<td ${css} data-trick-field="recurrentInvestment" data-trick-field-type="double" onclick="return editField(this);"
											title='<fmt:formatNumber value="${fct:round(measure.recurrentInvestment,0)}" maxFractionDigits="0" /> &euro;'
											data-real-value='<fmt:formatNumber value="${measure.recurrentInvestment*0.001}" maxFractionDigits="2" />'><fmt:formatNumber
												value="${fct:round(measure.recurrentInvestment*0.001,0)}" maxFractionDigits="0" /></td>
										<c:choose>
											<c:when test="${implementationRateValue >= 100 || measure.status eq 'NA' || measure.status eq 'EX'}">
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
										<td ${css} onclick="return editField(this);" data-trick-choose="1,2,3" data-trick-choose-translate='${imp1},${imp2},${imp3}' data-trick-choose-title='${titleImp1},${titleImp2},${titleImp3}' data-trick-field="importance" data-trick-field-type="integer"><c:choose>
											<c:when test="${measure.importance eq 1}">${imp1}</c:when>
											<c:when test="${measure.importance eq 2}">${imp2}</c:when>
											<c:when test="${measure.importance eq 3}">${imp3}</c:when>
										</c:choose></td>
										<td ${css} onclick="return editField(this);" data-trick-field="responsible" data-trick-field-type="string"><spring:message text="${measure.responsible}" /></td>
										<c:if test="${standardType.name.equals('NORMAL') || standardType.name.equals('ASSET')}">
											<td ${css} onclick="return editField(this);" data-trick-field="toCheck" data-trick-content="text" data-trick-field-type="string"><spring:message
													text="${measure.toCheck}" /></td>
										</c:if>
										<td ${css} onclick="return editField(this);" data-trick-callback="tryToReloadSOA('${standardid}','${measure.id}')" data-trick-field="comment" data-trick-content="text" data-trick-field-type="string"><spring:message
												text="${measure.comment}" /></td>
										<td ${todoCSS} onclick="return editField(this);" data-trick-field="toDo" data-trick-content="text" data-trick-field-type="string"><spring:message
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