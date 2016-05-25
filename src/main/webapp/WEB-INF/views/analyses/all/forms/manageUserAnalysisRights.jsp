<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
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
					<spring:message code="label.analysis.anage.access.info" arguments="${analysis.label} , ${analysis.version} , ${analysis.customer.organisation}"
						text="Analysis: ${analysis.label}, Version: ${analysis.version}, Customer: ${analysis.customer.organisation}" />
				</p>
				<jsp:include page="../../../template/successErrors.jsp" />
				<c:if test="${!empty userrights}">
					<form id="userrightsform" name="userrightsform" action="" method="post">
						<label><spring:message code="label.select.user" text="Select a user" /></label> <select id="userselect" name="userselect" class="form-control">
							<c:forEach items="${userrights.keySet()}" var="user" varStatus="status">
								<option value="${user.id}" ${user.id==currentUser?"selected='selected'":""}><spring:message text="${user.firstName}" />&nbsp;
									<spring:message text="${user.lastName}" /></option>
							</c:forEach>
						</select> <input name="analysis" type="hidden" value="${analysis.id}" />
						<c:forEach items="${userrights.keySet()}" var="user">
							<div id="user_${user.id}" ${user.id==currentUser?"":"hidden='hidden'"}>
								<div class="radio">
									<label> <c:set var="analysisRight" value="${userrights.get(user)}" scope="request" /> <input type="radio" class="" value="-1" name="analysisRight_${user.id}"
										${analysisRight == null?'checked="checked"':'' }> <spring:message code="label.analysis.right.none" text="None" />
									</label>
								</div>
								<c:forEach items="${analysisRights}" var="right">
									<div class="radio">
										<label> <input type="radio" ${analysisRight == right?'checked="checked"':'' } value="${right.ordinal()}" name="analysisRight_${user.id}" /> <spring:message
												code="label.analysis.right.${fn:toLowerCase(right)}" text="${fn:replace(right,'_',' ') }" />
										</label>
									</div>
								</c:forEach>
							</div>
						</c:forEach>
					</form>
				</c:if>
			</div>
			<div class="modal-footer">
				<button id="manageAnalysisAccessModelButton" type="button" class="btn btn-primary" onclick="updatemanageAnalysisAccess('userrightsform')">
					<spring:message code="label.action.Save" text="Save" />
				</button>
				<button type="button" name="cancel"  class="btn btn-default" data-dismiss="modal">
					<spring:message code="label.action.cancel" text="Cancel" />
				</button>
			</div>
		</div>
	</div>
</div>