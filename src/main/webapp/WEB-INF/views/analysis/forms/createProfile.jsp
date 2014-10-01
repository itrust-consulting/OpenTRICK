<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="modal fade" id="analysisProfileModal" tabindex="-1" role="dialog" data-aria-labelledby="newAnalysisProfile" data-aria-hidden="true" data-backdrop="static"
	data-keyboard="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="createAnalysisProfile-title">
					<spring:message code="label.title.create.analysis.profile" text="Create new profile" />
				</h4>
			</div>
			<div class="modal-body">
				<spring:hasBindErrors name="*">
					<spring:bind path="*">
						<c:forEach items="${status.errorMessages}" var="error">
							<spring:message text="${error}" />
							<br />
						</c:forEach>
					</spring:bind>
				</spring:hasBindErrors>
				<form:form commandName="analysisProfile" cssClass="form">
					<form:hidden path="idAnalysis" />
					<form:errors element="label" path="idAnalysis" cssClass="label label-danger" />
					<div class="form-group">
						<form:label path="name">
							<spring:message code="label.analysis.profile.name" text="Name" />
						</form:label>
						<form:textarea path="name" cssClass="form-control resize_vertical_only" />
						<form:errors element="label" path="name" cssClass="label label-danger" />
					</div>
					<div class="form-group">
						<form:label path="norms">
							<spring:message code="label.analysis.profile.norms" text="Standards to include in profile" />
						</form:label>
						<form:select path="norms" multiple="true" cssClass="form-control" itemValue="id" itemLabel="label" items="${norms}" />
					</div>
					<div class="form-group">
						<label for="scenario"><spring:message code="label.analysis.profile.scenario" text="Include risk scenarios of analysis in profile" /></label>
							<input name="scenario" style="max-width: 20px; float: none; display: inline; margin-top: -3px;" type="checkbox" class="form-control"/>
					</div>
				</form:form>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" onclick="saveAnalysisProfile('analysisProfile')">
					<spring:message code="label.action.save" text="Save" />
				</button>
			</div>
		</div>
		<!-- /.modal-content -->
	</div>
	<!-- /.modal-dialog -->
</div>
<!-- /.modal -->