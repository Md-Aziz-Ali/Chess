package com.example.chess

class User {
    var name: String? = null
    var email: String? = null
    var uid: String?= null
    var profileImageUrl: String? = null

    constructor(){}

    constructor(name: String?, email: String?, uid: String?, profileImageUrl: String?, tokenKey: String?) {
        this.name = name
        this.email = email
        this.uid = uid
        this.profileImageUrl = profileImageUrl
    }
}