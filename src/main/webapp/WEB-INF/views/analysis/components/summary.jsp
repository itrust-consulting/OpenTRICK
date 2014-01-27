<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<div class="section" id="section_summary">
	<div class="page-header">
		<h3 id="Phase">
			<spring:message code="label.summaries" text="Summaries" />
		</h3>
	</div>
	<spring:eval expression="T(lu.itrust.business.component.ActionPlanSummaryManager).buildTables(summaries,phases)" var="summariesStages" />
	<div class="panel panel-default">
		<div class="panel-heading" style="min-height: 60px">
			<ul class="nav nav-pills">
				<c:forEach items="${summariesStages.keySet()}" var="actionPlanType" varStatus="status">
					<li ${status.index==0? "class='disabled'" : ""} trick-nav-control="${actionPlanType.name}"><a href="#"
						onclick="return navToogled('section_summary','${actionPlanType.name}')"><spring:message code="label.actionPlanType.${actionPlanType.name}" text="${actionPlanType.name}"
								htmlEscape="true" /></a></li>
				</c:forEach>
			</ul>
		</div>
		<div class="panel-body" style="max-height: 700px; overflow: auto;">
			<c:forEach items="${summariesStages.keySet()}" var="actionPlanType" varStatus="status">
				<c:set var="summaryStages" value="${summariesStages.get(actionPlanType)}" />
				<div trick-nav-data="${actionPlanType.name}" ${status.index!=0? "hidden='true'" : "" }>
					<h4 class="text-center">
						<spring:message code="label.actionPlanType.${actionPlanType.name}" text="${actionPlanType.name}" htmlEscape="true" />
					</h4>
					<table class="table table-hover">
						<thead>
							<tr>
								<th><spring:message code="label.characteristic" text="Phase characteristic" /></th>
								<c:forEach items='${summaryStages.get("label.characteristic")}' var="phase">
									<th><spring:message text="${phase}" /></th>
								</c:forEach>
								<c:set var="rowCount" value="${summaryStages.get('label.characteristic').size()}" />
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${summaryStages.keySet()}" var="key" varStatus="status">
								<c:if test="${status.index != 0 }">
									<tr>
										<c:choose>
											<c:when test="${empty summaryStages.get(key)}">
												<td colspan="${rowCount+1}"><spring:message code="${key}" text="${key}" /></td>
											</c:when>
											<c:otherwise>
												<td><spring:message code="${key}" text="${key}" /></td>
												<c:forEach items="${summaryStages.get(key)}" var="value">
													<td><spring:message text="${value}" htmlEscape="true" /></td>
												</c:forEach>
											</c:otherwise>
										</c:choose>
									</tr>
								</c:if>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</c:forEach>
		</div>
	</div>
</div>