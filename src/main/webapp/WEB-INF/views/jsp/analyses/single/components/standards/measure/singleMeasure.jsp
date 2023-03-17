<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="fct" uri="https://trickservice.com/tags/functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<c:if test="${empty locale }">
	<spring:eval expression="T(org.springframework.web.servlet.support.RequestContextUtils).getLocale(pageContext.request)" var="locale" scope="request" />
</c:if>
<c:set var="language" value="${locale.language}" scope="request" />
<spring:message code="label.measure.status.m" var="statusM" />
<spring:message code="label.measure.status.ap" var="statusAP" />
<spring:message code="label.measure.status.na" var="statusNA" />
<spring:message code="label.title.measure.status.m" var="titleStatusM" />
<spring:message code="label.title.measure.status.ap" var="titleStatusAP" />
<spring:message code="label.title.measure.status.na" var="titleStatusNA" />
<c:forEach begin="1" step="1" end="3" var="impValue">
	<spring:message code="label.measure.importance.value" arguments="${impValue}" var="imp${impValue}" />
	<spring:message code="label.title.measure.importance.value" arguments="${impValue}" var="titleImp${impValue}" />
</c:forEach>
<fmt:setLocale value="fr" scope="session" />
<c:set value="${measure.getImplementationRateValue(valueFactory)}" var="implementationRate" />
<c:set var="css">
	<c:if test="${implementationRate < 100 and measure.status!='NA'}">class="editable"</c:if>
</c:set>
<c:set var="measureDescriptionText" value="${measure.measureDescription.getMeasureDescriptionTextByAlpha2(language)}" />
<c:set var="dblclickaction">
	<c:if test="${isEditable and ( isAnalysisOnly or type.quantitative and measure.measureDescription.computable && selectedStandard.computable && selectedStandard.type!='MATURITY')}">
		ondblclick="return editMeasure(this,${standardid},${measure.id});"
	</c:if>
</c:set>
<c:set var="todoCSS">
	<c:choose>
		<c:when test="${empty measure.toDo && fn:contains(css,'editable')}">class="danger"</c:when>
		<c:otherwise>${css}</c:otherwise>
	</c:choose>
</c:set>
<c:set var="popoverRef">
	<c:if test="${not(empty measure.measureDescription.reference or empty measureDescriptionText.description)}">
		data-toggle="tooltip" data-container="body" data-trigger="click" data-placement='auto'
		data-title='<spring:message text="${measureDescriptionText.description}" />' style='cursor: pointer;'
	</c:if>
</c:set>
<c:set var="implementationRateAttr">
	<c:choose>
		<c:when test="${type == 'QUALITATIVE' or not showDynamicAnalysis }">
			data-trick-min-value='0' data-trick-max-value='100' data-trick-step-value='1'
		</c:when>
		<c:otherwise>data-trick-list-value="dataListImplementationRate"</c:otherwise>
	</c:choose>
</c:set>
<c:set var="hasTicket" value="${isLinkedToProject and not empty measure.ticket}" />
<c:choose>
	<c:when test="${not measure.measureDescription.computable}">
		<tr data-trick-computable="false" onclick="selectElement(this)" data-trick-class="Measure" class='active' data-trick-id="${measure.id}"
			data-is-linked='${isLinkedToProject and not empty measure.ticket}' data-trick-callback="reloadMeasureRow('${measure.id}','${standardid}');" ${dblclickaction}>
			<c:if test="${isLinkedToProject or  isAnalysisOnly and isEditable}">
				<td><input type="checkbox" class="checkbox" onchange="return updateMenu(this,'#section_standard_${standardid}','#menu_standard_${standardid}');"></td>
			</c:if>
			<td><c:choose>
					<c:when test="${hasTicket}">
						<c:choose>
							<c:when test="${isNoClientTicketing}"><span style="white-space: nowrap;"><i class="fa fa-paper-plane" style="font-size: 8px;" aria-hidden="true"></i> <spring:message text="${measure.measureDescription.reference}" /></span></c:when>
							<c:otherwise>
								<c:set var="ttSysName" value="${fn:toLowerCase(ticketingName)}" />
								<spring:eval expression="T(lu.itrust.business.TS.model.ticketing.builder.ClientBuilder).TicketLink(ttSysName,ticketingURL,measure.ticket)" var="ticketLink" />
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
			<td ${popoverRef} colspan="${selectedStandard.label=='27002' and hasMaturity? '14' : '13' }"><spring:message
					text="${!empty measureDescriptionText? measureDescriptionText.domain : ''}" /></td>
			<c:choose>
				<c:when test="${standardType.name.equals('NORMAL') || standardType.name.equals('ASSET')}">
					<td class='warning' onclick="return editField(this);" data-trick-field="toCheck" data-trick-content="text" data-trick-field-type="string"><spring:message
							text="${measure.toCheck}" /></td>
					<td class='warning' onclick="return editField(this);" data-trick-field="comment" data-trick-content="text" data-trick-field-type="string"><spring:message
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
		<tr ${isAnalysisOnly?dblclickaction:''} data-trick-class="Measure" data-trick-id="${measure.id}" data-trick-reference='${measure.measureDescription.reference}'
			onclick="selectElement(this)" data-trick-callback="reloadMeasureRow('${measure.id}','${standardid}');" data-is-linked='${isLinkedToProject and not empty measure.ticket}'>
			<c:set var="measureDescriptionText" value="${measure.measureDescription.getMeasureDescriptionTextByAlpha2(language)}" />
			<c:if test="${isLinkedToProject or  isAnalysisOnly and isEditable}">
				<td><input type="checkbox" ${measure.status=='NA'?'disabled':''} class="checkbox"
					onchange="return updateMenu(this,'#section_standard_${standardid}','#menu_standard_${standardid}');"></td>
			</c:if>
			<td ${not isAnalysisOnly?dblclickaction:''}><c:choose>
					<c:when test="${hasTicket}">
						<c:choose>
							<c:when test="${isNoClientTicketing}"><span style="white-space: nowrap;"><i class="fa fa-paper-plane" style="font-size: 8px;" aria-hidden="true"></i> <spring:message text="${measure.measureDescription.reference}" /></span></c:when>
							<c:otherwise>
							     <c:set var="ttSysName" value="${fn:toLowerCase(ticketingName)}" />
								<spring:eval expression="T(lu.itrust.business.TS.model.ticketing.builder.ClientBuilder).TicketLink(ttSysName,ticketingURL,measure.ticket)" var="ticketLink" />
								<a href="${ticketLink}" target="_ticket_ts" class="btn btn-link btn-xs" style="padding-top:0; padding-left: 0" class="btn btn-link"><span style="white-space: nowrap;"><i class="fa fa-link" style="font-size: 12px;" aria-hidden="true"></i> <spring:message text="${measure.measureDescription.reference}" /></span></a>
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
			<td ${css} data-trick-field="status" data-trick-choose="M,AP,NA" data-trick-choose-translate='${statusM},${statusAP},${statusNA}'
				data-trick-choose-title='${titleStatusM},${titleStatusAP},${titleStatusNA}' data-trick-callback="reloadMeasureAndCompliance('${standardid}','${measure.id}')"
				data-trick-field-type="string" onclick="return editField(this);"><c:choose>
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
			<fmt:formatNumber value="${implementationRate}" maxFractionDigits="0" minFractionDigits="0" var="implementationRateValue" />
			<c:choose>
				<c:when test="${standardType.name.equals('MATURITY')}">
					<td ${css} data-trick-field="implementationRate" data-trick-class="MaturityMeasure" data-trick-field-type="double" title="${implementationRateValue} %"
						data-trick-callback="reloadMeasureAndCompliance('${standardid}','${measure.id}');updateMeasureEffience('${measure.measureDescription.reference}');"
						onclick="return editField(this);">${implementationRateValue}</td>
				</c:when>
				<c:otherwise>
					<td ${css} data-trick-field="implementationRate" ${implementationRateAttr} data-trick-field-type="string"
						data-trick-callback="reloadMeasureAndCompliance('${standardid}','${measure.id}')" title="${implementationRateValue} %" onclick="return editField(this);">${implementationRateValue}</td>
					<c:if test="${selectedStandard.label=='27002' and hasMaturity}">
						<td class="text-center" data-trick-field='mer'><c:choose>
								<c:when test="${empty effectImpl27002}">0</c:when>
								<c:otherwise>
									<fmt:formatNumber value="${effectImpl27002}" maxFractionDigits="0" minFractionDigits="0" />
								</c:otherwise>
							</c:choose></td>
					</c:if>
				</c:otherwise>
			</c:choose>
			<td ${css} data-trick-field="internalWL" data-trick-field-type="double" onclick="return editField(this);"><fmt:formatNumber value="${measure.internalWL}"
					maxFractionDigits="2" /></td>
			<td ${css} data-trick-field="externalWL" data-trick-field-type="double" onclick="return editField(this);"><fmt:formatNumber value="${measure.externalWL}"
					maxFractionDigits="2" /></td>
			<td ${css} data-trick-field="investment" data-trick-field-type="double" onclick="return editField(this);"
				title='<fmt:formatNumber value="${fct:round(measure.investment,0)}" maxFractionDigits="0" /> &euro;'
				data-real-value='<fmt:formatNumber value="${measure.investment*0.001}" maxFractionDigits="2" />'><fmt:formatNumber maxFractionDigits="0"
					value="${fct:round(measure.investment*0.001,0)}" /></td>
			<td ${css} data-trick-field="lifetime" data-trick-field-type="double" onclick="return editField(this);"><fmt:formatNumber value="${measure.lifetime}" maxFractionDigits="2" /></td>
			<td ${css} data-trick-field="internalMaintenance" data-trick-field-type="double" onclick="return editField(this);"><fmt:formatNumber value="${measure.internalMaintenance}"
					maxFractionDigits="2" /></td>
			<td ${css} data-trick-field="externalMaintenance" data-trick-field-type="double" onclick="return editField(this);"><fmt:formatNumber value="${measure.externalMaintenance}"
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
			<td ${css} onclick="return editField(this);" data-trick-choose="1,2,3" data-trick-choose-translate='${imp1},${imp2},${imp3}' data-trick-choose-title='${titleImp1},${titleImp2},${titleImp3}' data-trick-field="importance" data-trick-field-type="integer"><c:choose>
				<c:when test="${measure.importance eq 1}">${imp1}</c:when>
				<c:when test="${measure.importance eq 2}">${imp2}</c:when>
				<c:when test="${measure.importance eq 3}">${imp3}</c:when>
			</c:choose></td>
			<td ${css} onclick="return editField(this);" data-trick-field="responsible" data-trick-field-type="string"><spring:message text="${measure.responsible}" /></td>
			<c:if test="${standardType.name.equals('NORMAL') || standardType.name.equals('ASSET')}">
				<td ${css} onclick="return editField(this);" data-trick-field="toCheck" data-trick-content="text" data-trick-field-type="string"><spring:message text="${measure.toCheck}" /></td>
			</c:if>
			<td ${css} onclick="return editField(this);" data-trick-field="comment" data-trick-callback="tryToReloadSOA('${standardid}','${measure.id}')" data-trick-content="text"
				data-trick-field-type="string"><spring:message text="${measure.comment}" /></td>
			<td ${todoCSS} onclick="return editField(this);" data-trick-field="toDo" data-trick-content="text" data-trick-field-type="string"><spring:message text="${measure.toDo}" /></td>
		</tr>
	</c:otherwise>
</c:choose>