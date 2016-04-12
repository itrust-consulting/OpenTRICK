<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="modal fade" id="modal-ticketing-view" tabindex="-1" role="dialog" data-aria-labelledby="modalTicketingView" data-aria-hidden="true" data-backdrop="static">
	<div class="modal-dialog" style="width: 800px;">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title">
					<spring:message text="${project.name}" />
				</h4>
			</div>
			<div class="modal-body">
				<c:forEach items="${project.tasks}" var="task">
					<fieldset data-trick-id='${task.id}'>
						<legend>
							<spring:message text="${task.type}#${task.id}" />
						</legend>
					</fieldset>
					<h4>
						<spring:message text="${task.name}" />
					</h4>
					<fmt:formatDate value="${task.created}" var="created" />
					<p>
						<spring:message code="label.add.by" arguments="${task.reporter},${created}" text="Add by ${task.reporter}, ${created}" />
					</p>
					
					<dl class="dl-horizontal">
						<dt style="text-align: left;">
							<spring:message code="label.status" text="Status" />
						</dt>
						<dd>
							<spring:message text="${task.status}" />
						</dd>
					</dl>

					<dl class="dl-horizontal">
						<dt style="text-align: left;">
							<spring:message code="label.created_date" text="Created" />
						</dt>
						<dd>
							<fmt:formatDate value="${task.created}" />
						</dd>
					</dl>

					<dl class="dl-horizontal" style="width: 50%; text-align: left;">
						<dt>
							<spring:message code="label.priority" text="Priority" />
						</dt>
						<dd>
							<spring:message text="${task.priority}" />
						</dd>
					</dl>

					<dl class="dl-horizontal" style="width: 50%; text-align: left;">
						<dt>
							<spring:message code="label.due.date" text="Due date" />
						</dt>
						<dd>
							<fmt:formatDate value="${task.due}" />
						</dd>
					</dl>

					<dl class="dl-horizontal" style="width: 50%; text-align: left;">
						<dt>
							<spring:message code="label.assignee" text="Assignee" />
						</dt>
						<dd>
							<spring:message text="${task.assignee}" />
						</dd>
					</dl>

					<dl class="dl-horizontal" style="width: 50%; text-align: left;">
						<dt>
							<spring:message code="label.progress" text="Progress" />
						</dt>
						<dd>
							<fmt:formatNumber value="${task.progress}" type="PERCENT"/>
						</dd>
					</dl>
					<dl>
						<dt>
							<spring:message code="label.description" text="Description" />
						</dt>
						<dd>
							<spring:message text='${task.description}' />
						</dd>
					</dl>
				</c:forEach>
			</div>
			<div class="modal-footer">
				<button class="btn btn-default">First</button>
				<button class="btn btn-default">Previous</button>
				<button class="btn btn-default">Next</button>
				<button class="btn btn-default">Last</button>
				<button class="btn btn-default pull-right">Cancel</button>
			</div>
		</div>
	</div>
</div>