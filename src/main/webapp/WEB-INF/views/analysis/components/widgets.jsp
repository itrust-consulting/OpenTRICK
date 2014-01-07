<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<div id="widget">
	<!-- Modal -->
	<div id="contextMenu" class="dropdown clearfix"
		style="position: absolute; display: none;" trick-selected-id="-1">
		<ul class="dropdown-menu" role="menu" aria-labelledby="dropdownMenu"
			style="display: block; position: static; margin-bottom: 5px;">
			<li name="select" hidden="true"><a tabindex="-1" href="#"><spring:message
						code="label.action.select" text="Select" /></a></li>
			<li name="unselect" hidden="true"><a tabindex="-1" href="#"><spring:message
						code="label.action.un_select" text="Unselect" /></a></li>
			<li name="assessment" hidden="true"><a tabindex="-1" href="#"><spring:message
						code="label.action.assessment" text="Assessment" /></a></li>
			<li name="edit_row" hidden="true"><a tabindex="-1" href="#"><spring:message
						code="label.action.edit_row" text="Edit row" /></a></li>
			<li class="divider"></li>
			<li name="delete" hidden="true"><a tabindex="-1" href="#"><spring:message
						code="label.action.delete" text="Delete" /></a></li>
		</ul>
	</div>
	<div id="info-dialog" class="bootbox modal fade bootbox-confirm in"
		role="dialog" tabindex="-1" style="display: none;" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<!-- dialog body -->
				<div class="modal-body"></div>
				<!-- dialog buttons -->
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">
						<spring:message code="label.dialog.button.ok" text="OK" />
					</button>
				</div>
			</div>
		</div>
	</div>
	<div id="alert-dialog" class="bootbox modal fade bootbox-confirm in"
		role="dialog" tabindex="-1" style="display: none;" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<!-- dialog body -->
				<div class="modal-body"></div>
				<!-- dialog buttons -->
				<div class="modal-footer">
					<button type="button" class="btn btn-danger" data-dismiss="modal">
						<spring:message code="label.dialog.button.ok" text="OK" />
					</button>
				</div>
			</div>
		</div>
	</div>
	<div id="confirm-dialog" class="bootbox modal fade bootbox-confirm in"
		role="dialog" tabindex="-1" style="display: none;" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<!-- dialog body -->
				<div class="modal-body"></div>
				<!-- dialog buttons -->
				<div class="modal-footer">
					<button type="button" class="btn btn-danger" data-dismiss="modal">
						<spring:message code="label.dialog.button.ok" text="OK" />
					</button>
					<button type="button" class="btn btn-default" data-dismiss="modal">
						<spring:message code="label.dialog.button.cancel" text="Cancel" />
					</button>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="widgets/assetForm.jsp" />
	<jsp:include page="widgets/scenarioForm.jsp" />
	<jsp:include page="widgets/phaseForm.jsp" />
</div>