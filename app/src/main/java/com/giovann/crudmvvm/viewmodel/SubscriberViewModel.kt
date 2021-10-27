package com.giovann.crudmvvm.viewmodel

import android.util.Patterns
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.giovann.crudmvvm.db.Subscriber
import com.giovann.crudmvvm.eventmanager.Event
import com.giovann.crudmvvm.repository.SubscriberRepository
import kotlinx.coroutines.launch

class SubscriberViewModel(private val repository: SubscriberRepository) : ViewModel(), Observable {

    val subscribers = repository.subscribers
    private var isUpdateOrDelete = false
    private lateinit var subscriberToUpdateOrDelete: Subscriber

    private val statusMessage = MutableLiveData<Event<String>>()

    val message : LiveData<Event<String>>
        get() = statusMessage

    @Bindable
    val inputName = MutableLiveData<String>()

    @Bindable
    val inputEmail = MutableLiveData<String>()

    @Bindable
    val saveOrUpdateButtonText = MutableLiveData<String>()

    @Bindable
    val clearAllOrDeleteButtonText = MutableLiveData<String>()

    init {
        saveOrUpdateButtonText.value = "Save"
        clearAllOrDeleteButtonText.value = "Clear"
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    fun saveOrUpdate() {

        if (inputName.value == null) {
            statusMessage.value = Event("Name can't be empty")
        }
        else if (inputEmail.value == null) {
            statusMessage.value = Event("Email can't be empty")
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(inputEmail.value!!).matches()) {
            statusMessage.value = Event("Please enter a valid email address")
        }
        else {
            if (isUpdateOrDelete) {
                subscriberToUpdateOrDelete.name = inputName.value!!
                subscriberToUpdateOrDelete.email = inputEmail.value!!
                update(subscriberToUpdateOrDelete)
            }
            else {
                val name = inputName.value!!
                val email = inputEmail.value!!
                insert(Subscriber(0, name, email))
                inputName.value = null
                inputEmail.value = null
            }
        }
    }

    fun clearAllOrDelete() {
        if (isUpdateOrDelete) {
            delete(subscriberToUpdateOrDelete)
        }
        else {
            clearAll()
        }
    }

    fun insert(subscriber: Subscriber) {
        viewModelScope.launch {
            val newRowId = repository.insert(subscriber)
            if (newRowId > -1) {
                statusMessage.value = Event("Subscriber inserted successfully $newRowId")
            }
            else {
                statusMessage.value = Event("Error occured")
            }
        }
    }

    fun update(subscriber: Subscriber) {
        viewModelScope.launch {
            val numOfRows = repository.update(subscriber)
            if (numOfRows > 0) {
                inputName.value = null
                inputEmail.value = null
                isUpdateOrDelete = false
                saveOrUpdateButtonText.value = "Save"
                clearAllOrDeleteButtonText.value = "Clear All"
                statusMessage.value = Event("$numOfRows Subscriber updated successfully")
            }
            else {
                statusMessage.value = Event("Error occured")
            }
        }
    }

    fun delete(subscriber: Subscriber) {
        viewModelScope.launch {
            val numDeletedRows = repository.delete(subscriber)
            if (numDeletedRows > 0) {
                inputName.value = null
                inputEmail.value = null
                isUpdateOrDelete = false
                saveOrUpdateButtonText.value = "Save"
                clearAllOrDeleteButtonText.value = "Clear All"
                statusMessage.value = Event("$numDeletedRows Subscriber deleted successfully")
            }
            else {
                statusMessage.value = Event("Error when deleting certain subscriber")
            }

        }
    }

    fun clearAll() {
        viewModelScope.launch {
            val numOfRowsDeleted = repository.deleteAll()
            if (numOfRowsDeleted > 0) {
                statusMessage.value = Event("All $numOfRowsDeleted subscribers deleted successfully")
            }
            else {
                statusMessage.value = Event("Error clearAll")
            }

        }
    }

    fun initUpdateAndDelete(subscriber: Subscriber) {
        inputName.value = subscriber.name
        inputEmail.value = subscriber.email
        isUpdateOrDelete = true
        subscriberToUpdateOrDelete = subscriber
        saveOrUpdateButtonText.value = "Update"
        clearAllOrDeleteButtonText.value = "Delete"
    }

}