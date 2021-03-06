package com.kaizendeveloper.bitcoinsandbox.db.repository

import com.kaizendeveloper.bitcoinsandbox.db.SandboxDatabase
import com.kaizendeveloper.bitcoinsandbox.db.dao.UserDao
import com.kaizendeveloper.bitcoinsandbox.db.entity.User
import com.kaizendeveloper.bitcoinsandbox.model.AddressGenerator
import com.kaizendeveloper.bitcoinsandbox.util.Cipher
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.security.interfaces.ECPublicKey
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsersRepository @Inject constructor(
    private val db: SandboxDatabase,
    private val userDao: UserDao
) {

    val users = userDao.getAll()
    val currentUser = userDao.getCurrent()

    fun updateCurrent(old: User, new: User): Completable {
        return Completable.fromAction {
            db.runInTransaction {
                userDao.update(old.apply { isCurrent = false })
                userDao.update(new.apply { isCurrent = true })
            }
        }.subscribeOn(Schedulers.io())
    }

    fun createIfAbsent(name: String, isCurrent: Boolean = false): Single<User> {
        return userDao.getByName(name)
            .switchIfEmpty(
                Single.fromCallable {
                    val keyPair = Cipher.generateECKeyPair(name)
                    val address = AddressGenerator.generate(keyPair.public as ECPublicKey)
                    val user = User(name, address, isCurrent)

                    userDao.insert(user)
                    user
                }
            ).subscribeOn(Schedulers.computation())
    }
}
