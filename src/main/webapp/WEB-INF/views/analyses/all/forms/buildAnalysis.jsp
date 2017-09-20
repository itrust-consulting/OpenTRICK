<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<div class="modal fade" id="buildAnalysisModal" tabindex="-1" role="dialog" data-aria-labelledby="buildAnalysisModal" data-aria-hidden="true" data-backdrop="static">
	<div class="modal-dialog" style="width: 840px;">
		<div class="modal-content" style="height: 100%">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title">
					<spring:message code="label.title.build.analysis" text="Build an analysis" />
				</h4>
			</div>
			<div class="modal-body" style="padding-top: 2px;">
				<ul class="nav nav-tabs" role="tablist" style="margin-top: 0px;">
					<li class="active" role="tab_group_1"><a href="#group_1" data-toggle="tab"><spring:message code="label.menu.build.analyis.default" text="Default" /></a></li>
					<li><a href="#group_2" data-toggle="tab" data-helper-content='<spring:message code="help.analysis.advance" />'><spring:message code="label.menu.build.analyis.advance"
								text="Advance" /></a></li>
					<li class="col-sm-8 pull-right" id="build-analysis-modal-error"></li>
				</ul>
				<form action="#" class="form-horizontal tab-content" id="tabs" style="overflow-x: hidden; overflow-y: auto; height: 610px;">
					<div id="group_1" class="tab-pane active" style="padding-top: 10px;">
						<div class="form-group">
							<label for="type" class="col-sm-2 control-label" data-helper-content='<spring:message code="help.analysis.type"/>'><spring:message code="label.analysis.type"
									text="Type" /></label>
							<div class="col-sm-10" align="center">
								<div class="btn-group" data-toggle="buttons">
									<c:forEach items="${types}" var="type" varStatus="status">
										<c:set var="typeValue" value="${fn:toLowerCase(type)}" />
										<label class="btn btn-default ${status.index==1?'active':''}"><spring:message code="label.analysis.type.${typeValue}" text="${typeValue}" /><input
											${status.index==1 ? 'checked' :''} name="type" type="radio" value="${type}"></label>
									</c:forEach>
								</div>
							</div>
						</div>
						<div class="form-group">
							<label for="customer" class="col-sm-2 control-label" data-helper-content='<spring:message code="help.analysis.customer.organisation" />'><spring:message
									code="label.customer.organisation" text="Customer" /></label>
							<div class="col-sm-10">
								<select class="form-control" name="customer" required="required">
									<option value="-1" selected="selected" disabled="disabled"><spring:message code="label.action.choose" text="Choose..." /></option>
										<c:forEach items="${customers}" var="customer">
											<option value="${customer.id}">${customer.organisation}</option>
										</c:forEach>
								</select>
							</div>
						</div>
						<div class="form-group">
							<label for="language" class="col-sm-2 control-label" data-helper-content='<spring:message code="help.analysis.language" />'><spring:message
									code="label.analysis.language" text="Language" /></label>
							<div class="col-sm-10">
								<select name="language" class="form-control" required="required">
									<option value="-1" selected="selected" disabled="disabled"><spring:message code="label.action.choose" text="Choose..." /></option>
										<c:forEach items="${languages}" var="language">
											<option value="${language.id}">${language.name}</option>
										</c:forEach>
								</select>
							</div>
						</div>
						<div class="form-group">
							<label for="profile" class="col-sm-2 control-label" data-helper-content='<spring:message code="help.analysis.profile_analysis" />'> <spring:message
									code="label.analysis.profile_analysis" text="Profile" /></label>
							<div class="col-sm-10">
								<select name="profile" id="analysis_profile" class="form-control" required="required">
									<option value="-1" selected="selected" disabled="disabled"><spring:message code="label.action.choose.analysis_profile" text="Choose..." /></option>
									<c:forEach items="${profiles}" var="profile">
										<option value="${profile.id }" data-type='${profile.type}'><spring:message text="${profile.label}" /></option>
									</c:forEach>
								</select>
							</div>
						</div>
						<div class="form-group">
							<label for="author" class="col-sm-2 control-label" data-helper-content='<spring:message code="help.analysis.author" />'><spring:message code="label.analysis.author"
									text="Author" /></label>
							<div class="col-sm-10">
								<input type="text" class="form-control" name="author" value="${author}" required="required" />
							</div>
						</div>
						<div class="form-group">

							<label for="name" class="col-sm-2 control-label" data-helper-content='<spring:message code="help.analysis.label" />'><spring:message code="label.analysis.label"
									text="Label" /></label>
							<div class="col-sm-4">
								<input name="name" class="form-control" required="required">
							</div>

							<label for="version" class="col-sm-2 control-label" data-helper-content='<spring:message code="help.analysis.version" />' data-helper-placement='auto bottom'><spring:message
									code="label.analysis.version" text="Version" /></label>
							<div class="col-sm-4">
								<input name="version" class="form-control" type="text" value="0.1" required="required" />
							</div>
						</div>

						<div class="form-group">
							<label for="comment" class="col-sm-2 control-label" data-helper-content='<spring:message code="help.analysis.description" />'><spring:message
									code="label.analysis.description" text="Description" /></label>
							<div class="col-sm-10">
								<textarea name="comment" class="form-control resize_vectical_only" rows="2" required="required"></textarea>
							</div>
						</div>

						<div class="form-group">
							<label for="uncertainty" class="col-sm-2 control-label" data-helper-content='<spring:message code="help.analysis.uncertainty" />'><spring:message
									code="label.analysis.uncertainty" text="Uncertainty" /></label>
							<div class="col-sm-10" align="center">
								<input type="checkbox" name="uncertainty" class="checkbox">
							</div>
						</div>

						<div class="form-group" data-type="QUALITATIVE">
							<label for="impacts" class="col-sm-2 control-label" data-helper-content='<spring:message code="help.analysis.impacts" />'> <spring:message
									code="label.analysis.impacts" text="Impacts" /></label>
							<div class="col-sm-10">
								<select name="impacts" id="analysis_impacts" class="form-control" multiple="multiple" style="resize: vertical;">
									<option value="-1" selected="selected"><spring:message code="label.select.impact" text="From profile" /></option>
									<c:forEach items="${impacts}" var="impact">
										<option value="${impact.id}"><spring:message code="label.impact.type.${fn:toLowerCase(impact.name)}"
												text="${empty impact.translations[locale]? impact.displayName :  impact.translations[locale].name}" /></option>
									</c:forEach>
								</select>
							</div>
						</div>
						<div class="form-group" data-type="QUALITATIVE">
							<label for="scale.level" class="col-sm-2 control-label" data-helper-content='<spring:message code="help.analysis.scale.level" />'> <spring:message
									code="label.scale.leve" text="Level" />
							</label>
							<div class="col-sm-10">
								<input name="scale.level" id="scale_level" class="form-control" type="number" required="required" value="11" min="2" placeholder="11">
							</div>
						</div>
						<div class='form-group' data-type="HYBRID-ONLY">
							<label for="impacts" class="col-sm-2 control-label" data-helper-content='<spring:message code="help.analysis.scale.value" />'> <spring:message
									code="label.analysis.scale.value" text="Max value" /></label>
							<div class='col-sm-10'>
								<input name="scale.maxValue" id="scale_maxValue" value="300" class="form-control" type="number" min="1" required="required" placeholder="300">
							</div>
						</div>
					</div>
					<div id="group_2" class="tab-pane" style="padding-top: 10px;">
						<!-- <span data-helper-placement='auto left'  style="margin-top: -12px; display: block;" class="pull-right"></span> -->
						<div class="col-sm-4">
							<h4 data-helper-content='<spring:message code="help.analysis.advance.customers" />'>
								<spring:message code="label.analysis.customers" text="Customers" />
							</h4>
							<select class="form-control" id="selector-customer">
								<option value="-1" selected="selected"><spring:message code="label.action.choose" text="Choose..." /></option>
									<c:forEach items="${customers}" var="customer">
										<option value="${customer.id}"><spring:message text="${customer.organisation}" /></option>
									</c:forEach>
							</select>
							<h4 data-helper-content='<spring:message code="help.analysis.advance.analyses" />'>
								<spring:message code="label.analyses" text="Analyses" />
							</h4>
							<select class="form-control" id="selector-analysis">
								<option value="-1" selected="selected"><spring:message code="label.action.choose" text="Choose..." /></option>
							</select>
							<h4 data-helper-content='<spring:message code="help.analysis.advance.version" />'>
								<spring:message code="label.analysis.versions" text="Versions" />
							</h4>
							<ul class="list-group" style="max-height: 239px; overflow: auto;" id="analysis-versions">
							</ul>
						</div>
						<div class="col-sm-8" style="border-left: 1px solid #e5e5e5; margin-top: -5px;">
							<div class="form-group">
								<label for="scope" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.analysis.advance.scope" />'><spring:message
										code="label.analysis.scope" text="Scope" /></label>
								<div class="col-sm-9" id="analysis-build-scope" data-trick-name="scope" dropzone="true">
									<div class="well well-sm" data-supported='HYBRID'>
										<spring:message code="label.drop_here" text="Drop your analysis here" />
									</div>
									<input name="scope" value="-1" hidden="true">
								</div>
							</div>
							<div class="form-group" >
								<label for="riskInformation" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.analysis.advance.risk_information" />'><spring:message
										code="label.analysis.risk_information" text="Risk information" /></label>
								<div class="col-sm-9" id="analysis-build-riskInformation" data-trick-name="riskInformation" dropzone="true">
									<div class="well well-sm" data-supported='HYBRID'>
										<spring:message code="label.drop_here" text="Drop your analysis here" />
									</div>
									<input name="riskInformation" value="-1" hidden="true">
								</div>
							</div>
							<div class="form-group">
								<label for="parameters" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.analysis.advance.parameters" />'> <spring:message
										code="label.analysis.parameters" text="Parameters" /></label>
								<div class="col-sm-9" id="analysis-build-parameters" data-trick-name="parameter" dropzone="true">
									<div class="well well-sm" data-supported='HYBRID'>
										<spring:message code="label.drop_here" text="Drop your analysis here" />
									</div>
									<input name="parameter" value="-1" hidden="true">
								</div>
							</div>
							<div class="form-group">
								<label for="assets" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.analysis.advance.assets" />'> <spring:message
										code="label.analysis.assets" text="Assets" /></label>
								<div class="col-sm-9" id="analysis-build-assets" data-trick-name="asset" dropzone="true">
									<div class="well well-sm" data-supported='HYBRID'>
										<spring:message code="label.drop_here" text="Drop your analysis here" />
									</div>
									<input name="asset" value="-1" hidden="true">
								</div>
							</div>
							<div class="form-group">
								<label for="scenarios" class="col-sm-3 control-label" title='<spring:message code="label.analysis.scenarios" />'
									data-helper-content='<spring:message code="help.analysis.advance.scenarios" />'> <spring:message code="label.scenarios" /></label>
								<div class="col-sm-9" id="analysis-build-scenarios" dropzone="true" data-trick-name="scenario" >
									<div class="well well-sm" data-supported='HYBRID'>
										<spring:message code="label.drop_here" text="Drop your analysis here" />
									</div>
									<input name="scenario" value="-1" hidden="true">
								</div>
							</div>
							<div class="form-group">
								<label for="assessment" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.analysis.advance.risk_estimation" />'> <spring:message
										code="label.analysis.risk_estimation" text="Risk estimation" /></label>
								<div class="col-sm-9" align="center">
									<input type="checkbox" class="checkbox" name="assessment" disabled="disabled" />
								</div>
							</div>
							<div class="form-group">
								<label for="riskProfile" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.analysis.advance.risk_profile" />'> <spring:message
										code="label.analysis.risk_profile" text="Risk profile" /></label>
								<div class="col-sm-9" align="center">
									<input type="checkbox" class="checkbox" name="riskProfile" disabled="disabled" />
								</div>
							</div>
							<div class="form-group">
								<label for="standards" class="col-sm-3 control-label" title='<spring:message code="label.analysis.standards" text="Standards" />'
									data-helper-content='<spring:message code="help.analysis.advance.standards" />'><spring:message code="label.analysis.standards" /></label>
								<div class="col-sm-9" id="analysis-build-standards" data-trick-name="standards" dropzone="true">
									<div class="well well-sm" style="height: 170px; overflow-y: auto; resize: vertical;" data-supported='HYBRID'>
										<spring:message code="label.drop_here" text="Drop your analysis here" />
									</div>
								</div>
							</div>
							<div class="form-group">
								<label for="phase" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.analysis.advance.phases"/>'> <spring:message code="label.phases"
										text="Phase" /></label>
								<div class="col-sm-9" align="center">
									<input type="checkbox" class="checkbox" name="phase" disabled="disabled" />
								</div>
							</div>
						</div>
						<div class='clearfix'></div>
					</div>
				</form>
				<span style="display: block; clear: both; margin: 0; padding: 0"></span>
			</div>
			<div class="modal-footer" style="margin-top: 0">
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