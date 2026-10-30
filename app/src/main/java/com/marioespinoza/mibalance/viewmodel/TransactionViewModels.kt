package com.marioespinoza.mibalance.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.marioespinoza.mibalance.BalanceApplication
import com.marioespinoza.mibalance.domain.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class RepositoryViewModel(app: Application) : AndroidViewModel(app) {
    protected val repository = (app as BalanceApplication).repository
}
class DashboardViewModel(app: Application) : RepositoryViewModel(app) {
    val state = MutableLiveData<Result<List<Transaction>>>()
    private var job: Job? = null
    fun load(filter: TransactionFilter) {
        job?.cancel()
        job = viewModelScope.launch { state.value = withContext(Dispatchers.IO) { runCatching { repository.find(filter) } } }
    }
}
class HistoryViewModel(app: Application) : RepositoryViewModel(app) {
    val state = MutableLiveData<Result<List<Transaction>>>()
    private var job: Job? = null
    fun load(filter: TransactionFilter) {
        job?.cancel()
        job = viewModelScope.launch { state.value = withContext(Dispatchers.IO) { runCatching { repository.find(filter) } } }
    }
}
class TransactionEditViewModel(app: Application) : RepositoryViewModel(app) {
    val loaded = MutableLiveData<Result<Transaction?>>()
    val saved = MutableLiveData<Result<Unit>>()
    var saving = false; private set
    fun load(id: String) = viewModelScope.launch { loaded.value = withContext(Dispatchers.IO) { runCatching { repository.findById(id) } } }
    fun save(item: Transaction) {
        if (saving) return
        saving = true
        viewModelScope.launch {
            saved.value = withContext(Dispatchers.IO) { runCatching { repository.save(item) } }
            saving = false
        }
    }
}
class DetailViewModel(app: Application) : RepositoryViewModel(app) {
    val state = MutableLiveData<Result<Transaction?>>()
    val deleted = MutableLiveData<Result<Unit>>()
    var deleting = false; private set
    fun load(id: String) = viewModelScope.launch { state.value = withContext(Dispatchers.IO) { runCatching { repository.findById(id) } } }
    fun delete(id: String) {
        if (deleting) return
        deleting = true
        viewModelScope.launch {
            deleted.value = withContext(Dispatchers.IO) { runCatching { repository.delete(id) } }
            deleting = false
        }
    }
}
class LocationViewModel(app: Application) : AndroidViewModel(app) {
    var latitude: Double? = null
    var longitude: Double? = null
}
