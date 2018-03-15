<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="tab-pane" id="tab-language">
	<div class="section" id="section_language">
		<ul class="nav nav-pills bordered-bottom" id="menu_language">
			<li data-trick-ignored="true"><a href="#" onclick="return newLanguage();"><span class="glyphicon glyphicon-plus primary"></span> <spring:message code="label.menu.add.language" text="Add" /> </a></li>
			<li class="disabled" data-trick-selectable="true"><a href="#" onclick="return editSingleLanguage();"><span class="glyphicon glyphicon-edit danger"></span> <spring:message
						code="label.menu.edit.language" text="Edit" /> </a></li>
			<li class="disabled pull-right" data-trick-selectable="true"><a href="#" class="text-danger" onclick="return deleteLanguage();"><span class="glyphicon glyphicon-remove"></span> <spring:message
						code="label.menu.delete.language" text="Delete" /> </a></li>
		</ul>
		<c:choose>
			<c:when test="${!empty languages}">
				<table class="table table-striped table-hover">
					<thead>
						<tr>
							<th width="1%"></th>
							<th><spring:message code="label.language.alpha3" text="Alpha3"/></th>
							<th><spring:message code="label.language.name" text="Name" /></th>
							<th><spring:message code="label.language.alt_name" text="Alternative name"/></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${languages}" var="language">
							<tr data-trick-id="${language.id}" onclick="selectElement(this)" ondblclick="return editSingleLanguage('${language.id}');">
								<td><input type="checkbox" class="checkbox" onchange="return updateMenu(this,'#section_language','#menu_language');"></td>
								<td data-field-name="alpha3" ><spring:message text="${language.alpha3}" /></td>
								<td data-field-name="name" ><spring:message text="${language.name}" /></td>
								<td data-field-name="altName"><spring:message text="${language.altName}" /></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</c:when>
			<c:otherwise>
				<h4>
					<spring:message code="label.language.empty" text="No language"/>
				</h4>
			</c:otherwise>
		</c:choose>
	</div>
</div>