<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="tab-pane" id="tab-analyses">
	<div class="section" id="section_admin_analysis">
		<jsp:include page="../../template/successErrors.jsp" />
		<ul class="nav nav-pills bordered-bottom" id="menu_admin_analysis">
			<li class="disabled" data-trick-selectable="true" data-trick-check="isProfile('#section_admin_analysis')"><a href="#"
				onclick="return manageAnalysisAccess(null, 'section_admin_analysis');"> <span class="fa fa-users"></span> <spring:message
						code="label.menu.manage.access.analysis" text="Manage access rights" /></a></li>
						
			<li class="disabled" data-trick-selectable="true" data-trick-check="isProfile('#section_admin_analysis') && isAnalysisType('QUANTITATIVE','#section_admin_analysis')"><a href="#"
						onclick="return manageAnalysisIDSAccess('section_admin_analysis');"> <span class="fa fa-rss-square"></span> <spring:message code="label.menu.manage.ids.access.analysis"
								text="Manage IDS" /></a></li>

			<li class="disabled" data-trick-selectable="true" data-trick-check="isProfile('#section_admin_analysis')"><a href="#"
				onclick="return switchCustomer('section_admin_analysis');"> <span class="glyphicon glyphicon-transfer"></span> <spring:message code="label.menu.switch.customer"
						text="Switch customer" /></a></li>
						
			<li class="disabled" data-trick-selectable="true" data-trick-check="isProfile('#section_admin_analysis')"><a href="#"
				onclick="return switchOwner('section_admin_analysis');"> <span class="fa fa-exchange fa-sw"></span> <spring:message code="label.menu.switch.owner"
						text="Switch owner" /></a></li>

			<li class="disabled pull-right" data-trick-selectable="true" data-trick-check="isProfile('#section_admin_analysis')"><a href="#"
				onclick="return deleteAdminAnalysis(undefined,'section_admin_analysis');" class="text-danger"> <span class="glyphicon glyphicon-remove"></span> <spring:message
						code="label.menu.delete.analysis" text="Delete" /></a></li>
		</ul>
		<div class="center-block">
			<p class="text-center" style="margin-bottom: 0; margin-top: 10px;">
				<label><spring:message code="label.filter.analysis.customer" text="Analyses filtered by customer" /></label>
			</p>
			<form class="col-md-offset-5 col-md-2">
				<select class="form-control" onchange="return adminCustomerChange(this)" style="margin-bottom: 15px">
					<c:forEach items="${customers}" var="icustomer">
						<option value="${icustomer.id}" ${not empty(customer) && icustomer.id == customer? 'selected':'' }>
							<spring:message text="${icustomer.organisation}" />
						</option>
					</c:forEach>
				</select>
			</form>
		</div>
		<table class="table table-hover" style="border-top: 1px solid #dddddd;" data-fh-scroll-multi="0.995">
			<thead>
				<tr>
					<th width="1%"></th>
					<th><spring:message code="label.analysis.identifier" text="Identifier" /></th>
					<th width="5%"><spring:message code="label.analysis.type" text="Type" /></th>
					<th width="20%"><spring:message code="label.analysis.label" text="Name" /></th>
					<th width="40%"><spring:message code="label.analysis.comment" text="Comment" /></th>
					<th><spring:message code="label.analysis.version" text="Version" /></th>
					<th><spring:message code="label.analysis.creation_date" text="Create date" /></th>
					<th><spring:message code="label.analysis.owner" text="Owner" /></th>
					<th><spring:message code="label.analysis.language" text="Language" /></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${analyses}" var="analysis">
					<tr data-trick-id="${analysis.id}" onclick="selectElement(this)" data-trick-rights-id="0" data-trick-is-profile="${analysis.profile}" data-trick-type='${analysis.type}'>
						<td><input type="checkbox" class="checkbox" onchange="return updateMenu(this,'#section_admin_analysis','#menu_admin_analysis');"></td>
						<td><spring:message text="${analysis.identifier}" /></td>
						<td><spring:message code='label.analysis.type.${fn:toLowerCase(analysis.type)}' text="${fn:toLowerCase(analysis.type)}" /></td>
						<td data-trick-content='text'><spring:message text="${analysis.label}" /></td>
						<td><spring:message text="${analysis.lastHistory.comment}" /></td>
						<td data-trick-version="${analysis.version}"><spring:message text="${analysis.version}" /></td>
						<td><spring:message text="${analysis.creationDate}" /></td>
						<td><spring:message text="${analysis.owner.getFirstName()} ${analysis.owner.getLastName()}" /></td>
						<td><spring:message text="${analysis.language.name}" /></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
</div>