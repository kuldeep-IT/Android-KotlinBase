package com.peerbits.base.ui.base

import android.content.Context
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.AndroidViewModel
import com.peerbits.base.AppClass
import com.peerbits.base.utils.pref.SessionManager
import io.reactivex.disposables.CompositeDisposable
import java.io.Serializable

open class BaseViewModel<N : BaseNavigator>(val appClassContext: AppClass) :
    AndroidViewModel(appClassContext), Observable, Serializable {

    var navigator: N? = null

    val isLoading = ObservableBoolean(false)
    val isNoData = ObservableBoolean(false)
    var session = SessionManager(appClassContext)
    lateinit var appContext : Context

    protected val compositeDisposable: CompositeDisposable

    @Transient
    private var mCallbacks: PropertyChangeRegistry? = null

    init {
        compositeDisposable = CompositeDisposable()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    fun setLoading(isLoading: Boolean) {
        this.isLoading.set(isLoading)
    }

    fun setNoData(isNodata: Boolean) {
        this.isNoData.set(isNodata)
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        synchronized(this) {
            if (mCallbacks == null) {
                mCallbacks = PropertyChangeRegistry()
            }
        }
        mCallbacks!!.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        synchronized(this) {
            if (mCallbacks == null) {
                return
            }
        }
        mCallbacks!!.remove(callback)
    }

    /**
     * Notifies listeners that all properties of this instance have changed.
     */
    fun notifyChange() {
        synchronized(this) {
            if (mCallbacks == null) {
                return
            }
        }
        mCallbacks!!.notifyCallbacks(this, 0, null)
    }

    /**
     * Notifies listeners that a specific property has changed. The getter for the property
     * that changes should be marked with [Bindable] to generate a field in
     * `BR` to be used as `fieldId`.
     *
     * @param fieldId The generated BR id for the Bindable field.
     */
    fun notifyPropertyChanged(fieldId: Int) {
        synchronized(this) {
            if (mCallbacks == null) {
                return
            }
        }
        mCallbacks!!.notifyCallbacks(this, fieldId, null)
    }
}
