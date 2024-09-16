package com.aol.philipphofer.logic.observer

open class Observable<T> {

    private val listeners: MutableList<Listener<T>> = ArrayList()

    protected fun notifyListeners() {
        listeners.forEach { l -> l.observableChanged(this) }
    }

    fun addListener(listener: Listener<T>) {
        listeners.add(listener)
    }

}