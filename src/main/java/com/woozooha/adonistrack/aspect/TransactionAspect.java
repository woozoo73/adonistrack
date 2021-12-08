package com.woozooha.adonistrack.aspect;

import com.woozooha.adonistrack.domain.*;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class TransactionAspect extends PrintableAspect {

    @Pointcut("within(java.sql.Connection+) && execution(void setAutoCommit(boolean))")
    public void setAutoCommitPointcut() {
    }

    @Pointcut("within(java.sql.Connection+) && execution(void commit())")
    public void commitPointcut() {
    }

    @Pointcut("within(java.sql.Connection+) && execution(void rollback(..))")
    public void rollbackPointcut() {
    }

    @Around("setAutoCommitPointcut()")
    public Object profileSetAutoCommit(ProceedingJoinPoint joinPoint) throws Throwable {
        print("profileSetAutoCommit", joinPoint);

        if (joinPoint == null) {
            return null;
        }

        Object target = joinPoint.getTarget();

        boolean autoCommit = (boolean) joinPoint.getArgs()[0];
        this.addEvent(target, autoCommit, null, null);

        Object r = null;
        try {
            r = joinPoint.proceed();
        } catch (Throwable t) {
            throw t;
        } finally {
            return r;
        }
    }

    @Around("commitPointcut()")
    public Object profileCommit(ProceedingJoinPoint joinPoint) throws Throwable {
        print("profileCommit", joinPoint);

        if (joinPoint == null) {
            return null;
        }

        Object target = joinPoint.getTarget();

        this.addEvent(target, null, Boolean.TRUE, null);

        Object r = null;
        try {
            r = joinPoint.proceed();
        } catch (Throwable t) {
            throw t;
        } finally {
            return r;
        }
    }

    @Around("rollbackPointcut()")
    public Object profileRollback(ProceedingJoinPoint joinPoint) throws Throwable {
        print("profileRollback", joinPoint);

        if (joinPoint == null) {
            return null;
        }

        Object target = joinPoint.getTarget();

        this.addEvent(target, null, null, Boolean.TRUE);

        Object r = null;
        try {
            r = joinPoint.proceed();
        } catch (Throwable t) {
            throw t;
        } finally {
            return r;
        }
    }

    private boolean addEvent(Object target, Boolean autoCommit, Boolean commit, Boolean rollback) {
        Invocation invocation = Context.peekFromInvocationStack();

        if (invocation == null) {
            return false;
        }

        TransactionInfo transactionInfo = new TransactionInfo();
        transactionInfo.setStart(System.currentTimeMillis());
        transactionInfo.setId(System.identityHashCode(target));

        if (autoCommit != null) {
            transactionInfo.setAutoCommit(autoCommit);
            transactionInfo.setStart(System.currentTimeMillis());
        }
        if (commit != null) {
            transactionInfo.setCommit(commit);
        }
        if (rollback != null) {
            transactionInfo.setRollback(rollback);
        }

        transactionInfo.setMessage(transactionInfo.getMessage());

        TransactionEvent event = new TransactionEvent(transactionInfo);
        invocation.add(event);

        return true;
    }

}
