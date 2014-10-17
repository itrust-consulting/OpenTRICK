<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<div class="panel panel-default" id="section_standards">
	<div class="panel-heading" style="min-height: 60px">
		<ul id="menu_standards" class="nav nav-pills">
			<li><a onclick="return createStandard();" href="#"><span class="glyphicon glyphicon-plus primary"></span>&nbsp;<fmt:message key="label.action.create" /></a></li>
			<li><a onclick="return addStandard();" href="#"><span class="glyphicon glyphicon-plus primary"></span>&nbsp;<fmt:message key="label.action.add" /></a></li>
			<li trick-selectable="true" class="disabled"><a onclick="return editStandard();" href="#"><span class="glyphicon glyphicon-edit primary"></span>&nbsp;<fmt:message key="label.action.edit" /></a></li>
			<li trick-selectable="true" class="disabled"><a onclick="return manageMeasures();" href="#"><span class="glyphicon glyphicon-new-window"></span>&nbsp;<fmt:message
						key="label.action.show_measures" /></a></li>
			<li trick-selectable="true" class="disabled pull-right"><a onclick="return removeStandard();" class="text-danger" href="#"><span class="glyphicon glyphicon-remove"></span>&nbsp;<fmt:message
						key="label.action.remove" /></a></li>
		</ul>
	</div>
	<div class="panel-body" style="max-height: 700px; overflow: auto;">
		<c:if test="${!empty(currentStandards)}">
			<table class="table">
				<thead>
					<tr>
						<th>&nbsp;</th>
						<th><fmt:message key="label.norm.label" /></th>
						<th><fmt:message key="label.norm.version" /></th>
						<th colspan="3"><fmt:message key="label.norm.description" /></th>
						<th><fmt:message key="label.norm.computable" /></th>
						<th><fmt:message key="label.norm.type" /></th>
						<th><fmt:message key="label.norm.analysisOnly" /></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${currentStandards}" var="standard">
						<tr trick-id="${standard.id}">
							<td><input type="checkbox" class="checkbox" onchange="return updateMenu(this,'#section_standards','#menu_standards');"></td>
							<td><spring:message text="${standard.label}" /></td>
							<td><spring:message text="${standard.version}" /></td>
							<td colspan="3"><spring:message text="${standard.description}" /></td>
							<td style="text-align: center"><fmt:message key="label.${standard.computable?'yes':'no'}" /></td>
							<td style="text-align: center"><fmt:message key="label.norm.standard_type.${fn:toLowerCase(standard.type)}" /></td>
							<td style="text-align: center"><fmt:message key="label.${standard.analysis!=null?'yes':'no'}" /></td>
						</tr>
					</c:forEach>
					<c:if test="${currentStandards!=null?currentStandards.size()==0:true}">
						<tr>
							<td colspan="6"><fmt:message key="label.no_standards" /></td>
						</tr>
					</c:if>
				</tbody>
			</table>
		</c:if>
		<c:if test="${empty(currentStandards)}">
			<fmt:message key="label.no_standards" />
		</c:if>
	</div>
</div>