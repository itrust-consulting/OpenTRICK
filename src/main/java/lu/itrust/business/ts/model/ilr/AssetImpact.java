package lu.itrust.business.ts.model.ilr;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKey;
import jakarta.persistence.OneToMany;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.model.asset.Asset;
import lu.itrust.business.ts.model.scale.ScaleType;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class AssetImpact implements Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idAssetImpact")
    private long id;

    @ManyToOne
    @JoinColumn(name = "fiAsset", nullable = false, unique = true)
    @Cascade({ CascadeType.SAVE_UPDATE, CascadeType.MERGE, CascadeType.PERSIST })
    private Asset asset;

    @OneToMany
    @MapKey(name = "type")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JoinTable(name = "AssetILRImpactConfidentialities", joinColumns = @JoinColumn(name = "fiAssetImpact"), inverseJoinColumns = @JoinColumn(name = "fiILRImpact"))
    @Cascade(CascadeType.ALL)
    private Map<ScaleType, ILRImpact> confidentialityImpacts = new HashMap<>();

    @OneToMany
    @MapKey(name = "type")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JoinTable(name = "AssetILRImpactIntegrities", joinColumns = @JoinColumn(name = "fiAssetImpact"), inverseJoinColumns = @JoinColumn(name = "fiILRImpact"))
    @Cascade(CascadeType.ALL)
    private Map<ScaleType, ILRImpact> integrityImpacts = new HashMap<>();

    @OneToMany
    @MapKey(name = "type")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JoinTable(name = "AssetILRImpactAvailabilities", joinColumns = @JoinColumn(name = "fiAssetImpact"), inverseJoinColumns = @JoinColumn(name = "fiILRImpact"))
    @Cascade(CascadeType.ALL)
    private Map<ScaleType, ILRImpact> availabilityImpacts = new HashMap<>();

    public AssetImpact() {
    }

    public AssetImpact(Asset asset) {
        setAsset(asset);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    public Map<ScaleType, ILRImpact> getConfidentialityImpacts() {
        return confidentialityImpacts;
    }

    public void setConfidentialityImpacts(Map<ScaleType, ILRImpact> confidentialityImpacts) {
        this.confidentialityImpacts = confidentialityImpacts;
    }

    public Map<ScaleType, ILRImpact> getIntegrityImpacts() {
        return integrityImpacts;
    }

    public void setIntegrityImpacts(Map<ScaleType, ILRImpact> integrityImpacts) {
        this.integrityImpacts = integrityImpacts;
    }

    public Map<ScaleType, ILRImpact> getAvailabilityImpacts() {
        return availabilityImpacts;
    }

    public void setAvailabilityImpacts(Map<ScaleType, ILRImpact> availabilityImpacts) {
        this.availabilityImpacts = availabilityImpacts;
    }

    @Override
    public AssetImpact clone() {
        try {
            final AssetImpact clone = (AssetImpact) super.clone();
            if (availabilityImpacts != null)
                clone.availabilityImpacts = availabilityImpacts.values().stream().map(ILRImpact::clone)
                        .collect(Collectors.toMap(ILRImpact::getType, Function.identity()));

            if (integrityImpacts != null)
                clone.integrityImpacts = integrityImpacts.values().stream().map(ILRImpact::clone)
                        .collect(Collectors.toMap(ILRImpact::getType, Function.identity()));

            if (availabilityImpacts != null)
                clone.availabilityImpacts = availabilityImpacts.values().stream().map(ILRImpact::clone)
                        .collect(Collectors.toMap(ILRImpact::getType, Function.identity()));

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new TrickException("error.clone.asset_impact", "AssetImpact cannot be copied");
        }
    }

    public AssetImpact clone(Asset asset) {
        final AssetImpact clone = clone();
        clone.asset = asset;
        return clone;
    }

    public AssetImpact duplicate() {
        try {
            final AssetImpact clone = (AssetImpact) super.clone();
            if (confidentialityImpacts != null)
                clone.confidentialityImpacts = confidentialityImpacts.values().stream().map(ILRImpact::duplicate)
                        .collect(Collectors.toMap(ILRImpact::getType, Function.identity()));

            if (integrityImpacts != null)
                clone.integrityImpacts = integrityImpacts.values().stream().map(ILRImpact::duplicate)
                        .collect(Collectors.toMap(ILRImpact::getType, Function.identity()));

            if (availabilityImpacts != null)
                clone.availabilityImpacts = availabilityImpacts.values().stream().map(ILRImpact::duplicate)
                        .collect(Collectors.toMap(ILRImpact::getType, Function.identity()));

            clone.id = 0;

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new TrickException("error.clone.asset_impact", "AssetImpact cannot be copied");
        }
    }

    public AssetImpact duplicate(Asset asset) {
        final AssetImpact clone = duplicate();
        clone.asset = asset;
        return clone;
    }

}
