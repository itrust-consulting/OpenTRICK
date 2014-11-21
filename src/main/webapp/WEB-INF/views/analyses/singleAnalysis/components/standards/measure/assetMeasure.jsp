<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
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
			<div class="col-sm-6">
				<h3>Available assets</h3>
				<select class="form-control" name="availableAssets" id="availableAssets" style="display: none;">
					<c:forEach items="${availableAssets}" var="availableAsset">
						<option value="asset_${availableAsset.id}"><spring:message text="${availableAsset.name}" />
					</c:forEach>
				</select>
				<ul style="padding:0;margin:0;" trick-type="available">
					<c:forEach items="${availableAssets}" var="availableAsset">
						<li style="cursor: pointer" opt="asset_${availableAsset.id}" class="list-group-item"><spring:message text="${availableAsset.name}" /></li>
					</c:forEach>
				</ul>
			</div>
			<div class="col-sm-6">
				<h3>Measure assets</h3>
				<select class="form-control" name="measureAssets" id="measureAssets" style="display: none;">
					<c:forEach items="${measureAssets}" var="measureAsset">
						<option value="asset_${measureAsset.id}"><spring:message text="${measureAsset.name}" />
					</c:forEach>
				</select>
				<ul style="padding:0;margin:0;" trick-type="measure">
					<c:forEach items="${measureAssets}" var="measureAsset">
						<li style="cursor: pointer" opt="asset_${measureAsset.id}" class="list-group-item"><spring:message text="${measureAsset.name}" /></li>
					</c:forEach>
				</ul>
			</div>
		</div>
	</c:if>
</form>
