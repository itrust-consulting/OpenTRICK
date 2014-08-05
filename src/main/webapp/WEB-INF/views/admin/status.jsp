<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="section" id="section_status">
	<div class="page-header">
		<h3 id="Status">
			<spring:message code="label.menu.installation.status" text="Status" />
		</h3>
	</div>
	<div class="panel panel-default">
		<div class="panel-heading" style="min-height: 60px">
			<ul class="nav nav-pills " id="menu_status">
				<li class="active">
					<a href="#" onclick="return installTrickService();">
						<c:choose>
							<c:when test="${status.installed == true}">
								<spring:message code="label.installation.re_install" text="Reinstall TRICK Service" />
							</c:when>
							<c:otherwise>
								<spring:message code="label.installation.install" text="Install TRICK Service" />
							</c:otherwise>
						</c:choose>
					</a>
				</li>
			</ul>
		</div>
		<div class="panel-body autofitpanelbodydefinition">
			<div class="content" style="margin: 0 auto; max-width: 600px;">
				<!-- <img class="center-block" alt=<spring:message code="label.logo" text="Logo" /> src=<spring:url value="/data/Logo_TRICKService.png" /> style="max-width: 600px; height: auto;"> -->
				<table class="table">
					<thead>
						<tr>
							<th style="text-align: center;"><spring:message code="label.installation.status.version" text="Version"/></th>
							<th style="text-align: center;"><spring:message code="label.installation.status.installed" text="Installed"/></th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td style="text-align: center;">${status.version}</td>
							<c:choose>
								<c:when test="${status.installed == true}">
									<td style="text-align: center;"><spring:message code="label.installation.status.installed.yes" text="Installed" /></td>
								</c:when>
								<c:when test="${status.installed == false}">
									<td style="text-align: center;"><spring:message code="label.installation.status.installed.no" text="Not installed" /></td>
								</c:when>
							</c:choose>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</div>