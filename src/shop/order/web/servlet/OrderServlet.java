package shop.order.web.servlet;



import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import shop.book.domain.Book;
import shop.cart.domain.CartItem;
import shop.cart.service.CartItemService;
import shop.order.domain.Order;
import shop.order.domain.OrderItem;
import shop.order.service.OrderService;
import shop.pager.PageBean;
import shop.user.domain.User;
import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;

public class OrderServlet extends BaseServlet {
	private OrderService orderService = new OrderService();
	private CartItemService cartItemService = new CartItemService();
	//֧������
	public String payment(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		Properties props = new Properties();
		props.load(this.getClass().getClassLoader().getResourceAsStream("payment.properties"));
		//1.׼��13������
		String p0_Cmd = "Buy";//ҵ�����ͣ��̶�ֵBuy
		String p1_MerId = props.getProperty("p1_MerId");//�̺ű��룬���ױ���Ψһ��ʶ
		String p2_Order = req.getParameter("oid");//�������
		String p3_Amt = "0.01";//֧�����
		String p4_Cur = "CNY";//���ױ���
		String p5_Pid = "";//��Ʒ����
		String p6_Pcat = "";//��Ʒ����
		String p7_Pdesc ="";//��Ʒ����
		String p8_Url = props.getProperty("p8_Url");//֧���ɹ����ױ��ͷ��������ַ
		String p9_SAF = "";//�ͻ���ַ
		String pa_MP = "";//��չ��Ϣ
		String pd_FrpId = req.getParameter("yh");//֧��ͨ��
		String pr_NeedResponse = "1";//Ӧ����ƣ��̶�ֵ
		
		//2.����hmac,��Ҫ13��������keyValue ,�����㷨
		String keyValue = props.getProperty("keyValue");
		String hmac = PaymentUtil.buildHmac(p0_Cmd, p1_MerId, p2_Order, p3_Amt, p4_Cur, 
				p5_Pid, p6_Pcat, p7_Pdesc, p8_Url, p9_SAF, pa_MP, pd_FrpId,
				pr_NeedResponse, keyValue);
		//3.�ض����ױ���֧������
		StringBuilder sb = new StringBuilder("https://www.yeepay.com/app-merchant-proxy/node");
		sb.append("?").append("p0_Cmd=").append(p0_Cmd);
		sb.append("&").append("p1_MerId=").append(p1_MerId);
		sb.append("&").append("p2_Order=").append(p2_Order);
		sb.append("&").append("p3_Amt=").append(p3_Amt);
		sb.append("&").append("p4_Cur=").append(p4_Cur);
		sb.append("&").append("p5_Pid=").append(p5_Pid);
		sb.append("&").append("p6_Pcat=").append(p6_Pcat);
		sb.append("&").append("p7_Pdesc=").append(p7_Pdesc);
		sb.append("&").append("p8_Url=").append(p8_Url);
		sb.append("&").append("p9_SAF=").append(p9_SAF);
		sb.append("&").append("pa_MP=").append(pa_MP);
		sb.append("&").append("pd_FrpId=").append(pd_FrpId);
		sb.append("&").append("pr_NeedResponse=").append(pr_NeedResponse);
		sb.append("&").append("hmac=").append(hmac);
		resp.sendRedirect(sb.toString());
		return null;
	}
	//���л���������,��֧���ɹ�ʱ���ʣ����ַ���1,����������ض��򣨲��ɿ���
	//2.�ױ�������ʹ�õ�Ե��������������������success����Ȼ�ױ���������һֱ���ø÷���
	public String back(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		//1.��ȡ12������
		String p1_MerId = req.getParameter("p1_MerId");
		String r0_Cmd = req.getParameter("r0_Cmd");
		String r1_Code = req.getParameter("r1_Code");
		String r2_TrxId = req.getParameter("r2_TrxId");
		String r3_Amt = req.getParameter("r3_Amt");
		String r4_Cur = req.getParameter("r4_Cur");
		String r5_Pid = req.getParameter("r5_Pid");
		String r6_Order = req.getParameter("r6_Order");
		String r7_Uid = req.getParameter("r7_Uid");
		String r8_MP = req.getParameter("r8_MP");
		String r9_BType = req.getParameter("r9_BType");
		String hmac = req.getParameter("hmac");
		//2.��ȡkeyValue
		Properties props = new Properties();
		props.load(this.getClass().getClassLoader().getResourceAsStream("payment.properties"));
		String keyValue = props.getProperty("keyValue");
		boolean bool =  PaymentUtil.verifyCallback(hmac, p1_MerId, r0_Cmd,
				r1_Code, r2_TrxId, r3_Amt, r4_Cur, 
				r5_Pid, r6_Order, r7_Uid, r8_MP, r9_BType, keyValue);
		if(!bool){
			req.setAttribute("code", "error");
			req.setAttribute("msg", "��Ч��ǩ����֧��ʧ��(�㲻�Ǻ��ˣ�)");
			return "f:/jsps/user/msg.jsp";
		}
		
		//3.���ü��ܷ�����У������ߵ���ݣ����ʧ�ܣ����������Ϣ��msg.jsp,����ɹ����ж����ض����ǵ�Ե�
		//����ǰ�ߣ��޸Ķ���״̬������ɹ���Ϣ��msg.jsp
		//����ǵ�Ե㣬�޸Ķ���״̬������success
		
		if(r1_Code.equals("1")){
			orderService.updateStatus(r6_Order, 2);
			if(r9_BType.equals("1")){
				req.setAttribute("code", "success");
				req.setAttribute("msg", "��ϲ��֧���ɹ���");
				return "f:/jsps/user/msg.jsp";
			}else if(r9_BType.equals("2")){
				resp.getWriter().print("success");
			}
		}
		return null;
	
	}
	//֧��׼��
	public String paymentPre(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		String oid = req.getParameter("oid");
		Order order = orderService.load(oid);
		req.setAttribute("order", order);
		return "f:jsps/order/pay.jsp";
	}
	//ȡ������
	public String cancel(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		String oid= req.getParameter("oid");
		int status = orderService.findStatus(oid);
		if(status !=1){
			req.setAttribute("code", "error");
			req.setAttribute("msg", "״̬���ԣ�����ȡ����");
			return "f:/jsp/user/msg.jsp";
		}
		orderService.updateStatus(oid, 5);
		req.setAttribute("code", "success");
		req.setAttribute("msg", "���Ķ�����ȡ��������Ŷ��");
		return "f:/jsps/user/msg.jsp";
	}
	//ȷ���ջ�
	public String confirm(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		String oid= req.getParameter("oid");
		int status = orderService.findStatus(oid);
		if(status !=3){
			req.setAttribute("code", "error");
			req.setAttribute("msg", "״̬���ԣ�����ȷ���ջ���");
			return "f:/jsp/user/msg.jsp";
		}
		orderService.updateStatus(oid, 4);
		req.setAttribute("code", "success");
		req.setAttribute("msg", "��ϲ�����׳ɹ���");
		return "f:/jsp/user/msg.jsp";
	}
	
	//���ض���
	public String load(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		String oid = req.getParameter("oid");
		Order order = orderService.load(oid);
		req.setAttribute("order", order);
		String btn = req.getParameter("btn");//btn˵��������ĸ����������ʱ�����
		req.setAttribute("btn", btn);
		return "f:/jsps/order/desc.jsp";
	}
	
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
	//���ɶ���
	public String createOrder(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		//��ȡ���й��ﳵ��Ŀ��id
		String cartItemIds = req.getParameter("cartItemIds");
		List<CartItem> cartItemList = cartItemService.loadCartItems(cartItemIds);
		Order order = new Order();
		order.setOid(CommonUtils.uuid());
		order.setOrdertime(String.format("%tF %<tT",new Date()));//�µ�ʱ��
		order.setStatus(1);//���ö���״̬
		order.setAddress(req.getParameter("address"));
		User owner = (User)req.getSession().getAttribute("sessionUser");
		order.setOwner(owner);
		BigDecimal total =new BigDecimal("0");
		for(CartItem cartItem : cartItemList){
			total = total.add(new BigDecimal(cartItem.getSubtotal()+ ""));
		}
		order.setTotal(total.doubleValue());
		
		//����List<OrderItem>
		List<OrderItem> orderItemList = new ArrayList<OrderItem>();
		for(CartItem cartItem : cartItemList){
			OrderItem orderItem = new OrderItem();
			orderItem.setOrderItemId(CommonUtils.uuid());
			orderItem.setQuantity(cartItem.getQuantity());
			orderItem.setSubtotal(cartItem.getSubtotal());
			orderItem.setBook(cartItem.getBook());
			orderItem.setOrder(order);
			orderItemList.add(orderItem);
			}
		order.setOrderItemList(orderItemList);
		//����service������
		orderService.createOrder(order);
		cartItemService.batchDelete(cartItemIds);
		//���涩����ת����ordersucc.jsp
		req.setAttribute("order",order);
		return "f:/jsps/order/ordersucc.jsp";
		
	}
	
	
	//�ҵĶ���
	public String myOrders(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
				//�õ�pc�����ҳ�洫�ݣ�ʹ��ҳ��ģ��������1
				int pc = getPc(req);
				//�õ�url
				String url = getUrl(req);
				//��session�л�ȡuser
				User user = (User)req.getSession().getAttribute("sessionUser");
				
				//ʹ��pc��cid����service�е�findByCatrgBook���õ�PageBean
				PageBean<Order> pb = orderService.myOrders(user.getUid(), pc);
				//��pageBean����url������PageBean,ת����/jsp/book/list.jsp
				pb.setUrl(url);
				req.setAttribute("pb", pb);
				return "f:/jsps/order/list.jsp";
		}

	
}
