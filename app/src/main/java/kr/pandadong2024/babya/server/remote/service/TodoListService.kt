package kr.pandadong2024.babya.server.remote.service

import kr.pandadong2024.babya.server.remote.request.todo.TodoRequestBody
import kr.pandadong2024.babya.server.remote.request.todo.CategoryResponses
import kr.pandadong2024.babya.server.remote.responses.BaseResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface TodoListService {
    @GET("/todo/category")
    suspend fun getCategory(
        @Header("Authorization") accessToken: String,
    ): BaseResponse<CategoryResponses>

    @POST("/todo")
    suspend fun createTodo(
        @Header("Authorization") accessToken: String,
        @Body requestBody: TodoRequestBody,
    ): BaseResponse<String>
}