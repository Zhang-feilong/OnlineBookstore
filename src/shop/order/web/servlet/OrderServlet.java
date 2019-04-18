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
	//支付方法
	public String payment(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		Properties props = new Properties();
		props.load(this.getClass().getClassLoader().getResourceAsStream("payment.properties"));
		//1.准备13个参数
		String p0_Cmd = "Buy";//业务类型，固定值Buy
		String p1_MerId = props.getProperty("p1_MerId");//商号编码，在易宝的唯一标识
		String p2_Order = req.getParameter("oid");//订单编号
		String p3_Amt = "0.01";//支付金额
		String p4_Cur = "CNY";//交易币种
		String p5_Pid = "";//商品名称
		String p6_Pcat = "";//商品种类
		String p7_Pdesc ="";//商品描述
		String p8_Url = props.getProperty("p8_Url");//支付成功后，易宝就访问这个地址
		String p9_SAF = "";//送货地址
		String pa_MP = "";//扩展信息
		String pd_FrpId = req.getParameter("yh");//支付通道
		String pr_NeedResponse = "1";//应答机制，固定值
		
		//2.计算hmac,需要13个参数，keyValue ,加密算法
		String keyValue = props.getProperty("keyValue");
		String hmac = PaymentUtil.buildHmac(p0_Cmd, p1_MerId, p2_Order, p3_Amt, p4_Cur, 
				p5_Pid, p6_Pcat, p7_Pdesc, p8_Url, p9_SAF, pa_MP, pd_FrpId,
				pr_NeedResponse, keyValue);
		//3.重定向到易宝的支付网关
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
	//银行回馈处理方法,当支付成功时访问，两种方法1,引导浏览器重定向（不可靠）
	//2.易宝服务器使用点对点访问这个方法，必须回馈success，不然易宝服务器会一直调用该方法
	public String back(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		//1.获取12个参数
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
		//2.获取keyValue
		Properties props = new Properties();
		props.load(this.getClass().getClassLoader().getResourceAsStream("payment.properties"));
		String keyValue = props.getProperty("keyValue");
		boolean bool =  PaymentUtil.verifyCallback(hmac, p1_MerId, r0_Cmd,
				r1_Code, r2_TrxId, r3_Amt, r4_Cur, 
				r5_Pid, r6_Order, r7_Uid, r8_MP, r9_BType, keyValue);
		if(!bool){
			req.setAttribute("code", "error");
			req.setAttribute("msg", "无效的签名，支付失败(你不是好人！)");
			return "f:/jsps/user/msg.jsp";
		}
		
		//3.调用加密方法来校验调用者的身份；如果失败，保存错误信息的msg.jsp,如果成功，判断是重定向还是点对点
		//如是前者，修改订单状态，保存成功信息到msg.jsp
		//如果是点对点，修改订单状态，返回success
		
		if(r1_Code.equals("1")){
			orderService.updateStatus(r6_Order, 2);
			if(r9_BType.equals("1")){
				req.setAttribute("code", "success");
				req.setAttribute("msg", "恭喜，支付成功！");
				return "f:/jsps/user/msg.jsp";
			}else if(r9_BType.equals("2")){
				resp.getWriter().print("success");
			}
		}
		return null;
	
	}
	//支付准备
	public String paymentPre(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		String oid = req.getParameter("oid");
		Order order = orderService.load(oid);
		req.setAttribute("order", order);
		return "f:jsps/order/pay.jsp";
	}
	//取消订单
	public String cancel(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		String oid= req.getParameter("oid");
		int status = orderService.findStatus(oid);
		if(status !=1){
			req.setAttribute("code", "error");
			req.setAttribute("msg", "状态不对，不能取消！");
			return "f:/jsp/user/msg.jsp";
		}
		orderService.updateStatus(oid, 5);
		req.setAttribute("code", "success");
		req.setAttribute("msg", "您的订单已取消！别后悔哦！");
		return "f:/jsps/user/msg.jsp";
	}
	//确认收货
	public String confirm(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		String oid= req.getParameter("oid");
		int status = orderService.findStatus(oid);
		if(status !=3){
			req.setAttribute("code", "error");
			req.setAttribute("msg", "状态不对，不能确认收货！");
			return "f:/jsp/user/msg.jsp";
		}
		orderService.updateStatus(oid, 4);
		req.setAttribute("code", "success");
		req.setAttribute("msg", "恭喜，交易成功！");
		return "f:/jsp/user/msg.jsp";
	}
	
	//加载订单
	public String load(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		String oid = req.getParameter("oid");
		Order order = orderService.load(oid);
		req.setAttribute("order", order);
		String btn = req.getParameter("btn");//btn说明点击了哪个链接来访问本方法
		req.setAttribute("btn", btn);
		return "f:/jsps/order/desc.jsp";
	}
	
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
	//生成订单
	public String createOrder(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		//获取所有购物车条目的id
		String cartItemIds = req.getParameter("cartItemIds");
		List<CartItem> cartItemList = cartItemService.loadCartItems(cartItemIds);
		Order order = new Order();
		order.setOid(CommonUtils.uuid());
		order.setOrdertime(String.format("%tF %<tT",new Date()));//下单时间
		order.setStatus(1);//设置订单状态
		order.setAddress(req.getParameter("address"));
		User owner = (User)req.getSession().getAttribute("sessionUser");
		order.setOwner(owner);
		BigDecimal total =new BigDecimal("0");
		for(CartItem cartItem : cartItemList){
			total = total.add(new BigDecimal(cartItem.getSubtotal()+ ""));
		}
		order.setTotal(total.doubleValue());
		
		//创建List<OrderItem>
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
		//调用service完成添加
		orderService.createOrder(order);
		cartItemService.batchDelete(cartItemIds);
		//保存订单，转发到ordersucc.jsp
		req.setAttribute("order",order);
		return "f:/jsps/order/ordersucc.jsp";
		
	}
	
	
	//我的订单
	public String myOrders(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
				//得到pc，如果页面传递，使用页面的，否则等于1
				int pc = getPc(req);
				//得到url
				String url = getUrl(req);
				//从session中获取user
				User user = (User)req.getSession().getAttribute("sessionUser");
				
				//使用pc和cid调用service中的findByCatrgBook法得到PageBean
				PageBean<Order> pb = orderService.myOrders(user.getUid(), pc);
				//给pageBean设置url，保存PageBean,转发到/jsp/book/list.jsp
				pb.setUrl(url);
				req.setAttribute("pb", pb);
				return "f:/jsps/order/list.jsp";
		}

	
}
