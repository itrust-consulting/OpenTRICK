<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!-- invitation -->
<div id="tab-invitation" class="tab-pane" data-update-required="true" data-trigger="loadUserInvitation" data-scroll-trigger="userInvitationScrolling">
	<div class="col-md-3 col-lg-2 nav-left-affix" role="left-menu">
		<strong><spring:message
				code="label.title.control" text="Control" /></strong>
		<form class="form-horizontal" name="invitationControl" id="formInvitationControl">
			<div class="form-group">
				<label for="pageSize" class="col-sm-4 control-label"> <spring:message code="label.page.size" text="Page size" />
				</label>
				<div class="col-sm-8">
					<select name="size" class="form-control" onchange="updateInvitationControl()" id="invitationPageSize">
						<option value="30" ${invitationControl.size == 30?'selected':''}>30</option>
						<option value="120" ${invitationControl.size == 120?'selected':''}>120</option>
						<option value="500" ${invitationControl.size == 500?'selected':''}>500</option>
						<option value="1000" ${invitationControl.size == 1000?'selected':''}>1000</option>
						<option value="2000" ${invitationControl.size == 2000?'selected':''}>2000</option>
					</select>
				</div>
			</div>
			<div class="form-group">
				<label for="sort" class="col-sm-4 control-label"><spring:message code="label.action.sort.over" text="Sort by" /></label>
				<div class="col-sm-8">
					<select name="sort" class="form-control" onchange="updateInvitationControl()">
						<c:forEach items="${invitationSortNames}" var="name">
							<option ${invitationControl.sort == name? 'selected' :''} value='<spring:message text='${name}'/>'><spring:message code='label.invitation.${name}' text='${name}' /></option>
						</c:forEach>
					</select>
				</div>
			</div>
			<div class="form-group">
				<label for="direction" class="col-sm-4 control-label"><spring:message code="label.action.sort" text="Sort" /></label>
				<div class="col-sm-8">
					<div class="btn-group" data-toggle="buttons">
						<label class="btn btn-default ${invitationControl.direction=='asc'? 'active':'' }"
							title='<spring:message
													code="label.sort_direction.ascending" text="Ascending" />'> <input type="radio" name="direction" value="asc"
							${invitationControl.direction=='asc'? 'checked':'' } autocomplete="off" onchange="updateInvitationControl(this)"><i class="fa fa-play fa-rotate-270"></i>
						</label> <label class="btn btn-default ${invitationControl.direction=='desc'? 'active':'' }"
							title='<spring:message
													code="label.sort_direction.descending" text="Descending" />'> <input type="radio" name="direction" value="desc"
							${invitationControl.direction=='desc'? 'checked':'' } autocomplete="off" onchange="updateInvitationControl(this)"><i class="fa fa-play fa-rotate-90"></i>
						</label>
					</div>
				</div>
			</div>

		</form>
		<div id="progress-invitation" class="center-block" style="width: 60px">
			<i class="fa fa-spinner fa-pulse fa-5x fa-align-center fa-spin"></i>
		</div>
	</div>
	<div class="col-md-9 col-lg-10" id="section_invitation"></div>
</div>
