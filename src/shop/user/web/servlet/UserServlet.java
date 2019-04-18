package shop.user.web.servlet;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import shop.user.domain.User;
import shop.user.service.UserService;
import shop.user.service.exception.UserException;
import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;

public class UserServlet extends BaseServlet {
	private UserService userService = new UserService();
	/*ajax 用户名是否注册校验*/
	public String ajaxValidateLoginname(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		/*获取用户名*/
		String loginname = req.getParameter("loginname");
		/*通过service得到校验结果*/
		boolean b = userService.ajaxValidateLoginname(loginname);
		/*发给客户端*/
		resp.getWriter().print(b);
		return null;
	}
	/*ajax email是否注册校验*/
	public String ajaxValidateEmail(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		/*获取用户名*/
		String email = req.getParameter("email");
		/*通过service得到校验结果*/
		boolean b = userService.ajaxValidateEmail(email);
		/*发给客户端*/
		resp.getWriter().print(b);
		return null;
	}
	/*ajax 验证码是否正确校验*/
	public String ajaxValidateVerifyCode(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*获取文本框验证码*/
		String verifyCode = req.getParameter("verifyCode");
		/*获取图片真实验证码*/
		String vcode = (String) req.getSession().getAttribute("vCode");
		/*进行忽略大小写的比较 ，得到结果*/
		boolean b = verifyCode.equalsIgnoreCase(vcode);
		resp.getWriter().print(b);
		return null;
	}
	
	public String regist(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		/*将表单数据封装到user对象*/
		User formuser = CommonUtils.toBean(req.getParameterMap(),User.class);
		
		/*校验*/
		Map<String,String> errors = valiadateRegist(formuser,req.getSession());
		if(errors.size() > 0){
			req.setAttribute("form", formuser);
			req.setAttribute("errors", errors);
			return "f:jsps/user/regist.jsp";
		}
		
		/*使用service完成业务*/
		userService.regist(formuser);
	    req.setAttribute("code","success");
		req.setAttribute("msg", "注册成功，请马上到邮箱激活！");
		return "f:/jsps/user/msg.jsp";
	}
	/*注册校验*/
	private Map<String, String> valiadateRegist(User formuser,HttpSession session) throws SQLException{
		Map<String,String> errors = new HashMap<String,String>();
		/*校验登录名*/
		String loginname = formuser.getLoginname();
		if(loginname == null||loginname.trim().isEmpty()){
			errors.put("loginname", "用户名不能为空！");
		}else if(loginname.length()<3||loginname.length()>20){
			errors.put("loginname", "用户名长度在3~20之间！");
		}else if(!userService.ajaxValidateLoginname(loginname)){
			errors.put("loginname", "用户名已注册！");
		}
		
		
		/*校验密码*/
		String loginpass = formuser.getLoginpass();
		if(loginpass ==null||loginpass.trim().isEmpty()){
			errors.put("loginpass", "密码不能为空！");
		}else if(loginpass.length()<3||loginpass.length()>20){
			errors.put("loginpass", "密码长度在3~20之间！");
		}
		
		/*校验确认密码*/
		String reloginpass = formuser.getReloginpass();
		if(reloginpass ==null||reloginpass.trim().isEmpty()){
			errors.put("reloginpass", "确认密码不能为空！");
		}else if(!reloginpass.equals(loginpass)){
			errors.put("reloginpass", "两次输入不一致！");
		}
		/*校验email*/
		String email = formuser.getEmail();
		if(email ==null||email.trim().isEmpty()){
			errors.put("email", "邮箱不能为空！");
		}else if(!email.matches("^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\\.[a-zA-Z0-9_-]{2,3}){1,2})$")){
			errors.put("email", "邮箱格式不正确！");
		}else if(!userService.ajaxValidateEmail(email)){
			errors.put("email", "邮箱已注册！");
		}
		
		
		/*验证码校验*/
		String verifyCode = formuser.getVerifyCode();
		String vcode = (String) session.getAttribute("vCode");
		if(verifyCode ==null||verifyCode.trim().isEmpty()){
			errors.put("verifyCode", "验证码不能为空！");
		}else if(!verifyCode.equalsIgnoreCase(vcode)){
			errors.put("verifyCode", "验证码不正确！");
		}
		return errors;
	}
	
	/*激活功能*/
	public String activation(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		/*获取参数激活码*/
		/*用激活码调用Service方法完成激活，如果抛出异常将异常信息拿来保存到request中，转发到msg.jsp
		 * 否则保存成功信息到request，转发到msg.jsp
		 * */
		String code = req.getParameter("activationCode");
		try {
			userService.activation(code);
			req.setAttribute("code","success");
			req.setAttribute("msg","恭喜您，激活成功，请马上登录！");
		} catch (UserException e) {
			// 说明抛出了异常
			req.setAttribute("msg",e.getMessage());
			req.setAttribute("code","error");/*通知msg，显示×*/
			
		}
		return "f:/jsps/user/msg.jsp";
	}
	
	/*修改密码*/
	public String updatePassword(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		/*
		 * 1.封装表单数据到user
		 * 2.从session 中获取uid
		 * 3.使用uid和表单中的老密码和新密码来调用service方法
		 * 		如果出现异常，保存异常信息到requset中，转发到pwd.jsp
		 * 		保存成功信息到request
		 * 		转发到msg.jsp
		 * */
		User formUser = CommonUtils.toBean(req.getParameterMap(),User.class);
		Map<String,String> errors = valiadateUpdatePassword(formUser,req.getSession());
		if(errors.size()>0){
			req.setAttribute("form", formUser);
			req.setAttribute("errors", errors);
			return "f:/jsps/user/pwd.jsp";
		}
		User user = (User)req.getSession().getAttribute("sessionUser");
		if(user == null){
			req.setAttribute("msg","您还没有登录！");
			return "f:/jsps/user/login.jsp";
		}
		
		try {
			userService.updatePassword(user.getUid(), formUser.getNewloginpass(), formUser.getLoginpass());
			req.setAttribute("msg", "修改密码成功！");
			req.setAttribute("code","success");
			return "f:/jsps/user/msg.jsp"; 
		} catch (UserException e) {
			req.setAttribute("msg", e.getMessage());/*保存异常信息到request*/
			req.setAttribute("user", formUser);/*为了回显*/
			return "f:/jsps/user/pwd.jsp"; 
			
		}
	}
	
	private Map<String, String> valiadateUpdatePassword(User formuser,HttpSession session) throws SQLException{
		Map<String,String> errors = new HashMap<String,String>();
		/*校验密码*/
		String loginpass = formuser.getLoginpass();
		if(loginpass ==null||loginpass.trim().isEmpty()){
			errors.put("loginpass", "原密码不能为空！");
		}else if(loginpass.length()<3||loginpass.length()>20){
			errors.put("loginpass", "密码长度在3~20之间！");
		}
		/*校验新密码*/
		String newloginpass = formuser.getNewloginpass();
		if(newloginpass ==null||newloginpass.trim().isEmpty()){
			errors.put("newloginpass", "新密码不能为空！");
		}else if(newloginpass.length()<3||newloginpass.length()>20){
			errors.put("newloginpass", "密码长度在3~20之间！");
		}
		/*校验确认密码*/
		String reloginpass = formuser.getReloginpass();
		if(reloginpass ==null||reloginpass.trim().isEmpty()){
			errors.put("reloginpass", "确认密码不能为空！");
		}else if(!reloginpass.equals(newloginpass)){
			errors.put("reloginpass", "两次输入不一致！");
		}
	
		/*验证码校验*/
		String verifyCode = formuser.getVerifyCode();
		String vcode = (String) session.getAttribute("vCode");
		if(verifyCode ==null||verifyCode.trim().isEmpty()){
			errors.put("verifyCode", "验证码不能为空！");
		}else if(!verifyCode.equalsIgnoreCase(vcode)){
			errors.put("verifyCode", "验证码不正确！");
		}
		return errors;
	}
	/*退出功能*/
	public String quit(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		req.getSession().invalidate();
		return "r:/jsps/user/login.jsp";
		
	}
	
	public String login(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		/*封装表单数据到user
		 * 校验表单数据。使用Service 查询得到user
		 * 查看用户是否存在，如果不存在，保存错误信息：用户名或密码错误，保存用户数据，转发到login.jsp
		 * 如果存在，查看状态，如果为flase，保存错误信息，您没有激活，保存用户数据，转发到login.jsp
		 * 登陆成功
		 * 保存当前查询出的user到session中
		 * 保存当前用户名称到cookie中，注意中文恩需要编码处理
		 * */
		User formuser  = CommonUtils.toBean(req.getParameterMap(),User.class);
		Map<String,String> errors = valiadateLogin(formuser,req.getSession());
		if(errors.size()>0){
			req.setAttribute("form", formuser);
			req.setAttribute("errors", errors);
			return "f:/jsps/user/login.jsp";
		}
		User user = userService.login(formuser);
		if(user == null){
			req.setAttribute("msg", "用户名或密码错误！");
			req.setAttribute("user", formuser);
			return "f:/jsps/user/login.jsp";
		}else{
			if(!user.isStatus()){
				req.setAttribute("msg", "您还没有激活！");
				req.setAttribute("user", formuser);
				return "f:/jsps/user/login.jsp";
			}else{
				/*保存用户到session*/
				req.getSession().setAttribute("sessionUser", user);
				//获取用户名保存到cookie
				String loginname = user.getLoginname();
				loginname = URLEncoder.encode(loginname,"utf-8");
				Cookie cookie = new Cookie("loginname",loginname);
				cookie.setMaxAge(60*60*24*10);//保存时间为10天
				resp.addCookie(cookie);
				return "r:/index.jsp";//重定向到主页
				
				
				
			}
		}		
	
	}
	/*登录校验*/
	private Map<String, String> valiadateLogin(User formuser,HttpSession session) throws SQLException{
		Map<String,String> errors = new HashMap<String,String>();
		/*校验登录名*/
		String loginname = formuser.getLoginname();
		if(loginname == null||loginname.trim().isEmpty()){
			errors.put("loginname", "用户名不能为空！");
			System.out.println(loginname);
		}else if(loginname.length()<3||loginname.length()>20){
			errors.put("loginname", "用户名长度在3~20之间！");
		}
		
		
		/*校验密码*/
		String loginpass = formuser.getLoginpass();
		if(loginpass == null||loginpass.trim().isEmpty()){
			System.out.println(loginpass);
			errors.put("loginpass", "密码不能为空！");
		}else if(loginpass.length()<3||loginpass.length()>20){
			errors.put("loginpass", "密码长度在3~20之间！");
		}
		
		/*验证码校验*/
		String verifyCode = formuser.getVerifyCode();
		String vcode = (String) session.getAttribute("vCode");
		if(verifyCode ==null||verifyCode.trim().isEmpty()){
			errors.put("verifyCode", "验证码不能为空！");
		}else if(!verifyCode.equalsIgnoreCase(vcode)){
			errors.put("verifyCode", "验证码不正确！");
		}
		return errors;
	}
}
