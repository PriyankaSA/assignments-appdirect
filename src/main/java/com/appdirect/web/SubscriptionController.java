package com.appdirect.web;

import com.appdirect.mongo.documents.SubscriptionRecord;
import com.appdirect.service.SubscriptionService;
import com.appdirect.web.commons.Response;
import com.appdirect.web.commons.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/subscription")
public class SubscriptionController {

    @Resource
    private SubscriptionService subscriptionService;

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public ResponseEntity<Response> create(@RequestParam("url") String url) {
        Response response = subscriptionService.createSubscription(url);
        if(response instanceof SuccessResponse){
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/change", method = RequestMethod.GET)
    public ResponseEntity<Response> change(@RequestParam("url") String url) {
        Response response = subscriptionService.changeSubscription(url);
        if(response instanceof SuccessResponse){
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/cancel", method = RequestMethod.GET)
    public ResponseEntity<Response> cancel(@RequestParam("url") String url) {
        Response response = subscriptionService.cancelSubscription(url);
        if(response instanceof SuccessResponse){
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<SubscriptionRecord> getAll(){
        return subscriptionService.getSubscriptions();
    }
}
