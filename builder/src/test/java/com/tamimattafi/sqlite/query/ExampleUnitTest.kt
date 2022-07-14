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
            .where("isCredit")
            .equalTo(0)
            .andStartPriority("symbol")
            .containedIn(arrayOf("USDT", "BNB"))
            .or("network")
            .containedIn(arrayOf("ERC20", "BEP20"))
            .endPriority()
            .andStartPriority("amount")
            .like("12.00")
            .and("name")
            .like("Allah akbar")
            .endPriority()
            .or("price")
            .equalTo(1)
            .orderBy("symbol")
            .limit(10)
            .build()

        println(rawQueryBuilder)
    }

}