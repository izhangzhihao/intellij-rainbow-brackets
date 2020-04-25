package com.github.izhangzhihao.rainbow.brackets.util

import java.util.concurrent.ConcurrentHashMap

fun <A, R> ((A) -> R).memoize(): (A) -> R = object : (A) -> R {
    private val m = MemoizedHandler<((A) -> R), MemoizeKey1<A, R>, R>(this@memoize)
    override fun invoke(a: A) = m(MemoizeKey1(a))
}

fun <A, B, R> ((A, B) -> R).memoize(): (A, B) -> R = object : (A, B) -> R {
    private val m = MemoizedHandler<((A, B) -> R), MemoizeKey2<A, B, R>, R>(this@memoize)
    override fun invoke(a: A, b: B) = m(MemoizeKey2(a, b))
}

fun <A, B, C, R> ((A, B, C) -> R).memoize(): (A, B, C) -> R = object : (A, B, C) -> R {
    private val m = MemoizedHandler<((A, B, C) -> R), MemoizeKey3<A, B, C, R>, R>(this@memoize)
    override fun invoke(a: A, b: B, c: C) = m(MemoizeKey3(a, b, c))
}


private interface MemoizedCall<in F, out R> {
    operator fun invoke(f: F): R
}

private class MemoizedHandler<F, in K : MemoizedCall<F, R>, out R>(val f: F) {
    private val m = Platform.newConcurrentMap<K, R>()
    operator fun invoke(k: K): R = m[k] ?: run { m.putSafely(k, k(f)) }
}

private data class MemoizeKey1<out A, R>(val a: A) : MemoizedCall<(A) -> R, R> {
    override fun invoke(f: (A) -> R) = f(a)
}

private data class MemoizeKey2<out A, out B, R>(val a: A, val b: B) : MemoizedCall<(A, B) -> R, R> {
    override fun invoke(f: (A, B) -> R) = f(a, b)
}

private data class MemoizeKey3<out A, out B, out C, R>(val a: A, val b: B, val c: C) : MemoizedCall<(A, B, C) -> R, R> {
    override fun invoke(f: (A, B, C) -> R) = f(a, b, c)
}

object Platform {

    interface ConcurrentMap<K, V> : MutableMap<K, V> {
        fun putSafely(k: K, v: V): V
    }

    fun <K, V> newConcurrentMap(): ConcurrentMap<K, V> {
        val map by lazy { ConcurrentHashMap<K, V>() }
        return object : ConcurrentMap<K, V>, MutableMap<K, V> by map {
            override fun putSafely(k: K, v: V): V =
                    map.putIfAbsent(k, v) ?: v
        }
    }
}