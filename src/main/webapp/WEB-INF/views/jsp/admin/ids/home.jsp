<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<div class="tab-pane" id="tab-ids">
	<div class="section" id="section_ids">
		<ul class="nav nav-pills bordered-bottom" id="menu_ids">
			<li data-trick-ignored="true"><a href="#" onclick="return newIDS();"><span class="glyphicon glyphicon-plus primary"></span> <spring:message code="label.action.add" text="Add" /> </a></li>
			<li class="disabled" data-trick-selectable="true"><a href="#" onclick="return editIDS();"><span class="glyphicon glyphicon-edit"></span> <spring:message
						code="label.action.edit" text="Edit" /> </a></li>
			<li class="disabled" data-trick-selectable="multi"><a href="#" onclick="return renewIDSToken();"><span class="glyphicon glyphicon-refresh"></span> <spring:message
						code="label.action.renew" text="Renew" /> </a></li>
			<li class="disabled pull-right" data-trick-selectable="multi"><a href="#" class="text-danger" onclick="return deleteIDS();"><span class="glyphicon glyphicon-remove"></span>
					<spring:message code="label.action.delete" text="Delete" /> </a></li>
		</ul>
		<table class="table table-striped table-hover table-condensed">
			<thead>
				<tr>
					<th width="1%"><input type="checkbox" class="checkbox" onchange="return checkControlChange(this,'ids')"></th>
					<th width="6%"><spring:message code="label.name" text="Name" /></th>
					<th width="20%"><spring:message code="label.ids.token" text="Token" /></th>
					<th><spring:message code="label.description" text="Description" /></th>
					<th width="8%"><spring:message code="label.last_update" text="Last update" /></th>
					<th width="8%"><spring:message code="label.last_alert" text="Last alert" /></th>
					<th width="8%" class='text-center' ><spring:message code="label.ids.status" text="Status" /></th>
					<th width="8%" class='text-center'><spring:message code="label.count.subscriber" text="Number of subscriber" /></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${IDSs}" var="ids">
					<tr data-trick-id="${ids.id}" onclick="selectElement(this)" ondblclick="return editIDS(${ids.id});">
						<td><input type="checkbox" class="checkbox" onchange="return updateMenu(this,'#section_ids','#menu_ids');"></td>
						<td><spring:message text="${ids.prefix}" /></td>
						<td data-trick-field="token"><spring:message text="${ids.token}" /></td>
						<td><spring:message text="${ids.description}" /></td>
						<td><spring:message text="${ids.lastUpdate}" /></td>
						<td><spring:message text="${ids.lastAlert}" /></td>
						<td class='text-center'><spring:message code="label.user.account.state_${fn:toLowerCase(ids.enable)}" text="${ids.enable?'Enabled':'Disabled'}" /></td>
						<td class='text-center'><spring:message text="${ids.subscribers.size()}" /></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
</div>