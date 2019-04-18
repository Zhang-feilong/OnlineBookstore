package shop.order.domain;

import java.util.List;

import shop.user.domain.User;


public class Order {
	private String oid;//����
	private String ordertime;//�µ�ʱ��
	private double total;//�ܼ�
	private int status;//����״̬��1 δ���� 2�Ѹ��δ������3.�ѷ�����ûȷ���ջ�4.ȷ���ջ��쵼�����׳ɹ�5.ȡ������
	private String address;//�ջ���ַ
	private User owner;//������������
	private List<OrderItem> orderItemList;
	
	
	public List<OrderItem> getOrderItemList() {
		return orderItemList;
	}
	public void setOrderItemList(List<OrderItem> orderItemList) {
		this.orderItemList = orderItemList;
	}
	public String getOid() {
		return oid;
	}
	public void setOid(String oid) {
		this.oid = oid;
	}
	public String getOrdertime() {
		return ordertime;
	}
	public void setOrdertime(String ordertime) {
		this.ordertime = ordertime;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public User getOwner() {
		return owner;
	}
	public void setOwner(User owner) {
		this.owner = owner;
	}
}
