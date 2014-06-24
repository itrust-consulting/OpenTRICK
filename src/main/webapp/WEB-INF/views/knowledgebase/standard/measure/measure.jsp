<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ page trimDirectiveWhitespaces="true"%>
<c:if test="${!empty(measureDescription)}">
	<tr trick-id="${measureDescription.id}" ondblclick="return editSingleMeasure('${measureDescription.id}','${norm.id}');">
		<td><input type="checkbox" class="checkbox" onchange="return updateMenu('#section_measure_description','#menu_measure_description');"></td>
		<td>${measureDescription.level}</td>
		<td>${measureDescription.reference}</td>
		<td>${measureDescriptionText.domain.equals("")==false?measureDescriptionText.domain:"&nbsp;"}</td>
		<td>${measureDescriptionText.description.equals("")==false?measureDescriptionText.description:"&nbsp;"}</td>
		<td trick-computable="${measureDescription.computable}"><c:if test="${measureDescription.computable==true}">
				<spring:message code="label.yes_no.true" />
			</c:if> <c:if test="${measureDescription.computable==false}">
				<spring:message code="label.yes_no.false" />
			</c:if></td>
	</tr>
</c:if>