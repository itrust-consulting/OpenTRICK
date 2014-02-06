<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title><spring:message code="${title}" text="TRICK Service" /></title>
<!--<link rel="stylesheet" type="text/css" href='<spring:url value="/css/jquery-ui.css" />' /> -->
<link rel="stylesheet" type="text/css" href='<spring:url value="/css/jquery-ui.min.css" />' />
<link rel="stylesheet" type="text/css" href='<spring:url value="/js/datatables/media/css/jquery.dataTables.css" />' />
<link rel="stylesheet" type="text/css" href='<spring:url value="/css/bootstrap.min.css" />' />
<link rel="stylesheet" type="text/css" href='<spring:url value="/css/bootstrap-theme.min.css" />' />
<link rel="stylesheet" type="text/css" href='<spring:url value="/css/theme.bootstrap.css" />' /> 
<link rel="stylesheet" type="text/css" href='<spring:url value="/css/main.css" />' />
<script type="text/javascript">
<!--
	var context = '${pageContext.request.contextPath}';
	-->
</script>
<script type="text/javascript">
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

</head>
