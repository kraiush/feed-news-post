package com.faang.postservice.dto.post.corrector;

import com.faang.postservice.dto.post.corrector.FlaggedToken;
import lombok.Data;

import java.util.List;

@Data
public class PostCorrectorDto {

    public String _type;
    public List<FlaggedToken> flaggedTokens;
    public String correctionType;
}
