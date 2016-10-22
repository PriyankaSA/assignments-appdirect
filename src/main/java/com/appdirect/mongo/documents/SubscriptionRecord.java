package com.appdirect.mongo.documents;

import com.appdirect.dto.subscription.PricingDuration;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "subscription")
public class SubscriptionRecord {
    @Id
    private String id;

    private String companyName;
    private String companyUuid;
    private String edition;
    private PricingDuration pricingDuration;
    private String marketPlaceBaseUrl;
    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public String getMarketPlaceBaseUrl() {
        return marketPlaceBaseUrl;
    }

    public void setMarketPlaceBaseUrl(String marketPlaceBaseUrl) {
        this.marketPlaceBaseUrl = marketPlaceBaseUrl;
    }

    public String getCompanyUuid() {
        return companyUuid;
    }

    public void setCompanyUuid(String companyUuid) {
        this.companyUuid = companyUuid;
    }

    public PricingDuration getPricingDuration() {
        return pricingDuration;
    }

    public void setPricingDuration(PricingDuration pricingDuration) {
        this.pricingDuration = pricingDuration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
