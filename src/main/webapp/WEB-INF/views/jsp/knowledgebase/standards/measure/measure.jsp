<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ page trimDirectiveWhitespaces="true"%>
<c:if test="${!empty(measureDescription)}">
	<tr data-trick-id="${measureDescription.id}" ondblclick="return editSingleMeasure('${measureDescription.id}','${standard.id}');">
		<td><input type="checkbox" class="checkbox" onchange="return updateMenu('this,#section_measure_description','#menu_measure_description');"></td>
		<%-- <td colspan="2">${measureDescription.level}</td> --%>
		<td colspan="2">${measureDescription.reference}</td>
		<td colspan="10">${empty measureDescriptionText.domain?"&nbsp;":measureDescriptionText.domain}</td>
		<td colspan="10">${empty measureDescriptionText.description?"&nbsp;":measureDescriptionText.description}</td>
		<td colspan="2" data-trick-computable="${measureDescription.computable}"><spring:message code="label.yes_no.${measureDescription.computable}"
				text="${measureDescription.computable?'Yes':'No'}" /></td>
	</tr>
</c:if>