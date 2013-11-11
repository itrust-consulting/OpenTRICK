<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<div class="section">
	<div class="page-header">
		<h3 id="asset">
			<spring:message code="label.asset" text="Asset" />
		</h3>
		<button class="btn btn-primary" data-toggle="modal" data-target="#addAssetModel">
			<spring:message code="label.asset.add" text="Add new asset"/>
		</button>
	</div>
	
</div>
<!-- Modal -->
<div class="modal fade" id="addAssetModel" tabindex="-1" role="dialog" aria-labelledby="addNewAsset" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h4 class="modal-title" id="myModalLabel"><spring:message code="label.asset.add" text="Add new asset"/></h4>
      </div>
      <div class="modal-body">
      		<form name="asset" action="Asset/Save" class="form-horizontal">
      			<div class="form-group">
					<label for="Label" class="col-sm-2 control-label">
						<spring:message code="label.asset.label" text="Label"/>
					</label>
					<div class="col-sm-10">
						<input name="label" class="form-control" />
					</div>
				</div>
				<div class="form-group">
					<label for="Type" class="col-sm-2 control-label">
						<spring:message code="label.asset.type" text="Type"/>
					</label>
					<div class="col-sm-10">
						<select name="type" class="form-control">
							<option value=''><spring:message code="label.asset.type.default" text="Select asset type"/></option>
							<option value='Serv'><spring:message code="label.asset.type.serv" text="Serv"/></option>
							<option value='Inf'><spring:message code="label.asset.type.inf" text="Info"/></option>
							<option value='Pers'><spring:message code="label.asset.type.pers" text="Pers"/></option>
						</select>
					</div>
				</div>
      		</form>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
        <button type="button" class="btn btn-primary">Save changes</button>
      </div>
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div><!-- /.modal -->