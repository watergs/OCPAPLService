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

    @Value("${dgeUserHost:http://ocpdgeservice1-oc-test.apps-crc.testing}")
    private String dgeUserHost;

    @Value("${dgeBalanceHost:http://ocpdgeservice1-oc-test.apps-crc.testing}")
    private String dgeBalanceHost;

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/createUser")
    @Transactional
    public ResponseEntity<UserInfo> createUser(@RequestBody UserInfo userInfo) {
        User user = new User();
        user.setName(userInfo.getName());
        user.setEmail(userInfo.getEmail());

        System.out.println(dgeUserHost + "/dge/users");

        ResponseEntity<User> userResponse = restTemplate.postForEntity(dgeUserHost + "/dge/users", user, User.class);

        Balance balance = new Balance();
        balance.setUserId(Objects.requireNonNull(userResponse.getBody()).getId());
        balance.setAmount(userInfo.getAmount());

        userInfo.setId(userResponse.getBody().getId());
        ResponseEntity<Balance> balanceResponse = restTemplate.postForEntity(
                dgeBalanceHost + "/dge/balances/" + userResponse.getBody().getId() + "/update", balance, Balance.class);

        return new ResponseEntity<>(userInfo, HttpStatus.ACCEPTED);
    }

    @GetMapping("/fetchUser")
    public ResponseEntity<UserInfo> fetchUser(@RequestParam Long userId) {

        ResponseEntity<User> userResponse = restTemplate.getForEntity(dgeUserHost + "/dge/users/" + userId, User.class);

        ResponseEntity<Balance> balanceResponse = restTemplate.getForEntity(dgeBalanceHost + "/dge/balances/" + userId, Balance.class);

        UserInfo userInfo = new UserInfo();
        userInfo.setId(userId);
        userInfo.setName(userResponse.getBody().getName());
        userInfo.setEmail(userResponse.getBody().getEmail());
        userInfo.setAmount(balanceResponse.getBody().getAmount());

        return new ResponseEntity<>(userInfo, HttpStatus.OK);
    }
}
