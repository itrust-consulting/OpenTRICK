<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<div class="modal fade" id="analysisProfileModal" tabindex="-1"
	role="dialog" aria-labelledby="newAnalysisProfile" aria-hidden="true"
	data-backdrop="static" data-keyboard="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="createAnalysisProfile-title">
					<spring:message code="label.analysis.profile.create" text="Create new profile" />
				</h4>
			</div>
			<div class="modal-body">
				<form:form commandName="analysisProfile">
					<form:hidden path="idAnalysis" />
					<div class="form-group">
						<form:label path="name">
							<spring:message code="label.analysis.profile.name" text="Name" />
						</form:label>
						<form:input path="name" cssClass="form-control"/>
					</div>
					<div class="form-group">
						<form:label path="norms">
							<spring:message code="label.analysis.profile.norms"
								text="Standards" />
						</form:label>
						<form:select path="norms" multiple="true" cssClass="form-control"
							placeholder='<spring:message code="label.analysis.profile.norms" text="Standards" />'>
							<form:options items="${norms}" itemLabel="label" itemValue="id" />
						</form:select>
					</div>

					<table class="table">
						<thead>
							<tr>
								<th><form:label path="parameter">
										<spring:message code="label.analysis.profile.parameter"
											text="Parameters" />
									</form:label></th>
								<th><form:label path="itemInformation">
										<spring:message code="label.analysis.profile.itemInformation"
											text="Item information" />
									</form:label></th>
								<th><form:label path="riskInformation">
										<spring:message code="label.analysis.profile.riskInformation"
											text="Risk information" />
									</form:label></th>
								<th><form:label path="scenario">
										<spring:message code="label.analysis.profile.scenario"
											text="Scenario" />
									</form:label></th>
								<th><form:label path="asset">
										<spring:message code="label.analysis.profile.asset"
											text="Asset" />
									</form:label></th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<td><form:checkbox path="parameter" /></td>
								<td><form:checkbox path="itemInformation" /></td>
								<td><form:checkbox path="riskInformation" /></td>
								<td><form:checkbox path="scenario" /></td>
								<td><form:checkbox path="asset" /></td>
							</tr>
						</tbody>
					</table>

				</form:form>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary"
					onclick="saveAnalysisProfile('analysisProfile')">
					<spring:message code="label.action.save" text="Save" />
				</button>
			</div>
		</div>
		<!-- /.modal-content -->
	</div>
	<!-- /.modal-dialog -->
</div>
<!-- /.modal -->