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
	/*��ʾ���з���*/
	public String findCategoryAll(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		/*1.ͨ��service�õ����з���
		 * 2.���浽request�У�ת����left.jsp
		 * */
		List<Category> parents = categoryService.findAll();
		req.setAttribute("parents", parents);
		return "f:/adminjsps/admin/book/left.jsp";
	}
	
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
			return "f:/adminjsps/admin/book/list.jsp";
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
		return "f:/adminjsps/admin/book/list.jsp";
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
			return "f:/adminjsps/admin/book/list.jsp";
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
			return "f:/adminjsps/admin/book/list.jsp";	
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
			return "f:/adminjsps/admin/book/list.jsp";
	}
	
	//���ͼ���һ��
	public String addPre(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		//1.��ȡ����һ�����ࡣ����2.ת����add.jsp��ҳ����������б�����ʾ����һ������
		List<Category> parents = categoryService.findParents();
		req.setAttribute("parents",parents);
		
		return "f:/adminjsps/admin/book/add.jsp";
	}
	public String ajaxFindChildren(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		/*
		 * 1.��ȡpid
		 * 2.ͨ��pid��ѯ���ж�������
		 * 3.��List<Category>ת��Ϊjson,������ͻ���*/
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
	//����ͼ��
	public String load(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		//��ȡbid�õ�����Ȼ�󱣴�
		String bid = req.getParameter("bid");
		Book book = bookService.load(bid);
		req.setAttribute("book", book);
		//��ȡ����һ�����࣬Ȼ�󱣴�
		req.setAttribute("parents",categoryService.findParents());
		//��ȡ��ǰͼ������һ����������ж�������
		String pid = book.getCategory().getParent().getCid();
		req.setAttribute("children", categoryService.findChildren(pid));
		return "f:/adminjsps/admin/book/desc.jsp";
	}
	//�޸�ͼ��
	public String edit(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		//�ѱ����ݷ�װ��BOOK������
		//��װcid��category
		//��category����book������service����޸ģ�����ɹ���Ϣ��msg.jsp
		Map map = req.getParameterMap();
		Book book = CommonUtils.toBean(map, Book.class);
		Category category = CommonUtils.toBean(map, Category.class);
		book.setCategory(category);
		bookService.edit(book);
		req.setAttribute("msg","�޸�ͼ��ɹ���");
		return "f:/adminjsps/msg.jsp";
	}
	//ɾ��ͼ��
	public String delete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		String bid = req.getParameter("bid");
		Book  book = bookService.load(bid);
		String savepath = this.getServletContext().getRealPath("/");//��ȡ��ʵ·��
		new File(savepath,book.getImage_w()).delete();//ɾ���ļ�
		new File(savepath,book.getImage_b()).delete();//ɾ���ļ�
		bookService.delete(bid);
		req.setAttribute("msg","ɾ��ͼ��ɹ���");
		return "f:/adminjsps/msg.jsp";
	}
}
