<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<table data-trick-controller-name='measure' class="table table-condensed rrf-values" style="margin-bottom: 0;">
	<thead>
		<tr>
			<th class="warning text-center"><spring:message code="label.rrf.measure.strength_measure" /></th>
			<th class="warning text-center"><spring:message code="label.rrf.measure.strength_sectoral" /></th>
			<c:if test="${!empty(categories)}">
				<c:forEach items="${categories.keySet()}" var="category">
					<th class="info text-center" data-trick-class="Category" data-trick-value=<spring:message text="${category}" />><spring:message
							code="label.rrf.category.${fn:toLowerCase(fn:replace(category,'_','.'))}" /></th>
				</c:forEach>
			</c:if>

			<th class="success text-center"><spring:message code="label.rrf.measure.preventive" /></th>
			<th class="success text-center"><spring:message code="label.rrf.measure.detective" /></th>
			<th class="success text-center"><spring:message code="label.rrf.measure.limitative" /></th>
			<th class="success text-center"><spring:message code="label.rrf.measure.corrective" /></th>
			<th class="warning text-center"><spring:message code="label.rrf.measure.intentional" /></th>
			<th class="warning text-center"><spring:message code="label.rrf.measure.accidental" /></th>
			<th class="warning text-center"><spring:message code="label.rrf.measure.environmental" /></th>
			<th class="warning text-center"><spring:message code="label.rrf.measure.internal_threat" /></th>
			<th class="warning text-center"><spring:message code="label.rrf.measure.external_threat" /></th>
			<c:if test="${!empty(assetTypeValues)}">
				<c:forEach items="${assetTypeValues}" var="assetTypeValue">
					<th class="text-center"><spring:message code='label.asset_type.${fn:toLowerCase(assetTypeValue.assetType.name)}' /></th>
				</c:forEach>
			</c:if>
			<c:if test="${!empty(assets)}">
				<c:forEach items="${assets}" var="asset">
					<th class="text-center"><spring:message text='${asset.asset.name}' /></th>
				</c:forEach>
			</c:if>
		</tr>
	</thead>
	<tbody>
		<tr >
			<td class="text-center warning"><input type="range" id="measure_fmeasure" value="${strength_measure}" min="0" max="10" step="1"
				value="${strength_measure}" name="fmeasure" orient="vertical"></td>
			<td class="text-center warning"><input type="range" id="measure_fsectoral" value="${strength_sectorial}" min="0" max="4" step="1"
				value="${strength_sectorial}" name="fsectoral" orient="vertical"></td>
			<c:if test="${!empty(categories)}">
				<c:forEach items="${categories.keySet()}" var="category">
					<td class="text-center info" data-trick-class="Category" data-trick-value='<spring:message text="${category}"/>' ><input type="range"
						id="measure_${fn:replace(category,'.','_')}" value="${categories.get(category)}" min="0" max="4" step="1" value="${categories.get(category)}" 
						name='<spring:message text="${category}" />' orient="vertical" ></td>
				</c:forEach>
			</c:if>
			<td class="text-center success"><input type="range" id="measure_preventive"  value="${preventive}" min="0" max="4" step="1"
				value="${preventive}" orient="vertical"selection="after" name="preventive"tooltip="show"></td>
			<td class="text-center success"><input type="range" id="measure_detective" value="${detective}" min="0" max="4" step="1"
				value="${detective}" name="detective" orient="vertical"></td>
			<td class="text-center success"><input type="range" id="measure_limitative"  value="${limitative}" min="0" max="4" step="1"
				value="${limitative}" orient="vertical"selection="after" name="limitative"tooltip="show"></td>
			<td class="text-center success"><input type="range" id="measure_corrective" value="${corrective}" min="0" max="4" step="1"
				value="${corrective}" name="corrective" orient="vertical"></td>
			<td class="text-center warning"><input type="range" id="measure_intentional" value="${intentional}" min="0" max="4" step="1"
				value="${intentional}" name="intentional" orient="vertical"></td>
			<td class="text-center warning"><input type="range" id="measure_accidental" value="${accidental}" min="0" max="4" step="1"
				value="${accidental}" name="accidental" orient="vertical"></td>
			<td class="text-center warning"><input type="range" id="measure_environmental" value="${environmental}" min="0" max="4" step="1"
				value="${environmental}" name="environmental" orient="vertical"></td>
			<td class="text-center warning"><input type="range" id="measure_internalThreat" value="${internalThreat}" min="0" max="4" step="1"
				value="${internalThreat}" name="internalThreat" orient="vertical"></td>
			<td class="text-center warning"><input type="range" id="measure_externalThreat" value="${externalThreat}" min="0" max="4" step="1"
				value="${externalThreat}" name="externalThreat" orient="vertical"></td>
			<c:if test="${!empty(assetTypeValues)}">
				<c:forEach items="${assetTypeValues}" var="assetTypeValue">
					<td data-trick-class="AssetType" class='text-center' ><input type="range" id='measure_<spring:message text="${assetTypeValue.assetType.name}"/>' value="${assetTypeValue.value}"
						min="0" max="100" step="1" value="${assetTypeValue.value}" name='<spring:message text="${assetTypeValue.assetType.name}"/>' orient="vertical"></td>
				</c:forEach>
			</c:if>
			<c:if test="${!empty(assets)}">
				<c:forEach items="${assets}" var="asset">
					<td data-trick-class="MeasureAssetValue" class='text-center'><input type="range" id='measure_<spring:message text="${asset.asset.name}"/>' value="${asset.value}"
						min="0" max="100" step="1" value="${asset.value}" name='<spring:message text="${asset.asset.name}"/>' orient="vertical"></td>
				</c:forEach>
			</c:if>
		</tr>
		<tr>
			<td class="text-center warning"><input type="text"  readonly="readonly" class="text-center form-control" id="measure_fmeasure_value" value="${strength_measure}"
				name="fmeasure"></td>
			<td class="text-center warning"><input type="text"  readonly="readonly" class="text-center form-control" id="measure_fsectoral_value" value="${strength_sectorial}"
				name="fsectoral"></td>
			<c:if test="${!empty(categories)}">
				<c:forEach items="${categories.keySet()}" var="category" varStatus="catStatus">
					<td class="text-center info" data-trick-class="Category" data-trick-value="<spring:message text="${category}" />"><input type="text"
						id='measure_<spring:message text="${fn:replace(category,'.','_')}"/>_value' readonly="readonly" class="text-center form-control" value="${categories.get(category)}"
						name="<spring:message text="${category}" />"></td>
				</c:forEach>
			</c:if>
			<td class="text-center success"><input type="text"  readonly="readonly" class="text-center form-control" id="measure_preventive_value" value="${preventive}"
				name="preventive"></td>
			<td class="text-center success"><input type="text"  readonly="readonly" class="text-center form-control" id="measure_detective_value" value="${detective}"
				name="detective"></td>
			<td class="text-center success"><input type="text"  readonly="readonly" class="text-center form-control" id="measure_limitative_value" value="${limitative}"
				name="limitative"></td>
			<td class="text-center success"><input type="text"  readonly="readonly" class="text-center form-control" id="measure_corrective_value" value="${corrective}"
				name="corrective"></td>
			<td class="text-center warning"><input type="text"  readonly="readonly" class="text-center form-control" id="measure_intentional_value" value="${intentional}"
				name="intentional"></td>
			<td class="text-center warning"><input type="text"  readonly="readonly" class="text-center form-control" id="measure_accidental_value" value="${accidental}"
				name="accidental"></td>
			<td class="text-center warning"><input type="text"  readonly="readonly" class="text-center form-control" id="measure_environmental_value" value="${environmental}"
				name="environmental"></td>
			<td class="text-center warning"><input type="text"  readonly="readonly" class="text-center form-control" id="measure_internalThreat_value" value="${internalThreat}"
				name="internalThreat"></td>
			<td class="text-center warning"><input type="text"  readonly="readonly" class="text-center form-control" id="measure_externalThreat_value" value="${externalThreat}"
				name="externalThreat"></td>
			<c:if test="${!empty(assetTypeValues)}">
				<c:forEach items="${assetTypeValues}" var="assetTypeValue">
					<td data-trick-class="AssetType"><input type="text" class='text-center form-control' id='measure_<spring:message text="${assetTypeValue.assetType.name}"/>_value'
						readonly="readonly" value="${assetTypeValue.value}" name="<spring:message text="${assetTypeValue.assetType.name}" />"></td>
				</c:forEach>
			</c:if>
			<c:if test="${!empty(assets)}">
				<c:forEach items="${assets}" var="asset">
					<td data-trick-class="AssetType"><input type="text" class='text-center form-control' id='measure_<spring:message text="${asset.asset.name}"/>_value' readonly="readonly"
						value="${asset.value}" name="<spring:message text="${asset.asset.name}" />"></td>
				</c:forEach>
			</c:if>
		</tr>
	</tbody>
</table>