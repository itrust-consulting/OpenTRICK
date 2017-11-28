<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<div id="tab-message" class="tab-pane" data-update-required="true" data-trigger="loadNotification">
	<div class='row' id="section_notification"></div>
	<a id='btn-add-notification' href="#" class='btn btn-link' style="position: fixed; bottom: 30px; right: 0px;" ><i class='fa fa-plus-circle fa-3x'></i></a>
</div>