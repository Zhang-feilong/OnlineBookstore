package shop.user.domain;
/*�û�ģ��ʵ����*/
public class User {
	
	/*��Ӧ���ݿ��*/
	private String uid;/*����*/
	private String loginname;/*��¼��*/
	private String loginpass;/*��½����*/
	private String email;/*����*/
	private boolean status;/*true ��ʾ�������δ����*/
	private String activationCode;/*�����룬Ψһ��*/
	
	/*ע���*/
	private String reloginpass;/*ȷ������*/
	private String verifyCode;;/*��֤��*/
	
	/*�޸������*/
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
