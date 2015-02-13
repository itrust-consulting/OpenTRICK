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
						<strong class="col-xs-12" style="font-size: 14px; display: block; border-bottom: 2px solid #dddddd; margin: 8px; padding-bottom: 8px"> <spring:message
								code="label.title.control" text="Control" />
						</strong>
						<form class="form-horizontal">
							<div class="form-group">
								<label for="sort" class="col-sm-4 control-label">Sort</label>
								<div class="col-sm-8">
									<select name="sort" class="form-control">
										<option value="identifier">TRICK name</option>
										<option value="label">Name</option>
										<option value="version">Version</option>
										<option value="created">Created date</option>
										<option value="size">Size</option>
									</select>
								</div>
							</div>
							<div class="form-group">
								<label for="pageSize" class="col-sm-4 control-label"> <spring:message code="label.page.size" text="Page size" />
								</label>
								<div class="col-sm-8">
									<select name="pageSize" class="form-control">
										<option value="30">30</option>
										<option value="120">120</option>
										<option value=500>500</option>
										<option value="1000">1000</option>
										<option value="2000">2000</option>
									</select>
								</div>
							</div>
							<div class="form-group">
								<label for="filter" class="col-sm-4 control-label"> <spring:message code="label.filter" text="Filter" />
								</label>
								<div class="col-sm-8">
									<select name="filter" class="form-control">
										<option value="ALL">ALL</option>
										<option value="ENG_2014-04-10 10:08:55">ENG_2014-04-10 10:08:55</option>
										<option value="ENG_2014-04-10 10:08:56">ENG_2014-04-10 10:08:56</option>
										<option value="ENG_2014-04-10 10:08:57">ENG_2014-04-10 10:08:57</option>
										<option value="ENG_2014-04-10 10:08:58">ENG_2014-04-10 10:08:58</option>
										<option value="ENG_2014-04-10 10:08:59">ENG_2014-04-10 10:08:59</option>
									</select>
								</div>
							</div>
						</form>
					</div>
					<div class="col-xs-10" id="section_sqlite">
						<div class="center-block">
							<i class="fa fa-spinner fa-pulse fa-5x"></i>
						</div>
					</div>
				</div>
				<div id="report" class="tab-pane" data-update-required="true" data-trigger="loadUserReport">
					<div class="col-xs-2">
						<strong class="col-xs-12" style="font-size: 14px; display: block; border-bottom: 2px solid #dddddd; margin: 8px; padding-bottom: 8px"> <spring:message
								code="label.title.control" text="Control" />
						</strong>
						<form class="form-horizontal">
							<div class="form-group">
								<label for="sort" class="col-sm-4 control-label">Sort</label>
								<div class="col-sm-8">
									<select name="sort" class="form-control">
										<option value="identifier">TRICK name</option>
										<option value="label">Name</option>
										<option value="version">Version</option>
										<option value="created">Created date</option>
										<option value="size">Size</option>
									</select>
								</div>
							</div>
							<div class="form-group">
								<label for="pageSize" class="col-sm-4 control-label"> <spring:message code="label.page.size" text="Page size" />
								</label>
								<div class="col-sm-8">
									<select name="pageSize" class="form-control">
										<option value="30">30</option>
										<option value="120">120</option>
										<option value=500>500</option>
										<option value="1000">1000</option>
										<option value="2000">2000</option>
									</select>
								</div>
							</div>
							<div class="form-group">
								<label for="filter" class="col-sm-4 control-label"> <spring:message code="label.filter" text="Filter" />
								</label>
								<div class="col-sm-8">
									<select name="filter" class="form-control">
										<option value="ALL">ALL</option>
										<option value="ENG_2014-04-10 10:08:55">ENG_2014-04-10 10:08:55</option>
										<option value="ENG_2014-04-10 10:08:56">ENG_2014-04-10 10:08:56</option>
										<option value="ENG_2014-04-10 10:08:57">ENG_2014-04-10 10:08:57</option>
										<option value="ENG_2014-04-10 10:08:58">ENG_2014-04-10 10:08:58</option>
										<option value="ENG_2014-04-10 10:08:59">ENG_2014-04-10 10:08:59</option>
									</select>
								</div>
							</div>
						</form>
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