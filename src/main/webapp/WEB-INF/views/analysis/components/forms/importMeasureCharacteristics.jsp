<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<div class="modal fade in" id="importMeasureCharacteristics" tabindex="-1" role="dialog" data-aria-labelledby="importMeasureCharacteristics" data-aria-hidden="true"
	data-backdrop="static">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="phaseNewModal-title">
					<spring:message code="label.title.rrf.import.measure_characteristics" text="Import security measure characteristics" />
				</h4>
			</div>
			<div class="modal-body">
				<form action="${pageContext.request.contextPath}/KnowledgeBase/Norm/Import/RRF/Save" class="form">
					<label><spring:message code="label.profiles" text="Profiles" /></label> <select class="form-control" name="profile">
						<c:forEach items="${profiles}" var="profile">
							<option value="${profile.id}" title='<spring:message text="${profile.label}"/>'><spring:message text="${profile.identifier} v. ${profile.version}" />
							</option>
						</c:forEach>
					</select>
					<hr>
					<label><spring:message code="label.standards" text="Standards" /></label> <select class="form-control" name="norms" multiple="multiple">
						<c:forEach items="${profiles}" var="profile">
							<c:forEach items="${profile.analysisNorms}" var="analysisNorm">
								<c:if test="${idNorms.contains(analysisNorm.norm.id)}">
									<option title="${analysisNorm.norm.label} - v. ${analysisNorm.norm.version}" value="${analysisNorm.norm.id}" name="${profile.id}"><spring:message
											text="${analysisNorm.norm.label}" /></option>
								</c:if>
							</c:forEach>
						</c:forEach>
					</select>
				</form>
				<div class="progress progress-striped active" hidden="true">
					<div class="progress-bar" role="progressbar" data-aria-valuenow="100" data-aria-valuemin="0" data-aria-valuemax="100" style="width: 100%"></div>
				</div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-danger pull-left" name="show_rrf">
					<spring:message code="label.action.switch.rrf" text="Switch to RRF" />
				</button>
				<button type="button" class="btn btn-primary" name="import">
					<spring:message code="label.action.import" text="Import" />
				</button>
				<button type="button" class="btn btn-default" name="cancel">
					<spring:message code="label.action.cancel" text="Cancel" />
				</button>
			</div>
		</div>
		<!-- /.modal-content -->
	</div>
	<!-- /.modal-dialog -->
</div>
<!-- /.modal -->