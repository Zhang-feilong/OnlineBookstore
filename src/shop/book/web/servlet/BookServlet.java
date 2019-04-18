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
	
	/*��ȡ��ǰҳ��*/
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
	/*��ȡurl ��ҳ������Ҫʹ������Ϊ�����ӵ�Ŀ��*/
	private String getUrl(HttpServletRequest req){
		String url = req.getRequestURI() + "?" +  req.getQueryString();
		int index = url.lastIndexOf("&pc=");
		if(index !=-1){
			url = url.substring(0,index);
		}
		return url;
	}
	
	//�������ѯ
	public String findByCategory(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException, SQLException {
			//�õ�pc�����ҳ�洫�ݣ�ʹ��ҳ��ģ��������1
			int pc = getPc(req);
			//�õ�url
			String url = getUrl(req);
			//��ȡ��ѯ��������������cid,
			String cid = req.getParameter("cid");
			
			//ʹ��pc��cid����service�е�findByCatrgBook���õ�PageBean
			PageBean<Book> pb = bookService.findByCategory(cid, pc);
			//��pageBean����url������PageBean,ת����/jsp/book/list.jsp
			pb.setUrl(url);
			req.setAttribute("pb", pb);
			return "f:/jsps/book/list.jsp";
		}
	//�����߲�
	public String findByAuthor(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		//�õ�pc�����ҳ�洫�ݣ�ʹ��ҳ��ģ��������1
		int pc = getPc(req);
		//�õ�url
		String url = getUrl(req);
		//��ȡ��ѯ��������������author,
		String author = req.getParameter("author");
		
		//ʹ��pc��cid����service�е�findByCatrgBook���õ�PageBean
		PageBean<Book> pb = bookService.findByAuthor(author, pc);
		//��pageBean����url������PageBean,ת����/jsp/book/list.jsp
		pb.setUrl(url);
		req.setAttribute("pb", pb);
		return "f:/jsps/book/list.jsp";
	}
	
	
	//���������ѯ
	public String findByPress(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException, SQLException {
			//�õ�pc�����ҳ�洫�ݣ�ʹ��ҳ��ģ��������1
			int pc = getPc(req);
			//�õ�url
			String url = getUrl(req);
			//��ȡ��ѯ��������������author,
			String press = req.getParameter("press");
			
			//ʹ��pc��cid����service�е�findByCatrgBook���õ�PageBean
			PageBean<Book> pb = bookService.findByPress(press, pc);
			//��pageBean����url������PageBean,ת����/jsp/book/list.jsp
			pb.setUrl(url);
			req.setAttribute("pb", pb);
			return "f:/jsps/book/list.jsp";
	}
		//��������ѯ
	public String findByBname(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
			//�õ�pc�����ҳ�洫�ݣ�ʹ��ҳ��ģ��������1
			int pc = getPc(req);
			//�õ�url
			String url = getUrl(req);
			//��ȡ��ѯ��������������author,
			String bname = req.getParameter("bname");
				
			//ʹ��pc��cid����service�е�findByCatrgBook���õ�PageBean
			PageBean<Book> pb = bookService.findByBname(bname, pc);
			//��pageBean����url������PageBean,ת����/jsp/book/list.jsp
			pb.setUrl(url);
			req.setAttribute("pb", pb);
			return "f:/jsps/book/list.jsp";	
	}
	//�����������ѯ
	public String findByCombination(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException, SQLException {
			//�õ�pc�����ҳ�洫�ݣ�ʹ��ҳ��ģ��������1
			int pc = getPc(req);
			//�õ�url
			String url = getUrl(req);
			//��ȡ��ѯ������
			Book criteria = CommonUtils.toBean(req.getParameterMap(), Book.class);
			
			//ʹ��pc��cid����service�е�findByCatrgBook���õ�PageBean
			PageBean<Book> pb = bookService.findByCombination(criteria, pc);
			//��pageBean����url������PageBean,ת����/jsp/book/list.jsp
			pb.setUrl(url);
			req.setAttribute("pb", pb);
			return "f:/jsps/book/list.jsp";
	}
	
//	ͨ��bid��ѯ
	public String load(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		String bid = req.getParameter("bid");
		Book book = bookService.load(bid);
		req.setAttribute("book", book);
		return "f:/jsps/book/desc.jsp";
	}
	
		
}
	
	

