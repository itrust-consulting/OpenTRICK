<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<div class="modal fade" id="addStandardModal" tabindex="-1" role="dialog" data-aria-labelledby="addStandardForm" data-aria-hidden="true" data-backdrop="static">
	<div class="modal-dialog" style="width:800px;">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="phaseNewModal-title">
					<fmt:message key="label.title.analysis.manage_standard" />
				</h4>
			</div>
			<div class="modal-body">
				<ul id="managestandardtabs" class="nav nav-tabs" role="tablist" style="margin-top: 0px;">
					<li class="active" role="tab_group_1"><a href="#group_1" data-toggle="tab"><spring:message code="label.analysis.manage_standard.knowledgebase_standards"
								text="Knowledge Base Standards" /></a></li>
					<li><a href="#group_2" data-toggle="tab"><spring:message code="label.analysis.manage_standard.analysis_standards" text="Analysis Standards" /></a></li>
				</ul>
				<div class="tab-content">
					<div id="group_1" class="tab-pane active" style="padding-top: 10px;">
						<h3>
							<fmt:message key="label.title.analysis.manage_standard.current" />
						</h3>
						<div class="panel panel-default" id="section_kbStandards">
							<div class="panel-heading" style="min-height: 60px">
							<ul id="menu_kbStandards" class="nav nav-pills">
						<li><a onclick="return addStandard();" href="#"><span class="glyphicon glyphicon-plus primary"></span>&nbsp;<fmt:message key="label.action.add" /></a></li>
						<li trick-selectable="true" class="disabled"><a onclick="return showMeasures();" href="#"><span class="glyphicon glyphicon-edit danger"></span>&nbsp;<fmt:message key="label.action.show_measures" /></a></li>
						<li trick-selectable="true" class="disabled pull-right"><a onclick="return removeMeasure();" class="text-danger" href="#"><span class="glyphicon glyphicon-remove"></span>&nbsp;<fmt:message key="label.action.remove" /></a></li>
					</ul>
							</div>
							<div class="panel-body" style="max-height: 700px; overflow: auto;">
								<c:if test="${!empty(currentStandards)}">
									<table class="table">
										<thead>
											<tr>
												<th>&nbsp;</th>
												<th><fmt:message key="label.norm.label" /></th>
												<th><fmt:message key="label.norm.version" /></th>
												<th colspan="3"><fmt:message key="label.norm.description" /></th>
												<th><fmt:message key="label.norm.computable" /></th>
												<th><fmt:message key="label.norm.type" /></th>
											</tr>
										</thead>
										<tbody>
											<c:forEach items="${currentStandards}" var="standard">
												<tr>
												<td><input type="checkbox" class="checkbox" onchange="return updateMenu(this,'#section_kbStandards','#menu_kbStandards');"></td>
													<td><spring:message text="${standard.label}" /></td>
													<td><spring:message text="${standard.version}" /></td>
													<td colspan="3"><spring:message text="${standard.description}" /></td>
													<td style="text-align: center"><fmt:message key="label.${standard.computable?'yes':'no'}" /></td>
													<td style="text-align: center"><fmt:message key="label.norm.type_${fn:toLowerCase(standard.type)}" /></td>
												</tr>
											</c:forEach>
											<c:if test="${currentStandards!=null?currentStandards.size()==0:true}">
												<tr>
													<td colspan="6"><fmt:message key="label.no_standards" /></td>
												</tr>
											</c:if>
										</tbody>
									</table>
								</c:if>
								<c:if test="${empty(currentStandards)}">
									<fmt:message key="label.no_standards" />
								</c:if>
							</div>
						</div>
						<h3>
							<fmt:message key="label.title.analysis.manage_standard.available" />
						</h3>
						<c:if test="${!empty(standards)}">
							<form name="standard" action="${pageContext.request.contextPath}/Analysis/Save/Standard" class="form" id="addStandardForm">
								<div class="form-group">
									<label> <fmt:message key="label.analysis.add.standard.select.choose" />
									</label>
									<div style="height: 14px;">
										<div class="col-lg-11">
											<select name="idStandard" class="form-control" onchange="$('#selectedStandardDescription').html($('#addStandardForm select option:selected').attr('title'));">
												<c:forEach items="${standards}" var="standard">
													<option title='<spring:message text="${standard.description}"/>' value="${standard.id}">
														<spring:message text="${standard.label}" /> -
														<spring:message text="${standard.version}" />
													</option>
												</c:forEach>
											</select>
										</div>
										<div class="col-lg-1" style="padding: 6px;">
											<a href="#" onclick="saveStandard('addStandardForm')" style="font-size: 20px;" id="btn_save_standard" title='<fmt:message key="label.action.add" />'><span
												class="fa fa-plus-circle"></span></a>
										</div>
									</div>
								</div>
							</form>
						</c:if>
						<c:if test="${empty(standards)}">
							<fmt:message key="label.no_standards" />
						</c:if>
						<div class="progress progress-striped active" style="display: none; width: 100%; margin-top: 15px; margin-bottom: 0;" id="add_standard_progressbar">
							<div class="progress-bar progress-striped" role="progressbar" style="width: 100%;"></div>
						</div>
					</div>
					<div id="group_2" class="tab-pane" style="padding-top: 10px;">
						<h3>
							<fmt:message key="label.title.analysis.manage_standard.current" />
						</h3>
						<div class="panel panel-default" id="section_analysisStandards">
							<div class="panel-heading" style="min-height: 60px">
							
							<ul id="menu_analysisStandards" class="nav nav-pills">
						<li><a onclick="return addStandard();" href="#"><span class="glyphicon glyphicon-plus primary"></span>&nbsp;<fmt:message key="label.action.add" /></a></li>
						<li trick-selectable="true" class="disabled"><a onclick="return editStandard();" href="#"><span class="glyphicon glyphicon-edit"></span>&nbsp;<fmt:message key="label.action.edit" /></a></li>
						<li trick-selectable="true" class="disabled"><a onclick="return showMeasures();" href="#"><span class="glyphicon glyphicon-new-window"></span>&nbsp;<fmt:message key="label.action.show_measures" /></a></li>
						<li trick-selectable="true" class="disabled pull-right"><a onclick="return removeMeasure();" class="text-danger" href="#"><span class="glyphicon glyphicon-remove"></span>&nbsp;<fmt:message key="label.action.remove" /></a></li>
					</ul>
							
							
							</div>
							<div class="panel-body" style="max-height: 700px; overflow: auto;">
						<c:if test="${empty(currentAnalysisStandards)}">
							<fmt:message key="label.no_standards" />
						</c:if>
						<c:if test="${!empty(currentAnalysisStandards)}">
							<table class="table">
								<thead>
									<tr>
									<th>&nbsp;</th>
										<th><fmt:message key="label.norm.label" /></th>
										<th colspan="3"><fmt:message key="label.norm.description" /></th>
										<th><fmt:message key="label.norm.computable" /></th>
										<th><fmt:message key="label.norm.type" /></th>
										<th><fmt:message key="label.actions" /></th>
									</tr>
								</thead>
								<tbody>
									<c:forEach items="${currentAnalysisStandards}" var="standard">
										<tr>
										<td><input type="checkbox" class="checkbox" onchange="return updateMenu(this,'#section_analysisStandards','#menu_analysisStandards');"></td>
											<td><spring:message text="${standard.label}" /></td>
											<td><spring:message text="${standard.version}" /></td>
											<td colspan="3"><spring:message text="${standard.description}" /></td>
											<td style="text-align: center"><fmt:message key="label.${standard.computable?'yes':'no'}" /></td>
											<td style="text-align: center"><fmt:message key="label.norm.type_${fn:toLowerCase(standard.type)}" /></td>
											<td style="text-align: center"><a href="#" role="manage-standard" trick-class="standard" trick-id="${standard.id}"
												style="font-size: 20px; display: inline; text-decoration: none;" class="text-warning" title='<fmt:message key="label.action.edit" />'> <span
													class="fa fa-pencil-square-o"></span></a> <a href="#" role="remove-standard" trick-class="standard" trick-id="${standard.id}"
												style="font-size: 20px; display: inline; text-decoration: none;" class="text-danger" title='<fmt:message key="label.action.delete" />'> <span
													class="fa fa-minus-circle"></span></a></td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
						</c:if>
						</div>
						</div>
						<h3>
							<fmt:message key="label.title.analysis.manage_standard.create" />
						</h3>
						<form name="standard" action="/Save" class="form-horizontal" id="standard_form" method="post">
							<div class="form-group">
								<label for="label" class="col-sm-2 control-label"> <spring:message code="label.norm.label" text="Name" />
								</label>
								<div class="col-sm-10">
									<input name="label" id="standard_label" class="form-control" type="text" />
								</div>
							</div>
							<div class="form-group">
								<label for="version" class="col-sm-2 control-label"> <spring:message code="label.norm.version" text="Version" />
								</label>
								<div class="col-sm-10">
									<input name="version" id="standard_version" class="form-control" type="text" />
								</div>
							</div>
							<div class="form-group">
								<label for="description" class="col-sm-2 control-label"> <spring:message code="label.norm.description" text="Description" />
								</label>
								<div class="col-sm-10">
									<input name="description" id="standard_description" class="form-control" type="text" />
								</div>
							</div>
							<div class="panel panel-primary">
								<div class="panel-body">
									<label class="col-sm-12 text-center"><spring:message code="label.norm.standard_type" text="Standard Type" /></label> <label class="radio-inline col-sm-4"
										style="margin-left: 0;"> <input type="radio" name="type" value="NORMAL"> <spring:message code="label.norm.standard_type.normal" text="Normal" /></label> <label
										class="radio-inline col-sm-4" style="margin-left: 0;"> <input type="radio" name="type" value="MATURITY"> <spring:message
											code="label.norm.standard_type.maturity" text="Maturity" /></label> <label class="radio-inline col-sm-4" style="margin-left: 0;"> <input type="radio" name="type"
										value="ASSET"> <spring:message code="label.norm.standard_type.asset" text="Asset" /></label>
								</div>
							</div>
							<div class="form-group">
								<label for="computable" class="col-sm-2 control-label"> <spring:message code="label.norm.computable" text="Computable" />
								</label>
								<div class="col-sm-10">
									<input name="computable" id="standard_computable" class="form-control" type="checkbox" />
								</div>
							</div>
							<button id="addstandardbutton" type="button" class="btn btn-primary" onclick="saveAnalysisStandard('standard_form')">
								<spring:message code="label.action.add.norm" text="Add" />
							</button>
						</form>
					</div>
				</div>
			</div>
			<div class="modal-footer" style="margin-top: 0;"></div>
		</div>
		<!-- /.modal-content -->
	</div>
	<!-- /.modal-dialog -->
</div>
<!-- /.modal -->