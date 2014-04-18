<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<fmt:setLocale value="fr_FR" scope="session" />
<tr trick-class="Measure" trick-id="${measure.id}" trick-callback="reloadMeasureRow('${measure.id}','${norm}');">
	<c:set var="measureDescriptionText" value="${measure.measureDescription.getMeasureDescriptionTextByAlpha3(language)}" />
	<td><a href="#" class="descriptiontooltip" data-toggle="tooltip" data-html="true"
		title="<spring:message text="${!empty measureDescriptionText? measureDescriptionText.description : ''}" />"> <spring:message text="${measure.measureDescription.reference}" />
	</a></td>
	<td><div class="headertofixtablelargecolumn">
			<spring:message text="${!empty measureDescriptionText? measureDescriptionText.domain : ''}" />
		</div></td>
	<td class="success textaligncenter" trick-field="status" trick-choose="M,AP,NA" trick-field-type="string" ondblclick="return editField(this);"><spring:message
			text="${measure.status}" /></td>
	<c:choose>
		<c:when test="${norm.equalsIgnoreCase('Custom')==true}">
			<td class="success textaligncenter" trick-field="implementationRate" trick-field-type="double" ondblclick="return editField(this);"><fmt:formatNumber
					value="${measure.getImplementationRateValue()}" maxFractionDigits="0" minFractionDigits="0" /></td>
		</c:when>
		<c:when test="${!norm.equalsIgnoreCase('Maturity')}">
			<td class="success textaligncenter" trick-field="implementationRate" trick-field-type="double" trick-callback="reloadMeausreAndCompliance('${norm}','${measure.id}')"
				ondblclick="return editField(this);"><fmt:formatNumber value="${measure.getImplementationRateValue()}" maxFractionDigits="0" minFractionDigits="0" /></td>
		</c:when>
		<c:otherwise>
			<td class="success textaligncenter" trick-field="implementationRate" trick-field-type="double" ondblclick="return editField(this);" trick-class="MaturityMeasure"
				trick-id="${measure.id}"><fmt:formatNumber value="${measure.getImplementationRateValue()}" maxFractionDigits="0" minFractionDigits="0" /></td>
		</c:otherwise>
	</c:choose>
	<c:set var="internalWL">
		<fmt:formatNumber value="${measure.internalWL}" maxFractionDigits="1" minFractionDigits="1" />
	</c:set>
	<td class="success textaligncenter" trick-field="internalWL" trick-field-type="double" ondblclick="return editField(this);" real-value="${fn:replace(internalWL,',' ,'.') }">${internalWL}</td>
	<c:set var="externalWL">
		<fmt:formatNumber value="${measure.externalWL}" maxFractionDigits="1" minFractionDigits="1" />
	</c:set>
	<td class="success textaligncenter" trick-field="externalWL" trick-field-type="double" ondblclick="return editField(this);" real-value="${fn:replace(externalWL,',' ,'.') }">${externalWL}</td>
	<c:set var="investment">
		<fmt:formatNumber minFractionDigits="1" maxFractionDigits="1" value="${measure.investment*0.001}" />
	</c:set>
	<td class="success textaligncenter" trick-field="investment" trick-field-type="double" ondblclick="return editField(this);" title="${measure.investment}&euro;"
		real-value="${fn:replace(investment,',' ,'.') }">${investment}</td>
	<c:set var="lifetime">
		<fmt:formatNumber value="${measure.lifetime}" maxFractionDigits="1" minFractionDigits="1" />
	</c:set>
	<td class="success textaligncenter" trick-field="lifetime" trick-field-type="double" ondblclick="return editField(this);" real-value="${fn:replace(lifetime,',' ,'.') }">${lifetime}</td>
	<td class="success textaligncenter" trick-field="maintenance" trick-field-type="double" ondblclick="return editField(this);"><fmt:formatNumber value="${measure.maintenance}"
			maxFractionDigits="0" minFractionDigits="0" /></td>
	<td ${measure.cost == 0? "class='textaligncenter danger'" : "class='textaligncenter'" } title="${measure.cost}&euro;"><fmt:formatNumber value="${measure.cost*0.001}"
			maxFractionDigits="0" /></td>
	<td class="success textaligncenter" trick-field="phase" trick-field-type="integer" ondblclick="return editField(this);" trick-callback-pre="extractPhase(this)"
		real-value='${measure.phase.number}'><c:choose>
			<c:when test="${measure.phase.number == 0}">NA</c:when>
			<c:otherwise>${measure.phase.number}</c:otherwise>
		</c:choose></td>
	<td class="success" ondblclick="return editField(this.firstElementChild);">
		<div class="headertofixtablelargecolumn" trick-field="comment" trick-content="text" trick-field-type="string">
			<spring:message text="${measure.comment}" />
		</div>
	</td>
	<td class="success" ondblclick="return editField(this.firstElementChild);">
		<div class="headertofixtablelargecolumn" trick-field="toDo" trick-content="text" trick-field-type="string">
			<spring:message text="${measure.toDo}" />
		</div>
	</td>
</tr>