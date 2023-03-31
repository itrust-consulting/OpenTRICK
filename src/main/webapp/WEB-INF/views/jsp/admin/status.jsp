<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="tab-pane active" id="tab-status">
	<div class="section" id="section_status">
		<table class="table table-condensed">
			<thead>
				<tr>
					<th style="text-align: center;"><spring:message code="label.installation.status.version" text="Version" /></th>
					<th style="text-align: center;"><spring:message code="label.installation.status.installed" text="Installed" /></th>
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
		<div class="center-block" style="width: 200px;">
			<a href="#" class="btn btn-primary" onclick="return installTrickService();"> <c:choose>
					<c:when test="${status.installed == true}">
						<spring:message code="label.installation.re_install" text="Reinstall TRICK Service" />
					</c:when>
					<c:otherwise>
						<spring:message code="label.installation.install" text="Install TRICK Service" />
					</c:otherwise>
				</c:choose>
			</a>
		</div>
	</div>
</div>