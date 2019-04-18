package shop.book.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;


import shop.book.domain.Book;
import shop.category.domain.Category;
import shop.pager.Expression;
import shop.pager.PageBean;
import shop.pager.PageConstants;

import cn.itcast.commons.CommonUtils;
import cn.itcast.jdbc.TxQueryRunner;
/*ͨ�õĲ�ѯ����*/
public class BookDao {
	private QueryRunner qr = new TxQueryRunner();
	/*��bid��ѯ*/
	public Book findByBid(String bid) throws SQLException{
		String sql = "SELECT * FROM t_book b, t_category c WHERE b.cid=c.cid AND b.bid=?";
		// һ�м�¼�У������˺ܶ��book�����ԣ�����һ��cid����
		Map<String,Object> map = qr.query(sql, new MapHandler(), bid);
		// ��Map�г���cid�������������ӳ�䵽Book������
		Book book = CommonUtils.toBean(map, Book.class);
		// ��Map��cid����ӳ�䵽Category�У������Categoryֻ��cid
		Category category = CommonUtils.toBean(map, Category.class);
		// ���߽�����ϵ
		book.setCategory(category);
		
		// ��pid��ȡ����������һ��Category parnet����pid��������Ȼ���ٰ�parent����category
		if(map.get("pid") != null) {
			Category parent = new Category();
			parent.setCid((String)map.get("pid"));
			category.setParent(parent);
		}
		return book;
	}
	
	//�������ѯ
	public PageBean<Book> findByCategory(String cid ,int pc) throws SQLException{
		List<Expression> exprList = new ArrayList<Expression>();
		exprList.add(new Expression("cid","=",cid));
		return findByCriteria(exprList,pc);
	}
	//��������ѯ
	public PageBean<Book> findByBname(String bname ,int pc) throws SQLException{
		List<Expression> exprList = new ArrayList<Expression>();
		exprList.add(new Expression("bname","like","%"+bname+"%"));
		return findByCriteria(exprList,pc);
	}
	//�����߲�ѯ
	public PageBean<Book> findByAuthor(String author ,int pc) throws SQLException{
		List<Expression> exprList = new ArrayList<Expression>();
		exprList.add(new Expression("author","like","%"+author+"%"));
		return findByCriteria(exprList,pc);
	}
	
	//���������ѯ
	public PageBean<Book> findByPress(String press ,int pc) throws SQLException{
		List<Expression> exprList = new ArrayList<Expression>();
		exprList.add(new Expression("press","like","%"+press+"%"));
		return findByCriteria(exprList,pc);
	}
	
	//��������ϲ�ѯ
	public PageBean<Book> findByCombination(Book criteria ,int pc) throws SQLException{
		List<Expression> exprList = new ArrayList<Expression>();
		exprList.add(new Expression("bname","like","%"+criteria.getBname()+"%"));
		exprList.add(new Expression("author","like","%"+criteria.getAuthor()+"%"));
		exprList.add(new Expression("press","like","%"+criteria.getPress()+"%"));
		return findByCriteria(exprList,pc);
	}

	private PageBean<Book> findByCriteria(List<Expression> exprList,int pc) throws SQLException{
		/*1.�õ�ps
		 * 2.�õ�tr
		 * 3.�õ�beanList
		 * 4.����PageBean����
		 * */
		int ps = PageConstants.BOOK_PAGE_SIZE;//ÿҳ��¼��
		StringBuilder whereSql = new StringBuilder(" where 1=1");
		List<Object> params = new ArrayList<Object>();//��Ӧsql���ʺŵ�ֵ
		for(Expression expr: exprList){
			/*
			 * ���һ������
			 * ��and��ͷ�����������ƣ���������������� =��>��< is null  is null û��ֵ
			 * �����������is null ��׷���ʺţ�Ȼ������params�����һ�����ʺŶ�Ӧ��ֵ
			 * */
			whereSql.append(" and ").append(expr.getName()).append(" ")
			.append(expr.getOperator()).append(" ");
			if(!expr.getOperator().equals("is null")){
				whereSql.append("?");
				params.add(expr.getValue());
			}
		}
		String sql = "select count(*) from t_book" + whereSql;
		Number number = (Number)qr.query(sql, new ScalarHandler(),params.toArray());
		int tr = number.intValue();
		
		sql = "select * from t_book" + whereSql +" order by orderBy limit ?,?";
		params.add((pc-1)*ps);//��ǰҳ���м�¼���±�
		params.add(ps);//һ����ѯ���У�����ÿҳ��¼��
		
		List<Book> beanList =  qr.query(sql, new BeanListHandler<Book>(Book.class),
				params.toArray());
		//����PageBean,s���ò���
		PageBean<Book> pb= new PageBean<Book>();
		//����PageBean û��url ���������Service���
		pb.setBeanList(beanList);
		pb.setPc(pc);
		pb.setPs(ps);
		pb.setTr(tr);
		return pb;
	}
	//��ѯָ�������µ�ͼ�����
	public int findBookCountByCategory(String cid) throws SQLException{
		String sql = "select count(*) from t_book where cid=?";
		Number cnt =(Number)qr.query(sql, new ScalarHandler(),cid);
		return cnt == null ? 0:cnt.intValue();
	}
	//���ͼ��
	public void add(Book book) throws SQLException {
		String sql = "insert into t_book(bid,bname,author,price,currPrice,"+
	"discount,press,publishtime,edition,pageNum,wordNum,printtime," +
	"booksize,paper,cid,image_w,image_b) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		Object[] params = {book.getBid(),book.getBname(),book.getAuthor(),book.getPrice(),
				book.getCurrPrice(),book.getDiscount(),book.getPress(),book.getPublishtime(),
			book.getEdition(),book.getPageNum(),book.getWordNum(),book.getPrinttime(),
			book.getBooksize(),book.getPaper(),book.getCategory().getCid(),book.getImage_w(),book.getImage_b()};
		qr.update(sql,params);
	}
	//�޸�ͼ��
	public void edit(Book book) throws SQLException{
		String sql = "update t_book set bname=?,author=?,price=?,currPrice=?," +
				"discount=?,press=?,publishtime=?,edition=?,pageNum=?,wordNum=?," +
				"printtime=?,booksize=?,paper=?,cid=? where bid=?";
		Object[] params = {book.getBname(),book.getAuthor(),
				book.getPrice(),book.getCurrPrice(),book.getDiscount(),
				book.getPress(),book.getPublishtime(),book.getEdition(),
				book.getPageNum(),book.getWordNum(),book.getPrinttime(),
				book.getBooksize(),book.getPaper(), 
				book.getCategory().getCid(),book.getBid()};
		qr.update(sql, params);
	}
	/*ɾ��ͼ��*/
	public void delete(String bid) throws SQLException{
		String sql ="delete from t_book where bid=?";
		qr.update(sql,bid);
	}
}
