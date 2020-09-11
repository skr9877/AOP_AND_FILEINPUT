package com.survivalking.aop;

import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j;

@Aspect
@Component
@Log4j
public class LogAdvice {
	
	@Before("execution(* com.survivalking.service.SampleService*.*(..))")
	public void logBefore() {
		log.info("==========");
	}
	
	@Before("execution(* com.survivalking.service.SampleService*.doAdd(String,String)) && args(a,b)")
	public void logBeforeWithParam(String a, String b) {
		log.info("str1 : " + a);
		log.info("str2 : " + b);
	}
	
	@AfterThrowing(pointcut = "execution(* com.survivalking.service.SampleService*.*(..))", throwing = "e")
	public void logException(Exception e) {
		log.info("Exception......");
		log.info("exception : " + e);
	}
	
	@Around("execution(* com.survivalking.service.SampleService*.*(..))")
	public Object logTime(ProceedingJoinPoint pjp) {
		long start = System.currentTimeMillis();
		
		log.info("Target : " + pjp.getTarget());
		log.info("Param : " + Arrays.deepToString(pjp.getArgs()));
		
		Object result = null;
		
		try {
			result = pjp.proceed();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		
		long end = System.currentTimeMillis();
		
		log.info("TIME : " + (end - start));
		
		return result;
	}
}
