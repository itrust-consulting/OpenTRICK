package lu.itrust.business.dao.hbm;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Norm;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.dao.DAONorm;

import org.hibernate.Query;

public class DAONormHBM extends DAOHibernate implements DAONorm {

	@Override
	public Norm getNormByID(int normID) throws Exception {
		return (Norm) getSession().get(Norm.class, normID);
	}

	@Override
	public Norm loadNotCustomNormByName(String label) throws Exception {

		Query query =
			getSession().createQuery("from Norm where label = :label and label != :custom");
		query.setString("label", label);
		query.setString("custom", Constant.NORM_CUSTOM);
		return (Norm) query.uniqueResult();

	}

	@Override
	public Norm loadSingleNormByName(String label) throws Exception {

		Query query = getSession().createQuery("from Norm where label = :label");
		query.setString("label", label);
		return (Norm) query.uniqueResult();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Norm> loadAllFromAnalysis(Analysis analysis) throws Exception {

		Query query = getSession().createQuery("From Norm");
		return (List<Norm>) query.list();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Norm> loadAllFromAnalysisIdentifierVersionCreationDate(int identifier,
			String version, String creationDate) throws Exception {

		Query query = getSession().createQuery("From Norm");
		return (List<Norm>) query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Norm> loadAll() throws Exception {

		Query query = getSession().createQuery("From Norm");
		return (List<Norm>) query.list();
	}

	@Override
	public void save(Norm norm) throws Exception {

		getSession().save(norm);

	}

	@Override
	public void saveOrUpdate(Norm norm) throws Exception {
		getSession().saveOrUpdate(norm);
	}

	@Override
	public void remove(Norm norm) throws Exception {
		getSession().delete(norm);

	}

}
