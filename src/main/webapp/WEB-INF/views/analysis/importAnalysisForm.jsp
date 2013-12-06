<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>

<!-- ################################################################ Set Page Title ################################################################ -->

<c:set scope="request" var="title">title.analysis.import</c:set>

<!-- ###################################################################### HTML #################################################################### -->

<html>

<!-- Include Header -->
<jsp:include page="../header.jsp" />


<!-- ################################################################# Start Container ############################################################## -->

<body>

	<div id="wrap">

		<!-- ################################################################### Nav Menu ################################################################### -->

		<jsp:include page="../menu.jsp" />
		
		<div class="container">

			<!-- #################################################################### Content ################################################################### -->

			<div class="jumbotron" style="background-color: rgba(0, 0, 0, 0);"
				id="content">
				<c:if test="${!empty customers}">
					<jsp:include page="../successErrors.jsp" />
					<h2 class="text-muted">
						<spring:message code="label.analysis.import.title"
							text="Import a new Analysis" />
					</h2>
					<label class="text-muted"> <spring:message
							code="label.analysis.import.description"
							text="Please select a customer, choose a sqlite file and click on submit" />
					</label>
					<form id="importform" name="importform" method="post"
						action="${pageContext.request.contextPath}/Analysis/Import/Execute" enctype="multipart/form-data">
						<div class="input-group">
							<span class="text-muted"> 1. <spring:message
									code="label.analysis.import.select.customer" /></span>
							<form:select id="customerId" name="customerId" path="customerId"
								onchange="customerChanged()" cssClass="form-control">
								<form:option value="-1">
									<spring:message code="label.action.choose" />
								</form:option>
								<form:options items="${customers}" itemLabel="contactPerson"
									itemValue="id" />
							</form:select>
						</div>
						<div class="row">
							<div class="col-lg-12" style="margin-bottom: 10px;">
								<div class="input-group">
									<div class="row">
										<h4 class="col-lg-10 text-muted">
											2.
											<spring:message code="label.analysis.import.select.sqlite" />
										</h4>
										<div class="col-lg-10">
											<div class="input-group-btn">
												<input id="file" onchange="checkFile(true)" type="file"
													name="file" style="display: none;" disabled /> <input
													id="upload-file-info" class="form-control"
													readonly="readonly" />
												<button class="btn btn-primary" type="button"
													id="browse-button" onclick="$('input[id=file]').click();"
													disabled>Browse</button>
											</div>
										</div>
									</div>
								</div>
							</div>
							<div class="col-lg-2">
								<button id="validation" type="submit"
									class="btn btn-primary btn-block" disabled>
									<spring:message code="label.analysis.import.submit" />
								</button>
							</div>
						</div>
					</form>
				</c:if>
			</div>


			<!-- ################################################################ End Container ################################################################# -->

		</div>
		
		<!-- ################################################################ Include Footer ################################################################ -->
		<jsp:include page="../footer.jsp" />
		
		<jsp:include page="../scripts.jsp" />
		
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
					if (b)
						alert("You must import a .sqlite file!");
					break;
				}
			}
		</script>
	</div>
</body>

<!-- ################################################################### End HTML ################################################################### -->

</html>