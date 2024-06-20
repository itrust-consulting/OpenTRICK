package lu.itrust.business.ts.database.template;

import java.util.List;

import lu.itrust.business.ts.model.asset.Asset;
import lu.itrust.business.ts.model.ilr.AssetNode;

public interface TemplateAssetNode extends TemplateAnalysisMember<AssetNode, Long>{

    List<AssetNode> findByAsset(Asset asset);

    List<AssetNode> findByAssetId(int assetId);

    List<AssetNode> findByAnalysisIdAndAssetName(int analysisId , String assetName);
    
}
