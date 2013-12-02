<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<div class="modal fade" id="addAssetModel" tabindex="-1" role="dialog"
	aria-labelledby="addNewAsset" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="addAssetModel-title">
					<spring:message code="label.asset.${empty(asset)? 'add':'edit'}"
						text="${empty(asset)? 'Add new asset':'Edit asset'}" />
				</h4>
			</div>
			<div class="modal-body">
				<form name="asset"
					action="${pageContext.request.contextPath}/Asset/Save"
					class="form-horizontal" id="asset_form">
					<c:choose>
						<c:when test="${!empty(asset)}">
							<input name="id" value="${asset.id}" type="hidden">
						</c:when>
						<c:otherwise>
							<input name="id" value="-1" type="hidden">
						</c:otherwise>
					</c:choose>
					<div class="form-group">
						<label for="name" class="col-sm-2 control-label"> <spring:message
								code="label.asset.name" text="Name" />
						</label>
						<div class="col-sm-10">
							<input name="name" id="asset_name" class="form-control"
								value="${empty(asset)? '':asset.name}" />
						</div>
					</div>
					<div class="form-group">
						<label for="assetType.id" class="col-sm-2 control-label">
							<spring:message code="label.asset.type" text="Type" />
						</label>
						<div class="col-sm-10">
							<select name="assetType" class="form-control"
								id="asset_assettype_id">
								<c:choose>
									<c:when test="${!empty(assettypes)}">
										<option value='-1'><spring:message
												code="label.asset.type.select"
												text="Select the type of asset" /></option>
										<c:forEach items="${assettypes}" var="assettype">
											<option value="${assettype.id}"
												${asset.assetType == assettype?'selected':''}>${assettype.type}</option>
										</c:forEach>
									</c:when>
									<c:otherwise>
										<option value='-1'><spring:message
												code="label.asset.type.loading" text="Loading..." /></option>
									</c:otherwise>
								</c:choose>
							</select>
						</div>
					</div>
					<div class="form-group">
						<label for="value" class="col-sm-2 control-label"> <spring:message
								code="label.asset.value" text="Value" />
						</label>
						<div class="col-sm-10">
							<div class="input-group">
								<input name="value" id="asset_value" class="form-control"
									type="number" value="${empty(asset)? '0.0':asset.value}" /> <span
									class="input-group-addon">k&euro;</span>
							</div>
						</div>
					</div>
					<div class="form-group">
						<label for="selected" class="col-sm-2 control-label"> <spring:message
								code="label.asset.selected" text="Selected" />
						</label>
						<div class="col-sm-10">
							<input name="selected" id="asset_selected" class="form-control"
								type="checkbox"
								${empty(asset)? '': asset.selected? 'checked' : ''} />
						</div>
					</div>
					<div class="form-group">
						<label for="comment" class="col-sm-2 control-label"> <spring:message
								code="label.asset.comment" text="Comment" />
						</label>
						<div class="col-sm-10">
							<textarea name="comment" class="form-control" id="asset_comment">${empty(asset)? '': asset.comment}</textarea>
						</div>
					</div>
					<div class="form-group">
						<label for="hiddenComment" class="col-sm-2 control-label">
							<spring:message code="label.asset.comment_hidden"
								text="Comment hidden" />
						</label>
						<div class="col-sm-10">
							<textarea name="hiddenComment" id="asset_hiddenComment"
								class="form-control">${empty(asset)? '': asset.hiddenComment}</textarea>
						</div>
					</div>
				</form>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary"
					onclick="saveAsset('asset_form')">
					<spring:message code="label.action.save" text="Save" />
				</button>
			</div>
		</div>
		<!-- /.modal-content -->
	</div>
	<!-- /.modal-dialog -->
</div>
<!-- /.modal -->