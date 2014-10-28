<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<div class="modal fade" id="addScenarioModal" tabindex="-1" role="dialog" data-aria-labelledby="addNewScenario" data-aria-hidden="true" data-backdrop="static">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="addScenarioModel-title">
					<fmt:message key="label.title.scenario.${empty(scenario)? 'add':'edit'}" />
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
						<label for="name" class="col-sm-2 control-label"> <fmt:message key="label.scenario.name" />
						</label>
						<div class="col-sm-10">
							<input name="name" id="scenario_name" class="form-control" value='<spring:message text="${empty(scenario)? '':scenario.name}"/>' />
						</div>
					</div>
					<div class="form-group">
						<label for="scenarioType.id" class="col-sm-2 control-label"> <fmt:message key="label.scenario.type" />
						</label>
						<div class="col-sm-10">
							<select name="scenarioType" class="form-control" id="scenario_scenariotype_id">
								<c:choose>
									<c:when test="${!empty(scenariotypes)}">
										<option value='-1'><fmt:message key="label.scenario.type.select" /></option>
										<c:forEach items="${scenariotypes}" var="scenariotype">
											<option value="${scenariotype.id}" ${scenario.scenarioType == scenariotype?'selected':''}><fmt:message
													key="label.scenario.type.${fn:toLowerCase(fn:replace(scenariotype.name,'-','_'))}" /></option>
										</c:forEach>
									</c:when>
									<c:otherwise>
										<option value='-1'><fmt:message key="label.scenario.type.loading" /></option>
									</c:otherwise>
								</c:choose>
							</select>
						</div>
					</div>
					<div class="form-group">
						<label for="comment" class="col-sm-2 control-label"> <fmt:message key="label.scenario.description" />
						</label>
						<div class="col-sm-10">
							<textarea name="description" class="form-control resize_vectical_only" id="scenario_description"><spring:message
									text="${empty(scenario)? '': scenario.description}" /></textarea>
						</div>
					</div>
					<div class="form-group">
						<label for="selected" class="col-sm-2 control-label"> <fmt:message key="label.scenario.selected" />
						</label>
						<div class="col-sm-10">
							<input name="selected" id="scenario_selected" class="form-control checkbox" type="checkbox" value="true" ${empty(scenario)? '': scenario.selected? 'checked' : ''} />
						</div>
					</div>
					<div class="panel panel-primary">
						<div class="panel-body">
							<label class="col-sm-12 text-center"> <fmt:message key="label.scenario.application.asset.types" /></label>
							<table class="table">
								<c:choose>
									<c:when test="${!empty(scenario)}">
										<thead>
											<tr>
												<c:forEach items="${scenario.assetTypeValues}" var="assettypevalue" varStatus="status">
													<td><fmt:message key="label.asset_type.${fn:toLowerCase(assettypevalue.assetType.type)}" /></td>
												</c:forEach>
											</tr>
										</thead>
										<tbody>
											<tr>
												<c:forEach items="${scenario.assetTypeValues}" var="assetTypeValue" varStatus="status">
													<td><input type="checkbox" ${assetTypeValue.value > 0 ? 'checked' : ''} value="1" name="<spring:message text="${assetTypeValue.assetType.type}" />" /></td>
												</c:forEach>
											</tr>
										</tbody>
									</c:when>
									<c:when test="${!empty(assetTypes)}">
										<thead>
											<tr>
												<c:forEach items="${assetTypes}" var="assetType">
													<td><fmt:message key="label.asset_type.${fn:toLowerCase(assetType.type)}" /></td>
												</c:forEach>
											</tr>
										</thead>
										<tbody>
											<tr>
												<c:forEach items="${assetTypes}" var="assetType" varStatus="status">
													<td><input type="checkbox" value="1" name="<spring:message text="${assetType.type}" />" /></td>
												</c:forEach>
											</tr>
										</tbody>
									</c:when>
								</c:choose>
							</table>
						</div>
					</div>
				</form>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" onclick="saveScenario('scenario_form')">
					<fmt:message key="label.action.save" />
				</button>
			</div>
		</div>
		<!-- /.modal-content -->
	</div>
	<!-- /.modal-dialog -->
</div>
<!-- /.modal -->