package shop.cart.service;

import java.sql.SQLException;
import java.util.List;

import cn.itcast.commons.CommonUtils;

import shop.cart.dao.CartItemDao;
import shop.cart.domain.CartItem;

public class CartItemService {
	private CartItemDao cartItemDao = new CartItemDao();
	/*�ҵĹ��ﳵ����*/
	public List<CartItem> myCart(String uid){
		try {
			return cartItemDao.findByUser(uid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/*�����Ŀ*/
	public void add(CartItem cartItem){
		/*1.ʹ��uid��bidȥ���ݿ��ѯ�����Ŀ�Ƿ����*/
		try {
			CartItem _cartItem = cartItemDao.findByUidAndBid(cartItem.getUser().getUid(), cartItem.getBook().getBid());
			if(_cartItem ==null){//���ԭ��û�������Ŀ�������
				cartItem.setCartItemId(CommonUtils.uuid());
				cartItemDao.addCartItem(cartItem);
			}else{//���ԭ���У����޸�����
				//ʹ��ԭ������������Ŀ����֮�ͣ�����Ϊ�µ�����
				int quantity = cartItem.getQuantity() + _cartItem.getQuantity();
				//�޸��������Ŀ������
				cartItemDao.updateQuantity(_cartItem.getCartItemId(), quantity);
				}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	//����ɾ������
	public void batchDelete(String cartItemIds){
		try {
			cartItemDao.batchDelete(cartItemIds);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	//�޸Ĺ��ﳵ��Ŀ����
	public CartItem updateQuantity(String cartItemId,int quantity){
		try {
			cartItemDao.updateQuantity(cartItemId, quantity);
			return cartItemDao.findByCartItemId(cartItemId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	//���ض��cartItem
	public List<CartItem> loadCartItems(String cartItemIds){
		try {
			return cartItemDao.loadCartItems(cartItemIds);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
