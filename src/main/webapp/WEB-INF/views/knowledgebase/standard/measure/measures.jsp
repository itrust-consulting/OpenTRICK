<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div id="section_measure" class="section">
<div id="measures_header" class="page-header">
	<h3 id="Measures">
		<spring:message code="label.measure.measures" />
		: ${norm.label} <input type="hidden" id="normId" value="${norm.id}" />
		<input type="hidden" id="normLabel" value="${norm.label}" />
	</h3>
</div>
<div id="measures_body" class="content" role="main" data-spy="scroll">
	<c:if test="${!empty measureDescriptions}">
		<div class="panel panel-default">
			<div class="panel-heading">
				<button class="btn btn-default" onclick="newMeasure(${norm.id});">
					<spring:message code="label.measure.add.menu" text="Add a new Measure" />
				</button>
				<c:if test="${!empty languages}">
					<select id="languageselect" class="form-control" style="width:auto;">
						<c:forEach items="${languages}" var="language">
							<option ${language.id == selectedLanguage.id?'selected="selected"':""} value="${language.id}">${language.name}</option>
						</c:forEach>
					</select>
				</c:if>
			</div>
			<div class="panel-body">
				<table id="measurestable">
					<thead>
						<tr>
							<th><spring:message code="label.measure.level" /></th>
							<th><spring:message code="label.measure.reference" /></th>
							<th><spring:message code="label.measure.domain" /></th>
							<th><spring:message code="label.measure.description" /></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${measureDescriptions}" var="measureDescription">
							<tr trick-id="${measureDescription.id}">
								<td>${measureDescription.level}</td>
								<td>${measureDescription.reference}</td>
								<td>${measureDescription.measureDescriptionTexts[0].domain.equals("")==false?measureDescription.measureDescriptionTexts[0].domain:"&nbsp;"}</td>
								<td>${measureDescription.measureDescriptionTexts[0].description.equals("")==false?measureDescription.measureDescriptionTexts[0].description:"&nbsp;"}</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</c:if>
	<c:if test="${empty measureDescriptions}">
		<div class="panel panel-default">
			<div class="panel-heading">
				<button class="btn btn-default" data-toggle="modal" data-target="#addMeasureModel">
					<spring:message code="label.measure.add.menu" text="Add new Measure" />
				</button>
			</div>
			<div class="panel-body">
				<h4>
					<spring:message code="label.measure.notexist" />
				</h4>
			</div>
		</div>
	</c:if>
</div>
</div>
<script type="text/javascript">
$(document).ready(function() {
	$('#measurestable').dataTable({
		"bLengthChange" : false,
		"bAutoWidth" : false,
		"aoColumns": [
						{ "sWidth": "20px" },
						{ "sWidth": "20px" },
						null,
						null,
						{ "sWidth": "70px" }
					]
	});
	$("#measurestable").removeAttr( "style" );
});
</script>