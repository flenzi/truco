# Truco Game

Truco game is an implementation of the famous game with a finite-state machine using 
Spring Statemachine framework https://projects.spring.io/spring-statemachine/. 
The game follows the Argentinian rules https://es.wikipedia.org/wiki/Truco_argentino.

The state machine represents a single round, so there are various thing that need to be handled outside of its scope(e.g. the global score, who is hand, etc).


TODO
* Implement envido states/transitions
* Implement flor states/transitions