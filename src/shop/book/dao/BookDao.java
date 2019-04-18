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
/*通用的查询方法*/
public class BookDao {
	private QueryRunner qr = new TxQueryRunner();
	/*按bid查询*/
	public Book findByBid(String bid) throws SQLException{
		String sql = "SELECT * FROM t_book b, t_category c WHERE b.cid=c.cid AND b.bid=?";
		// 一行记录中，包含了很多的book的属性，还有一个cid属性
		Map<String,Object> map = qr.query(sql, new MapHandler(), bid);
		// 把Map中除了cid以外的其他属性映射到Book对象中
		Book book = CommonUtils.toBean(map, Book.class);
		// 把Map中cid属性映射到Category中，即这个Category只有cid
		Category category = CommonUtils.toBean(map, Category.class);
		// 两者建立关系
		book.setCategory(category);
		
		// 把pid获取出来，创建一个Category parnet，把pid赋给它，然后再把parent赋给category
		if(map.get("pid") != null) {
			Category parent = new Category();
			parent.setCid((String)map.get("pid"));
			category.setParent(parent);
		}
		return book;
	}
	
	//按分类查询
	public PageBean<Book> findByCategory(String cid ,int pc) throws SQLException{
		List<Expression> exprList = new ArrayList<Expression>();
		exprList.add(new Expression("cid","=",cid));
		return findByCriteria(exprList,pc);
	}
	//按书名查询
	public PageBean<Book> findByBname(String bname ,int pc) throws SQLException{
		List<Expression> exprList = new ArrayList<Expression>();
		exprList.add(new Expression("bname","like","%"+bname+"%"));
		return findByCriteria(exprList,pc);
	}
	//按作者查询
	public PageBean<Book> findByAuthor(String author ,int pc) throws SQLException{
		List<Expression> exprList = new ArrayList<Expression>();
		exprList.add(new Expression("author","like","%"+author+"%"));
		return findByCriteria(exprList,pc);
	}
	
	//按出版社查询
	public PageBean<Book> findByPress(String press ,int pc) throws SQLException{
		List<Expression> exprList = new ArrayList<Expression>();
		exprList.add(new Expression("press","like","%"+press+"%"));
		return findByCriteria(exprList,pc);
	}
	
	//多条件组合查询
	public PageBean<Book> findByCombination(Book criteria ,int pc) throws SQLException{
		List<Expression> exprList = new ArrayList<Expression>();
		exprList.add(new Expression("bname","like","%"+criteria.getBname()+"%"));
		exprList.add(new Expression("author","like","%"+criteria.getAuthor()+"%"));
		exprList.add(new Expression("press","like","%"+criteria.getPress()+"%"));
		return findByCriteria(exprList,pc);
	}

	private PageBean<Book> findByCriteria(List<Expression> exprList,int pc) throws SQLException{
		/*1.得到ps
		 * 2.得到tr
		 * 3.得到beanList
		 * 4.创建PageBean返回
		 * */
		int ps = PageConstants.BOOK_PAGE_SIZE;//每页记录数
		StringBuilder whereSql = new StringBuilder(" where 1=1");
		List<Object> params = new ArrayList<Object>();//对应sql中问号的值
		for(Expression expr: exprList){
			/*
			 * 添加一个条件
			 * 以and开头，条件的名称，条件运算符可以是 =、>、< is null  is null 没有值
			 * 如果条件不是is null 再追加问号，然后再向params中添加一个与问号对应的值
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
		params.add((pc-1)*ps);//当前页首行记录的下表
		params.add(ps);//一共查询几行，就是每页记录数
		
		List<Book> beanList =  qr.query(sql, new BeanListHandler<Book>(Book.class),
				params.toArray());
		//创建PageBean,s设置参数
		PageBean<Book> pb= new PageBean<Book>();
		//其中PageBean 没有url 这个任务由Service完成
		pb.setBeanList(beanList);
		pb.setPc(pc);
		pb.setPs(ps);
		pb.setTr(tr);
		return pb;
	}
	//查询指定分类下的图书个数
	public int findBookCountByCategory(String cid) throws SQLException{
		String sql = "select count(*) from t_book where cid=?";
		Number cnt =(Number)qr.query(sql, new ScalarHandler(),cid);
		return cnt == null ? 0:cnt.intValue();
	}
	//添加图书
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
	//修改图书
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
	/*删除图书*/
	public void delete(String bid) throws SQLException{
		String sql ="delete from t_book where bid=?";
		qr.update(sql,bid);
	}
}
