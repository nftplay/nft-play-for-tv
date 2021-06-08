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
 * assets collections
 */
@Entity
public class CollectionsTable {
    @Id(autoincrement = true)
    private Long id;

    @Index(unique = true)
    private String name;//It is verified that there is no ID field on Collections on OpenSEA. The name is unique and the suffix is automatically appended to the same name when it is created
    private String image_url;
    private String created_date;
    private Long created_time;
    private String owner_address;

    @ToMany(joinProperties = {@JoinProperty(name = "name", referencedName = "collection_name")})
    private List<AssetTable> assets;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1347551459)
    private transient CollectionsTableDao myDao;
    @Generated(hash = 1264599384)
    public CollectionsTable(Long id, String name, String image_url, String created_date,
            Long created_time, String owner_address) {
        this.id = id;
        this.name = name;
        this.image_url = image_url;
        this.created_date = created_date;
        this.created_time = created_time;
        this.owner_address = owner_address;
    }
    @Generated(hash = 1667840027)
    public CollectionsTable() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
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
    @Generated(hash = 320515852)
    public List<AssetTable> getAssets() {
        if (assets == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            AssetTableDao targetDao = daoSession.getAssetTableDao();
            List<AssetTable> assetsNew = targetDao._queryCollectionsTable_Assets(name);
            synchronized (this) {
                if (assets == null) {
                    assets = assetsNew;
                }
            }
        }
        return assets;
    }
    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
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
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 259188887)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getCollectionsTableDao() : null;
    }

   
}
