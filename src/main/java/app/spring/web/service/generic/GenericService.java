package app.spring.web.service.generic;

import java.io.Serializable;
import java.util.List;

import tk.mybatis.mapper.entity.Condition;

/**
 *  <p>Base Service For Dao using Mybatis
 * 	<br> and this class inherit from tk.mybatis, make easy to used like hibernate, but diffrent way
 * </p>
 * @author taufikm
 * 
 * @param <T>
 * @param <ID>
 * 
 * Dec 25, 2020
 */
public interface GenericService<T,ID extends Serializable> {

	List<T> getAll();
	List<T> getByCriteria(Condition condition);
	T getOneByCriteria(Condition condition);
	T get(ID id);
	
	/**
	 * 
	 * @param entity
	 * @return
	 */
	T save(T entity);
	void remove(ID id);
}
