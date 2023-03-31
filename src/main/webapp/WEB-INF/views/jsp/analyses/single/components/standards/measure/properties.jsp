<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<div id="tab_properties" class="tab-pane measure-property ${not isAnalysisOnly?'active':''}" style="padding-top: 17px;">
	<div style="overflow-x: auto; overflow-y: hidden;  scrollbar-width: thin;">
		<table class="table">
			<thead>
				<tr id="slidersTitle" class="text-center">
					<th class="warning"><spring:message code="label.rrf.measure.strength_measure" /></th>
					<th class="warning"><spring:message code="label.rrf.measure.strength_sectoral" /></th>
					<c:forEach items="${measureForm.properties.categories.keySet()}" var="category">
						<th class="info text-center" style="min-width:60px" ${not empty cssfExcludes[category]? 'hidden="hidden"' :''} data-trick-class="Category" data-trick-value=<spring:message text="${category}" />><fmt:message
								key="label.rrf.category.${fn:toLowerCase(fn:replace(category,'_','.'))}" /></th>
					</c:forEach>
					<th class="success"><spring:message code="label.rrf.measure.preventive" /></th>
					<th class="success"><spring:message code="label.rrf.measure.detective" /></th>
					<th class="success"><spring:message code="label.rrf.measure.limitative" /></th>
					<th class="success"><spring:message code="label.rrf.measure.corrective" /></th>
					<th class="warning"><spring:message code="label.rrf.measure.intentional" /></th>
					<th class="warning"><spring:message code="label.rrf.measure.accidental" /></th>
					<th class="warning"><spring:message code="label.rrf.measure.environmental" /></th>
					<th class="warning"><spring:message code="label.rrf.measure.internal_threat" /></th>
					<th class="warning"><spring:message code="label.rrf.measure.external_threat" /></th>
					<c:choose>
						<c:when test="${measureForm.type == 'ASSET' }">
							<c:forEach items="${measureForm.assetValues}" var="assetValue">
								<th data-trick-class="MeasureAssetValue" data-trick-asset-id="${assetValue.id}"><spring:message text='${assetValue.name}' /></th>
							</c:forEach>
						</c:when>
						<c:when test="${measureForm.type == 'NORMAL' }">
							<c:forEach items="${measureForm.assetValues}" var="assetValue">
								<th ${not empty hiddenAssetTypes[assetValue.type]? 'hidden="hidden"' :''}><spring:message code='label.asset_type.${fn:toLowerCase(assetValue.name)}' /></th>
							</c:forEach>
						</c:when>
					</c:choose>
				</tr>
			</thead>
			<tbody>
				<tr id="sliders" class="slider-vertical" style="text-align: center;">
					<td class="warning" data-trick-class="MeasureProperties"><input type="range" orient="vertical" id="fmeasure" value="${measureForm.properties.getFMeasure()}"
						min="0" max="10" step="1" value="${measureForm.properties.getFMeasure()}" name="fmeasure" orientation="vertical"
						selection="after" tooltip="show"></td>
					<td class="warning" data-trick-class="MeasureProperties"><input type="range" orient="vertical" id="fsectoral" value="${measureForm.properties.getFSectoral()}"
						min="0" max="4" step="1" value="${measureForm.properties.getFSectoral()}" name="fsectoral" orientation="vertical"
						selection="after" tooltip="show"></td>
					<c:forEach items="${measureForm.properties.categories.keySet()}" var="category">
						<td class="info" ${not empty cssfExcludes[category]? 'hidden="hidden"' :''} data-trick-class="Category" data-trick-value=<spring:message text="${category}"/>><input
							type="range" orient="vertical" id="${fn:replace(category,'.','_')}" value="${measureForm.properties.getCategoryValue(category)}" min="0" max="4"
							step="1" value="${measureForm.properties.getCategoryValue(category)}" name=<spring:message text="${fn:replace(fn:toLowerCase(category),'.','')}" />
							orientation="vertical" selection="after" tooltip="show"></td>
					</c:forEach>
					<td class="success"><input type="range" id="preventive" orient="vertical" value="${measureForm.properties.preventive}" min="0" max="4"
						step="1" value="${measureForm.properties.preventive}" orientation="vertical" selection="after" name="preventive"
						tooltip="show"></td>
					<td class="success"><input type="range" orient="vertical" id="detective" value="${measureForm.properties.detective}" min="0" max="4"
						step="1" value="${measureForm.properties.detective}" name="detective" orientation="vertical" selection="after"
						tooltip="show"></td>
					<td class="success"><input type="range" id="limitative" orient="vertical" value="${measureForm.properties.limitative}" min="0" max="4"
						step="1" value="${measureForm.properties.limitative}" orientation="vertical" selection="after" name="limitative"
						tooltip="show"></td>
					<td class="success"><input type="range" orient="vertical" id="corrective" value="${measureForm.properties.corrective}" min="0" max="4"
						step="1" value="${measureForm.properties.corrective}" name="corrective" orientation="vertical" selection="after"
						tooltip="show"></td>
					<td class="warning"><input type="range" orient="vertical" id="intentional" value="${measureForm.properties.intentional}" min="0" max="4"
						step="1" value="${measureForm.properties.intentional}" name="intentional" orientation="vertical" selection="after"
						tooltip="show"></td>
					<td class="warning"><input type="range" orient="vertical" id="accidental" value="${measureForm.properties.accidental}" min="0" max="4"
						step="1" value="${measureForm.properties.accidental}" name="accidental" orientation="vertical" selection="after"
						tooltip="show"></td>
					<td class="warning"><input type="range" orient="vertical" id="environmental" value="${measureForm.properties.environmental}" min="0" max="4"
						step="1" value="${measureForm.properties.environmental}" name="environmental" orientation="vertical" selection="after"
						tooltip="show"></td>
					<td class="warning"><input type="range" orient="vertical" id="internalThreat" value="${measureForm.properties.internalThreat}" min="0" max="4"
						step="1" value="${measureForm.properties.internalThreat}" name="internalThreat" orientation="vertical" selection="after"
						tooltip="show"></td>
					<td class="warning"><input type="range" orient="vertical" id="externalThreat" value="${measureForm.properties.externalThreat}" min="0" max="4"
						step="1" value="${measureForm.properties.externalThreat}" name="externalThreat" orientation="vertical" selection="after"
						tooltip="show"></td>
					<c:choose>
						<c:when test="${measureForm.type == 'ASSET'}">
							<c:forEach items="${measureForm.assetValues}" var="assetValue">
								<td data-trick-class="MeasureAssetValue" data-trick-asset-id="${assetValue.id}"><input type="range" orient="vertical"
									id='asset_slider_<spring:message text="${assetValue.id}"/>' value="${assetValue.value}" min="0" max="100" step="1"
									value="${assetValue.value}" name="<spring:message text="${assetValue.id}"/>" orientation="vertical" selection="after"
									tooltip="show"></td>
							</c:forEach>
						</c:when>
						<c:when test="${measureForm.type == 'NORMAL'}">
							<c:forEach items="${measureForm.assetValues}" var="assetValue">
								<td ${not empty hiddenAssetTypes[assetValue.type]? 'hidden="hidden"' :''}><input type="range" orient="vertical" id='asset_slider_<spring:message text="${assetValue.id}"/>'
									value="${assetValue.value}" min="0" max="100" step="1" value="${assetValue.value}"
									name="<spring:message text="${assetValue.id}"/>" orientation="vertical" selection="after" tooltip="show"></td>
							</c:forEach>
						</c:when>
					</c:choose>
				</tr>
				<tr id="values">
					<td class="warning" data-trick-class="MeasureProperties"><input type="text" style="text-align: center; min-width: 40px" readonly="readonly" class="form-control"
						id="fvalue" value="${measureForm.properties.getFMeasure()}" name="fmeasure"></td>
					<td class="warning" data-trick-class="MeasureProperties"><input type="text" style="text-align: center;" readonly="readonly" class="form-control" id="fsectoral_value"
						value="${measureForm.properties.getFSectoral()}" name="fsectoral"></td>
					<c:forEach items="${measureForm.properties.categories.keySet()}" var="category" varStatus="catStatus">
						<td class="info" ${not empty cssfExcludes[category]? 'hidden="hidden"' :''} data-trick-class="Category" data-trick-value="<spring:message text="${category}" />"><input
							type="text" style="text-align: center;" id='<spring:message text="${category}"/>_value' readonly="readonly" class="form-control"
							value="${measureForm.properties.getCategoryValue(category)}" name="<spring:message text="${fn:replace(fn:toLowerCase(category),'.','')}" />"></td>
					</c:forEach>
					<td class="success" data-trick-class="MeasureProperties"><input type="text" style="text-align: center;" readonly="readonly" class="form-control" id="preventive_value"
						value='<fmt:formatNumber maxFractionDigits="0">${measureForm.properties.preventive}</fmt:formatNumber>' name="preventive"></td>
					<td class="success" data-trick-class="MeasureProperties"><input type="text" style="text-align: center;" readonly="readonly" class="form-control" id="detective_value"
						value="<fmt:formatNumber maxFractionDigits="0">${measureForm.properties.detective}</fmt:formatNumber>" name="detective"></td>
					<td class="success" data-trick-class="MeasureProperties"><input type="text" style="text-align: center;" readonly="readonly" class="form-control" id="limitative_value"
						value="<fmt:formatNumber maxFractionDigits="0">${measureForm.properties.limitative}</fmt:formatNumber>" name="limitative"></td>
					<td class="success" data-trick-class="MeasureProperties"><input type="text" style="text-align: center;" readonly="readonly" class="form-control" id="corrective_value"
						value="<fmt:formatNumber maxFractionDigits="0">${measureForm.properties.corrective}</fmt:formatNumber>" name="corrective"></td>
					<td class="warning" data-trick-class="MeasureProperties"><input type="text" style="text-align: center;" readonly="readonly" class="form-control" id="intentional_value"
						value="${measureForm.properties.intentional}" name="intentional"></td>
					<td class="warning" data-trick-class="MeasureProperties"><input type="text" style="text-align: center;" readonly="readonly" class="form-control" id="accidental_value"
						value="${measureForm.properties.accidental}" name="accidental"></td>
					<td class="warning" data-trick-class="MeasureProperties"><input type="text" style="text-align: center;" readonly="readonly" class="form-control" id="environmental_value"
						value="${measureForm.properties.environmental}" name="environmental"></td>
					<td class="warning" data-trick-class="MeasureProperties"><input type="text" style="text-align: center;" readonly="readonly" class="form-control" id="internalThreat_value"
						value="${measureForm.properties.internalThreat}" name="internalThreat"></td>
					<td class="warning" data-trick-class="MeasureProperties"><input type="text" style="text-align: center;" readonly="readonly" class="form-control" id="externalThreat_value"
						value="${measureForm.properties.externalThreat}" name="externalThreat"></td>
					<c:choose>
						<c:when test="${measureForm.type == 'ASSET' }">
							<c:forEach items="${measureForm.assetValues}" var="assetValue">
								<td data-trick-class="MeasureAssetValue" data-trick-asset-id="${assetValue.id}"><input type="text" style="text-align: center;"
									id='property_asset_<spring:message text="${assetValue.id}"/>_value' style="min-width: 50px;" readonly="readonly" class="form-control" value="${assetValue.value}"
									name="<spring:message text="${assetValue.id}" />"></td>
							</c:forEach>
						</c:when>
						<c:when test="${measureForm.type == 'NORMAL' }">
							<c:forEach items="${measureForm.assetValues}" var="assetValue">
								<td ${not empty hiddenAssetTypes[assetValue.type]? 'hidden="hidden"' :''}><input type="text" style="text-align: center;"
									id='property_asset_type_<spring:message text="${assetValue.id}"/>_value' style="min-width: 50px;" readonly="readonly" class="form-control" value="${assetValue.value}"
									name="<spring:message text="${assetValue.id}" />"></td>
							</c:forEach>
						</c:when>
					</c:choose>
				</tr>
			</tbody>
		</table>
	</div>
</div>