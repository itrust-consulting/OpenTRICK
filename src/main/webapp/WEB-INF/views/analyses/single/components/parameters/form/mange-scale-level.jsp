<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<div class="modal fade" id="manageScaleLevelModal" tabindex="-1" role="dialog" data-aria-labelledby="manageScaleLevelModal" data-aria-hidden="true" data-backdrop="static"
	data-keyboard="true">
	<div class="modal-dialog modal-md">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title">
					<spring:message code="label.title.manage.analysis.impact_scale" text="Manage analysis impact scale" />
				</h4>
			</div>
			<div class="modal-body" style="padding: 5px 20px;">
				<form id="manage-analysis-impact-scale" action="/Analysis/Prameter/Scale-level/Manage/Save?${_csrf.parameterName}=${_csrf.token}" method="post" class="form-horizontal">
					<fieldset class='col-xs-6'>
						<legend><spring:message code='label.scale.level.current' text="Current levels" /></legend>
						<div class='list-group' style="height: 500px">
							<c:forEach begin="1" end="${maxLevel}" var="level">
								<div class='list-group-item' data-value='${level}' draggable="true"><spring:message code='label.scale.level.value' arguments="${level}" text="Level ${level}"/></div>
							</c:forEach>
						</div>
					</fieldset>
					<fieldset class='col-xs-6'>
						<legend><spring:message code='label.scale.level.new' text="New levels" /></legend>
						<div style="height: 500px" dropzone="true"></div>
					</fieldset>
				</form>
				<div class='clearfix'></div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" name='save'>
					<spring:message code="label.action.save" text="Save" />
				</button>
				<button type="button" class="btn btn-default" data-dismiss="modal">
					<spring:message code="label.action.cancel" text="Cancel" />
				</button>
			</div>

		</div>
		<!-- /.modal-content -->
		<div class="hidden">
			<code data-lang-code='error.manage.impact.empty'>
				<spring:message code="error.manage.impact.empty" />
			</code>
			<code data-lang-code='info.manage.impact.remove'>
				<spring:message code="info.manage.impact.remove" />
			</code>
		</div>
	</div>
	<!-- /.modal-dialog -->
</div>
<!-- /.modal -->