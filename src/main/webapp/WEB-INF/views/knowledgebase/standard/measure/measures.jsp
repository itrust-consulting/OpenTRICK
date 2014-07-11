<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div id="section_measure" class="section">
	<div id="measures_header" class="page-header">
		<h3 id="Measures">
			<spring:message code="label.measure.measures" />
			: ${norm.label} - ${norm.version} - ${norm.description} <input type="hidden" id="normId" value="${norm.id}" /> <input type="hidden" id="normLabel" value="${norm.label}" /> <input
				type="hidden" id="normVersion" value="${norm.version}" />
		</h3>
	</div>
	<div id="measures_body" class="content" role="main">
		<div id="section_measure_description" class="section">
			<div class="panel panel-default">
				<div class="panel-heading" style="min-height: 60px">
					<div class="row">
						<c:if test="${!empty languages}">
							<div class="col-md-1">
								<select id="languageselect" class="form-control" style="width: auto;">
									<c:forEach items="${languages}" var="language">
										<option ${language.id == selectedLanguage.id?'selected="selected"':""} value="${language.id}">${language.name}</option>
									</c:forEach>
								</select>
							</div>
						</c:if>
						<div class="col-md-11">
							<ul class="nav nav-pills" id="menu_measure_description">
								<li><a href="#" onclick="return newMeasure();"><span class="glyphicon glyphicon-plus primary"></span> <spring:message code="label.norm.add" text="Add" /> </a></li>
								<li class="disabled" trick-selectable="true"><a href="#" onclick="return editSingleMeasure();"><span class="glyphicon glyphicon-edit danger"></span> <spring:message
											code="label.norm.edit" text="Edit" /> </a></li>
								<li class="disabled" trick-selectable="true"><a href="#" onclick="return deleteMeasure();"><span class="glyphicon glyphicon-remove"></span> <spring:message
											code="label.norm.delete" text="Delete" /> </a></li>
							</ul>
						</div>
					</div>
				</div>
				<div class="panel-body" style="max-height: 700px; overflow: auto;">
					<c:choose>
						<c:when test="${!empty measureDescriptions}">
							<table id="measurestable" class="table table-hover table-fixed-header">
								<thead>
									<tr role="row">
										<th><input type="checkbox" class="checkbox" onchange="return checkControlChange(this,'measure_description','modal-measure')"></th>
										<th colspan="2"><spring:message code="label.measure.level" /></th>
										<th colspan="3"><spring:message code="label.measure.reference" /></th>
										<th colspan="20"><spring:message code="label.measure.domain" /></th>
										<th colspan="20"><spring:message code="label.measure.description" /></th>
										<th colspan="3"><spring:message code="label.measure.computable" /></th>
									</tr>
								</thead>
								<tbody>
									<c:forEach items="${measureDescriptions}" var="measureDescription">
										<tr trick-id="${measureDescription.id}" ondblclick="return editSingleMeasure('${measureDescription.id}','${norm.id}');">
											<td><input type="checkbox" class="checkbox" onchange="return updateMenu('#section_measure_description','#menu_measure_description','modal-measure');"></td>
											<td colspan="2">${measureDescription.level}</td>
											<td colspan="3">${measureDescription.reference}</td>
											<td colspan="20">${measureDescription.measureDescriptionTexts[0].domain.equals("")==false?measureDescription.measureDescriptionTexts[0].domain:"&nbsp;"}</td>
											<td colspan="20">${measureDescription.measureDescriptionTexts[0].description.equals("")==false?measureDescription.measureDescriptionTexts[0].description:"&nbsp;"}</td>
											<td colspan="3" trick-computable="${measureDescription.computable}"><c:if test="${measureDescription.computable==true}">
													<spring:message code="label.yes_no.true" />
												</c:if> <c:if test="${measureDescription.computable==false}">
													<spring:message code="label.yes_no.false" />
												</c:if></td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
						</c:when>
						<c:otherwise>
							<h4>
								<spring:message code="label.measure.notexist" />
							</h4>
						</c:otherwise>
					</c:choose>
				</div>
			</div>
		</div>
	</div>
</div>