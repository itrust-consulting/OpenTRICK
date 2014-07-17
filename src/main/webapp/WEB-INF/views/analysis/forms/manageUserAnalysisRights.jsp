<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="modal fade" id="manageAnalysisAccessModel" tabindex="-1" role="dialog" data-aria-labelledby="manageAnalysisAccessModel" data-aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="manageAnalysisAccessModel-title">
					<spring:message code="label.title.analysis.manage.access" text="Manage Analysis Access Rights" />
				</h4>
			</div>
			<div id="manageAnalysisAccessModelBody" class="modal-body">
				<p>
					<spring:message code="label.analysis.anage.access.info" arguments="${analysis.label} , ${analysis.version} , ${analysis.customer.organisation}"  text="Description: ${analysis.label}, Version: ${analysis.version}, Customer: ${analysis.customer.organisation}" />
				</p>
				<jsp:include page="../../successErrors.jsp" />
				<c:if test="${!empty userrights}">
					<form id="userrightsform" name="userrightsform" action="" method="post">
						<spring:message code="label.select.user" text="Select a user: " />
						<select id="userselect" name="userselect" class="form-control">
							<c:forEach items="${userrights.keySet()}" var="user" varStatus="status">
								<option value="${user.id}" ${user.id==currentUser?"selected='selected'":""}>${user.firstName}&nbsp;${user.lastName}</option>
							</c:forEach>
						</select> <input name="analysis" type="hidden" value="${analysis.id}" />
						<c:forEach items="${userrights.keySet()}" var="user">
							<div id="user_${user.id}" ${user.id==currentUser?"":"hidden='hidden'"}>
								<c:set var="analysisRight" value="${userrights.get(user)}" scope="request" />
								<input type="radio" value="-1" name="analysisRight_${user.id}" ${analysisRight == null?'checked="checked"':'' }>&nbsp;NONE<br>
								<c:forEach items="${analysisRights}" var="right">
									<input type="radio" ${analysisRight == right?'checked="checked"':'' } value="${right.ordinal()}" name="analysisRight_${user.id}" />&nbsp;${right}
					<br>
								</c:forEach>
							</div>
						</c:forEach>
					</form>
				</c:if>
			</div>
			<div class="modal-footer">
				<button id="manageAnalysisAccessModelButton" type="button" class="btn btn-primary" onclick="updatemanageAnalysisAccess('userrightsform')">
					<spring:message code="label.analysis.update" text="Update" />
				</button>
			</div>
		</div>
	</div>
</div>