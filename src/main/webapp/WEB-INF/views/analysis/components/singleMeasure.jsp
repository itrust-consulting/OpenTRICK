<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<c:set var="css"><c:if test="${not(measure.implementationRateValue==100 or measure.status=='NA')}">class="success"</c:if></c:set>
<tr trick-class="Measure" trick-id="${measure.id}" trick-callback="reloadMeasureRow('${measure.id}','${norm}');">
	<c:set var="measureDescriptionText" value="${measure.measureDescription.getMeasureDescriptionTextByAlpha3(language)}" />
	<c:choose>
	<c:when test="${empty measureDescriptionText or empty(measureDescriptionText.description)}">
	<td colspan="2" class="popover-element" data-toggle="popover" data-container="body" data-placement="right" data-trigger="hover" data-html="true"
		data-content=''
		title='<spring:message
	text="${measure.measureDescription.reference}" />'><spring:message text="${measure.measureDescription.reference}" /></td>
	</c:when>
	<c:otherwise>
	<td colspan="2" class="popover-element" data-toggle="popover" data-container="body" data-placement="right" data-trigger="hover" data-html="true"
		data-content='<pre><spring:message text="${measureDescriptionText.description}" /></pre>'
	title='<spring:message
	text="${measure.measureDescription.reference}" />'><spring:message text="${measure.measureDescription.reference}" /></td>
	</c:otherwise>
	</c:choose>
	<td colspan="5"><spring:message text="${!empty measureDescriptionText? measureDescriptionText.domain : ''}" /></td>
	<td ${css} textaligncenter" trick-field="status" trick-choose="M,AP,NA" trick-field-type="string" ondblclick="return editField(this);"><spring:message
			text="${measure.status}" /></td>
	<c:choose>
		<c:when test="${norm.equalsIgnoreCase('Custom')==true}">
			<td ${css} textaligncenter" trick-field="implementationRate" trick-field-type="double" ondblclick="return editField(this);"><fmt:formatNumber
					value="${measure.getImplementationRateValue()}" maxFractionDigits="0" minFractionDigits="0" /></td>
		</c:when>
		<c:when test="${!norm.equalsIgnoreCase('Maturity')}">
			<td ${css} trick-field="implementationRate" trick-field-type="double" trick-callback="reloadMeausreAndCompliance('${norm}','${measure.id}')"
				ondblclick="return editField(this);"><fmt:formatNumber value="${measure.getImplementationRateValue()}" maxFractionDigits="0" minFractionDigits="0" /></td>
		</c:when>
		<c:otherwise>
			<td ${css} trick-field="implementationRate" trick-field-type="double" ondblclick="return editField(this);" trick-class="MaturityMeasure" trick-id="${measure.id}"><fmt:formatNumber
					value="${measure.getImplementationRateValue()}" maxFractionDigits="0" minFractionDigits="0" /></td>
		</c:otherwise>
	</c:choose>
	<td ${css} trick-field="internalWL" trick-field-type="double" ondblclick="return editField(this);"><fmt:formatNumber value="${measure.internalWL}"
			maxFractionDigits="2" /></td>
	<td ${css} trick-field="externalWL" trick-field-type="double" ondblclick="return editField(this);"><fmt:formatNumber value="${measure.externalWL}"
			maxFractionDigits="2" /></td>
	<td ${css} trick-field="investment" trick-field-type="double" ondblclick="return editField(this);" title='<fmt:formatNumber value="${measure.investment}" />&euro;'
		real-value='<fmt:formatNumber
			value="${measure.investment*0.001}" maxFractionDigits="2" />'><fmt:formatNumber value="${measure.investment*0.001}" maxFractionDigits="0" /></td>
	<td ${css} trick-field="lifetime" trick-field-type="double" ondblclick="return editField(this);"><fmt:formatNumber value="${measure.lifetime}" maxFractionDigits="2" /></td>
	<td ${css} trick-field="internalMaintenance" trick-field-type="double" ondblclick="return editField(this);"><fmt:formatNumber value="${measure.internalMaintenance}"
			maxFractionDigits="2" /></td>
	<td ${css} trick-field="externalMaintenance" trick-field-type="double" ondblclick="return editField(this);"><fmt:formatNumber value="${measure.externalMaintenance}"
			maxFractionDigits="2" /></td>
	<td ${css} trick-field="recurrentInvestment" trick-field-type="double" ondblclick="return editField(this);"
		title='<fmt:formatNumber value="${measure.recurrentInvestment}" />&euro;' real-value='<fmt:formatNumber
			value="${measure.recurrentInvestment*0.001}" maxFractionDigits="2" />'><fmt:formatNumber
			value="${measure.recurrentInvestment*0.001}" maxFractionDigits="0" /></td>
	<c:set var="cost">
		<fmt:formatNumber value="${measure.cost*0.001}" maxFractionDigits="0" />
	</c:set>
	<c:choose>
		<c:when test="${measure.getImplementationRateValue()==100 || measure.getStatus().equals('NA')}">
			<td class='textaligncenter' title='<fmt:formatNumber value="${measure.cost}"/>&euro;'>${cost}</td>
		</c:when>
		<c:otherwise>
			<td ${measure.cost == 0? "class='textaligncenter danger'" : "class='textaligncenter'" } title='<fmt:formatNumber value="${measure.cost}"/>&euro;'>${cost}</td>
		</c:otherwise>
	</c:choose>
	<td ${css} trick-field="phase" trick-field-type="integer" ondblclick="return editField(this);" trick-callback-pre="extractPhase(this)" real-value='${measure.phase.number}'><c:choose>
			<c:when test="${measure.phase.number == 0}">NA</c:when>
			<c:otherwise>${measure.phase.number}</c:otherwise>
		</c:choose></td>
	<c:if test="${measure.getClass().name.equals('lu.itrust.business.TS.NormMeasure')}">
		<td colspan="8" ${css} ondblclick="return editField(this.firstElementChild);"><pre trick-field="toCheck" trick-content="text" trick-field-type="string"><spring:message text="${measure.toCheck}" /></pre></td>
	</c:if>
	<td colspan="8" ${css} ondblclick="return editField(this.firstElementChild);"><pre trick-field="comment" trick-content="text" trick-field-type="string"><spring:message text="${measure.comment}" /></pre></td>
	<td colspan="8" ${css} ondblclick="return editField(this.firstElementChild);"><pre trick-field="toDo" trick-content="text" trick-field-type="string"><spring:message text="${measure.toDo}" /></pre></td>
	
</tr>