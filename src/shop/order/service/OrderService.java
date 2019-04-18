package shop.order.service;

import java.sql.SQLException;

import cn.itcast.jdbc.JdbcUtils;

import shop.order.dao.OrderDao;
import shop.order.domain.Order;
import shop.pager.PageBean;

public class OrderService {
	private OrderDao orderDao = new OrderDao();
	//��ѯ����״̬
	public int findStatus(String oid){
		try {
			return orderDao.findStatus(oid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	//�޸Ķ���״̬
		public void updateStatus(String oid,int status){
			try {
				 orderDao.updateStatus(oid, status);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	//���ض���
	public Order load(String oid){
		try {
			JdbcUtils.beginTransaction();
			Order order = orderDao.load(oid);
			JdbcUtils.commitTransaction();
			return order;
		} catch (SQLException e) {
			try {
				JdbcUtils.rollbackTransaction();
			} catch (SQLException e1) {}
				throw new RuntimeException(e);
		}
	}
	//���ɶ���
	public void createOrder(Order order){
		try {
			JdbcUtils.beginTransaction();
			orderDao.add(order);
			JdbcUtils.commitTransaction();
		} catch (SQLException e) {
			try {
				JdbcUtils.rollbackTransaction();
			} catch (SQLException e1) {}
				throw new RuntimeException(e);
			
			
		}
	}
	
	//�ҵĶ���
	public PageBean<Order> myOrders(String uid,int pc){
		try {
			JdbcUtils.beginTransaction();
			PageBean<Order> pb = orderDao.findByUser(uid, pc);
			JdbcUtils.commitTransaction();
			return pb;
		} catch (SQLException e) {
			try {
				JdbcUtils.rollbackTransaction();
			} catch (SQLException e1) {}
				throw new RuntimeException(e);
			
			
		}
	}
	
	//��״̬��ѯ
	public PageBean<Order> findByStatus(int status,int pc){
		try {
			JdbcUtils.beginTransaction();
			PageBean<Order> pb = orderDao.findByStatus(status, pc);
			JdbcUtils.commitTransaction();
			return pb;
		} catch (SQLException e) {
			try {
				JdbcUtils.rollbackTransaction();
			} catch (SQLException e1) {}
				throw new RuntimeException(e);
			
			
		}
	}
	
	//��ѯ����
	public PageBean<Order> findAll(int pc){
		try {
			JdbcUtils.beginTransaction();
			PageBean<Order> pb = orderDao.findAll(pc);
			JdbcUtils.commitTransaction();
			return pb;
		} catch (SQLException e) {
			try {
				JdbcUtils.rollbackTransaction();
			} catch (SQLException e1) {}
				throw new RuntimeException(e);
			
			
		}
	}
}
