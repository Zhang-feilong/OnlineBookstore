package shop.order.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import shop.book.domain.Book;
import shop.order.domain.Order;
import shop.order.domain.OrderItem;
import shop.pager.Expression;
import shop.pager.PageBean;
import shop.pager.PageConstants;

import cn.itcast.commons.CommonUtils;
import cn.itcast.jdbc.TxQueryRunner;

public class OrderDao {
	private QueryRunner qr = new TxQueryRunner();
	
	//��ѯ����״̬
	public int findStatus(String oid) throws SQLException{
		String sql = "select status from t_order  where oid=?";
		Number number = (Number)qr.query(sql, new ScalarHandler(),oid);
		return number.intValue();
	}
	
	//�޸Ķ���״̬
	public void updateStatus(String oid,int status) throws SQLException{
		String sql = "update t_order set status=? where oid=?";
		qr.update(sql,status,oid);
	}
	
	//���ض���
	public Order load(String oid) throws SQLException{
		String sql = "select * from t_order where oid=?";
		Order order = qr.query(sql, new BeanHandler<Order>(Order.class),oid);
		loadOrderItem(order);//Ϊ��ǰ�����������ж�����Ŀ
		return order;
	}
	//���ɶ���
	public void add(Order order) throws SQLException{
		//���붩��
		String sql = "insert into t_order values(?,?,?,?,?,?)";
		Object[] params = {order.getOid(),order.getOrdertime(),order.getTotal(),
				order.getStatus(),order.getAddress(),order.getOwner().getUid()
		};
		qr.update(sql,params);
//		2.ѭ������������������Ŀ����ÿ����Ŀ����һ��Object[]
		sql = "insert into t_orderitem values(?,?,?,?,?,?,?,?)";
		int len = order.getOrderItemList().size();
		Object[][] objs = new Object[len][];
		for(int i=0;i<len;i++){
			OrderItem item = order.getOrderItemList().get(i);
			objs[i] = new Object[]{item.getOrderItemId(),item.getQuantity(),item.getSubtotal(),
					item.getBook().getBid(),item.getBook().getBname(),item.getBook().getCurrPrice(),
					item.getBook().getImage_b(),order.getOid()};
		}
		qr.batch(sql, objs);
		
	}
	/*���û���ѯ����*/
	
	public PageBean<Order> findByUser(String uid ,int pc) throws SQLException{
		List<Expression> exprList = new ArrayList<Expression>();
		exprList.add(new Expression("uid","=",uid));
		return findByCriteria(exprList,pc);
	}
	
	//��ѯ����
	public PageBean<Order> findAll(int pc) throws SQLException{
		List<Expression> exprList = new ArrayList<Expression>();
		return findByCriteria(exprList,pc);
	}
	
	//��״̬��ѯ
	public PageBean<Order> findByStatus(int status ,int pc) throws SQLException{
		List<Expression> exprList = new ArrayList<Expression>();
		exprList.add(new Expression("status","=",status+""));
		return findByCriteria(exprList,pc);
	}
	
	private PageBean<Order> findByCriteria(List<Expression> exprList,int pc) throws SQLException{
		/*1.�õ�ps
		 * 2.�õ�tr
		 * 3.�õ�beanList
		 * 4.����PageBean����
		 * */
		int ps = PageConstants.ORDER_PAGE_SIZE;//ÿҳ��¼��
		StringBuilder whereSql = new StringBuilder(" where 1=1");
		List<Object> params = new ArrayList<Object>();//��Ӧsql���ʺŵ�ֵ
		for(Expression expr: exprList){
			/*
			 * ���һ������
			 * ��and��ͷ�����������ƣ���������������� =��>��< is null  is null û��ֵ
			 * �����������is null ��׷���ʺţ�Ȼ������params�����һ�����ʺŶ�Ӧ��ֵ
			 * */
			whereSql.append(" and ").append(expr.getName()).append(" ")
			.append(expr.getOperator()).append(" ");
			if(!expr.getOperator().equals("is null")){
				whereSql.append("?");
				params.add(expr.getValue());
			}
		}
		String sql = "select count(*) from t_order" + whereSql;
		Number number = (Number)qr.query(sql, new ScalarHandler(),params.toArray());
		int tr = number.intValue();
		
		sql = "select * from t_order" + whereSql +" order by ordertime desc limit ?,?";
		params.add((pc-1)*ps);//��ǰҳ���м�¼���±�
		params.add(ps);//һ����ѯ���У�����ÿҳ��¼��
		
		List<Order> beanList =  qr.query(sql, new BeanListHandler<Order>(Order.class),
				params.toArray());
		//��Ȼ��ȡ�����ж�������ÿ��������û����Ŀ������ÿ������Ϊ��������ж�����Ŀ
		for(Order order : beanList){
			loadOrderItem(order);
		}
		//����PageBean,s���ò���
		PageBean<Order> pb= new PageBean<Order>();
		//����PageBean û��url ���������Service���
		pb.setBeanList(beanList);
		pb.setPc(pc);
		pb.setPs(ps);
		pb.setTr(tr);
		return pb;
	}
	//Ϊָ����order���������е�orderItem
	private void loadOrderItem(Order order) throws SQLException {
		String sql = "select * from t_orderitem where oid=?";
		List<Map<String,Object>> mapList = qr.query(sql, new MapListHandler(),order.getOid());
		List<OrderItem> orderItemList = toOrderItemList(mapList);
		order.setOrderItemList(orderItemList);
		
	}
	//�Ѷ��mapת���ɶ��OrderItem
	private List<OrderItem> toOrderItemList(List<Map<String, Object>> mapList) {
		List<OrderItem> orderItemList = new ArrayList<OrderItem>();
		for(Map<String,Object> map : mapList){
			OrderItem orderItem = toOrderItem(map);
			orderItemList.add(orderItem);
		}
		return orderItemList;
	}
//��һ��mapת����һ��OrderItem
	private OrderItem toOrderItem(Map<String, Object> map) {
		OrderItem orderItem = CommonUtils.toBean(map, OrderItem.class);
		Book book = CommonUtils.toBean(map, Book.class);
		orderItem.setBook(book);
		return orderItem;
	}
}
