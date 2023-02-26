package com.jjbacsa.jjbacsabackend.inquiry.entity;

import com.jjbacsa.jjbacsabackend.etc.entity.BaseEntity;
import com.jjbacsa.jjbacsabackend.inquiry.dto.request.AnswerRequest;
import com.jjbacsa.jjbacsabackend.inquiry.dto.request.InquiryRequest;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Entity
@SQLDelete(sql = "UPDATE inquiry SET is_deleted = 1 WHERE id=?")
@Where(clause = "is_deleted = 0")
@Table(name = "inquiry")
public class InquiryEntity extends BaseEntity {

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
    @Column(name = "secret")
    private String secret;

    @Basic
    @Column(name = "answer")
    private String answer;

    @Basic(optional = false)
    @Builder.Default
    @Column(name = "is_secreted")
    private int isSecreted = 0;

    public void setSecret(String secret){
        this.secret = secret;
        this.isSecreted = 1;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void update(InquiryRequest inquiryRequest) {
        if(inquiryRequest.getContent() != null) this.content = inquiryRequest.getContent();
        if(inquiryRequest.getTitle() != null) this.title = inquiryRequest.getTitle();
    }
}
