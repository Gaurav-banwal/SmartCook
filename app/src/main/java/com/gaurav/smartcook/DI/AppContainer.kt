package com.gaurav.smartcook.DI

import com.gaurav.smartcook.data.repository.AuthRepository
import com.gaurav.smartcook.data.repository.NetworkAuthRepository

interface AppContainer {

     val authRepository: AuthRepository
}

class DefaultAppContainer : AppContainer {



    override val authRepository: AuthRepository by lazy {
        NetworkAuthRepository()
    }


}