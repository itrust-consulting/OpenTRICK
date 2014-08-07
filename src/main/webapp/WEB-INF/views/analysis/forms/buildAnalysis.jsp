<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="modal fade" id="buildAnalysisModal" tabindex="-1" role="dialog" data-aria-labelledby="buildAnalysisModal" data-aria-hidden="true" data-backdrop="static">
	<div class="modal-dialog" style="width: 900px;">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title">
					<spring:message code="label.title.build.analysis" text="Build an analysis" />
				</h4>
			</div>
			<div class="modal-body" style="padding-top: 2px;">
				<ul class="nav nav-tabs" role="tablist" style="margin-top: 0px;">
					<li class="active" role="tab_group_1"><a href="#group_1" data-toggle="tab"><spring:message code="label.menu.build.analyis.general" text="General" /></a></li>
					<li><a href="#group_2" data-toggle="tab"><spring:message code="label.menu.build.analyis.others" text="Others" /></a></li>
				</ul>
				<form action="#" class="form-horizontal tab-content" id="tabs">
					<input type="hidden" name="id" value="-1" id="analysis_id">
					<div id="group_1" class="tab-pane active" style="padding-top: 10px; height: 400;">
						<div class="form-group">
							<label for="customer" class="col-sm-3 control-label"> <spring:message code="label.customer.organisation" text="Customer" />
							</label>
							<div class="col-sm-9">
								<select class="form-control" name="customer">
									<option value="-1" selected="selected"><spring:message code="label.action.choose" text="Choose..." />
										<c:forEach items="${customers}" var="customer">
											<option value="${customer.id}">${customer.organisation}</option>
										</c:forEach>
								</select>
							</div>
						</div>
						<div class="form-group">
							<label for="language" class="col-sm-3 control-label"> <spring:message code="label.analysis.language" text="Language" />
							</label>
							<div class="col-sm-9">
								<select name="language" class="form-control">
									<option value="-1" selected="selected"><spring:message code="label.action.choose" text="Choose..." />
										<c:forEach items="${languages}" var="language">
											<option value="${language.id}">${language.name}</option>
										</c:forEach>
								</select>
							</div>
						</div>
						<div class="form-group">
							<label for="author" class="col-sm-3 control-label"> <spring:message code="label.analysis.author" text="Author" />
							</label>
							<div class="col-sm-9">
								<input type="text" class="form-control" name="author" value="${author}" />
							</div>
						</div>
						<div class="form-group">
							<label for="version" class="col-sm-3 control-label"> <spring:message code="label.analysis.version" text="Version" />
							</label>
							<div class="col-sm-9">
								<input name="version" class="form-control" type="text" value="0.0.1" />
							</div>
						</div>
						
						<div class="form-group">
							<label for="label" class="col-sm-3 control-label"> <spring:message code="label.analysis.description" text="Description" />
							</label>
							<div class="col-sm-9">
								<textarea name="label" class="form-control resize_vectical_only" rows="12"></textarea>
							</div>
						</div>
					</div>

					<div id="group_2" class="tab-pane" style="padding-top: 10px; height: 400;">
						<div class="col-sm-4">

							<h4>
								<spring:message code="label.analysis.customer" text="Customers" />
							</h4>

							<select class="form-control" name="analysisCustomer" id="selector-customer">
								<option value="-1" selected="selected"><spring:message code="label.action.choose" text="Choose..." />
									<c:forEach items="${customers}" var="customer">
										<option value="${customer.id}"><spring:message text="${customer.organisation}" /></option>
									</c:forEach>
							</select>

							<h4>
								<spring:message code="label.analyses" text="Analyses" />
							</h4>

							<select class="form-control" name="analyses" id="selector-analysis">
								<option value="-1" selected="selected"><spring:message code="label.action.choose" text="Choose..." />
							</select>
							<h4>
								<spring:message code="label.analysis.versions" text="Versions" />
							</h4>

							<ul class="list-group" style="max-height: 265px; overflow: auto;" id="analysis-versions">
							</ul>
						</div>
						<div class="col-sm-8" style="border-left: 1px solid #e5e5e5;">
							<div class="form-group">
								<label for="scope" class="col-sm-3 control-label"> <spring:message code="label.analysis.scope" text="Scope" />
								</label>
								<div class="col-sm-9" id="analysis-build-scope" name="scope" dropzone="true">
									<div class="well well-sm"><spring:message code="label.drop_here" text="Drop your analysis here" /></div>
								</div>
							</div>

							<div class="form-group">
								<label for="riskInformation" class="col-sm-3 control-label"> <spring:message code="label.analysis.risk_information" text="Risk information" />
								</label>
								<div class="col-sm-9" id="analysis-build-riskInformation" name="riskInformation" dropzone="true">
									<div class="well well-sm">
										<spring:message code="label.drop_here" text="Drop your analysis here" />
									</div>
								</div>
							</div>

							<div class="form-group">
								<label for="parameters" class="col-sm-3 control-label"> <spring:message code="label.analysis.parameters" text="Parameters" />
								</label>
								<div class="col-sm-9" id="analysis-build-parameters" name="parameters" dropzone="true">
									<div class="well well-sm"><spring:message code="label.drop_here" text="Drop your analysis here" /></div>
								</div>
							</div>
							<div class="form-group">
								<label for="assets" class="col-sm-3 control-label"> <spring:message code="label.analysis.assets" text="Assets" />
								</label>
								<div class="col-sm-9" id="analysis-build-assets" name="assets" dropzone="true">
									<div class="well well-sm"><spring:message code="label.drop_here" text="Drop your analysis here" /></div>
								</div>
							</div>

							<div class="form-group">
								<label for="scenarios" class="col-sm-3 control-label"> <spring:message code="label.analysis.scenarios" text="Scenarios" />
								</label>
								<div class="col-sm-9" id="analysis-build-scenarios" dropzone="true" name="scenarios">
									<div class="well well-sm"><spring:message code="label.drop_here" text="Drop your analysis here" /></div>
								</div>
							</div>
							<div class="form-group">
								<label for="assessment" class="col-sm-3 control-label"> <spring:message code="label.analysis.risk_estimation" text="Risk estimation" />
								</label>
								<div class="col-sm-9">
									<input type="checkbox" class="form-control" name="assessment" disabled="disabled" />
								</div>
							</div>

							<div class="form-group">
								<label for="assessment" class="col-sm-3 control-label"> <spring:message code="label.analysis.standards" text="Standards" /></label>
								<div class="col-sm-9" id="analysis-build-standards" name="standards" dropzone="true">
									<div class="well well-sm"><spring:message code="label.drop_here" text="Drop your analysis here" /></div>
								</div>
							</div>

							<div class="form-group">
								<label for="assessment" class="col-sm-3 control-label"> <spring:message code="label.analysis.phases" text="Phase" />
								</label>
								<div class="col-sm-9">
									<input type="checkbox" class="form-control" name="phase" disabled="disabled" />
								</div>
							</div>

						</div>
					</div>
				</form>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" name="save">
					<spring:message code="label.action.save" text="Save" />
				</button>
				<button type="button" class="btn btn-default" name="cancel">
					<spring:message code="label.action.cancel" text="Cancel" />
				</button>
			</div>
		</div>
	</div>
</div>