package com.woozooha.adonistrack.format;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SimpleSqlMessageFormatTest {

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

    String multiLine0 = "foo\n" +
            "from\n" +
            "foo\n" +
            "where\n" +
            "id=?";

    String multiLine1 = "foo\n" +
            "from from from from from\n" +
            "from from from from from\n" +
            "from from from from from\n" +
            "from from from from from\n" +
            "from from from from from\n" +
            "from from from from from\n" +
            "foo \n" +
            "where\n" +
            "id=?";

    String multiLine2 = "foo0\r\n" +
            "from from from from from0\n\r" +
            "from from from from from0\r\n" +
            "from from from from from0\n\r";

    SimpleSqlMessageFormat format;

    @Before
    public void setUp() {
        format = new SimpleSqlMessageFormat();
    }

    @Test
    public void invalid0() {
        String message = format.format(invalid);
        assertEquals("? ?", message);
    }

    @Test
    public void select0() {
        String message = format.format(select0);
        assertEquals("SELECT foo", message);
    }

    @Test
    public void select1() {
        String message = format.format(select1);
        assertEquals("SELECT foo", message);
    }

    @Test
    public void select2() {
        String message = format.format(select2);
        assertEquals("SELECT user", message);
    }

    @Test
    public void select3() {
        String message = format.format(select3);
        assertEquals("SELECT user", message);
    }

    @Test
    public void select4() {
        String message = format.format(select4);
        assertEquals("SELECT user", message);
    }

    @Test
    public void select5() {
        String message = format.format(select5);
        assertEquals("SELECT user", message);
    }

    @Test
    public void select6() {
        String message = format.format(select6);
        assertEquals("SELECT foo", message);
    }

    @Test
    public void insert0() {
        String message = format.format(insert0);
        assertEquals("INSERT contract", message);
    }

    @Test
    public void update0() {
        String message = format.format(update0);
        assertEquals("UPDATE user", message);
    }

    @Test
    public void delete0() {
        String message = format.format(delete0);
        assertEquals("DELETE foo", message);
    }

    @Test
    public void multiLine0() {
        String message = format.format(multiLine0);
        assertEquals("? ?", message);
    }

    @Test
    public void multiLine1() {
        String message = format.format(multiLine1);
        assertEquals("? ?", message);
    }

    @Test
    public void multiLine2() {
        String message = format.format(multiLine2);
        assertEquals("? ?", message);
    }

}
