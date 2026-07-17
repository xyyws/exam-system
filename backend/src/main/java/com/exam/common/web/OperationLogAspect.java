package com.exam.common.web;

import com.exam.common.security.SecurityUtils;
import com.exam.system.entity.OperationLog;
import com.exam.system.mapper.OperationLogMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Aspect
@Component
public class OperationLogAspect {

    private static final Logger log = LoggerFactory.getLogger(OperationLogAspect.class);

    private final OperationLogMapper logMapper;

    public OperationLogAspect(OperationLogMapper logMapper) {
        this.logMapper = logMapper;
    }

    @Around("execution(* com.exam.system.controller..*(..)) || execution(* com.exam.auth.controller.AuthController.changePassword(..))")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        OperationLog opLog = new OperationLog();

        try {
            // Extract user info
            try {
                opLog.setUserId(SecurityUtils.getCurrentUserId());
                var loginUser = SecurityUtils.getLoginUser();
                opLog.setUsername(loginUser.getUsername());
                opLog.setRealName(loginUser.getRealName());
            } catch (Exception ignored) {}

            // Extract request info
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest req = attrs.getRequest();
                opLog.setMethod(req.getMethod());
                opLog.setUri(req.getRequestURI());
                opLog.setIp(getClientIp(req));
            }

            opLog.setOperation(resolveOperation(joinPoint));
            Object result = joinPoint.proceed();
            opLog.setStatus(1);
            return result;
        } catch (Throwable ex) {
            opLog.setStatus(0);
            opLog.setErrorMsg(ex.getMessage() != null ? ex.getMessage().substring(0, Math.min(ex.getMessage().length(), 200)) : null);
            throw ex;
        } finally {
            opLog.setCostTime(System.currentTimeMillis() - start);
            opLog.setCreateTime(LocalDateTime.now());
            try {
                logMapper.insert(opLog);
            } catch (Exception e) {
                log.warn("操作日志写入失败", e);
            }
        }
    }

    private String resolveOperation(ProceedingJoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String method = joinPoint.getSignature().getName();
        return className + "." + method;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip != null && ip.contains(",") ? ip.split(",")[0].trim() : ip;
    }
}
