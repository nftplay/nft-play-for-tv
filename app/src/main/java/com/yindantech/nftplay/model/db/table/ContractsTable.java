package com.yindantech.nftplay.model.db.table;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.JoinProperty;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

/**
 * ContractsTable
 */
@Entity
public class ContractsTable {

    @Id(autoincrement = true)
    private Long id;

    @Index(unique = true)
    private String address;
    private String asset_contract_type;
    private String schema_name;
    private String symbol;
    private String name;
    private String description;
    private String image_url;
    private String created_date;
    private Long created_time;
    private String owner_address;
    private String external_link;
    @ToMany(joinProperties = {@JoinProperty(name = "address", referencedName = "contract_address")})
    private List<AssetTable> assets;

    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /**
     * Used for active entity operations.
     */
    @Generated(hash = 666667246)
    private transient ContractsTableDao myDao;

    @Generated(hash = 529063958)
    public ContractsTable(Long id, String address, String asset_contract_type, String schema_name, String symbol,
            String name, String description, String image_url, String created_date, Long created_time,
            String owner_address, String external_link) {
        this.id = id;
        this.address = address;
        this.asset_contract_type = asset_contract_type;
        this.schema_name = schema_name;
        this.symbol = symbol;
        this.name = name;
        this.description = description;
        this.image_url = image_url;
        this.created_date = created_date;
        this.created_time = created_time;
        this.owner_address = owner_address;
        this.external_link = external_link;
    }

    @Generated(hash = 1238682177)
    public ContractsTable() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAsset_contract_type() {
        return this.asset_contract_type;
    }

    public void setAsset_contract_type(String asset_contract_type) {
        this.asset_contract_type = asset_contract_type;
    }

    public String getSchema_name() {
        return this.schema_name;
    }

    public void setSchema_name(String schema_name) {
        this.schema_name = schema_name;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage_url() {
        return this.image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getCreated_date() {
        return this.created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public Long getCreated_time() {
        return this.created_time;
    }

    public void setCreated_time(Long created_time) {
        this.created_time = created_time;
    }

    public String getOwner_address() {
        return this.owner_address;
    }

    public void setOwner_address(String owner_address) {
        this.owner_address = owner_address;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1675042802)
    public List<AssetTable> getAssets() {
        if (assets == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            AssetTableDao targetDao = daoSession.getAssetTableDao();
            List<AssetTable> assetsNew = targetDao._queryContractsTable_Assets(address);
            synchronized (this) {
                if (assets == null) {
                    assets = assetsNew;
                }
            }
        }
        return assets;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 1529819685)
    public synchronized void resetAssets() {
        assets = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    public String getExternal_link() {
        return this.external_link;
    }

    public void setExternal_link(String external_link) {
        this.external_link = external_link;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 936863941)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getContractsTableDao() : null;
    }

}
