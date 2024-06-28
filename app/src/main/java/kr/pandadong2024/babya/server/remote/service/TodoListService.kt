package kr.pandadong2024.babya.server.remote.service

import kr.pandadong2024.babya.server.remote.request.todo.TodoRequestBody
import kr.pandadong2024.babya.server.remote.request.todo.CategoryResponses
import kr.pandadong2024.babya.server.remote.request.todo.TodoModifyRequest
import kr.pandadong2024.babya.server.remote.responses.BaseResponse
import kr.pandadong2024.babya.server.remote.responses.todo.TodoResponses
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

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

    @GET("/todo")
    suspend fun getTodoList(
        @Header("Authorization") accessToken: String,
        @Query("category") category : String,
        @Query("date") date : String): BaseResponse<List<TodoResponses>>

    @PATCH("/todo")
    suspend fun modifyTodo(
        @Header("Authorization") accessToken: String,
        @Body requestBody : TodoModifyRequest
    ): BaseResponse<String>

    @DELETE("/todo/{id}")
    suspend fun deleteTodo(
        @Header("Authorization") accessToken: String,
        @Path("id") id : Int
    ) : BaseResponse<String>


}