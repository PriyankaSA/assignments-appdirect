package com.appdirect.web;

import com.appdirect.dto.Account;
import com.appdirect.dto.Address;
import com.appdirect.dto.Company;
import com.appdirect.dto.User;
import com.appdirect.dto.subscription.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/proxy/api/events")
public class AppDirectProxyController {

    /*
            {
    "type": "SUBSCRIPTION_ORDER",
    "marketplace": {
      "baseUrl": "https://www.acme.com",
      "partner": "APPDIRECT"
    },
    "creator": {
      "address": {
        "firstName": "Test",
        "fullName": "Test User",
        "lastName": "User"
      },
      "email": "testuser@testco.com",
      "firstName": "Test",
      "language": "en",
      "lastName": " User",
      "locale": "en_US",
      "openId": "https://www.acme.com/openid/id/47cb8f55-1af6-5bfc-9a7d-8061d3aa0c97",
      "uuid": "47cb8f55-1af6-5bfc-9a7d-8061d3aa0c97"
    },
    "payload": {
      "company": {
        "country": "US",
        "name": "tester",
        "phoneNumber": "1-800-333-3333",
        "uuid": "385beb51-51ae-4ffe-8c05-3f35a9f99825",
        "website": "www.testco.com"
      },
      "order": {
        "editionCode": "Standard",
        "pricingDuration": "MONTHLY",
        "item": {
          "quantity": "4",
          "unit": "USER"
        }
      }
    }
}
    */
    @RequestMapping(value = "/createSubscription", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Notification createSubscriptionEvent(){
        MarketPlace marketPlace = new MarketPlace("https://www.acme.com", "APPDIRECT");
        Address address = new Address("Sample", "Sample Tester", "Tester", null, null, null, null, null);
        User creator = new User("211aa369-f53b-4606-8887-80a361e0ef66","https://www.acme.com/openid/id/211aa367-f53b-4606-8887-80a381e0ef69",
                "sampletester@testco.com", "Sample", "Tester", "en", address, "en_US");
        Company company = new Company("bd58b532-323b-4627-a828-57729489b27b", "US", "Sample Testing co.","1-800-333-3333", "www.testerco.com");
        Item item = new Item(4, "USER");
        Order order = new Order("Standard", PricingDuration.MONTHLY, item);
        Payload payload = new Payload(company, order, null, null);
        Notification notification = new Notification(Notification.Type.SUBSCRIPTION_ORDER, marketPlace, creator, payload);
        return notification;
    }

    /*{
        "type": "SUBSCRIPTION_CHANGE",
            "marketplace": {
            "baseUrl": "https://www.acme.com",
                "partner": "APPDIRECT"
    },
        "creator": {
        "address": {
            "city": "San Jose",
                    "country": "US",
                    "firstName": "Test",
                    "fullName": "Test User",
                    "lastName": "User",
                    "state": "CA",
                    "street": "1 Main St",
                    "zip": "95131"
        },
        "email": "testuser@testco.com",
                "firstName": "Test",
                "language": "en",
                "lastName": "User",
                "locale": "en_US",
                "openId": "https://www.acme.com/openid/id/7f59aad1-85cd-4c04-b35b-906ee53acc71",
                "uuid": "7f59aad1-85cd-4c04-b35b-906ee53acc71"
    },
        "payload": {
        "account": {
            "accountIdentifier": "206123",
                    "status": "ACTIVE"
        },
        "order": {
            "editionCode": "DME",
                    "pricingDuration": "DAILY",
                    "item": {
                "quantity": "0",
                        "unit": "GIGABYTE"
            }
        }
    }
    }*/
    @RequestMapping(value = "/changeSubscription", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Notification changeSubscriptionEvent(@RequestParam("accountId") String accountId){
        MarketPlace marketPlace = new MarketPlace("https://www.acme.com", "APPDIRECT");
        Address address = new Address("Test", "Test User", "User", "San Jose", "US", "CA", "1 Main St", "95131");
        User creator = new User("211aa369-f53b-4606-8887-80a361e0ef66","https://www.acme.com/openid/id/211aa367-f53b-4606-8887-80a381e0ef69",
                "sampletester@testco.com", "Sample", "Tester", "en", address, "en_US");
        Account account = new Account(accountId, Account.Status.ACTIVE);
        Item item = new Item(0, "GIGABYTE");
        Order order = new Order("DME", PricingDuration.DAILY, item);
        Payload payload = new Payload(null, order, account, null);
        Notification notification = new Notification(Notification.Type.SUBSCRIPTION_CHANGE, marketPlace, creator, payload);
        return notification;
    }

    /*{
        "type": "SUBSCRIPTION_CANCEL",
            "marketplace": {
        "baseUrl": "https://www.acme.com",
                "partner": "APPDIRECT"
    },
        "creator": {
        "address": {
            "city": "Sommerville",
                    "country": "US",
                    "firstName": "Test",
                    "fullName": "Test User",
                    "lastName": "User",
                    "phone": "5305556465",
                    "state": "MA",
                    "street1": "55 Grove St",
                    "zip": "02144"
        },
        "email": "testuser@testco.com",
                "firstName": "Test",
                "language": "en",
                "lastName": "User",
                "locale": "en_US",
                "openId": "https://www.acme.com/openid/id/d124bf8b-0b0b-40d3-831b-b7f5a514d487",
                "uuid": "d124bf8b-0b0b-40d3-831b-b7f5a514d487"
    },
        "payload": {
        "account": {
            "accountIdentifier": "9d6fca98-aa94-462b-85fa-118804ad3fe3",
                    "status": "ACTIVE"
        }
    }
    }*/
    @RequestMapping(value = "/cancelSubscription", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Notification cancelSubscriptionEvent(@RequestParam("accountId") String accountId){
        MarketPlace marketPlace = new MarketPlace("https://www.acme.com", "APPDIRECT");
        Address address = new Address("Test", "Test User", "User", "San Jose", "US", "CA", "1 Main St", "95131");
        User creator = new User("211aa369-f53b-4606-8887-80a361e0ef66","https://www.acme.com/openid/id/211aa367-f53b-4606-8887-80a381e0ef69",
                "sampletester@testco.com", "Sample", "Tester", "en", address, "en_US");
        Account account = new Account(accountId, Account.Status.ACTIVE);
        Item item = new Item(0, "GIGABYTE");
        Order order = new Order("DME", PricingDuration.DAILY, item);
        Payload payload = new Payload(null, order, account, null);
        Notification notification = new Notification(Notification.Type.SUBSCRIPTION_CANCEL, marketPlace, creator, payload);
        return notification;
    }
    /*{
        "type": "USER_ASSIGNMENT",
            "marketplace": {
        "baseUrl": "https://www.acme.com",
                "partner": "APPDIRECT"
    },
        "creator": {
        "email": "sampletester@testco.com",
                "firstName": "Another",
                "language": "en",
                "lastName": "User",
                "locale": "en_US",
                "openId": "https://www.acme.com/openid/id/7ac30510-c54c-45ca-9c2f-f4d6b3aa2c15",
                "uuid": "7ac30510-c54c-45ca-9c2f-f4d6b3aa2c15"
    },
        "payload": {
        "account": {
            "accountIdentifier": "199722",
                    "status": "ACTIVE"
        },
        "user": {
            "email": "sampletester2@testco.com",
                    "firstName": "Another2",
                    "language": "en",
                    "lastName": "User2",
                    "locale": "en_US",
                    "openId": "https://www.acme.com/openid/id/c734676b-40f6-4783-b4ee-e20d59bbf943",
                    "uuid": "c734676b-40f6-4783-b4ee-e20d59bbf943"
        }
    }
    }*/
    @RequestMapping(value = "/userAssignment", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Notification assignUserEvent(@RequestParam("accountId") String accountId){
        MarketPlace marketPlace = new MarketPlace("https://www.acme.com", "APPDIRECT");
        Address address = new Address("Test", "Test User", "User", "San Jose", "US", "CA", "1 Main St", "95131");
        User creator = new User("211aa369-f53b-4606-8887-80a361e0ef66","https://www.acme.com/openid/id/211aa367-f53b-4606-8887-80a381e0ef69",
                "sampletester@testco.com", "Sample", "Tester", "en", address, "en_US");
        Account account = new Account(accountId, Account.Status.ACTIVE);
        Item item = new Item(0, "GIGABYTE");
        User user = new User("c734676b-40f6-4783-b4ee-e20d59bbf943","https://www.acme.com/openid/id/c734676b-40f6-4783-b4ee-e20d59bbf943",
                "sampletester2@testco.com", "Another2", "Tester", "en", null, "en_US");
        Payload payload = new Payload(null, null, account, user);
        Notification notification = new Notification(Notification.Type.USER_ASSIGNMENT, marketPlace, creator, payload);
        return notification;
    }

    @RequestMapping(value = "/userUnassignment", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Notification unassignUserEvent(@RequestParam("accountId") String accountId){
        MarketPlace marketPlace = new MarketPlace("https://www.acme.com", "APPDIRECT");
        Address address = new Address("Test", "Test User", "User", "San Jose", "US", "CA", "1 Main St", "95131");
        User creator = new User("211aa369-f53b-4606-8887-80a361e0ef66","https://www.acme.com/openid/id/211aa367-f53b-4606-8887-80a381e0ef69",
                "sampletester@testco.com", "Sample", "Tester", "en", address, "en_US");
        Account account = new Account(accountId, Account.Status.ACTIVE);
        Item item = new Item(0, "GIGABYTE");
        User user = new User("c734676b-40f6-4783-b4ee-e20d59bbf943","https://www.acme.com/openid/id/c734676b-40f6-4783-b4ee-e20d59bbf943",
                "sampletester2@testco.com", "Another2", "Tester", "en", null, "en_US");
        Payload payload = new Payload(null, null, account, user);
        Notification notification = new Notification(Notification.Type.USER_UNASSIGNMENT, marketPlace, creator, payload);
        return notification;
    }

}
