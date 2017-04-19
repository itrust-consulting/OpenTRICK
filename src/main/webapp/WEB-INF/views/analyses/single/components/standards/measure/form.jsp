<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="modal fade" id="modalMeasureForm" tabindex="-1" role="dialog" data-aria-labelledby="measureForm" style="z-index: 1042" data-aria-hidden="true">
	<div class="modal-dialog" style="min-width: 50%;">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title">
					<c:choose>
						<c:when test="${measureForm.id<1}">
							<spring:message code="label.tile.add.measure" />
						</c:when>
						<c:otherwise>
							<spring:message code="label.tile.edit.measure" />
						</c:otherwise>
					</c:choose>

				</h4>
			</div>
			<div class="modal-body" style="padding-top: 5px;">
				<div id="measure-form-container">
					<c:choose>
						<c:when test="${type=='QUALITATIVE'}">
							<c:choose>
								<c:when test="${isAnalysisOnly and measureForm.type == 'ASSET' }">
									<ul id="measure_form_tabs" class="nav nav-tabs">
										<li class="active"><a href="#tab_general" data-toggle="tab"><spring:message code="label.menu.general" text="General" /></a></li>
										<li><a href="#tab_asset" data-toggle="tab"><spring:message code="label.menu.assets" text="Assets" /></a></li>
										<li id="error_container" style="padding-top: 10px"></li>
									</ul>
								</c:when>
								<c:otherwise>
									<div id="error_container"></div>
								</c:otherwise>
							</c:choose>
						</c:when>
						<c:otherwise>
							<c:choose>
								<c:when test="${isAnalysisOnly}">
									<ul id="measure_form_tabs" class="nav nav-tabs">
										<li class="active"><a href="#tab_general" data-toggle="tab"><spring:message code="label.menu.general" text="General" /></a></li>
										<c:if test="${measureForm.type == 'ASSET' }">
											<li><a href="#tab_asset" data-toggle="tab"><spring:message code="label.menu.assets" text="Assets" /></a></li>
										</c:if>
										<li><a href="#tab_properties" data-toggle="tab"><spring:message code="label.menu.properties" text="Properties" /></a></li>
										<li id="error_container" style="padding-top: 10px"></li>
									</ul>
								</c:when>
								<c:otherwise>
									<div id="error_container"></div>
								</c:otherwise>
							</c:choose>
						</c:otherwise>
					</c:choose>

					<form name="measureForm" style="height: 478px;" action="/Save?${_csrf.parameterName}=${_csrf.token}" class="form-horizontal tab-content" id="measure_form" method="post">
						<input type="hidden" name="id" value="${measureForm.id}" id="id"> <input type="hidden" name="idStandard" value="${measureForm.idStandard}" id="idStandard">
						<c:if test="${isAnalysisOnly}">
							<div id="tab_general" class="tab-pane active" style="padding-top: 17px;">

								<div class="form-group">
									<label for="reference" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.measure.reference" />' > <spring:message code="label.reference" /></label>
									<div class="col-sm-9">
										<input name="reference" id="reference" value='<spring:message text="${measureForm.reference}"/>' class="form-control" />
									</div>
								</div>

								<div class="form-group">
									<label for="level" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.measure.level" />' > <spring:message code="label.measure.level" /></label>
									<div class="col-sm-9">
										<input name="level" id="level" value="${measureForm.level}" class="form-control" type="number" min="1" />
									</div>
								</div>

								<div class="form-group">
									<label for="computable" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.measure.computable" />' > <spring:message code="label.measure.computable" /></label>
									<div class="col-sm-9" align="center">
										<input name="computable" id="computable" ${measureForm.computable?'checked':''} class="checkbox" type="checkbox" />
									</div>
								</div>

								<div class="form-group">
									<label for="domain" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.measure.domain" />' > <spring:message code="label.measure.domain" /></label>
									<div class="col-sm-9">
										<textarea name="domain" id="domain" rows="4" class="form-control resize_vectical_only"><spring:message text="${measureForm.domain}" /></textarea>
									</div>
								</div>

								<div class="form-group">
									<label for="description" class="col-sm-3 control-label" data-helper-content='<spring:message code="help.measure.description" />' ><spring:message code="label.measure.description" /></label>
									<div class="col-sm-9">
										<textarea name="description" id="description" rows="9" class="form-control resize_vectical_only"><spring:message text="${measureForm.description}" /></textarea>
									</div>
								</div>
							</div>
							<c:if test="${measureForm.type == 'ASSET' }">
								<div id="tab_asset" class="tab-pane">
									<div class="row">
										<div class="col-sm-12">
											<h3>
												<spring:message code="label.assetmeasure.assets.title" />
											</h3>
											<p class="bordered-bottom" style="padding-bottom: 10px;">
												<spring:message code="label.assetmeasure.assets.description" />
											</p>

										</div>
										<div class="form-group" style="width: 47%; margin: 5px 15px;">
											<label class="col-xs-3" style="padding: 5px;"><spring:message code="label.asset_type" /></label>
											<div class="col-xs-9">
												<select class="form-control" name="assettypes" id="assettypes">
													<option value="ALL"><spring:message code="label.all" text="All" /></option>
													<c:forEach items="${assetTypes}" var="assetType">
														<option value="${assetType.name}" ${assetType.name.equals(selectedAssetType)?"selected='selected'":"" }><spring:message code="label.asset_type.${fn:toLowerCase(assetType.name)}" /></option>
													</c:forEach>
												</select>
											</div>
										</div>
									</div>
									<div class="row">
										<div class="col-sm-6">
											<h3>
												<spring:message code="label.assetmeasure.assetlist" />
											</h3>
											<p>
												<spring:message code="label.assetmeasure.assets.clickforselect" />
											</p>

											<ul class="asset-measure" data-trick-type="available">
												<c:forEach items="${availableAssets}" var="availableAsset">
													<li data-trick-id="${availableAsset.id}" class="list-group-item" data-trick-selected="false" data-trick-type="${availableAsset.assetType.name}"><spring:message
															text="${availableAsset.name}" /></li>
												</c:forEach>
											</ul>
										</div>
										<div class="col-sm-6">
											<h3>
												<spring:message code="label.assetmeasure.selected" />
											</h3>
											<p>
												<spring:message code="label.assetmeasure.assets.clickfordeselect" />
											</p>
											<ul class="asset-measure" data-trick-type="measure">
												<c:forEach items="${measureForm.assetValues}" var="assetValue">
													<li data-trick-id="${assetValue.id}" data-trick-selected="true" class="list-group-item" data-trick-type="${assetValue.type}"><spring:message
															text="${assetValue.name}" /><input name="assets" value="${assetValue.id}" hidden="hidden"></li>
												</c:forEach>
											</ul>
										</div>
									</div>
								</div>
							</c:if>
						</c:if>
						<c:if test="${type=='QUANTITATIVE'}">
							<jsp:include page="properties.jsp" />
						</c:if>
					</form>
					<div class="clearfix"></div>
				</div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" onclick="return saveMeasure('#measure_form')">
					<spring:message code="label.action.save" text="Save" />
				</button>
				<button type="button" class="btn btn-default" data-dismiss="modal">
					<spring:message code="label.action.cancel" text="Cancel" />
				</button>
			</div>
		</div>
	</div>
</div>
