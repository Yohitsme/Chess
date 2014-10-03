This is a Java chess engine made purely for fun and to get some practice with algorithms/optimizing data structures.

Move search/pruning algorithms used:
<li>Principal Variation Search
<li>Null Move Search Extensions
<li>Alpha Beta Pruning
<li>Killer Move Heuristic
<li>MVV/LVA Move ordering
<li>Iterative deepening
<li>Quiescence search

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
