package com.tamimattafi.sqlite.query

import com.tamimattafi.sqlite.query.SQLiteOperators.BETWEEN
import com.tamimattafi.sqlite.query.SQLiteOperators.GREATER_THAN_OR_EQUAL_TO
import com.tamimattafi.sqlite.query.SQLiteOperators.GREATER_THAN
import com.tamimattafi.sqlite.query.SQLiteOperators.EQUAL_TO
import com.tamimattafi.sqlite.query.SQLiteOperators.EXISTS
import com.tamimattafi.sqlite.query.SQLiteOperators.IN
import com.tamimattafi.sqlite.query.SQLiteOperators.IS
import com.tamimattafi.sqlite.query.SQLiteOperators.LESS_THAN
import com.tamimattafi.sqlite.query.SQLiteOperators.LESS_THAN_OR_EQUAL_TO
import com.tamimattafi.sqlite.query.SQLiteOperators.NOT
import com.tamimattafi.sqlite.query.SQLiteOperators.NOT_EQUAL_TO
import com.tamimattafi.sqlite.query.SQLiteOperators.NULL
import com.tamimattafi.sqlite.query.SQLiteQueryUtils.toContainedSQLiteElements
import com.tamimattafi.sqlite.query.SQLiteQueryUtils.toSQLiteElements
import com.tamimattafi.sqlite.query.SQLiteSyntax.AND
import com.tamimattafi.sqlite.query.SQLiteSyntax.CLOSER
import com.tamimattafi.sqlite.query.SQLiteSyntax.CLOSE_PARENTHESES
import com.tamimattafi.sqlite.query.SQLiteSyntax.DISTINCT
import com.tamimattafi.sqlite.query.SQLiteSyntax.ELEMENT_SEPARATOR
import com.tamimattafi.sqlite.query.SQLiteSyntax.FROM
import com.tamimattafi.sqlite.query.SQLiteSyntax.INNER_JOIN
import com.tamimattafi.sqlite.query.SQLiteSyntax.LIKE
import com.tamimattafi.sqlite.query.SQLiteSyntax.LIMIT
import com.tamimattafi.sqlite.query.SQLiteSyntax.NULLS_FIRST
import com.tamimattafi.sqlite.query.SQLiteSyntax.NULLS_LAST
import com.tamimattafi.sqlite.query.SQLiteSyntax.OFFSET
import com.tamimattafi.sqlite.query.SQLiteSyntax.ON
import com.tamimattafi.sqlite.query.SQLiteSyntax.OPEN_PARENTHESES
import com.tamimattafi.sqlite.query.SQLiteSyntax.OR
import com.tamimattafi.sqlite.query.SQLiteSyntax.ORDER_BY
import com.tamimattafi.sqlite.query.SQLiteSyntax.SELECT
import com.tamimattafi.sqlite.query.SQLiteSyntax.SEPARATOR
import com.tamimattafi.sqlite.query.SQLiteSyntax.WHERE
import java.lang.IllegalArgumentException
import java.lang.ref.WeakReference


open class SQLiteQueryBuilder {


    /**
     * The current SQLite syntax is stored here. StringBuilder is used to avoid concatenation overhead
     * @see StringBuilder
     *
     */
    protected open val rawQueryBuilder = StringBuilder()

    /**
     * Starts building a selection query to read lists, columns or single rows from a table
     *
     * @return Selection type handler
     *
     * @see Selecting
     * @see <a href="https://www.sqlitetutorial.net/sqlite-select/">SQLite selection syntax</a>
     *
     */
    open fun select(): Selecting {
        //Appends "SELECT" keyword
        this.append(SELECT)

        //Creates (Lazy) and returns selection type handler
        return Selecting()
    }


    /**
     * Appends a raw SQLite syntax or query to the current query that's being built
     *
     * Note:
     * This method only checks if the passed parameters are not empty.
     * It doesn't guarantee that the raw syntax has no errors.
     *
     * @param syntax The raw syntax to be appended
     *
     * @return The current query-builder instance
     *
     * @exception IllegalArgumentException if the syntax is empty.
     *
     * @see <a href="https://www.sqlite.org/lang.html">SQLite syntax</a>
     *
     */
    open fun append(syntax: String) = this.apply {
        //Throws Illegal argument exception if the syntax is blank (contains only spaces)
        require(syntax.isNotBlank())

        //Appends the raw syntax and a space at the end
        this.rawQueryBuilder.append(syntax).append(SEPARATOR)
    }


    /**
     * Appends SQLite syntax or query to the current query that's being built from another builder
     *
     * Note:
     * This method only checks if the passed parameters are not empty.
     * It doesn't guarantee that the raw syntax has no errors.
     *
     * @param syntax The raw syntax to be appended
     *
     * @return The current query-builder instance
     *
     * @exception IllegalArgumentException if the syntax is empty.
     *
     * @see <a href="https://www.sqlite.org/lang.html">SQLite syntax</a>
     *
     */
    open fun append(action: SQLiteQueryBuilder.() -> InnerBuilder) = this.apply {
        val syntax = SQLiteQueryBuilder().action().build()
        //Throws Illegal argument exception if the syntax is blank (contains only spaces)
        require(syntax.isNotBlank())

        //Appends the raw syntax and a space at the end
        this.rawQueryBuilder.append(syntax).append(SEPARATOR)
    }

    /**
     * Appends a raw syntax and starts source selection process.
     *
     * Note:
     * This method calls SQLiteQueryBuilder.append(...) to append the raw syntax.
     * This method only checks if the passed parameters are not empty.
     * It doesn't guarantee that the raw syntax has no errors.
     *
     * @param syntax The raw syntax to be appended
     *
     * @return Selection source handler
     *
     * @exception IllegalArgumentException if the syntax is empty.
     *
     * @see Source
     * @see append
     * @see <a href="https://www.sqlitetutorial.net/sqlite-select/">SQLite selection syntax</a>
     *
     */
    open fun appendAndSelectSource(syntax: String): Source {
        //Appends the raw syntax and a space at the end
        this.append(syntax)

        //Creates (Lazy) and returns source selection handler
        return Source()
    }


    /**
     * Appends a raw syntax and starts modification process.
     *
     * Note:
     * This method calls SQLiteQueryBuilder.append(...) to append the raw syntax.
     * This method only checks if the passed parameters are not empty.
     * It doesn't guarantee that the raw syntax has no errors.
     *
     * @param syntax The raw syntax to be appended
     *
     * @return Modification handler
     *
     * @exception IllegalArgumentException if the syntax is empty.
     *
     * @see Modifying
     * @see <a href="https://www.sqlitetutorial.net/sqlite-where/">SQLite Where Clause</a>
     * @see <a href="https://www.sqlitetutorial.net/sqlite-inner-join/">SQLite inner join syntax</a>
     *
     */
    open fun appendAndModify(syntax: String): Modifying {
        //Appends the raw syntax and a space at the end
        this.append(syntax)

        //Creates (Lazy) and returns modification handler
        return Modifying()
    }


    /**
     * Appends a raw syntax and starts filtering process.
     *
     * Note:
     * This method calls SQLiteQueryBuilder.append(...) to append the raw syntax.
     * This method only checks if the passed parameters are not empty.
     * It doesn't guarantee that the raw syntax has no errors.
     *
     * @param syntax The raw syntax to be appended
     *
     * @return Filtering handler
     *
     * @exception IllegalArgumentException if the syntax is empty.
     *
     * @see Filtering
     * @see <a href="https://www.sqlitetutorial.net/sqlite-where/">SQLite where Clause syntax</a>
     *
     */
    open fun appendAndFilter(syntax: String): Filtering {
        //Appends the raw syntax and a space at the end
        this.append(syntax)

        //Creates (Lazy) and returns filtering handler
        return Filtering()
    }

    open fun appendAndJoin(syntax: String): Joining {
        //Appends the raw syntax and a space at the end
        this.append(syntax)

        //Creates (Lazy) and returns joining handler
        return Joining()
    }


    /**
     * Appends a raw syntax and starts merging process.
     *
     * Note:
     * This method calls SQLiteQueryBuilder.append(...) to append the raw syntax.
     * This method only checks if the passed parameters are not empty.
     * It doesn't guarantee that the raw syntax has no errors.
     *
     * @param syntax The raw syntax to be appended
     *
     * @return Merging handler
     *
     * @exception IllegalArgumentException if the syntax is empty.
     *
     * @see Merging
     * @see <a href="https://www.tutorialspoint.com/sqlite/sqlite_and_or_clauses.htm">SQLite merge operators syntax</a>
     *
     */
    open fun appendAndMerge(syntax: String): Merging {
        //Appends the raw syntax and a space at the end
        this.append(syntax)

        //Creates (Lazy) and returns merging handler
        return Merging()
    }


    /**
     * Appends a raw syntax and starts sorting process.
     *
     * Note:
     * This method calls SQLiteQueryBuilder.append(...) to append the raw syntax.
     * This method only checks if the passed parameters are not empty.
     * It doesn't guarantee that the raw syntax has no errors.
     *
     * @param syntax The raw syntax to be appended
     *
     * @return Sorting handler
     *
     * @exception IllegalArgumentException if the syntax is empty.
     *
     * @see Sorting
     * @see <a href="https://www.sqlitetutorial.net/sqlite-order-by/">SQLite sorting syntax</a>
     *
     */
    open fun appendAndSort(syntax: String): Sorting {
        //Appends the raw syntax and a space at the end
        this.append(syntax)

        //Creates (Lazy) and returns merging handler
        return Sorting()
    }


    /**
     * Appends a raw syntax and starts sub-sorting process.
     *
     * Note:
     * This method calls SQLiteQueryBuilder.append(...) to append the raw syntax.
     * This method only checks if the passed parameters are not empty.
     * It doesn't guarantee that the raw syntax has no errors.
     *
     * @param syntax The raw syntax to be appended
     *
     * @return SubSorting handler
     *
     * @exception IllegalArgumentException if the syntax is empty.
     *
     * @see SubSorting
     * @see <a href="https://www.sqlitetutorial.net/sqlite-order-by/">SQLite sorting syntax</a>
     *
     */
    open fun appendAndSubSort(syntax: String): SubSorting {
        //Appends the raw syntax and a space at the end
        this.append(syntax)

        //Creates (Lazy) and returns merging handler
        return SubSorting()
    }


    /**
     * Appends a raw syntax and starts quantifying process.
     *
     * Note:
     * This method calls SQLiteQueryBuilder.append(...) to append the raw syntax.
     * This method only checks if the passed parameters are not empty.
     * It doesn't guarantee that the raw syntax has no errors.
     *
     * @param syntax The raw syntax to be appended
     *
     * @return Quantifying handler
     *
     * @exception IllegalArgumentException if the syntax is empty.
     *
     * @see Quantifying
     * @see <a href="https://www.sqlitetutorial.net/sqlite-limit/">SQLite limits syntax</a>
     *
     */
    open fun appendAndQuantify(syntax: String): Quantifying {
        //Appends the raw syntax and a space at the end
        this.append(syntax)

        //Creates (Lazy) and returns merging handler
        return Quantifying()
    }


    /**
     * Appends a raw syntax and starts skipping process.
     *
     * Note:
     * This method calls SQLiteQueryBuilder.append(...) to append the raw syntax.
     * This method only checks if the passed parameters are not empty.
     * It doesn't guarantee that the raw syntax has no errors.
     *
     * @param syntax The raw syntax to be appended
     *
     * @return Skipping handler
     *
     * @exception IllegalArgumentException if the syntax is empty.
     *
     * @see Skipping
     * @see <a href="https://www.sqlitetutorial.net/sqlite-limit/">SQLite offset syntax</a>
     *
     */
    open fun appendAndSkip(syntax: String): Skipping {
        //Appends the raw syntax and a space at the end
        this.append(syntax)

        //Creates (Lazy) and returns merging handler
        return Skipping()
    }


    /**
     * Handles building the final query as a raw string and limits syntax errors by limiting the amount of methods that can be called.
     * @see <a href="https://www.sqlite.org/lang.html">SQLite syntax</a>
     *
     * @see InnerBuilder.build
     *
     */
    open inner class InnerBuilder internal constructor() : Resetting() {


        /**
         * Builds the query and returns a raw form as a String.
         *
         * @param appendCloser Decides whether to append a semicolon at the end or not.
         *
         * @return raw form of the query as a String.
         * @see <a href="https://www.sqlite.org/lang.html">SQLite syntax</a>
         * @see SQLiteSyntax.CLOSER
         *
         */
        fun build(appendCloser: Boolean = true): String {
            //Gets the raw query builder instance from the parent class
            val rawQueryBuilder = this@SQLiteQueryBuilder.rawQueryBuilder

            //If appendCloser is true, a semicolon is appended
            if (appendCloser) rawQueryBuilder.append(CLOSER)

            //Converts the raw query builder to a string and returns its value
            return rawQueryBuilder.toString()
        }


    }


    /**
     * Handles resetting the query building process and limits syntax errors by limiting the amount of methods that can be called.
     * @see <a href="https://www.sqlite.org/lang.html">SQLite syntax</a>
     *
     * @see Resetting.reset
     *
     */
    open inner class Resetting internal constructor() {


        /**
         * Clears query build progress and restarts from zero.
         *
         * @return The current query-builder instance.
         *
         * @see <a href="https://www.sqlite.org/lang.html">SQLite Syntax</a>
         *
         */
        fun reset(): SQLiteQueryBuilder {
            //Clears the raw query builder
            this@SQLiteQueryBuilder.rawQueryBuilder.clear()

            //Returns the current query-builder instance.
            return this@SQLiteQueryBuilder
        }

    }


    /**
     * Handles building a selection query and limits syntax errors by limiting the amount of methods that can be called.
     * @see <a href="https://www.sqlitetutorial.net/sqlite-select/">SQLite selection syntax</a>
     *
     * @see Selecting.all
     * @see Selecting.distinct
     * @see Selecting.columns
     *
     */
    open inner class Selecting internal constructor() : Resetting() {


        /**
         * Creates a selection query that will return all columns of a table.
         * @see <a href="https://www.sqlitetutorial.net/sqlite-select/">SQLite selection syntax</a>
         *
         * @return Returns selection source handler
         * @see Source
         *
         */
        open fun all(): Source = this@SQLiteQueryBuilder.appendAndSelectSource(SQLiteSyntax.ALL)



        /**
         * Creates a selection query that will return some columns in a distinct way.
         * @see <a href="https://www.sqlitetutorial.net/sqlite-select-distinct/">SQLite distinct selection syntax</a>
         *
         * @param columns The columns that should be returned by the query
         *
         * @return Query source selection handler
         * @see Source
         *
         */
        open fun distinct(columns: Array<String>? = null): Source {
            //Prepares distinct syntax depending on the given columns
            val distinctSyntax = if (columns.isNullOrEmpty()) {
                //Initializes distinctSyntax with DISTINCT keyword without specifying columns
                DISTINCT
            } else {
                //Converts columns list to SQLite elements representation such as column1, column2 etc..
                val columnsSyntax = columns.toSQLiteElements()

                //Initializes distinctSyntax with DISTINCT keyword and specifies columns
                "$DISTINCT $columnsSyntax"
            }

            //Appends distinct syntax and switches to source selection
            return this@SQLiteQueryBuilder.appendAndSelectSource(distinctSyntax)
        }


        /**
         * Creates a selection query that will return some columns.
         * @see <a href="https://www.sqlitetutorial.net/sqlite-select/">SQLite column selection syntax</a>
         *
         * @param columns The columns that should be returned by the query
         *
         * @return Query source selection handler
         *
         * @throws IllegalArgumentException if columns array is empty
         *
         * @see Source
         *
         */
        open fun columns(columns: Array<String>): Source {
            //Asserts that columns array is not empty
            require(columns.isNotEmpty())

            //Converts columns list to SQLite elements representation such as column1, column2 etc..
            val columnsSyntax = columns.toSQLiteElements()

            //Appends columns selection syntax and switches to source selection
            return this@SQLiteQueryBuilder.appendAndSelectSource(columnsSyntax)
        }


    }


    /**
     * Handles query modification such as logical statements and joints and limits syntax errors by limiting the amount of methods that can be called.
     * @see <a href="https://www.sqlitetutorial.net/sqlite-where/">Logical statements syntax</a>
     *
     * @see Modifying.where
     * @see Modifying.innerJoin
     *
     */
    open inner class Modifying internal constructor() : Sorting() {


        /**
         * Adds a where-clause (condition) to the query to filter by a specific column of the selected table.
         * @see <a href="https://www.sqlitetutorial.net/sqlite-where/">SQLite where clause syntax</a>
         *
         * @param column The column to be filtered by
         *
         * @return Query filtering handler
         * @see Filtering
         *
         */
        open fun where(column: String): Filtering {
            //Asserts that column name is not empty or blank
            require(column.isNotBlank())

            //Creates where clause syntax with the given column
            val whereClauseSyntax = "$WHERE $column"

            //Appends where clause syntax and switches to filtering
            return this@SQLiteQueryBuilder.appendAndFilter(whereClauseSyntax)
        }


        /**
         * Adds an inner-join (relation between tables) to the query to return elements that have relation by specific columns
         * @see <a href="https://www.sqlitetutorial.net/sqlite-inner-join/">SQLite inner-join syntax</a>
         *
         * @param tableName The second table that has a relationship with the first table
         *
         * @return Returns where clause building handler
         * @see Filtering
         *
         */
        open fun innerJoin(tableName: String): Filtering {
            //Asserts that table name is not empty or blank
            require(tableName.isNotBlank())

            //Creates inner join syntax using the given table name
            val innerJoinSyntax = "$INNER_JOIN $tableName $ON"

            //Appends inner join syntax and switches to query joining
            return this@SQLiteQueryBuilder.appendAndFilter(innerJoinSyntax)
        }


    }


    /**
     * Handles query filtering using equations, ranges etc.. and limits syntax errors by limiting the amount of methods that can be called.
     * @see <a href="https://www.sqlitetutorial.net/sqlite-where/">SQLite where clause syntax</a>
     *
     * @see Filtering.equalTo
     * @see Filtering.notEqualTo
     * @see Filtering.lessThan
     * @see Filtering.greaterThan
     * @see Filtering.lessOrEqualTo
     * @see Filtering.greaterOrEqualTo
     * @see Filtering.containedIn
     * @see Filtering.notContainedIn
     * @see Filtering.containedInSubQuery
     * @see Filtering.notContainedInSubQuery
     * @see Filtering.exists
     * @see Filtering.notExists
     *
     */
    open inner class Filtering internal constructor() : Resetting() {


        /**
         * Appends equality operation to where clause.
         * @see <a href="https://www.sqlitetutorial.net/sqlite-where/">SQLite where clause syntax</a>
         *
         * @param value The value that will be compared to the column's value
         *
         * @return Query statements merging handler
         * @see Merging
         *
         */
        fun equalTo(value: Any): Merging {
            //Appends equality operation syntax and returns merging handler
            return this.appendLogicalOperation(EQUAL_TO, value)
        }


        /**
         * Appends non-equality operation to where clause.
         * @see <a href="https://www.sqlitetutorial.net/sqlite-where/">SQLite where clause syntax</a>
         *
         * @param value The value that will be compared to the column's value
         *
         * @return Query statements merging handler
         * @see Merging
         *
         */
        fun notEqualTo(value: Any): Merging {
            //Appends non-equality operation syntax and returns merging handler
            return this.appendLogicalOperation(NOT_EQUAL_TO, value)
        }


        /**
         * Appends non-priority operation to where clause.
         * @see <a href="https://www.sqlitetutorial.net/sqlite-where/">SQLite where clause syntax</a>
         *
         * @param value The value that will be compared to the column's value
         *
         * @return Query statements merging handler
         * @see Merging
         *
         */
        fun lessThan(value: Any): Merging {
            //Appends non-priority operation syntax and returns merging handler
            return this.appendLogicalOperation(LESS_THAN, value)
        }


        /**
         * Appends priority operation to where clause.
         * @see <a href="https://www.sqlitetutorial.net/sqlite-where/">SQLite where clause syntax</a>
         *
         * @param value The value that will be compared to the column's value
         *
         * @return Query statements merging handler
         * @see Merging
         *
         */
        fun greaterThan(value: Any): Merging {
            //Appends priority operation syntax and returns merging handler
            return this.appendLogicalOperation(GREATER_THAN, value)
        }


        /**
         * Appends equality or non-priority operation to where clause.
         * @see <a href="https://www.sqlitetutorial.net/sqlite-where/">SQLite where clause syntax</a>
         *
         * @param value The value that will be compared to the column's value
         *
         * @return Query statements merging handler
         * @see Merging
         *
         */
        fun lessOrEqualTo(value: Any): Merging {
            //Appends equality or non-priority operation syntax and returns merging handler
            return this.appendLogicalOperation(LESS_THAN_OR_EQUAL_TO, value)
        }


        /**
         * Appends equality or priority operation to where clause.
         * @see <a href="https://www.sqlitetutorial.net/sqlite-where/">SQLite where clause syntax</a>
         *
         * @param value The value that will be compared to the column's value
         *
         * @return Query statements merging handler
         * @see Merging
         *
         */
        fun greaterOrEqualTo(value: Any): Merging {
            //Appends equality or priority operation syntax and returns merging handler
            return this.appendLogicalOperation(GREATER_THAN_OR_EQUAL_TO, value)
        }


        /**
         * Appends containing operation to where clause.
         * @see <a href="https://www.sqlitetutorial.net/sqlite-where/">SQLite where clause syntax</a>
         *
         * @param values Values list that will be checked whether it contains the value of the given column
         *
         * @return Query statements merging handler
         * @see Merging
         *
         */
        fun containedIn(values: Array<Any>): Merging {
            //Asserts that the given list of values is not empty
            require(values.isNotEmpty())

            //Converts values list to SQLite elements representation such as value1, value2 etc..
            val valuesSyntax = values.toContainedSQLiteElements()

            //Appends containing operation syntax and returns merging handler
            return this.appendLogicalOperation(IN, valuesSyntax)
        }


        /**
         * Appends non-containing operation to where clause.
         * @see <a href="https://www.sqlitetutorial.net/sqlite-where/">SQLite where clause syntax</a>
         *
         * @param values Values list that will be checked whether it doesn't contain the value of the given column
         *
         * @return Query statements merging handler
         * @see Merging
         *
         */
        fun notContainedIn(values: Array<Any>): Merging {
            //Asserts that the given list of values is not empty
            require(values.isNotEmpty())

            //Converts values list to SQLite elements representation such as value1, value2 etc..
            val valuesSyntax = values.toContainedSQLiteElements()

            //Creates non-containing operation syntax
            val notInSyntax = "$NOT $IN"

            //Appends non-containing operation syntax and returns merging handler
            return this.appendLogicalOperation(notInSyntax, valuesSyntax)
        }


        /**
         * Appends containing operation to where clause.
         * @see <a href="https://www.sqlitetutorial.net/sqlite-where/">SQLite where clause syntax</a>
         *
         * @param subQuery The query that returns a list result which will be checked whether it contain the value of the given column
         *
         * @return Query statements merging handler
         * @see Merging
         *
         */
        fun containedInSubQuery(subQuery: String): Merging {
            //Asserts that the given subQuery is not blank or empty
            require(subQuery.isNotBlank())

            //Creates subQuery syntax using the given value
            val subQuerySyntax = "$OPEN_PARENTHESES $subQuery $CLOSE_PARENTHESES"

            //Appends containing operation syntax and returns merging handler
            return this.appendLogicalOperation(IN, subQuerySyntax)
        }


        /**
         * Appends non-containing operation to where clause.
         * @see <a href="https://www.sqlitetutorial.net/sqlite-where/">SQLite where clause syntax</a>
         *
         * @param subQuery The query that returns a list result which will be checked whether it doesn't contain the value of the given column
         *
         * @return Query statements merging handler
         * @see Merging
         *
         */
        fun notContainedInSubQuery(subQuery: String): Merging {
            //Asserts that the given subQuery is not blank or empty
            require(subQuery.isNotBlank())

            //Creates subQuery syntax using the given value
            val subQuerySyntax = "$OPEN_PARENTHESES $subQuery $CLOSE_PARENTHESES"

            //Creates non-containing operation syntax
            val notInSyntax = "$NOT $IN"

            //Appends non-containing operation syntax and returns merging handler
            return this.appendLogicalOperation(notInSyntax, subQuerySyntax)
        }


        /**
         * Appends search operation to where clause.
         * @see <a href="https://www.sqlitetutorial.net/sqlite-where/">SQLite where clause syntax</a>
         *
         * @param value The value that will be matched with the value of the given column
         *
         * @return Query statements merging handler
         * @see Merging
         *
         */
        fun like(value: Any): Merging {
            //Appends search operation syntax and returns merging handler
            return this.appendLogicalOperation(LIKE, value)
        }


        /**
         * Appends reversed-search operation to where clause.
         * @see <a href="https://www.sqlitetutorial.net/sqlite-where/">SQLite where clause syntax</a>
         *
         * @param value The value that should not be matched with the value of the given column
         *
         * @return Query statements merging handler
         * @see Merging
         *
         */
        fun notLike(value: Any): Merging {
            //Creates reversed search operation syntax
            val notLikeSyntax = "$NOT $LIKE"

            //Appends reversed-search operation syntax and returns merging handler
            return this.appendLogicalOperation(notLikeSyntax, value)
        }


        /**
         * Appends existence operation to where clause.
         * @see <a href="https://www.sqlitetutorial.net/sqlite-where/">SQLite where clause syntax</a>
         *
         * @param subQuery The query that will be checked for existence of the the given column
         *
         * @return Query statements merging handler
         * @see Merging
         *
         */
        fun exists(subQuery: String): Merging {
            //Asserts that the given subQuery is not blank or empty
            require(subQuery.isNotBlank())

            //Creates subQuery syntax using the given value
            val subQuerySyntax = "$OPEN_PARENTHESES $subQuery $CLOSE_PARENTHESES"

            //Appends existence operation syntax and returns merging handler
            return this.appendLogicalOperation(EXISTS, subQuerySyntax)
        }


        /**
         * Appends non-existence operation to where clause.
         * @see <a href="https://www.sqlitetutorial.net/sqlite-where/">SQLite where clause syntax</a>
         *
         * @param subQuery The query that will be checked for non-existence of the the given column
         *
         * @return Query statements merging handler
         * @see Merging
         *
         */
        fun notExists(subQuery: String): Merging {
            //Asserts that the given subQuery is not blank or empty
            require(subQuery.isNotBlank())

            //Creates subQuery syntax using the given value
            val subQuerySyntax = "$OPEN_PARENTHESES $subQuery $CLOSE_PARENTHESES"

            //Creates non-existence query syntax
            val notExistsSyntax = "$NOT $EXISTS"

            //Appends existence operation syntax and returns merging handler
            return this.appendLogicalOperation(notExistsSyntax, subQuerySyntax)
        }

        fun between(firstValue: Any, secondValue: Any): Merging {
            val rangeSyntax = "$firstValue $AND $secondValue"
            return this.appendLogicalOperation(BETWEEN, rangeSyntax)
        }

        fun notBetween(firstValue: Any, secondValue: Any): Merging {
            val rangeSyntax = "$firstValue $AND $secondValue"
            val notBetweenSyntax = "$NOT $BETWEEN"
            return this.appendLogicalOperation(notBetweenSyntax, rangeSyntax)
        }

        fun isNull(): Merging {
            val isNullSyntax = "$IS $NULL"
            return this@SQLiteQueryBuilder.appendAndMerge(isNullSyntax)
        }

        fun isNotNull(): Merging {
            val isNotNullSyntax = "$IS $NOT $NULL"
            return this@SQLiteQueryBuilder.appendAndMerge(isNotNullSyntax)
        }

        protected open fun appendLogicalOperation(operator: String, value: Any): Merging {
            val whereClauseSyntax = "$operator $value"
            return this@SQLiteQueryBuilder.appendAndMerge(whereClauseSyntax)
        }

    }

    open inner class Joining internal constructor() : Resetting() {

    }

    open inner class Merging internal constructor() : Sorting() {

        fun and(column: String): Filtering {
            val andSyntax = "$AND $column"
            return this@SQLiteQueryBuilder.appendAndFilter(andSyntax)
        }

        fun or(column: String): Filtering {
            val orSyntax = "$OR $column"
            return this@SQLiteQueryBuilder.appendAndFilter(orSyntax)
        }

    }


    open inner class Sorting internal constructor() : Quantifying() {

        fun orderBy(column: String, direction: SQLiteQueryDirection = SQLiteQueryDirection.ASCENDING): SubSorting {
            val orderBySyntax = "$ORDER_BY $column ${direction.rawValue}"
            return this@SQLiteQueryBuilder.appendAndSubSort(orderBySyntax)
        }

        fun orderNullsLast(column: String): SubSorting {
            val orderNullsLastSyntax = "$ORDER_BY $column $NULLS_LAST"
            return this@SQLiteQueryBuilder.appendAndSubSort(orderNullsLastSyntax)
        }

        fun orderNullsFirst(column: String): SubSorting {
            val orderNullsLastSyntax = "$ORDER_BY $column $NULLS_FIRST"
            return this@SQLiteQueryBuilder.appendAndSubSort(orderNullsLastSyntax)
        }

    }


    open inner class SubSorting internal constructor() : Quantifying() {

        fun andOrderBy(field: String, direction: SQLiteQueryDirection = SQLiteQueryDirection.ASCENDING): SubSorting = this.apply {
            val andOrderBySyntax = "$ELEMENT_SEPARATOR $field ${direction.rawValue}"
            this@SQLiteQueryBuilder.append(andOrderBySyntax)
        }

        fun andOrderNullsLast(column: String): SubSorting = this.apply {
            val orderNullsLastSyntax = "$ELEMENT_SEPARATOR $column $NULLS_LAST"
            this@SQLiteQueryBuilder.append(orderNullsLastSyntax)
        }

        fun andOrderNullsFirst(column: String): SubSorting = this.apply {
            val orderNullsLastSyntax = "$ELEMENT_SEPARATOR $column $NULLS_FIRST"
            this@SQLiteQueryBuilder.append(orderNullsLastSyntax)
        }

    }


    open inner class Quantifying internal constructor() : Skipping() {

        fun limit(limit: Number): Skipping {
            val limitSyntax = "$LIMIT $limit"
            return this@SQLiteQueryBuilder.appendAndSkip(limitSyntax)
        }

    }


    open inner class Skipping internal constructor() : InnerBuilder() {

        fun offset(offset: Number): InnerBuilder = this.apply {
            val offsetSyntax = "$OFFSET $offset"
            this@SQLiteQueryBuilder.append(offsetSyntax)
        }

    }


    /**
     * Handles building a selection source query and limits syntax errors by limiting the amount of methods that can be called
     * @see <a href="https://www.sqlitetutorial.net/sqlite-select/">SQLite 'SELECT column1, column2 FROM table' expression</a>
     *
     */
    open inner class Source internal constructor() : Resetting() {


        /**
         *  Creates a selection source that will return columns or data from a specific table
         *  @see <a href="https://www.sqlitetutorial.net/sqlite-select/">SQLite 'SELECT column1, column2 FROM table' expression</a>
         *
         *  @param table The name of the table containing data
         *
         *  @return Returns query modification handler
         *  @see Modifying
         *
         */
        open fun fromTable(table: String): Modifying {
            require(table.isNotBlank())
            val fromTableSyntax = "$FROM $table"
            return this@SQLiteQueryBuilder.appendAndModify(fromTableSyntax)
        }


        /**
         *  Creates a selection source that will return columns or data from the result of another query
         *  @see <a href="https://www.sqlitetutorial.net/sqlite-subquery/">SQLite 'SELECT * FROM (sub-query)' expression</a>
         *
         *  @param subQuery The sub-query that contains our required data
         *
         *  @return Returns query modification handler
         *  @see Modifying
         *
         */
        open fun fromSubQuery(subQuery: String): Modifying {
            require(subQuery.isNotBlank())
            val fromSubQuerySyntax = "$FROM $OPEN_PARENTHESES $subQuery $CLOSE_PARENTHESES"
            return this@SQLiteQueryBuilder.appendAndModify(fromSubQuerySyntax)
        }
    }
}
