<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
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