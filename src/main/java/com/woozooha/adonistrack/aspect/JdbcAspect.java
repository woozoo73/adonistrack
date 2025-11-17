package com.woozooha.adonistrack.aspect;

import com.woozooha.adonistrack.domain.*;
import com.woozooha.adonistrack.util.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;

import java.net.URL;
import java.sql.Date;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;

@Aspect
public class JdbcAspect {

    public static final String UNKNOWN_VALUE = "?";

    public static final int MAX_VALUE_LENGTH = 20;

    public static final Class<?>[] SHOULD_LOG_PARAMETER_TYPES = {
            // Primative types
            Byte.TYPE, Short.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE,
            // Wrapper types
            Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class,
            // String
            String.class,
            // Date & URL
            Date.class, Time.class, Timestamp.class, URL.class};

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
        if (!Context.checkCurrentTrace()) {
            return;
        }

        if (r == null) {
            return;
        }

        Statement statement = (Statement) r;
        JdbcStatementInfo statementInfo = new JdbcStatementInfo();
        String sql = (String) joinPoint.getArgs()[0];
        sql = sql.replaceAll("\\n", " ");
        sql = sql.replaceAll("\\t", " ");
        statementInfo.setSql(sql);

        JdbcContext.put(statement, statementInfo);
    }

    @Before("executePointcut()")
    public void profileBeforeExecute(JoinPoint joinPoint) {
        if (!Context.checkCurrentTrace()) {
            return;
        }

        Statement statement = (Statement) joinPoint.getTarget();
        JdbcStatementInfo statementInfo = JdbcContext.get(statement);

        if (statementInfo == null) {
            return;
        }

        statementInfo.setStart(System.nanoTime());
    }

    @After("executePointcut()")
    public void profileAfterExecute(JoinPoint joinPoint) {
        if (!Context.checkCurrentTrace()) {
            return;
        }

        Statement statement = (Statement) joinPoint.getTarget();
        JdbcStatementInfo statementInfo = JdbcContext.get(statement);

        if (statementInfo == null) {
            return;
        }

        statementInfo.setEnd(System.nanoTime());
        statementInfo.calculateDuration();

        Context current = Context.getCurrent().get();

        Invocation invocation = current.peekFromInvocationStack();

        if (invocation != null) {
            JdbcEvent jdbcEvent = new JdbcEvent(statementInfo);
            invocation.add(jdbcEvent);
            current.increaseCount();
        }
    }

    @AfterThrowing(pointcut = "executePointcut()", throwing = "t")
    public void profileAfterThrowingExecute(JoinPoint joinPoint, Throwable t) {
        if (!Context.checkCurrentTrace()) {
            return;
        }

        Statement statement = (Statement) joinPoint.getTarget();
        JdbcStatementInfo statementInfo = JdbcContext.get(statement);

        if (statementInfo == null) {
            return;
        }

        statementInfo.setThrowableInfo(new ObjectInfo(t));
    }

    @Before("setParameterPointcut()")
    public void profileSetParameter(JoinPoint joinPoint) {
        if (!Context.checkCurrentTrace()) {
            return;
        }

        Statement statement = (Statement) joinPoint.getTarget();
        JdbcStatementInfo statementInfo = JdbcContext.get(statement);

        if (statementInfo == null) {
            return;
        }

        Object[] args = joinPoint.getArgs();
        if (args[0] == null) {
            return;
        }

        Integer index = (Integer) args[0];
        final Object value = args[1];

        if (value == null) {
            statementInfo.setParameter(index, "null");
        } else if (shouldProfileParameter(joinPoint, value)) {
            Object v = value;
            if (value.getClass() == String.class && value != null) {
                v = StringUtils.abbreviate((String) value, MAX_VALUE_LENGTH);
            }
            statementInfo.setParameter(index, v);
        } else {
            statementInfo.setParameter(index, UNKNOWN_VALUE);
        }
    }

    private boolean shouldProfileParameter(JoinPoint joinPoint, Object value) {
        String signatureName = joinPoint.getSignature().getName();

        for (Class<?> type : SHOULD_LOG_PARAMETER_TYPES) {
            String setParameterName = "set" + StringUtils.capitalize(type.getSimpleName());
            if (value.getClass() == type && setParameterName.equals(signatureName)) {
                return true;
            }
        }

        return false;
    }

}
