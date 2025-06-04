package com.faang.postservice.dto.hashtag;

import com.faang.postservice.validation.hashtag.ValidHashtag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HashtagDto {

    @ValidHashtag
    private String name;
}
