package lu.itrust.business.ts.database.migration;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.model.asset.AssetType;

/**
 * @author eomar
 */
public class V2_4_0__Add_new_asset_type_and_scenario_type extends TrickServiceDataBaseMigration {

    private static final String QUERY_ASSET_TYPE_SELECT = "SELECT `idAssetType`, `dtLabel` FROM `AssetType` ORDER By idAssetType ASC";

    private static final String QUERY_ASSET_TYPE_INSERT = "INSERT INTO `AssetType`(`dtLabel`) VALUES (?)";

    @Override
    public void migrate(JdbcTemplate template) throws Exception {

        final String[] assetTypes = Constant.REGEXP_VALID_ASSET_TYPE.split("\\|");
        final List<AssetType> assetTypeList = template.query(QUERY_ASSET_TYPE_SELECT,
                (row, cout) -> new AssetType(row.getInt("idAssetType"), row.getString("dtLabel")));

        for (String assetType : assetTypes) {
            if (assetTypeList.stream().noneMatch(e -> e.isSame(assetType)))
                template.update(QUERY_ASSET_TYPE_INSERT, assetType);
        }

    }

}
