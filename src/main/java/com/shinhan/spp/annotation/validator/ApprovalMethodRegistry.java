package com.shinhan.spp.annotation.validator;

import com.shinhan.spp.annotation.ApprovalAfterProcess;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Component
public class ApprovalMethodRegistry implements ApplicationContextAware, SmartInitializingSingleton {

    private ApplicationContext context;

    private final Map<String, RegisteredMethod> registry = new HashMap<>();


    @Override
    public void setApplicationContext( ApplicationContext applicationContext) {
        this.context = applicationContext;
    }

@Override
public void afterSingletonsInstantiated() {
    Map<String, Object> beans = context.getBeansWithAnnotation(Component.class);

    for (Object bean : beans.values()) {
        if (bean == this) {
            continue;
        }

        Class<?> targetClass = AopUtils.getTargetClass(bean);

        Map<Method, ApprovalAfterProcess> annotatedMethods =
            MethodIntrospector.selectMethods(
                targetClass,
                (MethodIntrospector.MetadataLookup<ApprovalAfterProcess>) method -> {
                    ApprovalAfterProcess ann =
                        AnnotatedElementUtils.findMergedAnnotation(method, ApprovalAfterProcess.class);

                    if (ann != null && method.getParameterCount() == 1) {
                        return ann;
                    }
                    return null;
                }
            );

        for (Map.Entry<Method, ApprovalAfterProcess> entry : annotatedMethods.entrySet()) {
            Method method = entry.getKey();
            ApprovalAfterProcess ann = entry.getValue();

            method.setAccessible(true);
            registry.put(ann.value(), new RegisteredMethod(bean, method));
        }
    }
}

    public Object run(String key, Object vo) {
        RegisteredMethod regMethod = registry.get(key);
        if (regMethod == null) throw new IllegalArgumentException("No method for key: " + key);
        try {
            return regMethod.invoke(vo);
        } catch (Exception e) {
            throw new RuntimeException("Invocation failed", e);
        }
    }

    public Class<?> getVoType(String key) {
        RegisteredMethod regMethod = registry.get(key);
        if (regMethod == null) return null;
        return regMethod.getVoType();
    }

    private record RegisteredMethod(Object bean, Method method) {

        Object invoke(Object vo) throws Exception {
                return method.invoke(bean, vo);
            }

            Class<?> getVoType() {
                return method.getParameterTypes()[0];
            }
        }
}
