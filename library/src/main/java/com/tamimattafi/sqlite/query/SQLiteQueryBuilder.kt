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


    protected open var selecting: WeakReference<Selecting>? = null
    protected open var modifying: WeakReference<Modifying>? = null
    protected open var sourceSelection: WeakReference<Source>? = null
    protected open var filtering: WeakReference<Filtering>? = null
    protected open var merging: WeakReference<Merging>? = null
    protected open var sorting: WeakReference<Sorting>? = null
    protected open var subSorting: WeakReference<SubSorting>? = null
    protected open var quantifying: WeakReference<Quantifying>? = null
    protected open var skipping: WeakReference<Skipping>? = null


    /**
     * Starts building a selection query to read lists, columns or single rows from a table
     *
     * @return Selection type handler
     *
     * @see Selecting
     * @see <a href="https://www.sqlitetutorial.net/sqlite-select/">SQLite Selection Syntax</a>
     *
     */
    open fun select(): Selecting {
        //Appends "SELECT" keyword
        this.append(SELECT)

        //Creates (Lazy) and returns selection type handler
        return this.beginSelection()
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
     * @see <a href="https://www.sqlite.org/lang.html">SQLite Syntax</a>
     *
     */
    open fun append(syntax: String) = this.apply {
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
     * @see <a href="https://www.sqlitetutorial.net/sqlite-select/">SQLite Selection Syntax</a>
     *
     */
    open fun appendAndSelectSource(syntax: String): Source {
        //Appends the raw syntax and a space at the end
        this.append(syntax)

        //Creates (Lazy) and returns source selection handler
        return this.beginSourceSelection()
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
     * @see <a href="https://www.sqlitetutorial.net/sqlite-inner-join/">SQLite Inner Join</a>
     *
     */
    open fun appendAndModify(syntax: String): Modifying {
        //Appends the raw syntax and a space at the end
        this.append(syntax)

        //Creates (Lazy) and returns modification handler
        return this.modify()
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
     * @see <a href="https://www.sqlitetutorial.net/sqlite-where/">SQLite Where Clause</a>
     *
     */
    open fun appendAndFilter(syntax: String): Filtering {
        //Appends the raw syntax and a space at the end
        this.append(syntax)

        //Creates (Lazy) and returns filtering handler
        return this.filter()
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
     * @see <a href="https://www.tutorialspoint.com/sqlite/sqlite_and_or_clauses.htm">SQLite AND/OR Operators</a>
     *
     */
    open fun appendAndMerge(syntax: String): Merging {
        //Appends the raw syntax and a space at the end
        this.append(syntax)

        //Creates (Lazy) and returns merging handler
        return this.merge()
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
     * @see <a href="https://www.sqlitetutorial.net/sqlite-order-by/">SQLite Ordering syntax</a>
     *
     */
    open fun appendAndSort(syntax: String): Sorting {
        //Appends the raw syntax and a space at the end
        this.append(syntax)

        //Creates (Lazy) and returns merging handler
        return this.sort()
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
     * @see <a href="https://www.sqlitetutorial.net/sqlite-order-by/">SQLite Ordering syntax</a>
     *
     */
    open fun appendAndSubSort(syntax: String): SubSorting {
        //Appends the raw syntax and a space at the end
        this.append(syntax)

        //Creates (Lazy) and returns merging handler
        return this.subSort()
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
     * @see <a href="https://www.sqlitetutorial.net/sqlite-limit/">SQLite Limiting syntax</a>
     *
     */
    open fun appendAndQuantify(syntax: String): Quantifying {
        //Appends the raw syntax and a space at the end
        this.append(syntax)

        //Creates (Lazy) and returns merging handler
        return this.quantify()
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
     * @see <a href="https://www.sqlitetutorial.net/sqlite-limit/">SQLite Offset syntax</a>
     *
     */
    open fun appendAndSkip(syntax: String): Skipping {
        //Appends the raw syntax and a space at the end
        this.append(syntax)

        //Creates (Lazy) and returns merging handler
        return this.skip()
    }


    /**
     * Handles building the final query as a raw string and limits syntax errors by limiting the amount of methods that can be called.
     * @see <a href="https://www.sqlite.org/lang.html">SQLite Syntax</a>
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
         * @see <a href="https://www.sqlite.org/lang.html">SQLite Syntax</a>
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
     * @see <a href="https://www.sqlite.org/lang.html">SQLite Syntax</a>
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
            this@SQLiteQueryBuilder.rawQueryBuilder.clear()
            return this@SQLiteQueryBuilder
        }
    }


    /**
     * Handles building a selection query and limits syntax errors by limiting the amount of methods that can be called
     * @see <a href="https://www.sqlitetutorial.net/sqlite-select/">SQLite SELECT keyword</a>
     * @see select
     *
     */
    open inner class Selecting internal constructor() : Resetting() {


        /**
         * Creates a selection query that will return all columns of a table
         * @see <a href="https://www.sqlitetutorial.net/sqlite-select/">SQLite 'SELECT *' expression</a>
         *
         * @return Returns selection source handler
         * @see Source
         *
         */
        open fun all(): Source = this@SQLiteQueryBuilder.appendAndSelectSource(SQLiteSyntax.ALL)



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
        open fun distinct(columns: Array<String>? = null): Source {
            val distinctSyntax = if (columns.isNullOrEmpty()) {
                DISTINCT
            } else {
                val columnsSyntax = columns.toSQLiteElements()
                "$DISTINCT $columnsSyntax"
            }

            return this@SQLiteQueryBuilder.appendAndSelectSource(distinctSyntax)
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
        open fun columns(columns: Array<String>): Source {
            require(columns.isNotEmpty())
            val columnsSyntax = columns.toSQLiteElements()
            return this@SQLiteQueryBuilder.appendAndSelectSource(columnsSyntax)
        }


    }


    /**
     * Handles query modification such as logical statements, joints, direction etc..
     *
     */
    open inner class Modifying internal constructor() : Sorting() {


        /**
         * Adds a where-clause (condition) to the query to filter by a specific column of the selected table
         * @see <a href="https://www.sqlitetutorial.net/sqlite-where/">SQLite 'SELECT * FROM table WHERE (condition)' expression</a>
         *
         * @param column The column to be filtered by
         *
         * @return Returns filtering handler
         * @see Filtering
         *
         */
        open fun where(column: String): Filtering {
            require(column.isNotBlank())
            val whereClauseSyntax = "$WHERE $column"
            return this@SQLiteQueryBuilder.appendAndFilter(whereClauseSyntax)
        }


        /**
         * Adds an inner-join (relation between tables) to the query to return elements that have relation by specific columns
         * @see <a href="https://www.sqlitetutorial.net/sqlite-inner-join/">SQLite 'SELECT * FROM table INNER JOIN table2 ON table2.parentId = table.id' expression</a>
         *
         * @param tableName The second table that has a relationship with the first table
         *
         * @return Returns where clause building handler
         * @see Filtering
         *
         */
        open fun innerJoin(tableName: String): Filtering {
            require(tableName.isNotBlank())
            val innerJoinSyntax = "$INNER_JOIN $tableName $ON"
            return this@SQLiteQueryBuilder.appendAndFilter(innerJoinSyntax)
        }


    }

    open inner class Filtering internal constructor() : Resetting() {

        fun equalTo(value: Any): Merging {
            return this.whereClause(EQUAL_TO, value)
        }

        fun notEqualTo(value: Any): Merging {
            return this.whereClause(NOT_EQUAL_TO, value)
        }

        fun lessThan(value: Any): Merging {
            return this.whereClause(LESS_THAN, value)
        }

        fun greaterThan(value: Any): Merging {
            return this.whereClause(GREATER_THAN, value)
        }

        fun lessOrEqualTo(value: Any): Merging {
            return this.whereClause(LESS_THAN_OR_EQUAL_TO, value)
        }

        fun greaterOrEqualTo(value: Any): Merging {
            return this.whereClause(GREATER_THAN_OR_EQUAL_TO, value)
        }

        fun containedIn(values: Array<Any>): Merging {
            require(values.isNotEmpty())
            val valuesSyntax = values.toContainedSQLiteElements()
            return whereClause(IN, valuesSyntax)
        }

        fun notContainedIn(values: Array<Any>): Merging {
            require(values.isNotEmpty())
            val valuesSyntax = values.toContainedSQLiteElements()
            val notInSyntax = "$NOT $IN"
            return whereClause(notInSyntax, valuesSyntax)
        }

        fun containedInSubQuery(subQuery: String): Merging {
            require(subQuery.isNotBlank())
            val subQuerySyntax = "$OPEN_PARENTHESES $subQuery $CLOSE_PARENTHESES"
            return whereClause(IN, subQuerySyntax)
        }

        fun notContainedInSubQuery(subQuery: String): Merging {
            require(subQuery.isNotBlank())
            val subQuerySyntax = "$OPEN_PARENTHESES $subQuery $CLOSE_PARENTHESES"
            val notInSyntax = "$NOT $IN"
            return whereClause(notInSyntax, subQuerySyntax)
        }

        fun like(value: Any): Merging
            = this.whereClause(LIKE, value)

        fun notLike(value: Any): Merging {
            val notLikeSyntax = "$NOT $LIKE"
            return this.whereClause(notLikeSyntax, value)
        }

        fun exists(subQuery: String): Merging {
            require(subQuery.isNotBlank())
            val subQuerySyntax = "$OPEN_PARENTHESES $subQuery $CLOSE_PARENTHESES"
            return this.whereClause(EXISTS, subQuerySyntax)
        }

        fun notExists(subQuery: String): Merging {
            require(subQuery.isNotBlank())
            val subQuerySyntax = "$OPEN_PARENTHESES $subQuery $CLOSE_PARENTHESES"
            val notExistsSyntax = "$NOT $EXISTS"
            return this.whereClause(notExistsSyntax, subQuerySyntax)
        }

        fun between(firstValue: Any, secondValue: Any): Merging {
            val rangeSyntax = "$firstValue $AND $secondValue"
            return this.whereClause(BETWEEN, rangeSyntax)
        }

        fun notBetween(firstValue: Any, secondValue: Any): Merging {
            val rangeSyntax = "$firstValue $AND $secondValue"
            val notBetweenSyntax = "$NOT $BETWEEN"
            return this.whereClause(notBetweenSyntax, rangeSyntax)
        }

        fun isNull(): Merging {
            val isNullSyntax = "$IS $NULL"
            return this@SQLiteQueryBuilder.appendAndMerge(isNullSyntax)
        }

        fun isNotNull(): Merging {
            val isNotNullSyntax = "$IS $NOT $NULL"
            return this@SQLiteQueryBuilder.appendAndMerge(isNotNullSyntax)
        }

        protected open fun whereClause(operator: String, value: Any): Merging {
            val whereClauseSyntax = "$operator $value"
            return this@SQLiteQueryBuilder.appendAndMerge(whereClauseSyntax)
        }

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


    //TODO: refactor all these switch methods
    protected open fun beginSelection(): Selecting {
        var selecting = this.selecting?.get()

        if (selecting == null) {
            selecting = Selecting()
            this.selecting = WeakReference(selecting)
        }

        return selecting
    }


    protected open fun modify(): Modifying {
        var modifying = this.modifying?.get()

        if (modifying == null) {
            modifying = Modifying()
            this.modifying = WeakReference(modifying)
        }

        return modifying
    }


    protected open fun beginSourceSelection(): Source {
        var sourceSelection = this.sourceSelection?.get()

        if (sourceSelection == null) {
            sourceSelection = Source()
            this.sourceSelection = WeakReference(sourceSelection)
        }

        return sourceSelection
    }


    protected open fun filter(): Filtering {
        var filtering = this.filtering?.get()

        if (filtering == null) {
            filtering = Filtering()
            this.filtering = WeakReference(filtering)
        }

        return filtering
    }


    protected open fun merge(): Merging {
        var merging = this.merging?.get()

        if (merging == null) {
            merging = Merging()
            this.merging = WeakReference(merging)
        }

        return merging
    }


    protected open fun sort(): Sorting {
        var sorting = this.sorting?.get()

        if (sorting == null) {
            sorting = Sorting()
            this.sorting = WeakReference(sorting)
        }

        return sorting
    }

    protected open fun subSort(): SubSorting {
        var subSorting = this.subSorting?.get()

        if (subSorting == null) {
            subSorting = SubSorting()
            this.subSorting = WeakReference(subSorting)
        }

        return subSorting
    }


    protected open fun quantify(): Quantifying {
        var quantifying = this.quantifying?.get()

        if (quantifying == null) {
            quantifying = Quantifying()
            this.quantifying = WeakReference(quantifying)
        }

        return quantifying
    }


    protected open fun skip(): Skipping {
        var skipping = this.skipping?.get()

        if (skipping == null) {
            skipping = Skipping()
            this.skipping = WeakReference(skipping)
        }

        return skipping
    }

}