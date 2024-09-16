package com.aol.philipphofer.logic.observer

interface Listener<T> {

    fun observableChanged(observable: Observable<T>);
}