package com.drahovac.weatherstationdisplay.android.ui

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

/**
 * Use in compose previews for dynamic implementation of actions interface
 */
class ActionsInvocationHandler : InvocationHandler {

    override operator fun invoke(proxy: Any?, method: Method, args: Array<Any?>?): Any {
        return Unit
    }

    companion object {
        inline fun <reified T> createActionsProxy(): T {
            return Proxy.newProxyInstance(
                T::class.java.classLoader,
                arrayOf<Class<*>>(T::class.java),
                ActionsInvocationHandler()
            ) as T
        }
    }
}
