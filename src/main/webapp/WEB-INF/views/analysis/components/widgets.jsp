<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<div id="widget">
	<!-- Modal -->
	<div id="asset_contextMenu" class="dropdown clearfix"
		style="position: absolute; display: none;" trick-selected-id="-1">
		<ul class="dropdown-menu" role="menu" aria-labelledby="dropdownMenu"
			style="display: block; position: static; margin-bottom: 5px;">
			<li name="select"><a tabindex="-1" href="#" onclick="return selectAsset($('#asset_contextMenu').attr('trick-selected-id'),true);"><spring:message
						code="label.action.select" text="Select" /></a></li>
			<li name="unselect"><a tabindex="-1" href="#" onclick="return selectAsset($('#asset_contextMenu').attr('trick-selected-id'),false);"><spring:message
						code="label.action.un_select" text="Unselect" /></a></li>
			<li name="edit_row"><a tabindex="-1" href="#" onclick="return editAssetRow($('#asset_contextMenu').attr('trick-selected-id'));"><spring:message
						code="label.action.edit_row" text="Edit row" /></a></li>
			<li class="divider"></li>
			<li name="delete"><a tabindex="-1" href="#" onclick="return deleteAsset($('#asset_contextMenu').attr('trick-selected-id'));"><spring:message
						code="label.action.delete" text="Delete" /></a></li>
		</ul>
	</div>
	<div class="modal fade" id="addAssetModel" tabindex="-1" role="dialog"
		aria-labelledby="addNewAsset" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-hidden="true">&times;</button>
					<h4 class="modal-title" id="addAssetModel-title">
						<spring:message code="label.asset.add" text="Add new asset" />
					</h4>
				</div>
				<div class="modal-body">
					<form name="asset" action="Asset/Save" class="form-horizontal"
						id="asset_form">
						<input type="hidden" name="id" value="-1" id="asset_id">
						<div class="form-group">
							<label for="name" class="col-sm-2 control-label"> <spring:message
									code="label.asset.name" text="Name" />
							</label>
							<div class="col-sm-10">
								<input name="name" id="asset_name" class="form-control" />
							</div>
						</div>
						<div class="form-group">
							<label for="assetType.id" class="col-sm-2 control-label">
								<spring:message code="label.asset.type" text="Type" />
							</label>
							<div class="col-sm-10">
								<select name="assetType" class="form-control"
									id="asset_assettype_id">
									<option value='-1'><spring:message
											code="label.asset.type.loading" text="Loading..." /></option>
								</select>
							</div>
						</div>
						<div class="form-group">
							<label for="value" class="col-sm-2 control-label"> <spring:message
									code="label.asset.value" text="Value" />
							</label>
							<div class="col-sm-10">
								<div class="input-group">
									<input name="value"
										id="asset_value" class="form-control" type="number" />
										<span class="input-group-addon">k&euro;</span> 
								</div>
							</div>
						</div>
						<div class="form-group">
							<label for="selected" class="col-sm-2 control-label"> <spring:message
									code="label.asset.selected" text="Selected" />
							</label>
							<div class="col-sm-10">
								<input name="selected" id="asset_selected" class="form-control"
									type="checkbox" />
							</div>
						</div>
						<div class="form-group">
							<label for="comment" class="col-sm-2 control-label"> <spring:message
									code="label.asset.comment" text="Comment" />
							</label>
							<div class="col-sm-10">
								<textarea name="comment" class="form-control" id="asset_comment"></textarea>
							</div>
						</div>
						<div class="form-group">
							<label for="hiddenComment" class="col-sm-2 control-label">
								<spring:message code="label.asset.comment_hidden"
									text="Comment hidden" />
							</label>
							<div class="col-sm-10">
								<textarea name="hiddenComment" id="asset_hiddenComment"
									class="form-control"></textarea>
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
</div>