package com.woozooha.adonistrack.aspect;

import com.woozooha.adonistrack.domain.Context;
import com.woozooha.adonistrack.domain.Invocation;
import com.woozooha.adonistrack.domain.SqlEvent;
import com.woozooha.adonistrack.domain.SqlInfo;
import com.woozooha.adonistrack.format.SqlFormat;
import com.woozooha.adonistrack.format.SqlMessageFormat;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import java.util.function.UnaryOperator;

@Aspect
public class SqlSpyAspect extends PrintableAspect {

    protected static SqlFormat sqlFormat = new SqlMessageFormat(100);

    protected static UnaryOperator<String> sqlConverter = (sql) -> sql;

    public static void setSqlConverter(UnaryOperator<String> sqlConverter) {
        SqlSpyAspect.sqlConverter = sqlConverter;
    }

    @Pointcut("within(net.sf.log4jdbc.sql.jdbcapi.StatementSpy+) && execution(* reportSql(String, String))")
    public void reportSqlPointcut() {
    }

    @Before("reportSqlPointcut()")
    public void profileReportSql(JoinPoint joinPoint) {
        if (!Context.checkCurrentTrace()) {
            return;
        }

        print("profileReportSql", joinPoint);

        SqlInfo sqlInfo = new SqlInfo();
        String sql = (String) joinPoint.getArgs()[0];
        String convertedSql = sqlConverter.apply(sql);
        sqlInfo.setSql(convertedSql);
        sqlInfo.setStart(System.currentTimeMillis());
        sqlInfo.setEnd(System.currentTimeMillis());

        addEvent(sqlInfo);
    }

    private boolean addEvent(SqlInfo sqlInfo) {
        Context current = Context.getCurrent().get();

        Invocation invocation = current.peekFromInvocationStack();

        if (invocation == null) {
            return false;
        }

        sqlInfo.setMessage(sqlFormat.format(sqlInfo.getSql()));

        SqlEvent event = new SqlEvent(sqlInfo);
        invocation.add(event);

        return true;
    }

}
