package com.woozooha.adonistrack.aspect;

import com.woozooha.adonistrack.domain.*;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;

import java.net.URL;
import java.sql.Date;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;

@Aspect
public class SqlAspect {

    public static final String UNKNOWN_VALUE = "?";

    public static final int MAX_VALUE_LENGTH = 100;

    public static final Class<?>[] SHOULD_LOG_PARAMETER_TYPES = {
            // Primative types
            Byte.TYPE, Short.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE,
            // Wrapper types
            Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class,
            // String
            String.class,
            // Date & URL
            Date.class, Time.class, Timestamp.class, URL.class };

    @Pointcut("within(java.sql.Connection+) && (execution(java.sql.PreparedStatement+ prepareStatement(String)) || execution(java.sql.Statement+ createStatement(String)))")
    public void createStatementPointcut() {
    }

    @Pointcut("within(java.sql.Statement+) && (execution(boolean execute()) || execution(java.sql.ResultSet+ executeQuery()) || execution(int executeUpdate())) && !cflowbelow(execution(* java.sql..*+.*(..)))")
    public void executePointcut() {
    }

    @Pointcut("within(java.sql.PreparedStatement+) && execution(void java.sql.PreparedStatement+.set*(int, *))")
    public void setParameterPointcut() {
    }

    @AfterReturning(pointcut = "createStatementPointcut()", returning = "r")
    public void profileCreateStatement(JoinPoint joinPoint, Object r) {
        if (r == null) {
            return;
        }

        Statement statement = (Statement) r;
        SqlInfo sqlInfo = new SqlInfo();
        sqlInfo.setId(System.identityHashCode(statement));
        sqlInfo.setId(System.identityHashCode(joinPoint.getTarget()));

        String sql = (String) joinPoint.getArgs()[0];
        sqlInfo.setSql(sql);

        this.addEvent(sqlInfo);
    }

    @Before("executePointcut()")
    public void profileBeforeExecute(JoinPoint joinPoint) {
        Statement statement = (Statement) joinPoint.getTarget();
        SqlInfo sqlInfo = new SqlInfo();
        sqlInfo.setId(System.identityHashCode(statement));
        sqlInfo.setStart(System.currentTimeMillis());

        this.addEvent(sqlInfo);
    }

    @After("executePointcut()")
    public void profileAfterExecute(JoinPoint joinPoint) {
        Statement statement = (Statement) joinPoint.getTarget();
        SqlInfo sqlInfo = new SqlInfo();
        sqlInfo.setId(System.identityHashCode(statement));
        sqlInfo.setEnd(System.currentTimeMillis());

        this.addEvent(sqlInfo);
    }

    @AfterThrowing(pointcut = "executePointcut()", throwing = "t")
    public void profileAfterThrowingExecute(JoinPoint joinPoint, Throwable t) {
        Statement statement = (Statement) joinPoint.getTarget();
        SqlInfo sqlInfo = new SqlInfo();
        sqlInfo.setId(System.identityHashCode(statement));
        sqlInfo.setThrowableInfo(new ObjectInfo(t));

        this.addEvent(sqlInfo);
    }

    private boolean addEvent(SqlInfo sqlInfo) {
        Invocation invocation = Context.peekFromInvocationStack();

        if (invocation == null) {
            return false;
        }

        SqlEvent event = new SqlEvent(sqlInfo);
        invocation.add(event);

        return true;
    }

}
