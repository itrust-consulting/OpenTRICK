<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<jsp:include page="../../../template/successErrors.jsp" />
<div class="modal fade" id="editAnalysisModel" tabindex="-1" role="dialog" data-aria-labelledby="editAnalysis" data-aria-hidden="true" data-backdrop="static">
	<div class="modal-dialog modal-md">
		<div class="modal-content">
			<div class="modal-header bg-primary">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title">
					<spring:message code="label.title.properties" />
				</h4>
			</div>
			<div class="modal-body">
				<form name="analysis" action="Analysis/Save?${_csrf.parameterName}=${_csrf.token}" class="form-horizontal" id="analysis_form">
					<fieldset>
						<legend style="font-size: 15px">
							<spring:message code="label.property.analysis" />
						</legend>
						<input type="hidden" name="id" value="${analysis.id}" id="analysis_id">
						<c:if test="${not analysis.profile}">
							<div class="form-group">
								<label for="type" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.analysis.info.type"/>'> <spring:message code="label.analysis.type"
										text="Type" /></label>
								<div class="col-sm-9" align="center">
									<div class="btn-group" data-toggle="buttons">
										<c:forEach items="${types}" var="type" varStatus="status">
											<c:set var="typeValue" value="${fn:toLowerCase(type)}" />
											<label class="btn btn-default ${analysis.type == type? 'active':''}" ><spring:message code="label.analysis.type.${typeValue}" text="${typeValue}" />
												<input type="radio" name="type" value="${type}" ${analysis.type == type? 'checked':''}>
											</label>
										</c:forEach>
									</div>
								</div>
							</div>
						</c:if>
						<div class="form-group">
							<label for="identifier" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.analysis.info.identifier" />'> <spring:message
									code="label.analysis.identifier" text="Identifier" />
							</label>
							<div class="col-sm-9">
								<input name="identifier" id="analysis_identifier" class="form-control" type="text" value="${analysis.identifier}" readonly />
							</div>
						</div>
						
						
						<c:if test="${not analysis.profile}">
							<div class="form-group">
								<label for="customer" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.analysis.info.customer" />'> <spring:message
										code="label.customer.organisation" text="Customer" />
								</label>
								<div class="col-sm-9" id="customercontainer">
									<select name="customer" class="form-control">
										<c:forEach items="${customers}" var="customer">
											<option value="${customer.id}" ${customer.equals(analysis.customer)?"selected='selected'":""}>${customer.organisation}</option>
										</c:forEach>
									</select>
								</div>
							</div>
						</c:if>
						<div class="form-group">
							<label for="label" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.analysis.info.label" />'> <spring:message
									code="label.analysis.label" text="Name" />
							</label>
							<div class="col-sm-9">
								<input name="label" class="form-control resize_vectical_only" required="required" value='<spring:message text="${analysis.label}" />'>
							</div>
						</div>
					</fieldset>
					<fieldset>
						<legend style="font-size: 15px">
							<spring:message code="label.property.version" />
						</legend>
						<div class="form-group">
							<label for="creationDate" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.analysis.info.creation_date" />'> <spring:message
									code="label.analysis.creation_date" text="Creation date" />
							</label>
							<div class="col-sm-9">
								<input name="creationDate" id="analysis_creationDate" class="form-control" type="text" value="${analysis.creationDate}" readonly />
							</div>
						</div>
						<c:if test="${not analysis.profile}">
							<div class="form-group">
								<label for="version" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.analysis.info.version" />'> <spring:message
										code="label.analysis.version" text="Version" />
								</label>
								<div class="col-sm-9">
									<input name="version" id="analysis_version" class="form-control" type="text" value="${analysis.version}" readonly />
								</div>
							</div>
						</c:if>
						<c:if test="${not(analysis.profile || empty analysis.basedOnAnalysis)}">
							<div class="form-group">
								<label for="basedOnAnalysis" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.analysis.info.parent" />'> <spring:message
										code="label.analysis.based_on_analysis" text="Based on analysis version" />
								</label>
								<div class="col-sm-9">
									<input name="basedOnAnalysis" id="analysis_basedOnAnalysis" class="form-control" type="text" value="${analysis.basedOnAnalysis.version}" readonly />
								</div>
							</div>
						</c:if>
						<div class="form-group">
							<label for="owner" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.analysis.info.author" />'> <spring:message
									code="label.analysis.owner" text="Owner" />
							</label>
							<div class="col-sm-9">
								<input name="owner" id="analysis_owner" class="form-control" type="text" value="${analysis.owner.firstName} ${analysis.owner.lastName}" readonly />
							</div>
						</div>

						<div class="form-group">
							<label for="language" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.analysis.info.language" />'> <spring:message
									code="label.analysis.language" text="Language" />
							</label>
							<div class="col-sm-9" id="languagecontainer">
								<select name="language" class="form-control">
									<c:forEach items="${languages}" var="language">
										<option value="${language.id}" ${language.equals(analysis.language)?"selected='selected'":""}>${language.name}</option>
									</c:forEach>
								</select>
							</div>
						</div>

						<c:if test="${not analysis.profile}">
							<div class="form-group">
								<label for="uncertainty" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.analysis.info.uncertainty" />'> <spring:message
										code="label.analysis.uncertainty" text="Uncertainty" />
								</label>
								<div class="col-sm-9" align="center">
									<c:choose>
										<c:when test="${ analysis.type=='QUALITATIVE'}">
											<input type="checkbox" name="uncertainty" class="checkbox" disabled="disabled">
										</c:when>
										<c:otherwise>
											<input type="checkbox" name="uncertainty" class="checkbox" ${analysis.uncertainty?"checked='checked'":''}>
										</c:otherwise>
									</c:choose>

								</div>
							</div>
						</c:if>
					</fieldset>
				</form>
			</div>
			<div class="modal-footer">
				<button name='save' type="button" class="btn btn-primary">
					<spring:message code="label.action.save" text="Save" />
				</button>
				<button type="button" class="btn btn-default" data-dismiss="modal" name="cancel">
					<spring:message code="label.action.cancel" text="Cancel" />
				</button>
			</div>
		</div>
	</div>
</div>