package shop.admin.admin.domain;

public class Admin {
	private String adminId; //����
	private String adminname;//����Ա��¼��
	private String adminpwd;//����Ա��¼����
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
