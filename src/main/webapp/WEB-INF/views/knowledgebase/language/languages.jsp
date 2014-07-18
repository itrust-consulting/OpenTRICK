<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="section" id="section_language">
	<div class="page-header">
		<h3 id="Languages">
			<spring:message code="title.menu.knowledgebase.languages" text="Language"/>
		</h3>
	</div>
	<div class="panel panel-default">
		<div class="panel-heading" style="min-height: 60px">
			<ul class="nav nav-pills" id="menu_language">
				<li><a href="#" onclick="return newLanguage();"><span class="glyphicon glyphicon-plus primary"></span> <spring:message code="label.menu.add.language" text="Add" /> </a></li>
				<li class="disabled" trick-selectable="true"><a href="#" onclick="return editSingleLanguage();"><span class="glyphicon glyphicon-edit danger"></span> <spring:message
							code="label.menu.edit.language" text="Edit" /> </a></li>
				<li class="disabled pull-right" trick-selectable="true"><a href="#" class="text-danger" onclick="return deleteLanguage();"><span class="glyphicon glyphicon-remove"></span> <spring:message
							code="label.menu.delete.language" text="Delete" /> </a></li>
			</ul>
		</div>
		<div class="panel-body autofitpanelbodydefinition">
			<c:choose>
				<c:when test="${!empty languages}">
					<table class="table">
						<thead>
							<tr>
								<th><input type="checkbox" class="checkbox" onchange="return checkControlChange(this,'language')"></th>
								<th><spring:message code="label.language.alpha3" text="Alpha3"/></th>
								<th><spring:message code="label.language.name" text="Name" /></th>
								<th><spring:message code="label.language.alt_name" text="Alternative name"/></th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${languages}" var="language">
								<tr trick-id="${language.id}" ondblclick="return editSingleLanguage('${language.id}');">
									<td><input type="checkbox" class="checkbox" onchange="return updateMenu('#section_language','#menu_language');"></td>
									<td><spring:message text="${language.alpha3}" htmlEscape="true" /></td>
									<td><spring:message code="${language.name}" /></td>
									<td><spring:message code="${language.altName}" /></td>
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
</div>