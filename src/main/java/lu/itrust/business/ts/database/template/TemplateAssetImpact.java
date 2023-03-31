package lu.itrust.business.ts.database.template;

import java.util.List;

import lu.itrust.business.ts.model.asset.Asset;
import lu.itrust.business.ts.model.ilr.AssetImpact;

public interface TemplateAssetImpact extends TemplateAnalysisMember<AssetImpact, Long>{

    List<AssetImpact> findByAsset(Asset asset);

    List<AssetImpact> findByAssetId(int assetId);

    List<AssetImpact> findByAnalysisIdAndAssetName(int analysisId , String assetName);

}
