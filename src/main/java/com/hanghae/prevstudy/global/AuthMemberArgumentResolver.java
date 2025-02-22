package com.hanghae.prevstudy.global;

import com.hanghae.prevstudy.domain.member.exception.MemberErrorCode;
import com.hanghae.prevstudy.domain.security.UserDetailsImpl;
import com.hanghae.prevstudy.global.exception.PrevStudyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Slf4j
@Component
public class AuthMemberArgumentResolver implements HandlerMethodArgumentResolver {

    /**
     * @param parameter the method parameter to check
     * @return
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthMemberInfo.class);
    }

    /**
     *
     * @param parameter the method parameter to resolve. This parameter must
     * have previously been passed to {@link #supportsParameter} which must
     * have returned {@code true}.
     * @param mavContainer the ModelAndViewContainer for the current request
     * @param webRequest the current request
     * @param binderFactory a factory for creating {@link WebDataBinder} instances
     * @return
     * @throws Exception
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        // 클래스 타입 에러
        if (!parameter.getParameterType().equals(UserDetailsImpl.class)) {
            log.error("클래스 타입 에러입니다.");
            throw new PrevStudyException(MemberErrorCode.INVALID_AUTH_MEMBER_TYPE);
        }
        return null;
    }
}
