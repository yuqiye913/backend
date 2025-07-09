package com.programming.techie.springredditclone.mapper;

import com.programming.techie.springredditclone.dto.GetIntroDto;
import com.programming.techie.springredditclone.model.User;
import com.programming.techie.springredditclone.model.UserIntro;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "userId", source = "user.userId")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "bio", source = "userIntro.bio")
    GetIntroDto mapToGetIntroDto(User user, UserIntro userIntro);

    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "username", source = "username")
    @Mapping(target = "bio", constant = "null")
    GetIntroDto mapToGetIntroDto(User user);
} 