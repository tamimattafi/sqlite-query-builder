---
title: toSQLiteElements -
---
//[library](../../index.md)/[com.tamimattafi.sqlite.query](../index.md)/[SQLiteQueryUtils](index.md)/[toSQLiteElements](to-s-q-lite-elements.md)



# toSQLiteElements  
[androidJvm]  
Content  
fun [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)>.[toSQLiteElements](to-s-q-lite-elements.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  
More info  


Transfers this array to sqlite elements separated by a comma (','). This can be used inside sqlite arrays, or selection columns etc..



#### Return  


Returns a string that contains elements of this array joined by a comma (','). For example, ("James", "Carl", "Hannah") will be look like "James, Carl, Hannah"

  



