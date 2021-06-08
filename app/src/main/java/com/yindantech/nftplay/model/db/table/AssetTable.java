package com.yindantech.nftplay.model.db.table;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Generated;

/**
 * nft  asset
 */

@Entity
public class AssetTable {

    @Id(autoincrement = true)
    private Long id;

    //Assets NFT
    @Index(unique = true)
    private Long asset_id;
    private String asset_token_id;
    private String asset_image_url;
    private String asset_image_preview_url;
    private String asset_image_thumbnail_url;
    private String asset_image_original_url;
    private String asset_name;
    private String asset_description;
    private String asset_external_link;
    private String asset_animation_url; //Media file URL, may not have
    private String asset_animation_original_url;
    private String owner_address;
    private String creator_username;
    private String creator_profile_img_url;
    private String creator_address;
    private String contract_address;
    private String collection_name;


    @Override
    public String toString() {
        return "AssetTable{" +
                "id=" + id +
                ", asset_id=" + asset_id +
                ", asset_token_id='" + asset_token_id + '\'' +
                ", asset_image_url='" + asset_image_url + '\'' +
                ", asset_image_preview_url='" + asset_image_preview_url + '\'' +
                ", asset_image_thumbnail_url='" + asset_image_thumbnail_url + '\'' +
                ", asset_image_original_url='" + asset_image_original_url + '\'' +
                ", asset_name='" + asset_name + '\'' +
                ", asset_description='" + asset_description + '\'' +
                ", asset_external_link='" + asset_external_link + '\'' +
                ", asset_animation_url='" + asset_animation_url + '\'' +
                ", asset_animation_original_url='" + asset_animation_original_url + '\'' +
                ", owner_address='" + owner_address + '\'' +
                ", creator_username='" + creator_username + '\'' +
                ", creator_profile_img_url='" + creator_profile_img_url + '\'' +
                ", creator_address='" + creator_address + '\'' +
                ", contract_address='" + contract_address + '\'' +
                ", collection_name='" + collection_name + '\'' +
                '}';
    }

    @Generated(hash = 1292570618)
    public AssetTable(Long id, Long asset_id, String asset_token_id, String asset_image_url,
                      String asset_image_preview_url, String asset_image_thumbnail_url,
                      String asset_image_original_url, String asset_name, String asset_description,
                      String asset_external_link, String asset_animation_url,
                      String asset_animation_original_url, String owner_address,
                      String creator_username, String creator_profile_img_url, String creator_address,
                      String contract_address, String collection_name) {
        this.id = id;
        this.asset_id = asset_id;
        this.asset_token_id = asset_token_id;
        this.asset_image_url = asset_image_url;
        this.asset_image_preview_url = asset_image_preview_url;
        this.asset_image_thumbnail_url = asset_image_thumbnail_url;
        this.asset_image_original_url = asset_image_original_url;
        this.asset_name = asset_name;
        this.asset_description = asset_description;
        this.asset_external_link = asset_external_link;
        this.asset_animation_url = asset_animation_url;
        this.asset_animation_original_url = asset_animation_original_url;
        this.owner_address = owner_address;
        this.creator_username = creator_username;
        this.creator_profile_img_url = creator_profile_img_url;
        this.creator_address = creator_address;
        this.contract_address = contract_address;
        this.collection_name = collection_name;
    }

    @Generated(hash = 1713492618)
    public AssetTable() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAsset_id() {
        return this.asset_id;
    }

    public void setAsset_id(Long asset_id) {
        this.asset_id = asset_id;
    }

    public String getAsset_token_id() {
        return this.asset_token_id;
    }

    public void setAsset_token_id(String asset_token_id) {
        this.asset_token_id = asset_token_id;
    }

    public String getAsset_image_url() {
        return this.asset_image_url;
    }

    public void setAsset_image_url(String asset_image_url) {
        this.asset_image_url = asset_image_url;
    }

    public String getAsset_image_preview_url() {
        return this.asset_image_preview_url;
    }

    public void setAsset_image_preview_url(String asset_image_preview_url) {
        this.asset_image_preview_url = asset_image_preview_url;
    }

    public String getAsset_image_thumbnail_url() {
        return this.asset_image_thumbnail_url;
    }

    public void setAsset_image_thumbnail_url(String asset_image_thumbnail_url) {
        this.asset_image_thumbnail_url = asset_image_thumbnail_url;
    }

    public String getAsset_image_original_url() {
        return this.asset_image_original_url;
    }

    public void setAsset_image_original_url(String asset_image_original_url) {
        this.asset_image_original_url = asset_image_original_url;
    }

    public String getAsset_name() {
        return this.asset_name;
    }

    public void setAsset_name(String asset_name) {
        this.asset_name = asset_name;
    }

    public String getAsset_description() {
        return this.asset_description;
    }

    public void setAsset_description(String asset_description) {
        this.asset_description = asset_description;
    }

    public String getAsset_external_link() {
        return this.asset_external_link;
    }

    public void setAsset_external_link(String asset_external_link) {
        this.asset_external_link = asset_external_link;
    }

    public String getOwner_address() {
        return this.owner_address;
    }

    public void setOwner_address(String owner_address) {
        this.owner_address = owner_address;
    }

    public String getCreator_username() {
        return this.creator_username;
    }

    public void setCreator_username(String creator_username) {
        this.creator_username = creator_username;
    }

    public String getCreator_profile_img_url() {
        return this.creator_profile_img_url;
    }

    public void setCreator_profile_img_url(String creator_profile_img_url) {
        this.creator_profile_img_url = creator_profile_img_url;
    }

    public String getCreator_address() {
        return this.creator_address;
    }

    public void setCreator_address(String creator_address) {
        this.creator_address = creator_address;
    }

    public String getContract_address() {
        return this.contract_address;
    }

    public void setContract_address(String contract_address) {
        this.contract_address = contract_address;
    }


    public String getCollection_name() {
        return this.collection_name;
    }

    public void setCollection_name(String collection_name) {
        this.collection_name = collection_name;
    }

    public String getAsset_animation_url() {
        return this.asset_animation_url;
    }

    public void setAsset_animation_url(String asset_animation_url) {
        this.asset_animation_url = asset_animation_url;
    }

    public String getAsset_animation_original_url() {
        return this.asset_animation_original_url;
    }

    public void setAsset_animation_original_url(String asset_animation_original_url) {
        this.asset_animation_original_url = asset_animation_original_url;
    }


}
