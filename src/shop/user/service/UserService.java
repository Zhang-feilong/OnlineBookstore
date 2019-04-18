package shop.user.service;

import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;

import cn.itcast.commons.CommonUtils;
import cn.itcast.mail.Mail;
import cn.itcast.mail.MailUtils;

import shop.user.dao.UserDao;
import shop.user.domain.User;
import shop.user.service.exception.UserException;

public class UserService {
	private UserDao userDao = new UserDao();
	/*用户名注册校验*/
	public boolean ajaxValidateLoginname(String loginname) throws SQLException{
		try{ 
			return userDao.ajaxValidateLoginname(loginname);
		}catch(SQLException e){
			throw new RuntimeException(e);
		}
	}
	/*email注册校验*/
	public boolean ajaxValidateEmail(String email) throws SQLException{
		try{ 
			return userDao.ajaxValidateEmail(email);
		}catch(SQLException e){
			throw new RuntimeException(e);
		}
	}
	/*注册功能*/
	public void regist(User user){
		/*数据补齐*/
		user.setUid(CommonUtils.uuid());
		user.setStatus(false);
		user.setActivationCode(CommonUtils.uuid()+CommonUtils.uuid());
		/*向数据库插入*/
		try {
			userDao.add(user);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		/*发邮件*/
		Properties prop = new Properties();
		try {
			prop.load(this.getClass().getClassLoader().getResourceAsStream("email_template.properties"));
		} catch (IOException e1) {
			throw new RuntimeException(e1);
		}
		/*登录邮件服务器，得到session*/
		String host = prop.getProperty("host");
		String name = prop.getProperty("username");
		String pass = prop.getProperty("password");
		Session session =MailUtils.createSession(host,name,pass);
		/*创建邮件对象*/
		String from =prop.getProperty("from");
		String to =user.getEmail();
		String subject = prop.getProperty("subject");
		String content = MessageFormat.format(prop.getProperty("content"),user.getActivationCode()) ;
		Mail mail = new Mail(from,to,subject,content);
		try {
			MailUtils.send(session, mail);
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
		
			throw new RuntimeException(e);
		}
	}
	
	/*激活功能*/
	public void activation(String code) throws UserException{
		/*通过激活码查询用户，如果用户为null，抛出异常给出异常信息（无效激活码*/
		/*查看用户状态是否为true，如果为true，抛出异常给出异常信息（请不要二次激活*/
		/*修改用户状态为true*/
		try{
			User user =userDao.findByCode(code);
			if(user==null) throw new UserException("无效的激活码!");
			if(user.isStatus()) throw new UserException("您已经激活过了，请不要二次激活！");
			userDao.updateStatus(user.getUid(), true);/*修改状态*/
		}catch(SQLException e){
			throw new RuntimeException(e);
		}
		
	}
	/*登录功能*/
	public User login(User user){
		try {
			return userDao.findByLoginnameAndLoginpass(user.getLoginname(),user.getLoginpass());
		} catch (SQLException e) {
			
			throw new RuntimeException(e);
		}
	}
	/*修改密码*/
	public void updatePassword(String uid,String newPass,String oldPass) throws UserException{
		/*校验老密码
		 * 修改密码
		 * 
		 * */
		try {
			boolean bool = userDao.findByUidAndPassword(uid, oldPass);
			if(!bool){
				throw new UserException("老密码错误");
			}
			userDao.updatePassword(uid, newPass);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
