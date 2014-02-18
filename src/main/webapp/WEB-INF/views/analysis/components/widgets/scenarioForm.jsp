<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="modal fade" id="addScenarioModal" tabindex="-1" role="dialog" aria-labelledby="addNewScenario" aria-hidden="true" data-backdrop="static">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="addScenarioModel-title">
					<spring:message code="label.scenario.${empty(scenario)? 'add':'edit'}" text="${empty(scenario)? 'Add new scenario':'Edit scenario'}" />
				</h4>
			</div>
			<div class="modal-body">
				<form name="scenario" action="${pageContext.request.contextPath}/Scenario/Save" class="form-horizontal" id="scenario_form">
					<c:choose>
						<c:when test="${!empty(scenario)}">
							<input type="hidden" name="id" value="${scenario.id}" id="scenario_id">
						</c:when>
						<c:otherwise>
							<input type="hidden" name="id" value="-1" id="scenario_id">
						</c:otherwise>
					</c:choose>
					<div class="form-group">
						<label for="name" class="col-sm-2 control-label"> <spring:message code="label.scenario.name" text="Name" />
						</label>
						<div class="col-sm-10">
							<input name="name" id="scenario_name" class="form-control" value="${empty(scenario)? '':scenario.name}" />
						</div>
					</div>
					<div class="form-group">
						<label for="scenarioType.id" class="col-sm-2 control-label"> <spring:message code="label.scenario.type" text="Type" />
						</label>
						<div class="col-sm-10">
							<select name="scenarioType" class="form-control" id="scenario_scenariotype_id">
								<c:choose>
									<c:when test="${!empty(scenariotypes)}">
										<option value='-1'><spring:message code="label.scenario.type.select" text="Select the type of scenario" /></option>
										<c:forEach items="${scenariotypes}" var="scenariotype">
											<option value="${scenariotype.id}" ${scenario.scenarioType == scenariotype?'selected':''}>${scenariotype.name}</option>
										</c:forEach>
									</c:when>
									<c:otherwise>
										<option value='-1'><spring:message code="label.scenario.type.loading" text="Loading..." /></option>
									</c:otherwise>
								</c:choose>
							</select>
						</div>
					</div>
					<div class="form-group">
						<label for="comment" class="col-sm-2 control-label"> <spring:message code="label.scenario.description" text="Description" />
						</label>
						<div class="col-sm-10">
							<textarea name="description" class="form-control" id="scenario_description">${empty(scenario)? '': scenario.description}</textarea>
						</div>
					</div>
					<div class="form-group">
						<label for="selected" class="col-sm-2 control-label"> <spring:message code="label.scenario.selected" text="Selected" />
						</label>
						<div class="col-sm-10">
							<input name="selected" id="scenario_selected" class="form-control checkbox" type="checkbox" value="true" ${empty(scenario)? '': scenario.selected? 'checked' : ''} />
						</div>
					</div>
					<table class="table">
						<c:choose>
							<c:when test="${!empty(scenario)}">
								<thead>
									<tr>
										<c:forEach items="${scenario.assetTypeValues}" var="assettypevalue" varStatus="status">
											<td><spring:message code="label.assetTypeValue.${assettypevalue.assetType.type}" text="${assettypevalue.assetType.type}"/></td>
										</c:forEach>
									</tr>
								</thead>
								<tbody>
									<tr>
										<c:forEach items="${scenario.assetTypeValues }" var="assetTypeValue" varStatus="status">
											<td><input type="checkbox" ${assetTypeValue.value>0? 'checked' : ''} value="1"
												name="<spring:message
											text="${assetTypeValue.assetType.type}" htmlEscape="true"/>" /></td>
										</c:forEach>
									</tr>
								</tbody>
							</c:when>
						</c:choose>
					</table>
					
				</form>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" onclick="saveScenario('scenario_form')">
					<spring:message code="label.action.save" text="Save" />
				</button>
			</div>
		</div>
		<!-- /.modal-content -->
	</div>
	<!-- /.modal-dialog -->
</div>
<!-- /.modal -->