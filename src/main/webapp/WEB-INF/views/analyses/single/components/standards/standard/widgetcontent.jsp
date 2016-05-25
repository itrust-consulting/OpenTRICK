<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<div class="modal fade" id="standardModal" tabindex="-1" role="dialog" data-aria-labelledby="standardmodal" data-aria-hidden="true" data-backdrop="static">
	<div class="modal-dialog" style="width: 800px;">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title">
					<spring:message code="label.menu.manage_standard" />
				</h4>
			</div>
			<div class="modal-body">
				<div class="panel panel-default" id="section_manage_standards"></div>
			</div>
			<div class="modal-footer" style="margin-top: 0;">
				<div class="progress progress-striped active" style="display: none; width: 100%; margin-top: 15px; margin-bottom: 0;" id="standard_progressbar">
					<div class="progress-bar progress-striped" role="progressbar" style="width: 100%;"></div>
				</div>
			</div>
		</div>
	</div>
</div>
<div class="modal fade" id="addStandardModal" tabindex="-1" role="dialog" data-aria-labelledby="addStandardmodal" data-aria-hidden="true" data-backdrop="static">
	<div class="modal-dialog" style="width: 800px;">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title">
					<spring:message code="label.title.analysis.manage_standard.add" />
				</h4>
			</div>
			<div class="modal-body row"></div>
			<div class="modal-footer" style="margin-top: 0;">
				<div class="progress progress-striped active" style="display: none; width: 100%; margin-top: 15px; margin-bottom: 0;" id="add_standard_progressbar">
					<div class="progress-bar progress-striped" role="progressbar" style="width: 100%;"></div>
				</div>
			</div>
		</div>
	</div>
</div>
<div class="modal fade" id="createStandardModal" tabindex="-1" role="dialog" data-aria-labelledby="createStandardmodal" data-aria-hidden="true" data-backdrop="static">
	<div class="modal-dialog" style="width: 800px;">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="createstandardtitle">
					<spring:message code="label.title.analysis.manage_standard.create" />
				</h4>
			</div>
			<div class="modal-body">
				<form name="standard" action="/Create?${_csrf.parameterName}=${_csrf.token}" class="form-horizontal" id="standard_form" method="post">
					<input type="hidden" value="-1" name="id" id="id">
					<div class="form-group">
						<label for="label" class="col-sm-2 control-label"> <spring:message code="label.norm.label" />
						</label>
						<div class="col-sm-10">
							<input name="label" id="standard_label" class="form-control" type="text" />
						</div>
					</div>
					<div class="form-group">
						<label for="description" class="col-sm-2 control-label"> <spring:message code="label.norm.description" />
						</label>
						<div class="col-sm-10">
							<input name="description" id="standard_description" class="form-control" type="text" />
						</div>
					</div>
					<div class="form-group">
						<label for="computable" class="col-sm-2 control-label"> <spring:message code="label.norm.computable" />
						</label>
						<div class="col-sm-10" align="center">
							<input name="computable" id="standard_computable" class="checkbox" type="checkbox" checked />
						</div>
					</div>
					<div class="panel panel-primary">
						<div class="panel-body" align="center">
							<label class="col-sm-12"><spring:message code="label.norm.standard_type" /></label> <label class="radio-inline col-sm-offset-2 col-sm-4"> <input type="radio"
								name="type" value="NORMAL"> <spring:message code="label.norm.standard_type.normal" /></label> <label class="radio-inline col-sm-4"> <input type="radio" name="type"
								value="ASSET"> <spring:message code="label.norm.standard_type.asset" />
							</label>
						</div>
					</div>

				</form>
			</div>
			<div class="modal-footer" style="margin-top: 0;">
				<div class='col-xs-8' id='standard-modal-error-zone' style="text-align: left"></div>
				<div class='col-xs-4'>
					<button id="createstandardbutton" type="button" class="btn btn-primary" onclick="doCreateStandard('standard_form')">
						<spring:message code="label.action.create" />
					</button>
					<button type="button" class="btn btn-default" data-dismiss="modal">
						<spring:message code="label.action.cancel" text="Cancel" />
					</button>
				</div>
			</div>
		</div>
	</div>
</div>
<div class="modal fade" id="deleteStandardModal" tabindex="-1" data-aria-hidden="true" data-aria-labelledby="deleteStandard" role="dialog">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title">
					<spring:message code="label.title.analysis.manage_standard.delete" />
				</h4>
			</div>
			<div id="deleteStandardBody" class="modal-body">Your question here...</div>
			<div class="modal-footer">
				<div class="col-sm-8">
					<div class="progress progress-striped active" style="display: none; width: 100%; margin-top: 8px; margin-bottom: 0;" id="delete_standard_progressbar">
						<div class="progress-bar progress-striped" role="progressbar" style="width: 100%;"></div>
					</div>
				</div>
				<div class="col-sm-4">
					<button id="deletestandardbuttonYes" type="button" class="btn btn-danger" data-dismiss="modal">
						<spring:message code="label.yes" />
					</button>
					<button id="deletestandardbuttonCancel" type="button" class="btn btn-default" data-dismiss="modal">
						<spring:message code="label.action.cancel" />
					</button>
				</div>
			</div>
		</div>
	</div>
</div>