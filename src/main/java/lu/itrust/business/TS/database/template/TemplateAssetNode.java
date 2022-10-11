package lu.itrust.business.TS.database.template;

import java.util.List;

import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.ilr.AssetNode;

public interface TemplateAssetNode extends TemplateAnalysisMember<AssetNode, Long>{

    List<AssetNode> findByAsset(Asset asset);

    List<AssetNode> findByAssetId(int assetId);

    List<AssetNode> findByAnalysisIdAndAssetName(int analysisId , String assetName);
    
}
