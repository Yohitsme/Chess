This is a Java chess engine made purely for fun and to get some practice with algorithms/optimizing data structures.  At it's current strength it can consistently beat the Windows factory-installed engine Chess Titans on max difficulty while searching only to depth 4.

Move search/pruning algorithms used:
* Principal Variation Search
* Null Move Search Reduction (WIP)
* Alpha Beta Pruning
* Killer Move Heuristic
* MVV/LVA Move ordering
* Iterative deepening
* Quiescence search

Evaluation considerations:
* Material
* Positional (number of moves compared to opponent)
* Bishop pair bonuses
* Castled king bonuses
* Central pawns pushed bonuses
* Early queen movement penalty
* Connected rooks bonuses
* King safety (half open, fully open files next to king, pawn shields)

TODO:
* Transposition tables
* Bitboard representation
* Pawn structure evaluations
