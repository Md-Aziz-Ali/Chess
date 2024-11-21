package com.example.chess

class MoveData {
    var count: Int = 0
    var piece: String = ""
    var startPosition: Pair<Int, Int> = Pair(0, 0)
    var endPosition: Pair<Int, Int> = Pair(0, 0)
    var isCastle: Boolean = false
    var rookStartPosition: Pair<Int, Int> = Pair(0, 0)
    var rookEndPosition: Pair<Int, Int> = Pair(0, 0)
    var isEnPassant: Boolean = false
    var pawnPosition: Pair<Int, Int> = Pair(0, 0)
    var whiteLeftRookMoved: Boolean = false
    var whiteRightRookMoved: Boolean = false
    var whiteKingMoved: Boolean = false
    var blackLeftRookMoved: Boolean = false
    var blackRightRookMoved: Boolean = false
    var blackKingMoved: Boolean = false
    var tookOtherPiece: String = ""
    var tookPosition: Pair<Int, Int> = Pair(0, 0)
    var isUpgraded: String = ""

    constructor() {

    }

    constructor(
        count: Int,
        piece: String,
        startPosition: Pair<Int, Int>,
        endPosition: Pair<Int, Int>,
        isCastle: Boolean,
        rookStartPosition: Pair<Int, Int>,
        rookEndPosition: Pair<Int, Int>,
        isEnPassant: Boolean,
        pawnPosition: Pair<Int, Int>,
        whiteLeftRookMoved: Boolean,
        whiteRightRookMoved: Boolean,
        whiteKingMoved: Boolean,
        blackLeftRookMoved: Boolean,
        blackRightRookMoved: Boolean,
        blackKingMoved: Boolean,
        tookOtherPiece: String,
        tookPosition: Pair<Int, Int>,
        isUpgraded: String
    ) {
        this.count = count
        this.piece = piece
        this.startPosition = startPosition
        this.endPosition = endPosition
        this.isCastle = isCastle
        this.rookStartPosition = rookStartPosition
        this.rookEndPosition = rookEndPosition
        this.isEnPassant = isEnPassant
        this.pawnPosition = pawnPosition
        this.whiteLeftRookMoved = whiteLeftRookMoved
        this.whiteRightRookMoved = whiteRightRookMoved
        this.whiteKingMoved = whiteKingMoved
        this.blackLeftRookMoved = blackLeftRookMoved
        this.blackRightRookMoved = blackRightRookMoved
        this.blackKingMoved = blackKingMoved
        this.tookOtherPiece = tookOtherPiece
        this.tookPosition = tookPosition
        this.isUpgraded = isUpgraded
    }
}
