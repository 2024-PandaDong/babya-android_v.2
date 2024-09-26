package kr.pandadong2024.babya.util

class Pattern {
    companion object{
        //영문 대소문자, 특수문자, 숫자를 조합하여 6~20자로 입력해주세요
        val passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@$!%*?&]{6,20}\$".toRegex()
        val email = "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})\$".toRegex()
    }
}