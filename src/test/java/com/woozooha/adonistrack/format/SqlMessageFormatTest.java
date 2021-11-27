package com.woozooha.adonistrack.format;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SqlMessageFormatTest {

    String invalid = "the quick brown fox jumps over the lazy dog";

    String select0 = "    select\n" +
            "        foo_.id as foo1_5_0_,\n" +
            "        bar_.user_id as bar1_5_3_ \n" +
            "    from\n" +
            "        foo foo_ \n" +
            "    left outer join\n" +
            "        bar bar_ \n" +
            "            on foo_.user_id=bar_.id \n" +
            "    where\n" +
            "        foo_.id=?";

    String select1 = "/* get current state com.woozooha.ltr.model.Foo */ select\n" +
            "        foo_.id,\n" +
            "        foo_.nm as nm6_6_ \n" +
            "    from\n" +
            "        foo foo_ \n" +
            "    where\n" +
            "        foo_.id=?";

    String select2 = "/* get current state com.woozooha.ltr.model.User */ select\n" +
            "        user_.id,\n" +
            "        user_.name as name5_7_,\n" +
            "        user_.nickname as nickname6_7_ \n" +
            "    from\n" +
            "        user user_ \n" +
            "    where\n" +
            "        user_.id=?";

    String select3 = "/* get current state com.woozooha.ltr.model.User */ select\n" +
            "        user_.id,\n" +
            "        user_.name as name5_7_,\n" +
            "        user_.nickname as nickname6_7_ \n" +
            "    from\n" +
            "        user user_ \n" +
            "    where\n" +
            "        user_.id=? \n" +
            "    limit 20, 10";

    String select4 = "/* get current state com.woozooha.ltr.model.User */ select\n" +
            "        user_.id,\n" +
            "        user_.name as name5_7_,\n" +
            "        user_.nickname as nickname6_7_ \n" +
            "    from\n" +
            "        user user_ \n" +
            "    where\n" +
            "        user_.id=? \n" +
            "    limit 10";

    String select5 = "select * from ( select rownum rnum, id, name from user order by id asc ) where rnum between 11 and 20";

    String select6 = "select f.*, b.* from foo f, bar b where f.id = b.id";

    String insert0 = "/* insert com.woozooha.ltr.model.Contract\n" +
            "        */ insert \n" +
            "        into\n" +
            "            contract\n" +
            "            (name, user_id) \n" +
            "        values\n" +
            "            (?, ?)";
    String update0 = "/* update\n" +
            "        com.woozooha.ltr.model.User */ update\n" +
            "            user \n" +
            "        set\n" +
            "            age=? \n" +
            "        where\n" +
            "            id=?";

    String delete0 = "/* delete com.woozooha.ltr.model.Foo */ delete \n" +
            "        from\n" +
            "            foo \n" +
            "        where\n" +
            "            id=?";

    SqlMessageFormat format;

    @Before
    public void setUp() {
        format = new SqlMessageFormat();
    }

    @Test
    public void invalid0() {
        String message = format.format(invalid);
        assertEquals(invalid, message);
    }

    @Test
    public void invalid1() {
        format.setMaxLength(15);
        String message = format.format(invalid);
        assertEquals(15 + 4, message.length());
        assertEquals("the quick brown ...", message);
    }

    @Test
    public void select0() {
        String message = format.format(select0);
        assertEquals("SELECT ~ FROM foo foo_ ~ WHERE foo_.id = ? ~", message);
    }

    @Test
    public void select1() {
        String message = format.format(select1);
        assertEquals("SELECT ~ FROM foo foo_ ~ WHERE foo_.id = ? ~", message);
    }

    @Test
    public void select2() {
        String message = format.format(select2);
        assertEquals("SELECT ~ FROM user user_ ~ WHERE user_.id = ? ~", message);
    }

    @Test
    public void select3() {
        String message = format.format(select3);
        assertEquals("SELECT ~ FROM user user_ ~ WHERE user_.id = ? ~ [20, 10]", message);
    }

    @Test
    public void select4() {
        String message = format.format(select4);
        assertEquals("SELECT ~ FROM user user_ ~ WHERE user_.id = ? ~ [10]", message);
    }

    @Test
    public void select5() {
        String message = format.format(select5);
        // assertEquals("SELECT ~ FROM user ~ [20, 10]", message);
        assertEquals("SELECT ~", message);
    }

    @Test
    public void select6() {
        String message = format.format(select6);
        assertEquals("SELECT ~ FROM foo f ~ WHERE f.id = b.id ~", message);
    }

    @Test
    public void insert0() {
        String message = format.format(insert0);
        assertEquals("INSERT INTO contract ~", message);
    }

    @Test
    public void update0() {
        String message = format.format(update0);
        assertEquals("UPDATE user SET ~ WHERE id = ? ~", message);
    }

    @Test
    public void delete0() {
        String message = format.format(delete0);
        assertEquals("DELETE FROM foo WHERE id = ? ~", message);
    }

}
