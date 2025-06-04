package com.faang.postservice.client;

import com.faang.postservice.dto.user.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "user-service", url = "${user-service.host}:${user-service.port}")
public interface UserServiceClient {

    @GetMapping("/users/{userId}")
    UserDto getUser(@PathVariable long userId);

    @GetMapping("/users/authors/{userId}")
    List<Long> getAuthorIdsSubscribedByUser(@PathVariable long userId);

    @GetMapping("/users/followers/{userId}")
    List<Long> getFollowerIdsOfUser(@PathVariable long userId);
}
