package com.woozooha.adonistrack.aspect;

import com.woozooha.adonistrack.domain.*;
import com.woozooha.adonistrack.format.SqlFormat;
import com.woozooha.adonistrack.format.SqlMessageFormat;
import com.woozooha.adonistrack.util.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;

import java.net.URL;
import java.sql.*;

@Aspect
public class SqlAspect extends PrintableAspect {

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
            Date.class, Time.class, Timestamp.class, URL.class};

    protected static SqlFormat sqlFormat = new SqlMessageFormat();

    public static SqlFormat getSqlFormat() {
        return sqlFormat;
    }

    @Pointcut("within(java.sql.Connection+) && execution(java.sql.Statement+ createStatement())")
    public void createStatementPointcut() {
    }

    @Pointcut("within(java.sql.Connection+) && execution(java.sql.PreparedStatement+ prepareStatement(String))")
    public void prepareStatementPointcut() {
    }

    @Pointcut("within(java.sql.Statement+) && (" +
            "execution(boolean execute(..)) || execution(java.sql.ResultSet+ executeQuery(..)) || execution(int executeUpdate(..)) " +
            "|| execution(long executeLargeUpdate(..)) || execution(int[] executeBatch(..)) || execution(int[] executeLargeBatch(..))" +
            ") " +
            "&& !cflowbelow(execution(* java.sql..*+.*(..)))")
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

        print("profileCreateStatement", joinPoint);

        if (r == null) {
            return;
        }

        Statement statement = (Statement) r;
        Integer id = System.identityHashCode(statement);
        SqlInfo sqlInfo = new SqlInfo();
        sqlInfo.setId(id);
        sqlInfo.setConnectionId(System.identityHashCode(joinPoint.getTarget()));
        sqlInfo.setType(SqlInfo.Type.Statement);

        SqlContext.put(id, sqlInfo);
    }

    @AfterReturning(pointcut = "prepareStatementPointcut()", returning = "r")
    public void profilePrepareStatementPointcut(JoinPoint joinPoint, Object r) {
        if (!Context.checkCurrentTrace()) {
            return;
        }

        print("profilePrepareStatementPointcut", joinPoint);

        if (r == null) {
            return;
        }

        PreparedStatement preparedStatement = (PreparedStatement) r;
        Integer id = System.identityHashCode(preparedStatement);
        SqlInfo sqlInfo = new SqlInfo();
        sqlInfo.setId(id);
        sqlInfo.setConnectionId(System.identityHashCode(joinPoint.getTarget()));
        sqlInfo.setType(SqlInfo.Type.PreparedStatement);

        String sql = (String) joinPoint.getArgs()[0];
        sqlInfo.setSql(sql);

        SqlContext.put(id, sqlInfo);
    }

    @Before("executePointcut()")
    public void profileBeforeExecute(JoinPoint joinPoint) {
        if (!Context.checkCurrentTrace()) {
            return;
        }

        print("profileBeforeExecute", joinPoint);

        Statement statement = (Statement) joinPoint.getTarget();
        Integer id = System.identityHashCode(statement);
        SqlInfo sqlInfo = SqlContext.get(id);

        if (sqlInfo == null) {
            return;
        }

        sqlInfo.setStart(System.currentTimeMillis());
    }

    @After("executePointcut()")
    public void profileAfterExecute(JoinPoint joinPoint) {
        if (!Context.checkCurrentTrace()) {
            return;
        }

        print("profileAfterExecute", joinPoint);

        Statement statement = (Statement) joinPoint.getTarget();
        Integer id = System.identityHashCode(statement);
        SqlInfo sqlInfo = SqlContext.get(id);

        if (sqlInfo == null) {
            return;
        }

        sqlInfo.setEnd(System.currentTimeMillis());

        if (joinPoint.getArgs().length > 0) {
            String sql = (String) joinPoint.getArgs()[0];
            sqlInfo.setSql(sql);
        }

        addEvent(sqlInfo);
    }

    @AfterThrowing(pointcut = "executePointcut()", throwing = "t")
    public void profileAfterThrowingExecute(JoinPoint joinPoint, Throwable t) {
        if (!Context.checkCurrentTrace()) {
            return;
        }

        print("profileAfterThrowingExecute", joinPoint);

        Statement statement = (Statement) joinPoint.getTarget();
        Integer id = System.identityHashCode(statement);
        SqlInfo sqlInfo = SqlContext.get(id);

        if (sqlInfo == null) {
            return;
        }

        sqlInfo.setThrowableInfo(new ObjectInfo(t));
    }

    @Before("setParameterPointcut()")
    public void profileSetParameter(JoinPoint joinPoint) {
        if (!Context.checkCurrentTrace()) {
            return;
        }

        print("profileSetParameter", joinPoint);

        PreparedStatement preparedStatement = (PreparedStatement) joinPoint.getTarget();
        Integer id = System.identityHashCode(preparedStatement);
        SqlInfo sqlInfo = SqlContext.get(id);

        if (sqlInfo == null) {
            return;
        }

        Object[] args = joinPoint.getArgs();
        if (args[0] == null) {
            return;
        }

        Integer index = (Integer) args[0];
        final Object value = args[1];

        if (value == null) {
            sqlInfo.setParameter(index, "null");
        } else if (shouldProfileParameter(joinPoint, value)) {
            Object v = value;
            if (value.getClass() == String.class && value != null) {
                v = StringUtils.abbreviate((String) value, MAX_VALUE_LENGTH);
            }
            sqlInfo.setParameter(index, v);
        } else {
            sqlInfo.setParameter(index, UNKNOWN_VALUE);
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

    private boolean addEvent(SqlInfo sqlInfo) {
        Context current = Context.getCurrent().get();

        Invocation invocation = current.peekFromInvocationStack();

        if (invocation == null) {
            return false;
        }

        sqlInfo.setMessage(sqlInfo.getMessage());

        SqlEvent event = new SqlEvent(sqlInfo);
        invocation.add(event);

        return true;
    }

}
