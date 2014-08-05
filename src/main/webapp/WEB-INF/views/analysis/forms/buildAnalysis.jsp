<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="modal fade" id="buildAnalysisModal" tabindex="-1" role="dialog" data-aria-labelledby="buildAnalysisModal" data-aria-hidden="true" data-backdrop="static">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title">
					<spring:message code="label.title.build.analysis" text="Build an analysis" />
				</h4>
			</div>
			<div class="modal-body">
				<form action="#" class="form-horizontal">
					<input type="hidden" name="id" value="-1" id="analysis_id">
					<div class="form-group">
						<label for="customer" class="col-sm-3 control-label"> <spring:message code="label.customer.organisation" text="Customer" />
						</label>
						<div class="col-sm-9" id="analysiscustomercontainer">
							<select class="form-control" name="analysiscustomer">
								<option value="-1" selected="selected"><spring:message code="label.action.choose" text="Choose..." />
									<c:forEach items="${customers}" var="customer">
										<option value="${customer.id}">${customer.organisation}</option>
									</c:forEach>
							</select>
						</div>
					</div>
					<div class="form-group">
						<label for="language" class="col-sm-3 control-label"> <spring:message code="label.analysis.language" text="Language" />
						</label>
						<div class="col-sm-9">
							<select name="analysislanguage" class="form-control">
								<option value="-1" selected="selected"><spring:message code="label.action.choose" text="Choose..." />
									<c:forEach items="${languages}" var="language">
										<option value="${language.id}">${language.name}</option>
									</c:forEach>
							</select>
						</div>
					</div>

					<div class="form-group">
						<label for="scope" class="col-sm-3 control-label"> <spring:message code="label.analysis.scope" text="Scope" />
						</label>
						<div class="col-sm-9">
							<select class="form-control" name="analysiscustomer">
								<option value="-1" selected="selected"><spring:message code="label.action.choose" text="Choose..." />
									<c:forEach items="${customerAnalysis.keySet()}" var="customer">
										<option value="${customer.id}">${customer.organisation}</option>
									</c:forEach>
							</select>
						</div>
					</div>


					<div class="form-group">
						<label for="parameters" class="col-sm-3 control-label"> <spring:message code="label.analysis.parameters" text="Parameters" />
						</label>
						<div class="col-sm-9">
							<select class="form-control" name="analysiscustomer">
								<option value="-1" selected="selected"><spring:message code="label.action.choose" text="Choose..." />
									<c:forEach items="${customerAnalysis.keySet()}" var="customer">
										<option value="${customer.id}">${customer.organisation}</option>
									</c:forEach>
							</select> 
							
							<label for="parameters" class="control-label"> <spring:message code="label.analysis" text="Analysis" /></label>
							<select class="form-control" name="parameters">
								<option value="-1" selected="selected"><spring:message code="label.action.choose" text="Choose..." />
									<c:forEach items="${customerAnalysis.keySet()}" var="customer">
										<c:forEach items="${customerAnalysis[customer]}" var="analysis">
											<option value="${analysis.id}" name="${customer.id}">${analysis.label} - ${analysis.version}</option>
										</c:forEach>
									</c:forEach>
							</select>

						</div>
					</div>

					<div class="form-group">
						<label for="riskInformation" class="col-sm-3 control-label"> <spring:message code="label.analysis.risk_information" text="Risk information" />
						</label>
						<div class="col-sm-9">
							<select class="form-control" name="analysiscustomer">
								<option value="-1" selected="selected"><spring:message code="label.action.choose" text="Choose..." />
									<c:forEach items="${customerAnalysis.keySet()}" var="customer">
										<option value="${customer.id}">${customer.organisation}</option>
									</c:forEach>
							</select>
						</div>
					</div>

					<div class="form-group">
						<label for="assets" class="col-sm-3 control-label"> <spring:message code="label.analysis.assets" text="Assets" />
						</label>
						<div class="col-sm-9">
							<select class="form-control" name="analysiscustomer">
								<option value="-1" selected="selected"><spring:message code="label.action.choose" text="Choose..." />
									<c:forEach items="${customerAnalysis.keySet()}" var="customer">
										<option value="${customer.id}">${customer.organisation}</option>
									</c:forEach>
							</select>
						</div>
					</div>

					<div class="form-group">
						<label for="scenarios" class="col-sm-3 control-label"> <spring:message code="label.analysis.scenarios" text="Scenarios" />
						</label>
						<div class="col-sm-9">
							<select class="form-control" name="analysiscustomer">
								<option value="-1" selected="selected"><spring:message code="label.action.choose" text="Choose..." />
									<c:forEach items="${customerAnalysis.keySet()}" var="customer">
										<option value="${customer.id}">${customer.organisation}</option>
									</c:forEach>
							</select>
						</div>
					</div>

					<div class="form-group">
						<label for="assessment" class="col-sm-3 control-label"> <spring:message code="label.analysis.risk_estimation" text="Risk estimation" />
						</label>
						<div class="col-sm-9">
							<input type="checkbox" class="form-control" name="assessment" />
						</div>
					</div>

					<div class="form-group">
						<label for="standards" class="col-sm-3 control-label"> <spring:message code="label.analysis.standards" text="Standards" /></label>
						<div class="col-sm-9">
							<select class="form-control" name="analysiscustomer">
								<option value="-1" selected="selected"><spring:message code="label.action.choose" text="Choose..." />
									<c:forEach items="${customerAnalysis.keySet()}" var="customer">
										<option value="${customer.id}">${customer.organisation}</option>
									</c:forEach>
							</select>
						</div>
					</div>

					<div class="form-group">
						<label for="label" class="col-sm-3 control-label"> <spring:message code="label.analysis.description" text="Description" />
						</label>
						<div class="col-sm-9">
							<textarea name="label" class="form-control resize_vectical_only"></textarea>
						</div>
					</div>
					<div class="form-group">
						<label for="author" class="col-sm-3 control-label"> <spring:message code="label.analysis.author" text="Author" />
						</label>
						<div class="col-sm-9">
							<input type="text" class="form-control" name="author" value="${author}" />
						</div>
					</div>
					<div class="form-group">
						<label for="version" class="col-sm-3 control-label"> <spring:message code="label.analysis.version" text="Version" />
						</label>
						<div class="col-sm-9">
							<input name="version" class="form-control" type="text" value="0.0.1" />
						</div>
					</div>
				</form>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" name="save">
					<spring:message code="label.action.save" text="Save" />
				</button>
				<button type="button" class="btn btn-default" name="cancel">
					<spring:message code="label.action.cancel" text="Cancel" />
				</button>
			</div>
		</div>
	</div>
</div>