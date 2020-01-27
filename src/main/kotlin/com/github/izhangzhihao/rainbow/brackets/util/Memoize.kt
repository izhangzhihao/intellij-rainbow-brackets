package com.github.izhangzhihao.rainbow.brackets.util

private class Memoize1<in A, out R>(val f: (A) -> R) : (A) -> R {
    private val values = mutableMapOf<A, R>()
    override fun invoke(a: A): R {
        return values.getOrPut(a, { f(a) })
    }
}

fun <A, R> ((A) -> R).memoize(): (A) -> R = Memoize1(this)