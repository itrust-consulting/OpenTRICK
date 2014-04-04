<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="modal fade" id="analysisProfileModal" tabindex="-1" role="dialog" data-aria-labelledby="newAnalysisProfile" data-aria-hidden="true" data-backdrop="static" data-keyboard="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="createAnalysisProfile-title">
					<spring:message code="label.analysis.profile.create" text="Create new profile" />
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
				<form:form commandName="analysisProfile">
					<form:hidden path="idAnalysis" />
					<form:errors element="label" path="idAnalysis" cssClass="label label-danger" />
					<div class="form-group">
						<form:label path="name">
							<spring:message code="label.analysis.profile.name" text="Name" />
						</form:label>
						<form:input path="name" cssClass="form-control" />
						<form:errors element="label" path="name" cssClass="label label-danger" />
					</div>
					<div class="form-group">
						<form:label path="norms">
							<spring:message code="label.analysis.profile.norms" text="Standards" />
						</form:label>
						<form:select path="norms" multiple="true" cssClass="form-control" itemValue="id" itemLabel="label" items='<spring:message text="${norms}"/>' />
					</div>
					<table class="table">
						<thead>
							<tr>
								<th><form:label path="scenario">
										<spring:message code="label.analysis.profile.scenario" text="Scenarios" />
									</form:label></th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<td><form:checkbox path="scenario" /></td>
							</tr>
						</tbody>
					</table>
					<div class="form-group">
						<form:label path="comment">
							<spring:message code="label.analysis.profile.description" text="Description" />
						</form:label>
						<form:textarea path="comment" cssClass="form-control" />
						<form:errors element="label" path="comment" cssClass="label label-danger" />
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