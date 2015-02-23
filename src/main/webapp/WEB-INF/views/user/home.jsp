<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:set scope="request" var="title">label.title.profile</c:set>
<!DOCTYPE html>
<html>
<jsp:include page="../template/header.jsp" />
<body>
	<div id="wrap">
		<jsp:include page="../template/menu.jsp" />
		<div class="container">
			<ul class="nav nav-tabs affix affix-top col-xs-12 nav-tab">
				<li class="active"><a href="#profile" data-toggle="tab"><spring:message code="label.menu.profile" text="My Profile" /></a></li>
				<li><a href="#sqlite" data-toggle="tab"><spring:message code="label.menu.sqlite" text="My sqlites" /></a></li>
				<li><a href="#report" data-toggle="tab"><spring:message code="label.menu.report" text="My reports" /></a></li>
			</ul>
			<div class="tab-content" id="tab-container">
				<jsp:include page="profile.jsp" />
				<div id="sqlite" class="tab-pane" data-update-required="true" data-trigger="loadUserSqlite">
					<div class="col-xs-2">
						<div class="affix">
							<strong class="col-xs-12" style="font-size: 14px; display: block; border-bottom: 2px solid #dddddd; margin: 8px; padding-bottom: 8px"> <spring:message
									code="label.title.control" text="Control" />
							</strong>
							<form class="form-horizontal" name="sqliteControl">
								<div class="form-group">
									<label for="sort" class="col-sm-4 control-label"><spring:message code="label.action.sort" text="Sort" /></label>
									<div class="col-sm-8">
										<select name="sort" class="form-control" onchange="updateSqliteControl()">
											<option value="identifier" ${sqliteControl.sort == 'identifier'?'selected="selected"':''}><spring:message code="label.analysis.identifier" text="TRICK name" /></option>
											<option value="label" ${sqliteControl.sort == 'label'?'selected="selected"':''}><spring:message code="label.analysis.label" text="Name" /></option>
											<option value="version" ${sqliteControl.sort == 'version'?'selected="selected"':''}><spring:message code="label.analysis.version" text="Version" /></option>
											<option value="created" ${sqliteControl.sort == 'created'?'selected="selected"':''}><spring:message code="label.date.created" text="Created date" /></option>
											<option value="size" ${sqliteControl.sort == 'size'?'selected="selected"':''}><spring:message code="label.file.size" text="Size" /></option>
										</select>
									</div>
								</div>
								<div class="form-group">
									<label for="direction" class="col-sm-4 control-label"><spring:message code="label.action.sort_direction" text="Direction" /></label>
									<div class="col-sm-8">
										<div class="btn-group" data-toggle="buttons">
											<label class="btn btn-default ${sqliteControl.direction=='asc'? 'active':'' }" title='<spring:message
													code="label.action.sort_direction.ascending" text="Ascending" />'> <input type="radio"
												name="direction" id="sliteSortDirectionAsc" autocomplete="off" onchange="updateSqliteControl(this)"><i class="fa fa-play fa-rotate-270"></i>
											</label> <label class="btn btn-default ${sqliteControl.direction=='desc'? 'active':'' }" title='<spring:message
													code="label.action.sort_direction.descending" text="Descending" />'> <input type="radio"
												name="direction" id="sliteSortDirectionDesc" autocomplete="off" onchange="updateSqliteControl(this)"><i class="fa fa-play fa-rotate-90"></i>
											</label>
										</div>
									</div>
								</div>
								<div class="form-group">
									<label for="pageSize" class="col-sm-4 control-label"> <spring:message code="label.page.size" text="Page size" />
									</label>
									<div class="col-sm-8">
										<select name="pageSize" class="form-control" onchange="updateSqliteControl()">
											<option value="30" ${sqliteControl.size == 30?'selected="selected"':''}>30</option>
											<option value="120" ${sqliteControl.size == 120?'selected="selected"':''}>120</option>
											<option value=500 ${sqliteControl.size == 500?'selected="selected"':''}>500</option>
											<option value="1000" ${sqliteControl.size == 1000?'selected="selected"':''}>1000</option>
											<option value="2000" ${sqliteControl.size == 2000?'selected="selected"':''}>2000</option>
										</select>
									</div>
								</div>
								<div class="form-group">
									<label for="filter" class="col-sm-4 control-label"> <spring:message code="label.filter" text="Filter" />
									</label>
									<div class="col-sm-8">
										<select name="filter" class="form-control" onchange="updateSqliteControl()">
											<option value="ALL" ${sqliteControl.filter == 'ALL'?'selected="selected"':''}><spring:message code="label.all" text="All" /></option>
											<c:forEach items="${sqliteIdentifiers}" var="sqliteIdentifier">
												<option value='<spring:message text="${sqliteIdentifier}"/>' ${sqliteControl.filter == sqliteIdentifier ?'selected="selected"':''}><spring:message
														text="${sqliteIdentifier}" /></option>
											</c:forEach>
										</select>
									</div>
								</div>
							</form>
						</div>
					</div>
					<div class="col-xs-10" id="section_sqlite">
						<div class="center-block">
							<i class="fa fa-spinner fa-pulse fa-5x"></i>
						</div>
					</div>
				</div>
				<div id="report" class="tab-pane row" data-update-required="true" data-trigger="loadUserReport">
					<div class="col-xs-2">
						<div class="affix">
							<strong class="col-xs-12" style="font-size: 14px; display: block; border-bottom: 2px solid #dddddd; margin: 8px; padding-bottom: 8px"> <spring:message
									code="label.title.control" text="Control" />
							</strong>
							<form class="form-horizontal" name="reportControl">
								<div class="form-group">
									<label for="sort" class="col-sm-4 control-label"><spring:message code="label.action.sort" text="Sort" /></label>
									<div class="col-sm-8">
										<select name="sort" class="form-control" onchange="updateReportControl()">
											<option value="identifier" ${reportControl.sort == 'identifier'?'selected="selected"':''}><spring:message code="label.analysis.identifier" text="TRICK name" /></option>
											<option value="label" ${reportControl.sort == 'label'?'selected="selected"':''}><spring:message code="label.analysis.label" text="Name" /></option>
											<option value="version" ${reportControl.sort == 'version'?'selected="selected"':''}><spring:message code="label.analysis.version" text="Version" /></option>
											<option value="created" ${reportControl.sort == 'created'?'selected="selected"':''}><spring:message code="label.date.created" text="Created date" /></option>
											<option value="size" ${reportControl.sort == 'size'?'selected="selected"':''}><spring:message code="label.file.size" text="Size" /></option>
										</select>
									</div>
								</div>
								<div class="form-group">
									<label for="direction" class="col-sm-4 control-label"><spring:message code="label.action.sort_direction" text="Direction" /></label>
									<div class="col-sm-8">
										<div class="btn-group" data-toggle="buttons">
											<label class="btn btn-default ${reportControl.direction=='asc'? 'active':'' }" title='<spring:message
													code="label.action.sort_direction.ascending" text="Ascending" />'> <input type="radio"
												name="direction" id="reportSortDirectionAsc" autocomplete="off" onchange="updateReportControl(this)"><i class="fa fa-play fa-rotate-270"></i>
											</label> <label class="btn btn-default ${reportControl.direction=='desc'? 'active':'' }" title='<spring:message
													code="label.action.sort_direction.descending" text="Descending" />'> <input type="radio"
												name="direction" id="reportSortDirectionDesc" autocomplete="off" onchange="updateReportControl(this)"><i class="fa fa-play fa-rotate-90"></i>
											</label>
										</div>
									</div>
								</div>
								<div class="form-group">
									<label for="pageSize" class="col-sm-4 control-label"> <spring:message code="label.page.size" text="Page size" />
									</label>
									<div class="col-sm-8">
										<select name="size" class="form-control" onchange="updateReportControl()">
											<option value="30" ${reportControl.size == 30?'selected="selected"':''}>30</option>
											<option value="120" ${reportControl.size == 120?'selected="selected"':''}>120</option>
											<option value=500 ${reportControl.size == 500?'selected="selected"':''}>500</option>
											<option value="1000" ${reportControl.size == 1000?'selected="selected"':''}>1000</option>
											<option value="2000" ${reportControl.size == 2000?'selected="selected"':''}>2000</option>
										</select>
									</div>
								</div>
								<div class="form-group">
									<label for="filter" class="col-sm-4 control-label"> <spring:message code="label.filter" text="Filter" />
									</label>
									<div class="col-sm-8">
										<select name="filter" class="form-control" onchange="updateReportControl()">
											<option value="ALL" ${reportControl.filter == 'ALL'?'selected="selected"':''}><spring:message code="label.all" text="All" /></option>
											<c:forEach items="${reportIdentifiers}" var="reportIdentifier">
												<option value='<spring:message text="${reportIdentifier}"/>' ${reportControl.filter == reportIdentifier ?'selected="selected"':''}><spring:message
														text="${reportIdentifier}" /></option>
											</c:forEach>
										</select>
									</div>
								</div>
							</form>
						</div>
					</div>
					<div class="col-xs-10" id="section_report">
						<div class="center-block" style="margin-top:">
							<i class="fa fa-spinner fa-pulse fa-5x"></i>
						</div>
					</div>
				</div>
			</div>
		</div>
		<jsp:include page="../template/footer.jsp" />
		<jsp:include page="../template/scripts.jsp" />
		<script type="text/javascript" src="<spring:url value="/js/trickservice/profile.js" />"></script>
	</div>
</body>
<!-- ################################################################### End HTML ################################################################### -->
</html>