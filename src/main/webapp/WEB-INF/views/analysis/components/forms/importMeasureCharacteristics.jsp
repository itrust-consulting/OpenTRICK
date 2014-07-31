<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<div class="modal fade" id="ImportMeasureCharacteristics" tabindex="-1" role="dialog" data-aria-labelledby="ImportMeasureCharacteristics" data-aria-hidden="true" data-backdrop="static">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="phaseNewModal-title">
					<spring:message code="label.title.rrf.import.measure_characteristics" text="Import security measure characteristics" />
				</h4>
			</div>
			<div class="modal-body">
				<form action="" class="form">
					<label><spring:message  code="label.profiles" text="Profiles"/></label>
					<select class="form-control" name="profile">
						<option><spring:message code="label.action.choose" text="Choose..."/></option>
						<c:forEach items="${profiles}" var="profile">
							<option value="${profile.id}"><spring:message text="${profile.label} v.${profile.version}"/> </option>
						</c:forEach>
					</select>
					<label><spring:message  code="label.standards" text="standards"/></label>
					<select>
						<option><spring:message code="label.action.choose" text="Choose..."/></option>
						<c:forEach items="${profiles}" var="profile">
							<c:forEach items="${profile.analysisNorms}" var="analysisNorm">
								<c:if test="${idNorms.contains(analysisNorm.norm.id)}">
									<option></option>
								</c:if>
							</c:forEach>
						</c:forEach>
					</select>
				</form>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary">
					<spring:message code="label.action.import" text="Import" />
				</button>
			</div>
		</div>
		<!-- /.modal-content -->
	</div>
	<!-- /.modal-dialog -->
</div>
<!-- /.modal -->