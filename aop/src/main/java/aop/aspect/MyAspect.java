package aop.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

// @Aspect는 scanning 되지 않는다. 스캔해서 빈생성을 해주지는 않는다 그래서 @Component 어노테이션을 달아줘야 생성된다
@Component
@Aspect
public class MyAspect {
	
	@Before("execution(public aop.domain.Product aop.service.ProductService.find(String) throws RuntimeException)") // 리턴타입, 메서드명
	public void adviceBefore() {
		System.out.println("-- Before Advice --");
	}
	
	@After("execution(public aop.domain.Product aop.service.ProductService.find(String))") 
	public void adviceAfter() {
		System.out.println("-- After Advice --");
	}
	
	//리턴 값을 생략할 수는 없고, 와일드카드를 사용할 수 있다.
	@AfterReturning("execution(* aop.service.ProductService.find(..))") 
	public void adviceAfterReturning() {
		System.out.println("-- AfterReturning Advice --");
	}
	
	// 패키지를 줄일 수 있다. 패키지 개수 상관 없이 이렇게 쓴다 *..*
	@AfterThrowing(value="execution(* *..*.ProductService.find(..))", throwing="ex") 
	public void adviceAfterThrowing(Throwable ex) {
		System.out.println("-- AfterThrowing[" + ex.getMessage() + "]Advice --"); // 메시지 - empty name
	}
	
	@Around("execution(* *..*.*.*(..))") 
	public Object adviceAround(ProceedingJoinPoint pjp) throws Throwable {
		System.out.println("-- Around Advice : Before --");
		
		//proceed가 실행되면 find가 호출되는 것이고 pjp가 find가 아님
		Object result = pjp.proceed();
		
		//파라미터를 변경할 수 있다
		//Object[] params = {"PC"};
		//Object result = pjp.proceed(params);
		System.out.println("-- Around Advice : After --");
		
		return result;
	}
}
