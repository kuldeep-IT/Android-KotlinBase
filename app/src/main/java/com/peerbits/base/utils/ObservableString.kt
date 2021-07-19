package com.nlgic.insurance.utils

import androidx.databinding.BaseObservable

class ObservableString : BaseObservable() {
    private var value: String? = null
    fun get(): String {
        return if (value != null) value!! else ""
    }

    fun set(value: String?) {
        if (equals(this.value, value) == false) {
            this.value = value
            notifyChange()
        }
    }

    companion object {
        fun equals(a: Any?, b: Any?): Boolean {
            return if (a == null) b == null else a == b
        }
    }
}