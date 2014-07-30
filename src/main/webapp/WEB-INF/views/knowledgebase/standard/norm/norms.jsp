<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="section" id="section_norm">
	<div class="page-header">
		<h3 id="Norms">
			<spring:message code="label.menu.knowledgebase.standards" text="Standards"/>
		</h3>
	</div>
	<div class="panel panel-default">
		<div class="panel-heading" style="min-height: 60px">
			<div class="col-md-12">
				<ul class="nav nav-pills" id="menu_norm">
					<li><a href="#" onclick="return newNorm();"><span class="glyphicon glyphicon-plus primary"></span> <spring:message code="label.menu.add.norm" text="Add" /> </a></li>
					<li class="disabled" trick-selectable="true"><a href="#" onclick="return showMeasures();"><span class="glyphicon glyphicon-new-window"></span> <spring:message
								code="label.menu.show.measures" text="Show measures" /> </a></li>
					<li class="disabled" trick-selectable="true"><a href="#" onclick="return exportSingleNorm();"><span class="glyphicon glyphicon-edit danger"></span> <spring:message
								code="label.menu.export.norm" text="Export" /> </a></li>
					<li class="disabled" trick-selectable="true"><a href="#" onclick="return editSingleNorm();"><span class="glyphicon glyphicon-edit danger"></span> <spring:message
								code="label.menu.edit.norm" text="Edit" /> </a></li>
					<li><a href="#" onclick="return getImportNormTemplate();"><span class="glyphicon glyphicon-file"></span> <spring:message
								code="label.menu.norm.download.import_norm_template" text="Get Import Template" /> </a></li>
					<li><a href="#" onclick="return uploadImportNormFile();"><span class="glyphicon glyphicon-import"></span> <spring:message code="label.menu.import.norm" text="Import norm" />
					</a></li>
					<li class="disabled pull-right" trick-selectable="true"><a href="#" class="text-danger" onclick="return deleteNorm();"><span class="glyphicon glyphicon-remove"></span>
							<spring:message code="label.action.delete.norm" text="Delete" /> </a></li>
				</ul>
			</div>
		</div>
		<div class="panel-body autofitpanelbodydefinition">
			<c:choose>
				<c:when test="${!empty norms}">
					<table class="table text-left">
						<thead>
							<tr>
								<th><input type="checkbox" class="checkbox" onchange="return checkControlChange(this,'norm')"></th>
								<th><spring:message code="label.norm.label" text="Name"/></th>
								<th><spring:message code="label.norm.version" text="Version" /></th>
								<th><spring:message code="label.norm.description" text="Description" /></th>
								<th><spring:message code="label.norm.computable" text="Computable" /></th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${norms}" var="norm">
								<tr trick-id="${norm.id}" ondblclick="return editSingleNorm('${norm.id}');">
									<td><input type="checkbox" class="checkbox" onchange="return updateMenu(this,'#section_norm','#menu_norm');"></td>
									<td><spring:message text="${norm.label}"/></td>
									<td><spring:message text="${norm.version}"/></td>
									<td><spring:message text="${norm.description}"/></td>
									<td computable="${norm.computable?'Yes':'No'}"><spring:message code="label.yes_no.${norm.computable}" text="${norm.computable?'Yes':'No'}" /></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</c:when>
				<c:otherwise>
					<h4>
						<spring:message code="label.norm.empty" text="No standard"/>
					</h4>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
</div>