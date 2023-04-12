<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<c:if test="${empty locale }">
	<spring:eval expression="T(org.springframework.web.servlet.support.RequestContextUtils).getLocale(pageContext.request)" var="locale" />
</c:if>
<fmt:setLocale value="${locale.language}" scope="session"/>
<div class="modal fade" id="modal-ticketing-linker" tabindex="-1" role="dialog" data-aria-labelledby="modalTicketingLinker" data-aria-hidden="true" data-backdrop="static">
	<div class="modal-dialog modal-lgx">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="false">&times;</button>
				<h3 class="modal-title">
					<spring:message code='label.link.measures.to.project' text="Link measures to project" />
				</h3>
			</div>
			<div class="modal-body" style="height: 700px;">
				<div class="col-xs-3" style="height: 94.5%">
					<div class="list-group" style="height: 100%">
						<span class="list-group-item list-group-item-warning"><spring:message code='label.measures' text="Measures" /></span>
						<div class="scrollable" style="height: 100%" id='measure-container'>
							<c:forEach items="${measures}" var="measure">
								<c:set var="description" value="${measure.measureDescription.getMeasureDescriptionTextByAlpha2(language)}" />
								<spring:message text='${measure.measureDescription.standard.label} - ${measure.measureDescription.reference} - ${description.domain}' var="measureTitle"/>
								<a class="list-group-item" id="measure-controller-${measure.id}" aria-labelledby='measure-view-${measure.id}' href="#measure-view-${measure.id}" title="${measureTitle}"
									style="white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">${measureTitle}</a>
								<fieldset id='measure-view-${measure.id}' aria-controls='measure-controller-${measure.id}' 
									style="display: none" data-trick-id='${measure.id}' data-trick-parent-id='${measure.measureDescription.standard.id}'>
									<legend>${measureTitle}</legend>
									<h4><spring:message code='label.to_do' text='Todo' /></h4>
									<div class="well well-sm re-pre"><spring:message text='${measure.toDo}' /></div>
									<h4><spring:message code='label.description' text='Description' /></h4>
									<div class="well well-sm re-pre"><spring:message text="${description.description}" /></div>
								</fieldset>
							</c:forEach>
						</div>
					</div>
				</div>
				<div class="col-xs-6" style="height: 94.5%">
					<div class="panel panel-warning" style="height: 40%;">
						<div id="measure-viewer" class='panel-body' style="height: 100%; overflow-x: auto;">
						</div>
					</div>
					<div class="text-center" style="height: 3%; margin-bottom: 16px; margin-top: -20px;">
						<a href="#" id='measure-task-linker' title='<spring:message code='label.action.linked' text='Linked'/>' class="btn btn-link"><i class="fa fa-link fa-2x" style="transform: rotate(45deg)" aria-hidden="true"></i>
						</a>
					</div>
					<div class='panel panel-info' style="height: 60%">
						<div class='panel-body' id='task-viewer' style="height: 100%; overflow-x: auto;">
						</div>
					</div>
				</div>
				<div class="col-xs-3" style="height: 94.5%">
					<div class="list-group" style="height: 100%">
						<span class="list-group-item list-group-item-info"><spring:message code='label.tickets' text="Tickets" /></span>
						<div class="scrollable" style="height: 100%;" id="task-container" data-offset="${tasks.getOffset()}" data-max-size="${tasks.getMaxSize()}, ${tasks.getOffset()}">
							<c:forEach items="${tasks}" var="task">
								<spring:message text="${task.id}" var='taskId' />
								<a id='task-controller-${taskId}' class="list-group-item" href="#task-view-${taskId}" title='<spring:message text="${task.name}" />' aria-labelledby='task-view-${taskId}' style="white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"> <span
									class='list-group-item-heading'><spring:message text="${task.type} #${task.id}" /> - <spring:message text="${task.name}" /></span>
								</a>
								<fieldset id="task-view-${taskId}" aria-controls='task-controller-${taskId}' style="display: none" data-trick-id='${task.id}' data-title='<spring:message text="${task.type} #${task.id}" />'>
									<legend><spring:message text="${task.type} #${task.id}"/></legend>
									<h4>
										<spring:message text="${task.name}" />
									</h4>
									<p class="text-muted">
										<fmt:formatDate value="${task.created}" var="created" />
										<spring:message code="label.add.by" arguments="${task.reporter},${created}" text="Add by ${task.reporter}, ${created}" />
									</p>
									<table class="table">
										<thead>
											<tr>
												<th><spring:message code="label.status" text="Status" /></th>
												<th><spring:message code="label.priority" text="Priority" /></th>
												<th><spring:message code="label.assignee" text="Assignee" /></th>
												<th><spring:message code="label.created_date" text="Created" /></th>
												<th><spring:message code="label.due.date" text="Due date" /></th>
												<th><spring:message code="label.progress" text="Progress" /></th>
											</tr>
										</thead>
										<tbody>
											<tr>
												<td><spring:message text="${task.status}" /></td>
												<td><spring:message text="${task.priority}" /></td>
												<td><spring:message text="${task.assignee}" /></td>
												<td><fmt:formatDate value="${task.created}" /></td>
												<td><fmt:formatDate value="${task.due}" /></td>
												<td>
													<div class="progress" title="${task.progress}%">
														<div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="${task.progress}" aria-valuemin="0" aria-valuemax="100"
															style="width: ${task.progress}%;">
															<span style="color: #333;">${task.progress}%</span>
														</div>
													</div>
												</td>
											</tr>
										</tbody>
									</table>
									<h4>
										<spring:message code="label.description" text="Description" />
									</h4>
									<div class="well well-sm re-pre"><spring:message text='${task.description}' /></div>
								</fieldset>
							</c:forEach>
						</div>
					</div>
				</div>
			</div>
			<div class="modal-footer">
				<button class="btn btn-default" data-dismiss="modal" data-aria-hidden="false">
					<spring:message code='label.action.close' text='Close'/>
				</button>
			</div>
		</div>
	</div>
</div>