package com.faang.postservice.dto.cache;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;

@RedisHash(value="UserCache", timeToLive = 86400)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCache implements Serializable {

    @Id
    private Long id;
    private String username;
    private String email;
    @Builder.Default
    private Long lastPostId = 0L;
}
