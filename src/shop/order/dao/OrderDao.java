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
	
	//查询订单状态
	public int findStatus(String oid) throws SQLException{
		String sql = "select status from t_order  where oid=?";
		Number number = (Number)qr.query(sql, new ScalarHandler(),oid);
		return number.intValue();
	}
	
	//修改订单状态
	public void updateStatus(String oid,int status) throws SQLException{
		String sql = "update t_order set status=? where oid=?";
		qr.update(sql,status,oid);
	}
	
	//加载订单
	public Order load(String oid) throws SQLException{
		String sql = "select * from t_order where oid=?";
		Order order = qr.query(sql, new BeanHandler<Order>(Order.class),oid);
		loadOrderItem(order);//为当前订单加载所有订单条目
		return order;
	}
	//生成订单
	public void add(Order order) throws SQLException{
		//插入订单
		String sql = "insert into t_order values(?,?,?,?,?,?)";
		Object[] params = {order.getOid(),order.getOrdertime(),order.getTotal(),
				order.getStatus(),order.getAddress(),order.getOwner().getUid()
		};
		qr.update(sql,params);
//		2.循环遍历订单的所有条目，让每个条目生成一个Object[]
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
	/*按用户查询订单*/
	
	public PageBean<Order> findByUser(String uid ,int pc) throws SQLException{
		List<Expression> exprList = new ArrayList<Expression>();
		exprList.add(new Expression("uid","=",uid));
		return findByCriteria(exprList,pc);
	}
	
	//查询所有
	public PageBean<Order> findAll(int pc) throws SQLException{
		List<Expression> exprList = new ArrayList<Expression>();
		return findByCriteria(exprList,pc);
	}
	
	//按状态查询
	public PageBean<Order> findByStatus(int status ,int pc) throws SQLException{
		List<Expression> exprList = new ArrayList<Expression>();
		exprList.add(new Expression("status","=",status+""));
		return findByCriteria(exprList,pc);
	}
	
	private PageBean<Order> findByCriteria(List<Expression> exprList,int pc) throws SQLException{
		/*1.得到ps
		 * 2.得到tr
		 * 3.得到beanList
		 * 4.创建PageBean返回
		 * */
		int ps = PageConstants.ORDER_PAGE_SIZE;//每页记录数
		StringBuilder whereSql = new StringBuilder(" where 1=1");
		List<Object> params = new ArrayList<Object>();//对应sql中问号的值
		for(Expression expr: exprList){
			/*
			 * 添加一个条件
			 * 以and开头，条件的名称，条件运算符可以是 =、>、< is null  is null 没有值
			 * 如果条件不是is null 再追加问号，然后再向params中添加一个与问号对应的值
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
		params.add((pc-1)*ps);//当前页首行记录的下表
		params.add(ps);//一共查询几行，就是每页记录数
		
		List<Order> beanList =  qr.query(sql, new BeanListHandler<Order>(Order.class),
				params.toArray());
		//虽然获取了所有订单，但每个订单并没有条目，遍历每个订单为其加载所有订单条目
		for(Order order : beanList){
			loadOrderItem(order);
		}
		//创建PageBean,s设置参数
		PageBean<Order> pb= new PageBean<Order>();
		//其中PageBean 没有url 这个任务由Service完成
		pb.setBeanList(beanList);
		pb.setPc(pc);
		pb.setPs(ps);
		pb.setTr(tr);
		return pb;
	}
	//为指定的order加载它所有的orderItem
	private void loadOrderItem(Order order) throws SQLException {
		String sql = "select * from t_orderitem where oid=?";
		List<Map<String,Object>> mapList = qr.query(sql, new MapListHandler(),order.getOid());
		List<OrderItem> orderItemList = toOrderItemList(mapList);
		order.setOrderItemList(orderItemList);
		
	}
	//把多个map转换成多个OrderItem
	private List<OrderItem> toOrderItemList(List<Map<String, Object>> mapList) {
		List<OrderItem> orderItemList = new ArrayList<OrderItem>();
		for(Map<String,Object> map : mapList){
			OrderItem orderItem = toOrderItem(map);
			orderItemList.add(orderItem);
		}
		return orderItemList;
	}
//把一个map转换成一个OrderItem
	private OrderItem toOrderItem(Map<String, Object> map) {
		OrderItem orderItem = CommonUtils.toBean(map, OrderItem.class);
		Book book = CommonUtils.toBean(map, Book.class);
		orderItem.setBook(book);
		return orderItem;
	}
}
