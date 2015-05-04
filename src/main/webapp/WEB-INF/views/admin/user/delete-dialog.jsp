<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<div class="modal fade" id="deleteUserModal" tabindex="-1" data-aria-hidden="true" data-aria-labelledby="deleteUser" role="dialog">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<div class="modal-title">
					<h4 class="col-md-4">
						<spring:message code="label.title.delete.user" text="Delete user" />
					</h4>
					<div class="col-md-6">
						<div class="progress progress-striped active" style="margin-bottom: 5px; margin-top: 8px;">
							<div class="progress-bar" role="progressbar" data-aria-valuenow="100" data-aria-valuemin="0" data-aria-valuemax="100" style="width: 100%"></div>
						</div>
					</div>
					<div class="clearfix"></div>
				</div>
			</div>
			<div class="modal-body">
				<p>
					<spring:message code="confirm.delete.user" arguments="${user.firstName},${user.lastName},${user.email}"
						text="Are you sure, you want to delete: ${user.firstName} ${user.lastName}, email: ${user.email}?"></spring:message>
				</p>
				<c:if test="${not empty analyses}">
					<hr>
				</c:if>
				<form name="deleteUserForm" class="form form-horizontal" style="max-height: 350px; padding: 5px; overflow-y: auto; overflow-x: hidden;" action="#">
					<input type="hidden" value='${user.id}' name="idUser">
					<c:forEach items="${analyses}" var="analysis">
						<div class="form-group">
							<label class="col-md-6 control-label" for="${analysis.id}"> <c:choose>
									<c:when test="${analysis.profile}">
										<spring:message code="info.delete.analysis.profile" arguments="${analysis.label}" text="Profile: ${analysis.label}" />
									</c:when>
									<c:when test="${not analysis.data}">
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
								<select name="${analysis.id}" class="form-control" required>
									<option value="" selected disabled><spring:message code="label.select.switch.owner_or_delete" text="Delete or switch owner" /></option>
									<option value="0"><spring:message code="label.action.delete" text="Delete" /></option>
									<c:forEach items="${users}" var="owner">
										<c:set var="right" value="${analysis.getRightValue(owner)}" />
										<option value="${owner.id}"><spring:message code="label.action.switch.owner.to" arguments="${owner.firstName},${owner.lastName},${owner.email}"
												text="${owner.firstName} ${owner.lastName}, email: ${owner.email}" /> -
											<c:choose>
												<c:when test="${empty right}">
													<spring:message code="label.analysis.right.none" text="None" />
												</c:when>
												<c:otherwise>
													<spring:message code="label.analysis.right.${fn:toLowerCase(right)}" text="${fn:replace(right,'_', ' ')}" />
												</c:otherwise>
											</c:choose>
										</option>
									</c:forEach>
								</select>
							</div>
						</div>
					</c:forEach>
					<input type="submit" hidden="hidden" name="submit">
				</form>

			</div>
			<div class="modal-footer" style="margin-top: 0px;">
				<div id="deleteUserErrors" class="col-xs-7" align="left"></div>
				<div class="col-xs-5">
					<button type="submit" name="cancel" class="btn btn-default" data-dismiss="modal">
						<spring:message code="label.action.cancel" text="Cancel" />
					</button>
					<button type="button" name="delete" class="btn btn-danger">
						<spring:message code="label.action.delete" text="Delete" />
					</button>
				</div>
			</div>
		</div>
	</div>
</div>