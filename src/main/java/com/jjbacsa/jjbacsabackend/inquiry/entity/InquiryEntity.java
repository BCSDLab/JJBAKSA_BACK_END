package com.jjbacsa.jjbacsabackend.inquiry.entity;

import com.jjbacsa.jjbacsabackend.etc.entity.BaseEntity;
import com.jjbacsa.jjbacsabackend.inquiry.dto.request.AnswerRequest;
import com.jjbacsa.jjbacsabackend.inquiry.dto.request.InquiryRequest;
import com.jjbacsa.jjbacsabackend.inquiry_image.entity.InquiryImageEntity;
import com.jjbacsa.jjbacsabackend.review.entity.ReviewEntity;
import com.jjbacsa.jjbacsabackend.review_image.entity.ReviewImageEntity;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Entity
@SQLDelete(sql = "UPDATE inquiry SET is_deleted = 1 WHERE id=?")
@Where(clause = "is_deleted = 0")
@Table(name = "inquiry")
public class InquiryEntity extends BaseEntity {

    private static class InquiryEntityBuilderImpl extends InquiryEntity.InquiryEntityBuilder<InquiryEntity, InquiryEntity.InquiryEntityBuilderImpl> {

        @Override
        public InquiryEntity build() {
            InquiryEntity inquiryEntity = new InquiryEntity(this);
            if (inquiryEntity.getInquiryImages() != null) {
                for (InquiryImageEntity image : inquiryEntity.getInquiryImages()) {
                    image.setInquiry(inquiryEntity);
                }
            }
            return inquiryEntity;
        }

    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private UserEntity writer;

    @Basic(optional = false)
    @Column(name = "title", nullable = false)
    private String title;

    @Basic(optional = false)
    @Column(name = "content", nullable = false)
    private String content;

    @Basic
    @Column(name = "answer")
    private String answer;

    @Basic(optional = false)
    @Column(name = "is_secreted")
    @Builder.Default
    private int isSecreted = 0;

    @OrderBy("id asc")
    @OneToMany(mappedBy = "inquiry", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<InquiryImageEntity> inquiryImages = new ArrayList<>();

    // 연관관계 메서드
    public void addInquiryImageEntity(InquiryImageEntity inquiryImage) {
        if (inquiryImage != null) {
            this.inquiryImages.add(inquiryImage);
            inquiryImage.setInquiry(this);
        }
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void update(InquiryRequest inquiryRequest) {
        this.content = inquiryRequest.getContent();
        this.title = inquiryRequest.getTitle();
        this.isSecreted = inquiryRequest.getIsSecret() ? 1 : 0;
    }
}
