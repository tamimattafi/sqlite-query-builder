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
     * @see <a href="https://www.sqlitetutorial.net/sqlite-select/">SQLite SELECT keyword</a>
     *
     */
    open fun select(): Selecting {
        this.appendRawSyntax(SELECT)
        return this.beginSelection()
    }

    /**
     * Appends a raw SQLite syntax or query to the current query that's being built
     * @see <a href="https://www.sqlite.org/lang.html">SQLite Syntax</a>
     *
     */
    open fun appendRawSyntax(syntax: String) = this.apply {
        require(syntax.isNotBlank())
        this.rawQueryBuilder.append("\n").append(syntax).append(SEPARATOR)
    }


    /**
     * Appends a raw syntax to the selection process. Raw syntax is not filtered or checked for errors
     * @see <a href="https://www.sqlitetutorial.net/sqlite-select/">SQLite 'SELECT (raw-syntax)' expression</a>
     *
     * @param syntax The raw syntax to be appended
     *
     * @return Returns selection source handler
     * @see Source
     *
     */
    protected open fun appendAndSelectSource(syntax: String): Source {
        this.appendRawSyntax(syntax)
        return this.beginSourceSelection()
    }

    /**
     * Appends a raw syntax to the selection source process. Raw syntax is not filtered or checked for errors
     * @see <a href="https://www.sqlitetutorial.net/sqlite-subquery/">SQLite 'SELECT * FROM (raw-syntax)' expression</a>
     *
     * @param syntax The raw syntax to be appended
     *
     * @return Returns query modification handler
     * @see Modifying
     *
     */
    protected open fun appendAndModify(syntax: String): Modifying {
        this.appendRawSyntax(syntax)
        return this.modify()
    }


    protected open fun appendAndFilter(syntax: String): Filtering {
        this.appendRawSyntax(syntax)
        return this.filter()
    }


    protected open fun appendAndMerge(syntax: String): Merging {
        this.appendRawSyntax(syntax)
        return this.merge()
    }

    protected open fun appendAndSort(syntax: String): Sorting {
        this.appendRawSyntax(syntax)
        return this.sort()
    }

    protected open fun appendAndSubSort(syntax: String): SubSorting {
        this.appendRawSyntax(syntax)
        return this.subSort()
    }

    protected open fun appendAndQuantify(syntax: String): Quantifying {
        this.appendRawSyntax(syntax)
        return this.quantify()
    }

    protected open fun appendAndSkip(syntax: String): Skipping {
        this.appendRawSyntax(syntax)
        return this.skip()
    }

    open inner class InnerBuilder internal constructor() : Resetting() {
        fun build(appendCloser: Boolean = true): String {
            val rawQueryBuilder = this@SQLiteQueryBuilder.rawQueryBuilder
            if (appendCloser) rawQueryBuilder.append(CLOSER)
            return rawQueryBuilder.toString()
        }
    }

    open inner class Resetting internal constructor() {
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
    open inner class Modifying internal constructor() : InnerBuilder() {


        /**
         * Adds a where-clause (condition) to the query to filter by a specific column of the selected table
         * @see <a href="https://www.sqlitetutorial.net/sqlite-where/">SQLite 'SELECT * FROM table WHERE (condition)' expression</a>
         *
         * @param column The column to be filtered by
         *
         * @return Returns where clause building handler
         * @see Filtering
         *
         */
        open fun where(column: String): Filtering {
            require(column.isNotBlank())
            val whereClauseSyntax = "$WHERE $column"
            return this@SQLiteQueryBuilder.appendAndFilter(whereClauseSyntax)
        }

        open fun innerJoin(tableName: String): Filtering {
            require(tableName.isNotBlank())
            val innerJoinSyntax = "$INNER_JOIN $tableName $ON"
            return this@SQLiteQueryBuilder.appendAndFilter(innerJoinSyntax)
        }

        open fun orderBy(column: String) {

        }

        open fun limit(limit: Long) {

        }


    }

    open inner class Filtering internal constructor() : Resetting() {

        fun equalTo(value: Any): Merging
            = this.whereClause(EQUAL_TO, value)

        fun notEqualTo(value: Any): Merging
            = this.whereClause(NOT_EQUAL_TO, value)

        fun lessThan(value: Any): Merging
            = this.whereClause(LESS_THAN, value)

        fun greaterThan(value: Any): Merging
            = this.whereClause(GREATER_THAN, value)

        fun lessOrEqualTo(value: Any): Merging
            = this.whereClause(LESS_THAN_OR_EQUAL_TO, value)

        fun greaterOrEqualTo(value: Any): Merging
            = this.whereClause(GREATER_THAN_OR_EQUAL_TO, value)

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

        fun and(field: String): Filtering
            = this@SQLiteQueryBuilder.appendAndFilter(AND)

        fun or(field: String): Filtering
            = this@SQLiteQueryBuilder.appendAndFilter(OR)

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
            this@SQLiteQueryBuilder.appendRawSyntax(andOrderBySyntax)
        }

        fun andOrderNullsLast(column: String): SubSorting = this.apply {
            val orderNullsLastSyntax = "$ELEMENT_SEPARATOR $column $NULLS_LAST"
            this@SQLiteQueryBuilder.appendRawSyntax(orderNullsLastSyntax)
        }

        fun andOrderNullsFirst(column: String): SubSorting = this.apply {
            val orderNullsLastSyntax = "$ELEMENT_SEPARATOR $column $NULLS_FIRST"
            this@SQLiteQueryBuilder.appendRawSyntax(orderNullsLastSyntax)
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
            this@SQLiteQueryBuilder.appendRawSyntax(offsetSyntax)
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