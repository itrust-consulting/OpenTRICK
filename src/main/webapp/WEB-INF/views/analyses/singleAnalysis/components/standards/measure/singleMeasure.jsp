<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="fct" uri="http://trickservice.itrust.lu/JSTLFunctions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<fmt:setLocale value="fr" scope="session" />
<c:set var="css">
	<c:if test="${not(measure.implementationRateValue==100 or measure.status=='NA')}">class="success"</c:if>
</c:set>
<c:set var="measureDescriptionText" value="${measure.measureDescription.getMeasureDescriptionTextByAlpha2(language)}" />
<c:set var="dblclickaction">
	<c:if test="${isEditable && (isAnalysisOnly || measure.analysisStandard.standard.computable && measure.analysisStandard.standard.type!='MATURITY' && measure.measureDescription.computable) }">
		ondblclick="return editMeasure(this,${standardid},${measure.id});"
	</c:if>
</c:set>
<c:choose>
	<c:when test="${measure.measureDescription.computable==false}">
		<tr data-trick-computable="false" data-trick-level="${measure.measureDescription.level}" data-trick-class="Measure" style="background-color: #F8F8F8;" data-trick-id="${measure.id}"
			data-trick-callback="reloadMeasureRow('${measure.id}','${standardid}');" ${dblclickaction}>
			<c:if test="${isAnalysisOnly and isEditable}">
				<td><input type="checkbox" class="checkbox" onchange="return updateMenu(this,'#section_standard_${standardid}','#menu_standard_${standardid}');"></td>
			</c:if>
			<td><spring:message text="${measure.measureDescription.reference}" /></td>
			<td colspan="${standardType.name.equals('NORMAL') || standardType.name.equals('ASSET')?'17':'16'}"><spring:message
					text="${!empty measureDescriptionText? measureDescriptionText.domain : ''}" /></td>
		</tr>
	</c:when>
	<c:otherwise>
		<tr data-trick-class="Measure" data-trick-id="${measure.id}" data-trick-callback="reloadMeasureRow('${measure.id}','${standardid}');">
			<c:set var="measureDescriptionText" value="${measure.measureDescription.getMeasureDescriptionTextByAlpha2(language)}" />
			<c:if test="${isAnalysisOnly and isEditable}">
				<td><input type="checkbox" class="checkbox" onchange="return updateMenu(this,'#section_standard_${standardid}','#menu_asset_standard');"></td>
			</c:if>
			<td class="popover-element" data-toggle="popover" data-container="body" data-placement="right" data-trigger="hover" data-html="true"
				data-content='<pre><spring:message text="${measureDescriptionText.description}" /></pre>' title='<spring:message
	text="${measure.measureDescription.reference}" />'
				${dblclickaction}><spring:message text="${measure.measureDescription.reference}" /></td>
			<td ${dblclickaction}><spring:message text="${!empty measureDescriptionText? measureDescriptionText.domain : ''}" /></td>
			<td ${css} data-trick-field="status" data-trick-choose="M,AP,NA" data-trick-field-type="string" onclick="return editField(this);"><spring:message
					text="${measure.status}" /></td>
			<td ${css} data-trick-field="implementationRate" ${standardType.name.equals('MATURITY')?'data-trick-class="MaturityMeasure"':''} data-trick-field-type="double"
				data-trick-callback="reloadMeasureAndCompliance('${standardid}','${measure.id}')" onclick="return editField(this);"><fmt:formatNumber
					value="${measure.getImplementationRateValue()}" maxFractionDigits="0" minFractionDigits="0" /></td>
			<td ${css} data-trick-field="internalWL" data-trick-field-type="double" onclick="return editField(this);"><fmt:formatNumber value="${measure.internalWL}" maxFractionDigits="2" /></td>
			<td ${css} data-trick-field="externalWL" data-trick-field-type="double" onclick="return editField(this);"><fmt:formatNumber value="${measure.externalWL}" maxFractionDigits="2" /></td>
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
				<c:when test="${measure.getImplementationRateValue()==100 || measure.getStatus().equals('NA')}">
					<td class='textaligncenter' title='<fmt:formatNumber value="${fct:round(measure.cost,0)}" maxFractionDigits="0" /> &euro;'><fmt:formatNumber
							value="${fct:round(measure.cost*0.001,0)}" maxFractionDigits="0" /></td>
				</c:when>
				<c:otherwise>
					<td ${measure.cost == 0? "class='textaligncenter danger'" : "class='textaligncenter'" }
						title='<fmt:formatNumber value="${fct:round(measure.cost,0)}" maxFractionDigits="0" /> &euro;'><fmt:formatNumber value="${fct:round(measure.cost*0.001,0)}"
							maxFractionDigits="0" /></td>
				</c:otherwise>
			</c:choose>
			<td ${css} data-trick-field="phase" data-trick-field-type="integer" onclick="return editField(this);" data-trick-callback-pre="extractPhase(this)" data-real-value='${measure.phase.number}'><c:choose>
					<c:when test="${measure.phase.number == 0}">NA</c:when>
					<c:otherwise>${measure.phase.number}</c:otherwise>
				</c:choose></td>
			<c:if test="${standardType.name.equals('NORMAL') || standardType.name.equals('ASSET')}">
				<td ${css} onclick="return editField(this.firstElementChild);"><pre data-trick-field="toCheck" data-trick-content="text" data-trick-field-type="string"><spring:message text="${measure.toCheck}" /></pre></td>
			</c:if>
			<td ${css} onclick="return editField(this.firstElementChild);"><pre data-trick-field="comment" data-trick-content="text" data-trick-field-type="string"><spring:message text="${measure.comment}" /></pre></td>
			<td ${css} onclick="return editField(this.firstElementChild);"><pre data-trick-field="toDo" data-trick-content="text" data-trick-field-type="string"><spring:message text="${measure.toDo}" /></pre></td>
			<td ${css} onclick="return editField(this);" data-trick-field="responsible"  data-trick-field-type="string"><spring:message text="${measure.responsible}" /></td>
		</tr>
	</c:otherwise>
</c:choose>
<fmt:setLocale value="${language}" scope="session" />