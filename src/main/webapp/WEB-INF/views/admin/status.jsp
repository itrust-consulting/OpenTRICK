<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="section" id="section_status">
	<div class="page-header">
		<h3 id="Status">
			<spring:message code="menu.admin.status" text="Status" />
		</h3>
	</div>
	<div class="panel panel-default">
		<div class="panel-heading" style="min-height: 60px">
			<ul class="nav nav-pills" id="menu_status">
				<li>
					<button class="btn btn-primary btn-lg" type="button" onclick="return installTrickService();">
						<c:choose>
							<c:when test="${status.installed == true}">
								<spring:message code="label.reinstall" text="Reinstall TRICK Service" />
							</c:when>
							<c:when test="${status.installed == false}">
								<spring:message code="label.installed.no" text="Install TRICK Service" />
							</c:when>
						</c:choose>
					</button>
				</li>
			</ul>
		</div>
		<div class="panel-body autofitpanelbodydefinition">
			<div class="content" style="margin: 0 auto; max-width: 600px;">
				<!-- <img class="center-block" alt=<spring:message code="label.logo" text="Logo" /> src=<spring:url value="/data/Logo_TRICKService.png" /> style="max-width: 600px; height: auto;"> -->
				<table class="table">
					<thead>
						<tr>
							<th style="text-align: center;"><spring:message code="label.status.version" /></th>
							<th style="text-align: center;"><spring:message code="label.status.installed" /></th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td style="text-align: center;">${status.version}</td>
							<c:choose>
								<c:when test="${status.installed == true}">
									<td style="text-align: center;"><spring:message code="label.installed.yes" text="Installed" /></td>
								</c:when>
								<c:when test="${status.installed == false}">
									<td style="text-align: center;"><spring:message code="label.installed.no" text="Not installed" /></td>
								</c:when>
							</c:choose>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</div>