<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<c:if test="${empty locale }">
	<spring:eval expression="T(org.springframework.web.servlet.support.RequestContextUtils).getLocale(pageContext.request)" var="locale" />
</c:if>
<fmt:setLocale value="${locale.language}" scope="session"/>
<div class="modal fade" id="modal-ticketing-view" tabindex="-1" role="dialog" data-aria-labelledby="modalTicketingView" data-aria-hidden="true" data-backdrop="static">
	<div class="modal-dialog" style="width: 800px;">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="false">&times;</button>
				<h3 class="modal-title">
					<spring:message text="${first.type} #${first.id}" />
				</h3>
			</div>
			<div class="modal-body" style="height: 700px;">
				<c:forEach items="${tasks}" var="task">
					<fieldset data-trick-id='${task.id}' style="display: ${first != task? 'none':''}" data-title='<spring:message text="${task.type} #${task.id}" />'>
						<h4>
							<spring:message text="${task.name}" />
						</h4>
						<p class="text-muted">
							<fmt:formatDate value="${task.created}" var="created" />
							<spring:message code="label.add.by" arguments="${task.reporter},${created}" text="Add by ${task.reporter}, ${created}" />
						</p>
						<c:set value="${task.customFields.remove('Resolution')}" var="resolution"/>
						<table class="table">
							<thead>
								<tr>
									<th><spring:message code="label.status" text="Status" /></th>
									<th><spring:message code="label.priority" text="Priority" /></th>
									<th><spring:message code="label.assignee" text="Assignee" /></th>
									<th><spring:message code="label.created_date" text="Created" /></th>
									<th><spring:message code="label.due.date" text="Due date" /></th>
									<th><spring:message code="label.resolution" text="Resolution" /></th>
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
									<td><spring:message text="${resolution.value}" /></td>
									<td><div class="progress" title="${task.progress}%">
											<div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="${task.progress}" aria-valuemin="0" aria-valuemax="100"
												style="width: ${task.progress}%;">
												<span style="color: #333;">${task.progress}%</span>
											</div>
										</div></td>
								</tr>
							</tbody>
						</table>
						<h4>
							<spring:message code="label.description" text="Description" />
						</h4>
						<div class="well well-sm re-pre"><spring:message text='${task.description}' /></div>
						<c:forEach items="${task.customFields.values()}" var="customField">
							<h4><spring:message text="${customField.name}" /></h4>
							<p class="well well-sm re-pre"><spring:message text="${customField.value}"/></p>
						</c:forEach>
						<div class='panel-group' id='view-sub-item-${task.id}' role='tablist' aria-multiselectable="true">
							<c:if test="${not empty task.comments}">
								<div class='panel panel-info'>
									<div class='panel-heading' role='tab' id='heading-view-task-comment-${task.id}'>
										<h4 class="panel-title">
											<a role="button" data-toggle="collapse" data-parent="#view-sub-item-${task.id}" href="#view-task-comment-${task.id}" aria-expanded="true" aria-controls="view-task-comment-${task.id}"><spring:message
													code='label.comments' text='Comments' /> </a>
										</h4>
									</div>
									<div id="view-task-comment-${task.id}" class="panel-collapse collapse in" role="tabpanel" aria-labelledby="heading-view-task-comment-${task.id}">
										<div class='panel-body'>
											<c:forEach items="${task.comments}" var="comment">
											<blockquote>
												<p><spring:message text="${comment.description}" /></p>
												<fmt:formatDate value="${comment.created}" var="dateComment"/>
												<footer><spring:message text="${comment.author}, ${dateComment}"/></footer>
											</blockquote>
											</c:forEach>
										</div>
									</div>
								</div>
							</c:if>
							<c:if test="${not empty task.subTasks}">
								<div class='panel panel-info'>
									<div class='panel-heading' role='tab' id='heading-view-sub-task--${task.id}'>
										<h4 class="panel-title">
											<a role="button" data-toggle="collapse" data-parent="#view-sub-item-${task.id}" href="#view-sub-task--${task.id}" aria-expanded="true" aria-controls="view-sub-task--${task.id}"><spring:message
													code='label.sub_tasks' text='Sub tasks' /> </a>
										</h4>
									</div>
									<div id="view-sub-task--${task.id}" class="panel-collapse collapse" role="tabpanel" aria-labelledby="heading-view-sub-task--${task.id}">
										<div class='panel-body'>
											<table class="table">
												<thead>
													<tr>
														<th>#</th>
														<th><spring:message code='label.name' text="Name" /></th>
														<th><spring:message code='label.status' text="Status" /></th>
														<th><spring:message code='label.assignee' text="Assignee" /></th>
														<th><spring:message code="label.progress" text="Progress" /></th>
													</tr>
												</thead>
												<tbody>
													<c:forEach items="${task.subTasks}" var="subTask">
														<tr>
															<td><spring:message text="${subTask.type} #${subTask.id}" /></td>
															<td><spring:message text="${subTask.name}" /></td>
															<td><spring:message text="${subTask.status}" /></td>
															<td><spring:message text="${subTask.assignee}" /></td>
															<td>
																<div class="progress" title="${subTask.progress}%">
																	<div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="${subTask.progress}" aria-valuemin="0" aria-valuemax="100"
																		style="width: ${subTask.progress}%;">
																	</div>
																</div>
															</td>
														</tr>
														
													</c:forEach>
												</tbody>
											</table>
										</div>
									</div>
								</div>
							</c:if>
							<c:if test="${not empty task.issueLinks}">
								<div class='panel panel-info'>
									<div class='panel-heading' role='tab' id='heading-view-issue-link-${task.id}'>
										<h4 class="panel-title">
											<a role="button" data-toggle="collapse" data-parent="#view-sub-item-${task.id}" href="#view-issue-link-${task.id}" aria-expanded="true" aria-controls="view-issue-link-${task.id}"><spring:message
													code='label.issue_links' text='Issues links' /> </a>
										</h4>
									</div>
									<div id="view-issue-link-${task.id}" class="panel-collapse collapse" role="tabpanel" aria-labelledby="heading-view-issue-link-${task.id}">
										<div class='panel-body'>
												<c:forEach items="${task.issueLinks}" var="issueLink">
													<a href="${issueLink.link}" target="_blank" class="btn btn-link"><i class="fa fa-external-link" aria-hidden="true"></i> <spring:message text="${issueLink.name}"/></a>
												</c:forEach>
										</div>
									</div>
								</div>
							</c:if>
							
						</div>
					</fieldset>
				</c:forEach>
			</div>
			<div class="modal-footer">
				<c:if test="${tasks.size()>1}">
					<nav class="col-lg-10">
						<ul class="pager" style="margin: 0px;">
							<li class="previous disabled"><a href="#"><span aria-hidden="true">&larr;</span> <spring:message code="label.action.previous"/></a></li>
							<li class="next"><a href="#"><spring:message code="label.action.next"/> <span aria-hidden="true">&rarr;</span></a></li>
						</ul>
					</nav>
				</c:if>
				<button class="btn btn-default" data-dismiss="modal" data-aria-hidden="false"><spring:message code='label.action.close' text='Close'/></button>
			</div>
		</div>
	</div>
</div>