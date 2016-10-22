package com.appdirect.dto.subscription;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Order {

    private String editionCode;
    private PricingDuration pricingDuration;
    private Item item;

    public Order(){

    }
    @JsonCreator
    public Order(@JsonProperty("editionCode") String editionCode,
                 @JsonProperty("pricingDuration") PricingDuration pricingDuration,
                 @JsonProperty("item") Item item){
        this.editionCode = editionCode;
        this.pricingDuration = pricingDuration;
        this.item = item;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public String getEditionCode() {
        return editionCode;
    }

    public void setEditionCode(String editionCode) {
        this.editionCode = editionCode;
    }

    public PricingDuration getPricingDuration() {
        return pricingDuration;
    }

    public void setPricingDuration(PricingDuration pricingDuration) {
        this.pricingDuration = pricingDuration;
    }
}