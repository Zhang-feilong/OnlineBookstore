package shop.admin.book.web.servlet;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import shop.book.domain.Book;
import shop.book.service.BookService;
import shop.category.domain.Category;
import shop.category.service.CategoryService;
import shop.pager.PageBean;
import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;

public class AdminBookServlet extends BaseServlet {
	private BookService bookService = new BookService();
	private CategoryService categoryService = new CategoryService();
	/*显示所有分类*/
	public String findCategoryAll(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		/*1.通过service得到所有分类
		 * 2.保存到request中，转发到left.jsp
		 * */
		List<Category> parents = categoryService.findAll();
		req.setAttribute("parents", parents);
		return "f:/adminjsps/admin/book/left.jsp";
	}
	
	/*获取当前页码*/
	private int getPc(HttpServletRequest req){
		int pc = 1;
		String param = req.getParameter("pc");
		if(param !=null && !param.trim().isEmpty()){
			try{
				pc = Integer.parseInt(param);
			}catch(RuntimeException e){
			}
		}
		return pc;
	}
	/*截取url 分页导航需要使用它作为超链接的目标*/
	private String getUrl(HttpServletRequest req){
		String url = req.getRequestURI() + "?" +  req.getQueryString();
		int index = url.lastIndexOf("&pc=");
		if(index !=-1){
			url = url.substring(0,index);
		}
		return url;
	}
	
	//按分类查询
	public String findByCategory(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException, SQLException {
			//得到pc，如果页面传递，使用页面的，否则等于1
			int pc = getPc(req);
			//得到url
			String url = getUrl(req);
			//获取查询条件本方法就是cid,
			String cid = req.getParameter("cid");
			
			//使用pc和cid调用service中的findByCatrgBook法得到PageBean
			PageBean<Book> pb = bookService.findByCategory(cid, pc);
			//给pageBean设置url，保存PageBean,转发到/jsp/book/list.jsp
			pb.setUrl(url);
			req.setAttribute("pb", pb);
			return "f:/adminjsps/admin/book/list.jsp";
		}
	//按作者查
	public String findByAuthor(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		//得到pc，如果页面传递，使用页面的，否则等于1
		int pc = getPc(req);
		//得到url
		String url = getUrl(req);
		//获取查询条件本方法就是author,
		String author = req.getParameter("author");
		
		//使用pc和cid调用service中的findByCatrgBook法得到PageBean
		PageBean<Book> pb = bookService.findByAuthor(author, pc);
		//给pageBean设置url，保存PageBean,转发到/jsp/book/list.jsp
		pb.setUrl(url);
		req.setAttribute("pb", pb);
		return "f:/adminjsps/admin/book/list.jsp";
	}
	
	
	//按出版社查询
	public String findByPress(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException, SQLException {
			//得到pc，如果页面传递，使用页面的，否则等于1
			int pc = getPc(req);
			//得到url
			String url = getUrl(req);
			//获取查询条件本方法就是author,
			String press = req.getParameter("press");
			
			//使用pc和cid调用service中的findByCatrgBook法得到PageBean
			PageBean<Book> pb = bookService.findByPress(press, pc);
			//给pageBean设置url，保存PageBean,转发到/jsp/book/list.jsp
			pb.setUrl(url);
			req.setAttribute("pb", pb);
			return "f:/adminjsps/admin/book/list.jsp";
	}
		//按书名查询
	public String findByBname(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
			//得到pc，如果页面传递，使用页面的，否则等于1
			int pc = getPc(req);
			//得到url
			String url = getUrl(req);
			//获取查询条件本方法就是author,
			String bname = req.getParameter("bname");
				
			//使用pc和cid调用service中的findByCatrgBook法得到PageBean
			PageBean<Book> pb = bookService.findByBname(bname, pc);
			//给pageBean设置url，保存PageBean,转发到/jsp/book/list.jsp
			pb.setUrl(url);
			req.setAttribute("pb", pb);
			return "f:/adminjsps/admin/book/list.jsp";	
	}
	//多组合条件查询
	public String findByCombination(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException, SQLException {
			//得到pc，如果页面传递，使用页面的，否则等于1
			int pc = getPc(req);
			//得到url
			String url = getUrl(req);
			//获取查询条件本
			Book criteria = CommonUtils.toBean(req.getParameterMap(), Book.class);
			
			//使用pc和cid调用service中的findByCatrgBook法得到PageBean
			PageBean<Book> pb = bookService.findByCombination(criteria, pc);
			//给pageBean设置url，保存PageBean,转发到/jsp/book/list.jsp
			pb.setUrl(url);
			req.setAttribute("pb", pb);
			return "f:/adminjsps/admin/book/list.jsp";
	}
	
	//添加图书第一步
	public String addPre(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		//1.获取所有一级分类。保存2.转发到add.jsp该页面会在下拉列表中显示所有一级分类
		List<Category> parents = categoryService.findParents();
		req.setAttribute("parents",parents);
		
		return "f:/adminjsps/admin/book/add.jsp";
	}
	public String ajaxFindChildren(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		/*
		 * 1.获取pid
		 * 2.通过pid查询所有二级分类
		 * 3.把List<Category>转换为json,输出给客户端*/
		String pid = req.getParameter("pid");
		List<Category> children = categoryService.findChildren(pid);
		String json = Json(children);
		resp.getWriter().print(json);
		return null;
	}
	private String toJson(Category category){
		StringBuilder sb = new StringBuilder("{");
		sb.append("\"cid\"").append(":").append("\"").append(category.getCid()).append("\"");
		sb.append(",");
		sb.append("\"cname\"").append(":").append("\"").append(category.getCname()).append("\"");	
		sb.append("}");
		return sb.toString();
	}
	private String Json(List<Category> categoryList ){
		StringBuilder sb = new StringBuilder("[");
		for(int i=0;i<categoryList.size();i++){
			sb.append(toJson(categoryList.get(i)));
			if(i<categoryList.size()-1){
				sb.append(",");
			}
		}
		sb.append("]");
		return sb.toString();
	}
	//加载图书
	public String load(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		//获取bid得到对象，然后保存
		String bid = req.getParameter("bid");
		Book book = bookService.load(bid);
		req.setAttribute("book", book);
		//获取所有一级分类，然后保存
		req.setAttribute("parents",categoryService.findParents());
		//获取当前图书所属一级分类的所有二级分类
		String pid = book.getCategory().getParent().getCid();
		req.setAttribute("children", categoryService.findChildren(pid));
		return "f:/adminjsps/admin/book/desc.jsp";
	}
	//修改图书
	public String edit(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		//把表单数据封装到BOOK对象中
		//封装cid到category
		//把category赋给book，调用service完成修改，保存成功信息到msg.jsp
		Map map = req.getParameterMap();
		Book book = CommonUtils.toBean(map, Book.class);
		Category category = CommonUtils.toBean(map, Category.class);
		book.setCategory(category);
		bookService.edit(book);
		req.setAttribute("msg","修改图书成功！");
		return "f:/adminjsps/msg.jsp";
	}
	//删除图书
	public String delete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		String bid = req.getParameter("bid");
		Book  book = bookService.load(bid);
		String savepath = this.getServletContext().getRealPath("/");//获取真实路径
		new File(savepath,book.getImage_w()).delete();//删除文件
		new File(savepath,book.getImage_b()).delete();//删除文件
		bookService.delete(bid);
		req.setAttribute("msg","删除图书成功！");
		return "f:/adminjsps/msg.jsp";
	}
}
