<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="modal fade" id="addNormModel" tabindex="-1" role="dialog" aria-labelledby="addNewNorm" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="addNormModel-title">
					<spring:message code="label.norm.add.menu" text="Add new Norm" />
				</h4>
			</div>
			<div class="modal-body">
				<form name="norm" action="/Save" class="form-horizontal" id="norm_form" method="post">
					<input type="hidden" name="id" value="-1" id="norm_id">
					<div class="form-group">
						<label for="alpha3" class="col-sm-2 control-label"> <spring:message code="label.norm.label" text="Name" />
						</label>
						<div class="col-sm-10">
							<input name="label" id="norm_label" class="form-control" type="text" />
						</div>
					</div>
					<div class="form-group">
						<label for="version" class="col-sm-2 control-label"> <spring:message code="label.norm.version" text="Version" />
						</label>
						<div class="col-sm-10">
							<input name="version" id="norm_version" class="form-control" type="text" />
						</div>
					</div>
					<div class="form-group">
						<label for="description" class="col-sm-2 control-label"> <spring:message code="label.norm.description" text="Description" />
						</label>
						<div class="col-sm-10">
							<input name="description" id="norm_description" class="form-control" type="text" />
						</div>
					</div>
					<div class="form-group">
						<label for="computable" class="col-sm-2 control-label"> <spring:message code="label.norm.computable" text="Computable" />
						</label>
						<div class="col-sm-10">
							<input name="computable" id="norm_computable" class="form-control" type="checkbox" />
						</div>
					</div>
				</form>
			</div>
			<div class="modal-footer">
				<button id="addnormbutton" type="button" class="btn btn-primary" onclick="saveNorm('norm_form')">
					<spring:message code="label.norm.add.form" text="Add" />
				</button>
			</div>
		</div>
	</div>
</div>
<div class="modal fade" id="deleteNormModel" tabindex="-1" aria-hidden="true" aria-labelledby="deleteNorm" role="dialog">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="deleteNormModel-title">
					<spring:message code="title.norm.delete" text="Delete a norm" />
				</h4>
			</div>
			<div id="deleteNormBody" class="modal-body">Your question here...</div>
			<div class="modal-footer">
				<button id="deletenormbuttonYes" type="button" class="btn btn-danger" data-dismiss="modal" onclick="">
					<spring:message code="label.answer.yes" text="Yes" />
				</button>
				<button id="deletenormbuttonCancel" type="button" class="btn" data-dismiss="modal">
					<spring:message code="label.answer.cancel" text="Cancel" />
				</button>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript" src="<spring:url value="js/norm.js" />"></script>