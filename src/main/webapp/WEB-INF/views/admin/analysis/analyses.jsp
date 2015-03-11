<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="tab-pane" id="tab_analyses">
	<div class="section" id="section_admin_analysis">
		<jsp:include page="../../template/successErrors.jsp" />
		<ul class="nav nav-pills bordered-bottom" id="menu_admin_analysis">
			<li class="disabled" data-trick-selectable="true" data-trick-check="isProfile('#section_admin_analysis')"><a href="#"
				onclick="return manageAnalysisAccess(null, 'section_admin_analysis');"> <span class="glyphicon glyphicon-plus primary"></span> <spring:message
						code="label.menu.manage.access.analysis" text="Manage access rights" /></a></li>
						
			<li class="disabled" data-trick-selectable="true" data-trick-check="isProfile('#section_admin_analysis')"><a href="#"
				onclick="return switchCustomer('section_admin_analysis');"> <span class="fa fa-exchange fa-sw primary"></span> <spring:message
						code="label.menu.switch.customer" text="Switch customer" /></a></li>
						
			<li class="disabled pull-right" data-trick-selectable="true" data-trick-check="isProfile('#section_admin_analysis')"><a href="#" onclick="return deleteAdminAnalysis(undefined,'section_admin_analysis');" class="text-danger"> <span
					class="glyphicon glyphicon-remove"></span> <spring:message code="label.menu.delete.analysis" text="Delete" /></a></li>
		</ul>
		<div class="col-md-offset-5 col-md-2 text-center" style="margin-top: 20px;">
			<spring:message code="label.analysis.filter.customer" text="Analyses filtered by customer: " />
			<select class="form-control" onchange="return adminCustomerChange(this)" style="margin-bottom: 10px">
				<c:forEach items="${customers}" var="icustomer">
					<option value="${icustomer.id}" ${customer !=null && icustomer.id == customer? 'selected':'' }>
						<spring:message text="${icustomer.organisation}" />
					</option>
				</c:forEach>
			</select>
		</div>
		<table class="table table-hover" style="border-top: 1px solid #dddddd;">
			<thead>
				<tr>
					<th width="1%"></th>
					<th><spring:message code="label.analysis.identifier" text="Identifier" /></th>
					<th width="50%"><spring:message code="label.analysis.comment" text="Comment" /></th>
					<th><spring:message code="label.analysis.creation_date" text="Create date" /></th>
					<th><spring:message code="label.analysis.version" text="Version" /></th>
					<th><spring:message code="label.analysis.owner" text="Owner" /></th>
					<th><spring:message code="label.analysis.language" text="Language" /></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${analyses}" var="analysis">
					<tr data-trick-id="${analysis.id}" data-trick-rights-id="0" data-trick-is-profile="${analysis.profile}">
						<td><input type="checkbox" class="checkbox" onchange="return updateMenu(this,'#section_admin_analysis','#menu_admin_analysis');"></td>
						<td><spring:message text="${analysis.identifier}" /></td>
						<td><spring:message text="${analysis.label}" /></td>
						<td><spring:message text="${analysis.creationDate}" /></td>
						<td data-trick-version="${analysis.version}"><spring:message text="${analysis.version}" /></td>
						<td><spring:message text="${analysis.owner.getFirstName()} ${analysis.owner.getLastName()}" /></td>
						<td><spring:message text="${analysis.language.name}" /></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
</div>