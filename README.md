This is a Java chess engine made purely for fun back in college and to get some practice with algorithms/optimizing data structures.  At it's current strength it can consistently beat the Windows factory-installed engine Chess Titans on max difficulty while searching only to depth 4.  DISCLAIMER: I wrote this before I had any experience with enterprise software design.  It was an academic exercise and turned out to be a clever chess player, but I would definitely code this much cleaner if I were to redo it.  

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

Lessons learned from this project:
* I assumed that rebuilding the game tree for each search would be too wasteful and that storing it was a better approach.  This is incorrect because after searching 6 ply the game tree is too large to fit in most modern computer's RAM, without your JVM crashing.  When/if I rewrite this it would be best to use a transposition table to allow me to search deeper and greatly increase the engine's strength without storing the entire game tree.
* Data structures/design: I wrote this program before I knew anything about software design patterns/best practices/unit testing.  As such I would do better in the future to plan out data structures better (especially using bitboards), and make better uses of interfaces and general OOP design.  
* A big goal for this project now is to abandon the GUI I coded and adopt UCI protocol so this engine can play against other engines over a standard interface.  That would make testing and tuning much easier.  Having a test suite would also make it easier to refactor the fundamental parts of the algorithm and know nothing was broken.
