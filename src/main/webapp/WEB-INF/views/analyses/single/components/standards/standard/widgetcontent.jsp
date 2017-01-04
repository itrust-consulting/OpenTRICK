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
				<div class="modal-title">
					<h4 class="col-xs-5" style="padding: 0; margin: 0">
						<spring:message code="label.title.measures_collections.management" />
					</h4>
					<div class="col-xs-7" id="error-standard-modal" style="padding: 0"></div>
				</div>
			</div>
			<div class="modal-body tab-content">
				<div id="section_manage_standards" class="tab-pane active" style="height: 500px; overflow-y: auto; overflow-x: hidden; margin-top: -10px"></div>
				<div id="available_standards" class="tab-pane" style="height: 500px; overflow-y: auto; overflow-x: hidden; margin-top: -10px">
					<jsp:include page="form/import.jsp" />
				</div>
				<div id="standard_form_container" class="tab-pane" style="overflow-y: auto; overflow-x: hidden; height: 490px;">
					<jsp:include page="form/add.jsp" />
				</div>
			</div>
			<div class="modal-footer">
				<button class="btn btn-primary" style="display: none;" name="save">
					<spring:message code="label.action.save" />
				</button>
				<a class="btn btn-default" href="#section_manage_standards" data-toggle="tab" style="display: none;"><spring:message code="label.action.back" /></a>
				<button class="btn btn-default" type="button" data-dismiss="modal" name="cancel">
					<spring:message code="label.action.close" />
				</button>
			</div>
		</div>
	</div>
</div>
<div class="modal fade" id="deleteStandardModal" tabindex="-1" data-aria-hidden="true" data-aria-labelledby="deleteStandard" role="dialog" data-backdrop="false">
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