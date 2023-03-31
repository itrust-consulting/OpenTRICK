<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="tab-pane" id="tab-scope">
	<div class="page-header tab-content-header">
		<div class="container">
			<div class="row-fluid">
				<h3>
					<spring:message code="label.title.scope"/>
				</h3>
			</div>
		</div>
	</div>
	<div class="section" id="section_itemInformation">
		<table id="iteminformationtable" class="table table-condensed table-hover table-fixed-header-analysis">
			<thead>
				<tr>
					<th style="width:25%"><spring:message code="label.item_information.description" /></th>
					<th><spring:message code="label.item_information.value" /></th>
				</tr>
			</thead>
			<tfoot></tfoot>
			<tbody>
				<c:forEach items="${itemInformations}" var="itemInformation">
					<tr data-trick-class="ItemInformation" data-trick-id="${itemInformation.id}">
						<td><spring:message code="label.item_information.${fn:trim(itemInformation.description)}" text ="${itemInformation.description}" /></td>
						<td onclick="return editField(this);" class="editable" data-trick-field="value" data-trick-content="text" data-trick-field-type="string"><spring:message text="${itemInformation.value}" /></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
</div>