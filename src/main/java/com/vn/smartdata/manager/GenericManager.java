/*
 * Created by Leo
 */
package com.vn.smartdata.manager;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.vn.smartdata.repository.GenericRepository;


/**
 * This class serves as the Base class for all other Managers - namely to hold common CRUD methods that they might all
 * use. You should only need to extend this class when your require custom CRUD logic.
 * <p/>
 * <p>
 * To register this class in your Spring context file, use the following XML.
 * 
 * <pre>
 *     &lt;bean id="userManager" class="com.sts.service.impl.GenericManagerImpl"&gt;
 *         &lt;constructor-arg&gt;
 *             &lt;bean class="com.sts.dao.hibernate.GenericDaoHibernate"&gt;
 *                 &lt;constructor-arg value="com.sts.model.User"/&gt;
 *                 &lt;property name="sessionFactory" ref="sessionFactory"/&gt;
 *             &lt;/bean&gt;
 *         &lt;/constructor-arg&gt;
 *     &lt;/bean&gt;
 * </pre>
 * <p/>
 * <p>
 * If you're using iBATIS instead of Hibernate, use:
 * 
 * <pre>
 *     &lt;bean id="userManager" class="com.sts.service.impl.GenericManagerImpl"&gt;
 *         &lt;constructor-arg&gt;
 *             &lt;bean class="com.sts.dao.ibatis.GenericDaoiBatis"&gt;
 *                 &lt;constructor-arg value="com.sts.model.User"/&gt;
 *                 &lt;property name="dataSource" ref="dataSource"/&gt;
 *                 &lt;property name="sqlMapClient" ref="sqlMapClient"/&gt;
 *             &lt;/bean&gt;
 *         &lt;/constructor-arg&gt;
 *     &lt;/bean&gt;
 * </pre>
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a> Updated by jgarcia: added full text search +
 *         reindexing
 * @author khoi
 * @param <T> a type variable
 */
public class GenericManager<T> {

    /**
     * Log variable for all child classes. Uses LogFactory.getLog(getClass()) from Commons Logging
     */
    protected final Log log = LogFactory.getLog(getClass());

    /** GenericDao instance, set by constructor of child classes. */
    protected GenericRepository<T> repository;

    /**
     * Instantiates a new generic service.
     */
    public GenericManager() {
    }

    /**
     * Instantiates a new generic service.
     *
     * @param genericDao the generic dao
     */
    public GenericManager(GenericRepository<T> genericDao) {
        this.repository = genericDao;
    }

    /**
     * {@inheritDoc}
     */
    public List<T> getAll() {
        return repository.findAll();
    }

    /**
     * {@inheritDoc}
     */
    public T get(Long id) {
        return repository.findOne(id);
    }

    /**
     * {@inheritDoc}
     */
    public boolean exists(Long id) {
        return repository.exists(id);
    }

    /**
     * {@inheritDoc}
     */
    public T save(T object) {
        return repository.save(object);
    }
    
    /**
     * {@inheritDoc}
     */
    public T saveAndFlush(T object) {
        return repository.saveAndFlush(object);
    }
    /**
     * {@inheritDoc}
     */
    public void remove(T object) {
        repository.delete(object);
    }

    /**
     * {@inheritDoc}
     */
    public void remove(Long id) {
        repository.delete(id);
    }
}
