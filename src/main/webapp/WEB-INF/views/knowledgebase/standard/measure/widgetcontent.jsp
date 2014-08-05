<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="modal fade" tabindex="-1" role="dialog" data-aria-labelledby="showMeasures" data-aria-hidden="true">
	<div class="modal-dialog" style="width: 80%">
		<div class="modal-content">
			<div id="measures_header" class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="showMeasuresModel-title"></h4>
			</div>
			<div id="showmeasuresbody" class="modal-body"></div>
		</div>
	</div>
</div>
<div class="modal fade" id="addMeasureModel" tabindex="-1" role="dialog" data-aria-labelledby="addNewMeasure" data-aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="addMeasureModel-title">
					<spring:message code="label.tile.add.measure" text="Add new Measure" />
				</h4>
			</div>
			<div class="modal-body">
				<form name="measure" action="/Save" class="form-horizontal" id="measure_form" method="post">
					<input type="hidden" name="id" value="-1" id="measure_id">
					<div class="form-group">
						<label for="reference" class="col-sm-2 control-label"> <spring:message code="label.measure.reference" text="Reference" /></label>
						<div class="col-sm-10">
							<input name="reference" id="measure_reference" class="form-control" type="text" />
						</div>
					</div>
					<div class="form-group">
						<label for="level" class="col-sm-2 control-label"> <spring:message code="label.measure.level" text="Level" /></label>
						<div class="col-sm-10">
							<input name="level" id="measure_level" class="form-control" type="text" />
						</div>
					</div>
					<div class="form-group">
						<label for="computable" class="col-sm-2 control-label"> <spring:message code="label.measure.computable" text="Computable" /></label>
						<div class="col-sm-10">
							<input name="computable" id="measure_computable" class="form-control" type="checkbox" />
						</div>
					</div>
					<div id="measurelanguages"></div>
				</form>
			</div>
			<div class="modal-footer">
				<button id="addmeasurebutton" type="button" class="btn btn-primary" onclick="saveMeasure()">
					<spring:message code="label.action.add.norm" text="Add" />
				</button>
			</div>
		</div>
	</div>
</div>
<div class="modal fade" id="deleteMeasureModel" tabindex="-1" data-aria-hidden="true" data-aria-labelledby="deleteMeasure" role="dialog">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="deleteMeasureModel-title">
					<spring:message code="lable.title.delete.measure" text="Delete a measure" />
				</h4>
			</div>
			<div id="deleteMeasureBody" class="modal-body">Your question here...</div>
			<div class="modal-footer">
				<button id="deletemeasurebuttonYes" type="button" class="btn btn-danger" data-dismiss="modal" onclick="">
					<spring:message code="label.action.confirm.yes" text="Yes" />
				</button>
				<button id="deletemeasurebuttonCancel" type="button" class="btn" data-dismiss="modal">
					<spring:message code="label.action.cancel" text="Cancel" />
				</button>
			</div>
		</div>
	</div>
</div>