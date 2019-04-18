package shop.admin.order.web.servlet;



import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import shop.order.domain.Order;
import shop.order.service.OrderService;
import shop.pager.PageBean;
import shop.user.domain.User;
import cn.itcast.servlet.BaseServlet;

public class AdminOrderServlet extends BaseServlet {
	private OrderService orderService = new OrderService();

	/*获取当前页码*/
	private int getPc(HttpServletRequest req){
		int pc = 1;
		String param = req.getParameter("pc");
		if(param !=null && !param.trim().isEmpty()){
			try{
				pc = Integer.parseInt(param);
			}catch(RuntimeException e){
			}
		}
		return pc;
	}
	/*截取url 分页导航需要使用它作为超链接的目标*/
	private String getUrl(HttpServletRequest req){
		String url = req.getRequestURI() + "?" +  req.getQueryString();
		int index = url.lastIndexOf("&pc=");
		if(index !=-1){
			url = url.substring(0,index);
		}
		return url;
	}
	//查询所有
	public String findAll(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
				//得到pc，如果页面传递，使用页面的，否则等于1
				int pc = getPc(req);
				//得到url
				String url = getUrl(req);
				
				//使用pc调用service中的findByCatrgBook法得到PageBean
				PageBean<Order> pb = orderService.findAll(pc);
				//给pageBean设置url，保存PageBean,转发到/jsp/book/list.jsp
				pb.setUrl(url);
				req.setAttribute("pb", pb);
				return "f:/adminjsps/admin/order/list.jsp";
		}
	//按状态查询
	public String findByStatus(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
				//得到pc，如果页面传递，使用页面的，否则等于1
				int pc = getPc(req);
				//得到url
				String url = getUrl(req);
				//获取链接参数
				int status = Integer.parseInt(req.getParameter("status"));
				//使用pc调用service中的findByCatrgBook法得到PageBean
				PageBean<Order> pb = orderService.findByStatus(status,pc);
				//给pageBean设置url，保存PageBean,转发到/jsp/book/list.jsp
				pb.setUrl(url);
				req.setAttribute("pb", pb);
				return "f:/adminjsps/admin/order/list.jsp";
		}
	//加载订单详细
	public String load(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		String oid = req.getParameter("oid");
		Order order = orderService.load(oid);
		req.setAttribute("order", order);
		String btn = req.getParameter("btn");//btn说明点击了哪个链接来访问本方法
		req.setAttribute("btn", btn);
		return "f:/adminjsps/admin/order/desc.jsp";
	}
	
	//取消订单
		public String cancel(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException, SQLException {
			String oid= req.getParameter("oid");
			int status = orderService.findStatus(oid);
			if(status !=1){
				req.setAttribute("code", "error");
				req.setAttribute("msg", "状态不对，不能取消！");
				return "f:/adminjsp/msg.jsp";
			}
			orderService.updateStatus(oid, 5);
			req.setAttribute("code", "success");
			req.setAttribute("msg", "您的订单已取消！别后悔哦！");
			return "f:/adminjsps/msg.jsp";
		}
		
		//订单发货
				public String deliver(HttpServletRequest req, HttpServletResponse resp)
						throws ServletException, IOException, SQLException {
					String oid= req.getParameter("oid");
					int status = orderService.findStatus(oid);
					if(status != 2){
						req.setAttribute("code", "error");
						req.setAttribute("msg", "状态不对，不能发货！");
						return "f:/adminjsp/msg.jsp";
					}
					orderService.updateStatus(oid, 3);
					req.setAttribute("code", "success");
					req.setAttribute("msg", "您的订单已发货！请查看物流！");
					return "f:/adminjsps/msg.jsp";
				}
}
