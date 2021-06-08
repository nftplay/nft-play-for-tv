package com.yindantech.nftplay.model.db;


import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.yindantech.nftplay.common.base.BaseApp;
import com.yindantech.nftplay.common.interfaces.OnListener;
import com.yindantech.nftplay.model.db.table.AssetTable;
import com.yindantech.nftplay.model.db.table.AssetTableDao;
import com.yindantech.nftplay.model.db.table.CollectionsTable;
import com.yindantech.nftplay.model.db.table.CollectionsTableDao;
import com.yindantech.nftplay.model.db.table.ContractsTable;
import com.yindantech.nftplay.model.db.table.ContractsTableDao;
import com.yindantech.nftplay.model.db.table.DaoSession;
import com.yindantech.nftplay.model.db.table.UserTable;
import com.yindantech.nftplay.model.db.table.UserTableDao;
import com.yindantech.nftplay.common.utils.MyUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * DBUtils
 * The database operations are encapsulated here
 */
public class DBUtils {

    /**
     * getDaoSession
     *
     * @return DaoSession
     */
    public static DaoSession getDaoSession() {
        return BaseApp.getInstance().getDaoSession();
    }

    /**
     * saveUser
     *
     * @param user
     * @return
     */
    public static boolean saveUser(UserTable user) {
        try {
            getDaoSession().getUserTableDao().insertOrReplace(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * getUser
     *
     * @param address
     * @return
     */
    public static UserTable getUser(String address) {
        try {
            return getDaoSession().getUserTableDao().queryBuilder().where(UserTableDao.Properties.Address.eq(address)).build().unique();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * getUser
     *
     * @return
     */
    public static UserTable getLoginUser() {
        return getUser(MyUtils.getWalletAddress());
    }

    /**
     * updateDataLastTime
     */
    public static void updateDataLastTime() {
        UserTable loginUser = getLoginUser();
        loginUser.setLastUpdateTime(TimeUtils.getNowMills());
        saveUser(loginUser);
    }

    /**
     * saveCollections
     */
    public static void saveCollections(List<CollectionsTable> collectionsList) {
        try {
            if (null != collectionsList && !collectionsList.isEmpty())
                getDaoSession().getCollectionsTableDao().saveInTx(collectionsList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * getCollections
     * orderDesc Created_time
     *
     * @param address
     * @return
     */
    public static List<CollectionsTable> getCollections(String address) {
        try {
            return getDaoSession().getCollectionsTableDao().queryBuilder().where(CollectionsTableDao.Properties.Owner_address.eq(address)).orderDesc(CollectionsTableDao.Properties.Created_time).build().list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * getContracts
     *
     * @param address
     * @return
     */
    public static List<ContractsTable> getContracts(String address) {
        try {
            List<ContractsTable> list = getDaoSession().getContractsTableDao().queryBuilder().where(ContractsTableDao.Properties.Owner_address.eq(address)).orderDesc(ContractsTableDao.Properties.Created_time).build().list();
            //Rank in descending order according to NFT data volume in the contract
            //The default is in descending order of creation time
            Collections.sort(list, ((o1, o2) -> o2.getAssets().size() - o1.getAssets().size()));
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    /**
     * saveAsset
     */
    public static void saveAsset(List<AssetTable> assetList) {
        try {
            if (null != assetList && !assetList.isEmpty())
                getDaoSession().getAssetTableDao().saveInTx(assetList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Query the assets under the collection
     * Descending order by asset_id
     *
     * @param collection_name
     * @return
     */
    public static List<AssetTable> getAssetsByCollection(String collection_name) {
        try {
            return getDaoSession().getAssetTableDao().queryBuilder().where(AssetTableDao.Properties.Collection_name.eq(collection_name)).orderDesc(AssetTableDao.Properties.Asset_id).build().list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Check the assets under the contract
     * Descending order by asset_id
     *
     * @param contract_address
     * @return
     */
    public static List<AssetTable> getAssetsByContract(String contract_address) {
        try {
            return getDaoSession().getAssetTableDao().queryBuilder().where(AssetTableDao.Properties.Contract_address.eq(contract_address)).orderDesc(AssetTableDao.Properties.Asset_id).build().list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Query a specified asset
     *
     * @param asset_id
     * @return
     */
    public static AssetTable getAsset(Long asset_id) {
        try {
            return getDaoSession().getAssetTableDao().queryBuilder().where(AssetTableDao.Properties.Asset_id.eq(asset_id)).build().unique();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Batch query according to asset_id
     *
     * @param assetIds
     * @return
     */
    public static List<AssetTable> getAssetList(List<String> assetIds) {
        try {
            List<AssetTable> list = getDaoSession().getAssetTableDao().queryBuilder().where(AssetTableDao.Properties.Asset_id.in(assetIds)).orderDesc(AssetTableDao.Properties.Asset_id).build().list();
            //The default des , Specify sort : First pick, first show
            Collections.sort(list, (o1, o2) -> {
                String o1Id = String.valueOf(o1.getAsset_id());
                String o2Id = String.valueOf(o2.getAsset_id());
                if (assetIds.contains(o1Id) && assetIds.contains(o2Id)) {
                    if (assetIds.indexOf(o1Id) > assetIds.indexOf(o2Id)) {
                        return 1;
                    } else if (assetIds.indexOf(o1Id) == assetIds.indexOf(o2Id)) {
                        return 0;
                    } else {
                        return -1;
                    }
                } else if (assetIds.contains(o1Id) && !assetIds.contains(o2Id)) {
                    return -1;
                } else if (!assetIds.contains(o1Id) && assetIds.contains(o2Id)) {
                    return 1;
                }
                return 0;
            });
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

    }

    /**
     * Query all assets under the address
     *
     * @param address
     * @return
     */
    public static List<AssetTable> getAssetList(String address) {
        try {
            return getDaoSession().getAssetTableDao().queryBuilder().where(AssetTableDao.Properties.Owner_address.eq(address)).orderDesc(AssetTableDao.Properties.Asset_id).build().list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

    }


    /**
     * Update contract, collection, and asset data
     *
     * @param collectionsTableMap
     * @param assetTableList
     * @return
     */
    public static boolean updateData(Map<String, ContractsTable> contractsTableMap, Map<String, CollectionsTable> collectionsTableMap, List<AssetTable> assetTableList) {

        try {
            //1、Get the old data first
            UserTable loginUser = getLoginUser();
            String address = loginUser.getAddress();

            List<ContractsTable> oldContractsList = getContracts(address);
            List<CollectionsTable> oldCollectionsList = getCollections(address);
            List<AssetTable> oldAssetsList = getAssetList(address);


            //2、Delete new data that is not in the old data

            //Contracts
            List<ContractsTable> deleteContracts = new ArrayList<>();
            for (int i = 0; i < oldContractsList.size(); i++) {
                ContractsTable oldContractsTable = oldContractsList.get(i);
                //Determine if the old data exists in the new data
                boolean isExist = false;
                for (String addressKey : contractsTableMap.keySet()) {
                    if (oldContractsTable.getAddress().equals(addressKey)) {
                        contractsTableMap.get(addressKey).setId(oldContractsTable.getId());
                        isExist = true;
                        //Existence directly ends the cycle
                        break;
                    }
                }
                if (!isExist) {
                    //If it does not exist, mark it as deleted data
                    deleteContracts.add(oldContractsTable);
                }
            }
            //delete Action
            if (!deleteContracts.isEmpty()) {
                LogUtils.d("updateData：deleteContracts， size" + deleteContracts.size());
                getDaoSession().getContractsTableDao().deleteInTx(deleteContracts);
            } else {
                LogUtils.d("updateData：deleteContracts，no data");
            }

            //Collections
            List<CollectionsTable> deleteCollections = new ArrayList<>();
            for (int i = 0; i < oldCollectionsList.size(); i++) {
                CollectionsTable oldCollectionsTable = oldCollectionsList.get(i);
                //Determine if the old data exists in the new data
                boolean isExist = false;
                for (String name : collectionsTableMap.keySet()) {
                    if (oldCollectionsTable.getName().equals(name)) {
                        collectionsTableMap.get(name).setId(oldCollectionsTable.getId());
                        isExist = true;
                        //Existence directly ends the cycle
                        break;
                    }
                }
                if (!isExist) {
                    //If it does not exist, mark it as deleted data
                    deleteCollections.add(oldCollectionsTable);
                }
            }
            //delete Action
            if (!deleteCollections.isEmpty()) {
                LogUtils.d("updateData：deleteCollections, size" + deleteCollections.size());
                getDaoSession().getCollectionsTableDao().deleteInTx(deleteCollections);
            } else {
                LogUtils.d("updateData：deleteCollections，no");
            }

            // Assets
            List<AssetTable> deleteAssets = new ArrayList<>();
            for (int i = 0; i < oldAssetsList.size(); i++) {
                AssetTable oldAssetTable = oldAssetsList.get(i);
                //Determine if the old data exists in the new data
                boolean isExist = false;
                for (int x = 0; x < assetTableList.size(); x++) {
                    AssetTable newAssetTable = assetTableList.get(x);
                    if (oldAssetTable.getAsset_id().longValue() == newAssetTable.getAsset_id().longValue()) {
                        assetTableList.get(x).setId(oldAssetTable.getId());
                        isExist = true;
                        //Existence directly ends the cycle
                        break;
                    }
                }
                if (!isExist) {
                    //If it does not exist, mark it as deleted data
                    deleteAssets.add(oldAssetTable);
                }
            }
            //delete Action
            if (!deleteAssets.isEmpty()) {
                LogUtils.d("updateData：deleteAssets, size=" + deleteAssets.size());

                List<String> playList = new ArrayList<>();
                playList.addAll(loginUser.getPlayList());
                for (AssetTable deleteAsset : deleteAssets) {
                    //Delete the old NFT data, and also delete the data in the playback information
                    String assetId = String.valueOf(deleteAsset.getAsset_id());
                    if (playList.contains(assetId)) {
                        LogUtils.d("updateData：deleteAssets，delete playList", assetId);
                        playList.remove(assetId);
                    }
                }
                updatePlayList(playList);

                getDaoSession().getAssetTableDao().deleteInTx(deleteAssets);
            } else {
                LogUtils.d("updateData：deleteAssets，no");
            }
            //3、updateDATA
//            getDaoSession().getCollectionsTableDao().deleteAll();
//            getDaoSession().getAssetTableDao().deleteAll();

            List<CollectionsTable> newCollectionsTableList = new ArrayList<>();
            for (String name : collectionsTableMap.keySet()) {
                newCollectionsTableList.add(collectionsTableMap.get(name));
            }
            List<ContractsTable> newContractsTableList = new ArrayList<>();
            for (String addressKey : contractsTableMap.keySet()) {
                newContractsTableList.add(contractsTableMap.get(addressKey));
            }

            DBUtils.saveContracts(newContractsTableList);
            DBUtils.saveCollections(newCollectionsTableList);
            DBUtils.saveAsset(assetTableList);
            DBUtils.updateDataLastTime();

            LogUtils.d("updateData：Contracts，update size=" + newContractsTableList.size());
            LogUtils.d("updateData：Collections，update size=" + newCollectionsTableList.size());
            LogUtils.d("updateData：Asset，update size=" + assetTableList.size());

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }


    /**
     * saveCollections
     */
    private static void saveContracts(List<ContractsTable> contractsList) {
        try {
            if (null != contractsList && !contractsList.isEmpty())
                getDaoSession().getContractsTableDao().saveInTx(contractsList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * updatePlayList
     *
     * @param playList
     */
    public static void updatePlayList(List<String> playList) {
        UserTable loginUser = getLoginUser();
        List<String> newPlayList = new ArrayList<>();
        if (null != playList) {
            newPlayList.addAll(playList);
        }
        loginUser.setPlayList(newPlayList);
        saveUser(loginUser);
    }

    /**
     * updatePlayList
     *
     * @param intervalTime
     */
    public static void updateIntervalTime(long intervalTime) {
        UserTable loginUser = getLoginUser();
        loginUser.setPlayIntervalTime(intervalTime);
        saveUser(loginUser);
    }

    /**
     * deleteAllData
     */
    public static void deleteAllData(OnListener listener) {

        ThreadUtils.executeBySingle(new ThreadUtils.SimpleTask<Boolean>() {
            @Override
            public Boolean doInBackground() {
                try {
                    getDaoSession().getContractsTableDao().deleteAll();
                    getDaoSession().getCollectionsTableDao().deleteAll();
                    getDaoSession().getAssetTableDao().deleteAll();
                    updatePlayList(new ArrayList<>());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }

            @Override
            public void onSuccess(Boolean result) {
                listener.onListener(result);
            }
        });
    }


}
