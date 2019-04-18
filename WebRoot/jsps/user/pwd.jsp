<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>修改密码</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/css/user/pwd.css">
	<script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery-1.5.1.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath() %>/js/pwd.js"></script>
	
  </head>
  
  <body>
    <div class="div0">
    	<span>修改密码</span>
    </div>

	<div class="div1">
		<form action="<%=request.getContextPath() %>/UserServlet" method="post" target="_top">
			<input type="hidden" name="method" value="updatePassword"/>
		<table>
			<tr>
				<td><label class="error">${msg }</label></td>
				<td colspan="2">&nbsp;</td>
			</tr>
			<tr>
				<td align="right">原密码:</td>
				<td><input class="input" type="password" name="loginpass" id="loginpass" value="${user.loginpass }"/></td>
				<td><label id="loginpassError" class="error">${errors.loginpass }</label></td>
			</tr>
			<tr>
				<td align="right">新密码:</td>
				<td><input class="input" type="password" name="newloginpass" id="newloginpass" value="${user.newloginpass }"/></td>
				<td><label id="newpassError" class="error">${errors.newloginpass }</label></td>
			</tr>
			<tr>
				<td align="right">确认密码:</td>
				<td><input class="input" type="password" name="reloginpass" id="reloginpass" value="${user.reloginpass }"/></td>
				<td><label id="reloginpassError" class="error">${errors.reloginpass }</label></td>
			</tr>
			<tr>
				<td align="right"></td>
				<td>
				  <img id="vCode" src="<%=request.getContextPath() %>/VerifyCodeServlet" border="1"/>
		    	  <a href="javascript:_change();" id="verifycode">看不清，换一张</a>
				</td>
			</tr>
			<tr>
				<td align="right">验证码:</td>
				<td>
				  <input class="input" type="text" name="verifyCode" id="verifyCode" value="${user.verifyCode }""/>
				</td>
				<td><label id="verifyCodeError" class="error">${errors.verifyCode }</label></td>
			</tr>
			<tr>
				<td align="right"></td>
				<td><input id="submit" type="submit" value="修改密码"/></td>
			</tr>
		</table>
		</form>
	</div>
  </body>
</html>
