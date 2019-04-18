package shop.admin.admin.domain;

public class Admin {
	private String adminId; //主键
	private String adminname;//管理员登录名
	private String adminpwd;//管理员登录密码
	public String getAdminId() {
		return adminId;
	}
	public void setAdminId(String adminId) {
		this.adminId = adminId;
	}
	public String getAdminname() {
		return adminname;
	}
	public void setAdminname(String adminname) {
		this.adminname = adminname;
	}
	public String getAdminpwd() {
		return adminpwd;
	}
	public void setAdminpwd(String adminpwd) {
		this.adminpwd = adminpwd;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Admin [adminId=");
		builder.append(adminId);
		builder.append(", adminname=");
		builder.append(adminname);
		builder.append(", adminpwd=");
		builder.append(adminpwd);
		builder.append("]");
		return builder.toString();
	}
	
}
