<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<div class="section" id="section_kb_standard">
	<div class="page-header">
		<h3 id="Standards">
			<spring:message code="label.menu.knowledgebase.standards" text="Standards" />
		</h3>
	</div>
	<div class="panel panel-default">
		<div class="panel-heading" style="min-height: 60px">
			<div class="col-md-12">
				<ul class="nav nav-pills" id="menu_standard">
					<li><a href="#" onclick="return newStandard();"><span class="glyphicon glyphicon-plus primary"></span>&nbsp;<spring:message code="label.menu.add.norm" text="Add" /> </a></li>
					<li class="disabled" data-trick-selectable="true"><a href="#" onclick="return showMeasures();"><span class="glyphicon glyphicon-new-window"></span>&nbsp;<spring:message
								code="label.action.show_measures" text="Show measures" /> </a></li>
					<li class="disabled" data-trick-selectable="true"><a href="#" onclick="return exportSingleStandard();"><span class="glyphicon glyphicon-edit danger"></span>&nbsp;<spring:message
								code="label.menu.export.norm" text="Export" /> </a></li>
					<li class="disabled" data-trick-selectable="true"><a href="#" onclick="return editSingleStandard();"><span class="glyphicon glyphicon-edit danger"></span>&nbsp;<spring:message
								code="label.menu.edit.norm" text="Edit" /> </a></li>
					<li><a href="#" onclick="return getImportStandardTemplate();"><span class="glyphicon glyphicon-file"></span>&nbsp;<spring:message
								code="label.menu.norm.download.import_norm_template" text="Get Import Template" /> </a></li>
					<li><a href="#" onclick="return uploadImportStandardFile();"><span class="glyphicon glyphicon-import"></span>&nbsp;<spring:message code="label.menu.import.norm"
								text="Import Standard" /> </a></li>
					<li class="disabled pull-right" data-trick-selectable="true"><a href="#" class="text-danger" onclick="return deleteStandard();"><span class="glyphicon glyphicon-remove"></span>&nbsp;
							<spring:message code="label.action.delete.norm" text="Delete" /> </a></li>
				</ul>
			</div>
		</div>
		<div class="panel-body autofitpanelbodydefinition">
			<c:choose>
				<c:when test="${!empty standards}">
					<table class="table text-left">
						<thead>
							<tr>
								<th><input type="checkbox" class="checkbox" onchange="return checkControlChange(this,'standard')" disabled="disabled"></th>
								<th><spring:message code="label.norm.label" text="Name" /></th>
								<th><spring:message code="label.norm.version" text="Version" /></th>
								<th><spring:message code="label.norm.description" text="Description" /></th>
								<th><spring:message code="label.norm.type" text="Type" /></th>
								<th><spring:message code="label.norm.computable" text="Computable" /></th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${standards}" var="standard">
								<tr data-trick-id="${standard.id}" ondblclick="return editSingleStandard('${standard.id}');">
									<td><input type="checkbox" class="checkbox" onchange="return updateMenu(this,'#section_kb_standard','#menu_standard');"></td>
									<td><spring:message text="${standard.label}" /></td>
									<td><spring:message text="${standard.version}" /></td>
									<td><spring:message text="${standard.description}" /></td>
									<td data-trick-type="${standard.type}"><spring:message code="label.norm.type_${fn:toLowerCase(standard.type)}" text="${standard.type}" /></td>
									<td data-trick-computable="${standard.computable?'Yes':'No'}"><spring:message code="label.yes_no.${standard.computable}" text="${standard.computable?'Yes':'No'}" /></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</c:when>
				<c:otherwise>
					<h4>
						<spring:message code="label.norm.empty" text="No standard" />
					</h4>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
</div>