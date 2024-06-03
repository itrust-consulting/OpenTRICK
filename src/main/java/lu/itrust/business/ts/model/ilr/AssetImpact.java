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
import jakarta.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.model.asset.Asset;
import lu.itrust.business.ts.model.scale.ScaleType;


/**
 * Represents the impact of an asset in terms of confidentiality, integrity, and availability.
 * This class is used to store and manipulate the impact values for different scales of the asset.
 */
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

    @Transient
    private int maxConfidentialityImpact = -2;

    @Transient
    private int maxIntegratyImpact = -2;

    @Transient
    private int maxAvailabilityImpact = -2;

    public AssetImpact() {
    }

    public AssetImpact(Asset asset) {
        setAsset(asset);
    }

    /**
     * Returns the ID of the AssetImpact.
     *
     * @return the ID of the AssetImpact
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the ID of the AssetImpact.
     *
     * @param id the ID to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Represents an asset.
     */
    public Asset getAsset() {
        return asset;
    }

    /**
     * Sets the asset for this AssetImpact.
     *
     * @param asset the asset to be set
     */
    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    /**
     * Returns the map of confidentiality impacts.
     *
     * @return the map of confidentiality impacts
     */
    public Map<ScaleType, ILRImpact> getConfidentialityImpacts() {
        return confidentialityImpacts;
    }

    /**
     * Sets the confidentiality impacts for the asset.
     *
     * @param confidentialityImpacts a map containing the confidentiality impacts for different scale types
     */
    public void setConfidentialityImpacts(Map<ScaleType, ILRImpact> confidentialityImpacts) {
        this.confidentialityImpacts = confidentialityImpacts;
    }

    /**
     * Returns the integrity impacts of the asset.
     *
     * @return a map containing the integrity impacts of the asset
     */
    public Map<ScaleType, ILRImpact> getIntegrityImpacts() {
        return integrityImpacts;
    }

    /**
     * Sets the integrity impacts for the asset.
     *
     * @param integrityImpacts a map containing the integrity impacts for different scale types
     */
    public void setIntegrityImpacts(Map<ScaleType, ILRImpact> integrityImpacts) {
        this.integrityImpacts = integrityImpacts;
    }

    /**
     * Returns the availability impacts of the asset.
     *
     * @return a map containing the availability impacts of the asset
     */
    public Map<ScaleType, ILRImpact> getAvailabilityImpacts() {
        return availabilityImpacts;
    }

    /**
     * Sets the availability impacts for this AssetImpact.
     *
     * @param availabilityImpacts a map containing the availability impacts for different scale types
     */
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

    /**
     * Creates a duplicate of the current AssetImpact object with the specified asset.
     * 
     * @param asset The asset to be set in the cloned object.
     * @return A new AssetImpact object with the same properties as the original, but with the specified asset.
     */
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

    /**
     * Sets the maximum confidentiality impact for this asset impact.
     *
     * @param maxConfidentialityImpact the maximum confidentiality impact to set
     */
    public void setMaxConfidentialityImpact(int maxConfidentialityImpact) {
        this.maxConfidentialityImpact = maxConfidentialityImpact;
    }

    /**
     * Sets the maximum integrity impact for the asset.
     *
     * @param maxIntegratyImpact the maximum integrity impact to set
     */
    public void setMaxIntegratyImpact(int maxIntegratyImpact) {
        this.maxIntegratyImpact = maxIntegratyImpact;
    }

    /**
     * Sets the maximum availability impact for the asset.
     *
     * @param maxAvailabilityImpact the maximum availability impact to set
     */
    public void setMaxAvailabilityImpact(int maxAvailabilityImpact) {
        this.maxAvailabilityImpact = maxAvailabilityImpact;
    }

    /**
     * Returns the maximum confidentiality impact of the asset.
     *
     * @return the maximum confidentiality impact
     */
    public int getMaxConfidentialityImpact() {
        return maxConfidentialityImpact;
    }

    /**
     * Returns the maximum integrity impact.
     *
     * @return the maximum integrity impact
     */
    public int getMaxIntegratyImpact() {
        return maxIntegratyImpact;
    }

    /**
     * Returns the maximum availability impact.
     *
     * @return the maximum availability impact
     */
    public int getMaxAvailabilityImpact() {
        return maxAvailabilityImpact;
    }

    /**
     * Creates a duplicate of the current AssetImpact object with the specified asset.
     * 
     * @param asset The asset to be set in the cloned object.
     * @return A new AssetImpact object with the same properties as the original, but with the specified asset.
     */
    public AssetImpact duplicate(Asset asset) {
        final AssetImpact clone = duplicate();
        clone.asset = asset;
        return clone;
    }


}
