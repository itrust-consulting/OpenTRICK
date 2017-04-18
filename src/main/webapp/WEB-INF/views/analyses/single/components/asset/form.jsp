<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<fmt:setLocale value="fr" scope="session" />
<div class="modal fade" id="addAssetModal" tabindex="-1" role="dialog" data-aria-labelledby="addNewAsset" data-aria-hidden="true" data-backdrop="static" data-keyboard="true">
	<div class="modal-dialog modal-lg">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="addAssetModel-title">
					<spring:message code="label.title.asset.${empty(asset)? 'add':'edit'}" />
				</h4>
			</div>
			<div class="modal-body">
				<form name="asset" action="${pageContext.request.contextPath}/Asset/Save" class="form-horizontal" id="asset_form">
					<c:choose>
						<c:when test="${!empty(asset)}">
							<input name="id" value="${asset.id}" type="hidden">
						</c:when>
						<c:otherwise>
							<input name="id" value="-1" type="hidden">
						</c:otherwise>
					</c:choose>
					<div class="form-group">
						<label for="name" class="col-sm-2 control-label" data-helper-content='<spring:message code="help.asset.name" />' > <spring:message code="label.asset.name" />
						</label>
						<div class="col-sm-10">
							<input name="name" id="asset_name" class="form-control" value='<spring:message text="${empty(asset)? '':asset.name}" />' />
						</div>
					</div>
					<div class="form-group">
						<label for="assetType.id" class="col-sm-2 control-label" data-helper-content='<spring:message code="help.asset.type" />' > <spring:message code="label.asset.type" />
						</label>
						<div class="col-sm-10">
							<select name="assetType" class="form-control" id="asset_assettype_id">
								<c:choose>
									<c:when test="${!empty(assettypes)}">
										<option value='-1'><spring:message code="label.asset.type.select" /></option>
										<c:forEach items="${assettypes}" var="assettype">
											<option value="${assettype.id}" ${asset.assetType == assettype?'selected':''}><spring:message code="label.asset_type.${fn:toLowerCase(assettype.name)}" /></option>
										</c:forEach>
									</c:when>
									<c:otherwise>
										<option value='-1'><spring:message code="label.asset.type.loading" /></option>
									</c:otherwise>
								</c:choose>
							</select>
						</div>
					</div>
					<div class="form-group">
						<label for="value" class="col-sm-2 control-label" data-helper-content='<spring:message code="help.asset.value" />' > <spring:message code="label.form.asset.value" />
						</label>
						<div class="col-sm-10">
							<div class="input-group">
								<c:choose>
									<c:when test="${empty(asset)}">
										<input name="value" id="asset_value" class="form-control" value="0">
									</c:when>
									<c:otherwise>
										<input name="value" id="asset_value" class="form-control" value='<fmt:formatNumber value="${asset.value*0.001}" maxFractionDigits="1" />'>
									</c:otherwise>
								</c:choose>
								<span class="input-group-addon">k&euro;</span>
							</div>
						</div>
					</div>
					<div class="form-group">
						<label for="selected" class="col-sm-2 control-label" data-helper-content='<spring:message code="help.asset.status" />' > <spring:message code="label.status" />
						</label>
						<div class="col-sm-10" align="center">
							<div class="btn-group" data-toggle="buttons">
								<label class="btn btn-default ${empty(asset) or asset.selected ? 'active' : ''}"><spring:message code="label.action.select" /><input
									${empty(asset) or asset.selected ? 'checked' : ''} name=selected type="radio" value="true"></label> <label
									class="btn btn-default ${empty(asset) or asset.selected ? '' : 'active'} "><spring:message code="label.action.unselect" /><input
									${empty(asset) or asset.selected ? '' : 'checked'} name="selected" type="radio" value="false"></label>
							</div>
						</div>
					</div>
					<div class="form-group">
						<label for="comment" class="col-sm-2 control-label" data-helper-content='<spring:message code="help.asset.comment" />' > <spring:message code="label.asset.comment" />
						</label>
						<div class="col-sm-10">
							<textarea name="comment" class="form-control resize_vectical_only" id="asset_comment" rows="10" ><spring:message text="${empty(asset)? '': asset.comment}" /></textarea>
						</div>
					</div>
					<div class="form-group">
						<label for="hiddenComment" class="col-sm-2 control-label" data-helper-content='<spring:message code="help.asset.comment_hidden" />' > <spring:message code="label.asset.comment_hidden" />
						</label>
						<div class="col-sm-10">
							<textarea name="hiddenComment" id="asset_hiddenComment" class="form-control resize_vectical_only" rows="8"><spring:message text="${empty(asset)? '': asset.hiddenComment}" /></textarea>
						</div>
					</div>
				</form>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" onclick="saveAsset('asset_form')">
					<spring:message code="label.action.save" />
				</button>
				<button type="button" class="btn btn-default" data-dismiss="modal">
					<spring:message code="label.action.cancel" text="Cancel" />
				</button>
			</div>
		</div>
	</div>
</div>