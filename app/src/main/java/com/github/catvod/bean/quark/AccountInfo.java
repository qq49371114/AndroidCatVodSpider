package com.github.catvod.bean.quark;

public class AccountInfo {
    private boolean success;
    private Data data;
    private String code;

    public AccountInfo() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public static class Data {
        private String nickname;
        private String avatarUri;
        private String mobilekps;
        private Config config;

        public Data() {
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getAvatarUri() {
            return avatarUri;
        }

        public void setAvatarUri(String avatarUri) {
            this.avatarUri = avatarUri;
        }

        public String getMobilekps() {
            return mobilekps;
        }

        public void setMobilekps(String mobilekps) {
            this.mobilekps = mobilekps;
        }

        public Config getConfig() {
            return config;
        }

        public void setConfig(Config config) {
            this.config = config;
        }
    }

    public static class Config {
        public Config() {
        }
        // 可以根据实际情况添加属性
    }
}
