This is a Java chess engine made purely for fun and to get some practice with algorithms/optimizing data structures.

Move search/pruning algorithms used:
\n-Principal Variation Search
\n-Null Move Search Extensions
\n-Alpha Beta Pruning
\n-Killer Move Heuristic
\n-MVV/LVA Move ordering
\n-Iterative deepening
\n-Quiescence search

Evaluation considerations:
-Material
-Positional (number of moves compared to opponent)
-Bishop pair bonuses
-Castled king bonuses
-Central pawns pushed bonuses
-Early queen movement penalty

TODO:
-Transposition tables
-Bitboard representation
-Connected rooks bonuses
-Pawn structure evaluations
