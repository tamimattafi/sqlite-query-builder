package com.tamimattafi.sqlite.query

object SQLiteQueryUtils {


    /**
     * Transfers this array to sqlite elements separated by a comma (','). This can be used inside sqlite arrays, or selection columns etc..
     * @return Returns a string that contains elements of this array joined by a comma (','). For example, ("James", "Carl", "Hannah") will be
     * look like "James, Carl, Hannah"
     *
     */
    fun Array<String>.toSQLiteElements(): String = joinToString(SQLiteQueryBuilder.Syntax.ELEMENT_SEPARATOR)


}