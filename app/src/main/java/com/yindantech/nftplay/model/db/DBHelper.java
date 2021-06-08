package com.yindantech.nftplay.model.db;


import android.content.Context;
import android.database.Cursor;

import com.blankj.utilcode.util.LogUtils;
import com.yindantech.nftplay.model.db.table.DaoMaster;

import org.greenrobot.greendao.database.Database;

/**
 * DBHelper
 */
public class DBHelper extends DaoMaster.OpenHelper {

    public static final String DBNAME = "nftplay-db";

    public DBHelper(Context context) {
        super(context, DBNAME, null);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        LogUtils.i("onUpgrade oldVersion=" + oldVersion + " newVersion=" + newVersion);
        try {

        } catch (Exception e) {
            e.printStackTrace();
            DaoMaster.dropAllTables(db, true);
            DaoMaster.createAllTables(db, true);
        }
    }

    /**
     * isExistColumn
     *
     * @param db
     * @param tableName
     * @param columnName
     * @return
     */
    public static boolean isExistColumn(Database db, String tableName, String columnName) {
        boolean isExist = false;
        String sql = "SELECT sql FROM sqlite_master WHERE type='table' AND name='" + tableName + "';";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            String str = cursor.getString(0);
            int index = str.indexOf(columnName);
            if (index >= 0) {
                isExist = true;
            }
        }
        cursor.close();
        return isExist;
    }
}
