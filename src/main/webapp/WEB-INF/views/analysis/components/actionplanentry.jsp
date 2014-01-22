<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<tr trick-class="ActionPlanEntry" trick-id="${actionplanentry.id}" trick-callback="reloadActionPlanEntryRow('${actionplanentry.id}','${actionplanentry.actionPlanType.getName()}')">
	<td><spring:message text="${actionplanentry.position}" /></td>
	<td><spring:message text="${actionplanentry.measure.analysisNorm.norm.label}" /></td>
	<td><spring:message text="${actionplanentry.measure.measureDescription.reference}" /></td>
	<td ${actionplanentry.totalALE == 0? "class='danger'" : "" } title="${actionplanentry.totalALE}"><fmt:formatNumber value="${actionplanentry.totalALE*0.001}" maxFractionDigits="0" /></td>
	<td ${actionplanentry.deltaALE == 0? "class='danger'" : "" } title="${actionplanentry.deltaALE}"><fmt:formatNumber value="${actionplanentry.deltaALE*0.001}" maxFractionDigits="0" /></td>
	<td ${actionplanentry.measure.cost == 0? "class='danger'" : "" } title="${actionplanentry.measure.cost}"><fmt:formatNumber value="${actionplanentry.measure.cost*0.001}" maxFractionDigits="0" /></td>
	<td ${actionplanentry.ROI == 0? "class='danger'" : "" } title="${actionplanentry.ROI}"><fmt:formatNumber value="${actionplanentry.ROI*0.001}" maxFractionDigits="0" /></td>
	<td class="success" trick-field="phase" trick-field-type="integer" ondblclick="return editField(this);" trick-callback-pre="extractPhase(this)"
		trick-real-value='${actionplanentry.measure.phase.number}'>${actionplanentry.measure.phase.number}</td>
	<c:forEach items="${actionplanentry.actionPlanAssets}" var="apa">
		<td class="actionplanassethidden" title="${apa.currentALE}"><fmt:formatNumber value="${apa.currentALE*0.001}" maxFractionDigits="0" /></td>
	</c:forEach>
</tr>