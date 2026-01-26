package com.gaurav.smartcook.data.repository

//import androidx.room.compiler.processing.util.Resource
import com.google.firebase.auth.FirebaseUser


sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T> : Resource<T>()
}
interface AuthRepository {

    val currentUser: FirebaseUser?
    suspend fun login(email: String, password: String): Resource<FirebaseUser>
    suspend fun signup(name: String, email: String, password: String): Resource<FirebaseUser>
    fun logout()
}

class NetworkAuthRepository: AuthRepository {

    override val currentUser: FirebaseUser?
        get() = TODO("Not yet implemented")

    override suspend fun login(email: String, password: String): Resource<FirebaseUser> {
        return try{
          //  Resource.Loading()
            TODO("Not yet implemented")
        }catch (e:Exception){
            Resource.Error(e.message.toString())
        }

    }
    override suspend fun signup(name: String, email: String, password: String): Resource<FirebaseUser>
    {
        TODO("Not yet implemented")
    }
    override fun logout(){
        TODO("Not yet implemented")
    }

}



