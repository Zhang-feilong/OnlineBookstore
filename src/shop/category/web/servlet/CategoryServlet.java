package shop.category.web.servlet;

/*分类模块的web层*/

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import shop.category.domain.Category;
import shop.category.service.CategoryService;
import cn.itcast.servlet.BaseServlet;

public class CategoryServlet extends BaseServlet {
	private CategoryService categoryService = new CategoryService();
	/*查询所有分类*/
	public String findAll(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		/*1.通过service得到所有分类
		 * 2.保存到request中，转发到left.jsp
		 * */
		List<Category> parents = categoryService.findAll();
		req.setAttribute("parents", parents);
		return "f:/jsps/left.jsp";
	}
}
