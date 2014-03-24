<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="modal fade" id="addPhaseModel" tabindex="-1" role="dialog" aria-labelledby="phaseModalForm" aria-hidden="true" data-backdrop="static">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="phaseNewModal-title">
					<spring:message code="label.phase.${empty(phase)? 'add':'edit'}" text="${empty(phase)? 'Add new phase':'Edit phase'}" />
				</h4>
			</div>
			<div class="modal-body">
				<form name="phase" action="${pageContext.request.contextPath}/Phase/Save" class="form-horizontal" id="phase_form">
					<c:choose>
						<c:when test="${!empty(phase)}">
							<input name="id" value="${phase.id}" type="hidden">
						</c:when>
						<c:otherwise>
							<input name="id" value="-1" type="hidden">
						</c:otherwise>
					</c:choose>
					<div class="form-group">
						<label for="beginDate" class="col-sm-2 control-label"> <spring:message code="label.begin.date" text="Begin" />
						</label>
						<div class="col-sm-10">
							<div class="input-group">
								<input name="beginDate" id="phase_begin_date" class="form-control" type="date" value="${empty(phase)? '':phase.beginDate}" pattern="dddd-dd-dd"
									placeholder='<spring:message code="label.phase.date.pattern" text="YYYY-MM-DD"/>' />
							</div>
						</div>
					</div>
					<div class="form-group">
						<label for="endDate" class="col-sm-2 control-label"> <spring:message code="label.phase.end_date" text="End" />
						</label>
						<div class="col-sm-10">
							<div class="input-group">
								<input name="endDate" id="phase_endDate" class="form-control" type="date" pattern="dddd-dd-dd" placeholder='<spring:message code="label.phase.date.pattern" text="YYYY-MM-DD"/>'
									${empty(phase)? '': phase.endDate} />
							</div>
						</div>
					</div>
				</form>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" onclick="savePhase('phase_form')">
					<spring:message code="label.action.save" text="Save" />
				</button>
			</div>
		</div>
		<!-- /.modal-content -->
	</div>
	<!-- /.modal-dialog -->
</div>
<!-- /.modal -->