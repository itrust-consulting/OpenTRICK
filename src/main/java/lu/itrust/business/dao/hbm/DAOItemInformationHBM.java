/**
 * 
 */
package lu.itrust.business.dao.hbm;

import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.TS.ItemInformation;
import lu.itrust.business.dao.DAOItemInformation;

/**
 * @author eom
 * 
 */
@Repository
public class DAOItemInformationHBM extends DAOHibernate implements
		DAOItemInformation {
	/**
	 * 
	 */
	public DAOItemInformationHBM() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param session
	 */
	public DAOItemInformationHBM(Session session) {
		super(session);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOItemInformation#get(int)
	 */
	@Override
	public ItemInformation get(int id) throws Exception {
		return (ItemInformation) getSession().get(ItemInformation.class, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.dao.DAOItemInformation#loadFromDescription(java.lang
	 * .String)
	 */
	@Override
	public ItemInformation loadFromDescription(String description)
			throws Exception {
		return (ItemInformation) getSession()
				.createQuery(
						"From ItemInformation where description = :description")
				.setString("description", description).uniqueResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOItemInformation#loadAllFromAnalysisID(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ItemInformation> loadAllFromAnalysisID(int analysisID)
			throws Exception {
		return getSession()
				.createQuery(
						"Select itemInformation From Analysis as analysis inner join analysis.itemInformations as itemInformation  where analysis.id = :id")
				.setInteger("id", analysisID).list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOItemInformation#
	 * loadAllFromAnalysisIdentifierVersionCreationDate(int, java.lang.String,
	 * java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ItemInformation> loadAllFromAnalysisIdentifierVersionCreationDate(
			int identifier, String version, String creationDate)
			throws Exception {
		return getSession()
				.createQuery(
						"Select itemInformation From Analysis as analysis inner join analysis.itemInformations as itemInformation  where analysis.identifier = :identifier and analysis.version = :version and analysis.creationDate = :creationDate")
				.setInteger("identifier", identifier)
				.setString("version", version)
				.setString("creationDate", creationDate).list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOItemInformation#loadAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ItemInformation> loadAll() throws Exception {
		return getSession().createQuery("From ItemInformation").list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.dao.DAOItemInformation#save(lu.itrust.business.TS.
	 * ItemInformation)
	 */
	@Override
	public void save(ItemInformation itemInformation) throws Exception {
		getSession().save(itemInformation);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.dao.DAOItemInformation#saveOrUpdate(lu.itrust.business
	 * .TS.ItemInformation)
	 */
	@Override
	public void saveOrUpdate(ItemInformation itemInformation) throws Exception {
		getSession().saveOrUpdate(itemInformation);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.dao.DAOItemInformation#remove(lu.itrust.business.TS
	 * .ItemInformation)
	 */
	@Override
	public void remove(ItemInformation itemInformation) throws Exception {
		getSession().delete(itemInformation);

	}

}
