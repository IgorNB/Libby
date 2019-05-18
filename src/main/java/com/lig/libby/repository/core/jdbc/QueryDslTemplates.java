/*
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lig.libby.repository.core.jdbc;

import com.querydsl.core.types.*;

import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Map;

public class QueryDslTemplates extends com.querydsl.core.types.Templates {

    public static final QueryDslTemplates DEFAULT = new QueryDslTemplates();
    private final Map<Operator, Template> templates = new IdentityHashMap<>(150);
    private final Map<Operator, Integer> precedence = new IdentityHashMap<>(150);
    private final TemplateFactory templateFactory;
    private final char escape;

    protected QueryDslTemplates() {
        this('\\');
    }

    protected QueryDslTemplates(char escape) {
        this.escape = escape;
        templateFactory = new TemplateFactory(escape) {
            @Override
            public String escapeForLike(String str) {
                return QueryDslTemplates.this.escapeForLike(str);
            }
        };

        add(Ops.LIST, "{0}, {1}", Precedence.LIST);
        add(Ops.SET, "{0}, {1}", Precedence.LIST);
        add(Ops.SINGLETON, "{0}", Precedence.LIST);
        add(Ops.WRAPPED, "({0})");
        add(Ops.ORDER, "order()");

        add(Ops.AND, "{0} and {1}");
        add(Ops.NOT, "not {0}", Precedence.NOT);
        add(Ops.OR, "{0} or {1}");

        // comparison
        add(Ops.BETWEEN, "{0} between {1} and {2}", Precedence.COMPARISON);
        add(Ops.GOE, "{0} >= {1}", Precedence.COMPARISON);
        add(Ops.GT, "{0} > {1}", Precedence.COMPARISON);
        add(Ops.LOE, "{0} <= {1}", Precedence.COMPARISON);
        add(Ops.LT, "{0} < {1}", Precedence.COMPARISON);

        // various
        add(Ops.EQ, "{0} = {1}", Precedence.EQUALITY);
        add(Ops.EQ_IGNORE_CASE, "{0l} = {1l}");
        add(Ops.NE, "{0} != {1}", Precedence.EQUALITY);


        add(Ops.IN, "{0} in ({1})", Templates.Precedence.COMPARISON);
        add(Ops.NOT_IN, "{0} not in ({1})", Templates.Precedence.COMPARISON);
        add(Ops.ALIAS, "{0} as {1}", 0);

        add(Ops.IS_NULL, "{0} is null", Precedence.COMPARISON);
        add(Ops.IS_NOT_NULL, "{0} is not null", Precedence.COMPARISON);

        add(Ops.LIKE_IC, "{0l} like {1l}", Precedence.COMPARISON);
        add(Ops.LIKE_ESCAPE, "{0} like {1} escape '{2s}'", Precedence.COMPARISON);
        add(Ops.LIKE_ESCAPE_IC, "{0l} like {1l} escape '{2s}'", Precedence.COMPARISON);

        add(Ops.LIKE, "{0} like {1} escape '" + escape + "'", Precedence.COMPARISON);
        add(Ops.ENDS_WITH, "{0} like {%1} escape '" + escape + "'", Precedence.COMPARISON);
        add(Ops.ENDS_WITH_IC, "{0l} like {%%1} escape '" + escape + "'", Precedence.COMPARISON);
        add(Ops.STARTS_WITH, "{0} like {1%} escape '" + escape + "'", Precedence.COMPARISON);
        add(Ops.STARTS_WITH_IC, "{0l} like {1%%} escape '" + escape + "'", Precedence.COMPARISON);
        add(Ops.STRING_CONTAINS, "{0} like {%1%} escape '" + escape + "'", Precedence.COMPARISON);
        add(Ops.STRING_CONTAINS_IC, "{0l} like {%%1%%} escape '" + escape + "'", Precedence.COMPARISON);


        // path types
        add(PathType.PROPERTY, "{0}_{1s}");
        add(PathType.VARIABLE, "{0s}");
        add(PathType.DELEGATE, "{0}");

        for (PathType type : new PathType[]{
                PathType.LISTVALUE,
                PathType.MAPVALUE,
                PathType.MAPVALUE_CONSTANT}) {
            add(type, "{0}.get({1})");
        }
        add(PathType.ARRAYVALUE, "{0}({1})");
        add(PathType.COLLECTION_ANY, "any5({0})");

        add(PathType.LISTVALUE_CONSTANT, "{0}.get({1s})"); // serialized constant
        add(PathType.ARRAYVALUE_CONSTANT, "{0}({1s})");    // serialized constant
    }

    @Override
    protected String escapeForLike(String str) {
        final StringBuilder rv = new StringBuilder(str.length() + 3);
        for (char ch : str.toCharArray()) {
            if (ch == escape || ch == '%' || ch == '_') {
                rv.append(escape);
            }
            rv.append(ch);
        }
        return rv.toString();
    }

    @Override
    protected void setPrecedence(int p, Operator... ops) {
        setPrecedence(p, Arrays.asList(ops));
    }

    @Override
    protected void setPrecedence(int p, Iterable<? extends Operator> ops) {
        for (Operator op : ops) {
            precedence.put(op, p);
        }
    }

    protected static class Precedence {
        public static final int HIGHEST = -1;
        public static final int DOT = 5;
        public static final int NOT_HIGH = 10;
        public static final int NEGATE = 20;
        public static final int ARITH_HIGH = 30;
        public static final int ARITH_LOW = 40;
        public static final int COMPARISON = 50;
        public static final int EQUALITY = 60;
        public static final int CASE = 70;
        public static final int LIST = CASE;
        public static final int NOT = 80;
        public static final int AND = 90;
        public static final int XOR = 100;
        public static final int XNOR = XOR;
        public static final int OR = 110;
        private Precedence() {
            throw new IllegalStateException("Utility class");
        }
    }

}
