<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<div class="modal fade in" id="importMeasureCharacteristics" tabindex="-1" role="dialog" data-aria-labelledby="importMeasureCharacteristics" data-aria-hidden="true"
	data-backdrop="static">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="phaseNewModal-title">
					<fmt:message key="label.title.rrf.import.measure_characteristics" />
				</h4>
			</div>
			<div class="modal-body">
				<form action="${pageContext.request.contextPath}/Analysis/RRF/Import/Save" class="form">
					<div class="form-group">
						<label> <fmt:message key="label.customers" />
						</label> <select class="form-control" name="customer">
							<c:forEach items="${customers}" var="customer">
								<option value="${customer.id}">
									<spring:message text="${customer.organisation}" />
								</option>
							</c:forEach>
						</select>
					</div>
					<div class="form-group">
						<label><fmt:message key="label.analyses" /></label> <select class="form-control" name="analysis">
							<c:forEach items="${analyses}" var="analysis">
								<c:choose>
									<c:when test="${analysis.profile}">
										<option value="${analysis.id}" title='<spring:message text="${analysis.label}"/>' data-trick-id='${analysis.customer.id}'><spring:message
												text="${analysis.label}" />
										</option>
									</c:when>
									<c:otherwise>
										<option value="${analysis.id}" title='<spring:message text="${analysis.label} - v.${analysis.version}"/>' data-trick-id='${analysis.customer.id}'><spring:message
												text="${analysis.label} - v.${analysis.version}" />
										</option>
									</c:otherwise>
								</c:choose>

							</c:forEach>
						</select>

					</div>
					<div class="form-group">
						<label><fmt:message key="label.standards" /></label> <select class="form-control" name="standards" multiple="multiple">
							<c:forEach items="${analyses}" var="analysis">
								<c:forEach items="${analysis.analysisStandards}" var="analysisStandard">
									<c:if test="${standards.contains(analysisStandard.standard)}">
										<option title="${analysisStandard.standard.label} - v.${analysisStandard.standard.version}" value="${analysisStandard.standard.id}" data-trick-id="${analysis.id}"><spring:message
												text="${analysisStandard.standard.label}" /></option>
									</c:if>
								</c:forEach>
							</c:forEach>
						</select>
					</div>
				</form>
				<div class="progress progress-striped active" hidden="true" style="margin-top: 10px; margin-bottom: -15px">
					<div class="progress-bar" role="progressbar" data-aria-valuenow="100" data-aria-valuemin="0" data-aria-valuemax="100" style="width: 100%"></div>
				</div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-danger pull-left" name="show_rrf">
					<fmt:message key="label.action.switch.rrf" />
				</button>
				<button type="button" class="btn btn-primary" name="import">
					<fmt:message key="label.action.import" />
				</button>
				<button type="button" class="btn btn-default" name="cancel">
					<fmt:message key="label.action.cancel" />
				</button>
			</div>
		</div>
		<!-- /.modal-content -->
	</div>
	<!-- /.modal-dialog -->
</div>
<!-- /.modal -->