package com.appdirect.web;

import com.appdirect.mongo.documents.SubscriptionRecord;
import com.appdirect.mongo.documents.UserRecord;
import com.appdirect.service.SubscriptionService;
import com.appdirect.service.UserService;
import com.appdirect.web.commons.Response;
import com.appdirect.web.commons.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.websocket.server.PathParam;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private UserService userService;

    @RequestMapping(value = "/assign", method = RequestMethod.GET)
    public ResponseEntity<Response> assign(@RequestParam("url") String url) {
        Response response = userService.assignUser(url);
        if(response instanceof SuccessResponse){
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/unassign", method = RequestMethod.GET)
    public ResponseEntity<Response> unassign(@RequestParam("url") String url) {
        Response response = userService.unassignUser(url);
        if(response instanceof SuccessResponse){
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value="/{accountId}", method = RequestMethod.GET)
    public List<UserRecord> getUsersBySubscription(@PathVariable("accountId") String id){
        return userService.findUsers(id);
    }
}
