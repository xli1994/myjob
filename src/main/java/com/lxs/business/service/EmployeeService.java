package com.lxs.business.service;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Named;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lxs.business.ejb.entity.Employee;
import com.lxs.web.servlets.EMF;

//not work
//@Named(value="employeeService")
@ManagedBean(name = "employeeService", eager = true)
//must set eager=true, otherwise got null in EmployeeFaceBean
@ViewScoped
public class EmployeeService implements Serializable
{
	private static final long serialVersionUID = -1;
	
	private static final Logger logger = LogManager.getLogger(EmployeeService.class);

	public List<Employee> getEmployeeList()
	{
		logger.debug("Get Employee list now...");
		EntityManager em = EMF.createEntityManager();
		em.getTransaction().begin();	
		Query query = em.createQuery("SELECT e FROM Employee e");
	    
		List<Employee> list = query.getResultList();
		em.getTransaction().commit();
		em.close();
		return list;
	}
	
	/**
	 * Delete batch employees
	 * @param list
	 */
	public void deleteEmployees(List<Integer> list)
	{
		logger.debug("Deleteing employees: list="+list);
		EntityManager em = EMF.createEntityManager();
		em.getTransaction().begin();	
		Query query = em.createQuery("DELETE FROM Employee e where e.id in :idList");
		query.setParameter("idList", list);
		int row = query.executeUpdate();
		em.getTransaction().commit();
		logger.debug("deleted row="+row);
		em.close();
	}
	
	public void deleteSingleEmployee(Employee employee)
	{
		logger.debug("Deleteing single employee: ="+employee);
		EntityManager em = EMF.createEntityManager();
		em.getTransaction().begin();	
		//must merger it first to attach it, stupid! this load entity, not necessary
		//employee = em.merge(employee); 
		//change to this
		employee = em.getReference(Employee.class, employee.getId());
		em.remove(employee);
		em.getTransaction().commit();
		logger.debug("deleted single employee");
		em.close();
	}

	public void updateBatchEmployee(List <Employee> update)
	{
		logger.debug("Update batch employees: size="+update.size()+"; list="+update);
		EntityManager em = EMF.createEntityManager();
		em.getTransaction().begin();	
		int i = 0;
		for(Employee employee: update)
		{
			i++;
			em.merge(employee);
			if(i%100 == 0)
			{
				em.flush();
			}
			
		}
		em.getTransaction().commit();
		logger.debug("Completed update batch employees");
		em.close();
	}
	
	public void printMe()
	{
		logger.debug("printMe called, test get db connection from web jndi....");

		//test jndi:
		getDatasouceFromJNDI();
	}

	/**
	 * Test inserting 
	 * @param employee
	 * @return
	 */
	public int addEmployee(Employee employee)
	{
		logger.debug("Inerset Employee=" + employee);
		EntityManager em = EMF.createEntityManager();
		em.getTransaction().begin();	
		em.persist(employee);
		
		em.getTransaction().commit();
		em.close();
		return employee.getId();
	}
	
	//Works!
	public void testJPA()
	{
		EntityManager em = EMF.createEntityManager();
		em.getTransaction().begin();
		Employee employee = em.find(Employee.class, new Integer(11));
		logger.debug("Employee=" + employee);
		//modify it
		employee.setSalary(3332);
		em.persist(employee);
		em.getTransaction().commit();
		em.close();
	}

	/**
	 * This is to test to get datasource from web jndi: works
	 */
	private void getDatasouceFromJNDI()
	{
		logger.debug("getting db connection from jndi...");
		//test db connection:
		java.sql.Connection conn = null;
		try
		{
			//Obtain Connection
			InitialContext initialContext = new InitialContext();
			javax.sql.DataSource ds = (javax.sql.DataSource) initialContext
					.lookup("java:comp/env/jdbc/mysqldb");
			conn = ds.getConnection();
			if (conn != null && !conn.isClosed())
			{
				logger.debug("Got db connection from jndi-----");
			}
			else
			{
				logger.debug("Failed to get db connection from jndi!!!!");
			}
		}
		catch (NamingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			try
			{
				conn.close();
			}
			catch (SQLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
