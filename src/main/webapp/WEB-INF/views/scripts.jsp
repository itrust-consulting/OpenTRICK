<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<script src="<spring:url value="/js/jquery-2.0.js" />"></script>
<script src="<spring:url value="/js/jquery-ui.js" />"></script>
<script src="<spring:url value="/js/jquery.tablesorter.min.js" />"></script>
<script src="<spring:url value="/js/jquery.tablesorter.widgets.js" />"></script>
<script src="<spring:url value="/js/jquery.tablesorter.pager.js" />"></script>
<script src="<spring:url value="/js/jquery.fileDownload.js" />"></script>
<script src="<spring:url value="/js/bootstrap.min.js" />"></script>
<script src="<spring:url value="/js/bootbox.min.js" />"></script>
<script src="<spring:url value="/js/dom-parser.js" />"></script>
<script src="<spring:url value="/js/highstock.js" />"></script>
<script src="<spring:url value="/js/highcharts-more.js" />"></script>
<script src="<spring:url value="/js/modules/exporting.js" />"></script>
<script src="<spring:url value="/js/main.js" />"></script>
<script src="<spring:url value="/js/datatables/media/js/jquery.dataTables.min.js" />"></script>
<script type="text/javascript">
	<sec:authorize ifNotGranted="ROLE_ANONYMOUS" >
	<!--
	var taskManager = new TaskManager();
	taskManager.Start();
	-->
	</sec:authorize>
	$(document).ready(function() {
	        // Tooltip only Text
	        $(".masterTooltip").hover(function(){
	                // Hover over code
	                var title = $(this).attr('title');
	                $(this).data('tipText', title).removeAttr('title');
	                $('<p class="tooltip"></p>')
	                .text(title)
	                .appendTo('body')
	                .fadeIn('slow');
	        }, function() {
	                // Hover out code
	                $(this).attr('title', $(this).data('tipText'));
	                $('.tooltip').remove();
	        }).mousemove(function(e) {
	                var mousex = e.pageX + 20; //Get X coordinates
	                var mousey = e.pageY + 10; //Get Y coordinates
	                $('.tooltip')
	                .css({ top: mousey, left: mousex })
	        });
	});
</script>
<jsp:include page="alertDialog.jsp"/>
