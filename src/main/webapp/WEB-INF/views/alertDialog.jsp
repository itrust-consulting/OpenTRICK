<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div id="dialog-body">
	<div id="info-dialog" class="bootbox modal fade bootbox-confirm" role="dialog" tabindex="-1" style="display: none;" data-aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<!-- dialog body -->
				<div class="modal-body"></div>
				<!-- dialog buttons -->
				<div class="modal-footer">
					<button type="button" class="btn btn-primary" data-dismiss="modal">
						<spring:message code="label.action.ok" text="OK" />
					</button>
				</div>
			</div>
		</div>
	</div>
	<div id="alert-dialog" class="bootbox modal fade bootbox-confirm" role="dialog" tabindex="-1" style="display: none;" data-aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<!-- dialog body -->
				<div class="modal-body"></div>
				<!-- dialog buttons -->
				<div class="modal-footer">
					<button type="button" class="btn btn-danger" data-dismiss="modal">
						<spring:message code="label.action.ok" text="OK" />
					</button>
				</div>
			</div>
		</div>
	</div>
	<div id="confirm-dialog" class="bootbox modal fade bootbox-confirm" role="dialog" tabindex="-1" style="display: none;" data-aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<!-- dialog body -->
				<div class="modal-body"></div>
				<!-- dialog buttons -->
				<div class="modal-footer">
					<button type="button" class="btn btn-danger" data-dismiss="modal">
						<spring:message code="label.action.confirm.yes" text="Yes" />
					</button>
					<button type="button" class="btn btn-default" data-dismiss="modal">
						<spring:message code="label.action.confirm.no" text="No" />
					</button>
				</div>
			</div>
		</div>
	</div>
</div>