package com.spockchain.wallet.service;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FetchTransactionsRequestBody {

    @SerializedName("addr")
    @Expose
    private String addr;
    @SerializedName("length")
    @Expose
    private Integer length;
    @SerializedName("start")
    @Expose
    private Integer start;
    @SerializedName("contract")
    @Expose
    private String contract;

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public FetchTransactionsRequestBody withAddr(String addr) {
        this.addr = addr;
        return this;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public FetchTransactionsRequestBody withLength(Integer length) {
        this.length = length;
        return this;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public FetchTransactionsRequestBody withStart(Integer start) {
        this.start = start;
        return this;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contractAddr) {
        this.contract = contractAddr;
    }

    public FetchTransactionsRequestBody withContract(String contractAddr) {
        this.contract = contractAddr;
        return this;
    }
}