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
        return parameter.hasParameterAnnotation(WithUserContext.class)
                && parameter.getParameterType().equals(UserContext.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        UserContext userContext = (UserContext) webRequest.getAttribute(
                "userContext",
                RequestAttributes.SCOPE_REQUEST
        );

        WithUserContext annotation = parameter.getParameterAnnotation(WithUserContext.class);
        boolean required = (annotation != null) && annotation.required();

        if (required && userContext == null) {
            throw new IllegalStateException("Login required");
        }

        return userContext;
    }
}