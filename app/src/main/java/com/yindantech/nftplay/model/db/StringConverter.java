package com.yindantech.nftplay.model.db;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * StringConverter
 */
public class StringConverter implements PropertyConverter<List<String>, String> {

    @Override
    public List<String> convertToEntityProperty(String databaseValue) {
        if (databaseValue == null) {
            return new ArrayList<>();
        } else {
            List<String> list = Arrays.asList(databaseValue.split(","));
            return list;
        }
    }

    @Override
    public String convertToDatabaseValue(List<String> entityProperty) {
        if (entityProperty == null) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();
            for (String value : entityProperty) {
                sb.append(value);
                sb.append(",");
            }
            return sb.toString();
        }
    }
}