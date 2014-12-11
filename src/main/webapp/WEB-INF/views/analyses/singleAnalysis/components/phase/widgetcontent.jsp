<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="modal fade" id="addPhaseModel" tabindex="-1" role="dialog" data-aria-labelledby="phaseModalForm" data-aria-hidden="true" data-backdrop="static">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="phaseNewModal-title">
					<fmt:message key="label.title.phase.add" />
				</h4>
			</div>
			<div class="modal-body">
				<form name="phase" action="${pageContext.request.contextPath}/Phase/Save" class="form-horizontal" id="phase_form">
					<c:choose>
						<c:when test="${!empty(phase)}">
							<input name="id" id="phaseid" value="${phase.id}" type="hidden">
						</c:when>
						<c:otherwise>
							<input name="id" id="phaseid" value="-1" type="hidden">
						</c:otherwise>
					</c:choose>
					<div class="form-group">
						<label for="date" class="col-sm-3 control-label"> <fmt:message key="label.phase_period" />
						</label>
						<div class="col-sm-9">
							<div id="datepicker_container" class="input-daterange"></div>
						</div>
					</div>
				</form>
			</div>
			<div class="modal-footer">
				<button id="addphasemodelbutton" type="button" class="btn btn-primary" onclick="savePhase('phase_form')">
					<fmt:message key="label.action.save" />
				</button>
			</div>
		</div>
		<!-- /.modal-content -->
	</div>
	<!-- /.modal-dialog -->
</div>
<div id="datepicker_prototype" style="display: none;">
	<input name="beginDate" type="text" class="form-control" style="width: auto; display: inline; background-color: white; cursor: inherit;"
		value="${empty(phase)? '':phase.beginDate}" placeholder='<fmt:message key="label.phase.date.pattern"/>' readonly /> <span class="add-on" style="height: 31px;">to</span> <input
		name="endDate" type="text" class="form-control" style="width: auto; display: inline; background-color: white; cursor: inherit;" value="${empty(phase)? '':phase.beginDate}"
		placeholder='<fmt:message key="label.phase.date.pattern"/>' readonly />
</div>
<!-- /.modal -->