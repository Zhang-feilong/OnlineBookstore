package shop.book.web.servlet;



import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import shop.book.domain.Book;
import shop.book.service.BookService;
import shop.pager.PageBean;
import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;

public class BookServlet extends BaseServlet {
	private BookService bookService = new BookService();
	
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
			return "f:/jsps/book/list.jsp";
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
		return "f:/jsps/book/list.jsp";
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
			return "f:/jsps/book/list.jsp";
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
			return "f:/jsps/book/list.jsp";	
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
			return "f:/jsps/book/list.jsp";
	}
	
//	通过bid查询
	public String load(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		String bid = req.getParameter("bid");
		Book book = bookService.load(bid);
		req.setAttribute("book", book);
		return "f:/jsps/book/desc.jsp";
	}
	
		
}
	
	

