package com.yindantech.nftplay.model.db.table;

import android.text.TextUtils;

import com.yindantech.nftplay.model.db.StringConverter;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.util.ArrayList;
import java.util.List;

import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.JoinProperty;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.DaoException;

/**
 * user
 */
@Entity
public class UserTable {

    @Id(autoincrement = true)
    private Long id;

    @Index(unique = true)
    private String address;
    private String username;
    private String profile_img_url;
    private long playIntervalTime;
    private long lastUpdateTime;

    @Convert(columnType = String.class, converter = StringConverter.class)
    private List<String> playList;//string:asset_id

    @ToMany(joinProperties = {@JoinProperty(name = "address", referencedName = "owner_address")})
    private List<ContractsTable> contracts;

    @ToMany(joinProperties = {@JoinProperty(name = "address", referencedName = "owner_address")})
    private List<CollectionsTable> collections;


    /**
     * isPlayList
     *
     * @return
     */
    public boolean isPlayList() {
        return null != getPlayList() && !getPlayList().isEmpty() && !TextUtils.isEmpty(getPlayList().get(0));
    }

    /**
     * isPlayTime
     *
     * @return
     */
    public boolean isPlayTime() {
        return getPlayIntervalTime() > 0;
    }


    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;


    /**
     * Used for active entity operations.
     */
    @Generated(hash = 1224316336)
    private transient UserTableDao myDao;


    @Generated(hash = 1751817170)
    public UserTable(Long id, String address, String username, String profile_img_url,
                     long playIntervalTime, long lastUpdateTime, List<String> playList) {
        this.id = id;
        this.address = address;
        this.username = username;
        this.profile_img_url = profile_img_url;
        this.playIntervalTime = playIntervalTime;
        this.lastUpdateTime = lastUpdateTime;
        this.playList = playList;
    }

    @Generated(hash = 726134616)
    public UserTable() {
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

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfile_img_url() {
        return this.profile_img_url;
    }

    public void setProfile_img_url(String profile_img_url) {
        this.profile_img_url = profile_img_url;
    }

    public List<String> getPlayList() {
        if (null == this.playList) {
            this.playList = new ArrayList<>();
        }
        return this.playList;
    }

    public void setPlayList(List<String> playList) {
        this.playList = playList;
    }

    public long getPlayIntervalTime() {
        return this.playIntervalTime;
    }

    public void setPlayIntervalTime(long playIntervalTime) {
        this.playIntervalTime = playIntervalTime;
    }

    public long getLastUpdateTime() {
        return this.lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1712984301)
    public List<CollectionsTable> getCollections() {
        if (collections == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            CollectionsTableDao targetDao = daoSession.getCollectionsTableDao();
            List<CollectionsTable> collectionsNew = targetDao._queryUserTable_Collections(address);
            synchronized (this) {
                if (collections == null) {
                    collections = collectionsNew;
                }
            }
        }
        return collections;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 2142528386)
    public synchronized void resetCollections() {
        collections = null;
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

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 307590045)
    public List<ContractsTable> getContracts() {
        if (contracts == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ContractsTableDao targetDao = daoSession.getContractsTableDao();
            List<ContractsTable> contractsNew = targetDao._queryUserTable_Contracts(address);
            synchronized (this) {
                if (contracts == null) {
                    contracts = contractsNew;
                }
            }
        }
        return contracts;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 1157686510)
    public synchronized void resetContracts() {
        contracts = null;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1889643915)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getUserTableDao() : null;
    }
}
