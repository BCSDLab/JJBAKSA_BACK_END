package com.jjbacsa.jjbacsabackend.inquiry.serviceImpl;

import com.jjbacsa.jjbacsabackend.etc.enums.ErrorMessage;
import com.jjbacsa.jjbacsabackend.etc.enums.UserType;
import com.jjbacsa.jjbacsabackend.etc.exception.RequestInputException;
import com.jjbacsa.jjbacsabackend.inquiry.dto.request.AnswerRequest;
import com.jjbacsa.jjbacsabackend.inquiry.dto.request.InquiryRequest;
import com.jjbacsa.jjbacsabackend.inquiry.dto.response.InquiryResponse;
import com.jjbacsa.jjbacsabackend.inquiry.entity.InquiryEntity;
import com.jjbacsa.jjbacsabackend.inquiry.mapper.InquiryMapper;
import com.jjbacsa.jjbacsabackend.inquiry.repository.InquiryRepository;
import com.jjbacsa.jjbacsabackend.inquiry.service.InquiryService;
import com.jjbacsa.jjbacsabackend.inquiry_image.entity.InquiryImageEntity;
import com.jjbacsa.jjbacsabackend.inquiry_image.service.InternalInquiryImageService;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import com.jjbacsa.jjbacsabackend.user.service.InternalUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class InquiryServiceImpl implements InquiryService {
    private final InternalUserService userService;
    private final InternalInquiryImageService inquiryImageService;

    private final InquiryRepository inquiryRepository;

    @Override
    @Transactional(readOnly = true)
    public InquiryResponse getInquiry(Long inquiryId) throws Exception {
        InquiryEntity inquiry = getInquiryEntity(inquiryId);
        InquiryResponse response = InquiryMapper.INSTANCE.toInquiryResponse(inquiry);
        if (inquiry.getIsSecreted() == 1 && !checkUser(inquiry)) {
            throw new RequestInputException(ErrorMessage.INVALID_PERMISSION_INQUIRY);
        }
        return response;
    }

    @Override
    public InquiryResponse create(InquiryRequest inquiryRequest) throws Exception {
        InquiryEntity inquiry = inquiryRepository.save(createInquiryEntity(inquiryRequest));
        return InquiryMapper.INSTANCE.toInquiryResponse(inquiry);
    }

    @Override
    public InquiryResponse modify(InquiryRequest inquiryRequest, Long inquiryId) throws Exception {
        UserEntity userEntity = userService.getLoginUser();
        InquiryEntity inquiry = getInquiryEntity(inquiryId);
        if (inquiry.getWriter().equals(userEntity)) {
            inquiry.update(inquiryRequest);
            if (inquiryRequest.getInquiryImages() == null) {
                for (int i = 0; i < inquiry.getInquiryImages().size(); i++) {
                    inquiryImageService.delete(inquiry.getInquiryImages().get(i));
                    inquiry.getInquiryImages().remove(i);
                }
            } else inquiryImageService.modify(inquiryRequest.getInquiryImages(), inquiry);
        } else throw new RequestInputException(ErrorMessage.INVALID_PERMISSION_INQUIRY);
        return InquiryMapper.INSTANCE.toInquiryResponse(inquiry);
    }

    @Override
    public void delete(Long inquiryId) throws Exception {
        UserEntity userEntity = userService.getLoginUser();
        InquiryEntity inquiry = getInquiryEntity(inquiryId);
        if (inquiry.getWriter().equals(userEntity)) inquiryRepository.deleteById(inquiryId);
        else throw new RequestInputException(ErrorMessage.INVALID_PERMISSION_INQUIRY);
    }

    @Override
    public InquiryResponse addAnswer(AnswerRequest answerRequest, Long inquiryId) {
        InquiryEntity inquiry = getInquiryEntity(inquiryId);
        inquiry.setAnswer(answerRequest.getAnswer());
        return InquiryMapper.INSTANCE.toInquiryResponse(inquiry);
    }

    private InquiryEntity getInquiryEntity(Long inquiryId) {
        return inquiryRepository.findById(inquiryId).orElseThrow(
                () -> new RequestInputException(ErrorMessage.INQUIRY_NOT_EXISTS_EXCEPTION));
    }

    private InquiryEntity createInquiryEntity(InquiryRequest inquiryRequest) throws Exception {
        UserEntity userEntity = userService.getLoginUser();
        InquiryEntity inquiry = InquiryEntity.builder()
                .createdBy(inquiryRequest.getCreatedBy() == null ? userEntity.getNickname() : inquiryRequest.getCreatedBy())
                .writer(userEntity)
                .title(inquiryRequest.getTitle())
                .content(inquiryRequest.getContent())
                .isSecreted(inquiryRequest.getIsSecret() ? 1 : 0)
                .build();
        if (inquiryRequest.getInquiryImages() != null) {
            for (InquiryImageEntity image : inquiryImageService.create(inquiryRequest.getInquiryImages())) {
                inquiry.addInquiryImageEntity(image);
            }
        }
        return inquiry;
    }

    private boolean checkUser(InquiryEntity inquiryEntity) throws Exception {
        UserEntity userEntity = userService.getLoginUser();
        return userEntity.getUserType().equals(UserType.ROOT) ||
                userEntity.getUserType().equals(UserType.ADMIN) ||
                inquiryEntity.getWriter().equals(userEntity) ? true : false;
    }
}
