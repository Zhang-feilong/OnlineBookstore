package shop.cart.dao;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;

import shop.book.domain.Book;
import shop.cart.domain.CartItem;
import shop.user.domain.User;

import cn.itcast.commons.CommonUtils;
import cn.itcast.jdbc.TxQueryRunner;

public class CartItemDao {
	private QueryRunner qr = new TxQueryRunner();
	//��ѯĳ���û���ĳ��ͼ��Ĺ��ﳵ��Ŀ�Ƿ����
	public CartItem findByUidAndBid(String uid,String bid) throws SQLException{
		String sql = "select * from t_cartitem where uid=? and bid=?";
		Map<String,Object> map = qr.query(sql, new MapHandler(),uid,bid);
		CartItem cartItem = toCartItem(map);
		return cartItem;
	}
	//�޸�ָ����Ŀ������
	public void updateQuantity(String cartItemId,int quantity) throws SQLException{
		String sql = "update t_cartitem set quantity=? where cartItemId=?";
		qr.update(sql,quantity,cartItemId);
	}
	//�����Ŀ
	public void addCartItem(CartItem cartItem) throws SQLException{
		String sql = "insert into t_cartitem(cartItemId,quantity,bid,uid)" +"values(?,?,?,?)";
		Object[] params = {cartItem.getCartItemId(),cartItem.getQuantity(),
				cartItem.getBook().getBid(),cartItem.getUser().getUid()};
		qr.update(sql,params);
	}
	
	/*��һ��Mapӳ���һ��CartItem*/
	private CartItem toCartItem(Map<String,Object> map){
		if(map == null ||map.size() == 0) return null;
		CartItem cartItem = CommonUtils.toBean(map, CartItem.class);
		Book book = CommonUtils.toBean(map, Book.class);
		User user = CommonUtils.toBean(map, User.class);
		cartItem.setBook(book);
		cartItem.setUser(user);
		return cartItem;
		
	}
	private List<CartItem> toCartItemList(List<Map<String,Object>> mapList){
		List<CartItem> cartItemList = new ArrayList<CartItem>();
		for(Map<String,Object> map :mapList){
			CartItem cartItem = toCartItem(map);
			cartItemList.add(cartItem);
		}
		return cartItemList;
	}
	/*ͨ���û���ѯ������Ŀ*/
	public List<CartItem> findByUser(String uid) throws SQLException{
		String sql = "select * from  t_cartitem c, t_book b where c.bid=b.bid and uid=? order by c.orderBy";
		List<Map<String,Object>> mapList = qr.query(sql, new MapListHandler(),uid);
		return toCartItemList(mapList);
	}
	// ��������where�Ӿ�
	private String toWhereSql(int len){
		StringBuilder sb = new StringBuilder("cartItemId in(");
		for(int i=0;i<len;i++){
			sb.append("?");
			if(i<len-1){
				sb.append(",");
			}
		}
		sb.append(")");
		return sb.toString();
	}
	//����ɾ��
	public void batchDelete(String cartItemIds) throws SQLException{
		/*��Ҫ�Ȱ�cartItemIdsת��������
		 * ��cartItemIdsת����һ��where�Ӿ�
		 *��delet from������һ��Ȼ��ִ��
		 * */
		Object[] cartItemIdArray =cartItemIds.split(",");
		String whereSql = toWhereSql(cartItemIdArray.length);
		String sql = "delete from t_cartitem where " + whereSql;
		qr.update(sql,cartItemIdArray);
	}
	
	//���ض��cartItem
	public List<CartItem> loadCartItems(String cartItemIds) throws SQLException{
		Object[] cartItemIdArray =cartItemIds.split(",");
		String whereSql = toWhereSql(cartItemIdArray.length);
		String sql = "select * from t_cartitem c,t_book b where c.bid=b.bid and " + whereSql;
		return toCartItemList(qr.query(sql, new MapListHandler(),cartItemIdArray));
	}
	
	//��id��ѯ
	public CartItem findByCartItemId(String cartItemId) throws SQLException{
		String sql = "select * from t_cartitem c,t_book b where c.bid=b.bid and c.cartItemId=?";
		Map<String,Object> map = qr.query(sql, new MapHandler(),cartItemId);
		return toCartItem(map);
	}
}
