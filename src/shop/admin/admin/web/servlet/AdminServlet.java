package shop.admin.admin.web.servlet;



import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import shop.admin.admin.domain.Admin;
import shop.admin.admin.service.AdminService;
import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;

public class AdminServlet extends BaseServlet {
	private AdminService adminService = new AdminService();
	//��¼����
	public String login(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		//1����װ�����ݵ�Admin
		Admin form = CommonUtils.toBean(req.getParameterMap(), Admin.class);
		Admin admin = adminService.login(form);
		if(admin == null){
			req.setAttribute("msg", "���û������������");
			return "/adminjsps/login.jsp";
		}
		req.getSession().setAttribute("admin", admin);
		return "f:/adminjsps/admin/index.jsp";
	}
}
