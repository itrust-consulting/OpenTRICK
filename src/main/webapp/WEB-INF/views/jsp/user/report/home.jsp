<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<!-- report -->
<div id="tab-report" class="tab-pane" data-update-required="true" data-trigger="loadUserReport" data-scroll-trigger="userReportScrolling">
	<div class="col-md-3 col-lg-2 nav-left-affix" role="left-menu">
		<strong><spring:message code="label.title.control" text="Control" /></strong>
		<form class="form-horizontal" name="reportControl" id="formReportControl">
			<div class="form-group">
				<label for="pageSize" class="col-sm-4 control-label"> <spring:message code="label.page.size" text="Page size" />
				</label>
				<div class="col-sm-8">
					<select name="size" class="form-control" onchange="updateReportControl()" id="reportPageSize">
						<option value="30" ${reportControl.size == 30?'selected':''}>30</option>
						<option value="120" ${reportControl.size == 120?'selected':''}>120</option>
						<option value=500 ${reportControl.size == 500?'selected':''}>500</option>
						<option value="1000" ${reportControl.size == 1000?'selected':''}>1000</option>
						<option value="2000" ${reportControl.size == 2000?'selected':''}>2000</option>
					</select>
				</div>
			</div>
			<div class="form-group">
				<label for="filter" class="col-sm-4 control-label"> <spring:message code="label.action.show" text="Show" /></label>
				<div class="col-sm-8">
					<select name="filter" class="form-control" onchange="updateReportControl()">
						<option value="ALL" ${reportControl.filter == 'ALL'?'selected="selected"':''}><spring:message code="label.all" text="All" /></option>
						<c:forEach items="${reportIdentifiers}" var="reportIdentifier">
							<option value='<spring:message text="${reportIdentifier}"/>' ${reportControl.filter == reportIdentifier ?'selected':''}><spring:message text="${reportIdentifier}" /></option>
						</c:forEach>
					</select>
				</div>
			</div>
			<div class="form-group">
				<label for="sort" class="col-sm-4 control-label"><spring:message code="label.action.sort.over" text="Sort by" /></label>
				<div class="col-sm-8">
					<select name="sort" class="form-control" onchange="updateReportControl()">
						<option value="identifier" ${reportControl.sort == 'identifier'?'selected':''}><spring:message code="label.analysis.identifier" text="TRICK name" /></option>
						<option value="label" ${reportControl.sort == 'label'?'selected':''}><spring:message code="label.analysis.label" text="Name" /></option>
						<option value="version" ${reportControl.sort == 'version'?'selected':''}><spring:message code="label.analysis.version" text="Version" /></option>
						<option value="created" ${reportControl.sort == 'created'?'selected':''}><spring:message code="label.date.created" text="Created date" /></option>
						<option value="length" ${reportControl.sort == 'length'?'selected':''}><spring:message code="label.file.size" text="Size" /></option>
					</select>
				</div>
			</div>
			<div class="form-group">
				<label for="direction" class="col-sm-4 control-label"><spring:message code="label.action.sort" text="Sort" /></label>
				<div class="col-sm-8">
					<div class="btn-group" data-toggle="buttons">
						<label class="btn btn-default ${reportControl.direction=='asc'? 'active':'' }" title='<spring:message
													code="label.sort_direction.ascending" text="Ascending" />'>
							<input type="radio" name="direction" autocomplete="off" ${reportControl.direction=='asc'? 'checked':'' } value="asc" onchange="updateReportControl(this)"><i
							class="fa fa-play fa-rotate-270"></i>
						</label> <label class="btn btn-default ${reportControl.direction=='desc'? 'active':'' }"
							title='<spring:message
													code="label.sort_direction.descending" text="Descending" />'> <input type="radio" name="direction"
							${reportControl.direction=='desc'? 'checked':'' } autocomplete="off" value="desc" onchange="updateReportControl(this)"><i class="fa fa-play fa-rotate-90"></i>
						</label>
					</div>
				</div>
			</div>


		</form>
		<div id="progress-report" class="center-block" style="width: 60px">
			<i class="fa fa-spinner fa-pulse fa-5x fa-align-center fa-spin"></i>
		</div>
	</div>
	<div class="col-md-9 col-lg-10" id="section_report"></div>
</div>