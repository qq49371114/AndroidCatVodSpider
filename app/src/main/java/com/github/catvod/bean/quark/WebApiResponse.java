package com.github.catvod.bean.quark;

import com.google.gson.annotations.SerializedName;

public class WebApiResponse {
    private int status;
    private String message;
    private Data data;

    public WebApiResponse() {
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        public Data() {
        }
        private Members members;

        public Members getMembers() {
            return members;
        }

        public void setMembers(Members members) {
            this.members = members;
        }
    }

    public static class Members {
        public Members() {
        }
        private String token;
        @SerializedName("service_ticket")
        private String serviceTicket;

        public String getServiceTicket() {
            return serviceTicket;
        }

        public void setServiceTicket(String serviceTicket) {
            this.serviceTicket = serviceTicket;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
