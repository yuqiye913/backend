package com.programming.techie.springredditclone.mapper;

import com.programming.techie.springredditclone.dto.GetFollowersDto;
import com.programming.techie.springredditclone.dto.GetFollowingDto;
import com.programming.techie.springredditclone.dto.FollowerCountDto;
import com.programming.techie.springredditclone.dto.FollowingCountDto;
import com.programming.techie.springredditclone.model.Follow;
import com.programming.techie.springredditclone.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FollowMapper {

    @Mapping(target = "userId", source = "follower.userId")
    @Mapping(target = "username", source = "follower.username")
    @Mapping(target = "email", source = "follower.email")
    @Mapping(target = "created", source = "follower.created")
    @Mapping(target = "enabled", source = "follower.enabled")
    @Mapping(target = "followedAt", source = "followedAt")
    @Mapping(target = "active", source = "active")
    @Mapping(target = "muted", source = "muted")
    @Mapping(target = "closeFriend", source = "closeFriend")
    GetFollowersDto mapToGetFollowersDto(Follow follow);

    @Mapping(target = "userId", source = "following.userId")
    @Mapping(target = "username", source = "following.username")
    @Mapping(target = "email", source = "following.email")
    @Mapping(target = "created", source = "following.created")
    @Mapping(target = "enabled", source = "following.enabled")
    @Mapping(target = "followedAt", source = "followedAt")
    @Mapping(target = "active", source = "active")
    @Mapping(target = "muted", source = "muted")
    @Mapping(target = "closeFriend", source = "closeFriend")
    GetFollowingDto mapToGetFollowingDto(Follow follow);

    @Mapping(target = "userId", source = "user.userId")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "followerCount", source = "followerCount")
    FollowerCountDto mapToFollowerCountDto(User user, Long followerCount);

    @Mapping(target = "userId", source = "user.userId")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "followingCount", source = "followingCount")
    FollowingCountDto mapToFollowingCountDto(User user, Long followingCount);
} 