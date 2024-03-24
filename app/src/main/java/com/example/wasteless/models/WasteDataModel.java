package com.example.wasteless.models;

import java.util.HashMap;
import java.util.Map;


public class WasteDataModel {
    private float weight;
    private String productName;
    private String productImage = null;
    private String uuid;

    public WasteDataModel() {

    }

    public WasteDataModel(float weight, String productName, String productImage, String uuid) {
        this.weight = weight;
        this.productName = productName;
        this.productImage = productImage;
        this.uuid = uuid;
    }

    public WasteDataModel(float weight, String productName, String productImage) {
        this.weight = weight;
        this.productName = productName;
        this.productImage = productImage;
    }

    // Convert WasteDataModel to HashMap for Firebase
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("weight", weight);
        result.put("productName", productName);
        result.put("productImage", productImage);
        result.put("uuid", uuid);
        return result;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
