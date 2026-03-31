package com.retrip.trip.infra.adapter.in.presentation.resolver;

import com.retrip.trip.application.in.request.context.UserContext;
import com.retrip.trip.application.in.request.context.WithUserContext;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class UserContextArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(WithUserContext.class);
    }

    @Override
    public UserContext resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                       NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        UserContext userContext = (UserContext) webRequest.getAttribute(
                "userContext",
                RequestAttributes.SCOPE_REQUEST
        );

//        UserContext userContext = UserContext.mockOf();

        if (userContext == null) {
            throw new IllegalStateException("UserContext not found in request");
        }
        return userContext;
    }
}
