package com.jjbacsa.jjbacsabackend.inquiry.serviceImpl;

import com.jjbacsa.jjbacsabackend.inquiry.entity.InquiryEntity;
import com.jjbacsa.jjbacsabackend.inquiry.repository.InquiryRepository;
import com.jjbacsa.jjbacsabackend.inquiry.service.InternalInquiryService;
import com.jjbacsa.jjbacsabackend.inquiry_image.entity.InquiryImageEntity;
import com.jjbacsa.jjbacsabackend.inquiry_image.service.InternalInquiryImageService;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import com.jjbacsa.jjbacsabackend.user.service.InternalUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class InternalInquiryServiceImpl implements InternalInquiryService {

    private final InquiryRepository inquiryRepository;
    private final InternalInquiryImageService inquiryImageService;
    private final InternalUserService userService;

    @Override
    public void deleteInquiriesWithUser(UserEntity user) throws Exception {
        UserEntity userEntity = userService.getLoginUser();
        List<InquiryEntity> inquiries = inquiryRepository.findAllByWriter(userEntity);

        for (InquiryEntity inquiry : inquiries) {

            for (InquiryImageEntity inquiryImage : inquiry.getInquiryImages()) {
                inquiryImageService.delete(inquiryImage);
            }
            inquiryRepository.delete(inquiry);
        }
    }
}
