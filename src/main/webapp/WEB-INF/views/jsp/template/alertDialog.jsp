<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div id="dialog-body">
	<img src='<c:url value="/images/loading.gif" />' id="loading-indicator" style="display:none" align="middle" />
	<div id="progress-dialog" class="bootbox modal fade" role="dialog" tabindex="-1" style="display: none; z-index: 5000;" data-aria-hidden="false">
		<div class="modal-dialog" style="width: 0px; top: 50%;">
			<div class="modal-content" style="border: none;">
				<!-- dialog body -->
				<i class="fa fa-spinner fa-pulse fa-5x fa-align-center fa-spin"></i>
			</div>
		</div>
	</div>
	<div id="info-dialog" class="bootbox modal fade bootbox-confirm" role="dialog" tabindex="-1" style="display: none; z-index: 5000;" data-aria-hidden="true">
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
	<div id="alert-dialog" class="bootbox modal fade bootbox-confirm" role="dialog" tabindex="-1" style="display: none; z-index: 5000;" data-aria-hidden="true">
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
	<div id="confirm-dialog" class="bootbox modal fade bootbox-confirm" role="dialog" tabindex="-1" style="display: none; z-index: 5000;" data-aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<!-- dialog body -->
				<div class="modal-body"></div>
				<!-- dialog buttons -->
				<div class="modal-footer">
					<button type="button" name="yes" class="btn btn-danger" data-dismiss="modal">
						<spring:message code="label.action.confirm.yes" text="Yes" />
					</button>
					<button type="button" name="no" class="btn btn-default" data-dismiss="modal">
						<spring:message code="label.action.confirm.no" text="No" />
					</button>
				</div>
			</div>
		</div>
	</div>
</div>