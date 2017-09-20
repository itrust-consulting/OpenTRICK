<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<spring:message code="label.drop.level" text="Drop your level here" var="dropMessage" />
<div class="modal fade" id="manageScaleLevelModal" tabindex="-1" role="dialog" data-aria-labelledby="manageScaleLevelModal" data-aria-hidden="true" data-backdrop="static"
	data-keyboard="true">
	<div class="modal-dialog modal-md">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title" data-helper-content='<spring:message code="helper.scale.level.migrate" />' data-helper-placement='bottom' >
					<spring:message code="label.title.manage.analysis.scale_level" text="Manage analysis scale level" />
				</h4>
			</div>
			<div class="modal-body" style="padding: 5px 20px;">
				<div class='alert alert-sm alert-danger' style="margin-top: 0px"> <spring:message code='warning.scale.level.migrate' /></div>
				<form id="manage-analysis-impact-scale" action="/Analysis/Prameter/Scale-level/Manage/Save?${_csrf.parameterName}=${_csrf.token}" method="post" class="form-horizontal">
					<fieldset class='col-xs-6'>
						<legend>
							<spring:message code='label.scale.level.current' text="Current levels" />
						</legend>
						<div id="original-container" class='list-group' style="height: 500px; overflow-x: auto;">
							<c:forEach begin="1" end="${maxLevel}" var="level">
								<div id="scale-level-${level}" class='list-group-item list-group-item-info' data-value='${level}' draggable="true">
									<spring:message code='label.scale.level.value' arguments="${level}" text="Level ${level}" />
								</div>
							</c:forEach>
						</div>
					</fieldset>
					<fieldset class='col-xs-6'>
						<legend>
							<spring:message code='label.scale.level.new' text="New levels" />
							<button class='btn btn-xs btn-primary pull-right' type="button" id='btn-add-level'>
								<i class='fa fa-plus'></i>
							</button>
						</legend>
						<div id='new-level-container' style="height: 500px; overflow-x: auto; padding-right: 5px;">
							<div class='panel panel-success' data-container-level='0'>
								<div class='panel-heading'>
									<spring:message code='label.scale.level.na' text="Level 0: Not Applicable" />
								</div>
								<div class='panel-body list-group' data-level-value='0' dropzone="true">
									<div id="scale-level-0" class='list-group-item list-group-item-info' data-value='0'>
										<spring:message code='label.scale.level.na' text="Level 0: Not Applicable" />
									</div>
								</div>
							</div>
							<c:forEach begin="1" end="${maxLevel}" var="level">
								<div class='panel panel-success' data-container-level='${level}'>
									<div class='panel-heading'>
										<span class='panel-title'><spring:message code='label.scale.level.value' arguments="${level}" text="Level ${level}" /></span><a href="#" data-role='remove'
											class='text-danger pull-right' style="font-size: 18px;"><span class='glyphicon glyphicon-remove-circle'></span></a>
									</div>
									<div class='panel-body list-group'>
										<span>${dropMessage}</span>
									</div>
								</div>
							</c:forEach>
						</div>
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


			<code data-lang-code='error.scale.level.not.all.selected'>
				<spring:message code='error.scale.level.not.all.selected' text="All current levels must have a match" />
			</code>
			<code data-lang-code='label.scale.level.value'>
				<spring:message code='label.scale.level.value' text="Level {0}" />
			</code>
			<code data-lang-code='label.drop.level'>${dropMessage}</code>
			<div id='level-template-ui' class='panel panel-success' data-container-level=''>
				<div class='panel-heading'>
					<span class='panel-title'>x</span><a href="#" data-role='remove' class='text-danger pull-right' style="font-size: 18px;"><span class='glyphicon glyphicon-remove-circle'></span></a>
				</div>
				<div class='panel-body list-group'>
					<span>${dropMessage}</span>
				</div>
			</div>
		</div>
	</div>
	<!-- /.modal-dialog -->
</div>
<!-- /.modal -->