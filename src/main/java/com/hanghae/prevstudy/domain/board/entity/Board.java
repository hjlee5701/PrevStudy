package com.hanghae.prevstudy.domain.board.entity;

import com.hanghae.prevstudy.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@NoArgsConstructor
@Getter
@AllArgsConstructor
@Builder
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member writer;
    private String content;
    private String password;

    @CreationTimestamp
    private Date regAt;

    @UpdateTimestamp
    private Date modAt;

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
