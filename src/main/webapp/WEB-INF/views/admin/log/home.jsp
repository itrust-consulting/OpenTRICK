<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<div id="tab_log" class="tab-pane" data-update-required="true" data-trigger="loadSystemLog" data-scroll-trigger="loadSystemLogScrolling">
	<div class="col-xs-3">
		<div data-spy="affix" class="col-xs-2">
			<strong class="col-xs-12" style="font-size: 14px; display: block; border-bottom: 2px solid #dddddd; margin: 8px; padding-bottom: 8px"> <spring:message
					code="label.title.control" text="Control" />
			</strong>
			<form name="logFilter" id="logFilterForm" class="form-horizontal">
				<div class="form-group">
					<label for="sort" class="col-sm-5 control-label"><spring:message code="label.action.filter.level" text="Filter by level" /></label>
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
					<label for="type" class="col-sm-5 control-label"> <spring:message code="label.action.filter.type" text="Filter by type" /></label>
					<div class="col-sm-7">
						<select name="type" class="form-control" onchange="updateLogFilter()">
							<option ${empty logFilter.type?'selected="selected"':''} value="ALL"><spring:message code="label.all" /></option>
							<c:forEach items="${logTypes}" var="logType">
								<option value="${logType}" ${logFilter.type == logType?'selected="selected"':''}><spring:message code="label.log.type.${fn:toLowerCase(logType)}"
										text="${fn:toLowerCase(logType)}" /></option>
							</c:forEach>
						</select>
					</div>
				</div>
				<div class="form-group">
					<label for="action" class="col-sm-5 control-label"> <spring:message code="label.action.filter.action" text="Filter by action" /></label>
					<div class="col-sm-7">
						<select name="action" class="form-control" onchange="updateLogFilter()">
							<option ${empty logFilter.action?'selected="selected"':''} value="ALL"><spring:message code="label.all" /></option>
							<c:forEach items="${actions}" var="action">
								<option value="${action}" ${logFilter.action == action?'selected="selected"':''}><spring:message code="label.log.action.${fn:toLowerCase(action)}"
										text="${fn:replace(fn:toLowerCase(action),'_',' ')}" /></option>
							</c:forEach>
						</select>
					</div>
				</div>
				<div class="form-group">
					<label for="author" class="col-sm-5 control-label"> <spring:message code="label.action.filter.author" text="Filter by author" /></label>
					<div class="col-sm-7">
						<select name="author" class="form-control" onchange="updateLogFilter()">
							<option ${empty logFilter.action?'selected="selected"':''} value="ALL"><spring:message code="label.all" /></option>
							<c:forEach items="${authors}" var="author">
								<option value="${author}" ${logFilter.author == author?'selected="selected"':''}><spring:message
										text="${author}" /></option>
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
					<label for="direction" class="col-sm-5 control-label"><spring:message code="label.action.sort_direction" text="Direction" /></label>
					<div class="col-sm-7">
						<div class="btn-group" data-toggle="buttons">
							<label class="btn btn-default ${logFilter.direction=='asc'? 'active':'' }"
								title='<spring:message
													code="label.action.sort_direction.ascending" text="Ascending" />'> <input type="radio" name="direction" value="asc"
								${logFilter.direction=='asc'? 'checked="checked"':'' } autocomplete="off" onchange="updateLogFilter(this)"><i class="fa fa-play fa-rotate-270"></i>
							</label> <label class="btn btn-default ${logFilter.direction=='desc'? 'active':'' }"
								title='<spring:message
													code="label.action.sort_direction.descending" text="Descending" />'> <input type="radio" name="direction" value="desc"
								${logFilter.direction=='desc'? 'checked="checked"':'' } autocomplete="off"  onchange="updateLogFilter(this)"><i class="fa fa-play fa-rotate-90"></i>
							</label>
						</div>
					</div>
				</div>
			</form>
			<div id="progress-trickLog" class="center-block" style="width: 60px">
				<i class="fa fa-spinner fa-pulse fa-5x fa-align-center fa-spin"></i>
			</div>
		</div>
	</div>
	<div class="col-xs-9">
		<div id="section_log"></div>
	</div>
</div>