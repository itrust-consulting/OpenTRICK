<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="section" id="section_admin_analysis">
	<div class="page-header">
		<h3>
			<spring:message code="label.analysis.title" text="All Analyses" />
		</h3>
		<jsp:include page="../../successErrors.jsp" />
	</div>
	<div class="panel panel-default">
		<div class="panel-heading" style="min-height: 60px">
			<ul class="nav nav-pills" id="menu_analysis">
				<li class="disabled profilemenu"><a href="#" onclick="return manageAnalysisAccess(null, 'section_admin_analysis');"> <span class="glyphicon glyphicon-plus primary"></span>
						<spring:message code="label.menu.manage.access.analysis" text="Manage access rights" /></a></li>
			</ul>
		</div>
		<div class="panel-body">
			<div class="col-md-offset-5 col-md-2 text-center">
				<spring:message code="label.analysis.filter.customer" text="Analyses filtered by customer: " />
				<select class="form-control" onchange="return adminCustomerChange(this)" style="margin-bottom: 10px">
					<c:forEach items="${customers}" var="icustomer">
						<option value="${icustomer.id}" ${customer !=null && icustomer.id == customer? 'selected':'' }>
							<spring:message text="${icustomer.organisation}" />
						</option>
					</c:forEach>
				</select>
			</div>
			<div style="clear:both " class="autofitpanelbodydefinition">
			<table class="table table-hover">
				<thead>
					<tr>
						<th><input type="checkbox" class="checkbox" onchange="return checkControlChange(this,'admin_analysis')"></th>
						<th><spring:message code="label.analysis.identifier" text="Identifier"/></th>
						<th><spring:message code="label.analysis.comment" text="Comment"/></th>
						<th><spring:message code="label.analysis.creation_date" text="Create date"/></th>
						<th><spring:message code="label.analysis.version" text="Version"/></th>
						<th><spring:message code="label.analysis.owner" text="Owner" /></th>
						<th><spring:message code="label.analysis.language" text="Language"/></th>
						<th><spring:message code="label.analysis.profile" text="Profile" />
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${analyses}" var="analysis">
						<tr trick-id="${analysis.id}" trick-rights-id="0" data="${analysis.hasData()}" trick-isProfile="${analysis.profile}">
							<td><input type="checkbox" class="checkbox"
								onchange="updateMenu('#section_admin_analysis','#menu_analysis');return disableifprofile('#section_admin_analysis','#menu_analysis');"></td>
							<td>${analysis.identifier}</td>
							<td>${analysis.label}</td>
							<td>${analysis.creationDate}</td>
							<td trick-version="${analysis.version}">${analysis.version}</td>
							<td>${analysis.owner.getFirstName()} ${analysis.owner.getLastName()}</td>
							<td>${analysis.language.name}</td>
							<c:choose>
								<c:when test="${analysis.profile == true}">
									<td><spring:message code="label.yes" text="Yes" /></td>
								</c:when>
								<c:otherwise>
									<td><spring:message code="label.no" text="No" /></td>
								</c:otherwise>
							</c:choose>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			</div>
		</div>
	</div>
</div>