package com.woozooha.adonistrack.aspect;

import com.woozooha.adonistrack.domain.Context;
import com.woozooha.adonistrack.domain.Invocation;
import com.woozooha.adonistrack.domain.SqlEvent;
import com.woozooha.adonistrack.domain.SqlInfo;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class SqlSpyAspect extends PrintableAspect {

    @Pointcut("within(net.sf.log4jdbc.sql.jdbcapi.StatementSpy+) && execution(* reportSql(String, String))")
    public void reportSqlPointcut() {
    }

    @Before("reportSqlPointcut()")
    public void profileReportSql(JoinPoint joinPoint) {
        print("profileReportSql", joinPoint);

        SqlInfo sqlInfo = new SqlInfo();
        String sql = (String) joinPoint.getArgs()[0];
        sqlInfo.setSql(sql);
        sqlInfo.setStart(System.currentTimeMillis());
        sqlInfo.setEnd(System.currentTimeMillis());

        addEvent(sqlInfo);
    }

    private boolean addEvent(SqlInfo sqlInfo) {
        Invocation invocation = Context.peekFromInvocationStack();

        if (invocation == null) {
            return false;
        }

        sqlInfo.setMessage(sqlInfo.getMessage());

        SqlEvent event = new SqlEvent(sqlInfo);
        invocation.add(event);

        return true;
    }

}
