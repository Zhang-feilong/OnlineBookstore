package shop.category.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import shop.category.domain.Category;

import cn.itcast.commons.CommonUtils;
import cn.itcast.jdbc.TxQueryRunner;

/*
 * 分类持久层
 * */
public class CategoryDao {
	private QueryRunner qr = new TxQueryRunner();
	/*把一个Map中的数据映射到Category中*/
	private Category toCategory(Map<String,Object> map){
		Category category = CommonUtils.toBean(map, Category.class);
		String pid = (String)map.get("pid");
		/*如果父分类id不为空，使用一个父分类对象来拦截pid，再把父分类设置给category*/
		if(pid != null){
			Category  parent = new Category();
			parent.setCid(pid);
			category.setParent(parent);
		}
		return category;
	}
	/*把多个Map(List<Map>) 映射成多个Category(List<Category>) */
	private List<Category> toCategoryList(List<Map<String,Object>> mapList){
		List<Category> categoryList = new ArrayList<Category>();
		for(Map<String,Object> map : mapList){
			Category c = toCategory(map);
			categoryList.add(c);
		}
		return categoryList;
	}
	
	/*返回所有分类*/
	public List<Category> findAll() throws SQLException{
		 String sql = "select * from t_category where pid is null order by orderBy";
		 List<Map<String,Object>> mapList = qr.query(sql,new MapListHandler());
		 List<Category> parents = toCategoryList(mapList);
		 for(Category parent : parents){
			 /*查询出当前父分类的所有子分类*/
			 List<Category> children = findByParent(parent.getCid());
			 /*设置给父分类*/
			 parent.setChildren(children);
		 }
		return parents;
	}
	/*通过父分类查询子分类*/
	public List<Category> findByParent(String pid) throws SQLException{
		String sql = "select * from t_category where pid =?";
		List<Map<String,Object>> mapList =	qr.query(sql, new MapListHandler(),pid);
		return toCategoryList(mapList);
	}
	//添加分类
	public void add(Category category) throws SQLException{
		String sql = "insert into t_category(cid,cname,pid,`desc`) values(?,?,?,?)";
		//因为一级分类没有parent,而二级分类有，这个方法要兼容两个分类，所以要判断
		String pid = null;//一级分类赋值
		if(category.getParent() != null){
			pid = category.getParent().getCid();
		}
		Object[] params = {category.getCid(),category.getCname(),pid,category.getDesc()};
		qr.update(sql,params);
	}
	//获取所有父分类，但不带子分类
	public List<Category> findParents() throws SQLException{
		 String sql = "select * from t_category where pid is null order by orderBy";
		 List<Map<String,Object>> mapList = qr.query(sql,new MapListHandler());
		return toCategoryList(mapList);
	}
	//加载分类，一级和二级都可以
	public Category load(String cid) throws SQLException{
		String sql = "select * from t_category where cid=?";
		return toCategory(qr.query(sql, new MapHandler(),cid));
	}
	//修改分类
	public void edit(Category category) throws SQLException{
		String sql = "update t_category set cname=?, pid=?, `desc`=? where cid=?";
		String pid = null;
		if(category.getParent() != null){
			pid = category.getParent().getCid();
		}
		Object[] params = {category.getCname(),pid,category.getDesc(),category.getCid()};
		qr.update(sql, params);
	}
	//查询指定分类下分类的个数
	public int findChildrenCountByParent(String pid) throws SQLException{
		String sql = "select count(*) from t_category where pid=?";
		Number cnt = (Number)qr.query(sql, new ScalarHandler(),pid);
		return cnt == null ? 0:cnt.intValue();
	}
	public void delete(String cid) throws SQLException{
		String sql = "delete from t_category where cid=?";
		qr.update(sql,cid);
	}
}
