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
@RequestMapping("/apl")
public class APIController {

    @Value("${ageUserHost:http://ocpageservice1-oc-test.apps-crc.testing}")
    private String ageUserHost;

    @Value("${ageBalanceHost:http://ocpageservice1-oc-test.apps-crc.testing}")
    private String ageBalanceHost;

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/createUser")
    @Transactional
    public ResponseEntity<UserInfo> createUser(@RequestBody UserInfo userInfo) {
        User user = new User();
        user.setName(userInfo.getName());
        user.setEmail(userInfo.getEmail());

        System.out.println(ageUserHost + "/age/users");

        ResponseEntity<User> userResponse = restTemplate.postForEntity(ageUserHost + "/age/users", user, User.class);

        Balance balance = new Balance();
        balance.setUserId(Objects.requireNonNull(userResponse.getBody()).getId());
        balance.setAmount(userInfo.getAmount());

        userInfo.setId(userResponse.getBody().getId());
        ResponseEntity<Balance> balanceResponse = restTemplate.postForEntity(
                ageBalanceHost + "/age/balances/" + userResponse.getBody().getId() + "/update", balance, Balance.class);

        return new ResponseEntity<>(userInfo, HttpStatus.ACCEPTED);
    }

    @GetMapping("/fetchUser")
    public ResponseEntity<UserInfo> fetchUser(@RequestParam Long userId) {

        ResponseEntity<User> userResponse = restTemplate.getForEntity(ageUserHost + "/age/users/" + userId, User.class);

        ResponseEntity<Balance> balanceResponse = restTemplate.getForEntity(ageBalanceHost + "/age/balances/" + userId, Balance.class);

        UserInfo userInfo = new UserInfo();
        userInfo.setId(userId);
        userInfo.setName(userResponse.getBody().getName());
        userInfo.setEmail(userResponse.getBody().getEmail());
        userInfo.setAmount(balanceResponse.getBody().getAmount());

        return new ResponseEntity<>(userInfo, HttpStatus.OK);
    }
}
