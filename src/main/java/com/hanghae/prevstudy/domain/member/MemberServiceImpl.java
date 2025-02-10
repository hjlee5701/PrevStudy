package com.hanghae.prevstudy.domain.member;

import com.hanghae.prevstudy.global.exception.PrevStudyException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {

    @Override
    @Transactional
    public void signup(MemberAddRequest memberAddRequest) {
        throw new PrevStudyException(MemberErrorCode.DUPLICATE_USERNAME);
    }

}
