/**
 * 
 */
package lu.itrust.business.TS.model.general.helper;

import java.util.LinkedList;
import java.util.List;

import lu.itrust.business.TS.model.asset.Asset;

/**
 * @author eomar
 *
 */
public class ProxyAsset implements ProxyAssetScenario {

	private Asset asset;

	@Override
	public int getId() {
		return asset.getId();
	}

	@Override
	public String getName() {
		return this.asset.getName();
	}

	@Override
	public List<Object[]> getAllFields() {
		List<Object[]> fields = new LinkedList<>();
		fields.add(getField("name"));
		fields.add(getField("comment"));
		fields.add(getField("hiddenComment"));
		return fields;
	}

	@Override
	public Object[] getField(String fieldName) {
		switch (fieldName) {
		case "name":
			return new Object[] { fieldName, getName(), "label.asset.name" };
		case "comment":
			return new Object[] { fieldName, get().getComment(), "label.asset.comment" };
		case "hiddenComment":
			return new Object[] { fieldName, get().getHiddenComment(), "label.asset.hidden_comment" };
		}
		return null;
	}

	@Override
	public Asset get() {
		return asset;
	}

}
