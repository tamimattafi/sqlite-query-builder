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
        val rawQuery = SQLiteQueryBuilder()
                .select()
                .all()
                .fromTable("users")
                .where("age")
                .between(12, 18)
                .and("status")
                .equalTo("pupil")
                .or("status")
                .equalTo("student")
                .orderBy("age")
                .andOrderBy("status")
                .andOrderNullsFirst("middleName")
                .andOrderNullsLast("fatherName")
                .limit(20)
                .offset(10)
                .reset()
                .select()
                .all()
                .fromTable("users")
                .where("age")
                .between(12, 18)
                .build()

        print(rawQuery)
    }

}