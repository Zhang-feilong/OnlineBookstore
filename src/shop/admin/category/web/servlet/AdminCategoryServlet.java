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
	//��ѯ���з���
	public String findAll(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		req.setAttribute("parents", categoryService.findAll());
		return "f:/adminjsps/admin/category/list.jsp";
	}
	//���һ������
	public String addParent(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		//1.��װ�����ݵ�category
		//2.����service����������
		//3. ����findAll������ʾ���з���
		Category parent = CommonUtils.toBean(req.getParameterMap(), Category.class);
		parent.setCid(CommonUtils.uuid());
		categoryService.add(parent);;
		return findAll(req,resp);
	}
	//��Ӷ��������һ��
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
		//1.��װ�����ݵ�category
		//2.��Ҫ�ֶ��ѱ��е�pidӳ�䵽child��
		//3.����service����������
		//4. ����findAll������ʾ���з���
		Category child = CommonUtils.toBean(req.getParameterMap(), Category.class);
		child.setCid(CommonUtils.uuid());
		String pid = req.getParameter("pid");
		Category parent = new Category();
		parent.setCid(pid);
		child.setParent(parent);
		categoryService.add(child);;
		return findAll(req,resp);
	}
	
	//�޸�һ�������һ��
	public String editParentPre(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		//1.��ȡ�����е�cid
		//2.ʹ��cid����Category
		//3.���沢ת����edit.jsp��ʾ
		String cid = req.getParameter("cid");
		Category parent = categoryService.load(cid);
		req.setAttribute("parent", parent);
		return "f:/adminjsps/admin/category/edit.jsp";
		
		
	}
	//�޸�һ������ڶ���
	public String editParent(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		/*1.��װ�����ݵ�Category
		 * ����service����޸�
		 * ת����list.jsp��ʾ���з���
		 * */
		Category parent = CommonUtils.toBean(req.getParameterMap(), Category.class);
		categoryService.edit(parent);
		return findAll(req,resp);
	}
	//�޸Ķ��������һ��
	public String editChildPre(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
			//1.��ȡ�����е�cid
			//2.ʹ��cid����Category����ѯ����һ�����࣬����
			//3.���沢ת����edit.jsp��ʾ
		String cid = req.getParameter("cid");
		Category child = categoryService.load(cid);
		req.setAttribute("child", child);
		req.setAttribute("parents",categoryService.findParents());
		return "f:/adminjsps/admin/category/edit2.jsp";
	}
	
	//�޸Ķ�������ڶ���
		public String editChild(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException, SQLException {
				/*1.��װ�����ݵ�Category,�ѱ��е�pidҲ��װ��child��
				 * ����service����޸�
				 * ת����list.jsp��ʾ���з���
				 * */ 
			Category child = CommonUtils.toBean(req.getParameterMap(), Category.class);
			String pid = req.getParameter("pid");
			Category parent = new Category();
			parent.setCid(pid);
			child.setParent(parent);
			
			categoryService.edit(child);
			return findAll(req,resp);
		}
		//ɾ��һ������
		public String deleteParent(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException, SQLException {
			//1.��ȡ�����е�cid
			//2.ʹ��cid��ѯ�÷����µ��ӷ������
			//3.�������0������ɾ�����������0 ɾ���󷵻�list.jsp
			String cid= req.getParameter("cid");
			int cnt = categoryService.findChildrenCountByParent(cid);
			if(cnt >0){
				req.setAttribute("msg", "�÷����»����ӷ��࣬����ɾ����");
				return "f:/adminjsps/msg.jsp";
			}else{
				categoryService.delete(cid);
				return findAll(req,resp);
			}
		}
		
		//ɾ��e��������
				public String deleteChild(HttpServletRequest req, HttpServletResponse resp)
						throws ServletException, IOException, SQLException {
					//1.��ȡ�����е�cid
					//2.ʹ��cid��ѯ�÷����µ�ͼ�����
					//3.�������0������ɾ�����������0 ɾ���󷵻�list.jsp
					String cid= req.getParameter("cid");
					int cnt = bookService.findBookCountByCategory(cid);
					if(cnt >0){
						req.setAttribute("msg", "�÷����»���ͼ�飬����ɾ����");
						return "f:/adminjsps/msg.jsp";
					}else{
						categoryService.delete(cid);
						return findAll(req,resp);
					}
				}
}
