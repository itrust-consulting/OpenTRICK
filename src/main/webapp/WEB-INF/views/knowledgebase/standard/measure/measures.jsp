<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!-- ################################################################ Set Page Title ################################################################ -->
<c:set scope="request" var="title">title.knowledgebase</c:set>
<!-- ###################################################################### HTML #################################################################### -->
<html>
<!-- Include Header -->
<jsp:include page="../../../header.jsp" />
<!-- ################################################################# Start Container ############################################################## -->
<body>
	<div id="wrap">
		<!-- ################################################################### Nav Menu ################################################################### -->
		<jsp:include page="../../../menu.jsp" />
		<div class="container">
			<jsp:include page="../../../successErrors.jsp" />
			<!-- #################################################################### Content ################################################################### -->
			<div class="row">
				<div class="page-header">
					<h3 id="Measures">
						<spring:message code="label.measure.measures" /> : ${norm}
					</h3>
				</div>
				<div class="content" role="main" data-spy="scroll">
					<c:if test="${!empty measureDescriptions}">
						<div class="panel panel-default">
							<div class="panel-heading">
								<button class="btn btn-default" onclick="newMeasure();">
									<spring:message code="label.measure.add.menu" text="Add a new Measure" />
								</button>
								<select>
								</select>
							</div>
							<div class="panel-body">
								<table id="measurestable">
									<thead>
										<tr>
											<th><spring:message code="label.measure.id" text="id" /></th>
											<th><spring:message code="label.measure.level" /></th>
											<th><spring:message code="label.measure.reference" /></th>
											<th><spring:message code="label.measure.domain" /></th>
											<th><spring:message code="label.measure.description" /></th>
											<th><spring:message code="label.action" /></th>
										</tr>
									</thead>
									<tbody>
										<c:forEach items="${measureDescriptions}" var="measureDescription">
											<tr>
												<td>${measureDescription.id}</td>
												<td>${measureDescription.level}</td>
												<td>${measureDescription.reference}</td>
												<td>${measureDescription.measureDescriptionTexts[0].domain.equals("")==false?measureDescription.measureDescriptionTexts[0].domain:"&nbsp;"}</td>
												<td>${measureDescription.measureDescriptionTexts[0].description.equals("")==false?measureDescription.measureDescriptionTexts[0].description:"&nbsp;"}</td>
												<td><a href="Edit/${measureDescription.id}"><spring:message code="label.action.edit" /></a>| <a href="Delete/${measureDescription.id}"><spring:message
															code="label.action.delete" /></a></td>
											</tr>
										</c:forEach>
									</tbody>
								</table>
							</div>
						</div>
					</c:if>
					<c:if test="${empty measureDescriptions}">
						<div class="panel panel-default">
							<div class="panel-heading">
								<button class="btn btn-default" data-toggle="modal" data-target="#addMeasureModel">
									<spring:message code="label.measure.add.menu" text="Add new Measure" />
								</button>
							</div>
							<div class="panel-body">
								<h4>
									<spring:message code="label.measure.notexist" />
								</h4>
							</div>
						</div>
					</c:if>
				</div>
			</div>
			<!-- ################################################################ End Container ################################################################# -->
		</div>
		<!-- ################################################################ Include Footer ################################################################ -->
		<jsp:include page="../../../footer.jsp" />
		<script src="${pageContext.request.contextPath}/js/jquery-2.0.js"></script>
		<script src="${pageContext.request.contextPath}/js/jquery-ui.js"></script>
		<script src="${pageContext.request.contextPath}/js/bootstrap.min.js"></script>
		<script src="${pageContext.request.contextPath}/js/bootbox.min.js"></script>
		<script src="${pageContext.request.contextPath}/js/dom-parser.js"></script>
		<script src="${pageContext.request.contextPath}/js/main.js"></script>
		<script src="${pageContext.request.contextPath}/js/datatables/media/js/jquery.dataTables.min.js"></script>
		<script type="text/javascript">
							$(document).ready(function() {
								$('#measurestable').dataTable({
									"bLengthChange" : false,
									"aoColumns": [
													{ "sWidth": "20px" },
													{ "sWidth": "20px" },
													{ "sWidth": "20px" },
													null,
													null,
													{ "sWidth": "20px" }
												]
								});
							});
						</script>
	</div>
</body>
<!-- ################################################################### End HTML ################################################################### -->
</html>