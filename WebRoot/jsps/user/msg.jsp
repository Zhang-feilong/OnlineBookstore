<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>信息板</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/user/msg.css" />
  </head>
  <body>
  <c:choose>
  	<c:when test="${code eq 'success'}"><%--如果code是success，它显示对号图片 --%>
  		<c:set var="img" value="images/duihao.jpg"/>
  		<c:set var="title" value="成功" />
  	</c:when>
  	<c:when test="${code eq 'error'}"><%--如果code是error，它显示错号图片 --%>
  		<c:set var="img" value="images/cuohao.png"/>
  		<c:set var="title" value="失败"/>
  	</c:when>	
  </c:choose>
<div class="divBody">
	<div class="divTitle">
		<span class="spanTitle">${title }</span>
	</div>
	<div class="divContent">
	  <div style="margin: 20px;">
		<img style="float: left; margin-right: 30px;" src="<c:url value='${img }'/>" width="150"/>
		<span style="font-size: 30px; color: #c30; font-weight: 900;">${msg }</span>
		<br/>
		<br/>
		<br/>
		<br/>
		<span style="margin-left: 50px;"><a target="_top" href="<c:url value='/jsps/user/login.jsp'/>">登录</a></span>
		<span style="margin-left: 50px;"><a target="_top" href="<c:url value='/index.jsp'/>">主页</a></span>
	  </div>
	</div>
</div>
  </body>
</html>
