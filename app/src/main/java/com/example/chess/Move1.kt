package com.example.chess

class Move1 {
    var count: Int = 0
    var isWhiteTurn: Boolean = true
    var upgradedTo: String = ""
//    var piece: String = ""
    var startPosition: Position = Position()
    var endPosition: Position = Position()
//    var isCastle: Boolean = false      // If true, it's a castling move, else it's a regular move
//    var rookStartPosition: Position = Position()
//    var rookEndPosition: Position = Position()
//    var isEnPassant: Boolean = false
//    var pawnPosition: Position = Position()
//
//    var whiteLeftRookMoved: Boolean = false
//    var whiteRightRookMoved: Boolean = false
//    var whiteKingMoved: Boolean = false
//
//    var blackLeftRookMoved: Boolean = false
//    var blackRightRookMoved: Boolean = false
//    var blackKingMoved: Boolean = false
//
//    var tookOtherPiece: String = ""
//    var tookPosition: Position = Position()
//    var isUpgraded: String = ""

    constructor() {}

    constructor(
        isWhiteTurn: Boolean,
        count: Int,
//        piece: String,
//        isCastle: Boolean,
        startPosition: Position,
        upgradedTo: String,
        endPosition: Position,
//        rookStartPosition: Position,
//        rookEndPosition: Position,
//        isEnPassant: Boolean,
//        whiteLeftRookMoved: Boolean,
//        whiteRightRookMoved: Boolean,
//        whiteKingMoved: Boolean,
//
//        blackLeftRookMoved: Boolean,
//        blackRightRookMoved: Boolean,
//        blackKingMoved: Boolean,
//        pawnPosition: Position,
//        tookOtherPiece: String,
//        tookPosition: Position,
//        isUpgraded: String
    ) {
        this.count = count
        this.isWhiteTurn = isWhiteTurn
        this.upgradedTo = upgradedTo
//        this.piece = piece
        this.startPosition = startPosition
        this.endPosition = endPosition
//        this.rookStartPosition = rookStartPosition
//        this.rookEndPosition = rookEndPosition
//        this.pawnPosition = pawnPosition
//        this.tookOtherPiece = tookOtherPiece
//        this.tookPosition = tookPosition
//        this.isUpgraded = isUpgraded
//        this.isCastle = isCastle
//        this.isEnPassant = isEnPassant
//        this.whiteKingMoved = whiteKingMoved
//        this.whiteLeftRookMoved = whiteLeftRookMoved
//        this.whiteRightRookMoved = whiteRightRookMoved
//        this.blackKingMoved = blackKingMoved
//        this.blackRightRookMoved = blackRightRookMoved
//        this.blackLeftRookMoved = blackLeftRookMoved
    }
}
