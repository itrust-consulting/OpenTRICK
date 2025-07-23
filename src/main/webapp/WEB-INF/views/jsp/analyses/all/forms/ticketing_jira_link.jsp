<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<c:if test="${empty locale }">
	<spring:eval expression="T(org.springframework.web.servlet.support.RequestContextUtils).getLocale(pageContext.request)" var="locale" />
</c:if>
<fmt:setLocale value="${locale.language}" scope="session"/>
<div class="modal fade" id="modal-ticketing-project-linker" tabindex="-1" role="dialog" data-aria-labelledby="modalTicketingProjectLinker" data-aria-hidden="true" data-backdrop="static">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header bg-primary">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="false">&times;</button>
				<h3 class="modal-title">
					<spring:message text="${analysis.label}, V.${analysis.version}" />
				</h3>
			</div>
			<div class="modal-body">
				<div class="form-group">
					<label class='control-label'><spring:message code='label.ticketing.link_to' text="Link to" /></label>
						<select name="project" class="form-control">
							<option disabled="disabled"><spring:message code='label.select.project.to.link' text='Select project' /></option>
							<c:forEach items="${projects}" var="project">
								<option value='<spring:message text="${project.id}"/>'><spring:message text="${project.name}" /></option>
							</c:forEach>
						</select>
				</div>
			</div>
			<div class='clearfix'></div>
			<div class="modal-footer">
				<button class="btn btn-primary" name="save"><spring:message code='label.action.save' text='Save' /></button>
				<button class="btn btn-default" data-dismiss="modal" data-aria-hidden="false"><spring:message code='label.action.cancel' text='Cancel' /></button>
			</div>
		</div>
	</div>
</div>