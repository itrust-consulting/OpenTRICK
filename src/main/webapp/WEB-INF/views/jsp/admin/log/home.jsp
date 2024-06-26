<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<div id="tab-log" class="tab-pane" data-update-required="true" data-trigger="loadSystemLog" data-scroll-trigger="loadSystemLogScrolling">
	<div class="col-md-3 col-lg-2 nav-left-affix" role="left-menu">
		<strong><spring:message code="label.title.control" text="Control" /></strong>
		<form name="logFilter" id="logFilterForm" class="form-horizontal">
			<div class="form-group">
				<label for="sort" class="col-sm-5 control-label"><spring:message code="label.filter.level" text="Filter by level" /></label>
				<div class="col-sm-7">
					<select name="level" class="form-control" onchange="updateLogFilter()">
						<option ${empty logFilter.level?'selected="selected"':''} value="ALL"><spring:message code="label.all" /></option>
						<c:forEach items="${logLevels}" var="logLevel">
							<option value="${logLevel}" ${logFilter.level == logLevel?'selected="selected"':''}><spring:message code="label.log.level.${fn:toLowerCase(logLevel)}"
									text="${fn:toLowerCase(logLevel)}" /></option>
						</c:forEach>
					</select>
				</div>
			</div>
			<div class="form-group">
				<label for="type" class="col-sm-5 control-label"> <spring:message code="label.filter.type" text="Filter by type" /></label>
				<div class="col-sm-7">
					<select name="type" class="form-control" onchange="updateLogFilter()">
						<option ${empty logFilter.type?'selected="selected"':''} value="ALL"><spring:message code="label.all" /></option>
						<c:forEach items="${logTypes}" var="logType">
							<option value="${logType}" ${logFilter.type == logType?'selected="selected"':''}><spring:message code="label.log.type.${fn:toLowerCase(logType)}"
									text="${fn:replace(fn:toLowerCase(logType),'_',' ')}" /></option>
						</c:forEach>
					</select>
				</div>
			</div>
			<div class="form-group">
				<label for="action" class="col-sm-5 control-label"> <spring:message code="label.filter.action" text="Filter by action" /></label>
				<div class="col-sm-7">
					<select name="action" class="form-control" onchange="updateLogFilter()">
						<option ${empty logFilter.action?'selected="selected"':''} value="ALL"><spring:message code="label.all" /></option>
						<c:forEach items="${actions}" var="action">
							<option value="${action}" ${logFilter.action == action?'selected="selected"':''}><spring:message code="label.action.${fn:toLowerCase(action)}"
									text="${fn:replace(fn:toLowerCase(action),'_',' ')}" /></option>
						</c:forEach>
					</select>
				</div>
			</div>
			<div class="form-group">
				<label for="author" class="col-sm-5 control-label"> <spring:message code="label.filter.author" text="Filter by author" /></label>
				<div class="col-sm-7">
					<select name="author" class="form-control" onchange="updateLogFilter()">
						<option ${empty logFilter.action?'selected="selected"':''} value="ALL"><spring:message code="label.all" /></option>
						<c:forEach items="${authors}" var="author">
							<option value="${author}" ${logFilter.author == author?'selected="selected"':''}><spring:message text="${author}" /></option>
						</c:forEach>
					</select>
				</div>
			</div>
			<div class="form-group">
				<label for="pageSize" class="col-sm-5 control-label"> <spring:message code="label.page.size" text="Page size" />
				</label>
				<div class="col-sm-7">
					<select name="size" class="form-control" id="logFilterPageSize" onchange="updateLogFilter()">
						<option value="60" ${logFilter.size == 60?'selected="selected"':''}>60</option>
						<option value="120" ${logFilter.size == 120?'selected="selected"':''}>120</option>
						<option value="250" ${logFilter.size == 250?'selected="selected"':''}>250</option>
						<option value=500 ${logFilter.size == 500?'selected="selected"':''}>500</option>
						<option value="1000" ${logFilter.size == 1000?'selected="selected"':''}>1000</option>
						<option value="2000" ${logFilter.size == 2000?'selected="selected"':''}>2000</option>
					</select>
				</div>
			</div>
			<div class="form-group">
				<label for="direction" class="col-sm-5 control-label"><spring:message code="label.action.sort" text="Sort" /></label>
				<div class="col-sm-7">
					<div class="btn-group" data-toggle="buttons">
						<label class="btn btn-default ${logFilter.direction=='asc'? 'active':'' }" title='<spring:message
													code="label.sort_direction.ascending" text="Ascending" />'>
							<input type="radio" name="direction" value="asc" ${logFilter.direction=='asc'? 'checked="checked"':'' } autocomplete="off" onchange="updateLogFilter(this)"><i
							class="fa fa-play fa-rotate-270"></i>
						</label> <label class="btn btn-default ${logFilter.direction=='desc'? 'active':'' }" title='<spring:message
													code="label.sort_direction.descending" text="Descending" />'>
							<input type="radio" name="direction" value="desc" ${logFilter.direction=='desc'? 'checked="checked"':'' } autocomplete="off" onchange="updateLogFilter(this)"><i
							class="fa fa-play fa-rotate-90"></i>
						</label>
					</div>
				</div>
			</div>
		</form>
		<!-- <div id="progress-trickLog" class="center-block" style="width: 60px">
			<i class="fa fa-spinner fa-pulse fa-5x fa-align-center fa-spin"></i>
		</div> -->
	</div>
	<div class="col-md-9 col-lg-10" id="section_log"></div>

</div>