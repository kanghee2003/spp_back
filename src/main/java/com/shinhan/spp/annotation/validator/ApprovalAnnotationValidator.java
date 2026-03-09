package com.shinhan.spp.annotation.validator;

import com.shinhan.spp.annotation.ApprovalAfterProcess;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Component
public class ApprovalAnnotationValidator implements ApplicationListener<ContextRefreshedEvent> {

    private final ApplicationContext applicationContext;

    public ApprovalAnnotationValidator(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(Component.class);

        Map<String, String> valueToMethod = new HashMap<>();

        for (Object bean : beans.values()) {
            Class<?> targetClass = AopUtils.getTargetClass(bean);

            Map<Method, ApprovalAfterProcess> annotatedMethods =
                MethodIntrospector.selectMethods(
                    targetClass,
                    (MethodIntrospector.MetadataLookup<ApprovalAfterProcess>) method ->
                        AnnotatedElementUtils.findMergedAnnotation(method, ApprovalAfterProcess.class)
                );

            for (Map.Entry<Method, ApprovalAfterProcess> entry : annotatedMethods.entrySet()) {
                Method method = entry.getKey();
                ApprovalAfterProcess annotation = entry.getValue();
                String value = annotation.value();

                String currentMethodInfo = targetClass.getName() + "#" + method.getName();

                if (valueToMethod.containsKey(value)) {
                    throw new IllegalStateException(
                        "중복된 @ApprovalAfterProcess value: " + value +
                        " / " +  valueToMethod.get(value) +
                        " / " + currentMethodInfo
                    );
                }

                valueToMethod.put(value, currentMethodInfo);
            }
        }
    }
}