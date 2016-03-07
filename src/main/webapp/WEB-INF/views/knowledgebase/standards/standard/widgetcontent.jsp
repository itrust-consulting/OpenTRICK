<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="modal fade" id="addStandardModel" tabindex="-1" role="dialog" data-aria-labelledby="addNewStandard" data-aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="addStandardModel-title">
					<spring:message code="label.title.add.norm" text="Add new Standard" />
				</h4>
			</div>
			<div class="modal-body">
				<form name="standard" action="/Save?${_csrf.parameterName}=${_csrf.token}" class="form-horizontal" id="standard_form" method="post">
					<input type="hidden" name="id" value="-1" id="standard_id">
					<div class="form-group">
						<label for="alpha3" class="col-sm-2 control-label"> <spring:message code="label.norm.label" text="Name" />
						</label>
						<div class="col-sm-10">
							<input name="label" id="standard_label" class="form-control" type="text" />
						</div>
					</div>
					<div class="form-group">
						<label for="version" class="col-sm-2 control-label"> <spring:message code="label.norm.version" text="Version" />
						</label>
						<div class="col-sm-10">
							<input name="version" id="standard_version" class="form-control" type="text" />
						</div>
					</div>
					<div class="form-group">
						<label for="description" class="col-sm-2 control-label"> <spring:message code="label.norm.description" text="Description" />
						</label>
						<div class="col-sm-10">
							<input name="description" id="standard_description" class="form-control" type="text" />
						</div>
					</div>
					<div class="form-group">
						<label for="computable" class="col-sm-2 control-label"> <spring:message code="label.norm.computable" text="Computable" />
						</label>
						<div class="col-sm-10" align="center">
							<input name="computable" id="standard_computable" class="checkbox" type="checkbox" />
						</div>
					</div>
					<div class="panel panel-primary">
						<div class="panel-body" align="center">
							<label class="col-sm-12"><spring:message code="label.norm.standard_type" text="Standard Type" /></label> 
							<label class="radio-inline col-sm-offset-2 col-sm-4" > <input type="radio" name="type" value="NORMAL"><spring:message code="label.norm.standard_type.normal" text="Normal" /></label>
							<label class="radio-inline col-sm-4"> <input type="radio" name="type" value="MATURITY"><spring:message code="label.norm.standard_type.maturity" text="Maturity" /></label>
						</div>
					</div>
					
				</form>
			</div>
			<div class="modal-footer">
				<button id="addstandardbutton" type="button" class="btn btn-primary" onclick="saveStandard('standard_form')">
					<spring:message code="label.action.add.norm" text="Add" />
				</button>
				<button type="button" class="btn btn-default" data-dismiss="modal">
					<spring:message code="label.action.cancel" text="Cancel" />
				</button>
			</div>
		</div>
	</div>
</div>
<div class="modal fade" id="deleteStandardModel" tabindex="-1" data-aria-hidden="true" data-aria-labelledby="deleteStandard" role="dialog">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="deleteStandardModel-title">
					<spring:message code="label.title.delete.norm" text="Delete a standard" />
				</h4>
			</div>
			<div id="deleteStandardBody" class="modal-body">Your question here...</div>
			<div class="modal-footer">
				<button id="deletestandardbuttonYes" type="button" class="btn btn-danger" data-dismiss="modal" onclick="">
					<spring:message code="label.action.confirm.yes" text="Yes" />
				</button>
				<button id="deletestandardbuttonCancel" type="button" class="btn btn-default" data-dismiss="modal">
					<spring:message code="label.action.cancel" text="Cancel" />
				</button>
			</div>
		</div>
	</div>
</div>