package shop.admin.admin.service;

import java.sql.SQLException;

import shop.admin.admin.dao.AdminDao;
import shop.admin.admin.domain.Admin;

public class AdminService {
	private AdminDao adminDao =new AdminDao();
	public Admin login(Admin admin){
		try {
			return adminDao.find(admin.getAdminname(), admin.getAdminpwd());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
