package com.tamimattafi.sqlite.query

import com.tamimattafi.sqlite.query.SQLiteQueryBuilder.Syntax.ALL
import com.tamimattafi.sqlite.query.SQLiteQueryBuilder.Syntax.SEPARATOR

open class SQLiteQueryBuilder {


    /**
     * The current SQLite syntax is stored here. StringBuilder is used to avoid concatenation
     * @see StringBuilder
     *
     */
    private val rawQueryBuilder = StringBuilder()


    /**
     * Starts building a selection query to read lists, columns or single rows from a table
     * @see <a href="https://www.sqlitetutorial.net/sqlite-select/">SQLite SELECT keyword</a>
     *
     */
    fun select(): Selection {

    }


    /**
     * Appends a raw SQLite syntax or query to the current query that's being built
     * @see <a href="https://www.sqlite.org/lang.html">SQLite Syntax</a>
     *
     */
    fun appendRawSyntax(syntax: String) = this.apply {
        this.rawQueryBuilder.append(syntax).append(SEPARATOR)
    }


    /**
     * Handles building a selection query and limits syntax errors by limiting the amount of methods that can be called
     * @see <a href="https://www.sqlitetutorial.net/sqlite-select/">SQLite SELECT keyword</a>
     * @see select
     *
     */
    inner class Selection {


        /**
         * Creates a selection query that will return all columns of a table
         * @see <a href="https://www.sqlitetutorial.net/sqlite-select/">SQLite 'SELECT *' expression</a>
         *
         * @return Returns selection source handler
         * @see Source
         *
         */
        fun all(): Source = this.appendSelectionSyntax(ALL)


        /**
         * Creates a selection query that will return some columns in a distinct way
         * @see <a href="https://www.sqlitetutorial.net/sqlite-select-distinct/">SQLite 'SELECT DISTINCT column1, column2' expression</a>
         *
         * @param columns The columns that should be returned by the query
         *
         * @return Returns selection source handler
         * @see Source
         *
         */
        fun distinct(columns: String): Source {

        }


        /**
         * Creates a selection query that will return some columns
         * @see <a href="https://www.sqlitetutorial.net/sqlite-select/">SQLite 'SELECT column1, column2' expression</a>
         *
         * @param columns The columns that should be returned by the query
         *
         * @return Returns selection source handler
         * @see Source
         *
         */
        fun columns(columns: String): Source {

        }


        /**
         * Appends a raw syntax to the selection process. Raw syntax is not filtered or checked for errors
         * @see <a href="https://www.sqlitetutorial.net/sqlite-select/">SQLite 'SELECT column1, column2' expression</a>
         *
         * @param syntax The raw syntax to be appended
         *
         * @return Returns selection source handler
         * @see Source
         *
         */
        fun appendSelectionSyntax(syntax: String): Source {
            appendRawSyntax(syntax)
        }

        inner class Source {

            fun fromTable(table: String): Modification {

            }

            fun fromRawQuery(rawQuery: String): Modification {

            }

        }

    }

    inner class Modification {



    }

    object Direction {
        const val ASCENDING = "ASC"
        const val DESCENDING = "DESC"
    }

    object Operators {
        const val EQUAL_TO = "="
        const val IN = "IN"
        const val NOT_EQUAL_TO = "!="
        const val BIGGER_THAN = ">"
        const val LESS_THAN = "<"
        const val BIGGER_THAN_OR_EQUAL_TO = ">="
        const val LESS_THAN_OR_EQUAL_TO = "<="
    }

    object Syntax {
        const val WHERE = "WHERE"
        const val AND = "AND"
        const val OR = "OR"
        const val SELECT = "SELECT"
        const val DELETE = "DELETE"
        const val ALL = "*"
        const val DISTINCT = "DISTINCT"
        const val FROM = "FROM"
        const val LIMIT = "LIMIT"
        const val OFFSET = "OFFSET"
        const val LIKE = "LIKE"
        const val ORDER_BY = "ORDER BY"
        const val INNER_JOIN = "INNER JOIN"
        const val ON = "ON"
        const val IS_NULL = "IS NULL"
        const val IS_NOT_NULL = "IS NOT NULL"
        const val SEPARATOR = ' '
        const val ELEMENT_SEPARATOR = ','
    }
}