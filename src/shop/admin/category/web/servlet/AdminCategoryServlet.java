package shop.admin.category.web.servlet;



import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import shop.book.service.BookService;
import shop.category.domain.Category;
import shop.category.service.CategoryService;
import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;

public class AdminCategoryServlet extends BaseServlet {
	private CategoryService categoryService = new CategoryService();
	private BookService bookService = new BookService();
	//查询所有分类
	public String findAll(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		req.setAttribute("parents", categoryService.findAll());
		return "f:/adminjsps/admin/category/list.jsp";
	}
	//添加一级分类
	public String addParent(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		//1.封装表单数据到category
		//2.调用service方法完成添加
		//3. 调用findAll方法显示所有分类
		Category parent = CommonUtils.toBean(req.getParameterMap(), Category.class);
		parent.setCid(CommonUtils.uuid());
		categoryService.add(parent);;
		return findAll(req,resp);
	}
	//添加二级分类第一步
	public String addChildPre(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		String pid  = req.getParameter("pid");
		List<Category> parents = categoryService.findParents();
		req.setAttribute("pid", pid);
		req.setAttribute("parents", parents);
		return "f:/adminjsps/admin/category/add2.jsp";
	}
	
	public String addChild(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		//1.封装表单数据到category
		//2.需要手动把表单中的pid映射到child中
		//3.调用service方法完成添加
		//4. 调用findAll方法显示所有分类
		Category child = CommonUtils.toBean(req.getParameterMap(), Category.class);
		child.setCid(CommonUtils.uuid());
		String pid = req.getParameter("pid");
		Category parent = new Category();
		parent.setCid(pid);
		child.setParent(parent);
		categoryService.add(child);;
		return findAll(req,resp);
	}
	
	//修改一级分类第一步
	public String editParentPre(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		//1.获取链接中的cid
		//2.使用cid加载Category
		//3.保存并转发到edit.jsp显示
		String cid = req.getParameter("cid");
		Category parent = categoryService.load(cid);
		req.setAttribute("parent", parent);
		return "f:/adminjsps/admin/category/edit.jsp";
		
		
	}
	//修改一级分类第二步
	public String editParent(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		/*1.封装表单数据到Category
		 * 调用service完成修改
		 * 转发到list.jsp显示所有分类
		 * */
		Category parent = CommonUtils.toBean(req.getParameterMap(), Category.class);
		categoryService.edit(parent);
		return findAll(req,resp);
	}
	//修改二级分类第一步
	public String editChildPre(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
			//1.获取链接中的cid
			//2.使用cid加载Category，查询所有一级分类，保存
			//3.保存并转发到edit.jsp显示
		String cid = req.getParameter("cid");
		Category child = categoryService.load(cid);
		req.setAttribute("child", child);
		req.setAttribute("parents",categoryService.findParents());
		return "f:/adminjsps/admin/category/edit2.jsp";
	}
	
	//修改二级分类第二步
		public String editChild(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException, SQLException {
				/*1.封装表单数据到Category,把表单中的pid也封装到child中
				 * 调用service完成修改
				 * 转发到list.jsp显示所有分类
				 * */ 
			Category child = CommonUtils.toBean(req.getParameterMap(), Category.class);
			String pid = req.getParameter("pid");
			Category parent = new Category();
			parent.setCid(pid);
			child.setParent(parent);
			
			categoryService.edit(child);
			return findAll(req,resp);
		}
		//删除一级分类
		public String deleteParent(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException, SQLException {
			//1.获取链接中的cid
			//2.使用cid查询该分类下的子分类个数
			//3.如果大于0，不能删除，如果等于0 删除后返回list.jsp
			String cid= req.getParameter("cid");
			int cnt = categoryService.findChildrenCountByParent(cid);
			if(cnt >0){
				req.setAttribute("msg", "该分类下还有子分类，不能删除！");
				return "f:/adminjsps/msg.jsp";
			}else{
				categoryService.delete(cid);
				return findAll(req,resp);
			}
		}
		
		//删除e二级分类
				public String deleteChild(HttpServletRequest req, HttpServletResponse resp)
						throws ServletException, IOException, SQLException {
					//1.获取链接中的cid
					//2.使用cid查询该分类下的图书个数
					//3.如果大于0，不能删除，如果等于0 删除后返回list.jsp
					String cid= req.getParameter("cid");
					int cnt = bookService.findBookCountByCategory(cid);
					if(cnt >0){
						req.setAttribute("msg", "该分类下还有图书，不能删除！");
						return "f:/adminjsps/msg.jsp";
					}else{
						categoryService.delete(cid);
						return findAll(req,resp);
					}
				}
}
