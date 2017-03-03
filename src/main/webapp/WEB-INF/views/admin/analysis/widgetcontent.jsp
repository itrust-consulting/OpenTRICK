<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="modal fade" id="manageAnalysisAccessModel" tabindex="-1" role="dialog" data-aria-labelledby="manageAnalysisAccessModel" data-aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title">
					<spring:message code="label.analysis.manage.access" text="Manage Analysis Access Rights" />
				</h4>
			</div>
			<div class="modal-body"></div>
			<div class="modal-footer">
				<button type="button" name="save" class="btn btn-primary">
					<spring:message code="label.analysis.save" text="Save" />
				</button>
				<button type="button" name="cancel" class="btn btn-default" data-dismiss="modal">
					<spring:message code="label.action.cancel" text="Cancel" />
				</button>
			</div>
		</div>
	</div>
</div>
<div class="modal fade" id="deleteAnalysisModel" tabindex="-1" data-aria-hidden="true" data-aria-labelledby="deleteAnalysis" role="dialog" data-backdrop="static">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title" id="deleteAnalysisModel-title">
					<spring:message code="label.title.delete.analysis" text="Delete an analysis" />
				</h4>
			</div>
			<div class="modal-body"></div>
			<div class="modal-footer">
				<button name='delete' type="button" class="btn btn-danger">
					<spring:message code="label.yes_no.true" text="Yes" />
				</button>
				<button name='cancel' type="button" class="btn btn-default" data-dismiss="modal" >
					<spring:message code="label.yes_no.false" text="No" />
				</button>
			</div>
		</div>
	</div>
</div>