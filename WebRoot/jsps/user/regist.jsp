<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>用户注册</title>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/user/regist.css" />
</head>
<body>
	<div id="div_main">
		<div id="div_title">
			<span>新用户注册</span>
		</div>
		<div id="div_table">
		<form action="<%=request.getContextPath()%>/UserServlet" method="post" id="registform">
		<input type="hidden" name="method" value="regist" />
			<table>
				<tr>
					<td class="tdtext">用户名：</td>
					<td class="tdinput">
						<input type="text" name="loginname" id="loginname" class="inputclass" value="${form.loginname }" />
					</td>
					<td class="tderror">
						<lable class="errorclass" id="loginnameerror">${errors.loginname }</lable>
					</td>
				</tr>
				<tr>
					<td class="tdtext">登录密码：</td>
					<td>
						<input type="password" name="loginpass" id="loginpass" class="inputclass" value="${form.loginpass }" />
					</td>
					<td>
						<lable class="errorclass" id="loginpasserror">${errors.loginpass }</lable>
					</td>
				</tr>
				<tr>
					<td class="tdtext">确认密码：</td>
					<td>
						<input type="password" name="reloginpass" id="reloginpass" class="inputclass" value="${form.reloginpass }" />
					</td>
					<td>
						<lable class="errorclass" id="reloginpasserror">${errors.reloginpass }</lable>
					</td>
				</tr>
				<tr>
					<td class="tdtext">Email:</td>
					<td>
						<input type="text" name="email" id="email" class="inputclass" value="${form.email }" />
					</td>
					<td>
						<lable class="errorclass" id="emailerror">${errors.email }</lable>
					</td>
				</tr>
				<tr>
					<td class="tdtext">验证码：</td>
					<td>
						<input type="text" name="verifyCode" id="verifyCode" class="inputclass" value="${form.verifyCode }" />
					</td>
					<td>
						<lable class="errorclass" id="verifyCodeerror">${errors.verifyCode }</lable>
					</td>
				</tr>
				<tr>
					<td></td>
					<td class="inputclass">
						<div id="imgVerify">
							<img src="<%=request.getContextPath()%>/VerifyCodeServlet" id="imgVerifyCode" />
						</div>
					</td>
					<td>
						<lable>
							<a id="huan" href="javascript:_hyz()">换一张</a>
						</lable>
					</td>
				</tr>
				<tr>
					<td></td>
					<td>
						<input type="image" src="<%=request.getContextPath()%>/images/regist1.jpg" id="submitBtn" />
					</td>
					<td></td>
				</tr>
			</table>
		</form>
		</div>
	</div>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-1.5.1.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/regist.js"></script>
</body>
</html>