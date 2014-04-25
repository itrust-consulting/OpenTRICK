<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<c:set scope="request" var="title">title.status</c:set>
<html>
<!-- Include Header -->
<jsp:include page="../header.jsp" />
<body>
	<div id="wrap">
		<jsp:include page="../menu.jsp" />
		<div class="container">
			<jsp:include page="../successErrors.jsp" />
			<div class="page-header">
				<h1>
					<spring:message code="menu.admin.status" text="Status" />
				</h1>
			</div>
			<div class="content" id="content" style="margin: 0 auto; max-width: 600px;">
				<img class="center-block" alt=<spring:message code="label.logo" text="Logo" /> src=<spring:url value="/data/Logo_TRICKService.png" /> style="max-width: 600px;height: auto;">
				<table class="table">
					<thead>
						<tr>
							<th style="text-align:center;"><spring:message code="label.status.version" /></th>
							<th style="text-align:center;"><spring:message code="label.status.installed" /></th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td style="text-align:center;">${status.version}</td>
							<c:choose>
								<c:when test="${status.installed == true}">
									<td style="text-align:center;"><spring:message code="label.installed.yes" text="Installed" /></td>
								</c:when>
								<c:when test="${status.installed == false}">
									<td style="text-align:center;"><spring:message code="label.installed.no" text="Not installed" /></td>
								</c:when>
							</c:choose>
						</tr>
						<tr>
						
						
						<td colspan="2" style="text-align:center;">
						
						 
						
						<button class="btn btn-primary btn-lg" type="button" onclick="return installTrickService();">
						<c:choose>
								<c:when test="${status.installed == true}">
									<spring:message code="label.reinstall" text="Reinstall TRICKService" />
								</c:when>
								<c:when test="${status.installed == false}">
									<spring:message code="label.installed.no" text="Install TRICKService" />
								</c:when>
							</c:choose>
						</button>
						</td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
		<jsp:include page="../footer.jsp" />
		<jsp:include page="../scripts.jsp" />
		<script type="text/javascript">
		
		function installTrickService() {
			
			$.ajax({
				url : context + "/Install",
				type : "GET",
				async : true,
				contentType : "application/json",
				success : function(response) {

					alert(response);

				},
				error : function(jqXHR, textStatus, errorThrown) {
					console.log(textStatus, errorThrown);
				},
			});
			
			return false;
		}
		
		</script>
	</div>
</body>
</html>