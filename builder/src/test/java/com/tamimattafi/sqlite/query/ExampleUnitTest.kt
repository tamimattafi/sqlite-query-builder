package com.tamimattafi.sqlite.query

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun addition_isCorrect() {
        val rawQueryBuilder = SQLiteQueryBuilder()
                .select()
                .all()
                .fromTable("users")

        rawQueryBuilder
            .where("lol")
            .equalTo("Nice")

        rawQueryBuilder
            .where("lol", SQLiteQueryBuilder.Merging::and)
            .equalTo("Nice")

        val rawQuery = rawQueryBuilder.build()
        print("Raw query: $rawQuery")

        assertTrue(rawQuery.contains("AND"))
    }

}