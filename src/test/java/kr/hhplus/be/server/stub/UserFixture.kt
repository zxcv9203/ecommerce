package kr.hhplus.be.server.stub

import kr.hhplus.be.server.domain.user.User

object UserFixture {
    fun create(
        name: String = "유저 A",
        balance: Long = 0,
        version: Int = 0,
        id: Long = 1L,
    ): User = User(name = name, balance = balance, version = version, id = id)
}
