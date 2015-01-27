<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<form name="manageAssetMeasure" action="/Save" class="form-horizontal" id="manageAssetMeasure_form" method="post">
	<c:if test="${!empty(error)}">
		<div class="alert alert-danger" role="alert">
			<spring:message text="${error}" />
		</div>
	</c:if>
	<c:if test="${empty(error)}">
		<input type="hidden" name="idMeasure" value="${idMeasure}" id="measure_id">
		<input type="hidden" name="idStandard" value="${idStandard}" id="standard_id">
		<div class="row">
			<div id="group_1" class="tab-pane active" style="padding-top: 10px;">
				<div class="form-group">
					<label for="reference" class="col-sm-2 control-label"> <fmt:message key="label.measure.reference" /></label>
					<div class="col-sm-5">
						<input name="reference" id="measure_reference" value="${desc.reference}" class="form-control" type="text" />
					</div>
				</div>
				<div class="form-group">
					<label for="level" class="col-sm-2 control-label"> <fmt:message key="label.measure.level" /></label>
					<div class="col-sm-5">
						<input name="level" id="measure_level" value="${desc.level}" class="form-control" type="text" />
					</div>
				</div>
				<div class="form-group">
					<label for="computable" class="col-sm-2 control-label"> <fmt:message key="label.measure.computable" /></label>
					<div class="col-sm-5">
						<input name="computable" id="measure_computable" ${!empty(desc)?desc.computable?'checked':'':''} class="form-control" type="checkbox" />
					</div>
				</div>
				<div class="form-group">
					<label for="domain" class="col-sm-2 control-label"> <fmt:message key="label.measure.domain" /></label>
					<div class="col-sm-5">
						<input name="domain" id="measure_domain" value="${desctext.domain}" class="form-control" type="text" />
					</div>
				</div>
				<div class="form-group" style="margin-bottom: 0;">
					<label for="description" class="col-sm-2 control-label"><fmt:message key="label.measure.description" /></label>
					<div class="col-sm-5">
						<textarea name="description" id="measure_description" class="form-control resize_vectical_only">${desctext.description}</textarea>
					</div>
				</div>
			</div>
			<div id="group_2" style="padding-top: 10px; display: none;">
				<div class="col-sm-12">
					<h3>
						<fmt:message key="label.assetmeasure.assets.title" />
					</h3>
					<p>
						<fmt:message key="label.assetmeasure.assets.description" />
					</p>
					<div style="width: 50%; margin-left: auto; margin-right: auto;">
						Filter(for both lists): <select class="form-control" name="assettypes" id="assettypes">
							<c:forEach items="${assetTypes}" var="assetType">
								<option data-trick-type="${assetType.type}" ${assetType.type.equals(selectedAssetType)?"selected='selected'":"" }><spring:message text="${assetType.type}" /></option>
							</c:forEach>
						</select>
					</div>
				</div>
				<div class="col-sm-6">
					<h3>
						<fmt:message key="label.assetmeasure.assetlist" />
					</h3>
					<p>
						<fmt:message key="label.assetmeasure.assets.clickforselect" />
					</p>
					<select class="form-control" name="availableAssets" id="availableAssets" style="display: none;">
						<c:forEach items="${availableAssets}" var="availableAsset">
							<option value="asset_${availableAsset.id}"><spring:message text="${availableAsset.name}" /></option>
						</c:forEach>
					</select>
					<ul style="padding: 0; margin: 0; max-height: 300px; overflow: scroll;" data-trick-type="available">
						<c:forEach items="${availableAssets}" var="availableAsset">
							<li style="cursor: pointer;${availableAsset.assetType.type.equals(selectedAssetType)?'display:block;':'display:none;' }" opt="asset_${availableAsset.id}"
								class="list-group-item" data-trick-type="${availableAsset.assetType.type}"><spring:message text="${availableAsset.name}" /></li>
						</c:forEach>
					</ul>
				</div>
				<div class="col-sm-6">
					<h3>
						<fmt:message key="label.assetmeasure.selected" />
					</h3>
					<p>
						<fmt:message key="label.assetmeasure.assets.clickfordeselect" />
					</p>
					<select class="form-control" name="measureAssets" id="measureAssets" style="display: none;">
						<c:forEach items="${measureAssets}" var="measureAsset">
							<option value="asset_${measureAsset.id}"><spring:message text="${measureAsset.name}" /></option>
						</c:forEach>
					</select>
					<ul style="padding: 0; margin: 0;" data-trick-type="measure">
						<c:forEach items="${measureAssets}" var="measureAsset">
							<li style="cursor: pointer;${measureAsset.assetType.type.equals(selectedAssetType)?'display:block;':'display:none;' }" opt="asset_${measureAsset.id}" class="list-group-item"
								data-trick-type="${measureAsset.assetType.type}"><spring:message text="${measureAsset.name}" /></li>
						</c:forEach>
					</ul>
				</div>
			</div>
			<div id="group_3" style="padding-top: 10px; display: none;">
				<spring:message text="${typeValue?'success':'danger'}" var="cssclass" />
				<div class="panel panel-primary" style="margin-bottom: 0;">
					<div class="panel-body">
						<div style="overflow: auto;">
							<table class="table" style="margin-bottom: 0;">
								<thead>
									<tr id="tableheaderrow">
										<th class="warning"><fmt:message key="label.rrf.measure.strength_measure" /></th>
										<th class="warning"><fmt:message key="label.rrf.measure.strength_sectoral" /></th>
										<c:if test="${!empty(categories)}">
											<c:forEach items="${categories.keySet()}" var="category">
												<th class="info" data-trick-class="Category" data-trick-value=<spring:message text="${category}" />><fmt:message
														key="label.rrf.category.${fn:toLowerCase(fn:replace(category,'_','.'))}" /></th>
											</c:forEach>
										</c:if>
										<th class="success"><fmt:message key="label.rrf.measure.preventive" /></th>
										<th class="success"><fmt:message key="label.rrf.measure.detective" /></th>
										<th class="success"><fmt:message key="label.rrf.measure.limitative" /></th>
										<th class="success"><fmt:message key="label.rrf.measure.corrective" /></th>
										<th class="warning"><fmt:message key="label.rrf.measure.intentional" /></th>
										<th class="warning"><fmt:message key="label.rrf.measure.accidental" /></th>
										<th class="warning"><fmt:message key="label.rrf.measure.environmental" /></th>
										<th class="warning"><fmt:message key="label.rrf.measure.internal_threat" /></th>
										<th class="warning"><fmt:message key="label.rrf.measure.external_threat" /></th>
										<c:if test="${!empty(assets)}">
											<c:forEach items="${assets}" var="asset">
												<th data-trick-class="MeasureAssetValue" data-trick-name="${asset.asset.name}"><spring:message text='${asset.asset.name}' /></th>
											</c:forEach>
										</c:if>
									</tr>
								</thead>
								<tbody>
									<tr id="tablesliderrow">
										<td class="warning" data-trick-class="MeasureProperties"><input type="text" class="slider" id="measure_fmeasure" value="${props.getFMeasure()}" data-slider-min="0"
											data-slider-max="10" data-slider-step="1" data-slider-value="${props.getFMeasure()}" name="fmeasure" data-slider-orientation="vertical" data-slider-selection="after"
											data-slider-tooltip="show"></td>
										<td class="warning" data-trick-class="MeasureProperties"><input type="text" class="slider" id="measure_fsectoral" value="${props.getFSectoral()}" data-slider-min="0"
											data-slider-max="4" data-slider-step="1" data-slider-value="${props.getFSectoral()}" name="fsectoral" data-slider-orientation="vertical" data-slider-selection="after"
											data-slider-tooltip="show"></td>
										<c:if test="${!empty(categories)}">
											<c:forEach items="${categories.keySet()}" var="category">
												<td class="info" data-trick-class="Category" data-trick-value=<spring:message text="${category}"/>><input type="text" class="slider"
													id="measure_${fn:replace(category,'.','_')}" value="${categories.get(category)}" data-slider-min="0" data-slider-max="4" data-slider-step="1"
													data-slider-value="${categories.get(category)}" name=<spring:message text="${category}" /> data-slider-orientation="vertical" data-slider-selection="after"
													data-slider-tooltip="show"></td>
											</c:forEach>
										</c:if>
										<td class="success"><input type="text" id="measure_preventive" class="slider" value="${props.preventive}" data-slider-min="0" data-slider-max="4"
											data-slider-step="1" data-slider-value="${props.preventive}" data-slider-orientation="vertical" data-slider-selection="after" name="preventive"
											data-slider-tooltip="show"></td>
										<td class="success"><input type="text" class="slider" id="measure_detective" value="${props.detective}" data-slider-min="0" data-slider-max="4"
											data-slider-step="1" data-slider-value="${props.detective}" name="detective" data-slider-orientation="vertical" data-slider-selection="after"
											data-slider-tooltip="show"></td>
										<td class="success"><input type="text" id="measure_limitative" class="slider" value="${props.limitative}" data-slider-min="0" data-slider-max="4"
											data-slider-step="1" data-slider-value="${props.limitative}" data-slider-orientation="vertical" data-slider-selection="after" name="limitative"
											data-slider-tooltip="show"></td>
										<td class="success"><input type="text" class="slider" id="measure_corrective" value="${props.corrective}" data-slider-min="0" data-slider-max="4"
											data-slider-step="1" data-slider-value="${props.corrective}" name="corrective" data-slider-orientation="vertical" data-slider-selection="after"
											data-slider-tooltip="show"></td>
										<td class="warning"><input type="text" class="slider" id="measure_intentional" value="${props.intentional}" data-slider-min="0" data-slider-max="4"
											data-slider-step="1" data-slider-value="${props.intentional}" name="intentional" data-slider-orientation="vertical" data-slider-selection="after"
											data-slider-tooltip="show"></td>
										<td class="warning"><input type="text" class="slider" id="measure_accidental" value="${props.accidental}" data-slider-min="0" data-slider-max="4"
											data-slider-step="1" data-slider-value="${props.accidental}" name="accidental" data-slider-orientation="vertical" data-slider-selection="after"
											data-slider-tooltip="show"></td>
										<td class="warning"><input type="text" class="slider" id="measure_environmental" value="${props.environmental}" data-slider-min="0" data-slider-max="4"
											data-slider-step="1" data-slider-value="${props.environmental}" name="environmental" data-slider-orientation="vertical" data-slider-selection="after"
											data-slider-tooltip="show"></td>
										<td class="warning"><input type="text" class="slider" id="measure_internalThreat" value="${props.internalThreat}" data-slider-min="0" data-slider-max="4"
											data-slider-step="1" data-slider-value="${props.internalThreat}" name="internalThreat" data-slider-orientation="vertical" data-slider-selection="after"
											data-slider-tooltip="show"></td>
										<td class="warning"><input type="text" class="slider" id="measure_externalThreat" value="${props.externalThreat}" data-slider-min="0" data-slider-max="4"
											data-slider-step="1" data-slider-value="${props.externalThreat}" name="externalThreat" data-slider-orientation="vertical" data-slider-selection="after"
											data-slider-tooltip="show"></td>
										<c:if test="${!empty(assets)}">
											<c:forEach items="${assets}" var="asset">
												<td data-trick-class="MeasureAssetValue"><input type="text" class="slider" id='measure_<spring:message text="${asset.asset.name}"/>' value="${asset.value}"
													data-slider-min="0" data-slider-max="100" data-slider-step="1" data-slider-value="${asset.value}" name="<spring:message text="${asset.asset.name}"/>"
													data-slider-orientation="vertical" data-slider-selection="after" data-slider-tooltip="show"></td>
											</c:forEach>
										</c:if>
									</tr>
									<tr id="tabledatarow">
										<td class="warning" data-trick-class="MeasureProperties"><input type="text" readonly="readonly" class="form-control" id="measure_fmeasure_value"
											value="${props.getFMeasure()}" name="fmeasure"></td>
										<td class="warning" data-trick-class="MeasureProperties"><input type="text" readonly="readonly" class="form-control" id="measure_fsectoral_value"
											value="${props.getFSectoral()}" name="fsectoral"></td>
										<c:if test="${!empty(categories)}">
											<c:forEach items="${categories.keySet()}" var="category" varStatus="catStatus">
												<td class="info" data-trick-class="Category" data-trick-value="<spring:message text="${category}" />"><input type="text"
													id='measure_<spring:message text="${category}"/>_value' readonly="readonly" class="form-control" value="${categories.get(category)}"
													name="<spring:message text="${category}" />"></td>
											</c:forEach>
										</c:if>
										<td class="success" data-trick-class="MeasureProperties"><input type="text" readonly="readonly" class="form-control" id="measure_preventive_value"
											value='<fmt:formatNumber maxFractionDigits="0">${props.preventive}</fmt:formatNumber>' name="preventive"></td>
										<td class="success" data-trick-class="MeasureProperties"><input type="text" readonly="readonly" class="form-control" id="measure_detective_value"
											value="<fmt:formatNumber maxFractionDigits="0">${props.detective}</fmt:formatNumber>" name="detective"></td>
										<td class="success" data-trick-class="MeasureProperties"><input type="text" readonly="readonly" class="form-control" id="measure_limitative_value"
											value="<fmt:formatNumber maxFractionDigits="0">${props.limitative}</fmt:formatNumber>" name="limitative"></td>
										<td class="success" data-trick-class="MeasureProperties"><input type="text" readonly="readonly" class="form-control" id="measure_corrective_value"
											value="<fmt:formatNumber maxFractionDigits="0">${props.corrective}</fmt:formatNumber>" name="corrective"></td>
										<td class="warning" data-trick-class="MeasureProperties"><input type="text" readonly="readonly" class="form-control" id="measure_intentional_value"
											value="${props.intentional}" name="intentional"></td>
										<td class="warning" data-trick-class="MeasureProperties"><input type="text" readonly="readonly" class="form-control" id="measure_accidental_value"
											value="${props.accidental}" name="accidental"></td>
										<td class="warning" data-trick-class="MeasureProperties"><input type="text" readonly="readonly" class="form-control" id="measure_environmental_value"
											value="${props.environmental}" name="environmental"></td>
										<td class="warning" data-trick-class="MeasureProperties"><input type="text" readonly="readonly" class="form-control" id="measure_internalThreat_value"
											value="${props.internalThreat}" name="internalThreat"></td>
										<td class="warning" data-trick-class="MeasureProperties"><input type="text" readonly="readonly" class="form-control" id="measure_externalThreat_value"
											value="${props.externalThreat}" name="externalThreat"></td>
										<c:if test="${!empty(assets)}">
											<c:forEach items="${assets}" var="asset">
												<td data-trick-class="MeasureAssetValue"><input type="text" id='measure_<spring:message text="${asset.asset.name}"/>_value' style="min-width: 50px;" readonly="readonly"
													class="form-control" value="${asset.value}" name="<spring:message text="${asset.asset.name}" />"></td>
											</c:forEach>
										</c:if>
									</tr>
								</tbody>
							</table>
						</div>
					</div>
				</div>
			</div>
		</div>
	</c:if>
</form>
