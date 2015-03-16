<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="tab-pane" id="tabScope">
	<div class="section" id="section_itemInformation">
		<c:set var="newline" value="(\r\n|\n\r|\r|\n)"/>
		<table id="iteminformationtable" class="table table-condensed table-hover table-fixed-header-analysis">
			<thead>
				<tr>
					<th style="width:25%"><fmt:message key="label.item_information.description" /></th>
					<th><fmt:message key="label.item_information.value" /></th>
					
				</tr>
			</thead>
			<tfoot></tfoot>
			<tbody>
				<c:forEach items="${itemInformations}" var="itemInformation">
					<tr data-trick-class="ItemInformation" data-trick-id="${itemInformation.id}">
						<td>
						<fmt:message key="label.item_information.${itemInformation.description.trim()}" /></td>
						<td onclick="return editField(this.firstElementChild);" class="success"><pre data-trick-field="value" data-trick-content="text" data-trick-field-type="string"><spring:message text="${itemInformation.value}" /></pre></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
</div>