<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
					<li class="active" role="tab_group_1"><a href="#group_1" data-toggle="tab"><spring:message code="label.menu.build.analyis.default" text="Default" /></a></li>
					<li><a href="#group_2" data-toggle="tab"><spring:message code="label.menu.build.analyis.advance" text="Advance" /></a></li>
					<li class="col-sm-6 pull-right">
						<div class="progress progress-striped active" hidden="true" style="margin-bottom: 5px; margin-top: 8px;">
							<div class="progress-bar" role="progressbar" data-aria-valuenow="100" data-aria-valuemin="0" data-aria-valuemax="100" style="width: 100%"></div>
						</div>
					</li>
				</ul>
				<form action="#" class="form-horizontal tab-content" id="tabs" style="height: 420">
					<div id="group_1" class="tab-pane active" style="padding-top: 10px;">
						<div class="form-group">
							<label for="customer" class="col-sm-2 control-label"> <spring:message code="label.customer.organisation" text="Customer" />
							</label>
							<div class="col-sm-10">
								<select class="form-control" name="customer" required="required">
									<option value="-1" selected="selected"><spring:message code="label.action.choose" text="Choose..." />
										<c:forEach items="${customers}" var="customer">
											<option value="${customer.id}">${customer.organisation}</option>
										</c:forEach>
								</select>
							</div>
						</div>
						<div class="form-group">
							<label for="language" class="col-sm-2 control-label"> <spring:message code="label.analysis.language" text="Language" />
							</label>
							<div class="col-sm-10">
								<select name="language" class="form-control" required="required">
									<option value="-1" selected="selected"><spring:message code="label.action.choose" text="Choose..." />
										<c:forEach items="${languages}" var="language">
											<option value="${language.id}">${language.name}</option>
										</c:forEach>
								</select>
							</div>
						</div>
						<div class="form-group">
							<label for="profile" class="col-sm-2 control-label"> <spring:message code="label.analysis.profile_analysis" text="Profile" />
							</label>
							<div class="col-sm-10">
								<select name="profile" id="analysis_profile" class="form-control">
									<option value="-1"><spring:message code="label.action.choose.analysis_profile" text="Choose..." /></option>
									<c:forEach items="${profiles}" var="profile">
										<option value="${profile.id }"><spring:message text="${profile.label}" /></option>
									</c:forEach>
								</select>
							</div>
						</div>
						<div class="form-group">
							<label for="author" class="col-sm-2 control-label"> <spring:message code="label.analysis.author" text="Author" />
							</label>
							<div class="col-sm-10" >
								<input type="text" class="form-control" name="author" value="${author}" required="required" />
							</div>
						</div>
						<div class="form-group">
							<label for="version" class="col-sm-2 control-label"> <spring:message code="label.analysis.version" text="Version" />
							</label>
							<div class="col-sm-10">
								<input name="version" class="form-control" type="text" value="0.0.1" required="required" />
							</div>
						</div>
						<div class="form-group">
							<label for="name" class="col-sm-2 control-label"> <spring:message code="label.analysis.label" text="Name" />
							</label>
							<div class="col-sm-10">
								<input name="name" class="form-control" required="required">
							</div>
						</div>
						<div class="form-group">
							<label for="comment" class="col-sm-2 control-label"> <spring:message code="label.analysis.description" text="Description" />
							</label>
							<div class="col-sm-10">
								<textarea name="comment" class="form-control resize_vectical_only" rows="5" required="required"></textarea>
							</div>
						</div>
						<div class="form-group">
							<label for="uncertainty" class="col-sm-2 control-label"> <spring:message code="label.analysis.uncertainty" text="Uncertainty" />
							</label>
							<div class="col-sm-10" align="center">
								<input type="checkbox" name="uncertainty" class="checkbox">
							</div>
						</div>
						<div class="form-group">
							<label for="cssf" class="col-sm-2 control-label"> <spring:message code="label.analysis.cssf" text="CSSF" />
							</label>
							<div class="col-sm-10" align="center">
								<input type="checkbox" name="cssf" class="checkbox">
							</div>
						</div>
					</div>
					<div id="group_2" class="tab-pane" style="padding-top: 10px;">
						<div class="col-sm-4">
							<h4>
								<spring:message code="label.analysis.customers" text="Customers" />
							</h4>
							<select class="form-control" id="selector-customer">
								<option value="-1" selected="selected"><spring:message code="label.action.choose" text="Choose..." />
									<c:forEach items="${customers}" var="customer">
										<option value="${customer.id}"><spring:message text="${customer.organisation}" /></option>
									</c:forEach>
							</select>
							<h4>
								<spring:message code="label.analyses" text="Analyses" />
							</h4>
							<select class="form-control" id="selector-analysis">
								<option value="-1" selected="selected"><spring:message code="label.action.choose" text="Choose..." />
							</select>
							<h4>
								<spring:message code="label.analysis.versions" text="Versions" />
							</h4>
							<ul class="list-group" style="max-height: 239px; overflow: auto;" id="analysis-versions">
							</ul>
						</div>
						<div class="col-sm-8" style="border-left: 1px solid #e5e5e5;">
							<div class="form-group">
								<label for="scope" class="col-sm-3 control-label"> <spring:message code="label.analysis.scope" text="Scope" />
								</label>
								<div class="col-sm-9" id="analysis-build-scope" data-trick-name="scope" dropzone="true">
									<div class="well well-sm">
										<spring:message code="label.drop_here" text="Drop your analysis here" />
									</div>
									<input name="scope" value="-1" hidden="true">
								</div>
							</div>
							<div class="form-group">
								<label for="riskInformation" class="col-sm-3 control-label"> <spring:message code="label.analysis.risk_information" text="Risk information" />
								</label>
								<div class="col-sm-9" id="analysis-build-riskInformation" data-trick-name="riskInformation" dropzone="true">
									<div class="well well-sm">
										<spring:message code="label.drop_here" text="Drop your analysis here" />
									</div>
									<input name="riskInformation" value="-1" hidden="true">
								</div>
							</div>
							<div class="form-group">
								<label for="parameters" class="col-sm-3 control-label"> <spring:message code="label.analysis.parameters" text="Parameters" />
								</label>
								<div class="col-sm-9" id="analysis-build-parameters" data-trick-name="parameter" dropzone="true">
									<div class="well well-sm">
										<spring:message code="label.drop_here" text="Drop your analysis here" />
									</div>
									<input name="parameter" value="-1" hidden="true">
								</div>
							</div>
							<div class="form-group">
								<label for="assets" class="col-sm-3 control-label"> <spring:message code="label.analysis.assets" text="Assets" />
								</label>
								<div class="col-sm-9" id="analysis-build-assets" data-trick-name="asset" dropzone="true">
									<div class="well well-sm">
										<spring:message code="label.drop_here" text="Drop your analysis here" />
									</div>
									<input name="asset" value="-1" hidden="true">
								</div>
							</div>
							<div class="form-group">
								<label for="scenarios" class="col-sm-3 control-label"> <spring:message code="label.analysis.scenarios" text="Scenarios" />
								</label>
								<div class="col-sm-9" id="analysis-build-scenarios" dropzone="true" data-trick-name="scenario">
									<div class="well well-sm">
										<spring:message code="label.drop_here" text="Drop your analysis here" />
									</div>
									<input name="scenario" value="-1" hidden="true">
								</div>
							</div>
							<div class="form-group">
								<label for="assessment" class="col-sm-3 control-label"> <spring:message code="label.analysis.risk_estimation" text="Risk estimation" />
								</label>
								<div class="col-sm-9" align="center">
									<input type="checkbox" class="checkbox" name="assessment" disabled="disabled" />
								</div>
							</div>
							<div class="form-group">
								<label for="assessment" class="col-sm-3 control-label"> <spring:message code="label.analysis.standards" text="Standards" /></label>
								<div class="col-sm-9" id="analysis-build-standards" data-trick-name="standards" dropzone="true">
									<div class="well well-sm" style="height: 131px;overflow-y:auto">
										<spring:message code="label.drop_here" text="Drop your analysis here" />
									</div>
								</div>
							</div>
							<div class="form-group">
								<label for="assessment" class="col-sm-3 control-label"> <spring:message code="label.analysis.phases" text="Phase" />
								</label>
								<div class="col-sm-9" align="center">
									<input type="checkbox" class="checkbox" name="phase" disabled="disabled" />
								</div>
							</div>
						</div>
					</div>
				</form>
				<span style="display: block; clear: both; margin: 0; padding: 0"></span>
			</div>
			<div class="modal-footer" style="margin-top: 0">
				<div class="col-sm-8" id="build-analysis-modal-error"></div>
				<div class="col-sm-4">
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
</div>