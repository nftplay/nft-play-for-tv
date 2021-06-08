package com.yindantech.nftplay.model;

import java.util.List;

/**
 * Assets json bean
 */
public class AssetsBean {
    private List<Asset> assets;

    public List<Asset> getAssets() {
        return assets;
    }

    public void setAssets(List<Asset> assets) {
        this.assets = assets;
    }

    public static class Asset {
        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getToken_id() {
            return token_id;
        }

        public void setToken_id(String token_id) {
            this.token_id = token_id;
        }

        public String getImage_url() {
            return image_url;
        }

        public void setImage_url(String image_url) {
            this.image_url = image_url;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public AssetContract getAsset_contract() {
            return asset_contract;
        }

        public void setAsset_contract(AssetContract asset_contract) {
            this.asset_contract = asset_contract;
        }

        public Owner getOwner() {
            return owner;
        }

        public void setOwner(Owner owner) {
            this.owner = owner;
        }

        public Collection getCollection() {
            return collection;
        }

        public void setCollection(Collection collection) {
            this.collection = collection;
        }

        public Creator getCreator() {
            return creator;
        }

        public void setCreator(Creator creator) {
            this.creator = creator;
        }

        public String getImage_preview_url() {
            return image_preview_url;
        }

        public void setImage_preview_url(String image_preview_url) {
            this.image_preview_url = image_preview_url;
        }

        public String getImage_thumbnail_url() {
            return image_thumbnail_url;
        }

        public void setImage_thumbnail_url(String image_thumbnail_url) {
            this.image_thumbnail_url = image_thumbnail_url;
        }

        public String getImage_original_url() {
            return image_original_url;
        }

        public void setImage_original_url(String image_original_url) {
            this.image_original_url = image_original_url;
        }

        public String getExternal_link() {
            return external_link;
        }

        public void setExternal_link(String external_link) {
            this.external_link = external_link;
        }

        public String getAnimation_url() {
            return animation_url;
        }

        public void setAnimation_url(String animation_url) {
            this.animation_url = animation_url;
        }

        public String getAnimation_original_url() {
            return animation_original_url;
        }

        public void setAnimation_original_url(String animation_original_url) {
            this.animation_original_url = animation_original_url;
        }


        private long id;
        private String token_id;
        private String image_url;
        private String image_preview_url;
        private String image_thumbnail_url;
        private String image_original_url;


        private String animation_url;
        private String animation_original_url;


        private String name;
        private String description;
        private String external_link;


        private AssetContract asset_contract;
        private Owner owner;
        private Collection collection;
        private Creator creator;

        public static class AssetContract {
            private String address;
            private String created_date;
            private String name;
            private String schema_name;//ERC1155
            private String symbol;//OPENSTORE
            private String description;
            private String external_link;
            private String asset_contract_type;
            private String image_url;

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public String getCreated_date() {
                return created_date;
            }

            public void setCreated_date(String created_date) {
                this.created_date = created_date;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getSchema_name() {
                return schema_name;
            }

            public void setSchema_name(String schema_name) {
                this.schema_name = schema_name;
            }

            public String getSymbol() {
                return symbol;
            }

            public void setSymbol(String symbol) {
                this.symbol = symbol;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public String getExternal_link() {
                return external_link;
            }

            public void setExternal_link(String external_link) {
                this.external_link = external_link;
            }


            public String getAsset_contract_type() {
                return asset_contract_type;
            }

            public void setAsset_contract_type(String asset_contract_type) {
                this.asset_contract_type = asset_contract_type;
            }

            public String getImage_url() {
                return image_url;
            }

            public void setImage_url(String image_url) {
                this.image_url = image_url;
            }


        }

        public static class Owner {
            private User user;

            public User getUser() {
                return user;
            }

            public void setUser(User user) {
                this.user = user;
            }

            public String getProfile_img_url() {
                return profile_img_url;
            }

            public void setProfile_img_url(String profile_img_url) {
                this.profile_img_url = profile_img_url;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            private String profile_img_url;
            private String address;


            public class User {
                private String username;

                public String getUsername() {
                    return username;
                }

                public void setUsername(String username) {
                    this.username = username;
                }
            }
        }

        public class Collection {


            public String getCreated_date() {
                return created_date;
            }

            public void setCreated_date(String created_date) {
                this.created_date = created_date;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getImage_url() {
                return image_url;
            }

            public void setImage_url(String image_url) {
                this.image_url = image_url;
            }

            private String created_date;//2021-03-20T09:49:50.936389
            private String description;
            private String name;
            private String image_url;

        }

        public class Creator {
            private User user;

            public User getUser() {
                return user;
            }

            public void setUser(User user) {
                this.user = user;
            }

            public String getProfile_img_url() {
                return profile_img_url;
            }

            public void setProfile_img_url(String profile_img_url) {
                this.profile_img_url = profile_img_url;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            private String profile_img_url;
            private String address;

            public class User {
                public String getUsername() {
                    return username;
                }

                public void setUsername(String username) {
                    this.username = username;
                }

                private String username;
            }
        }
    }
}
