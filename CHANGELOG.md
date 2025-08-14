# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## Unreleased

## 7.0.2 - 2025.08.13

### Changed

- all packages are now starting with `io` instead of `com`
- module name is now `football.game.engine` instead of `game.engine`

## 6.0.0 - 2023.10.19

### Removed

- remove slf4j dependency. It is still possible to have access to library logs just by using SPI [Logger] interface

[Logger]: src/main/java/com/github/lipinskipawel/board/spi/Logger.java

### Added

- allLegalMovesFuture method in Board interface with promise of more flexible API through the new type LegalMovesFuture
  which exposes more convenient ways for clients to deal with expensive computation

### Fixed

- returning empty move from [MoveStrategy] is much less likely to happen. It is possible only when given too little time
- stop consuming resources after cancellation of searching for best move

[MoveStrategy]: src/main/java/com/github/lipinskipawel/board/ai/MoveStrategy.java

### Deprecated

- Deprecated allLegalMoves from Board interface in favour of allLegalMovesFuture

## 5.0.0 - 2021.12.17

### Added

- Add possibility to define custom Player types. Board interface will now use it in its API's
- Extends the MoveStrategy and BoardEvaluator API's to accept Board parametrized by the wildcard
- Add PlayerProvider<T> class to this API which will hold new provided types to Board<T>

### Changed

- Library supports now JDK 11
- takeTheWinner() method will now return T instead of Player enum
- Board now is a parametrized class
- Board executeMove will not throw RuntimeException when move can not be made
- Equals, hashCode and toString methods are implemented in all API classes

## 4.0.0 - 2021.03.01

### Removed

- The MiniMax class is no longer a part of the API
- The DummyBoardEvaluator is no longer a part of the API
- The MiniMaxAlphaBeta is no longer a part of the API
- All API methods from MoveStrategy

### Rename

- BoardInterface into Board

### Added

- Default method providing builder for MoveStrategy
- One high cohesive method for searching move in MoveStrategy

## 3.5.0 - 2020.12.12

### Removed

- The direction class is no more coupled with [neuristic] library

[neuristic]: https://github.com/lipinskipawel/neuristic

### Added

- Ability to provide default depth for supported Board Evaluators
- Add third method in MoveStrategy with the timeout argument

## 3.4.0 - 2020-04-29

### Added

- Add non-binary transformation for BoardInterface
- Add alpha-beta version of MiniMax algorithm

### Fixed

- Make SmartBoardEvaluator aware when Player First hit a goal

## 3.3.0

### Added

- Method for BoardInterface which take winner of the game
- Better BoardEvaluator which is SmartBoardEvaluator

### Fixed

- Stop searching for best move when the game is over in MiniMax
- BoardInterface nextPlayerToMove
- Returning null in MiniMax#execute

## 3.2.0

### Added

- Add BoardEvaluator

## 3.1.0

### Added

- Mark Move class with Serializable

### Fixed

- BoardInterface undo method

## 3.0.1

### Fixed

- getPlayer method to return proper Player

## 3.0.0

### Removed

- Rename package to com.github.lipinskipawel
- Rename BoardInterface2 to BoardInterface
- Rename Point2 to Point

### Added

- Add possibility for retrieving all moves made by players

## 2.0.0

### Removed

- Remove deprecated code from 1.1.1 version

### Added

- Add module-info for declaration of the API

## 1.1.1

### Deprecated

- Deprecate legacy engine

## 1.1.0

### Added

- Add Matrix operations

## 1.0.0

### Added

- Add license, README
- Import engine from the [LAN-Game] project

[lan-game]: https://github.com/lipinskipawel/LAN-game
