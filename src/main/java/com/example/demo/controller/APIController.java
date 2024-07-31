package com.example.demo.controller;

import com.example.demo.controller.model.UserInfo;
import com.example.demo.entity.Balance;
import com.example.demo.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

@RestController
@RequestMapping("/spl")
public class APIController {

    @Value("${dgeHost:http://localhost:8080/}")
    private String dgeHost;

    private String USER_SERVICE_URL = dgeHost + "/dge/users/";
    private String BALANCE_SERVICE_URL = dgeHost + "/dge/balances/";

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/createUser")
    @Transactional
    public ResponseEntity<UserInfo> createUser(@RequestBody UserInfo userInfo) {
        User user = new User();
        user.setName(userInfo.getName());
        user.setEmail(userInfo.getEmail());

        ResponseEntity<User> userResponse = restTemplate.postForEntity(USER_SERVICE_URL, user, User.class);

        Balance balance = new Balance();
        balance.setUserId(Objects.requireNonNull(userResponse.getBody()).getId());
        balance.setAmount(userInfo.getAmount());
        ResponseEntity<Balance> balanceResponse = restTemplate.postForEntity(
                BALANCE_SERVICE_URL + userInfo.getAmount() + "/update", balance, Balance.class);

        return new ResponseEntity<>(userInfo, HttpStatus.ACCEPTED);
    }

    @GetMapping("/fetchUser")
    public ResponseEntity<UserInfo> fetchUser(@RequestParam Long userId) {

        ResponseEntity<User> userResponse = restTemplate.getForEntity(USER_SERVICE_URL + userId, User.class);

        ResponseEntity<Balance> balanceResponse = restTemplate.getForEntity(BALANCE_SERVICE_URL + userId, Balance.class);

        UserInfo userInfo = new UserInfo();
        userInfo.setId(userId);
        userInfo.setName(userResponse.getBody().getName());
        userInfo.setEmail(userResponse.getBody().getEmail());
        userInfo.setAmount(balanceResponse.getBody().getAmount());

        return new ResponseEntity<>(userInfo, HttpStatus.OK);
    }
}
