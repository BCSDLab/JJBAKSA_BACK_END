package com.jjbacsa.jjbacsabackend.user.serviceImpl;

import com.jjbacsa.jjbacsabackend.etc.enums.ErrorMessage;
import com.jjbacsa.jjbacsabackend.etc.exception.RequestInputException;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import com.jjbacsa.jjbacsabackend.user.repository.UserRepository;
import com.jjbacsa.jjbacsabackend.user.service.InternalUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InternalUserServiceImpl implements InternalUserService {

    private final UserRepository userRepository;

    @Override
    public UserEntity getUserById(Long userId) {

        return userRepository.findById(userId)
                .orElseThrow(() -> new RequestInputException(ErrorMessage.USER_NOT_EXISTS_EXCEPTION));
    }

    @Override
    @Transactional
    public void increaseReviewCount(Long userId) {

        UserEntity user = getUserById(userId);
        user.getUserCount().setReviewCount(userRepository.getReviewCount(userId) + 1);
    }

    @Override
    @Transactional
    public void increaseScrapCount(Long userId) {

        UserEntity user = getUserById(userId);
        user.getUserCount().setScrapCount(userRepository.getScrapCount(userId) + 1);
    }

    @Override
    @Transactional
    public void increaseFriendCount(Long userId) {

        UserEntity user = getUserById(userId);
        user.getUserCount().setFriendCount(userRepository.getFriendCount(userId) + 1);
    }

    @Override
    @Transactional
    public void decreaseReviewCount(Long userId) {

        UserEntity user = getUserById(userId);
        user.getUserCount().setReviewCount(userRepository.getReviewCount(userId) - 1);
    }

    @Override
    @Transactional
    public void decreaseScrapCount(Long userId) {

        UserEntity user = getUserById(userId);
        user.getUserCount().setScrapCount(userRepository.getScrapCount(userId) - 1);

    }

    @Override
    @Transactional
    public void decreaseFriendCount(Long userId) {

        UserEntity user = getUserById(userId);
        user.getUserCount().setFriendCount(userRepository.getFriendCount(userId) - 1);
    }
}
