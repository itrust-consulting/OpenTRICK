/**
 * 
 */
package lu.itrust.business.ts.database.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.ts.database.dao.DAOWordReport;
import lu.itrust.business.ts.database.service.ServiceWordReport;
import lu.itrust.business.ts.model.general.document.impl.WordReport;
import lu.itrust.business.ts.model.general.helper.FilterControl;
import lu.itrust.business.ts.usermanagement.User;

/**
 * @author eomar
 *
 */
@Service
@Transactional(readOnly = true)
public class ServiceWordReportImpl implements ServiceWordReport {

	@Autowired
	private DAOWordReport daoWordReport;
	
	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.service.ServiceWordReport#get(java.lang.Integer)
	 */
	@Override
	public WordReport get(Long id) {
		return daoWordReport.get(id);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.service.ServiceWordReport#getByFilename(java.lang.String)
	 */
	@Override
	public WordReport getByName(String fileName) {
		return daoWordReport.getByName(fileName);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.service.ServiceWordReport#getByIdAndUser(java.lang.Integer, java.lang.String)
	 */
	@Override
	public WordReport getByIdAndUser(Long id, String username) {
		return daoWordReport.getByIdAndUser(id, username);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.service.ServiceWordReport#getAllFromUser(java.lang.String)
	 */
	@Override
	public List<WordReport> getAllFromUser(String username) {
		return daoWordReport.getAllFromUser(username);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.service.ServiceWordReport#getAllFromUser(java.lang.String, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public List<WordReport> getAllFromUser(String username, Integer pageIndex, Integer pageSize) {
		return daoWordReport.getAllFromUser(username, pageIndex, pageSize);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.service.ServiceWordReport#getAllFromUserAndIdentifier(java.lang.String, java.lang.String, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public List<WordReport> getAllFromUserAndIdentifier(String username, String identifier, Integer pageIndex, Integer pageSize) {
		return daoWordReport.getAllFromUserAndIdentifier(username, identifier, pageIndex, pageSize);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.service.ServiceWordReport#save(lu.itrust.business.ts.model.general.WordReport)
	 */
	@Transactional
	@Override
	public WordReport save(WordReport wordReport) {
		return daoWordReport.save(wordReport);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.service.ServiceWordReport#saveOrUpdate(lu.itrust.business.ts.model.general.WordReport)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(WordReport wordReport) {
		daoWordReport.saveOrUpdate(wordReport);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.service.ServiceWordReport#merge(lu.itrust.business.ts.model.general.WordReport)
	 */
	@Transactional
	@Override
	public WordReport merge(WordReport wordReport) {
		return daoWordReport.merge(wordReport);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.service.ServiceWordReport#delete(java.lang.Integer)
	 */
	@Transactional
	@Override
	public void delete(Long id) {
		daoWordReport.delete(id);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.service.ServiceWordReport#delete(java.lang.String)
	 */
	@Transactional
	@Override
	public void delete(String filename) {
		daoWordReport.delete(filename);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.service.ServiceWordReport#delete(lu.itrust.business.ts.model.general.WordReport)
	 */
	@Transactional
	@Override
	public void delete(WordReport wordReport) {
		daoWordReport.delete(wordReport);
	}

	@Override
	public List<String> getDistinctIdentifierByUser(User user) {
		return daoWordReport.getDistinctIdentifierByUser(user);
	}

	@Override
	public List<WordReport> getAllFromUserByFilterControl(String username, Integer page, FilterControl filter) {
		return daoWordReport.getAllFromUserByFilterControl(username,page,filter);
	}

}
