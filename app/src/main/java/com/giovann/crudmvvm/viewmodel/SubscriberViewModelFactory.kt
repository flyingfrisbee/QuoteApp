package com.giovann.crudmvvm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.giovann.crudmvvm.repository.SubscriberRepository

class SubscriberViewModelFactory(private val repository: SubscriberRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SubscriberViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return SubscriberViewModel(repository) as T
        }
        else {
            throw IllegalArgumentException("Unknown View Model Class")
        }
    }
}