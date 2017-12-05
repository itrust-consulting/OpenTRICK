package lu.itrust.business.TS.database.dao.hbm;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import lu.itrust.business.TS.database.dao.DAOEmailValidatingRequest;
import lu.itrust.business.TS.usermanagement.EmailValidatingRequest;
import lu.itrust.business.TS.usermanagement.User;

@Repository
public class DAOEmailValidatingRequestHBM extends DAOHibernate implements DAOEmailValidatingRequest {

	@Override
	public EmailValidatingRequest findByToken(String token) {
		return getSession().createQuery("From EmailValidatingRequest where token = :token", EmailValidatingRequest.class).setParameter("token", token).uniqueResult();
	}

	@Override
	public EmailValidatingRequest findByUsername(String username) {
		return getSession().createQuery("From EmailValidatingRequest where user.login = :username", EmailValidatingRequest.class).setParameter("username", username).uniqueResult();
	}

	@Override
	public EmailValidatingRequest findByEmail(String email) {
		return getSession().createQuery("From EmailValidatingRequest where email = :email", EmailValidatingRequest.class).setParameter("email", email).uniqueResult();
	}

	@Override
	public boolean existsByEmail(String email) {
		return getSession().createQuery("Select count(*) > 0 From EmailValidatingRequest where email = :email", Boolean.class).setParameter("email", email).uniqueResult();
	}

	@Override
	public boolean existsByToken(String token) {
		return getSession().createQuery("Select count(*) > 0 From EmailValidatingRequest where token = :token", Boolean.class).setParameter("token", token).uniqueResult();
	}

	@Override
	public boolean existsByUsername(String username) {
		return getSession().createQuery("Select count(*) From EmailValidatingRequest where user.login = :username", Boolean.class).setParameter("username", username)
				.uniqueResult();
	}

	@Override
	public void deleteByUser(User user) {
		getSession().createQuery("Delete From EmailValidatingRequest where user = :user").setParameter("user", user).executeUpdate();
		getSession().createQuery("Delete From EmailValidatingRequest where email = :email").setParameter("email", user.getEmail()).executeUpdate();
	}

	@Override
	public long count() {
		return getSession().createQuery("Select count(*) From EmailValidatingRequest", Long.class).uniqueResult();
	}

	@Override
	public void delete(Collection<? extends EmailValidatingRequest> entities) {
		entities.forEach(entity -> delete(entity));
	}

	@Override
	public void delete(Long id) {
		getSession().createQuery("Delete From EmailValidatingRequest where id = :id").setParameter("id", id).executeUpdate();
	}

	@Override
	public void delete(EmailValidatingRequest entity) {
		getSession().delete(entity);
	}

	@Override
	public void deleteAll() {
		getSession().createQuery("Delete From EmailValidatingRequest").executeUpdate();
	}

	@Override
	public boolean exists(Long id) {
		return getSession().createQuery("Select count(*) > 0 From EmailValidatingRequest where id = :id", Boolean.class).setParameter("id", id).uniqueResult();
	}

	@Override
	public List<EmailValidatingRequest> findAll() {
		return getSession().createQuery("From EmailValidatingRequest", EmailValidatingRequest.class).list();
	}

	@Override
	public List<EmailValidatingRequest> findAll(List<Long> ids) {
		return ids.isEmpty() ? Collections.emptyList()
				: getSession().createQuery("From EmailValidatingRequest where id = in :ids", EmailValidatingRequest.class).setParameterList("ids", ids).list();
	}

	@Override
	public EmailValidatingRequest findOne(Long id) {
		return getSession().get(EmailValidatingRequest.class, id);
	}

	@Override
	public EmailValidatingRequest merge(EmailValidatingRequest entity) {
		return (EmailValidatingRequest) getSession().merge(entity);
	}

	@Override
	public List<Long> save(List<EmailValidatingRequest> entities) {
		return entities.stream().map(entity -> save(entity)).collect(Collectors.toList());
	}

	@Override
	public Long save(EmailValidatingRequest entity) {
		return (Long) getSession().save(entity);
	}

	@Override
	public void saveOrUpdate(List<EmailValidatingRequest> entities) {
		entities.forEach(entity -> save(entity));
	}

	@Override
	public void saveOrUpdate(EmailValidatingRequest entity) {
		getSession().saveOrUpdate(entity);
	}

}
