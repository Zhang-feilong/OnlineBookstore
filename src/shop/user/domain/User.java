package shop.user.domain;
/*用户模块实现类*/
public class User {
	
	/*对应数据库表*/
	private String uid;/*主键*/
	private String loginname;/*登录名*/
	private String loginpass;/*登陆密码*/
	private String email;/*邮箱*/
	private boolean status;/*true 表示激活，或者未激活*/
	private String activationCode;/*激活码，唯一的*/
	
	/*注册表单*/
	private String reloginpass;/*确认密码*/
	private String verifyCode;;/*验证码*/
	
	/*修改密码表单*/
	private String newloginpass;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getLoginname() {
		return loginname;
	}

	public void setLoginname(String loginname) {
		this.loginname = loginname;
	}

	public String getLoginpass() {
		return loginpass;
	}

	public void setLoginpass(String loginpass) {
		this.loginpass = loginpass;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getActivationCode() {
		return activationCode;
	}

	public void setActivationCode(String activationCode) {
		this.activationCode = activationCode;
	}

	public String getReloginpass() {
		return reloginpass;
	}

	public void setReloginpass(String reloginpass) {
		this.reloginpass = reloginpass;
	}

	public String getVerifyCode() {
		return verifyCode;
	}

	public void setVerifyCode(String verifyCode) {
		this.verifyCode = verifyCode;
	}

	public String getNewloginpass() {
		return newloginpass;
	}

	public void setNewloginpass(String newloginpass) {
		this.newloginpass = newloginpass;
	}
}
