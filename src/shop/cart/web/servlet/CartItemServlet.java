package shop.cart.web.servlet;



import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import shop.book.domain.Book;
import shop.cart.domain.CartItem;
import shop.cart.service.CartItemService;
import shop.user.domain.User;
import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;

public class CartItemServlet extends BaseServlet {
	private CartItemService cartItemService = new CartItemService();
	/*
	 * 我的购物车*/
	public String myCart(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		//1.得到uid
		User user =(User)req.getSession().getAttribute("sessionUser");
		String uid = user.getUid();
		//2.通过service得到当前用户的所有购物条目
		List<CartItem> cartItemList = cartItemService.myCart(uid);
		//3.保存起来，转发到/cart/list.jsp
		req.setAttribute("cartItemList", cartItemList);
		return "f:/jsps/cart/list.jsp";
	}
	
	/*
	 * 添加条目*/
	public String add(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		/*封装表单数据到CartItem(bid，quantity)*/
		Map map = req.getParameterMap();
		CartItem cartItem = CommonUtils.toBean(map, CartItem.class);
		Book book = CommonUtils.toBean(map, Book.class);
		User user = (User)req.getSession().getAttribute("sessionUser");
		cartItem.setBook(book);
		cartItem.setUser(user);
		/*调用service完成添加*/
		cartItemService.add(cartItem);
		/*查询出当前用户的所有条目，转发到list.jsp*/
		return myCart(req,resp);
	}
	/*批量删除*/
	public String batchDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		/*
		 * 1.获取cartItemIds参数
		 * 2.调用service方法完成删除工作
		 * 3.返回到list.jsp*/
		String cartItemIds = req.getParameter("cartItemIds");
		cartItemService.batchDelete(cartItemIds);
		return myCart(req,resp);
	}
	
	public String updateQuantity(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		String cartItemId = req.getParameter("cartItemId");
		int quantity = Integer.parseInt(req.getParameter("quantity"));
		CartItem cartItem = cartItemService.updateQuantity(cartItemId, quantity);
		StringBuilder sb = new StringBuilder("{");
		sb.append("\"quantity\"").append(":").append(cartItem.getQuantity());
		sb.append(",");
		sb.append("\"subtotal\"").append(":").append(cartItem.getSubtotal());
		sb.append("}");
		resp.getWriter().print(sb);
		System.out.println(sb);
		return null;
	}
	//加载多个cartItem
	public String loadCartItems(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		//1.获取cartItemIds参数
		String cartItemIds = req.getParameter("cartItemIds");
		double total = Double.parseDouble(req.getParameter("total"));
		//2.通过service得到List<cartItem>
		List<CartItem> cartItemList = cartItemService.loadCartItems(cartItemIds);
		//3.保存，转发到/cart/showitem.jsp
		req.setAttribute("cartItemList", cartItemList);
		req.setAttribute("total", total);
		req.setAttribute("cartItemIds", cartItemIds);
		return "f:/jsps/cart/showitem.jsp";
	}
}
