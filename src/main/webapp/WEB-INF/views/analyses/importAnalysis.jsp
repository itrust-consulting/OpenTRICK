<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<html>
<c:set scope="request" var="title" value="label.title.analysis.import" />
<jsp:include page="../template/header.jsp" />
<body>
	<div id="wrap" class="wrap">
		<jsp:include page="../template/menu.jsp" />
		<div class="container">
			<div class="page-header">
				<h1>
					<spring:message code="label.title.import.analysis" text="Import a new Analysis" />
				</h1>
				<jsp:include page="../template/successErrors.jsp" />
			</div>
			<div id="import-container">
				<c:if test="${!empty customers}">
					<h3>
						<spring:message code="label.import.analysis.description" text="Please select a customer then, choose a sqlite file and click on submit" />
					</h3>
					<form id="importform" name="importform" method="post" action="${pageContext.request.contextPath}/Analysis/Import/Execute?${_csrf.parameterName}=${_csrf.token}" enctype="multipart/form-data">
						<div class="row">
							<div class="col-lg-12" style="margin-bottom: 10px;">
								<div class="input-group">
									<div class="row">
										<h4 class="col-lg-10">
											<b>1.</b>
											<spring:message code="label.import.analysis.select.customer" text="Select a customer" />
										</h4>
										<div class="col-lg-10">
											<select id="customerId" name="customerId" onchange="customerChanged()" class="form-control" style="width: 250px">
												<option value="-1">
													<spring:message code="label.action.choose" />
												</option>
												<c:forEach items="${customers}" var="customer">
													<option value="${customer.id}"><spring:message text="${customer.organisation}"/></option>
												</c:forEach>
											</select>
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class="row">
							<div class="col-lg-12" style="margin-bottom: 10px;">
								<div class="input-group">
									<div class="row">
										<h4 class="col-lg-8">
											<b>2.</b>
											<spring:message code="label.import.analysis.select.sqlite" text="Select a sqlite file" />
										</h4>
										<div class="col-lg-7">
											<div class="input-group-btn">
												<input id="file" onchange="checkFile(true)" type="file" name="file" accept=".sqlite" style="display: none;" disabled /> <input id="upload-file-info" class="form-control"
													readonly="readonly" />
												<button class="btn btn-default" type="button" id="browse-button" onclick="$('input[id=file]').click();" disabled><spring:message code="label.action.browse" text="Browse"/></button>
											</div>
										</div>
									</div>
								</div>
							</div>
							<div class="col-lg-1">
								<button id="validation" type="submit" class="btn btn-primary btn-block" disabled>
									<spring:message code="label.action.import" text="Import" />
								</button>
							</div>
						</div>
					</form>
				</c:if>
				<c:if test="${empty customers}">
					<h2>
						<spring:message code="label.import.no_customer"
							text="You do not have access to any customers, create a new customer or contact an administrator to get access to you customers!" />
					</h2>
				</c:if>
			</div>
		</div>
		<jsp:include page="../template/footer.jsp" />
	</div>
	<jsp:include page="../template/scripts.jsp" />
	<script type="text/javascript">
		function customerChanged() {
			var e = document.getElementById("customerId");

			var strUser = e.options[e.selectedIndex].value;

			if (strUser == -1) {
				document.getElementById("file").disabled = true;
				document.getElementById("validation").disabled = true;
				$("#browse-button").prop("disabled", true);
			} else {
				document.getElementById("file").disabled = false;
				$("#browse-button").prop("disabled", false);
				checkFile(false);
			}
		}

		function checkFile(b) {
			var fileVal = document.getElementById("file").value;

			var ext = fileVal.substr(fileVal.length - 7, fileVal.length);

			switch (ext) {
			case '.sqlite':
				document.getElementById("validation").disabled = false;
				$("#upload-file-info").prop("value", fileVal);
				break;
			default:
				document.getElementById("validation").disabled = true;
				if (b) {
					if ($("#import-container .alert.alert-error").length)
						$("#import-container .alert.alert-error").remove();
					showError($('#import-container')[0], '<spring:message code="error.import.analysis.selected_file" text="You must import a .sqlite file!"/>');
				}
				break;
			}
		}
	</script>
</body>
</html>