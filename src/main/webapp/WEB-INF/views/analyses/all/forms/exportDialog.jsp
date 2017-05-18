<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div id="analysis-export-dialog" class="bootbox modal fade bootbox-confirm" role="dialog" tabindex="-1" data-aria-hidden="true" data-backdrop="static">
	<div class="modal-dialog modal-sm">
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title">
					<spring:message code="label.title.analysis.export.report" text="Exporting analysis word report" />
				</h4>
			</div>
			<!-- dialog body -->
			<div class="modal-body">
				<spring:message code="info.choose.analysis.export.report" text="Please select type of report you want to export." />
			</div>
			<!-- dialog buttons -->
			<div class="modal-footer">
				<div class="text-center">
					<button type="button" data-trick-type='QUANTITATIVE' name="export" class="btn btn-primary">
						<spring:message code="label.analysis.type.quantitative" />
					</button>
					<button type="button" data-trick-type='QUALITATIVE' name="export" class="btn btn-success">
						<spring:message code="label.analysis.type.qualitative" />
					</button>
				</div>
			</div>
		</div>
	</div>
</div>
<div id="analysis-export-raw-action-plan-dialog" class="bootbox modal fade bootbox-confirm" role="dialog" tabindex="-1" data-aria-hidden="true" data-backdrop="static">
	<div class="modal-dialog modal-sm">
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title">
					<spring:message code="label.title.analysis.export.raw_action_plan" text="Exporting analysis raw action plan" />
				</h4>
			</div>
			<!-- dialog body -->
			<div class="modal-body">
				<spring:message code="info.choose.analysis.export.raw_action_plan" text="Please select type you want to export." />
			</div>
			<!-- dialog buttons -->
			<div class="modal-footer">
				<div class="text-center">
					<button type="button" data-trick-type='QUANTITATIVE' name="export" class="btn btn-primary">
						<spring:message code="label.analysis.type.quantitative" />
					</button>
					<button type="button" data-trick-type='QUALITATIVE' name="export" class="btn btn-success">
						<spring:message code="label.analysis.type.qualitative" />
					</button>
				</div>
			</div>
		</div>
	</div>
</div>