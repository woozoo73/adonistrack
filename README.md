# AdonisTrack
Java profiling tool.
- You can trace the call stack immediately.
- Provide method call information, parameters, return values and error information.
- Compatible with Spring Framework.
- Information is not mixed in concurrent calls.
- Provides an extension point for custom.

#### Make your aspect to profile the request: AdonistrackAspect.java
##### configure "profilePointcut" and "executionPointcut" by using pointcut expression.

    package com.woozooha.hello;

    import org.aspectj.lang.annotation.Aspect;
    import org.aspectj.lang.annotation.Pointcut;
    import org.springframework.stereotype.Component;

    import com.woozooha.adonistrack.ProfileAspect;

    @Aspect
    @Component
    public class AdonistrackAspect extends ProfileAspect {

        @Pointcut("execution(* com.woozooha.hello.*Controller.*(..))")
        protected void profilePointcut() {
        }

        @Pointcut("execution(* *(..)) && within(com.woozooha.hello..*)")
        protected void executionPointcut() {
        }

    }

#### Run your application.

      .   ____          _            __ _ _
     /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
    ( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
     \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
      '  |____| .__|_| |_|_| |_\__, | / / / /
     =========|_|==============|___/=/_/_/_/
     :: Spring Boot ::        (v2.1.3.RELEASE)

    2019-04-16 15:03:15.493  INFO 210736 --- [           main] com.woozooha.hello.Application           : Starting Application on woo-thinkpad with PID 210736 (C:\Users\woozoo\eclipse-workspace\lilacscent-test\target\classes started by woozoo in C:\Users\woozoo\eclipse-workspace\lilacscent-test)
    ...
    2019-04-16 15:03:25.651  INFO 210736 --- [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : Completed initialization in 7 ms
    
#### When request: GET /greeting?name=foo HTTP/1.1
#### Output profiling log:

    ----> com.woozooha.hello.GreetingController.greeting(World) (6.89ms:100.00%)
        ----> com.woozooha.hello.GreetingService.greeting(World) (3.76ms:100.00%)
            ----> com.woozooha.hello.GreetingRepository.find(foo) (1.77ms:99.27%)
            <---- hello (1.77ms:99.27%)
            ----> com.woozooha.hello.GreetingRepository.find(bar) (0.01ms:0.73%)
            <---- hello (0.01ms:0.73%)
        <---- com.woozooha.hello.Greeting@2879ceae (3.76ms:100.00%)
    <---- com.woozooha.hello.Greeting@2879ceae (6.89ms:100.00%)
