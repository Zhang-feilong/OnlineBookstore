package shop.user.dao;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import shop.user.domain.User;

import cn.itcast.jdbc.TxQueryRunner;

/*用户模块持久层*/
public class UserDao {
	private QueryRunner qr = new TxQueryRunner();
	
	/* 检验用户名是否注册*/
	public boolean ajaxValidateLoginname(String loginname) throws SQLException{
		String sql ="select count(1) from t_user where loginname=?";
		Number number = (Number)qr.query(sql,new ScalarHandler(),loginname);
		return number.intValue()==0;
	}
	/*检验邮箱是否注册*/
	public boolean ajaxValidateEmail(String email) throws SQLException{
		String sql ="select count(1) from t_user where email=?";
		Number number = (Number)qr.query(sql,new ScalarHandler(),email);
		return number.intValue()==0;
	}
	
	/*添加用户*/
	public void add(User user) throws SQLException{
		
		String sql = "insert into t_user values(?,?,?,?,?,?)";
		Object[] params = {user.getUid(),user.getLoginname(),user.getLoginpass(),user.getEmail(),user.isStatus(),
				user.getActivationCode()};
		qr.update(sql,params);
		//System.out.println();
	}
	
	/*通过激活码查询业务*/
	public User findByCode(String code) throws SQLException{
		String sql = "select * from t_user where activationCode=?";
		return qr.query(sql,new BeanHandler<User>(User.class),code);
	}
	
	/*修改用户的状态*/
	public void updateStatus(String uid,boolean status) throws SQLException{
		String sql = "update t_user set status=? where uid=?";
		qr.update(sql,status,uid);
		
	}
	
	
	public User findByLoginnameAndLoginpass(String loginname,String loginpass) throws SQLException{
		String sql = "select * from t_user where loginname=? and loginpass=?";
		return qr.query(sql,new  BeanHandler<User>(User.class),loginname, loginpass);
		
		
	}
	/*按uid 和密码查询用户*/
	public boolean findByUidAndPassword(String uid,String password) throws SQLException{
		String sql = "select count(*) from t_user where uid=? and loginpass=?";
		Number number = (Number)qr.query(sql,new ScalarHandler(),uid,password);
		return number.intValue()>0; 
	}
	/*修改密码*/
	public void updatePassword(String uid,String password) throws SQLException{
		String sql = "update t_user set loginpass=? where uid=?";
		qr.update(sql, password,uid);
	}
	
}
