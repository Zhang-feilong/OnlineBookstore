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
	 * �ҵĹ��ﳵ*/
	public String myCart(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		//1.�õ�uid
		User user =(User)req.getSession().getAttribute("sessionUser");
		String uid = user.getUid();
		//2.ͨ��service�õ���ǰ�û������й�����Ŀ
		List<CartItem> cartItemList = cartItemService.myCart(uid);
		//3.����������ת����/cart/list.jsp
		req.setAttribute("cartItemList", cartItemList);
		return "f:/jsps/cart/list.jsp";
	}
	
	/*
	 * �����Ŀ*/
	public String add(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		/*��װ�����ݵ�CartItem(bid��quantity)*/
		Map map = req.getParameterMap();
		CartItem cartItem = CommonUtils.toBean(map, CartItem.class);
		Book book = CommonUtils.toBean(map, Book.class);
		User user = (User)req.getSession().getAttribute("sessionUser");
		cartItem.setBook(book);
		cartItem.setUser(user);
		/*����service������*/
		cartItemService.add(cartItem);
		/*��ѯ����ǰ�û���������Ŀ��ת����list.jsp*/
		return myCart(req,resp);
	}
	/*����ɾ��*/
	public String batchDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		/*
		 * 1.��ȡcartItemIds����
		 * 2.����service�������ɾ������
		 * 3.���ص�list.jsp*/
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
	//���ض��cartItem
	public String loadCartItems(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		//1.��ȡcartItemIds����
		String cartItemIds = req.getParameter("cartItemIds");
		double total = Double.parseDouble(req.getParameter("total"));
		//2.ͨ��service�õ�List<cartItem>
		List<CartItem> cartItemList = cartItemService.loadCartItems(cartItemIds);
		//3.���棬ת����/cart/showitem.jsp
		req.setAttribute("cartItemList", cartItemList);
		req.setAttribute("total", total);
		req.setAttribute("cartItemIds", cartItemIds);
		return "f:/jsps/cart/showitem.jsp";
	}
}
