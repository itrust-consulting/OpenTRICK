<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div id="analysis-import-dialog" class="modal fade" role="dialog" tabindex="-1" data-aria-hidden="true" data-backdrop="static">
	<div class="modal-dialog modal-sml">
		<div class="modal-content">
			<div class="modal-header bg-primary">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title">
					<spring:message code="label.title.import.risk.analysis" text="Import a new analysis" />
				</h4>
			</div>
			<!-- dialog body -->
			<div class="modal-body">
				<form class="form form-inline" action="${pageContext.request.contextPath}/Analysis/Import/${customer.id}" method="post" enctype="multipart/form-data">
					<input type="hidden" name="customer" id="importAnalysis.customer" value="${customer.id}">
					<c:choose>
						<c:when test="${ maxFileSize< 1024}">
							<spring:message code="label.max.unit.data.byte" arguments="${maxFileSize}" var="maxSizeInfo" />
						</c:when>
						<c:when test="${ maxFileSize < 1048576}">
							<spring:message code="label.max.unit.data.kilo.byte" arguments="${maxFileSize / 1024}" var="maxSizeInfo" />
						</c:when>
						<c:otherwise>
							<spring:message code="label.max.unit.data.mega.byte" arguments="${maxFileSize / 1048576}" var="maxSizeInfo" />
						</c:otherwise>
					</c:choose>
					<label><spring:message code="label.import.analysis.select.sqlite" /></label>
					<div class="input-group-btn">
						<input id="importAnalysis.file" type="file" accept=".sqlite,.tsdb" name="file" style="display: none;" maxlength="${maxFileSize}" /> <input id="importAnalysis.file.info"
							name="filename" placeholder="${maxSizeInfo}" class="form-control" readonly="readonly" required="required" style="width: 83%" />
						<button class="btn btn-primary" type="button" id="importAnalysis.file.browse.button" name="browse" style="margin-left: -5px;">
							<spring:message code="label.action.browse" text="Browse" />
						</button>
					</div>


					<button name='submit' type="submit" style="display: none"></button>
					<div class='clearfix'></div>
				</form>

			</div>
			<!-- dialog buttons -->
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" name="import">
					<spring:message code="label.action.import" />
				</button>
				<button type="button" class="btn btn-default" name="cancel" data-dismiss="modal">
					<spring:message code="label.action.cancel" text="Cancel" />
				</button>
			</div>
		</div>
	</div>
</div>