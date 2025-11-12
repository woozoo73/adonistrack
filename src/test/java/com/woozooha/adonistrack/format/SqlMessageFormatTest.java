package com.woozooha.adonistrack.format;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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

    SqlMessageFormat whereFormat;
    SqlMessageFormat format;

    @Before
    public void setUp() {
        whereFormat = new SqlMessageFormat(100, true);
        format = new SqlMessageFormat(100);
    }

    @Test
    public void invalid0() {
        String message = whereFormat.format(invalid);
        assertEquals(invalid, message);
    }

    @Test
    public void invalid1() {
        whereFormat.setMaxLength(15);
        String message = whereFormat.format(invalid);
        assertEquals(15 + 4, message.length());
        assertEquals("the quick brown ...", message);
    }

    @Test
    public void select0() {
        {
            String message = whereFormat.format(select0);
            assertEquals("SELECT ~ FROM foo foo_ ~ WHERE foo_.id = ? ~", message);
        }
        {
            String message = format.format(select0);
            assertEquals("SELECT ~ FROM foo foo_ ~", message);
        }
    }

    @Test
    public void select1() {
        {
            String message = whereFormat.format(select1);
            assertEquals("SELECT ~ FROM foo foo_ ~ WHERE foo_.id = ? ~", message);
        }
        {
            String message = format.format(select1);
            assertEquals("SELECT ~ FROM foo foo_ ~", message);
        }
    }

    @Test
    public void select2() {
        {
            String message = whereFormat.format(select2);
            assertEquals("SELECT ~ FROM user user_ ~ WHERE user_.id = ? ~", message);
        }
        {
            String message = format.format(select2);
            assertEquals("SELECT ~ FROM user user_ ~", message);
        }
    }

    @Test
    public void select3() {
        {
            String message = whereFormat.format(select3);
            assertEquals("SELECT ~ FROM user user_ ~ WHERE user_.id = ? ~ [20, 10]", message);
        }
        {
            String message = format.format(select3);
            assertEquals("SELECT ~ FROM user user_ ~ [20, 10]", message);
        }
    }

    @Test
    public void select4() {
        {
            String message = whereFormat.format(select4);
            assertEquals("SELECT ~ FROM user user_ ~ WHERE user_.id = ? ~ [10]", message);
        }
        {
            String message = format.format(select4);
            assertEquals("SELECT ~ FROM user user_ ~ [10]", message);
        }
    }

    @Test
    public void select5() {
        {
            String message = whereFormat.format(select5);
            assertEquals("SELECT ~", message);
        }
        {
            String message = format.format(select5);
            assertEquals("SELECT ~", message);
        }
    }

    @Test
    public void select6() {
        {
            String message = whereFormat.format(select6);
            assertEquals("SELECT ~ FROM foo f ~ WHERE f.id = b.id ~", message);
        }
        {
            String message = format.format(select6);
            assertEquals("SELECT ~ FROM foo f ~", message);
        }
    }

    @Test
    public void insert0() {
        {
            String message = whereFormat.format(insert0);
            assertEquals("INSERT INTO contract ~", message);
        }
        {
            String message = format.format(insert0);
            assertEquals("INSERT INTO contract ~", message);
        }
    }

    @Test
    public void update0() {
        {
            String message = whereFormat.format(update0);
            assertEquals("UPDATE user SET ~ WHERE id = ? ~", message);
        }
        {
            String message = format.format(update0);
            assertEquals("UPDATE user SET ~", message);
        }
    }

    @Test
    public void delete0() {
        {
            String message = whereFormat.format(delete0);
            assertEquals("DELETE FROM foo WHERE id = ? ~", message);
        }
        {
            String message = format.format(delete0);
            assertEquals("DELETE FROM foo", message);
        }
    }

    @Test
    public void multiLine0() {
        {
            String message = whereFormat.format(multiLine0);
            assertEquals("foo from foo where id=?", message);
        }
        {
            String message = format.format(multiLine0);
            assertEquals("foo from foo where id=?", message);
        }
    }

    @Test
    public void multiLine1() {
        {
            String message = whereFormat.format(multiLine1);
            assertEquals("foo from from from from from from from from from from from from from from from from from from from f ...", message);
        }
        {
            String message = format.format(multiLine1);
            assertEquals("foo from from from from from from from from from from from from from from from from from from from f ...", message);
        }
    }

    @Test
    public void multiLine2() {
        {
            String message = whereFormat.format(multiLine2);
            assertEquals("foo0  from from from from from0  from from from from from0  from from from from from0  ", message);
        }
        {
            String message = format.format(multiLine2);
            assertEquals("foo0  from from from from from0  from from from from from0  from from from from from0  ", message);
        }
    }

}
