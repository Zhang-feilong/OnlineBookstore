<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>会员登录</title>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/user/login.css" />
</head>
<body>
<div class="main">
	  <div id="top">
	  	<div id="top_content">
	  		<img id="logoimg" src="<%=request.getContextPath() %>/images/logo.gif" />
	  	</div>
	  </div>
	  <div class="content">
	  	<div id="img_content">
	  		<img alt="" src="<%=request.getContextPath() %>/images/zj.png" />
	  	</div>
	  	<div class="right_content">
	  		<div>
	  			<p id="title">绿荫轩会员登录</p>
	  			<p>
                <a href="<%=request.getContextPath() %>/jsps/user/regist.jsp" class="registBtn"></a>
              </p>
	  		</div>
	  		<div class="table_content">
	  		 <form target="_top" action="<%=request.getContextPath() %>/UserServlet" method="post" id="loginform">
                <input type="hidden" name="method" value="login" />
	  			<table>
	  			<tr>
                      <td width="50"></td>
                      <td><label class="error" id="msg">${msg }</label></td>
                    </tr>
	  				<tr>
	  					<td width="50">用户名</td>
	  					<td>
	  						<input type="text" name="loginname" id="loginname" class="inputclass" value="${user.loginname }" />
	  					</td>
	  				</tr>
	  				<tr>
                      <td height="20">&nbsp;</td>
                      <td><label id="loginnameerror" class="error">${errors.loginname }</label></td>
                    </tr>
	  				<tr>
	  					<td>密 &nbsp; 码</td>
	  					<td>
	  						<input type="password" name="loginpass" id="loginpass" class="inputclass" value="${user.loginpass }" />
	  					</td>
	  				</tr>
	  				<td height="20">&nbsp;</td>
                      <td><label id="loginpasserror" class="error">${errors.loginpass }</label></td>
                    </tr>
	  				<tr>
	  					<td>验证码</td>
                      <td>
                        <input class="inputclass yzm" type="text" name="verifyCode" id="verifyCode" value="${user.verifyCode }"/>
                        <img id="vCode" src="<%=request.getContextPath() %>/VerifyCodeServlet"/>
                        <a id="verifyCode" href="javascript:_change()">换一张</a>
                      </td>
                    </tr>
	  				<tr>
	  					<td height="20px">&nbsp;</td>
                      <td><label id="verifyCodeerror" class="error">${errors.verifyCode }</label></td>
	  				</tr>
	  				<tr>
                      <td>&nbsp;</td>
                      <td align="left">
                        <input type="image" id="submit" src="<%=request.getContextPath() %>/images/login1.jpg" class="loginBtn"/>
                      </td>
                    </tr>		
	  			</table>
	  		</form>
	  		</div>
	  	</div>
	  </div>
</div>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-1.5.1.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/login.js"></script>
<script type="text/javascript">
	$(function(){
	/*Map<String(Cookie名称),Cookie(Cookie本身)>*/
		var loginname =window.decodeURI("${cookie.loginname.value}");
		if("${requestScope.user.loginname}"){
		loginname ="${requestScope.user.loginname}";
		}
		$("#loginname").val(loginname);
	});
</script>
</body>
</html>