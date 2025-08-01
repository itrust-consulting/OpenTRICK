<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<div class="modal fade" id="formScaleTypeModal" tabindex="-1" role="dialog" data-aria-labelledby="formTypeScaleModal" data-aria-hidden="true" data-backdrop="static" data-keyboard="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header bg-primary">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title">
					<spring:message code="label.title.scale_type.${empty scaleType? 'add':'edit'}" text="${empty scaleType? 'Add new impact':'Edit impact type'}" />
				</h4>
			</div>
			<div class="modal-body">
				<form name="scale" action="${pageContext.request.contextPath}/KnowledgeBase/ScaleType/Save" class="form-horizontal" id="scale_type_form" style="height: 400px;">
					<c:choose>
						<c:when test="${empty scaleType}">
							<input name="id" value="0" type="hidden">
						</c:when>
						<c:otherwise>
							<input name="id" value="${scaleType.id}" type="hidden">
						</c:otherwise>
					</c:choose>

					<div class="form-group">
						<label for="name" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.scale_type.name" />' > <spring:message code="label.scale.name" text="Name" />
						</label>
						<div class="col-sm-9">
							<input name="name" id="scale_type_name" style="text-transform:uppercase" class="form-control" value="${empty scaleType? '': scaleType.name }" required="required" ${empty scaleType? '': 'readonly="readonly" '} />
						</div>
					</div>

					<div class="form-group">
						<label for="acronym" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.scale_type.acronym" />' > <spring:message code="label.scale.acronym" text="Acronym" />
						</label>
						<div class="col-sm-9">
							<input name="acronym" id="scale_type_acronym" class="form-control" value="${empty scaleType? '': scaleType.acronym}" required="required" ${empty scaleType? '': 'readonly="readonly" '} />
						</div>
					</div>
					
					<fieldset style="height: 330px; overflow-y: auto; overflow-x: hidden;">
						<legend data-helper-content='<spring:message code="help.scale_type.translations" />'>
							<spring:message code="label.scale.translations" text="Translations" />
						</legend>
						<c:forEach items="${languages}" var="language">
							<c:set var="alpha2" value="${language.alpha2}" />
							<div class="form-group">
								<label for="translations['${alpha2}']" class="col-sm-3 control-label"> <spring:message text="${locale == fn:toLowerCase(alpha2)? language.name : language.altName}" />
								</label>
								<div class="col-sm-5">
									<input name="translations['${alpha2}'].name" id="scale_type_translations_${alpha2}_name" class="form-control" value="${empty scaleType? '': scaleType.translations[alpha2].name}" required="required" placeholder='<spring:message code='label.translation'/>' />
								</div>
								<div class="col-sm-4">
									<input name="translations['${alpha2}'].shortName" id="scale_type_translations_${alpha2}_shortName" class="form-control" value="${empty scaleType? '': scaleType.translations[alpha2].shortName}" placeholder='<spring:message code='label.translation.short'/>' required="required" />
								</div>
							</div>

						</c:forEach>
					</fieldset>
					<button type="submit" hidden="hidden" id="scale_type_form_submit_button"></button>
				</form>
				<div id="scale-type-error-container"></div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" id="scale_type_submit_button">
					<spring:message code="label.action.save" />
				</button>
				<button type="button" class="btn btn-default" data-dismiss="modal">
					<spring:message code="label.action.cancel" text="Cancel" />
				</button>
			</div>
		</div>
	</div>
</div>