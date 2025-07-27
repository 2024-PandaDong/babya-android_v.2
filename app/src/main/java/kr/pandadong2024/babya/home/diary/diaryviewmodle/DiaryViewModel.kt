package kr.pandadong2024.babya.home.diary.diaryviewmodle

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.remote.request.SubCommentRequest
import kr.pandadong2024.babya.server.remote.responses.CommentResponses
import kr.pandadong2024.babya.server.remote.responses.SubCommentResponses
import kr.pandadong2024.babya.server.remote.responses.diary.DiaryDataResponses
import retrofit2.HttpException

class DiaryViewModel(application: Application) : AndroidViewModel(application) {

    private val _diaryId = MutableLiveData<Int>().apply { value = -1 }
    val diaryId: LiveData<Int> = _diaryId

    var isPublic = MutableLiveData<Boolean>().apply { value = true }  // true : 공개 | 비공개 : false

    val isOpenSearchView = MutableLiveData<Boolean>().apply { value = false }

    val diarySearchKeyWord = MutableLiveData<String>().apply { value = "" }

    private val _isDiaryScrolled = MutableLiveData<Boolean>(true)
    val isDiaryScrolled: LiveData<Boolean> = _isDiaryScrolled

    val editDiaryData = MutableLiveData<DiaryDataResponses?>().apply { value = null }

    val pagingSize = 8

    private var _accessToken = MutableLiveData<String>().apply { value = "" }
    var accessToken: LiveData<String> = _accessToken

    private val _toastMessage = MutableLiveData<String>().apply { value = "" }
    val toastMessage: LiveData<String> = _toastMessage

    private val _startSubCommentPage = MutableLiveData<Int>(0)
    val startSubCommentPage: LiveData<Int> = _startSubCommentPage

    private val _startCommentPage = MutableLiveData<Int>(0)
    val startCommentPage: LiveData<Int> = _startCommentPage

    private val _startPublicDiaryPage = MutableLiveData<Int>(1)
    val startPublicDiaryPage: LiveData<Int> = _startPublicDiaryPage

    private val _startPrivateDiaryPage = MutableLiveData<Int>(1)
    val startPrivateDiaryPage: LiveData<Int> = _startPrivateDiaryPage

    private val _subCommentList = MutableLiveData<List<SubCommentResponses>>(emptyList())
    val subCommentList: LiveData<List<SubCommentResponses>> = _subCommentList

    private val _commentList = MutableLiveData<List<CommentResponses>>(emptyList())
    val commentList: LiveData<List<CommentResponses>> = _commentList

    private var _publicDiaryList = MutableLiveData<List<DiaryDataResponses>>(emptyList())
    var publicDiaryList: LiveData<List<DiaryDataResponses>> = _publicDiaryList

    private var _privateDiaryList = MutableLiveData<List<DiaryDataResponses>>(emptyList())
    var privateDiaryList: LiveData<List<DiaryDataResponses>> = _privateDiaryList

    private var _diaryList = MutableLiveData<List<DiaryDataResponses>>(emptyList())
    var diaryList: LiveData<List<DiaryDataResponses>> = _diaryList

    fun setToastMessage(message: String) {
        _toastMessage.value = message
    }


    fun setDiarySearchKeyWord(inputKeyWord: String) {
        diarySearchKeyWord.value = inputKeyWord
    }

    fun setAccessToken(token: String) = viewModelScope.launch() {
        _accessToken.value = token
    }

    fun setDiaryId(diaryId: Int) {
        _diaryId.value = diaryId
    }

    fun changeOpenSearchView() {
        isOpenSearchView.value = isOpenSearchView.value?.not() ?: false
    }

    fun initKeyword() {
        diarySearchKeyWord.value = ""
        isOpenSearchView.value = false
    }

    fun initDiaryList() {
        _startPublicDiaryPage.value = 1
        _startPrivateDiaryPage.value = 1
        _publicDiaryList.value = emptyList()
        _privateDiaryList.value = emptyList()
    }

    fun initEditDate() {
        editDiaryData.value = null
    }

    fun addSubCommentPage(commentId: Int) {
        viewModelScope.launch { getSubComment(commentId) }
    }

    fun addCommentPage() {
        viewModelScope.launch { getCommentList() }
    }

    fun addDiaryPage(
        public: Boolean = isPublic.value == true,
        keyword: String = diarySearchKeyWord.value ?: ""
    ) {
        if (_publicDiaryList.value?.isNotEmpty() == true) {
            if (public) {
                _startPublicDiaryPage.value = startPublicDiaryPage.value?.plus(1)
            } else {
                _startPrivateDiaryPage.value = startPrivateDiaryPage.value?.plus(1)
            }
            viewModelScope.launch { getDiaryData(public, keyword) }
        }
    }

    fun changeList(isVisible: Boolean = isPublic.value == true) {
        _diaryList.value = if (isVisible) {
            _publicDiaryList.value
        } else {
            _privateDiaryList.value
        }
    }

    fun getDiaryData(
        public: Boolean = isPublic.value == true,
        keyword: String? = "",
    ) = viewModelScope.launch {
        kotlin.runCatching {
            val diaryPagingSize = if (pagingSize % 2 == 0) {
                pagingSize
            } else {
                pagingSize + 1
            }
            runCatching {
                when (public) {
                    true -> {
                        RetrofitBuilder.getDiaryService().getDiaryList(
                            accessToken = "Bearer ${accessToken.value}",
                            page = startPublicDiaryPage.value ?: 1,
                            size = diaryPagingSize,
                            keyword = keyword ?: ""
                        )
                    }

                    false -> {
                        RetrofitBuilder.getDiaryService().getMyDiaryData(
                            accessToken = "Bearer ${accessToken.value}",
                            page = startPrivateDiaryPage.value ?: 1,
                            size = diaryPagingSize,
                            keyword = keyword ?: ""
                        )
                    }
                }
            }.onSuccess {
                if (public) {
                    val list = _publicDiaryList.value?.toMutableList()
                    list?.addAll(it.data ?: listOf())
                    _publicDiaryList.value = list ?: listOf()
                } else {
                    val list = _privateDiaryList.value?.toMutableList()
                    list?.addAll(it.data ?: listOf())
                    _privateDiaryList.value = list ?: listOf()
                }
                changeList(public)
                if (keyword == null) {
                    _publicDiaryList.value = it.data ?: listOf()
                } else {
                    if (it.data?.isEmpty() == true) {
                        _isDiaryScrolled.value = false
                        if (public && (_startPublicDiaryPage.value != 1 || _startPrivateDiaryPage.value != 1)) {
                            _startPublicDiaryPage.value = startPublicDiaryPage.value?.plus(-1)
                        } else {
                            _startPrivateDiaryPage.value = startPrivateDiaryPage.value?.plus(-1)
                        }
                    } else {
                        _isDiaryScrolled.value = true
                    }
                }
            }.onFailure {
                _isDiaryScrolled.value = false
                _publicDiaryList.value = listOf()
                if (it is HttpException) {
                    _toastMessage.value = when (it.code()) {
                        500 -> "서버 에러"
                        else -> ""
                    }
                }
            }
        }
    }

    fun reportDiary(diaryId: Int, action: () -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        runCatching {
            RetrofitBuilder.getDiaryService().reportDiary(
                accessToken = "Bearer ${_accessToken.value}",
                id = diaryId
            )
        }.onSuccess {
            _toastMessage.value = "정상적으로 신고가 접수되었습니다."
            action()
        }.onFailure { result ->
            if (result is HttpException) {
                if (result.code() == 500) {
                    _toastMessage.value = "서버에서 문제가 발생했습니다."
                } else {
                    _toastMessage.value = "신고를 하는도중 문제가 발생했습니다. CODE : ${result.code()}"
                }
            }
        }
    }

    fun postSubComment(
        parentCommentId: Int,
        comment: String,
        diaryId: Int,
        onAction: () -> Unit
    ) = viewModelScope.launch(Dispatchers.IO) {
        kotlin.runCatching {
            RetrofitBuilder.getDiaryService().postComment(
                accessToken = "Bearer ${accessToken.value}",
                body = SubCommentRequest(
                    comment = comment,
                    diaryId = diaryId,
                    parentCommentId = parentCommentId
                )
            )
        }.onSuccess { _ ->
            withContext(Dispatchers.Main) {
                onAction()
                delay(500)
                getSubComment(parentCommentId)
            }
        }.onFailure { result ->
            result.printStackTrace()
            if (result is HttpException) {
                if (result.code() == 500) {
                    _toastMessage.value = "서버에서 에러가 났습니다."
                }
            }
        }
    }

    fun getCommentList() = viewModelScope.launch() {
        kotlin.runCatching {
            RetrofitBuilder.getDiaryService().getComment(
                accessToken = "Bearer ${accessToken.value}",
                page = (startCommentPage.value ?: 0) + 1,
                size = pagingSize,
                diaryId = _diaryId.value ?: 0
            )
        }.onSuccess { result ->
            val list = _commentList.value?.toMutableList()
            list?.addAll(result.data ?: listOf())

            _commentList.value = list?.toList()
            if (result.data?.isNotEmpty() == true) {
                _startCommentPage.value = _startCommentPage.value?.plus(1)
            }
        }.onFailure { result ->
            if (result is HttpException) {
                if (result.code() == 500) {
                    _toastMessage.value = "서버에서 에러가 났습니다."
                } else {
                    _toastMessage.value = "댓글을 불러오는데 실패했습니다. ${result.code()}"
                }
            }
            result.printStackTrace()
        }
    }

    fun getSubComment(commentId: Int) =
        viewModelScope.launch() {
            kotlin.runCatching {
                RetrofitBuilder.getDiaryService().getSubComment(
                    accessToken = "Bearer ${_accessToken.value}",
                    parentId = commentId,
                    page = (startSubCommentPage.value ?: 0) + 1,
                    size = pagingSize,
                )
            }.onSuccess { result ->
                withContext(Dispatchers.Main)
                {
                    val list = subCommentList.value?.toMutableList()
                    list?.addAll(result.data ?: listOf())
                    _subCommentList.value = list ?: listOf()

                    if (result.data?.isNotEmpty() == true) {
                        _startSubCommentPage.value = startSubCommentPage.value?.plus(1)
                    }
                }
            }.onFailure { result ->
                if (result is HttpException) {
                    if (result.code() == 500) {
                        _toastMessage.value = "서버에서 에러가 났습니다."
                    } else {
                        _toastMessage.value = "댓글을 불러오는데 실패했습니다. ${result.code()}"
                    }
                }
                result.printStackTrace()
            }
        }

    fun initBottomSubComment() {
        _subCommentList.value = emptyList()
        _startSubCommentPage.value = 0
    }

    fun initPublicCommentList() {
        _commentList.value = emptyList()
        _startCommentPage.value = 0
    }

    fun initDiary(isVisible: Boolean = isPublic.value == true) {
        _startPrivateDiaryPage.value = 1
        _startPublicDiaryPage.value = 1
        _publicDiaryList.value = emptyList()
        getDiaryData(public = isVisible, keyword = null)
    }

    fun initPublicDiary() = viewModelScope.launch {
        launch {
            if (_diaryId.value != null) {
                getCommentList()
            } else {
                _toastMessage.value = "정보를 불러오지 못했어요."
            }
        }
        launch {
            getDiaryData()
        }
    }
}
