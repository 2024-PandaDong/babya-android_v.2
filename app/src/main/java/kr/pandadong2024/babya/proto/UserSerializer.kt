package kr.pandadong2024.babya.proto

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.example.babya_android.datastore.User

import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object UserSerializer : Serializer<User> {
    override val defaultValue: User
        get() = User.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): User {
        try {
            return User.parseFrom(input)
        } catch (e: InvalidProtocolBufferException){
            throw CorruptionException("Cannot read proto.", e)
        }
    }

    override suspend fun writeTo(t: User, output: OutputStream) {
        t.writeTo(output)
    }
}