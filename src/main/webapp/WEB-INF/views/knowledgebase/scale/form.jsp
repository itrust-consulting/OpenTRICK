<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="modal fade" id="formScaleModal" tabindex="-1" role="dialog" data-aria-labelledby="formScaleModal" data-aria-hidden="true" data-backdrop="static" data-keyboard="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title">
					<spring:message code="label.title.scale.${empty scale? 'add':'edit'}" text="${empty scale? 'Add new impact':'Edit impact'}" />
				</h4>
			</div>
			<div class="modal-body">
				<form name="scale" action="${pageContext.request.contextPath}/KnowledgeBase/Scale/Save" class="form-horizontal" id="scale_form" style="height: 360px;">
					<c:choose>
						<c:when test="${empty scale}">
							<input name="id" value="0" type="hidden">
						</c:when>
						<c:otherwise>
							<input name="id" value="${scale.id}" type="hidden">
						</c:otherwise>
					</c:choose>

					<c:choose>
						<c:when test="${empty scale}">
							<c:set var="alpha2" value="${fn:toUpperCase(locale) }" />
							<fieldset>
								<legend>
									<spring:message code="label.scale.type" text="Type" />
								</legend>
								<div class="form-group">
									<div class="col-sm-12">
										<select name='type.id' id="scale_type_id" class="form-control">
											<option value="-1"><spring:message code="label.action.choose" /></option>
											<c:forEach items="${scaleTypes}" var="type">
												<option value="${type.id}">
													<spring:message text="${empty type.translations[alpha2]? type.name : type.translations[alpha2] }" />
												</option>
											</c:forEach>
										</select>
									</div>
								</div>
								<div class="form-group">
									<label for="name" class="col-sm-2 control-label"> <spring:message code="label.scale.name" text="Name" />
									</label>
									<div class="col-sm-10">
										<input name="name" id="scale_type_name" class="form-control" required="required" readonly="readonly" />
									</div>
								</div>
								<div class="form-group">
									<label for="type.acronym" class="col-sm-2 control-label"> <spring:message code="label.scale.acronym" text="Acronym" />
									</label>
									<div class="col-sm-10">
										<input name="type.acronym" id="scale_type_acronym" class="form-control" required="required" readonly="readonly" />
									</div>
								</div>
							</fieldset>
						</c:when>
						<c:otherwise>
							<div class="form-group">
								<label for="name" class="col-sm-2 control-label"> <spring:message code="label.scale.name" text="Name" />
								</label>
								<div class="col-sm-10">
									<input name="name" id="scale_type_name" class="form-control" value="${scale.type.name}" required="required" readonly="readonly" />
								</div>
							</div>
							<div class="form-group">
								<label for="type.acronym" class="col-sm-2 control-label"> <spring:message code="label.scale.acronym" text="Acronym" />
								</label>
								<div class="col-sm-10">
									<input name="type.acronym" id="scale_type_acronym" class="form-control" value="${scale.type.acronym}" required="required" readonly="readonly" />
								</div>
							</div>
						</c:otherwise>
					</c:choose>
					<fieldset>
						<legend><spring:message code="label.scale" text="Scale" /></legend>
						<div class="form-group">
							<label for="level" class="col-sm-2 control-label"> <spring:message code="label.scale.level" text="Level" />
							</label>
							<div class="col-sm-10">
								<input name="level" id="scale_level" class="form-control" value="${empty scale? '3': scale.level}" type="number" min="3" required="required" />
							</div>
						</div>

						<div class="form-group">
							<label for="maxValue" class="col-sm-2 control-label"> <spring:message code="label.scale.max_value" text="Max value" />
							</label>
							<div class="col-sm-10">
								<div class="input-group">
									<c:choose>
										<c:when test="${empty(scale)}">
											<input name="maxValue" id="scale_maxValue" class="form-control" type="number" min="1" required="required">
										</c:when>
										<c:otherwise>
											<input name="maxValue" id="scale_maxValue" class="form-control" value='<fmt:formatNumber value="${scale.maxValue*0.001}" maxFractionDigits="1" />' type="number" min="1"
												required="required">
										</c:otherwise>
									</c:choose>
									<span class="input-group-addon">k&euro;</span>
								</div>
							</div>
						</div>
					</fieldset>
					<button type="submit" hidden="hidden" id="scale_form_submit_button"></button>
				</form>
				<div id="scale-error-container"></div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" id="scale_submit_button">
					<spring:message code="label.action.save" />
				</button>
				<button type="button" class="btn btn-default" data-dismiss="modal">
					<spring:message code="label.action.cancel" text="Cancel" />
				</button>
			</div>
		</div>
	</div>
</div>