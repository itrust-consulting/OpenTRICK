<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="modal fade" id="switchOwnerModal" tabindex="-1" role="dialog" data-aria-labelledby="switchOwnerModal" data-aria-hidden="true" data-backdrop="static">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header" style="margin-bottom: 0px">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title">
					<spring:message code="label.title.analysis.switch.owner" text="Switch owner" />
				</h4>
			</div>
			<div class="modal-body">
				<form id="formSwitchOwner" class="form-horizontal">
					<input name="idAnalysis" hidden="hidden" value="${idAnalysis}">
					<div class="form-group">
						<label class="col-xs-4"> <spring:message code="label.current.owner" text="Current owner" />
						</label>
						<div class="list-group col-xs-8">
							<label><spring:message text="${analysis.owner.firstName} ${analysis.owner.lastName}" /></label>
						</div>
					</div>
					<div class="form-group">
						<label class="col-xs-4"> <spring:message code="label.new.owner" text="New owner" />
						</label>
						<div class="list-group col-xs-8">
							<select name="owner" class="form-control">
								<c:forEach items="${userAnalysisRights.keySet()}" var="user">
									<option value="${user.id}"><spring:message text="${user.firstName} ${user.lastName}" /> -
										<c:choose>
											<c:when test="${empty userAnalysisRights[user]}">
												<spring:message code="label.analysis.right.none" text="None" />
											</c:when>
											<c:otherwise>
												<spring:message code="label.analysis.right.${fn:toLowerCase(userAnalysisRights[user])}" text="${fn:replace(userAnalysisRights[user],'_', ' ')}" />
											</c:otherwise>
										</c:choose>

									</option>
								</c:forEach>
							</select>
						</div>
					</div>
				</form>
			</div>
			<div class="modal-footer" style="margin-top: 0px">
				<button type="button" name="cancel"  class="btn btn-danger" data-dismiss="modal">
					<spring:message code="label.action.cancel" text="Cancel" />
				</button>
				<button type="button" name="save" class="btn btn-primary">
					<spring:message code="label.action.save" text="Save" />
				</button>
			</div>
		</div>
	</div>
</div>