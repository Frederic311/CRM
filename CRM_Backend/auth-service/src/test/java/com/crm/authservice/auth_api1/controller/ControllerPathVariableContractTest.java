package com.crm.authservice.auth_api1.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Every @PathVariable of a handler has to be bound by the URL template that
 * declares it.
 *
 * AuthenticationController::deleteProfilePhoto was mapped to DELETE auth/delete
 * while still taking a @PathVariable Long userId. The template exposed no
 * segment to bind, so the identifier could never be resolved and the endpoint
 * could not work.
 *
 * Nothing catches this on its own: the application starts normally, because
 * Spring only fails when it cannot resolve the name of a @PathVariable, not
 * when the template it resolved it against has no matching placeholder.
 *
 * Checking the single occurrence would only protect that one line, so this
 * walks every registered handler instead. The same mistake made later on
 * another endpoint fails here too.
 */
@SpringBootTest
@ActiveProfiles("test")
class ControllerPathVariableContractTest {

    @Autowired
    private RequestMappingHandlerMapping handlerMapping;

    @Test
    @DisplayName("every @PathVariable is bound by the URL template of its handler")
    void everyPathVariableIsPresentInTheUrlTemplate() {
        List<String> unbound = new ArrayList<>();

        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMapping.getHandlerMethods().entrySet()) {
            HandlerMethod handler = entry.getValue();
            Set<String> templates = templatesOf(entry.getKey());
            if (templates.isEmpty()) {
                continue;
            }

            for (Parameter parameter : handler.getMethod().getParameters()) {
                PathVariable annotation = parameter.getAnnotation(PathVariable.class);
                if (annotation == null) {
                    continue;
                }

                // @PathVariable("x") sets the alias "value", and a raw JDK
                // annotation proxy does not resolve @AliasFor the way Spring
                // does, so both accessors have to be considered before falling
                // back to the parameter name.
                String name = firstNonBlank(annotation.name(), annotation.value(), parameter.getName());

                if (name == null || name.matches("arg\\d+")) {
                    unbound.add(handler.getMethod().getName()
                            + ": @PathVariable name is not recoverable, declare it as name=\"...\"");
                    continue;
                }

                String placeholder = "{" + name + "}";
                if (templates.stream().noneMatch(t -> t.contains(placeholder))) {
                    unbound.add(handler.getMethod().getName()
                            + ": @PathVariable " + name + " has no " + placeholder + " in " + templates);
                }
            }
        }

        assertThat(unbound).isEmpty();
    }

    private String firstNonBlank(String... candidates) {
        for (String candidate : candidates) {
            if (candidate != null && !candidate.isEmpty()) {
                return candidate;
            }
        }
        return null;
    }

    private Set<String> templatesOf(RequestMappingInfo info) {
        if (info.getPathPatternsCondition() != null) {
            return info.getPathPatternsCondition().getPatternValues();
        }
        return info.getPatternsCondition().getPatterns();
    }
}
