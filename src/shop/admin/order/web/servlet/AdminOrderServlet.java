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

	/*��ȡ��ǰҳ��*/
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
	/*��ȡurl ��ҳ������Ҫʹ������Ϊ�����ӵ�Ŀ��*/
	private String getUrl(HttpServletRequest req){
		String url = req.getRequestURI() + "?" +  req.getQueryString();
		int index = url.lastIndexOf("&pc=");
		if(index !=-1){
			url = url.substring(0,index);
		}
		return url;
	}
	//��ѯ����
	public String findAll(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
				//�õ�pc�����ҳ�洫�ݣ�ʹ��ҳ��ģ��������1
				int pc = getPc(req);
				//�õ�url
				String url = getUrl(req);
				
				//ʹ��pc����service�е�findByCatrgBook���õ�PageBean
				PageBean<Order> pb = orderService.findAll(pc);
				//��pageBean����url������PageBean,ת����/jsp/book/list.jsp
				pb.setUrl(url);
				req.setAttribute("pb", pb);
				return "f:/adminjsps/admin/order/list.jsp";
		}
	//��״̬��ѯ
	public String findByStatus(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
				//�õ�pc�����ҳ�洫�ݣ�ʹ��ҳ��ģ��������1
				int pc = getPc(req);
				//�õ�url
				String url = getUrl(req);
				//��ȡ���Ӳ���
				int status = Integer.parseInt(req.getParameter("status"));
				//ʹ��pc����service�е�findByCatrgBook���õ�PageBean
				PageBean<Order> pb = orderService.findByStatus(status,pc);
				//��pageBean����url������PageBean,ת����/jsp/book/list.jsp
				pb.setUrl(url);
				req.setAttribute("pb", pb);
				return "f:/adminjsps/admin/order/list.jsp";
		}
	//���ض�����ϸ
	public String load(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		String oid = req.getParameter("oid");
		Order order = orderService.load(oid);
		req.setAttribute("order", order);
		String btn = req.getParameter("btn");//btn˵��������ĸ����������ʱ�����
		req.setAttribute("btn", btn);
		return "f:/adminjsps/admin/order/desc.jsp";
	}
	
	//ȡ������
		public String cancel(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException, SQLException {
			String oid= req.getParameter("oid");
			int status = orderService.findStatus(oid);
			if(status !=1){
				req.setAttribute("code", "error");
				req.setAttribute("msg", "״̬���ԣ�����ȡ����");
				return "f:/adminjsp/msg.jsp";
			}
			orderService.updateStatus(oid, 5);
			req.setAttribute("code", "success");
			req.setAttribute("msg", "���Ķ�����ȡ��������Ŷ��");
			return "f:/adminjsps/msg.jsp";
		}
		
		//��������
				public String deliver(HttpServletRequest req, HttpServletResponse resp)
						throws ServletException, IOException, SQLException {
					String oid= req.getParameter("oid");
					int status = orderService.findStatus(oid);
					if(status != 2){
						req.setAttribute("code", "error");
						req.setAttribute("msg", "״̬���ԣ����ܷ�����");
						return "f:/adminjsp/msg.jsp";
					}
					orderService.updateStatus(oid, 3);
					req.setAttribute("code", "success");
					req.setAttribute("msg", "���Ķ����ѷ�������鿴������");
					return "f:/adminjsps/msg.jsp";
				}
}
