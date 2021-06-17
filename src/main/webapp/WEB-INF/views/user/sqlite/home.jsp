<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!-- sqlite -->
<div id="tab-sqlite" class="tab-pane" data-update-required="true" data-trigger="loadUserSqlite" data-scroll-trigger="userSqliteScrolling">
	<div class="col-md-3 col-lg-2 nav-left-affix" role="left-menu">
		<strong><spring:message
				code="label.title.control" text="Control" /></strong>
		<form class="form-horizontal" name="sqliteControl" id="formSqliteControl">

			<div class="form-group">
				<label for="pageSize" class="col-sm-4 control-label"> <spring:message code="label.page.size" text="Page size" />
				</label>
				<div class="col-sm-8">
					<select name="size" class="form-control" onchange="updateSqliteControl()" id="sqlitePageSize">
						<option value="30" ${sqliteControl.size == 30?'selected':''}>30</option>
						<option value="120" ${sqliteControl.size == 120?'selected':''}>120</option>
						<option value=500 ${sqliteControl.size == 500?'selected':''}>500</option>
						<option value="1000" ${sqliteControl.size == 1000?'selected':''}>1000</option>
						<option value="2000" ${sqliteControl.size == 2000?'selected':''}>2000</option>
					</select>
				</div>
			</div>

			<div class="form-group">
				<label for="filter" class="col-sm-4 control-label"> <spring:message code="label.action.show" text="Show" />
				</label>
				<div class="col-sm-8">
					<select name="filter" class="form-control" onchange="updateSqliteControl()">
						<option value="ALL" ${sqliteControl.filter == 'ALL'?'selected="selected"':''}><spring:message code="label.all" text="All" /></option>
						<c:forEach items="${sqliteIdentifiers}" var="sqliteIdentifier">
							<option value='<spring:message text="${sqliteIdentifier}"/>' ${sqliteControl.filter == sqliteIdentifier ?'selected':''}><spring:message text="${sqliteIdentifier}" /></option>
						</c:forEach>
					</select>
				</div>
			</div>

			<div class="form-group">
				<label for="sort" class="col-sm-4 control-label"><spring:message code="label.action.sort.over" text="Sort by" /></label>
				<div class="col-sm-8">
					<select name="sort" class="form-control" onchange="updateSqliteControl()">
						<option value="identifier" ${sqliteControl.sort == 'identifier'?'selected':''}><spring:message code="label.analysis.identifier" text="TRICK name" /></option>
						<option value="label" ${sqliteControl.sort == 'label'?'selected':''}><spring:message code="label.analysis.label" text="Name" /></option>
						<option value="version" ${sqliteControl.sort == 'version'?'selected':''}><spring:message code="label.analysis.version" text="Version" /></option>
						<option value="created" ${sqliteControl.sort == 'created'?'selected':''}><spring:message code="label.date.created" text="Created date" /></option>
						<option value="size" ${sqliteControl.sort == 'size'?'selected':''}><spring:message code="label.file.size" text="Size" /></option>
					</select>
				</div>
			</div>

			<div class="form-group">
				<label for="direction" class="col-sm-4 control-label"><spring:message code="label.action.sort" text="Sort" /></label>
				<div class="col-sm-8">
					<div class="btn-group" data-toggle="buttons">
						<label class="btn btn-default ${sqliteControl.direction=='asc'? 'active':'' }" title='<spring:message
													code="label.sort_direction.ascending" text="Ascending" />'>
							<input type="radio" name="direction" value="asc" ${sqliteControl.direction=='asc'? 'checked':'' } autocomplete="off" onchange="updateSqliteControl(this)"><i
							class="fa fa-play fa-rotate-270"></i>
						</label> <label class="btn btn-default ${sqliteControl.direction=='desc'? 'active':'' }"
							title='<spring:message
													code="label.sort_direction.descending" text="Descending" />'> <input type="radio" name="direction" value="desc"
							${sqliteControl.direction=='desc'? 'checked':'' } autocomplete="off" onchange="updateSqliteControl(this)"><i class="fa fa-play fa-rotate-90"></i>
						</label>
					</div>
				</div>
			</div>

		</form>
		<div id="progress-sqlite" class="center-block" style="width: 60px">
			<i class="fa fa-spinner fa-pulse fa-5x fa-align-center fa-spin"></i>
		</div>
	</div>
	<div class="col-md-9 col-lg-10" id="section_sqlite"></div>
</div>
