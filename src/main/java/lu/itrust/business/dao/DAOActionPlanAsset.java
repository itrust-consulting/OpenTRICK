package lu.itrust.business.dao;

import java.util.List;

import lu.itrust.business.TS.Asset;
import lu.itrust.business.TS.actionplan.ActionPlanEntry;

public interface DAOActionPlanAsset {
	
	public Asset get(long assetId, ActionPlanEntry actionPlanEntry) throws Exception;
	
	public List<Asset> loadAllFromEntry(ActionPlanEntry actionPlanEntry) throws Exception;
	
	public void save(Asset asset) throws Exception;
	
	public void saveOrUpdate(Asset asset) throws Exception;
	
	public void remove(Asset asset) throws Exception;

}