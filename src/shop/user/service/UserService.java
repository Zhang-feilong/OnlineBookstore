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
	/*�û���ע��У��*/
	public boolean ajaxValidateLoginname(String loginname) throws SQLException{
		try{ 
			return userDao.ajaxValidateLoginname(loginname);
		}catch(SQLException e){
			throw new RuntimeException(e);
		}
	}
	/*emailע��У��*/
	public boolean ajaxValidateEmail(String email) throws SQLException{
		try{ 
			return userDao.ajaxValidateEmail(email);
		}catch(SQLException e){
			throw new RuntimeException(e);
		}
	}
	/*ע�Ṧ��*/
	public void regist(User user){
		/*���ݲ���*/
		user.setUid(CommonUtils.uuid());
		user.setStatus(false);
		user.setActivationCode(CommonUtils.uuid()+CommonUtils.uuid());
		/*�����ݿ����*/
		try {
			userDao.add(user);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		/*���ʼ�*/
		Properties prop = new Properties();
		try {
			prop.load(this.getClass().getClassLoader().getResourceAsStream("email_template.properties"));
		} catch (IOException e1) {
			throw new RuntimeException(e1);
		}
		/*��¼�ʼ����������õ�session*/
		String host = prop.getProperty("host");
		String name = prop.getProperty("username");
		String pass = prop.getProperty("password");
		Session session =MailUtils.createSession(host,name,pass);
		/*�����ʼ�����*/
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
	
	/*�����*/
	public void activation(String code) throws UserException{
		/*ͨ���������ѯ�û�������û�Ϊnull���׳��쳣�����쳣��Ϣ����Ч������*/
		/*�鿴�û�״̬�Ƿ�Ϊtrue�����Ϊtrue���׳��쳣�����쳣��Ϣ���벻Ҫ���μ���*/
		/*�޸��û�״̬Ϊtrue*/
		try{
			User user =userDao.findByCode(code);
			if(user==null) throw new UserException("��Ч�ļ�����!");
			if(user.isStatus()) throw new UserException("���Ѿ�������ˣ��벻Ҫ���μ��");
			userDao.updateStatus(user.getUid(), true);/*�޸�״̬*/
		}catch(SQLException e){
			throw new RuntimeException(e);
		}
		
	}
	/*��¼����*/
	public User login(User user){
		try {
			return userDao.findByLoginnameAndLoginpass(user.getLoginname(),user.getLoginpass());
		} catch (SQLException e) {
			
			throw new RuntimeException(e);
		}
	}
	/*�޸�����*/
	public void updatePassword(String uid,String newPass,String oldPass) throws UserException{
		/*У��������
		 * �޸�����
		 * 
		 * */
		try {
			boolean bool = userDao.findByUidAndPassword(uid, oldPass);
			if(!bool){
				throw new UserException("���������");
			}
			userDao.updatePassword(uid, newPass);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
