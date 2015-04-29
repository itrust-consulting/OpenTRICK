<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<div class="modal fade" id="deleteUserModel" tabindex="-1" data-aria-hidden="true" data-aria-labelledby="deleteUser" role="dialog">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="deleteUserModel-title">
					<spring:message code="label.title.delete.user" text="Delete user" />
				</h4>
			</div>
			<div class="modal-body">
				<p>
					<spring:message code="info.delete.user" arguments="${user.firstName},${user.lastName},${user.login},${user.email}"
						text="Full name: ${user.firstName} ${user.lastName}, Username: ${user.login}, Email: ${user.email}"></spring:message>
				</p>
				<hr>
				<c:set var="analysisAction">
					<option value="-1"><spring:message code="label.select.action.to_do" text="Select action to do" /></option>
					<option value="0"><spring:message code="label.action.delete" text="Delete" /></option>
					<c:forEach items="${users}" var="owner">
						<option value="${owner.id}"><spring:message code="label.action.switch.owner.to" arguments="${owner.firstName},${owner.lastName},${owner.login},${owner.email}"
								text="Switch owner to: ${owner.firstName} ${owner.lastName}, username: ${owner.login}, email: ${owner.email}" /></option>
					</c:forEach>
				</c:set>
				<form name="deleteUserForm" class="form form-horizontal" style="max-height: 350px; overflow-y: auto; overflow-x: hidden;">
					<input type="hidden" value='${user.id}' name="idUser">
					<c:forEach items="${analyses}" var="analysis">
						<div class="form-group">
							<label class="col-md-6 control-label" for="${analysis.id}"> <c:choose>
									<c:when test="${analysis.profile}">
										<spring:message code="info.delete.analysis.profile" arguments="${analysis.label}" text="Profile: ${analysis.label}" />
									</c:when>
									<c:when test="${analysis.data}">
										<spring:message code="info.delete.analysis.no_data" arguments="${analysis.label},${analysis.version},${analysis.identifier},${analysis.customer.organisation}"
											text="Empty anlysis: ${analysis.label}, version: ${analysis.version}, TRICK Name: ${analysis.identifier}, Customer: ${analysis.customer.organisation}" />
									</c:when>
									<c:otherwise>
										<spring:message code="info.delete.analysis" arguments="${analysis.label},${analysis.version},${analysis.identifier},${analysis.customer.organisation}"
											text="Anlysis: ${analysis.label}, version: ${analysis.version}, TRICK Name: ${analysis.identifier}, Customer: ${analysis.customer.organisation}" />
									</c:otherwise>
								</c:choose>
							</label>
							<div class="col-md-6">
								<select name="${analysis.id}" class="form-control"> ${analysisAction}
								</select>
							</div>
						</div>
					</c:forEach>
				</form>
			</div>
			<div class="modal-footer">
				<button type="button" name="delete" class="btn btn-danger">
					<spring:message code="label.action.delete" text="Delete" />
				</button>
				<button type="button" name="cancel" class="btn btn-default" data-dismiss="modal">
					<spring:message code="label.action.cancel" text="Cancel" />
				</button>
			</div>
		</div>
	</div>
</div>