package com.spockchain.wallet.entity;

import java.util.List;

public class TransactionMetadata {

    private String hash;
    private String from;
    private String to;
    private String value;

    public long getTimestamp() {
        return timestamp;
    }

    private long timestamp;

    public TransactionMetadata(String hash, String from, String to, String value, long timestamp) {
        this.hash = hash;
        this.from = from;
        this.to = to;
        this.value = value;
        this.timestamp = timestamp;
    }

    public TransactionMetadata(List<String> webResponse) {
        this(webResponse.get(0), webResponse.get(2), webResponse.get(3), webResponse.get(1), Long.parseLong(webResponse.get(6)));
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
